/*
 * option argument
 *
 * License : The MIT License
 * Copyright(c) 2016 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * オプション指定子。
 */
public enum OptArg implements Iterable<String> {

    /** ヘルプ。 */
    OPT_HELP   ("-h", "-help", "-?"),
    /** 国名指定。 */
    OPT_LAND   ("-land"),
    /** 村ID指定。 */
    OPT_VID    ("-vid"),
    /** 指定ディレクトリに書き込む。 */
    OPT_OUTDIR ("-outdir"),
    /** 標準出力へ書き込む。 */
    OPT_STDOUT ("-stdout"),
    ;


    private static final Map<String, OptArg> MAP_OPT;
    private static final String FMT_HELP =
        "\n"
        + "{0} 人狼BBS アーカイブ作成ツール\n\n"
        + "-h, -help, -?\n\tヘルプメッセージ\n"
        + "-land 国識別子\n"
        + "-vid 村番号\n"
        + "-outdir 出力ディレクトリ\n"
        + "-stdout\n\t標準出力へ出力\n\n"
        + "※ -outdir と -stdout は排他指定\n\n"
        + "利用可能な国識別子は {1}\n";


    private final List<String> argList;


    static{
        Map<String, OptArg> map = new HashMap<>();

        for(OptArg optArg : values()){
            for(String argtxt : optArg){
                map.put(argtxt, optArg);
            }
        }

        MAP_OPT = Collections.unmodifiableMap(map);
    }


    /**
     * コンストラクタ。
     * @param opts オプション文字列。
     */
    private OptArg(String ... opts){
        List<String> list;
        list = Arrays.asList(opts);
        list = Collections.unmodifiableList(list);

        this.argList = list;

        return;
    }


    /**
     * 文字列からオプション指定子を得る。
     * @param txt 文字列
     * @return オプション指定子。見つからなければnull
     */
    public static OptArg parseOptArg(String txt){
        OptArg result = MAP_OPT.get(txt);
        return result;
    }

    /**
     * ヘルプメッセージを生成する。
     * @param generator アプリ名
     * @return メッセージ
     */
    public static String getHelpMessage(String generator){
        String appName = "";
        if(generator != null) appName = generator;
        String catalog = LandUtils.getLandIdCatalog();

        String result = MessageFormat.format(FMT_HELP, appName, catalog);

        return result;
    }


    /**
     * オプション文字列のリストを得る。
     * @return オプション文字列のリスト
     */
    public List<String> getArgList(){
        return this.argList;
    }

    /**
     * {@inheritDoc}
     * オプション文字列の列挙。
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<String> iterator(){
        return this.argList.iterator();
    }

}
