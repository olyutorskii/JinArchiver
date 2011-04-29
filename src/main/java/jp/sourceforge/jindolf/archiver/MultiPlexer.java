/*
 * Multiplex Writer
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Writerのマルチプレクサ。
 */
public class MultiPlexer extends Writer{

    private final List<Writer> childs = new LinkedList<Writer>();

    /**
     * コンストラクタ。
     */
    public MultiPlexer(){
        this(null);
        return;
    }

    /**
     * コンストラクタ。
     * @param writer 初期Writer
     */
    public MultiPlexer(Writer writer){
        super();

        if(writer != null){
            this.childs.add(writer);
        }

        return;
    }

    /**
     * Writerを追加する。
     * @param writer 追加するWriter。nullなら無視。
     */
    public void addWriter(Writer writer){
        if(writer == null) return;
        this.childs.add(writer);
        return;
    }

    /**
     * 出力するWriterの一覧を得る。
     * @return Writer一覧。
     */
    public List<Writer> getWriterList(){
        return Collections.unmodifiableList(this.childs);
    }

    /**
     * {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void close() throws IOException{
        for(Writer writer : this.childs){
            writer.close();
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void flush() throws IOException{
        for(Writer writer : this.childs){
            writer.flush();
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @param cbuf {@inheritDoc}
     * @param off {@inheritDoc}
     * @param len {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException{
        for(Writer writer : this.childs){
            writer.write(cbuf, off, len);
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @param csq {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public Writer append(CharSequence csq) throws IOException{
        for(Writer writer : this.childs){
            writer.append(csq);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * @param csq {@inheritDoc}
     * @param start {@inheritDoc}
     * @param end {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public Writer append(CharSequence csq, int start, int end)
            throws IOException{
        for(Writer writer : this.childs){
            writer.append(csq, start, end);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * @param c {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public Writer append(char c) throws IOException{
        for(Writer writer : this.childs){
            writer.append(c);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * @param c {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void write(int c) throws IOException{
        for(Writer writer : this.childs){
            writer.write(c);
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @param cbuf {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void write(char[] cbuf) throws IOException{
        for(Writer writer : this.childs){
            writer.write(cbuf);
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @param str {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void write(String str) throws IOException{
        for(Writer writer : this.childs){
            writer.write(str);
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @param str {@inheritDoc}
     * @param off {@inheritDoc}
     * @param len {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void write(String str, int off, int len) throws IOException{
        for(Writer writer : this.childs){
            writer.write(str, off, len);
        }
        return;
    }

}
