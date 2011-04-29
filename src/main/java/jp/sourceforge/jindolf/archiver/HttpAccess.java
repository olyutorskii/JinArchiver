/*
 * downloader
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import jp.sourceforge.jindolf.corelib.LandDef;
import jp.sourceforge.jindolf.corelib.LandState;
import jp.sourceforge.jindolf.corelib.PeriodType;
import jp.sourceforge.jindolf.parser.DecodeException;
import jp.sourceforge.jindolf.parser.DecodedContent;
import jp.sourceforge.jindolf.parser.HtmlAdapter;
import jp.sourceforge.jindolf.parser.HtmlParseException;
import jp.sourceforge.jindolf.parser.HtmlParser;
import jp.sourceforge.jindolf.parser.PageType;
import jp.sourceforge.jindolf.parser.SeqRange;

/**
 * 人狼HTTPサーバ内のリソース情報を展開する。
 */
public final class HttpAccess{

    /**
     * 隠しコンストラクタ。
     */
    private HttpAccess(){
        throw new Error();
    }


    /**
     * 日一覧ページ(エピローグの翌日)のURLを得る。
     * @param landDef 国指定
     * @param vid 村番号
     * @return 一覧ページへのURL
     * @throws IOException 入力エラー
     */
    public static URL getPeriodListURL(LandDef landDef, int vid)
            throws IOException{
        StringBuilder urlText = new StringBuilder();

        urlText.append(landDef.getCgiURI().toASCIIString());
        urlText.append('?').append("vid=").append(vid);
        if(landDef.getLandState() == LandState.ACTIVE){
            urlText.append('&').append("meslog=");
        }

        URL result = new URL(urlText.toString());

        return result;
    }

    /**
     * 日ページのロード元情報一覧を得る。
     * @param landDef 国指定
     * @param vid 村番号
     * @return ロード元情報一覧
     * @throws DecodeException デコードエラー
     * @throws HtmlParseException パースエラー
     * @throws IOException 入力エラー
     */
    public static List<PeriodResource> loadResourceList(LandDef landDef,
                                                          int vid)
            throws DecodeException,
                   HtmlParseException,
                   IOException {
        URL url = getPeriodListURL(landDef, vid);

        Charset charset = landDef.getEncoding();
        InputStream istream = url.openStream();
        DecodedContent content = Builder.contentFromStream(charset, istream);
        istream.close();

        HtmlParser parser = new HtmlParser();
        PeriodListHandler handler = new PeriodListHandler(landDef, vid);
        parser.setBasicHandler(handler);
        parser.setTalkHandler(handler);
        parser.setSysEventHandler(handler);
        parser.parseAutomatic(content);

        List<PeriodResource> result = handler.getResourceList();

        return result;
    }

    /**
     * 日一覧パース用ハンドラ。
     */
    public static class PeriodListHandler extends HtmlAdapter{

        private final LandDef landDef;
        private final int vid;

        private List<PeriodResource> resourceList = null;

        private int progressDays;
        private boolean hasDone;

        /**
         * コンストラクタ。
         * @param landDef 国指定
         * @param vid 村番号
         */
        public PeriodListHandler(LandDef landDef, int vid){
            super();
            this.landDef = landDef;
            this.vid = vid;
            return;
        }

        /**
         * 日ページのURL文字列を生成する。
         * @param type 日種類
         * @param day 日にち
         * @return URL文字列
         */
        public String getURL(PeriodType type, int day){
            String base = this.landDef.getCgiURI().toASCIIString();
            base += "?vid=" + this.vid;

            if(this.landDef.getLandId().equals("wolfg")){
                base += "&meslog=";
                String dnum = "000" + (day - 1);
                dnum = dnum.substring(dnum.length() - 3);
                switch(type){
                case PROLOGUE:
                    base += "000_ready";
                    break;
                case PROGRESS:
                    base += dnum;
                    base += "_progress";
                    break;
                case EPILOGUE:
                    base += dnum;
                    base += "_party";
                    break;
                default:
                    assert false;
                    return null;
                }
            }else{
                base += "&meslog=" + this.vid + "_";
                switch(type){
                case PROLOGUE:
                    base += "ready_0";
                    break;
                case PROGRESS:
                    base += "progress_" + (day - 1);
                    break;
                case EPILOGUE:
                    base += "party_" + (day - 1);
                    break;
                default:
                    return null;
                }
            }

            base += "&mes=all";

            return base;
        }

        /**
         * PeriodResource一覧を得る。
         * @return PeriodResource一覧
         */
        public List<PeriodResource> getResourceList(){
            return this.resourceList;
        }

        /**
         * {@inheritDoc}
         * @param content {@inheritDoc}
         * @throws HtmlParseException {@inheritDoc}
         */
        @Override
        public void startParse(DecodedContent content)
                throws HtmlParseException{
            this.resourceList = new LinkedList<PeriodResource>();
            this.progressDays = 0;
            this.hasDone = false;
            return;
        }

        /**
         * {@inheritDoc}
         * @param type {@inheritDoc}
         * @throws HtmlParseException {@inheritDoc}
         */
        @Override
        public void pageType(PageType type) throws HtmlParseException{
            if(type != PageType.PERIOD_PAGE) throw new HtmlParseException();
            return;
        }

        /**
         * {@inheritDoc}
         * @param content {@inheritDoc}
         * @param anchorRange {@inheritDoc}
         * @param periodType {@inheritDoc}
         * @param day {@inheritDoc}
         * @throws HtmlParseException {@inheritDoc}
         */
        @Override
        public void periodLink(DecodedContent content,
                                SeqRange anchorRange,
                                PeriodType periodType,
                                int day )
                throws HtmlParseException{
            if(periodType == null){
                this.hasDone = true;
            }else if(periodType == PeriodType.PROGRESS){
                this.progressDays = day;
            }
            return;
        }

        /**
         * {@inheritDoc}
         * @throws HtmlParseException {@inheritDoc}
         */
        @Override
        public void endParse() throws HtmlParseException{
            if( ! this.hasDone ) throw new HtmlParseException();

            PeriodResource resource;

            String prologueURI = getURL(PeriodType.PROLOGUE, 0);
            resource = new PeriodResource(this.landDef,
                                          this.vid,
                                          PeriodType.PROLOGUE,
                                          0,
                                          prologueURI,
                                          0L,
                                          null);
            this.resourceList.add(resource);

            for(int day = 1; day <= this.progressDays; day++){
                String progressURI = getURL(PeriodType.PROGRESS, day);
                resource = new PeriodResource(this.landDef,
                                              this.vid,
                                              PeriodType.PROGRESS,
                                              day,
                                              progressURI,
                                              0L,
                                              null);
                this.resourceList.add(resource);
            }

            String epilogueURI = getURL(PeriodType.EPILOGUE,
                                        this.progressDays + 1);
            resource = new PeriodResource(this.landDef,
                                          this.vid,
                                          PeriodType.EPILOGUE,
                                          this.progressDays + 1,
                                          epilogueURI,
                                          0L,
                                          null);
            this.resourceList.add(resource);

            return;
        }

    }

}
