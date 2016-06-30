/*
 * parse handler
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.sourceforge.jindolf.corelib.DisclosureType;
import jp.sourceforge.jindolf.corelib.EventFamily;
import jp.sourceforge.jindolf.corelib.GameRole;
import jp.sourceforge.jindolf.corelib.LandDef;
import jp.sourceforge.jindolf.corelib.PeriodType;
import jp.sourceforge.jindolf.corelib.SysEventType;
import jp.sourceforge.jindolf.corelib.TalkType;
import jp.sourceforge.jindolf.corelib.Team;
import jp.sourceforge.jindolf.corelib.VillageTag;
import jp.sourceforge.jindolf.parser.DecodedContent;
import jp.sourceforge.jindolf.parser.EntityConverter;
import jp.sourceforge.jindolf.parser.HtmlAdapter;
import jp.sourceforge.jindolf.parser.HtmlParseException;
import jp.sourceforge.jindolf.parser.PageType;
import jp.sourceforge.jindolf.parser.SeqRange;

/**
 * パーサ用ハンドラ。
 */
public class Handler extends HtmlAdapter{

    private static final Pattern MURDER_PATTERN =
            Pattern.compile("^(.*)\u0020！\u0020今日がお前の命日だ！$");

    private final EntityConverter converter = new EntityConverter();

    private VillageData villageData = null;
    private String pageTitle = null;

    private PeriodData currentPeriod = null;
    private PeriodResource currentResource = null;

    private TalkData currentTalk = null;
    private EventData currentEvent = null;

    /**
     * コンストラクタ。
     */
    public Handler(){
        super();
        return;
    }

    /**
     * 村情報を初期化。
     * @param villageDataArg 村情報
     */
    public void initVillageData(VillageData villageDataArg){
        this.villageData = villageDataArg;
        this.currentPeriod = null;
        this.pageTitle = null;
        return;
    }

    /**
     * Periodリソース情報を初期化。
     * @param resource リソース情報
     */
    public void initPeriodResource(PeriodResource resource){
        this.currentResource = resource;
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void startParse(DecodedContent content) throws HtmlParseException{
        if(this.villageData == null) throw new HtmlParseException();
        this.currentPeriod =
                new PeriodData(this.villageData, this.currentResource);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param titleRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void pageTitle(DecodedContent content, SeqRange titleRange)
            throws HtmlParseException{
        DecodedContent title = this.converter.convert(content, titleRange);
        if(this.pageTitle == null){
            this.pageTitle = title.toString();
        }else{
            if( ! this.pageTitle.contentEquals(title) ){
                throw new HtmlParseException();
            }
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param loginRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void loginName(DecodedContent content, SeqRange loginRange)
            throws HtmlParseException{
        DecodedContent account = this.converter.convert(content, loginRange);
        this.currentPeriod.setLoginName(account);
        return;
    }

    /**
     * {@inheritDoc}
     * @param type {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void pageType(PageType type) throws HtmlParseException{
        if(type != PageType.PERIOD_PAGE) throw new HtmlParseException();
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param villageRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void villageName(DecodedContent content, SeqRange villageRange)
            throws HtmlParseException{
        String vName =
                this.converter.convert(content, villageRange).toString();
        String fullName = this.villageData.getFullName();
        if(fullName.length() <= 0){
            if( ! this.pageTitle.endsWith(vName) ){
                throw new HtmlParseException();
            }
            Pattern ptn = Pattern.compile("^([^0-9]*)([0-9]+)\\s+(\\S+)$");
            Matcher matcher = ptn.matcher(vName);
            if( ! matcher.matches() ) throw new HtmlParseException();
            String prefix = matcher.group(1);
            String vid    = matcher.group(2);
            String vtag   = matcher.group(3);
            LandDef landDef = this.villageData.getLandDef();
            if( ! prefix.equals(landDef.getLandPrefix()) ){
                throw new HtmlParseException();
            }
            if( Integer.parseInt(vid) != this.villageData.getVillageId() ){
                throw new HtmlParseException();
            }
            matcher.reset(vtag);
            if(VillageTag.lookingAtVillageTag(matcher) == null){
                throw new HtmlParseException();
            }
            this.villageData.setFullName(vName);
        }else{
            if( ! vName.equals(fullName) ) throw new HtmlParseException();
        }

        return;
    }

    /**
     * {@inheritDoc}
     * @param month {@inheritDoc}
     * @param day {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void commitTime(int month, int day, int hour, int minute)
            throws HtmlParseException{
        int commitHour   = this.villageData.getCommitHour();
        int commitMinute = this.villageData.getCommitMinute();

        if(commitHour < 0){
            this.villageData.setCommitHour(hour);
        }else{
            if(hour != commitHour) throw new HtmlParseException();
        }

        if(commitMinute < 0){
            this.villageData.setCommitMinute(minute);
        }else{
            if(minute != commitMinute) throw new HtmlParseException();
        }

        this.currentPeriod.setCommitMonth(month);
        this.currentPeriod.setCommitDay(day);
        this.currentPeriod.setCommitHour(hour);
        this.currentPeriod.setCommitMinute(minute);

        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param anchorRange {@inheritDoc}
     * @param periodType {@inheritDoc}
     * @param day {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void periodLink(DecodedContent content,
                            SeqRange anchorRange,
                            PeriodType periodType,
                            int day)
            throws HtmlParseException{
        if(anchorRange.isValid()){
            DisclosureType newType;

            if(periodType == null){
                newType = DisclosureType.COMPLETE;
            }else if(    periodType == PeriodType.EPILOGUE
                      && this.currentResource.getPeriodType()
                  != PeriodType.EPILOGUE){
                newType = DisclosureType.COMPLETE;
            }else if(    periodType != PeriodType.PROLOGUE
                      && this.currentResource.getPeriodType()
                  == PeriodType.PROLOGUE){
                newType = DisclosureType.COMPLETE;
            }else{
                newType = DisclosureType.UNCOMPLETE;
            }

            this.currentPeriod.setDisclosureType(newType);

            return;
        }

        if(periodType != this.currentResource.getPeriodType()){
            throw new HtmlParseException();
        }

        if(periodType == PeriodType.PROGRESS
                && day != this.currentResource.getDay()){
            throw new HtmlParseException();
        }

        this.currentPeriod.setDisclosureType(DisclosureType.HOT);

        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void startTalk() throws HtmlParseException{
        this.currentTalk = new TalkData();
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkAvatar(DecodedContent content, SeqRange avatarRange)
            throws HtmlParseException{
        DecodedContent avatarName =
                this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentTalk.setAvatarData(avatar);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param urlRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkIconUrl(DecodedContent content, SeqRange urlRange)
            throws HtmlParseException{
        DecodedContent faceIcon = this.converter.convert(content, urlRange);
        this.currentTalk.setFaceIconUri(faceIcon.toString());
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param idRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkId(DecodedContent content, SeqRange idRange)
            throws HtmlParseException{
        DecodedContent xname = this.converter.convert(content, idRange);
        this.currentTalk.setXName(xname.toString());
        return;
    }

    /**
     * {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkTime(int hour, int minute) throws HtmlParseException{
        this.currentTalk.setHour(hour);
        this.currentTalk.setMinute(minute);
        return;
    }

    /**
     * {@inheritDoc}
     * @param type {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkType(TalkType type) throws HtmlParseException{
        this.currentTalk.setTalkType(type);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param textRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkText(DecodedContent content, SeqRange textRange)
            throws HtmlParseException{
        DecodedContent line = this.converter.convert(content, textRange);
        this.currentTalk.addLine(line);
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkBreak() throws HtmlParseException{
        this.currentTalk.addBreak();
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void endTalk() throws HtmlParseException{
        String faceIcon = this.currentTalk.getFaceIconUri();
        if(this.currentTalk.getTalkType() == TalkType.GRAVE){
            this.villageData.setGraveIconUri(faceIcon);
        }else{
            AvatarData avatar = this.currentTalk.getAvatarData();
            if(avatar.getFaceIconUri() == null){
                avatar.setFaceIconUri(faceIcon);
            }
        }

        TalkType currentTalkType = this.currentTalk.getTalkType();
        int talkLineNum = this.currentTalk.getLineNum();

        boolean hasNotMurder  = ! this.currentPeriod.hasMurderResult();
        boolean isWolfTalk    = currentTalkType == TalkType.WOLFONLY;
        boolean isOneLineTalk = talkLineNum == 1;

        boolean maybeAssaultEvent =
                hasNotMurder && isWolfTalk && isOneLineTalk;

        if(maybeAssaultEvent){
            DecodedContent line1st = this.currentTalk.get1stLine();
            Matcher matcher = MURDER_PATTERN.matcher(line1st);
            if(matcher.matches()){
                String avatarName = matcher.group(1);
                AvatarData target =
                        this.villageData.getAvatarData(avatarName);
                EventData event = buildAssaultEvent(target);
                this.currentPeriod.addTopicData(event);
                this.currentTalk = null;
                return;
            }
        }

        this.currentPeriod.addTopicData(this.currentTalk);
        this.currentTalk = null;
        return;
    }

    /**
     * 襲撃イベントを生成する。
     * @param target 襲撃対象
     * @return 襲撃イベント
     */
    private EventData buildAssaultEvent(AvatarData target){
        DecodedContent line1st = this.currentTalk.get1stLine();

        String xname = this.currentTalk.getXName();

        int hour   = this.currentTalk.getHour();
        int minute = this.currentTalk.getMinute();

        AvatarData byWhom = this.currentTalk.getAvatarData();
        String iconUri    = this.currentTalk.getFaceIconUri();
        if(byWhom.getFaceIconUri() == null){
            byWhom.setFaceIconUri(iconUri);
        }

        EventData event = new EventData();

        event.setEventType(SysEventType.ASSAULT);
        event.addLine(line1st);
        event.addAvatarData(byWhom);
        event.addAvatarData(target);
        event.addDecodedContent(new DecodedContent(xname));
        event.addInteger(hour);
        event.addInteger(minute);
        event.addDecodedContent(new DecodedContent(iconUri));

        return event;
    }

    /**
     * {@inheritDoc}
     * @param eventFamily {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void startSysEvent(EventFamily eventFamily)
            throws HtmlParseException{
        this.currentEvent = new EventData();
        return;
    }

    /**
     * {@inheritDoc}
     * @param type {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventType(SysEventType type) throws HtmlParseException{
        this.currentEvent.setEventType(type);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param contentRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventContent(DecodedContent content,
                                  SeqRange contentRange)
            throws HtmlParseException{
        DecodedContent line = this.converter.convert(content, contentRange);
        this.currentEvent.addLine(line);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param anchorRange {@inheritDoc}
     * @param contentRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventContentAnchor(DecodedContent content,
                                      SeqRange anchorRange,
                                      SeqRange contentRange)
            throws HtmlParseException{
        DecodedContent line = this.converter.convert(content, contentRange);
        this.currentEvent.addLine(line);
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventContentBreak() throws HtmlParseException{
        this.currentEvent.addBreak();
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param entryNo {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventOnStage(DecodedContent content,
                                  int entryNo,
                                  SeqRange avatarRange)
            throws HtmlParseException{
        DecodedContent avatarName =
                this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(avatar);
        this.currentEvent.addInteger(entryNo);
        return;
    }

    /**
     * {@inheritDoc}
     * @param role {@inheritDoc}
     * @param num {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventOpenRole(GameRole role, int num)
            throws HtmlParseException{
        this.currentEvent.addGameRole(role);
        this.currentEvent.addInteger(num);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventMurdered(DecodedContent content,
                                   SeqRange avatarRange)
            throws HtmlParseException{
        DecodedContent avatarName =
                this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(avatar);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventSurvivor(DecodedContent content,
                                   SeqRange avatarRange)
            throws HtmlParseException{
        DecodedContent avatarName =
                this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(avatar);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param voteByRange {@inheritDoc}
     * @param voteToRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventCounting(DecodedContent content,
                                   SeqRange voteByRange,
                                   SeqRange voteToRange)
            throws HtmlParseException{
        AvatarData avatar;

        if(voteByRange.isValid()){
            DecodedContent voteBy =
                    this.converter.convert(content, voteByRange);
            avatar = this.villageData.getAvatarData(voteBy);
            this.currentEvent.addAvatarData(avatar);
        }

        DecodedContent voteTo =
                this.converter.convert(content, voteToRange);
        avatar = this.villageData.getAvatarData(voteTo);
        this.currentEvent.addAvatarData(avatar);

        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param voteByRange {@inheritDoc}
     * @param voteToRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventCounting2(DecodedContent content,
                                    SeqRange voteByRange,
                                    SeqRange voteToRange)
            throws HtmlParseException{
        AvatarData avatar;

        DecodedContent voteBy =
                this.converter.convert(content, voteByRange);
        avatar = this.villageData.getAvatarData(voteBy);
        this.currentEvent.addAvatarData(avatar);

        DecodedContent voteTo =
                this.converter.convert(content, voteToRange);
        avatar = this.villageData.getAvatarData(voteTo);
        this.currentEvent.addAvatarData(avatar);

        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventSuddenDeath(DecodedContent content,
                                       SeqRange avatarRange)
            throws HtmlParseException{
        DecodedContent avatarName =
                this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(avatar);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @param anchorRange {@inheritDoc}
     * @param loginRange {@inheritDoc}
     * @param isLiving {@inheritDoc}
     * @param role {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventPlayerList(DecodedContent content,
                                      SeqRange avatarRange,
                                      SeqRange anchorRange,
                                      SeqRange loginRange,
                                      boolean isLiving,
                                      GameRole role)
            throws HtmlParseException{
        DecodedContent avatarName =
                this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(avatar);

        DecodedContent uri;
        if(anchorRange.isValid()){
            uri = this.converter.convert(content, anchorRange);
        }else{
            uri = new DecodedContent("");
        }
        Win31j.supplyWin31jChar(uri);
        this.currentEvent.addDecodedContent(uri);

        DecodedContent account =
                this.converter.convert(content, loginRange);
        Win31j.supplyWin31jChar(account);
        this.currentEvent.addDecodedContent(account);

        if(isLiving) this.currentEvent.addInteger(1);
        else         this.currentEvent.addInteger(0);

        this.currentEvent.addGameRole(role);

        return;
    }

    /**
     * {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @param minLimit {@inheritDoc}
     * @param maxLimit {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventAskEntry(int hour, int minute,
                                 int minLimit, int maxLimit)
            throws HtmlParseException{
        this.currentEvent.addInteger(hour);
        this.currentEvent.addInteger(minute);
        this.currentEvent.addInteger(minLimit);
        this.currentEvent.addInteger(maxLimit);
        return;
    }

    /**
     * {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventAskCommit(int hour, int minute)
            throws HtmlParseException{
        this.currentEvent.addInteger(hour);
        this.currentEvent.addInteger(minute);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventNoComment(DecodedContent content,
                                  SeqRange avatarRange)
            throws HtmlParseException{
        DecodedContent avatarName;
        avatarName = this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(avatar);
        return;
    }

    /**
     * {@inheritDoc}
     * @param winner {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventStayEpilogue(Team winner,
                                     int hour, int minute)
            throws HtmlParseException{
        GameRole role;
        switch(winner){
        case VILLAGE: role = GameRole.INNOCENT; break;
        case WOLF:    role = GameRole.WOLF;     break;
        case HAMSTER: role = GameRole.HAMSTER;  break;
        default: throw new IllegalArgumentException();
        }

        this.currentEvent.addGameRole(role);
        this.currentEvent.addInteger(hour);
        this.currentEvent.addInteger(minute);

        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param guardByRange {@inheritDoc}
     * @param guardToRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventGuard(DecodedContent content,
                                SeqRange guardByRange,
                                SeqRange guardToRange)
            throws HtmlParseException{
        DecodedContent avatarName;

        avatarName = this.converter.convert(content, guardByRange);
        AvatarData guardBy = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(guardBy);

        avatarName = this.converter.convert(content, guardToRange);
        AvatarData guardTo = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(guardTo);

        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param judgeByRange {@inheritDoc}
     * @param judgeToRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventJudge(DecodedContent content,
                                SeqRange judgeByRange,
                                SeqRange judgeToRange)
            throws HtmlParseException{
        DecodedContent avatarName;

        avatarName = this.converter.convert(content, judgeByRange);
        AvatarData judgeBy = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(judgeBy);

        avatarName = this.converter.convert(content, judgeToRange);
        AvatarData judgeTo = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(judgeTo);

        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @param votes {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventExecution(DecodedContent content,
                                    SeqRange avatarRange,
                                    int votes)
            throws HtmlParseException{
        DecodedContent avatarName;
        avatarName = this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(avatar);

        if(votes > 0){
            this.currentEvent.addInteger(votes);
        }

        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventCheckout(DecodedContent content,
                                   SeqRange avatarRange)
            throws HtmlParseException{
        DecodedContent avatarName;
        avatarName = this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(avatar);
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventVanish(DecodedContent content,
                                 SeqRange avatarRange)
            throws HtmlParseException{
        DecodedContent avatarName;
        avatarName = this.converter.convert(content, avatarRange);
        AvatarData avatar = this.villageData.getAvatarData(avatarName);
        this.currentEvent.addAvatarData(avatar);
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void endSysEvent() throws HtmlParseException{
        this.currentPeriod.addTopicData(this.currentEvent);
        this.currentEvent = null;
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void endParse() throws HtmlParseException{
        this.villageData.addPeriodData(this.currentPeriod);
        this.currentPeriod = null;
        return;
    }

}
