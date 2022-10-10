/*
 * topic data
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import jp.osdn.jindolf.parser.content.DecodedContent;

/**
 * テキスト行の集合。
 */
public abstract class TopicData{

    private static final DecodedContent BREAK = new DecodedContent("\n");

    private final List<DecodedContent> lineList = new LinkedList<>();

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
    public void dumpLines(XmlOut writer) throws IOException{
        DecodedContent lastLine = null;
        DecodedContent lastContent = null;

        for(DecodedContent content : this.lineList){
            lastContent = content;
            if(content == BREAK){
                if(lastLine != null){
                    writer.append("</li>");
                    lastLine = null;
                }else{
                    writer.append("<li/>");
                }
                writer.nl();
            }else{
                if(lastLine == null){
                    writer.append("<li>");
                }
                writer.dumpDecodedContent(content);
                lastLine = content;
            }
        }

        if(lastLine != null){
            writer.append("</li>");
        }else if(lastContent == BREAK){
            writer.append("<li/>");
        }
        writer.nl();

        return;
    }

    /**
     * 要素をXML出力する。
     * @param writer 出力先
     * @throws IOException 出力エラー
     */
    public abstract void dumpXml(XmlOut writer) throws IOException;

}
