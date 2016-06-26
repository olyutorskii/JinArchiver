/*
 * Snif Writer
 *
 * License : The MIT License
 * Copyright(c) 2016 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * 別スレッドから盗聴可能なWriter。
 */
public class SnifWriter extends Writer{

    private static final int SZ_PIPEREAD = 8 * 1024;


    private final Writer fout;

    private final Writer pout;
    private final Reader reader;


    /**
     * コンストラクタ。
     * @param writer 移譲先Writer
     */
    public SnifWriter(Writer writer){
        super();

        if(writer == null) throw new NullPointerException();
        this.fout = writer;

        PipedWriter pipeOut = new PipedWriter();
        PipedReader pipeIn  = new PipedReader(SZ_PIPEREAD);

        try{
            pipeOut.connect(pipeIn);
        }catch(IOException e){
            // ありえない
            assert false;
        }

        this.pout = pipeOut;
        this.reader = pipeIn;

        return;
    }


    /**
     * 傍受用Readerを返す。
     * @return Reader
     */
    public Reader getSnifReader(){
        return this.reader;
    }

    /**
     * {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void close() throws IOException{
        this.fout.close();
        this.pout.close();
        return;
    }

    /**
     * {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void flush() throws IOException{
        this.fout.flush();
        this.pout.flush();
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
        this.fout.write(cbuf, off, len);
        this.pout.write(cbuf, off, len);
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
    public void write(String str, int off, int len) throws IOException {
        this.fout.write(str, off, len);
        this.pout.write(str, off, len);
        return;
    }

    /**
     * {@inheritDoc}
     * @param c {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void write(int c) throws IOException {
        this.fout.write(c);
        this.pout.write(c);
        return;
    }

}
