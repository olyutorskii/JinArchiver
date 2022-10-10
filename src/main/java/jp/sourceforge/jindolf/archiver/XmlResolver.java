/*
 * XML resource resolver
 *
 * License : The MIT License
 * Copyright(c) 2016 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * XML各種外部リソースの解決。
 */
public class XmlResolver implements LSResourceResolver{

    private static final String RES_XMLXSD =
            "resources/xmldummy.xsd";
    private static final String RES_COREXSD =
            "resources/coreType-090929.xsd";
    private static final String RES_BBSXSD =
            "resources/bbsArchive-110421.xsd";
    private static final String RES_BBSDTD =
            "resources/bbsArchive-110421.dtd";

    private static final String URI_XMLXSD =
            "http://www.w3.org/2001/xml.xsd";
    private static final String URI_COREXSD =
            "http://jindolf.sourceforge.jp/xml/xsd/coreType-090929.xsd";
    private static final String URI_BBSXSD =
            "http://jindolf.sourceforge.jp/xml/xsd/bbsArchive-110421.xsd";
    private static final String URI_BBSDTD =
            "http://jindolf.sourceforge.jp/xml/dtd/bbsArchive-110421.dtd";

    private static final DOMImplementationLS DOM_LS;

    static{
        try{
            DOM_LS = buildDomImplLS();
        }catch(ParserConfigurationException e){
            throw new ExceptionInInitializerError(e);
        }
    }


    private final Map<URI, URI> uriMap;


    /**
     * コンストラクタ。
     */
    public XmlResolver(){
        super();

        this.uriMap = new HashMap<>();

        setUriMap();

        return;
    }


    /**
     * DOMImplementationLS実装を生成する。
     * @return DOMImplementationLS実装
     * @throws ParserConfigurationException XML実装が満たされない
     */
    private static DOMImplementationLS buildDomImplLS()
            throws ParserConfigurationException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImp = builder.getDOMImplementation();

        Object feature = domImp.getFeature("LS", "3.0");
        assert feature instanceof DOMImplementationLS;

        DOMImplementationLS result;
        result = (DOMImplementationLS) feature;

        return result;
    }

    /**
     * 絶対URIと相対URIを合成したURIを返す。
     * 正規化も行われる。
     * @param base 絶対URIでなければならない。nullでもよい。
     * @param relative 絶対URIでもよいがその場合baseは無視される。null可。
     * @return 合成結果のURLオブジェクト。必ず絶対URIになる。
     * @throws java.net.URISyntaxException URIとして変。
     * @throws java.lang.IllegalArgumentException 絶対URIが生成できない。
     */
    public static URI buildBaseRelativeURI(String base, String relative)
            throws URISyntaxException,
                   IllegalArgumentException {
        URI baseURI;
        if(base != null){
            baseURI = new URI(base);
            if( ! baseURI.isAbsolute() ){
                throw new IllegalArgumentException();
            }
        }else{
            baseURI = null;
        }

        URI relativeURI;
        if(relative != null){
            relativeURI = new URI(relative);
        }else{
            relativeURI = URI.create("");
        }

        URI resultURI;
        if(baseURI == null || relativeURI.isAbsolute()){
            resultURI = relativeURI;
        }else{
            resultURI = baseURI.resolve(relativeURI);
        }

        if( ! resultURI.isAbsolute() ){
            throw new IllegalArgumentException();
        }

        resultURI = resultURI.normalize();

        return resultURI;
    }


    /**
     * 置換マップを設定する。
     */
    private void setUriMap(){

        try{
            putMap(URI_BBSDTD,  RES_BBSDTD);
            putMap(URI_BBSXSD,  RES_BBSXSD);
            putMap(URI_COREXSD, RES_COREXSD);
            putMap(URI_XMLXSD,  RES_XMLXSD);
        }catch(URISyntaxException e){
            assert false;
            return;
        }

        return;
    }

    /**
     * 置換マップを設定する。
     * @param uri オリジナルURI
     * @param resource リソース名
     * @throws URISyntaxException URIが変
     */
    private void putMap(String uri, String resource)
            throws URISyntaxException{
        URI orig = new URI(uri);

        Class<?> klass = getClass();
        URL resUrl = klass.getResource(resource);
        URI resUri = resUrl.toURI();

        orig = orig.normalize();
        resUri = resUri.normalize();

        this.uriMap.put(orig, resUri);

        return;
    }

    /**
     * URIを解決する。
     * @param origUri オリジナルURI
     * @return 解決リソースへのURI
     */
    private URI resolveMap(URI origUri){
        URI key = origUri.normalize();
        URI result = this.uriMap.get(key);

        if(result == null) result = origUri;

        return result;
    }

    /**
     * {@inheritDoc}
     * @param type {@inheritDoc}
     * @param namespaceURI {@inheritDoc}
     * @param publicId {@inheritDoc}
     * @param systemId {@inheritDoc}
     * @param baseURI {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public LSInput resolveResource(String type,
                                     String namespaceURI,
                                     String publicId,
                                     String systemId,
                                     String baseURI ){
        URI origUri;
        try{
            origUri = buildBaseRelativeURI(baseURI, systemId);
        }catch(URISyntaxException e){
            assert false;
            return null;
        }

        URI resourceUri = resolveMap(origUri);

        URL resourceUrl;
        try{
            resourceUrl = resourceUri.toURL();
        }catch(MalformedURLException e){
            assert false;
            return null;
        }

        InputStream resourceStream;
        try{
            resourceStream = resourceUrl.openStream();
        }catch(IOException e){
            assert false;
            return null;
        }

        LSInput result = DOM_LS.createLSInput();

        result.setBaseURI(baseURI);
        result.setPublicId(publicId);
        result.setSystemId(systemId);

        result.setByteStream(resourceStream);

        return result;
    }

}
