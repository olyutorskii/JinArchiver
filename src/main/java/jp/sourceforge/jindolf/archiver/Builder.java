/*
 * information builder from input
 *
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import jp.sourceforge.jindolf.parser.ContentBuilder;
import jp.sourceforge.jindolf.parser.ContentBuilderSJ;
import jp.sourceforge.jindolf.parser.ContentBuilderUCS2;
import jp.sourceforge.jindolf.parser.DecodeException;
import jp.sourceforge.jindolf.parser.DecodedContent;
import jp.sourceforge.jindolf.parser.HtmlParseException;
import jp.sourceforge.jindolf.parser.HtmlParser;
import jp.sourceforge.jindolf.parser.SjisDecoder;
import jp.sourceforge.jindolf.parser.StreamDecoder;

/**
 * 入力から内部構造を生成する。
 */
public final class Builder{

    private static final int BUF_SZ = 100 * 1024;

    /**
     * 入力ストリームをデコードする。
     * @param charset 文字コード指定
     * @param istream 入力ストリーム
     * @return デコード結果
     * @throws IOException 入力エラー
     * @throws DecodeException デコードエラー
     */
    public static DecodedContent contentFromStream(Charset charset,
                                                     InputStream istream)
            throws IOException, DecodeException{
        StreamDecoder decoder;
        ContentBuilder builder;

        if(charset.name().equalsIgnoreCase("Shift_JIS")){
            decoder = new SjisDecoder();
            builder = new ContentBuilderSJ(BUF_SZ);
        }else if(charset.name().equalsIgnoreCase("UTF-8")){
            decoder = new StreamDecoder(charset.newDecoder());
            builder = new ContentBuilderUCS2(BUF_SZ);
        }else{
            assert false;
            return null;
        }

        decoder.setDecodeHandler(builder);

        decoder.decode(istream);

        DecodedContent content = builder.getContent();

        return content;
    }

    /**
     * 村の各日々をロードしパースする。
     * @param villageData 村情報
     * @throws IOException 入力エラー
     * @throws DecodeException デコードエラー
     * @throws HtmlParseException パースエラー
     */
    public static void fillVillageData(VillageData villageData)
            throws IOException, DecodeException, HtmlParseException {
        HtmlParser parser = new HtmlParser();
        Handler handler = new Handler();
        parser.setBasicHandler   (handler);
        parser.setTalkHandler    (handler);
        parser.setSysEventHandler(handler);

        handler.initVillageData(villageData);

        Charset charset = villageData.getLandDef().getEncoding();

        for(PeriodResource resource : villageData.getPeriodResourceList()){
            handler.initPeriodResource(resource);
            URL url;
            url = resource.getResourceUrl();
            if(url == null){
                url = new URL(resource.getOrigUrlText());
            }
            URLConnection conn = url.openConnection();
            InputStream istream = conn.getInputStream();
            if(resource.getDownTimeMs() <= 0){
                long downTimeMs = conn.getDate();
                resource.setDownTimeMs(downTimeMs);
            }
            DecodedContent content = contentFromStream(charset, istream);
            istream.close();
            parser.parseAutomatic(content);
        }

        return;
    }

    /**
     * 隠れコンストラクタ。
     */
    private Builder(){
        super();
        return;
    }

}
