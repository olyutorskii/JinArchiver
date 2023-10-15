/*
 */

package jp.sourceforge.jindolf.archiver;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class XmlUtilsTest {

    public XmlUtilsTest() {
    }

    /**
     * Test of createDocumentBuilder method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateDocumentBuilder() throws Exception {
        System.out.println("createDocumentBuilder");

        DocumentBuilder result;

        result = XmlUtils.createDocumentBuilder();
        assertNotNull(result);

        return;
    }

    /**
     * Test of createValidator method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateValidator() throws Exception {
        System.out.println("createValidator");

        Validator result;

        result = XmlUtils.createValidator();
        assertNotNull(result);

        return;
    }

}
