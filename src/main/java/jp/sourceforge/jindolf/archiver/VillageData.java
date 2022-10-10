/*
 * village data
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
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

    private final List<PeriodResource> resourceList;

    private final LandDef landDef;
    private final int villageId;
    private final String baseUri;

    private String fullName = "";
    private int commitHour = -1;
    private int commitMinute = -1;
    private String graveIconUri;

    private final List<AvatarData> avatarList = new LinkedList<>();
    private int undefAvatarNo = 1;

    private final List<PeriodData> periodList = new LinkedList<>();


    /**
     * コンストラクタ。
     * @param resourceList PeriodResource並び
     */
    public VillageData(List<PeriodResource> resourceList){
        super();

        validatePeriodResource(resourceList);

        this.resourceList = new LinkedList<>(resourceList);

        PeriodResource resource1st = this.resourceList.get(0);
        this.landDef   = resource1st.getLandDef();
        this.villageId = resource1st.getVillageId();
        this.baseUri = getBaseUri(this.resourceList);

        return;
    }


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
    public void dumpAvatarList(XmlOut writer) throws IOException{
        writer.append("<avatarList>");
        writer.nl();
        writer.nl();

        for(AvatarData avatar : this.avatarList){
            avatar.dumpXml(writer);
            writer.nl();
        }

        writer.append("</avatarList>");
        writer.nl();

        return;
    }

    /**
     * 全period要素のXML出力。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpPeriodList(XmlOut writer) throws IOException{
        for(PeriodData period : this.periodList){
            period.dumpXml(writer);
            writer.nl();
        }
        return;
    }

    /**
     * village要素のXML出力。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpXml(XmlOut writer) throws IOException{
        writer.append("<village");
        writer.nl();

        writer.indent(1);
        writer.dumpNameSpaceDecl();
        writer.nl();

        writer.indent(1);
        writer.dumpSiNameSpaceDecl();
        writer.nl();

        writer.indent(1);
        writer.dumpSchemeLocation();
        writer.nl();

        writer.indent(1);
        writer.attrOut("xml:lang", "ja-JP");
        writer.nl();

        writer.indent(1);
        writer.attrOut("xml:base", this.baseUri);
        writer.nl();

        writer.indent(1);
        writer.attrOut("fullName", this.fullName);

        writer.sp();
        writer.attrOut("vid", Integer.toString(this.villageId));
        writer.nl();

        writer.indent(1);
        writer.timeAttrOut("commitTime",
                           this.commitHour, this.commitMinute);
        writer.nl();

        writer.indent(1);
        writer.attrOut("state", "gameover");

        DisclosureType type = getDisclosureType();
        if(type != DisclosureType.COMPLETE){
            writer.sp();
            writer.attrOut("disclosure", type.getXmlName());
        }

        String isValid;
        if(this.landDef.isValidVillageId(this.villageId)){
            isValid = "true";
        }else{
            isValid = "false";
        }
        writer.sp();
        writer.attrOut("isValid", isValid);
        writer.nl();

        writer.indent(1);
        writer.attrOut("landName", this.landDef.getLandName());

        writer.sp();
        writer.attrOut("formalName", this.landDef.getFormalName());
        writer.nl();

        writer.indent(1);
        writer.attrOut("landId", this.landDef.getLandId());

        writer.sp();
        writer.attrOut("landPrefix", this.landDef.getLandPrefix());
        writer.nl();

        writer.indent(1);
        String locale = this.landDef.getLocale().toString();
        locale = locale.replaceAll("_", "-");
        writer.attrOut("locale", locale);

        writer.sp();
        writer.attrOut("origencoding", this.landDef.getEncoding().name());

        writer.sp();
        writer.attrOut("timezone", this.landDef.getTimeZone().getID());
        writer.nl();

        writer.indent(1);
        writer.attrOut("graveIconURI", getGraveIconUri());
        writer.nl();

        writer.indent(1);
        writer.attrOut("generator", JinArchiver.GENERATOR);
        writer.nl();

        writer.append(">");
        writer.nl();

        writer.nl();
        dumpAvatarList(writer);

        writer.nl();
        dumpPeriodList(writer);

        writer.append("</village>");
        writer.nl();

        return;
    }

}
