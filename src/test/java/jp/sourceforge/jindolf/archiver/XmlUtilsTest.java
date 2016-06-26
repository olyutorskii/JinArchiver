/*
 */

package jp.sourceforge.jindolf.archiver;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Validator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class XmlUtilsTest {

    public XmlUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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
