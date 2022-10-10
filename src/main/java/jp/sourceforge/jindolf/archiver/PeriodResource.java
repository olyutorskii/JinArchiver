/*
 * Period resource
 *
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.net.URL;
import jp.sourceforge.jindolf.corelib.LandDef;
import jp.sourceforge.jindolf.corelib.PeriodType;

/**
 * Periodのロード元情報。
 */
public class PeriodResource{

    private final LandDef landDef;
    private final int villageId;
    private final PeriodType periodType;
    private final int day;
    private final String origUrlText;
    private long downTimeMs;
    private URL resourceUrl;

    /**
     * コンストラクタ。
     * @param landDef 国情報
     * @param villageId 村ID
     * @param periodType Period種別
     * @param day 日付
     * @param origUrlText ロード元URI文字列
     * @param downTimeMs ロード時刻
     * @param resourceUrl ロード元URL
     */
    public PeriodResource(LandDef landDef,
                            int villageId,
                            PeriodType periodType,
                            int day,
                            String origUrlText,
                            long downTimeMs,
                            URL resourceUrl ) {
        super();

        this.landDef = landDef;
        this.villageId = villageId;
        this.periodType = periodType;
        this.day = day;
        this.origUrlText = origUrlText;
        this.downTimeMs = downTimeMs;
        this.resourceUrl = resourceUrl;

        return;
    }

    /**
     * 国情報を取得する。
     * @return 国情報
     */
    public LandDef getLandDef(){
        return landDef;
    }

    /**
     * 村番号を取得する。
     * @return 村番号
     */
    public int getVillageId(){
        return villageId;
    }

    /**
     * Periodの種別を取得する。
     * @return Period種別
     */
    public PeriodType getPeriodType(){
        return periodType;
    }

    /**
     * 日付を取得する。
     * @return 日付
     */
    public int getDay(){
        return day;
    }

    /**
     * オリジナルのダウンロード元URL文字列を取得する。
     * @return ダウンロード元URL文字列
     */
    public String getOrigUrlText(){
        return origUrlText;
    }

    /**
     * オリジナルのダウンロード時刻を取得する。
     * @return ダウンロード時刻。エポック秒(ms)
     */
    public long getDownTimeMs(){
        return this.downTimeMs;
    }

    /**
     * オリジナルのダウンロード時刻を設定する。
     * @param downTimeMs ダウンロード時刻。エポック秒(ms)
     */
    public void setDownTimeMs(long downTimeMs){
        this.downTimeMs = downTimeMs;
        return;
    }

    /**
     * XHTML格納先URLを取得する。
     * @return 格納先URL
     */
    public URL getResourceUrl(){
        return resourceUrl;
    }

    /**
     * XHTML格納先URLを設定する。
     * @param resourceUrl 格納先URL
     */
    public void setResourceUrl(URL resourceUrl){
        this.resourceUrl = resourceUrl;
        return;
    }

}
