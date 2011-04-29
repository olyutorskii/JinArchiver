/*
 * XML-validation task
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 * XML検証タスク。
 */
public class ValidateTask implements Runnable{

    private static final SchemaFactory FACTORY =
            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    private final Validator validator;
    private final Source source;

    /**
     * コンストラクタ。
     * @param reader 文字入力
     * @throws SAXException 内部エラー
     */
    protected ValidateTask(Reader reader) throws SAXException{
        super();
        Schema schema = FACTORY.newSchema();
        this.validator = schema.newValidator();
        this.source = new StreamSource(reader);
        return;
    }

    /**
     * 検証タスク。
     * {@inheritDoc}
     */
    @Override
    public void run(){
        try{
            this.validator.validate(this.source);
        }catch(Throwable e){
            e.printStackTrace(System.err);
            System.err.println("XML検証に失敗しました。");
            System.exit(1);
        }
        return;
    }

    /**
     * 文字出力を横取りしバックグラウンドで検証を行うWriterを生成する。
     * @param writer 元出力
     * @return 新しい出力
     */
    public static Writer wrapValidator(Writer writer){
        PipedReader reader = new PipedReader();
        Writer pipeWriter;
        try{
            pipeWriter = new PipedWriter(reader);
        }catch(IOException e){
            e.printStackTrace(System.err);
            System.err.println("処理を続行できません。");
            System.exit(1);
            return null;
        }

        MultiPlexer mtplx = new MultiPlexer();
        mtplx.addWriter(writer);
        mtplx.addWriter(pipeWriter);

        Runnable task;
        try{
            task = new ValidateTask(reader);
        }catch(SAXException e){
            e.printStackTrace(System.err);
            System.err.println("処理を続行できません。");
            System.exit(1);
            return null;
        }
        Thread th = new Thread(task);
        th.setDaemon(false);
        th.start();

        return mtplx;
    }

}
