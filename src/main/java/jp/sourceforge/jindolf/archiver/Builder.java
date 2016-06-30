/*
 * information builder from input
 *
 * License : The MIT License
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
     * 隠れコンストラクタ。
     */
    private Builder(){
        assert false;
        throw new AssertionError();
    }


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

        String name = charset.name();
        if("Shift_JIS".equalsIgnoreCase(name)){
            decoder = new SjisDecoder();
            builder = new ContentBuilderSJ(BUF_SZ);
        }else if("UTF-8".equalsIgnoreCase(name)){
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

            DecodedContent content;
            URLConnection conn = url.openConnection();
            try(InputStream istream = conn.getInputStream()){//;
                if(resource.getDownTimeMs() <= 0){
                    long downTimeMs = conn.getDate();
                    resource.setDownTimeMs(downTimeMs);
                }
                content = contentFromStream(charset, istream);
            }

            parser.parseAutomatic(content);
        }

        return;
    }

}
