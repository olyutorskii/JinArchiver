/*
 * option info
 *
 * License : The MIT License
 * Copyright(c) 2016 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import jp.sourceforge.jindolf.corelib.LandDef;

/**
 * オプション情報。
 *
 * <p>ヘルプ指定がある場合、他の情報は無効。
 * ヘルプ指定が無くエラー情報がある場合、他の情報は無効。
 */
public final class OptInfo{

    private static final String FMT_INVOPT =
            "不正なオプションです。 {0}";
    private static final String FMT_NOARG =
            "オプション {0} に引数がありません。";
    private static final String FMT_INVVID =
            "不正な村番号です。 {0}";
    private static final String FMT_INVLID =
            "不正な国識別子です。 {0}";
    private static final String MSG_NOLID =
            "-land オプションで国識別子を指定してください。";
    private static final String MSG_NOVID =
            "-vid オプションで村番号を指定してください。";
    private static final String MSG_EXCOUT =
            "-outdir か -stdout のどちらか一方を指定してください。";


    private boolean isHelp = false;

    private LandDef landDef = null;
    private int vid = -1;

    private boolean isStdout = false;
    private String outDir = null;

    private String errMsg = null;


    /**
     * コンストラクタ。
     */
    private OptInfo(){
        super();
        return;
    }


    /**
     * オプション情報をパースする。
     * @param args コマンドライン文字列配列
     * @return オプション情報
     */
    public static OptInfo parseOptInfo(String ... args){
        List<String> argList = Arrays.asList(args);
        return parseOptInfo(argList);
    }

    /**
     * オプション情報をパースする。
     * @param argList コマンドライン文字列リスト
     * @return オプション情報
     */
    public static OptInfo parseOptInfo(List<String> argList){
        OptInfo optInfo = new OptInfo();

        if(argList.isEmpty()){
            optInfo.isHelp = true;
            return optInfo;
        }

        Iterator<String> it = argList.iterator();
        while(it.hasNext()){
            String arg = it.next();

            OptArg optArg = OptArg.parseOptArg(arg);
            if(optArg == null){
                optInfo.errMsg = MessageFormat.format(FMT_INVOPT, arg);
                break;
            }

            boolean isNoArgOpt;
            isNoArgOpt = optInfo.applyDepOpt(optArg);
            if(optInfo.isHelp()) break;
            if(isNoArgOpt) continue;

            if(! it.hasNext()){
                optInfo.errMsg = MessageFormat.format(FMT_NOARG, arg);
                break;
            }

            String val = it.next();
            optInfo.applyOptWithVal(optArg, val);

            if(optInfo.hasError()) break;
        }

        if(optInfo.isHelp() || optInfo.hasError()){
            return optInfo;
        }

        optInfo.condErrCheck();

        return optInfo;
    }


    /**
     * 引数無しオプションのパース。
     * @param opt オプション種別
     * @return 引数無しオプションだったらtrue
     */
    private boolean applyDepOpt(OptArg opt){
        boolean isNoArgOpt = true;

        switch(opt){
        case OPT_HELP:
            this.isHelp = true;
            break;
        case OPT_STDOUT:
            this.isStdout = true;
            this.outDir = null;
            break;
        default:
            isNoArgOpt = false;
            break;
        }

        return isNoArgOpt;
    }

    /**
     * 引数付きオプションのパース。
     * @param opt オプション種別
     * @param val オプション引数
     */
    private void applyOptWithVal(OptArg opt, String val){
        switch(opt){
        case OPT_LAND:
            this.landDef = LandUtils.getLandDef(val);
            if(getLandDef() == null){
                this.errMsg = MessageFormat.format(FMT_INVLID, val);
            }
            break;
        case OPT_VID:
            try{
                this.vid = Integer.parseInt(val);
            }catch(NumberFormatException e){
                this.vid = -1;
            }

            if(getVid() < 0){
                this.errMsg = MessageFormat.format(FMT_INVVID, val);
            }
            break;
        case OPT_OUTDIR:
            this.outDir = val;
            this.isStdout = false;
            break;
        default:
            break;
        }

        return;
    }

    /**
     * 状況に応じて異常系を察知しエラーメッセージを組み立てる。
     */
    private void condErrCheck(){
        String msg = null;

        if(getLandDef() == null) msg = MSG_NOLID;
        else if(getVid() < 0)    msg = MSG_NOVID;
        else if(! isSingleOut()) msg = MSG_EXCOUT;

        if(msg != null){
            this.errMsg = msg;
        }

        return;
    }

    /**
     * 出力先が一つにしぼれているケースをテストする。
     * @return 一つにしぼれていればtrue
     */
    private boolean isSingleOut(){
        boolean exclusiveCase;
        exclusiveCase = (getOutdir() != null) ^ isStdout();
        return exclusiveCase;
    }

    /**
     * ヘルプ出力が指定されたか調べる。
     * @return 指定されていればtrue
     */
    public boolean isHelp(){
        return this.isHelp;
    }

    /**
     * 国情報を得る。
     * @return 国情報。無ければnull
     */
    public LandDef getLandDef(){
        return this.landDef;
    }

    /**
     * 村IDを得る。
     * @return 村ID
     */
    public int getVid(){
        return this.vid;
    }

    /**
     * 標準出力への出力が指定されたか調べる。
     * @return 指定されていればtrue
     */
    public boolean isStdout(){
        return this.isStdout;
    }

    /**
     * 出力ディレクトリを得る。
     * @return 出力ディレクトリ。無ければnull。
     */
    public String getOutdir(){
        return this.outDir;
    }

    /**
     * エラーメッセージを得る。
     * @return メッセージ。無ければnull
     */
    public String getErrMsg(){
        return this.errMsg;
    }

    /**
     * エラーの有無を返す。
     * @return エラー情報があればtrue
     */
    public boolean hasError(){
        if(this.isHelp) return false;
        if(this.errMsg == null) return false;

        return true;
    }

}
