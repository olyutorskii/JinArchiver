/*
 * village data
 *
 * Copyright(c) 2008 olyutorskii
 * $Id: VillageData.java 877 2009-10-25 15:16:13Z olyutorskii $
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import jp.sourceforge.jindolf.corelib.DisclosureType;
import jp.sourceforge.jindolf.corelib.LandDef;
import jp.sourceforge.jindolf.corelib.PeriodType;
import jp.sourceforge.jindolf.corelib.PreDefAvatar;

/**
 * 村のデータモデル。
 * villageタグに相当。
 */
public class VillageData{

    /**
     * PeriodResourceの組が正当かチェックする。
     * <ul>
     * <li>全て同じ国に属していなければならない
     * <li>全て同じ村に属していなければならない
     * <li>日付は0から始まる連続した数値でなければならない
     * <li>プロローグで始まらなければならない
     * <li>エピローグで終わらなければならない
     * <li>進行日はプロローグとエピローグに挟まれていなければならない
     * </ul>
     * @param list PeriodResource並び
     * @throws IllegalArgumentException 引数が正当でない
     */
    public static void validatePeriodResource(List<PeriodResource> list)
            throws IllegalArgumentException{
        LandDef landDef = null;
        int villageId = -1;
        int lastDay = -1;
        PeriodType periodType = null;

        for(PeriodResource resource : list){
            if(landDef == null){
                landDef = resource.getLandDef();
            }else if(resource.getLandDef() != landDef){
                throw new IllegalArgumentException();
            }

            if(villageId < 0){
                villageId = resource.getVillageId();
            }else if(resource.getVillageId() != villageId){
                throw new IllegalArgumentException();
            }

            if(lastDay < 0){
                lastDay = resource.getDay();
                if(lastDay != 0) throw new IllegalArgumentException();
            }else{
                if(resource.getDay() != lastDay + 1){
                    throw new IllegalArgumentException();
                }
                lastDay = resource.getDay();
            }

            if(periodType == null){
                periodType = resource.getPeriodType();
                if(periodType != PeriodType.PROLOGUE){
                    throw new IllegalArgumentException();
                }
                if(lastDay != 0) throw new IllegalArgumentException();
            }else if(periodType == PeriodType.PROLOGUE){
                periodType = resource.getPeriodType();
                if(periodType != PeriodType.PROGRESS){
                    throw new IllegalArgumentException();
                }
            }else if(periodType == PeriodType.PROGRESS){
                periodType = resource.getPeriodType();
            }else if(periodType == PeriodType.EPILOGUE){
                throw new IllegalArgumentException();
            }
        }

        if(lastDay < 0) throw new IllegalArgumentException();
        if(periodType != PeriodType.EPILOGUE){
            throw new IllegalArgumentException();
        }

        return;
    }

    /**
     * 全PeriodResourceから、共通するベースURIを抽出する。
     * @param list PeriodResource並び
     * @return ベースURI文字列
     * @throws IllegalArgumentException ベースURIが一致していない
     */
    public static String getBaseUri(List<PeriodResource> list)
            throws IllegalArgumentException{
        String result = null;

        for(PeriodResource resource : list){
            String urlText = resource.getOrigUrlText();
            urlText = urlText.replaceAll("[^/]*$", "");
            if(result == null){
                result = urlText;
            }else{
                if( ! result.equals(urlText) ){
                    throw new IllegalArgumentException();
                }
            }
        }

        return result;
    }

    private final List<PeriodResource> resourceList;

    private final LandDef landDef;
    private final int villageId;
    private final String baseUri;

    private String fullName = "";
    private int commitHour = -1;
    private int commitMinute = -1;
    private String graveIconUri;

    private final List<AvatarData> avatarList = new LinkedList<AvatarData>();
    private int undefAvatarNo = 1;

    private final List<PeriodData> periodList = new LinkedList<PeriodData>();


    /**
     * コンストラクタ。
     * @param resourceList PeriodResource並び
     */
    public VillageData(List<PeriodResource> resourceList){
        super();

        validatePeriodResource(resourceList);

        this.resourceList = new LinkedList<PeriodResource>(resourceList);

        PeriodResource resource1st = this.resourceList.get(0);
        this.landDef   = resource1st.getLandDef();
        this.villageId = resource1st.getVillageId();
        this.baseUri = getBaseUri(this.resourceList);

        return;
    }

    /**
     * 国情報を取得する。
     * @return 国情報
     */
    public LandDef getLandDef(){
        return this.landDef;
    }

    /**
     * 村IDを取得する。
     * @return 村ID
     */
    public int getVillageId(){
        return this.villageId;
    }

    /**
     * ベースURIを取得する。
     * @return ベースURI
     */
    public String getBaseUri(){
        return this.baseUri;
    }

    /**
     * 村フルネームを取得する。
     * @return 村フルネーム
     */
    public String getFullName(){
        return this.fullName;
    }

    /**
     * 村フルネームを設定する。
     * @param fullName 村フルネーム
     */
    public void setFullName(String fullName){
        this.fullName = fullName;
        return;
    }

    /**
     * 更新時を取得する。
     * @return 更新時
     */
    public int getCommitHour(){
        return this.commitHour;
    }

    /**
     * 更新時を設定する。
     * @param commitHour 更新時
     */
    public void setCommitHour(int commitHour){
        this.commitHour = commitHour;
        return;
    }

    /**
     * 更新分を取得する。
     * @return 更新分
     */
    public int getCommitMinute(){
        return this.commitMinute;
    }

    /**
     * 更新分を設定する。
     * @param commitMinute 更新分
     */
    public void setCommitMinute(int commitMinute){
        this.commitMinute = commitMinute;
        return;
    }

    /**
     * 墓アイコンURIを取得する。
     * @return 墓アイコンURI文字列
     */
    public String getGraveIconUri(){
        if(this.graveIconUri == null){
            return this.landDef.getTombFaceIconURI().toASCIIString();
        }
        return this.graveIconUri;
    }

    /**
     * 墓アイコンURI文字列を設定する。
     * @param graveIconUri 墓アイコンURI文字列
     */
    public void setGraveIconUri(String graveIconUri){
        this.graveIconUri = graveIconUri;
        return;
    }

    /**
     * 全Periodの開示状況から総合開示状況を算出する。
     * @return 公開状況
     */
    public DisclosureType getDisclosureType(){
        DisclosureType result = DisclosureType.COMPLETE;

        for(PeriodData period : this.periodList){
            DisclosureType type = period.getDisclosureType();
            switch(type){
            case HOT:
                return DisclosureType.HOT;
            case UNCOMPLETE:
                result = DisclosureType.UNCOMPLETE;
                break;
            default:
                break;
            }
        }

        return result;
    }

    /**
     * Periodモデルを追加する。
     * @param period Periodモデル
     */
    public void addPeriodData(PeriodData period){
        this.periodList.add(period);
        return;
    }

    /**
     * PeriodResourcenar並びを取得する。
     * @return PeriodResource並び
     */
    public List<PeriodResource> getPeriodResourceList(){
        return Collections.unmodifiableList(this.resourceList);
    }

    /**
     * 未知の新規Avatarを生成する。
     * ※ F1556村などへの対処。
     * Avatarのフルネーム、短縮名、識別子が設定される。
     * @param avfullName Avatarのフルネーム
     * @return 新規Avatarモデル
     */
    public AvatarData createAvatar(String avfullName){
        AvatarData avatar = new AvatarData();

        avatar.setFullName(avfullName);

        String[] token = avfullName.split("\\s");
        String shortName = token[token.length - 1];
        avatar.setShortName(shortName);

        String avatarId = "ukavatar" + this.undefAvatarNo;
        this.undefAvatarNo++;
        avatar.setAvatarId(avatarId);

        return avatar;
    }

    /**
     * AvatarフルネームからAvatarを得る。
     * まだこの村にいないAvatarならAvatar一覧に登録される。
     * @param seq Avatarフルネーム
     * @return Avatarモデル
     */
    public AvatarData getAvatarData(CharSequence seq){
        for(AvatarData avatar : this.avatarList){
            String avfullName = avatar.getFullName();
            if(avfullName.contentEquals(seq)){
                return avatar;
            }
        }

        PreDefAvatar predefAvatar =
                AvatarData.getPreDefAvatar(seq);
        if(predefAvatar != null){
            AvatarData avatar = new AvatarData(predefAvatar);
            this.avatarList.add(avatar);
            return avatar;
        }

        AvatarData avatar = createAvatar(seq.toString());
        this.avatarList.add(avatar);

        return avatar;
    }

    /**
     * avatarList要素のXML出力。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpAvatarList(Writer writer) throws IOException{
        writer.append("<avatarList>").append("\n\n");

        for(AvatarData avatar : this.avatarList){
            avatar.dumpXml(writer);
            writer.append('\n');
        }

        writer.append("</avatarList>").append('\n');

        return;
    }

    /**
     * 全period要素のXML出力。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpPeriodList(Writer writer) throws IOException{
        for(PeriodData period : this.periodList){
            period.dumpXml(writer);
            writer.append('\n');
        }
        return;
    }

    /**
     * village要素のXML出力。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpXml(Writer writer) throws IOException{
        writer.append("<village\n");

        XmlUtils.indent(writer, 1);
        XmlUtils.dumpNameSpaceDecl(writer);
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.dumpSiNameSpaceDecl(writer);
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.dumpSchemeLocation(writer);
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "xml:lang", "ja-JP");
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "xml:base", this.baseUri);
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "fullName", this.fullName);

        writer.append(' ');
        XmlUtils.attrOut(writer, "vid", Integer.toString(this.villageId));
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.timeAttrOut(writer,
                             "commitTime",
                             this.commitHour, this.commitMinute);
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "state", "gameover");

        DisclosureType type = getDisclosureType();
        if(type != DisclosureType.COMPLETE){
            writer.append(' ');
            XmlUtils.attrOut(writer, "disclosure", type.getXmlName());
        }

        String isValid;
        if(this.landDef.isValidVillageId(this.villageId)){
            isValid = "true";
        }else{
            isValid = "false";
        }
        writer.append(' ');
        XmlUtils.attrOut(writer, "isValid", isValid);
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "landName", this.landDef.getLandName());

        writer.append(' ');
        XmlUtils.attrOut(writer, "formalName", this.landDef.getFormalName());
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "landId", this.landDef.getLandId());

        writer.append(' ');
        XmlUtils.attrOut(writer, "landPrefix", this.landDef.getLandPrefix());
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        String locale = this.landDef.getLocale().toString();
        locale = locale.replaceAll("_", "-");
        XmlUtils.attrOut(writer, "locale", locale);

        writer.append(' ');
        XmlUtils.attrOut(writer,
                "origencoding", this.landDef.getEncoding().name());

        writer.append(' ');
        XmlUtils.attrOut(writer,
                "timezone", this.landDef.getTimeZone().getID());
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "graveIconURI", getGraveIconUri());
        writer.append('\n');

        XmlUtils.indent(writer, 1);
        XmlUtils.attrOut(writer, "generator", JinArchiver.GENERATOR);
        writer.append('\n');

        writer.append(">").append('\n');

        writer.append('\n');
        dumpAvatarList(writer);

        writer.append('\n');
        dumpPeriodList(writer);

        writer.append("</village>").append("\n");

        return;
    }

}
