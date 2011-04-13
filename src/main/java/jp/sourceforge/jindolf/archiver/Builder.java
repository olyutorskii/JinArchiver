/*
 * information builder from input
 *
 * Copyright(c) 2008 olyutorskii
 * $Id: Builder.java 877 2009-10-25 15:16:13Z olyutorskii $
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import jp.sourceforge.jindolf.parser.ContentBuilder;
import jp.sourceforge.jindolf.parser.ContentBuilderSJ;
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

    /**
     * 入力ストリームをShift_JISでデコードする。
     * @param istream 入力
     * @return デコード結果
     * @throws IOException 入力エラー
     * @throws DecodeException デコードエラー
     */
    public static DecodedContent contentFromStream(InputStream istream)
            throws IOException, DecodeException{
        StreamDecoder decoder = new SjisDecoder();
        ContentBuilder builder = new ContentBuilderSJ();
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
            DecodedContent content = contentFromStream(istream);
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
