/*
 * file archive utilities
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.sourceforge.jindolf.corelib.LandDef;
import jp.sourceforge.jindolf.corelib.PeriodType;

/**
 * ファイルシステム上に納められた
 * 生XHTMLデータおよびログファイルへのアクセス諸々。
 */
public final class FileArchive{

    private static final Pattern LINE_PATTERN;
    private static final DateFormat ISO_FORMAT;

    static{
        String fnameRegex =
                 "(jin_([^_]+)_(\\d+)_(\\d+)_"
                +"(?:(prologue)|(progress)|(epilogue))"
                +"\\.html)";
        LINE_PATTERN = Pattern.compile(
            "^" + fnameRegex + "\\s+(\\S+)\\s+(\\S+)\\s+(\\d)" + "$");

        ISO_FORMAT =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.JAPAN);
    }


    /**
     * 隠れコンストラクタ。
     */
    private FileArchive(){
        throw new Error();
    }


    /**
     * ISO形式の日付時刻情報をパースする。
     * @param text 日付表記
     * @return エポック秒(ms)
     */
    public static long parseISODate(String text){
        Date date;
        try{
            synchronized(ISO_FORMAT){
                date = ISO_FORMAT.parse(text);
            }
        }catch(ParseException e){
            throw new IllegalArgumentException(e);
        }
        return date.getTime();
    }

    /**
     * ログ記述からリソース情報を生成する。
     * @param logLine 1行に納められたログ記述
     * @return リソース情報
     */
    public static PeriodResource parseDownLogLine(CharSequence logLine){
        PeriodResource result;

        Matcher matcher = LINE_PATTERN.matcher(logLine);
        if( ! matcher.matches() ) throw new IllegalArgumentException();

        String fname = matcher.group(1);
        String landId = matcher.group(2);
        int villageId = Integer.parseInt(matcher.group(3));
        int day = Integer.parseInt(matcher.group(4));

        PeriodType periodType;
        if     (matcher.start(5) >= 0) periodType = PeriodType.PROLOGUE;
        else if(matcher.start(6) >= 0) periodType = PeriodType.PROGRESS;
        else if(matcher.start(7) >= 0) periodType = PeriodType.EPILOGUE;
        else throw new IllegalArgumentException();

        String uriText = matcher.group(8);
        String dateText = matcher.group(9);
        int hasError = Integer.parseInt(matcher.group(10));
        if(hasError != 0) throw new IllegalArgumentException();

        long dateMs = parseISODate(dateText);

        if(landId.equals("wolf0")) landId = "wolf";
        if(landId.equals("wolf1")) landId = "wolf0";
        LandDef landDef = JinArchiver.getLandDef(landId);

        File file = new File(fname);
        URI fileUri = file.toURI();
        URL fileUrl;
        try{
            fileUrl = fileUri.toURL();
        }catch(MalformedURLException e){
            throw new IllegalArgumentException(e);
        }

        result = new PeriodResource(landDef,
                                    villageId,
                                    periodType,
                                    day,
                                    uriText,
                                    dateMs,
                                    fileUrl );

        return result;
    }

    /**
     * ログファイルからリソース列を抽出する。
     * @param reader ログファイルの内容
     * @return リソース列
     * @throws IOException 入力エラー
     */
    public static List<PeriodResource> parseDownList(LineNumberReader reader)
            throws IOException{
        List<PeriodResource> result = new LinkedList<PeriodResource>();

        for(;;){
            String line = reader.readLine();
            if(line == null) break;
            PeriodResource info = parseDownLogLine(line);
            if(info == null){
                throw new IllegalArgumentException();
            }
            result.add(info);
        }

        return result;
    }

    /**
     * ログファイルからリソース列を抽出する。
     * @param istream ログファイルの内容
     * @return リソース列
     * @throws IOException 入力エラー
     */
    public static List<PeriodResource> parseDownloadLog(InputStream istream)
            throws IOException{
        Reader reader = new InputStreamReader(istream, "US-ASCII");
        LineNumberReader lineReader = new LineNumberReader(reader);
        List<PeriodResource> result = parseDownList(lineReader);
        lineReader.close();
        return result;
    }

}
