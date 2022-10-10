/*
 * XML-dump task
 *
 * License : The MIT License
 * Copyright(c) 2016 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * XML出力タスク。
 */
public class DumpXmlTask implements Callable<Void> {

    private static final String ERR_IO    = "出力エラーが発生しました";
    private static final String ERR_ABORT = "XML出力が中断されました";


    private final VillageData villageData;
    private final XmlOut writer;


    /**
     * コンストラクタ。
     * @param villageData 村情報
     * @param writer 出力先
     */
    public DumpXmlTask(VillageData villageData, XmlOut writer){
        super();
        this.villageData = villageData;
        this.writer = writer;
        return;
    }


    /**
     * 例外に応じたエラー説明を返す。
     * @param cause 例外
     * @return エラー説明
     */
    public static String getErrDescription(Throwable cause){
        String desc;
        if(cause instanceof IOException) desc = ERR_IO;
        else                             desc = ERR_ABORT;
        return desc;
    }


    /**
     * {@inheritDoc}
     * @return null
     * @throws IOException 出力エラー
     */
    @Override
    public Void call() throws IOException{
        try{
            this.writer.dumpVillageData(this.villageData);
        }finally{
            this.writer.close();
        }

        return null;
    }

}
