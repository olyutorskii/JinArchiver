/*
 * information builder from input
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import io.bitbucket.olyutorskii.jiocema.DecodeBreakException;
import io.bitbucket.olyutorskii.jiocema.DecodeNotifier;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import jp.osdn.jindolf.parser.HtmlParseException;
import jp.osdn.jindolf.parser.HtmlParser;
import jp.osdn.jindolf.parser.content.ContentBuilder;
import jp.osdn.jindolf.parser.content.ContentBuilderSJ;
import jp.osdn.jindolf.parser.content.DecodedContent;
import jp.osdn.jindolf.parser.content.SjisNotifier;

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
     * @throws DecodeBreakException デコードエラー
     */
    public static DecodedContent contentFromStream(Charset charset,
                                                     InputStream istream)
            throws IOException, DecodeBreakException{
        DecodeNotifier decoder;
        ContentBuilder builder;

        String name = charset.name();
        if("Shift_JIS".equalsIgnoreCase(name)){
            decoder = new SjisNotifier();
            builder = new ContentBuilderSJ(BUF_SZ);
        }else if("UTF-8".equalsIgnoreCase(name)){
            decoder = new DecodeNotifier(charset.newDecoder());
            builder = new ContentBuilder(BUF_SZ);
        }else{
            assert false;
            return null;
        }

        decoder.setCharDecodeListener(builder);

        decoder.decode(istream);

        DecodedContent content = builder.getContent();

        return content;
    }

    /**
     * 村の各日々をロードしパースする。
     * @param villageData 村情報
     * @throws IOException 入力エラー
     * @throws DecodeBreakException デコードエラー
     * @throws HtmlParseException パースエラー
     */
    public static void fillVillageData(VillageData villageData)
            throws IOException, DecodeBreakException, HtmlParseException {
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
