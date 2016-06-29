/*
 * main entry
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;
import javax.xml.validation.Validator;
import jp.sourceforge.jindolf.corelib.LandDef;
import jp.sourceforge.jindolf.parser.DecodeException;
import jp.sourceforge.jindolf.parser.HtmlParseException;
import org.xml.sax.SAXException;

/**
 * メインエントリ。
 */
public final class JinArchiver{

    /** Generator. */
    public static final String GENERATOR;

    /** このClass。 */
    private static final Class<?> SELF_KLASS;
    /** このPackage。 */
    private static final Package  SELF_PACKAGE;
    /** タイトル。 */
    private static final String TITLE;
    /** バージョン。 */
    private static final String VERSION;

    /** バージョン定義リソース。 */
    private static final String RES_VERDEF = "resources/version.properties";


    static{
        SELF_KLASS   = JinArchiver.class;
        SELF_PACKAGE = SELF_KLASS.getPackage();

        Properties verProp = loadVersionDefinition(SELF_KLASS);
        TITLE   = getPackageInfo(verProp, "pkg-title.",   "Unknown");
        VERSION = getPackageInfo(verProp, "pkg-version.", "0");
        GENERATOR = TITLE + " " + VERSION;
    }


    /**
     * 隠しコンストラクタ。
     */
    private JinArchiver(){
        assert false;
        throw new AssertionError();
    }


    /**
     * リソース上のパッケージ定義プロパティをロードする。
     * MANIFEST.MFが参照できない実行環境での代替品。
     * @param klass パッケージを構成する任意のクラス
     * @return プロパティ
     */
    private static Properties loadVersionDefinition(Class<?> klass){
        Properties result = new Properties();

        InputStream istream = klass.getResourceAsStream(RES_VERDEF);

        try{
            try{
                result.load(istream);
            }finally{
                istream.close();
            }
        }catch(IOException e){
            // NOTHING
        }

        return result;
    }

    /**
     * リソース上のプロパティから
     * このクラスのパッケージのパッケージ情報を取得する。
     * MANIFEST.MFが参照できない実行環境での代替品。
     * @param prop プロパティ
     * @param prefix 接頭辞
     * @param defValue 見つからなかった場合のデフォルト値
     * @return パッケージ情報
     */
    private static String getPackageInfo(Properties prop,
                                          String prefix,
                                          String defValue){
        return getPackageInfo(prop, SELF_PACKAGE, prefix, defValue);
    }

    /**
     * リソース上のプロパティからパッケージ情報を取得する。
     * MANIFEST.MFが参照できない実行環境での代替品。
     * @param prop プロパティ
     * @param pkg 任意のパッケージ
     * @param prefix 接頭辞
     * @param defValue デフォルト値
     * @return 見つからなかった場合のパッケージ情報
     */
    private static String getPackageInfo(Properties prop,
                                          Package pkg,
                                          String prefix,
                                          String defValue){
        String propName = prefix + pkg.getName();
        String result = prop.getProperty(propName, defValue);
        return result;
    }

    /**
     * System.err.println()のWrapper。
     * @param text 出力テキスト
     */
    private static void errprintln(CharSequence text){
        System.err.println(text);
        return;
    }

    /**
     * プログラムの終了。
     * @param code プロセスコード。
     */
    private static void exit(int code){
        System.exit(code);
        assert false;
        return;
    }

    /**
     * ヘルプメッセージ出力。
     */
    private static void helpMessage(){
        String msg = OptArg.getHelpMessage(GENERATOR);
        errprintln(msg);
        return;
    }

    /**
     * オプション文字列を解析する。
     * @param optInfo オプション情報
     */
    private static void dumpOut(OptInfo optInfo){
        String outdir   = optInfo.getOutdir();
        LandDef landDef = optInfo.getLandDef();
        int vid         = optInfo.getVid();

        Validator validator;
        try{
            validator = XmlUtils.createValidator();
        }catch(SAXException e){
            abortWithException(e, "処理を続行できません。");
            return;
        }

        Writer writer;
        if(outdir != null){
            writer = getFileWriter(outdir, landDef, vid);
        }else{
            writer = getStdOutWriter();
        }

        VillageData villageData;
        try{
            villageData = load(landDef, vid);
        }catch(IOException e){
            abortWithException(e);
            return;
        }catch(DecodeException e){
            abortWithException(e);
            return;
        }catch(HtmlParseException e){
            abortWithException(e);
            return;
        }

        SnifWriter snifWriter = new SnifWriter(writer);
        Reader reader = snifWriter.getSnifReader();

        writer = new BufferedWriter(snifWriter);
        reader = new BufferedReader(reader);

        XmlOut xmlOut = new XmlOut(writer);
        Charset cs = landDef.getEncoding();
        xmlOut.setSourceCharset(cs);

        ValidateTask valTask = new ValidateTask(reader, validator);
        DumpXmlTask dumpTask = new DumpXmlTask(villageData, xmlOut);

        ProdCons taskman = new ProdCons(dumpTask, valTask);
        try{
            taskman.submit();
        }catch(InterruptedException e){
            abortWithException(e);
        }

        if(taskman.hasError()){
            Throwable cause = taskman.getCause();
            String desc = taskman.getErrDescription();
            abortWithException(cause, desc);
            assert false;
        }

        return;
    }

    /**
     * 例外によるアプリ終了。
     * @param e 例外
     */
    private static void abortWithException(Throwable e){
        abortWithException(e, "処理を続行できません。");
        exit(1);
        return;
    }

    /**
     * 例外によるアプリ終了。
     * @param e 例外
     * @param desc 詳細テキスト
     */
    private static void abortWithException(Throwable e, String desc){
        e.printStackTrace(System.err);
        errprintln(desc);
        exit(1);
        return;
    }

    /**
     * 主処理。人狼サーバからXHTMLを読み込み。XMLで出力。
     * @param landDef 国情報
     * @param vid 村番号
     * @return 村情報
     * @throws IOException 入出力エラー
     * @throws DecodeException デコードエラー
     * @throws HtmlParseException パースエラー
     */
    public static VillageData load(LandDef landDef, int vid)
            throws IOException, DecodeException, HtmlParseException{
        List<PeriodResource> resourceList =
                HttpAccess.loadResourceList(landDef, vid);
        VillageData villageData = new VillageData(resourceList);

        Builder.fillVillageData(villageData);

        return villageData;
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
            errprintln(
                    "標準出力に書き込めません。");
            exit(1);
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
            errprintln(
                    outdir + " が存在しません。");
            exit(1);
            return null;
        }
        if( ! outFile.isDirectory() ){
            errprintln(
                    outdir + " はディレクトリではありません。");
            exit(1);
            return null;
        }
        if( ! outFile.canWrite() ){
            errprintln(
                    outdir + " に書き込めません。");
            exit(1);
            return null;
        }
        String fname = MessageFormat.format(
            "jin_{0}_{1,number,#00000}.xml", landDef.getLandId(), vid);
        File xmlFile = new File(outFile, fname);
        boolean created;
        try{
            created = xmlFile.createNewFile();
        }catch(IOException e){
            errprintln(
                    xmlFile.getName() + " が作成できません。");
            exit(1);
            return null;
        }
        if( ! created ){
            errprintln(
                    fname + " が既に" + outdir + "に存在します。");
            exit(1);
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
            errprintln(
                    xmlFile.getName() + " に書き込めません。");
            exit(1);
            return null;
        }

        return writer;
    }

    /**
     * スタートアップエントリ。
     * @param args 引数
     */
    public static void main(String[] args){
        OptInfo optInfo = OptInfo.parseOptInfo(args);

        if(optInfo.isHelp()){
            helpMessage();
            exit(0);
            assert false;
            return;
        }

        if(optInfo.hasError()){
            String errMsg = optInfo.getErrMsg();
            errprintln(errMsg);
            exit(1);
            assert false;
            return;
        }

        dumpOut(optInfo);

        exit(0);
        assert false;

        return;
    }

}
