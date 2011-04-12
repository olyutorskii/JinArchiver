/*
 * main entry
 *
 * Copyright(c) 2008 olyutorskii
 * $Id: JinArchiver.java 877 2009-10-25 15:16:13Z olyutorskii $
 */

package jp.sourceforge.jindolf.archiver;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jp.sourceforge.jindolf.corelib.LandDef;
import jp.sourceforge.jindolf.parser.DecodeException;
import jp.sourceforge.jindolf.parser.HtmlParseException;

/**
 * メインエントリ。
 */
public final class JinArchiver{
    /** Generator. */
    public static final String GENERATOR = "JinArchiver 1.401.2";
    private static final List<LandDef> LANDDEF_LIST;

    static{
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder builder = factory.newDocumentBuilder();
            LANDDEF_LIST = LandDef.buildLandDefList(builder);
        }catch(RuntimeException e){
            throw e;
        }catch(Exception e){
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 国IDから国情報を得る。
     * @param landId 国ID
     * @return 国情報
     */
    public static LandDef getLandDef(String landId){
        for(LandDef landDef : LANDDEF_LIST){
            if(landDef.getLandId().equals(landId)) return landDef;
        }
        return null;
    }

    /**
     * ヘルプメッセージ出力。
     */
    private static void helpMessage(){
        System.err.println(
                "\n" + GENERATOR + " 人狼BBS アーカイブ作成ツール\n\n"
                +"-h, -help, -?\n\tヘルプメッセージ\n"
                +"-land 国識別子\n"
                +"-vid 村番号\n"
                +"-outdir 出力ディレクトリ\n"
                +"-stdout\n\t標準出力へ出力\n\n"
                +"※ -outdir と -stdout は排他指定\n"
                );
        StringBuilder landList = new StringBuilder();
        for(LandDef landDef : LANDDEF_LIST){
            landList.append(landDef.getLandId()).append(' ');
        }
        System.err.print("利用可能な国識別子は ");
        System.err.println(landList);
        System.err.println();
        return;
    }

    /**
     * オプション文字列を解析する。
     * @param args オプション文字列
     */
    private static void parseOption(String[] args){
        if(args.length <= 0){
            helpMessage();
            System.exit(0);
            return;
        }

        LandDef landDef = null;
        int vid = -1;
        String outdir = null;
        boolean stdout = false;

        for(int pos = 0; pos < args.length; pos++){
            String arg = args[pos];

            if( ! arg.startsWith("-") ){
                System.err.println("不正なオプションです。 " + arg);
                System.exit(1);
                return;
            }

            if(arg.equals("-h") || arg.equals("-help") || arg.equals("-?")){
                helpMessage();
                System.exit(0);
                return;
            }

            if(arg.equals("-stdout")){
                stdout = true;
                outdir = null;
                continue;
            }

            if(++pos >= args.length){
                System.err.println(
                        "オプション " + arg + " に引数がありません。");
                System.exit(1);
                return;
            }

            String val = args[pos];
            if(arg.equals("-land")){
                landDef = getLandDef(val);
                if(landDef == null){
                    System.err.println("不正な国識別子です。 " + val);
                    System.exit(1);
                    return;
                }
            }else if(arg.equals("-vid")){
                vid = Integer.parseInt(val);
                if(vid < 0){
                    System.err.println("不正な村番号です。 " + vid);
                    System.exit(1);
                    return;
                }
            }else if(arg.equals("-outdir")){
                outdir = val;
                stdout = false;
            }else{
                System.err.println("不正なオプションです。 " + arg);
                System.exit(1);
                return;
            }
        }

        if(landDef == null){
            System.err.println(
                    "-land オプションで国識別子を指定してください。");
            System.exit(1);
            return;
        }

        if(vid < 0){
            System.err.println(
                    "-vid オプションで村番号を指定してください。");
            System.exit(1);
            return;
        }

        if(   (outdir == null && stdout == false)
           || (outdir != null && stdout == true)  ){
            System.err.println(
                    "-outdir か -stdout のどちらか一方を指定してください。");
            System.exit(1);
            return;
        }

        Writer writer;
        if(outdir != null){
            writer = getFileWriter(outdir, landDef, vid);
        }else{
            writer = getStdOutWriter();
        }

        writer = ValidateTask.wrapValidator(writer);

        try{
            dump(writer, landDef, vid);
        }catch(RuntimeException e){
            throw e;
        }catch(Exception e){
            e.printStackTrace(System.err);
            System.err.println("処理を続行できません。");
            System.exit(1);
            return;
        }

        return;
    }

    /**
     * 主処理。人狼サーバからXHTMLを読み込み。XMLで出力。
     * @param writer 出力先
     * @param landDef 国情報
     * @param vid 村番号
     * @throws IOException 入出力エラー
     * @throws DecodeException デコードエラー
     * @throws HtmlParseException パースエラー
     */
    public static void dump(Writer writer, LandDef landDef, int vid)
            throws IOException, DecodeException, HtmlParseException{
        List<PeriodResource> resourceList =
                HttpAccess.loadResourceList(landDef, vid);
        VillageData village = new VillageData(resourceList);

        Builder.fillVillageData(village);
        XmlUtils.dumpVillageData(writer, village);

        return;
    }

    /**
     * 標準出力への出力先を得る。
     * @return 出力先
     */
    public static Writer getStdOutWriter(){
        OutputStream ostream;
        ostream = new BufferedOutputStream(System.out);
        Writer writer;
        try{
            writer = new OutputStreamWriter(ostream, "UTF-8");
            writer = new BufferedWriter(writer, 4 * 1024);
        }catch(IOException e){
            System.err.println(
                    "標準出力に書き込めません。");
            System.exit(1);
            return null;
        }
        return writer;
    }

    /**
     * ローカルファイルへの出力先を得る。
     * @param outdir 出力ディレクトリ
     * @param landDef 国情報
     * @param vid 村番号
     * @return 出力先
     */
    public static Writer getFileWriter(String outdir,
                                         LandDef landDef,
                                         int vid ){
            File outFile = new File(outdir);
            if( ! outFile.exists() ){
                System.err.println(
                        outdir + " が存在しません。");
                System.exit(1);
                return null;
            }
            if( ! outFile.isDirectory() ){
                System.err.println(
                        outdir + " はディレクトリではありません。");
                System.exit(1);
                return null;
            }
            if( ! outFile.canWrite() ){
                System.err.println(
                        outdir + " に書き込めません。");
                System.exit(1);
                return null;
            }
            String fname = MessageFormat.format(
                "jin_{0}_{1,number,#00000}.xml", landDef.getLandId(), vid);
            File xmlFile = new File(outFile, fname);
            boolean created;
            try{
                created = xmlFile.createNewFile();
            }catch(IOException e){
                System.err.println(
                        xmlFile.getName() + " が作成できません。");
                System.exit(1);
                return null;
            }
            if( ! created ){
                System.err.println(
                        fname + " が既に" + outdir + "に存在します。");
                System.exit(1);
                return null;
            }
            /* JRE 1.6 only
            xmlFile.setReadable(true);
            xmlFile.setWritable(true);
            xmlFile.setExecutable(false, false);
            */
            Writer writer;
            try{
                OutputStream ostream;
                ostream = new FileOutputStream(xmlFile);
                ostream = new BufferedOutputStream(ostream, 4 * 1024);
                writer = new OutputStreamWriter(ostream, "UTF-8");
                writer = new BufferedWriter(writer, 4 * 1024);
            }catch(IOException e){
                System.err.println(
                        xmlFile.getName() + " に書き込めません。");
                System.exit(1);
                return null;
            }

            return writer;
    }

    /**
     * スタートアップエントリ。
     * @param args 引数
     */
    public static void main(String[] args){
        parseOption(args);
        System.exit(0);
        return;
    }

    /**
     * 隠しコンストラクタ。
     */
    private JinArchiver(){
        throw new Error();
    }

}
