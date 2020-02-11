/*
 * main entry
 *
 * License : The MIT License
 * Copyright(c) 2020 olyutorskii
 */

package jp.osdn.jindolf.ar;

/**
 * FQN短縮版メインエントリ。
 */
public final class JinArchiver{

    /**
     * 隠しコンストラクタ。
     */
    private JinArchiver(){
        assert false;
        throw new AssertionError();
    }


    /**
     * スタートアップエントリ。
     *
     * <p>{@link jp.sourceforge.jindolf.archiver.JinArchiver}の
     * 旧エントリへ丸投げする。
     *
     * @param args 引数
     *
     * @see jp.sourceforge.jindolf.archiver.JinArchiver
     */
    public static void main(String[] args){
        jp.sourceforge.jindolf.archiver.JinArchiver.main(args);
        return;
    }

}
