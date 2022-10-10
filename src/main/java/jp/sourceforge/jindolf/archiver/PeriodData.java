/*
 * period model
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import jp.osdn.jindolf.parser.content.DecodedContent;
import jp.sourceforge.jindolf.corelib.DisclosureType;
import jp.sourceforge.jindolf.corelib.SysEventType;

/**
 * Periodモデル。
 */
public class PeriodData{

    private final VillageData parent;
    private final PeriodResource resource;
    private DecodedContent loginName = new DecodedContent("");
    private int commitMonth;
    private int commitDay;
    private int commitHour;
    private int commitMinute;
    private DisclosureType disclosureType = DisclosureType.HOT;
    private boolean hasMurderResult = false;

    private final List<TopicData> topicList = new LinkedList<>();

    /**
     * コンストラクタ。
     * @param parent 所属村
     * @param resource ロード元情報
     */
    public PeriodData(VillageData parent, PeriodResource resource){
        super();
        this.parent = parent;
        this.resource = resource;
        return;
    }

    /**
     * ロード時のログイン名を取得する。
     * @return ログイン名
     */
    public DecodedContent getLoginName(){
        return this.loginName;
    }

    /**
     * ロード時のログイン名を設定する。
     * @param loginName ログイン名
     */
    public void setLoginName(DecodedContent loginName){
        this.loginName = loginName;
        return;
    }

    /**
     * コミット月を取得する。
     * @return コミット月
     */
    public int getCommitMonth(){
        return this.commitMonth;
    }

    /**
     * コミット月を設定する。
     * @param commitMonth コミット月
     */
    public void setCommitMonth(int commitMonth){
        this.commitMonth = commitMonth;
        return;
    }

    /**
     * コミット日を取得する。
     * @return コミット日
     */
    public int getCommitDay(){
        return this.commitDay;
    }

    /**
     * コミット日を設定する。
     * @param commitDay コミット日
     */
    public void setCommitDay(int commitDay){
        this.commitDay = commitDay;
        return;
    }

    /**
     * コミット時を取得する。
     * @return コミット時
     */
    public int getCommitHour(){
        return this.commitHour;
    }

    /**
     * コミット時を設定する。
     * @param commitHour コミット時
     */
    public void setCommitHour(int commitHour){
        this.commitHour = commitHour;
        return;
    }

    /**
     * コミット分を取得する。
     * @return コミット分
     */
    public int getCommitMinute(){
        return this.commitMinute;
    }

    /**
     * コミット分を設定する。
     * @param commitMinute コミット分
     */
    public void setCommitMinute(int commitMinute){
        this.commitMinute = commitMinute;
        return;
    }

    /**
     * 開示状況を取得する。
     * @return 開示状況
     */
    public DisclosureType getDisclosureType(){
        return this.disclosureType;
    }

    /**
     * 開示状況を設定する。
     * @param type 開示状況
     */
    public void setDisclosureType(DisclosureType type){
        this.disclosureType = type;
        return;
    }

    /**
     * 襲撃結果イベントが既に格納されているか確認する。
     * @return 襲撃結果があればtrue
     */
    public boolean hasMurderResult(){
        return this.hasMurderResult;
    }

    /**
     * TopicDataを追加する。
     * 襲撃結果の有無も判定される。
     * @param topicData TopiData
     */
    public void addTopicData(TopicData topicData){
        this.topicList.add(topicData);

        if(topicData instanceof EventData){
            EventData event = (EventData) topicData;
            SysEventType type = event.getEventType();
            if(    type == SysEventType.MURDERED
                || type == SysEventType.NOMURDER){
                this.hasMurderResult = true;
            }
        }

        return;
    }

    /**
     * period要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpXml(XmlOut writer) throws IOException{
        writer.append("<period");
        writer.nl();

        String ptype;
        switch(this.resource.getPeriodType()){
        case PROLOGUE:
            ptype = "prologue";
            break;
        case PROGRESS:
            ptype = "progress";
            break;
        case EPILOGUE:
            ptype = "epilogue";
            break;
        default:
            throw new IllegalArgumentException();
        }

        writer.indent(1);
        writer.attrOut("type", ptype);

        writer.sp();
        writer.attrOut("day", Integer.toString(this.resource.getDay()));
        writer.nl();

        if(this.disclosureType != DisclosureType.COMPLETE){
            writer.indent(1);
            writer.attrOut("disclosure", this.disclosureType.getXmlName());
            writer.nl();
        }

        writer.indent(1);
        writer.dateAttrOut("nextCommitDay",
                           this.commitMonth, this.commitDay);

        writer.sp();
        writer.timeAttrOut("commitTime",
                           this.commitHour, this.commitMinute);
        writer.nl();

        URI baseUri   = URI.create(this.parent.getBaseUri());
        URI periodUri = URI.create(this.resource.getOrigUrlText());
        URI relativeUri = baseUri.relativize(periodUri);
        writer.indent(1);
        writer.attrOut("sourceURI", relativeUri.toString());
        writer.nl();

        long downTimeMs = this.resource.getDownTimeMs();
        writer.indent(1);
        writer.dateTimeAttr("loadedTime", downTimeMs);
        writer.nl();

        if(this.loginName.length() > 0){
            writer.indent(1);
            writer.attrOut("loadedBy", this.loginName.toString());
            writer.nl();
        }

        writer.append(">");
        writer.nl();
        writer.nl();

        for(TopicData topic : this.topicList){
            topic.dumpXml(writer);
            writer.nl();
        }

        writer.append("</period>");
        writer.nl();

        return;
    }

}
