/*
 * XML utils
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import jp.sourceforge.jindolf.corelib.LandDef;
import jp.sourceforge.jindolf.parser.DecodeErrorInfo;
import jp.sourceforge.jindolf.parser.DecodedContent;

/**
 * XML用各種ユーティリティ。
 */
public final class XmlUtils{

    private static final String ORIG_DTD =
            "http://jindolf.sourceforge.jp/xml/dtd/bbsArchive-110421.dtd";
    private static final String ORIG_NS =
            "http://jindolf.sourceforge.jp/xml/ns/501";
    private static final String ORIG_SCHEME =
            "http://jindolf.sourceforge.jp/xml/xsd/bbsArchive-110421.xsd";
    private static final String SCHEMA_NS =
            "http://www.w3.org/2001/XMLSchema-instance";

    private static final String OUTPATH = "D:\\TEMP\\zxzx\\";

    private static final char BS_CHAR = (char) 0x005c; // Backslash
    private static final String INDENT_UNIT = "\u0020\u0020";

    private static final TimeZone TZ_TOKYO =
            TimeZone.getTimeZone("Asia/Tokyo");


    /**
     * 隠れコンストラクタ。
     */
    private XmlUtils(){
        throw new Error();
    }


    /**
     * DOCTYPE宣言を出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public static void dumpDocType(Writer writer) throws IOException{
        writer.append("<!DOCTYPE village SYSTEM ");
        writer.append('"');
        writer.append(ORIG_DTD);
        writer.append('"');
        writer.append(" >");
        return;
    }

    /**
     * オリジナルNameSpace宣言を出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public static void dumpNameSpaceDecl(Writer writer)
            throws IOException{
        attrOut(writer, "xmlns", ORIG_NS);
        return;
    }

    /**
     * スキーマNameSpace宣言を出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public static void dumpSiNameSpaceDecl(Writer writer)
            throws IOException{
        attrOut(writer, "xmlns:xsi", SCHEMA_NS);
        return;
    }

    /**
     * スキーマ位置指定を出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public static void dumpSchemeLocation(Writer writer)
            throws IOException{
        attrOut(writer,
                "xsi:schemaLocation",
                ORIG_NS + " " + ORIG_SCHEME);
        return;
    }

    /**
     * インデント用空白を出力する。
     * ネスト単位は空白2文字
     * @param writer 出力先
     * @param level ネストレベル
     * @throws IOException 出力エラー
     */
    public static void indent(Writer writer, int level) throws IOException{
        for(int ct = 1; ct <= level; ct++){
            writer.append(INDENT_UNIT);
        }
        return;
    }

    /**
     * XML数値文字参照を出力する。
     * @param writer 出力先
     * @param chVal 出力文字
     * @throws IOException 出力エラー
     */
    public static void charRefOut(Writer writer, char chVal)
            throws IOException{
        if(chVal == '\u0020'){
            writer.append("&#x20;");
            return;
        }

        if(chVal == '\u0009'){
            writer.append("&#x09;");
            return;
        }

        int ival = 0xffff & ((int) chVal);
        String hex = Integer.toHexString(ival);
        if(hex.length() % 2 != 0) hex = "0" + hex;

        writer.append("&#x");
        writer.append(hex);
        writer.append(";");

        return;
    }

    /**
     * 不正文字をXML出力する。
     * @param writer 出力先
     * @param chVal 不正文字
     * @throws IOException 出力エラー
     */
    public static void dumpInvalidChar(Writer writer, char chVal)
            throws IOException{
        int hexVal;
        hexVal = chVal & 0xff;
        String hexBin = Integer.toHexString(hexVal);
        if(hexBin.length() % 2 != 0) hexBin = "0" + hexBin;

        char replaceChar = '\ufffd';
        if('\u0000' <= chVal && chVal <= '\u001f'){
            replaceChar = (char)( chVal + '\u2400' );
        }

        writer.append("<rawdata");

        writer.append(' ');
        attrOut(writer, "encoding", "Shift_JIS");

        writer.append(' ');
        attrOut(writer, "hexBin", hexBin);

        writer.append(" >");
        writer.append(replaceChar);
        writer.append("</rawdata>");
    }

    /**
     * 任意の文字がXML規格上のホワイトスペースに属するか判定する。
     * @param chVal 文字
     * @return ホワイトスペースならtrue
     */
    public static boolean isWhiteSpace(char chVal){
        switch(chVal){
        case '\u0020':
        case '\t':
        case '\n':
        case '\r':
            return true;
        default:
            break;
        }

        return false;
    }

    /**
     * 文字列を出力する。
     * <ul>
     * <li>先頭および末尾のホワイトスペースは強制的に文字参照化される。
     * <li>連続したホワイトスペースの2文字目以降は文字参照化される。
     * <li>スペースでないホワイトスペースは無条件に文字参照化される。
     * <li>{@literal &, <, >, "}は無条件に文字参照化される。
     * </ul>
     * 参考：XML 1.0 規格 3.3.3節
     * @param writer 出力先
     * @param seq CDATA文字列
     * @throws IOException 出力エラー
     */
    public static void textOut(Writer writer, CharSequence seq)
            throws IOException{
        int len = seq.length();

        boolean leadSpace = false;

        for(int pos = 0; pos < len; pos++){
            char chVal = seq.charAt(pos);

            if(isWhiteSpace(chVal)){
                if(pos == 0 || pos >= len - 1 || leadSpace){
                    charRefOut(writer, chVal);
                }else if(chVal != '\u0020'){
                    charRefOut(writer, chVal);
                }else{
                    writer.append(chVal);
                }
                leadSpace = true;
            }else{
                if(chVal == '&'){
                    writer.append("&amp;");
                }else if(chVal == '<'){
                    writer.append("&lt;");
                }else if(chVal == '>'){
                    writer.append("&gt;");
                }else if(chVal == '"'){
                    writer.append("&quot;");
                }else if(chVal == '\''){
                    writer.append("&apos;");
                }else if(chVal == BS_CHAR){
                    writer.append('\u00a5');
                }else if(chVal == '\u007e'){
                    writer.append('\u203e');
                }else if(Character.isISOControl(chVal)){
                    dumpInvalidChar(writer, chVal);
                }else{
                    writer.append(chVal);
                }
                leadSpace = false;
            }
        }

        return;
    }

    /**
     * 属性を出力する。
     * @param writer 出力先
     * @param name 属性名
     * @param value 属性値
     * @throws IOException 出力エラー
     */
    public static void attrOut(Writer writer,
                                CharSequence name,
                                CharSequence value)
            throws IOException{
        StringBuilder newValue = new StringBuilder(value);
        for(int pt = 0; pt < newValue.length(); pt++){
            char chVal = newValue.charAt(pt);
            if(chVal == '\n' || chVal == '\r' || chVal == '\t') continue;
            if(Character.isISOControl(chVal)){
                newValue.setCharAt(pt, (char)('\u2400' + chVal));
            }
        }

        writer.append(name);
        writer.append('=');
        writer.append('"');
        textOut(writer, newValue);
        writer.append('"');
        return;
    }

    /**
     * xsd:time形式の時刻属性を出力する。
     * タイムゾーンは「+09:00」固定
     * @param writer 出力先
     * @param name 属性名
     * @param hour 時間
     * @param minute 分
     * @throws IOException 出力エラー
     */
    public static void timeAttrOut(Writer writer,
                                     CharSequence name,
                                     int hour, int minute)
            throws IOException{
        String cmtTime =
                MessageFormat
                .format("{0,number,#00}:{1,number,#00}:00+09:00",
                        hour, minute);
        attrOut(writer, name, cmtTime);
        return;
    }

    /**
     * xsd:gMonthDay形式の日付属性を出力する。
     * タイムゾーンは「+09:00」固定
     * @param writer 出力先
     * @param name 属性名
     * @param month 月
     * @param day 日
     * @throws IOException 出力エラー
     */
    public static void dateAttrOut(Writer writer,
                                     CharSequence name,
                                     int month, int day)
            throws IOException{
        String dateAttr =
                MessageFormat.format("--{0,number,#00}-{1,number,#00}+09:00",
                                     month, day);
        attrOut(writer, name, dateAttr);
        return;
    }

    /**
     * xsd:dateTime形式の日付時刻属性を出力する。
     * タイムゾーンは「+09:00」固定
     * @param writer 出力先
     * @param name 属性名
     * @param epochMs エポック時刻
     * @throws IOException 出力エラー
     */
    public static void dateTimeAttr(Writer writer,
                                      CharSequence name,
                                      long epochMs)
            throws IOException{
        Calendar calendar = new GregorianCalendar(TZ_TOKYO);

        calendar.setTimeInMillis(epochMs);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        int msec = calendar.get(Calendar.MILLISECOND);

        String attrVal = MessageFormat.format(
                 "{0,number,#0000}-{1,number,#00}-{2,number,#00}"
                +"T{3,number,#00}:{4,number,#00}:{5,number,#00}"
                +".{6,number,#000}+09:00",
                year, month, day, hour, minute, sec, msec);

        attrOut(writer, name, attrVal);

        return;
    }

    /**
     * デコードエラー情報をrawdataタグで出力する。
     * 文字列集合に関するエラーの場合、windows31jでのデコード出力を試みる。
     * @param writer 出力先
     * @param errorInfo デコードエラー
     * @throws IOException 出力エラー
     */
    public static void dumpErrorInfo(Writer writer,
                                       DecodeErrorInfo errorInfo)
            throws IOException{
        int hexVal;
        hexVal = errorInfo.getRawByte1st() & 0xff;
        if(errorInfo.has2nd()){
            hexVal <<= 8;
            hexVal |= errorInfo.getRawByte2nd() & 0xff;
        }

        String hexBin = Integer.toHexString(hexVal);
        if(hexBin.length() % 2 != 0) hexBin = "0" + hexBin;

        char replaceChar = Win31j.getWin31jChar(errorInfo);

        writer.append("<rawdata");

        writer.append(' ');
        attrOut(writer, "encoding", "Shift_JIS");

        writer.append(' ');
        attrOut(writer, "hexBin", hexBin);

        writer.append(" >");
        writer.append(replaceChar);
        writer.append("</rawdata>");

        return;
    }

    /**
     * デコードエラー込みのテキストを出力する。
     * @param writer 出力先
     * @param content テキスト
     * @throws IOException 出力エラー
     */
    public static void dumpDecodedContent(Writer writer,
                                             DecodedContent content)
            throws IOException{
        if( ! content.hasDecodeError() ){
            textOut(writer, content);
            return;
        }

        int last = 0;

        List<DecodeErrorInfo> errList = content.getDecodeErrorList();
        for(DecodeErrorInfo err : errList){
            int charPos = err.getCharPosition();
            CharSequence line = content.subSequence(last, charPos);
            textOut(writer, line);
            dumpErrorInfo(writer, err);
            last = charPos + 1;
        }

        CharSequence line = content.subSequence(last, content.length());
        textOut(writer, line);

        return;
    }

    /**
     * 村情報をXML形式で出力する。
     * @param writer 出力先
     * @param villageData 村情報
     * @throws IOException 出力エラー
     */
    public static void dumpVillageData(Writer writer,
                                         VillageData villageData)
            throws IOException{
        writer.append("<?xml");
        writer.append(' ');
        attrOut(writer, "version", "1.0");
        writer.append(' ');
        attrOut(writer, "encoding", "UTF-8");
        writer.append(" ?>\n\n");

        writer.append("<!--\n");
        writer.append("  人狼BBSアーカイブ\n");
        writer.append("  http://jindolf.sourceforge.jp/\n");
        writer.append("-->\n\n");

        dumpDocType(writer);
        writer.append("\n\n");

        villageData.dumpXml(writer);

        writer.append("\n<!-- EOF -->\n");

        writer.flush();

        return;
    }

    /**
     * 村情報を反映した出力ファイル名を生成する。
     * @param village 村情報
     * @return XML出力ファイル名
     */
    public static String createOutFileName(VillageData village){
        LandDef landDef = village.getLandDef();
        String landId = landDef.getLandId();
        int vid = village.getVillageId();

        String fname =
                MessageFormat.format(
                "{0}jin_{1}_{2,number,#00000}.xml", OUTPATH, landId, vid);
        return fname;
    }

    /**
     * 村情報を反映した出力ファイルへの文字ストリームを生成する。
     * @param village 村情報
     * @return 出力先文字ストリーム
     * @throws IOException 出力エラー
     */
    public static Writer createFileWriter(VillageData village)
            throws IOException{
        String fname = createOutFileName(village);
        File file = new File(fname);

        OutputStream ostream;
        ostream = new FileOutputStream(file);
        ostream = new BufferedOutputStream(ostream, 10000);
        Writer writer;
        writer = new OutputStreamWriter(ostream, "UTF-8");
        writer = new BufferedWriter(writer, 10000);
        return writer;
    }

}
