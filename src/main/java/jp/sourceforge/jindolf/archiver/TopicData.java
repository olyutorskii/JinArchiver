/*
 * topic data
 *
 * Copyright(c) 2008 olyutorskii
 * $Id: TopicData.java 877 2009-10-25 15:16:13Z olyutorskii $
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import jp.sourceforge.jindolf.parser.DecodedContent;

/**
 * テキスト行の集合。
 */
public abstract class TopicData{

    private static final DecodedContent BREAK = new DecodedContent("\n");

    private final List<DecodedContent> lineList =
            new LinkedList<DecodedContent>();

    /**
     * コンストラクタ。
     */
    protected TopicData(){
        super();
        return;
    }

    /**
     * 行を追加する。
     * @param content 行を構成する文字列
     */
    public void addLine(DecodedContent content){
        this.lineList.add(content);
        return;
    }

    /**
     * 行ブレークを追加する。
     */
    public void addBreak(){
        this.lineList.add(BREAK);
        return;
    }

    /**
     * 行数を取得する。
     * @return 行数
     */
    public int getLineNum(){
        return this.lineList.size();
    }

    /**
     * 最初の行を取得する。
     * @return 最初の行
     */
    public DecodedContent get1stLine(){
        return this.lineList.get(0);
    }

    /**
     * 1行li要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public void dumpLines(Writer writer) throws IOException{
        DecodedContent lastLine = null;
        DecodedContent lastContent = null;

        for(DecodedContent content : this.lineList){
            lastContent = content;
            if(content == BREAK){
                if(lastLine != null){
                    writer.append("</li>\n");
                    lastLine = null;
                }else{
                    writer.append("<li/>\n");
                }
            }else{
                if(lastLine == null){
                    writer.append("<li>");
                }
                XmlUtils.dumpDecodedContent(writer, content);
                lastLine = content;
            }
        }

        if(lastLine != null){
            writer.append("</li>\n");
        }else if(lastContent == BREAK){
            writer.append("<li/>\n");
        }

        return;
    }

    /**
     * 要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public abstract void dumpXml(Writer writer) throws IOException;

}
