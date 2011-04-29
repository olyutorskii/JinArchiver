/*
 * windows-31j encoding utilities
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import jp.sourceforge.jindolf.parser.DecodeErrorInfo;
import jp.sourceforge.jindolf.parser.DecodedContent;

/**
 * windows-31jエンコーディング(機種依存文字)に関する諸々。
 *
 * TODO 携帯絵文字サポート
 */
public final class Win31j{

    /** デフォルト置換文字。 */
    public static final char REP_CHAR = '\ufffd';

    /** windows-31j Charset. */
    public static final Charset CS_WIN31J = Charset.forName("windows-31j");

    private static final CharsetDecoder WIN31JDECODER;
    private static final ByteBuffer byteBuffer = ByteBuffer.allocate(2);

    static{
        WIN31JDECODER = CS_WIN31J.newDecoder();
        WIN31JDECODER.onMalformedInput(CodingErrorAction.REPORT);
        WIN31JDECODER.onUnmappableCharacter(CodingErrorAction.REPORT);
        WIN31JDECODER.reset();
        byteBuffer.clear();
    }

    /**
     * winsows-31jエンコーディングされたと想定した2バイトデータ
     * の復号を試みる。
     * 復号に失敗すればU+FFFDを返す。
     * @param b1 1バイト目
     * @param b2 2バイト目
     * @return 復号化された1文字
     */
    public static synchronized char getWin31jChar(byte b1, byte b2){
        char replaced;

        WIN31JDECODER.reset();
        byteBuffer.clear();
        byteBuffer.put(b1).put(b2);
        byteBuffer.flip();

        try{
            replaced = WIN31JDECODER.decode(byteBuffer).charAt(0);
        }catch(CharacterCodingException e){
            replaced = REP_CHAR;
        }

        return replaced;
    }

    /**
     * デコードエラーがwindows-31jに由来する物と仮定して
     * 復号を試みる。
     * 1バイトエラーもしくは復号に失敗すればU+FFFDを返す。
     * @param info デコードエラー
     * @return 復号化された1文字
     */
    public static char getWin31jChar(DecodeErrorInfo info){
        if( ! info.has2nd() ) return REP_CHAR;

        byte b1 = info.getRawByte1st();
        byte b2 = info.getRawByte2nd();
        char replaceChar = getWin31jChar(b1, b2);

        return replaceChar;
    }

    /**
     * デコードエラーを含む文字列に対し、
     * windows-31jによる復号での補完を試みる。
     * @param content 文字列
     */
    public static void supplyWin31jChar(DecodedContent content){
        if( ! content.hasDecodeError() ) return;

        for(DecodeErrorInfo info : content.getDecodeErrorList()){
            int pos = info.getCharPosition();
            char replaceChar = getWin31jChar(info);
            content.setCharAt(pos, replaceChar);
        }

        return;
    }

    /**
     * 隠しコンストラクタ。
     */
    private Win31j(){
        throw new Error();
    }

}
