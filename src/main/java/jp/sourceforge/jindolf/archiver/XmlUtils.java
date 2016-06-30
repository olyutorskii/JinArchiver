/*
 * XML utils
 *
 * License : The MIT License
 * Copyright(c) 2008 olyutorskii
 */

package jp.sourceforge.jindolf.archiver;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/**
 * XML用各種ユーティリティ。
 */
public final class XmlUtils{

    /**
     * 隠れコンストラクタ。
     */
    private XmlUtils(){
        assert false;
        throw new AssertionError();
    }


    /**
     * XML読み込み用DocumentBuilderを生成する。
     * @return DocumentBuilder
     * @throws ParserConfigurationException 実装が要求に応えられない。
     */
    public static DocumentBuilder createDocumentBuilder()
            throws ParserConfigurationException {
        DocumentBuilderFactory factory;
        factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();

        return builder;
    }

    /**
     * バリデータを生成する。
     * @return バリデータ
     * @throws SAXException 実装が要求に応えられない。
     */
    public static Validator createValidator() throws SAXException{
        SchemaFactory factory;
        String nsuri = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        factory = SchemaFactory.newInstance(nsuri);

        Schema schema;
        schema = factory.newSchema();

        Validator validator = schema.newValidator();

        LSResourceResolver resolver = new XmlResolver();
        validator.setResourceResolver(resolver);

        return validator;
    }

}
