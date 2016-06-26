/*
 * XML output
 *
 * License : The MIT License
 * Copyright(c) 2016 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import jp.sourceforge.jindolf.parser.DecodeErrorInfo;
import jp.sourceforge.jindolf.parser.DecodedContent;


/**
 * XML出力。
 */
public class XmlOut implements Appendable, Flushable, Closeable{

    private static final String ORIG_DTD =
            "http://jindolf.sourceforge.jp/xml/dtd/bbsArchive-110421.dtd";
    private static final String ORIG_NS =
            "http://jindolf.sourceforge.jp/xml/ns/501";
    private static final String ORIG_SCHEME =
            "http://jindolf.sourceforge.jp/xml/xsd/bbsArchive-110421.xsd";
    private static final String SCHEMA_NS =
            "http://www.w3.org/2001/XMLSchema-instance";

    private static final char BS_CHAR = (char) 0x005c; // Backslash
    private static final char SQ_CHAR = '\'';
    private static final char DQ_CHAR = '"';
    private static final char TILDE_CHAR     = '\u007e';
    private static final char DELETE_CHAR    = '\u007f';
    private static final char YEN_CHAR       = '\u00a5';
    private static final char OVERLINE_CHAR  = '\u203e';
    private static final char SYMNULL_CHAR   = '\u2400';
    private static final char SYMDELETE_CHAR = '\u2421';
    private static final char REP_CHAR       = '\ufffd';
    private static final String INDENT_UNIT = "\u0020\u0020";

    private static final String FORM_XSD_DATETIME =
              "{0,number,#0000}-{1,number,#00}-{2,number,#00}"
            + "T{3,number,#00}:{4,number,#00}:{5,number,#00}"
            + ".{6,number,#000}+09:00";

    private static final TimeZone TZ_TOKYO =
            TimeZone.getTimeZone("Asia/Tokyo");


    private final Writer writer;


    /**
     * コンストラクタ。
     * @param writer 出力
     */
    XmlOut(Writer writer){
        super();
        this.writer = writer;
        return;
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
     * XMLに出現可能な文字(Char)か判定する。
     *
     * <p>サロゲートペアのシーケンスまでは調べない。
     *
     * @param chVal 文字
     * @return 出現可能ならtrue
     */
    public static boolean isXmlChar(char chVal){
        if('\u0020' <= chVal && chVal <= '\ud7ff') return true;
        if('\ue000' <= chVal && chVal <= '\ufffd') return true;

        if(chVal == '\t' || chVal == '\n' || chVal == '\r') return true;

        if(Character.isSurrogate(chVal)) return true;

        return false;
    }

    /**
     * 印字可能な代替キャラクタへの変換を行う。
     *
     * <p>ControlPicturesが利用できない場合はU+FFFDを用いる。
     *
     * <p>UnicodeのControl Picturesブロックを参照せよ。
     *
     * @param chVal 対象文字
     * @return 代替キャラクタ
     */
    public static char replaceChar(char chVal){
        char result;

        if('\u0000' <= chVal && chVal <= '\u001f'){
            result = (char) ( chVal + SYMNULL_CHAR );
        }else if(chVal == DELETE_CHAR){
            result = SYMDELETE_CHAR;
        }else{
            result = REP_CHAR;
        }

        return result;
    }


    /**
     * {@inheritDoc}
     * @param csq {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public Appendable append(CharSequence csq) throws IOException{
        this.writer.append(csq);
        return this;
    }

    /**
     * {@inheritDoc}
     * @param csq {@inheritDoc}
     * @param start {@inheritDoc}
     * @param end {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public Appendable append(CharSequence csq, int start, int end)
            throws IOException{
        this.writer.append(csq, start, end);
        return this;
    }

    /**
     * {@inheritDoc}
     * @param c {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public Appendable append(char c) throws IOException{
        this.writer.append(c);
        return this;
    }

    /**
     * {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void flush() throws IOException{
        this.writer.flush();
        return;
    }

    /**
     * {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void close() throws IOException{
        this.writer.close();
        return;
    }

    /**
     * 空白を出力する。
     * @throws IOException 出力エラー
     */
    public void sp() throws IOException{
        append('\u0020');
        return;
    }

    /**
     * 改行を出力する。
     * @throws IOException 出力エラー
     */
    public void nl() throws IOException{
        append('\n');
        return;
    }

    /**
     * インデント用空白を出力する。
     * ネスト単位は空白2文字
     * @param level ネストレベル
     * @throws IOException 出力エラー
     */
    public void indent(int level) throws IOException{
        for(int ct = 1; ct <= level; ct++){
            append(INDENT_UNIT);
        }
        return;
    }

    /**
     * XML数値文字参照を出力する。
     * @param chVal 出力文字
     * @throws IOException 出力エラー
     */
    public void charRefOut(char chVal)
            throws IOException{
        int ival = 0xffff & ((int) chVal);
        String hex = Integer.toHexString(ival);
        if(hex.length() % 2 != 0) hex = "0" + hex;

        append("&#x");
        append(hex);
        append(";");

        return;
    }

    /**
     * 属性を出力する。
     * @param name 属性名
     * @param value 属性値
     * @throws IOException 出力エラー
     */
    public void attrOut(CharSequence name, CharSequence value)
            throws IOException{
        append(name);

        append('=');

        append(DQ_CHAR);
        attrValOut(value);
        append(DQ_CHAR);

        return;
    }

    /**
     * 属性値を出力する。
     *
     * <p>XML規格のAttValueを参照せよ。
     *
     * @param value 属性値
     * @throws IOException 出力エラー
     */
    private void attrValOut(CharSequence value) throws IOException{
        int len = value.length();

        for(int pos = 0; pos < len; pos++){
            char chVal = value.charAt(pos);
            switch (chVal){
            case '&':
                append("&amp;");
                break;
            case '<':
                append("&lt;");
                break;
            case '>':
                append("&gt;");
                break;
            case DQ_CHAR:
                append("&quot;");
                break;
            case SQ_CHAR:
                append("&apos;");
                break;
            default:
                append(chVal);
                break;
            }
        }

        return;
    }

    /**
     * xsd:time形式の時刻属性を出力する。
     * タイムゾーンは「+09:00」固定
     * @param name 属性名
     * @param hour 時間
     * @param minute 分
     * @throws IOException 出力エラー
     */
    public void timeAttrOut(CharSequence name, int hour, int minute)
            throws IOException{
        append(name);

        append('=');

        append(DQ_CHAR);
        digi2colOut(hour);
        append(':');
        digi2colOut(minute);
        append(":00+09:00");
        append(DQ_CHAR);

        return;
    }

    /**
     * xsd:gMonthDay形式の日付属性を出力する。
     * タイムゾーンは「+09:00」固定
     * @param name 属性名
     * @param month 月
     * @param day 日
     * @throws IOException 出力エラー
     */
    public void dateAttrOut(CharSequence name, int month, int day)
            throws IOException{
        append(name);

        append('=');

        append(DQ_CHAR);
        append("--");
        digi2colOut(month);
        append('-');
        digi2colOut(day);
        append("+09:00");
        append(DQ_CHAR);

        return;
    }

    /**
     * 二桁の整数を出力する。
     *
     * <p>負の値の出力は未定義。
     *
     * <p>100より大きい値の出力は未定義。
     *
     * @param digit 整数
     * @throws IOException 出力エラー
     */
    private void digi2colOut(int digit) throws IOException{
        int col2 = Math.abs(digit) % 100;

        char ch1st = (char) ('0' + (col2 / 10));
        char ch2nd = (char) ('0' + (col2 % 10));

        append(ch1st);
        append(ch2nd);

        return;
    }

    /**
     * xsd:dateTime形式の日付時刻属性を出力する。
     * タイムゾーンは「+09:00」固定
     * @param name 属性名
     * @param epochMs エポック時刻
     * @throws IOException 出力エラー
     */
    public void dateTimeAttr(CharSequence name, long epochMs)
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

        String attrVal =
                MessageFormat.format(
                        FORM_XSD_DATETIME,
                        year, month, day, hour, minute, sec, msec
                );

        attrOut(name, attrVal);

        return;
    }

    /**
     * デコードエラー込みのテキストを出力する。
     * @param content テキスト
     * @throws IOException 出力エラー
     */
    public void dumpDecodedContent(DecodedContent content)
            throws IOException{
        if( ! content.hasDecodeError() ){
            charDataOut(content);
            return;
        }

        int last = 0;

        List<DecodeErrorInfo> errList = content.getDecodeErrorList();
        for(DecodeErrorInfo err : errList){
            int charPos = err.getCharPosition();
            CharSequence line = content.subSequence(last, charPos);
            charDataOut(line);
            dumpErrorInfo(err);
            last = charPos + 1;
        }

        CharSequence line = content.subSequence(last, content.length());
        charDataOut(line);

        return;
    }

    /**
     * デコードエラー情報をrawdataタグで出力する。
     * 文字列集合に関するエラーの場合、windows31jでのデコード出力を試みる。
     * @param errorInfo デコードエラー
     * @throws IOException 出力エラー
     */
    public void dumpErrorInfo(DecodeErrorInfo errorInfo)
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

        rawDataOut(hexBin, replaceChar);

        return;
    }

    /**
     * 生データ出力
     * @param hex 16進文字列
     * @param replace 代替キャラクタ
     * @throws IOException 出力エラー
     */
    private void rawDataOut(String hex, char replace) throws IOException{
        append("<rawdata");

        sp();
        attrOut("encoding", "Shift_JIS");

        sp();
        attrOut("hexBin", hex);

        sp();
        append(">");
        append(replace);
        append("</rawdata>");

        return;
    }

    /**
     * XMLのCharData文字列を出力する。
     * <ul>
     * <li>先頭および末尾のホワイトスペースは強制的に文字参照化される。
     * <li>連続したホワイトスペースの2文字目以降は文字参照化される。
     * <li>スペースでないホワイトスペースは無条件に文字参照化される。
     * <li>{@literal &, <, >, "}は無条件に文字参照化される。
     * </ul>
     * 参考：XML 1.0 規格 3.3.3節
     * @param seq CDATA文字列
     * @throws IOException 出力エラー
     */
    public void charDataOut(CharSequence seq)
            throws IOException{
        int len = seq.length();

        boolean leadSpace = false;

        for(int pos = 0; pos < len; pos++){
            char chVal = seq.charAt(pos);

            if(isWhiteSpace(chVal)){
                boolean is1stPos  = pos == 0;
                boolean isLastPos = pos >= len - 1;
                if(is1stPos || isLastPos || leadSpace){
                    charRefOut(chVal);
                }else if(chVal != '\u0020'){
                    charRefOut(chVal);
                }else{
                    append(chVal);
                }
                leadSpace = true;
            }else{
                nonSpaceOut(chVal);
                leadSpace = false;
            }
        }

        return;
    }

    /**
     * CharDataのホワイトスペース以外の文字を出力する。
     * @param chVal 文字
     * @throws IOException 出力エラー
     */
    private void nonSpaceOut(char chVal) throws IOException{
        if(chVal == '&'){
            append("&amp;");
        }else if(chVal == '<'){
            append("&lt;");
        }else if(chVal == '>'){
            append("&gt;");
        }else if(chVal == DQ_CHAR){
            append("&quot;");
        }else if(chVal == SQ_CHAR){
            append("&apos;");
        }else if(chVal == BS_CHAR){
            append(YEN_CHAR);
        }else if(chVal == TILDE_CHAR){
            append(OVERLINE_CHAR);
        }else if(! isXmlChar(chVal)){
            // TODO: U+007fの扱い
            dumpRawData(chVal);
        }else{
            append(chVal);
        }

        return;
    }

    /**
     * 不正文字をXML出力する。
     * @param chVal 不正文字
     * @throws IOException 出力エラー
     */
    public void dumpRawData(char chVal)
            throws IOException{
        int hexVal;
        hexVal = chVal & 0xff;
        String hexBin = Integer.toHexString(hexVal);
        if(hexBin.length() % 2 != 0) hexBin = "0" + hexBin;

        char replaceChar = replaceChar(chVal);

        rawDataOut(hexBin, replaceChar);

        return;
    }

    /**
     * 村情報をXML形式で出力する。
     * @param villageData 村情報
     * @throws IOException 出力エラー
     */
    public void dumpVillageData(VillageData villageData)
            throws IOException{
        append("<?xml");
        sp();
        attrOut("version", "1.0");
        sp();
        attrOut("encoding", "UTF-8");
        sp();
        append("?>");
        nl();
        nl();

        append("<!--");
        nl();

        indent(1);
        append("人狼BBSアーカイブ");
        nl();

        indent(1);
        append("http://jindolf.sourceforge.jp/");
        nl();

        append("-->");
        nl();
        nl();

        dumpDocType();
        nl();
        nl();

        villageData.dumpXml(this);

        nl();
        append("<!-- EOF -->");
        nl();

        flush();

        return;
    }

    /**
     * DOCTYPE宣言を出力する。
     * @throws IOException 出力エラー
     */
    public void dumpDocType() throws IOException{
        append("<!DOCTYPE village SYSTEM");
        sp();
        append('"');
        append(ORIG_DTD);
        append('"');
        sp();
        append(">");
        return;
    }

    /**
     * オリジナルNameSpace宣言を出力する。
     * @throws IOException 出力エラー
     */
    public void dumpNameSpaceDecl()
            throws IOException{
        attrOut("xmlns", ORIG_NS);
        return;
    }

    /**
     * スキーマNameSpace宣言を出力する。
     * @throws IOException 出力エラー
     */
    public void dumpSiNameSpaceDecl()
            throws IOException{
        attrOut("xmlns:xsi", SCHEMA_NS);
        return;
    }

    /**
     * スキーマ位置指定を出力する。
     * @throws IOException 出力エラー
     */
    public void dumpSchemeLocation()
            throws IOException{
        attrOut("xsi:schemaLocation",
                ORIG_NS + " " + ORIG_SCHEME);
        return;
    }

}
