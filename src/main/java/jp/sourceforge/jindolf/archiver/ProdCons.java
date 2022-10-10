/*
 * producer-consumer tasks manager
 *
 * License : The MIT License
 * Copyright(c) 2016 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Producer-Consumer モデルでのタスク管理。
 * 生産者タスクと消費者タスク間での異常系のリカバリを適切に行う。
 */
public class ProdCons{

    private static final int NUM_THREADS = 2;


    private final CompletionService<Void> service;

    private final DumpXmlTask dumpTask;
    private final ValidateTask validateTask;

    private Future<Void> dumpFuture;
    private Future<Void> validFuture;

    private boolean endValidator1st;

    private Throwable cause = null;
    private Callable<Void> causeTask = null;


    /**
     * コンストラクタ。
     * @param dumpTask XML出力タスク
     * @param validateTask XML検証タスク
     */
    public ProdCons(DumpXmlTask dumpTask, ValidateTask validateTask){
        super();
        this.service = createService();
        this.dumpTask = dumpTask;
        this.validateTask = validateTask;
        return;
    }


    /**
     * 2スレッド専用サービスを生成する。
     * @return サービス
     */
    private static CompletionService<Void> createService(){
        Executor executor = Executors.newFixedThreadPool(NUM_THREADS);
        CompletionService<Void> service =
                new ExecutorCompletionService<>(executor);
        return service;
    }

    /**
     * 終了タスクの失敗原因を得る。
     * @param future タスク
     * @return 失敗原因
     * @throws IllegalStateException 終了していない。
     */
    private static Throwable getReason(Future<?> future)
            throws IllegalStateException{
        if(! future.isDone()) throw new IllegalStateException();

        Throwable result = null;

        try{
            Object dummyNull = future.get();
            assert dummyNull == null;
        }catch(InterruptedException e){
            assert false;
            throw new IllegalStateException(e);
        }catch(ExecutionException e){
            result = e.getCause();
        }

        return result;
    }


    /**
     * タスク投入。
     * @throws InterruptedException 割り込まれた。※ありえない
     */
    public void submit() throws InterruptedException{
        // consumer first
        this.validFuture = this.service.submit(this.validateTask);
        this.dumpFuture  = this.service.submit(this.dumpTask);

        waitTask1st();
        waitTask2nd();

        return;
    }

    /**
     * 最初のタスク完了を待つ。
     * @throws InterruptedException 割り込まれた
     */
    private void waitTask1st() throws InterruptedException{
        Future<Void> future1st;
        future1st = this.service.take();

        if(future1st == this.validFuture) this.endValidator1st = true;
        else                              this.endValidator1st = false;

        this.cause = getReason(future1st);
        if(this.cause != null){
            if(this.endValidator1st) this.causeTask = this.validateTask;
            else                     this.causeTask = this.dumpTask;
        }

        return;
    }

    /**
     * 最後のタスク完了を待つ。
     * 最初のタスクで既に異常系が進行している場合、
     * 最後のタスクはキャンセルされる。
     * @throws InterruptedException 割り込まれた
     */
    private void waitTask2nd() throws InterruptedException{
        if(this.cause != null){
            cancel2ndTask();
            return;
        }

        Future<?> future2nd = this.service.take();

        this.cause = getReason(future2nd);
        if(this.cause != null){
            if(this.endValidator1st) this.causeTask = this.dumpTask;
            else                     this.causeTask = this.validateTask;
        }

        return;
    }

    /**
     * 最後のタスクをキャンセルする。
     * 最初のタスクの異常系は確定しているため、
     * 最後のタスクの異常系は無視される。
     * @throws InterruptedException 割り込まれた
     */
    private void cancel2ndTask() throws InterruptedException{
        Future<Void> future2nd;
        if(this.endValidator1st) future2nd = this.dumpFuture;
        else                     future2nd = this.validFuture;

        boolean useInterrupt = true;
        future2nd.cancel(useInterrupt);

        Future<Void> future = this.service.take();
        assert future == future2nd;

        // ignore error with cancel

        return;
    }

    /**
     * 異常系が発生しているか判定する。
     * @return 異常系ならtrue
     */
    public boolean hasError(){
        boolean result;
        result = this.cause != null;
        return result;
    }

    /**
     * 異常系を引き起こした例外を返す。
     * @return 例外。無ければnull
     */
    public Throwable getCause(){
        return this.cause;
    }

    /**
     * エラー説明文を返す。
     * @return 説明文。無ければnull
     */
    public String getErrDescription(){
        String desc;

        if(this.causeTask == this.validateTask){
            desc = ValidateTask.getErrDescription(this.cause);
        }else if(this.causeTask == this.dumpTask){
            desc = DumpXmlTask.getErrDescription(this.cause);
        }else{
            desc = null;
        }

        return desc;
    }

}
