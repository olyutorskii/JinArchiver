/*
 * Land Utilities
 *
 * License : The MIT License
 * Copyright(c) 2016 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import jp.sourceforge.jindolf.corelib.LandDef;
import org.xml.sax.SAXException;

/**
 * 国情報の各種アクセス。
 */
public final class LandUtils{

    private static final List<LandDef> LANDDEF_LIST;
    private static final char DELIM_SP = '\u0020';


    static{
        DocumentBuilder builder;
        try{
            builder = XmlUtils.createDocumentBuilder();
        }catch(ParserConfigurationException e){
            throw new ExceptionInInitializerError(e);
        }

        try{
            LANDDEF_LIST = LandDef.buildLandDefList(builder);
        }catch(IOException e){
            throw new ExceptionInInitializerError(e);
        }catch(SAXException e){
            throw new ExceptionInInitializerError(e);
        }
    }


    /**
     * 隠しコンストラクタ。
     */
    private LandUtils(){
        assert false;
        throw new AssertionError();
    }


    /**
     * 国IDから国情報を得る。
     * @param landId 国ID
     * @return 国情報 見つからなければnull
     */
    public static LandDef getLandDef(String landId){
        for(LandDef landDef : LANDDEF_LIST){
            String id = landDef.getLandId();
            if(id.equals(landId)) return landDef;
        }

        return null;
    }

    /**
     * 国識別子一覧文字列を得る。
     * 各識別子は空白記号で区切られる。
     * @return 一覧文字列
     */
    public static String getLandIdCatalog(){
        StringBuilder catalog = new StringBuilder();

        for(LandDef landDef : LANDDEF_LIST){
            if(catalog.length() > 0){
                catalog.append(DELIM_SP);
            }
            String id = landDef.getLandId();
            catalog.append(id);
        }

        String result = catalog.toString();
        return result;
    }

}
