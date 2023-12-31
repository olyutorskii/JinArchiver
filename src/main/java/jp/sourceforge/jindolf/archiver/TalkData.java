/*
 * talk dialog
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import jp.sourceforge.jindolf.corelib.TalkType;

/**
 * 発言モデル。
 */
public class TalkData extends TopicData{

    private TalkType talkType = null;
    private AvatarData avatarData = null;
    private String xName;
    private String faceIconUri = null;
    private int hour;
    private int minute;

    /**
     * コンストラクタ。
     */
    public TalkData(){
        super();
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
     * @param xNameArg name属性値
     */
    public void setXName(String xNameArg){
        this.xName = xNameArg;
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
    public void dumpXml(XmlOut writer) throws IOException{
        writer.append("<talk");
        writer.nl();

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

        writer.indent(1);
        writer.attrOut("type", typeStr);

        writer.sp();
        writer.attrOut("avatarId", this.avatarData.getAvatarId());
        writer.nl();

        writer.indent(1);
        writer.attrOut("xname", this.xName);

        writer.sp();
        writer.timeAttrOut("time", this.hour, this.minute);
        writer.nl();

        if(    this.talkType != TalkType.GRAVE
            && ! this.faceIconUri.equals(this.avatarData.getFaceIconUri()) ){
            writer.indent(1);
            writer.attrOut("faceIconURI", this.faceIconUri);
            writer.nl();
        }

        writer.append(">");
        writer.nl();

        dumpLines(writer);

        writer.append("</talk>");
        writer.nl();

        return;
    }

}
