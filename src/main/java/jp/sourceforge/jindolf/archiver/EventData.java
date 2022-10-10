/*
 * system event
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import jp.osdn.jindolf.parser.content.DecodedContent;
import jp.sourceforge.jindolf.corelib.GameRole;
import jp.sourceforge.jindolf.corelib.SysEventType;

/**
 * システムイベント モデル。
 */
public class EventData extends TopicData{

    private SysEventType eventType = null;

    private final List<AvatarData> avatarList = new LinkedList<>();
    private final List<Integer> intList = new LinkedList<>();
    private final List<GameRole> roleList = new LinkedList<>();
    private final List<DecodedContent> strList =
            new LinkedList<>();


    /**
     * コンストラクタ。
     */
    public EventData(){
        super();
        return;
    }


    /**
     * イベント種別からXML要素名を取得する。
     * @param type イベント種別
     * @return 要素名
     */
    public static String getTagName(SysEventType type){
        String tagName;

        switch(type){
        case STARTENTRY:   tagName = "startEntry";   break;
        case ONSTAGE:      tagName = "onStage";      break;
        case STARTMIRROR:  tagName = "startMirror";  break;
        case OPENROLE:     tagName = "openRole";     break;
        case MURDERED:     tagName = "murdered";     break;
        case STARTASSAULT: tagName = "startAssault"; break;
        case SURVIVOR:     tagName = "survivor";     break;
        case COUNTING:     tagName = "counting";     break;
        case NOMURDER:     tagName = "noMurder";     break;
        case SUDDENDEATH:  tagName = "suddenDeath";  break;
        case WINVILLAGE:   tagName = "winVillage";   break;
        case WINWOLF:      tagName = "winWolf";      break;
        case WINHAMSTER:   tagName = "winHamster";   break;
        case PLAYERLIST:   tagName = "playerList";   break;
        case PANIC:        tagName = "panic";        break;
        case EXECUTION:    tagName = "execution";    break;
        case VANISH:       tagName = "vanish";       break;
        case CHECKOUT:     tagName = "checkout";     break;
        case SHORTMEMBER:  tagName = "shortMember";  break;
        case ASKENTRY:     tagName = "askEntry";     break;
        case ASKCOMMIT:    tagName = "askCommit";    break;
        case NOCOMMENT:    tagName = "noComment";    break;
        case STAYEPILOGUE: tagName = "stayEpilogue"; break;
        case GAMEOVER:     tagName = "gameOver";     break;
        case GUARD:        tagName = "guard";        break;
        case JUDGE:        tagName = "judge";        break;
        case COUNTING2:    tagName = "counting2";    break;
        case ASSAULT:      tagName = "assault";      break;
        default: throw new IllegalArgumentException();
        }

        return tagName;
    }

    /**
     * 役職からXMLシンボル名を取得する。
     * @param role 役職
     * @return XMLシンボル名
     */
    public static String getRoleAttrValue(GameRole role){
        String roleName;

        switch(role){
        case INNOCENT: roleName = "innocent"; break;
        case WOLF:     roleName = "wolf";     break;
        case SEER:     roleName = "seer";     break;
        case SHAMAN:   roleName = "shaman";   break;
        case MADMAN:   roleName = "madman";   break;
        case HUNTER:   roleName = "hunter";   break;
        case FRATER:   roleName = "frater";   break;
        case HAMSTER:  roleName = "hamster";  break;
        default: throw new IllegalArgumentException();
        }

        return roleName;
    }

    /**
     * avatarRef要素をXML出力する。
     * @param writer 出力先
     * @param avatar Avatar
     * @throws IOException 出力エラー
     */
    public static void dumpAvatarRef(XmlOut writer, AvatarData avatar)
            throws IOException{
        writer.append("<avatarRef");
        writer.sp();
        writer.attrOut("avatarId", avatar.getAvatarId());
        writer.append(" />");
        writer.nl();
        return;
    }

    /**
     * システムイベント種別を取得する。
     * @return システムイベント種別
     */
    public SysEventType getEventType(){
        return this.eventType;
    }

    /**
     * システムイベント種別を設定する。
     * @param eventType システムイベント種別
     */
    public void setEventType(SysEventType eventType){
        this.eventType = eventType;
        return;
    }

    /**
     * Avatar情報を追加する。
     * @param avatarData Avatar情報
     */
    public void addAvatarData(AvatarData avatarData){
        this.avatarList.add(avatarData);
        return;
    }

    /**
     * 整数情報を追加する。
     * @param intVal 整数情報
     */
    public void addInteger(int intVal){
        this.intList.add(intVal);
        return;
    }

    /**
     * 役職情報を追加する。
     * @param role 役職情報
     */
    public void addGameRole(GameRole role){
        this.roleList.add(role);
        return;
    }

    /**
     * 文字列情報を追加する。
     * @param seq 文字列情報
     */
    public void addDecodedContent(DecodedContent seq){
        this.strList.add(seq);
        return;
    }

    /**
     * ONSTAGE属性値をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpOnstageAttr(XmlOut writer) throws IOException{
        int entryNo = this.intList.get(0);
        AvatarData avatarData = this.avatarList.get(0);

        writer.sp();
        writer.attrOut("entryNo", Integer.toString(entryNo));
        writer.sp();
        writer.attrOut("avatarId", avatarData.getAvatarId());

        return;
    }

    /**
     * avatarId属性値をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpSingleAvatarAttr(XmlOut writer) throws IOException{
        AvatarData avatarData = this.avatarList.get(0);

        writer.sp();
        writer.attrOut("avatarId", avatarData.getAvatarId());

        return;
    }

    /**
     * COUNTINGのvictim属性値をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpCountingAttr(XmlOut writer) throws IOException{
        int total = this.avatarList.size();
        if(total % 2 != 0){
            AvatarData victim = this.avatarList.get(total - 1);
            writer.sp();
            writer.attrOut("victim", victim.getAvatarId());
        }
        return;
    }

    /**
     * EXECUTIONのvictim属性値をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpExecutionAttr(XmlOut writer) throws IOException{
        int totalAvatar = this.avatarList.size();
        int totalVotes = this.intList.size();
        if(totalAvatar != totalVotes){
            AvatarData victim = this.avatarList.get(totalAvatar - 1);
            writer.sp();
            writer.attrOut("victim", victim.getAvatarId());
        }
        return;
    }

    /**
     * ASKENTRY属性値をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpAskEntryAttr(XmlOut writer) throws IOException{
        int hour     = this.intList.get(0);
        int minute   = this.intList.get(1);
        int minLimit = this.intList.get(2);
        int maxLimit = this.intList.get(3);

        writer.sp();
        writer.timeAttrOut("commitTime", hour, minute);

        writer.sp();
        writer.attrOut("minMembers", Integer.toString(minLimit));

        writer.sp();
        writer.attrOut("maxMembers", Integer.toString(maxLimit));

        return;
    }

    /**
     * ASKCOMMIT属性値をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpAskCommitAttr(XmlOut writer) throws IOException{
        int hour     = this.intList.get(0);
        int minute   = this.intList.get(1);

        writer.sp();
        writer.timeAttrOut("limitVote", hour, minute);

        writer.sp();
        writer.timeAttrOut("limitSpecial", hour, minute);

        return;
    }

    /**
     * STAYEPILOGUE属性値をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpStayEpilogueAttr(XmlOut writer) throws IOException{
        GameRole role = this.roleList.get(0);
        int hour   = this.intList.get(0);
        int minute = this.intList.get(1);

        String winner;
        switch(role){
        case INNOCENT: winner = "village"; break;
        case WOLF:     winner = "wolf";    break;
        case HAMSTER:  winner = "hamster"; break;
        default: throw new IllegalArgumentException();
        }
        writer.sp();
        writer.attrOut("maxMembers", winner);

        writer.sp();
        writer.timeAttrOut("limitTime", hour, minute);

        return;
    }

    /**
     * openRole子要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpOpenroleElem(XmlOut writer) throws IOException{
        int num = this.roleList.size();
        for(int index = 0; index < num; index++){
            int heads = this.intList.get(index);
            GameRole role = this.roleList.get(index);
            String roleName = getRoleAttrValue(role);

            writer.append("<roleHeads");
            writer.sp();
            writer.attrOut("role", roleName);
            writer.sp();
            writer.attrOut("heads", Integer.toString(heads));
            writer.append(" />");
            writer.nl();
        }
        return;
    }

    /**
     * murdered子要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpMurderedElem(XmlOut writer) throws IOException{
        for(AvatarData avatar : this.avatarList){
            dumpAvatarRef(writer, avatar);
        }
        return;
    }

    /**
     * survivor子要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpSurvivorElem(XmlOut writer) throws IOException{
        for(AvatarData avatar : this.avatarList){
            dumpAvatarRef(writer, avatar);
        }
        return;
    }

    /**
     * nocomment子要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpNoCommentElem(XmlOut writer) throws IOException{
        for(AvatarData avatar : this.avatarList){
            dumpAvatarRef(writer, avatar);
        }
        return;
    }

    /**
     * counting子要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpCountingElem(XmlOut writer) throws IOException{
        int total = this.avatarList.size();
        total = total / 2 * 2;
        for(int index = 0; index < total; index += 2){
            AvatarData voteBy = this.avatarList.get(index);
            AvatarData voteTo = this.avatarList.get(index + 1);
            writer.append("<vote");
            writer.sp();
            writer.attrOut("byWhom", voteBy.getAvatarId());
            writer.sp();
            writer.attrOut("target", voteTo.getAvatarId());
            writer.append(" />");
            writer.nl();
        }
        return;
    }

    /**
     * execution子要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpExecutionElem(XmlOut writer) throws IOException{
        int total = this.intList.size();
        for(int index = 0; index < total; index++){
            AvatarData voteTo = this.avatarList.get(index);
            int count = this.intList.get(index);
            writer.append("<nominated");
            writer.sp();
            writer.attrOut("avatarId", voteTo.getAvatarId());
            writer.sp();
            writer.attrOut("count", "" + count);
            writer.append(" />");
            writer.nl();
        }
        return;
    }

    /**
     * playerlist子要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpPlayerlistElem(XmlOut writer) throws IOException{
        int num = this.avatarList.size();

        for(int index = 0; index < num; index++){
            AvatarData avatar = this.avatarList.get(index);
            DecodedContent uri     = this.strList.get(index * 2);
            DecodedContent account = this.strList.get(index * 2 + 1);
            int isLiving = this.intList.get(index);
            String survive;
            if(isLiving == 0) survive = "false";
            else              survive = "true";
            GameRole role = this.roleList.get(index);
            String roleName = getRoleAttrValue(role);

            writer.append("<playerInfo");
            writer.sp();
            writer.attrOut("playerId", account.toString());
            writer.sp();
            writer.attrOut("avatarId", avatar.getAvatarId());
            writer.sp();
            writer.attrOut("survive", survive);
            writer.sp();
            writer.attrOut("role", roleName);

            String uriStr = uri.toString();
            uriStr = uriStr.replaceAll("^[\\s\\p{Cntrl}]+", "");
            uriStr = uriStr.replaceAll("[\\s\\p{Cntrl}]+$", "");
            uriStr = uriStr.replaceAll("[\\s]+", "\u0020");
            if(uriStr.length() > 0){
                writer.sp();
                writer.attrOut("uri", uriStr);
            }

            writer.append(" />");
            writer.nl();
        }

        return;
    }

    /**
     * Avatar間関係の属性値をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpByWhomAttr(XmlOut writer) throws IOException{
        AvatarData by = this.avatarList.get(0);
        AvatarData to = this.avatarList.get(1);

        writer.sp();
        writer.attrOut("byWhom", by.getAvatarId());
        writer.sp();
        writer.attrOut("target", to.getAvatarId());

        return;
    }

    /**
     * ASSAULT属性値をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpAssaultAttr(XmlOut writer) throws IOException{
        AvatarData by = this.avatarList.get(0);
        AvatarData to = this.avatarList.get(1);

        writer.nl();

        writer.indent(1);
        writer.attrOut("byWhom", by.getAvatarId());
        writer.sp();
        writer.attrOut("target", to.getAvatarId());
        writer.nl();

        DecodedContent xname = this.strList.get(0);
        writer.indent(1);
        writer.attrOut("xname", xname);

        int hour = this.intList.get(0);
        int minute = this.intList.get(1);
        writer.sp();
        writer.timeAttrOut("time", hour, minute);
        writer.nl();

        String icon = this.strList.get(1).toString();
        if( ! icon.equals(by.getFaceIconUri()) ){
            writer.indent(1);
            writer.attrOut("faceIconURI", icon);
            writer.nl();
        }

        return;
    }

    /**
     * システムイベント各種要素のXML出力を行う。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    @Override
    public void dumpXml(XmlOut writer) throws IOException{
        String tagName = getTagName(this.eventType);

        writer.append("<");
        writer.append(tagName);

        boolean hasAttr = true;
        switch(this.eventType){
        case ONSTAGE:
            dumpOnstageAttr(writer);
            break;
        case COUNTING:
            dumpCountingAttr(writer);
            break;
        case EXECUTION:
            dumpExecutionAttr(writer);
            break;
        case SUDDENDEATH:
        case VANISH:
        case CHECKOUT:
            dumpSingleAvatarAttr(writer);
            break;
        case ASKENTRY:
            dumpAskEntryAttr(writer);
            break;
        case ASKCOMMIT:
            dumpAskCommitAttr(writer);
            break;
        case STAYEPILOGUE:
            dumpStayEpilogueAttr(writer);
            break;
        case JUDGE:
        case GUARD:
            dumpByWhomAttr(writer);
            break;
        case ASSAULT:
            dumpAssaultAttr(writer);
            break;
        default:
            hasAttr = false;
            break;
        }

        if(hasAttr) writer.sp();
        writer.append(">");
        writer.nl();

        dumpLines(writer);

        switch(this.eventType){
        case OPENROLE:
            dumpOpenroleElem(writer);
            break;
        case MURDERED:
            dumpMurderedElem(writer);
            break;
        case SURVIVOR:
            dumpSurvivorElem(writer);
            break;
        case COUNTING:
        case COUNTING2:
            dumpCountingElem(writer);
            break;
        case EXECUTION:
            dumpExecutionElem(writer);
            break;
        case PLAYERLIST:
            dumpPlayerlistElem(writer);
            break;
        case NOCOMMENT:
            dumpNoCommentElem(writer);
            break;
        default:
            break;
        }

        writer.append("</");
        writer.append(tagName);
        writer.append(">");
        writer.nl();

        return;
    }

}
