/*
 * ZIP utils
 *
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import jp.sourceforge.jindolf.parser.DecodeException;
import jp.sourceforge.jindolf.parser.HtmlParseException;

/**
 * ZIPアーカイブされた生XHTML情報へのアクセス諸々。
 */
public final class ZipUtils{

    /**
     * ZIPファイルに格納された村一覧を抽出する。
     * 各日のロードはまだ行われない。
     * @param zipFile ZIPファイル
     * @return 村一覧
     * @throws IOException 入力エラー
     */
    public static List<VillageData> getVillageDataList(ZipFile zipFile)
            throws IOException{
        List<VillageData> result = new LinkedList<VillageData>();

        List<ZipEntry> logList = getDownloadLogList(zipFile);
        for(ZipEntry entry : logList){
            VillageData villageData =
                getVillageDataFromLogEntry(zipFile, entry);
            result.add(villageData);
        }

        return result;
    }

    /**
     * ZIPファイルからダウンロードログファイルの一覧を取得する。
     * ダウンロードログファイルは必ず「download.log」の名前を持つ。
     * @param file ZIPファイル
     * @return ログファイル一覧
     */
    public static List<ZipEntry> getDownloadLogList(ZipFile file){
        List<ZipEntry> result = new LinkedList<ZipEntry>();

        Enumeration<? extends ZipEntry> list = file.entries();
        while(list.hasMoreElements()){
            ZipEntry entry = list.nextElement();
            String name = entry.getName();
            if(name.endsWith("/download.log" )){
                result.add(entry);
            }
        }

        return result;
    }

    /**
     * ログファイルを表すZIPエントリから村情報を抽出する。
     * @param zipFile ZIPファイル
     * @param logEntry ログファイルのZIPエントリ
     * @return 村情報
     * @throws IOException 入力エラー
     */
    public static VillageData getVillageDataFromLogEntry(
            ZipFile zipFile, ZipEntry logEntry)
            throws IOException{
        InputStream istream = zipFile.getInputStream(logEntry);
        List<PeriodResource> list =
                FileArchive.parseDownloadLog(istream);
        istream.close();

        String baseName = logEntry.getName().replaceAll("/[^/]+$", "/");
        for(PeriodResource resource : list){
            modifyResourceUrl(zipFile, baseName, resource);
        }

        VillageData villageData = new VillageData(list);
        return villageData;
    }

    /**
     * ログ記録に書かれたXHTMLファイル名を実際にアクセス可能なURLに修正する。
     * @param zipFile ZIPファイル
     * @param entryBase ファイル名のベース
     * @param resource リソース情報
     * @return 引数と同じ物
     */
    public static PeriodResource modifyResourceUrl(ZipFile zipFile,
                                           String entryBase,
                                           PeriodResource resource ){
        String fileName;
        try{
            URL resUrl = resource.getResourceUrl();
            URI resUri = resUrl.toURI();
            File file = new File(resUri);
            fileName = file.getName();
        }catch(URISyntaxException e){
            throw new IllegalArgumentException(e);
        }

        String zipUri = new File(zipFile.getName()).toURI().toString();

        String urlText = "jar:" + zipUri + "!/" + entryBase + fileName;

        URL zipResource;
        try{
            zipResource = new URL(urlText);
        }catch(MalformedURLException e){
            throw new IllegalArgumentException(e);
        }

        resource.setResourceUrl(zipResource);

        return resource;
    }

    /**
     * 村番号から村情報を得る。
     * @param zipFile ZIPファイル
     * @param vid 村番号
     * @return 村情報
     * @throws IOException 入力エラー
     */
    public static VillageData getVillageData(ZipFile zipFile, int vid)
            throws IOException{
        ZipEntry entry = getDownloadLogEntry(zipFile, vid);
        VillageData result = getVillageDataFromLogEntry(zipFile, entry);
        return result;
    }

    /**
     * 村番号から該当するログファイルエントリを取得する。
     * @param file ZIPファイル
     * @param vid 村番号
     * @return ログファイルのZIPエントリ。見つからなければnull。
     */
    public static ZipEntry getDownloadLogEntry(ZipFile file, int vid){
        Pattern entryPattern =
                Pattern.compile("jin_[^_]+_([0-9]+)/download.log$");

        Enumeration<? extends ZipEntry> list = file.entries();
        while(list.hasMoreElements()){
            ZipEntry entry = list.nextElement();
            String name = entry.getName();
            Matcher matcher = entryPattern.matcher(name);
            if(matcher.find()){
                String vnum = matcher.group(1);
                if(vid == Integer.parseInt(vnum)) return entry;
            }
        }
        return null;
    }

    /**
     * ZIPファイル中の指定した村番号の村をXML出力する。
     * @param zipFile ZIPファイル
     * @param vid 村番号
     * @param writer 出力先
     * @throws IOException 入出力エラー
     * @throws DecodeException デコードエラー
     * @throws HtmlParseException パースエラー
     */
    public static void dumpZipVid(ZipFile zipFile, int vid, Writer writer)
            throws IOException, DecodeException, HtmlParseException{
        VillageData data = getVillageData(zipFile, vid);
        Builder.fillVillageData(data);
        XmlUtils.dumpVillageData(writer, data);

        return;
    }

    /**
     * ZIPファイル中の全村をXML出力する。
     * @param zipFile ZIPファイル
     * @throws IOException 入出力エラー
     * @throws DecodeException デコードエラー
     * @throws HtmlParseException パースエラー
     */
    public static void dumpZipAll(ZipFile zipFile)
            throws IOException, DecodeException, HtmlParseException{
        List<VillageData> villageDataList;
        villageDataList = ZipUtils.getVillageDataList(zipFile);
        Iterator<VillageData> it = villageDataList.iterator();
        while(it.hasNext()){
            VillageData villageData = it.next();
            Builder.fillVillageData(villageData);
            Writer writer = XmlUtils.createFileWriter(villageData);
            XmlUtils.dumpVillageData(writer, villageData);
            writer.close();
            it.remove();
        }

        return;
    }

    /**
     * 隠れコンストラクタ。
     */
    private ZipUtils(){
        super();
        return;
    }

}
