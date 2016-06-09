/*
 * XML-validation task
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Callable;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 * XML検証タスク。
 */
public class ValidateTask implements Callable<Void> {

    private static final String ERR_IO    = "XML入力エラーが発生しました";
    private static final String ERR_XSD   = "XML検証が失敗しました";
    private static final String ERR_ABORT = "XML検証が中断されました";


    private final Reader reader;
    private final Source source;
    private final Validator validator;


    /**
     * コンストラクタ。
     * @param reader 文字入力
     * @param valid バリデータ
     */
    public ValidateTask(Reader reader, Validator valid){
        super();
        this.reader = reader;
        this.source = new StreamSource(this.reader);
        this.validator = valid;
        return;
    }


    /**
     * 例外に応じたエラー説明を返す。
     * @param cause 例外
     * @return エラー説明
     */
    public static String getErrDescription(Throwable cause){
        String desc;
        if     (cause instanceof IOException)  desc = ERR_IO;
        else if(cause instanceof SAXException) desc = ERR_XSD;
        else                                   desc = ERR_ABORT;
        return desc;
    }


    /**
     * XML検証タスク。
     * @return null
     * @throws IOException 入力エラー
     * @throws SAXException 検証エラー
     */
    @Override
    public Void call() throws IOException, SAXException{
        try{
            this.validator.validate(this.source);
        }finally{
            this.reader.close();
        }

        return null;
    }

}
