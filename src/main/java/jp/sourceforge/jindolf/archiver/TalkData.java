/*
 * talk dialog
 *
 * Copyright(c) 2008 olyutorskii
 * $Id: TalkData.java 877 2009-10-25 15:16:13Z olyutorskii $
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.Writer;
import jp.sourceforge.jindolf.corelib.TalkType;

/**
 * 発言モデル。
 */
public class TalkData extends TopicData{

    private final PeriodData parent;
    private TalkType talkType = null;
    private AvatarData avatarData = null;
    private String xName;
    private String faceIconUri = null;
    private int hour;
    private int minute;

    /**
     * コンストラクタ。
     * @param parent 所属Period
     */
    public TalkData(PeriodData parent){
        super();
        this.parent = parent;
        return;
    }

    /**
     * 発言種別を取得する。
     * @return 発言種別
     */
    public TalkType getTalkType(){
        return this.talkType;
    }

    /**
     * 発言種別を設定する。
     * @param talkType 発言種別
     */
    public void setTalkType(TalkType talkType){
        this.talkType = talkType;
        return;
    }

    /**
     * 発言したAvatarを取得する。
     * @return 発言Avatar
     */
    public AvatarData getAvatarData(){
        return this.avatarData;
    }

    /**
     * 発言したAvatarを設定する。
     * @param avatarData 発言Avatar
     */
    public void setAvatarData(AvatarData avatarData){
        this.avatarData = avatarData;
        return;
    }

    /**
     * 元発言のname属性値を取得する。
     * @return name属性値
     */
    public String getXName(){
        return this.xName;
    }

    /**
     * 元発言のname属性値を設定する。
     * @param xName name属性値
     */
    public void setXName(String xName){
        this.xName = xName;
        return;
    }

    /**
     * 顔アイコン画像URI文字列を取得する。
     * @return 顔アイコン画像URI文字列
     */
    public String getFaceIconUri(){
        return this.faceIconUri;
    }

    /**
     * 顔アイコン画像URI文字列を設定する。
     * @param faceIconUri 顔アイコン画像URI文字列
     */
    public void setFaceIconUri(String faceIconUri){
        this.faceIconUri = faceIconUri;
        return;
    }

    /**
     * 発言時を取得する。
     * @return 発言時
     */
    public int getHour(){
        return this.hour;
    }

    /**
     * 発言時を設定する。
     * @param hour 発言時
     */
    public void setHour(int hour){
        this.hour = hour;
        return;
    }

    /**
     * 発言分を取得する。
     * @return 発言分
     */
    public int getMinute(){
        return this.minute;
    }

    /**
     * 発言分を設定する。
     * @param minute 発言分
     */
    public void setMinute(int minute){
        this.minute = minute;
        return;
    }

    /**
     * talk要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    @Override
    public void dumpXml(Writer writer) throws IOException{
        writer.append("<talk\n");

        String typeStr;
        switch(this.talkType){
        case PUBLIC:
            typeStr = "public";
            break;
        case WOLFONLY:
            typeStr = "wolf";
            break;
        case PRIVATE:
            typeStr = "private";
            break;
        case GRAVE:
            typeStr = "grave";
            break;
        default:
            throw new IllegalArgumentException();
        }

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "type", typeStr);

        writer.append(' ');
        XmlUtils.attrOut(writer, "avatarId", this.avatarData.getAvatarId());
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "xname", this.xName);

        writer.append(' ');
        XmlUtils.timeAttrOut(writer, "time", this.hour, this.minute);
        writer.append('\n');

        if(   this.talkType != TalkType.GRAVE
           && ! this.faceIconUri.equals(this.avatarData.getFaceIconUri()) ){
            XmlUtils.indent(writer, 1);
            XmlUtils.attrOut(writer, "faceIconURI", this.faceIconUri);
            writer.append('\n');
        }

        writer.append(">\n");

        dumpLines(writer);

        writer.append("</talk>\n");
        return;
    }

}
