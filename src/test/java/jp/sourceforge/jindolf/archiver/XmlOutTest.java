/*
 */

package jp.sourceforge.jindolf.archiver;

import java.io.StringWriter;
import java.io.Writer;
import jp.sourceforge.jindolf.parser.DecodeErrorInfo;
import jp.sourceforge.jindolf.parser.DecodedContent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class XmlOutTest {

    public XmlOutTest() {
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
     * Test of isWhiteSpace method, of class XmlUtils.
     */
    @Test
    public void testIsWhiteSpace() {
        System.out.println("isWhiteSpace");

        assertTrue(XmlOut.isWhiteSpace('\u0020'));
        assertTrue(XmlOut.isWhiteSpace('\t'));
        assertTrue(XmlOut.isWhiteSpace('\n'));
        assertTrue(XmlOut.isWhiteSpace('\r'));

        assertFalse(XmlOut.isWhiteSpace('\u0000'));
        assertFalse(XmlOut.isWhiteSpace('\u3000'));
        assertFalse(XmlOut.isWhiteSpace('A'));
        assertFalse(XmlOut.isWhiteSpace('亜'));

        return;
    }

    /**
     * Test of dumpDocType method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testDumpDocType() throws Exception {
        System.out.println("dumpDocType");
        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dumpDocType();
        xmlOut.close();

        assertEquals("<!DOCTYPE village SYSTEM \"http://jindolf.sourceforge.jp/xml/dtd/bbsArchive-110421.dtd\" >", writer.toString());

        return;
    }

    /**
     * Test of dumpNameSpaceDecl method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testDumpNameSpaceDecl() throws Exception {
        System.out.println("dumpNameSpaceDecl");
        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dumpNameSpaceDecl();
        xmlOut.close();

        assertEquals("xmlns=\"http://jindolf.sourceforge.jp/xml/ns/501\"", writer.toString());

        return;
    }

    /**
     * Test of dumpSiNameSpaceDecl method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testDumpSiNameSpaceDecl() throws Exception {
        System.out.println("dumpSiNameSpaceDecl");
        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dumpSiNameSpaceDecl();
        xmlOut.close();

        assertEquals("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", writer.toString());

        return;
    }

    /**
     * Test of dumpSchemeLocation method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testDumpSchemeLocation() throws Exception {
        System.out.println("dumpSchemeLocation");
        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dumpSchemeLocation();
        xmlOut.close();

        assertEquals("xsi:schemaLocation=\"http://jindolf.sourceforge.jp/xml/ns/501 http://jindolf.sourceforge.jp/xml/xsd/bbsArchive-110421.xsd\"", writer.toString());

        return;
    }

    /**
     * Test of indent method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testIndent() throws Exception {
        System.out.println("indent");
        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.indent(1);
        xmlOut.close();
        assertEquals("  ", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.indent(2);
        xmlOut.close();
        assertEquals("    ", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.indent(0);
        xmlOut.close();
        assertEquals("", writer.toString());

        return;
    }

    /**
     * Test of charRefOut method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testCharRefOut() throws Exception {
        System.out.println("charRefOut");

        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charRefOut('\u0020');
        xmlOut.close();
        assertEquals("&#x20;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charRefOut('\u0009');
        xmlOut.close();
        assertEquals("&#x09;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charRefOut('\u0000');
        xmlOut.close();
        assertEquals("&#x00;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charRefOut('\u0001');
        xmlOut.close();
        assertEquals("&#x01;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charRefOut('\u00ff');
        xmlOut.close();
        assertEquals("&#xff;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charRefOut('\u0100');
        xmlOut.close();
        assertEquals("&#x0100;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charRefOut('\u1000');
        xmlOut.close();
        assertEquals("&#x1000;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charRefOut('\ud800');
        xmlOut.close();
        assertEquals("&#xd800;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charRefOut('亜');
        xmlOut.close();
        assertEquals("&#x4e9c;", writer.toString());

        return;
    }

    /**
     * Test of dumpRawData method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testDumpRawData() throws Exception {
        System.out.println("dumpInvalidChar");

        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dumpRawData('\u0000');
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"00\" >\u2400</rawdata>", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dumpRawData('\u001f');
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"1f\" >\u241f</rawdata>", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dumpRawData('\u0020');
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"20\" >\ufffd</rawdata>", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dumpRawData('\u00ff');
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"ff\" >\ufffd</rawdata>", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dumpRawData('\u0100');
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"00\" >\ufffd</rawdata>", writer.toString());

        return;
    }

    /**
     * Test of charDataOut method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testCharDataOut() throws Exception {
        System.out.println("textOut");

        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("ABC");
        xmlOut.close();
        assertEquals("ABC", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("&<>\"'");
        xmlOut.close();
        assertEquals("&amp;&lt;&gt;&quot;&apos;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u0008B");
        xmlOut.close();
        assertEquals("A<rawdata encoding=\"Shift_JIS\" hexBin=\"08\" >\u2408</rawdata>B", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u007fB");
        xmlOut.close();
        assertEquals("A\u007fB", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u009fB");
        xmlOut.close();
        assertEquals("A\u009fB", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u00a0B");
        xmlOut.close();
        assertEquals("A\u00a0B", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\\B");
        xmlOut.close();
        assertEquals("A¥B", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A~B");
        xmlOut.close();
        assertEquals("A‾B", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u0020\u0020");
        xmlOut.close();
        assertEquals("A &#x20;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u0020");
        xmlOut.close();
        assertEquals("A&#x20;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u0020B");
        xmlOut.close();
        assertEquals("A B", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u0020");
        xmlOut.close();
        assertEquals("A&#x20;", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u0020\u0020B");
        xmlOut.close();
        assertEquals("A &#x20;B", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("\u0020\u0020B");
        xmlOut.close();
        assertEquals("&#x20;&#x20;B", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("A\u0020\u0020\u0020B");
        xmlOut.close();
        assertEquals("A &#x20;&#x20;B", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.charDataOut("\u0020\u0020\u0020B");
        xmlOut.close();
        assertEquals("&#x20;&#x20;&#x20;B", writer.toString());

        return;
    }

    /**
     * Test of attrOut method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testAttrOut() throws Exception {
        System.out.println("attrOut");

        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.attrOut("A", "B");
        xmlOut.close();
        assertEquals("A=\"B\"", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.attrOut("A", "B\u0008C");
        xmlOut.close();
        assertEquals("A=\"B\u0008C\"", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.attrOut("A", "B\nC\rD\tE");
        xmlOut.close();
        assertEquals("A=\"B\nC\rD\tE\"", writer.toString());

        return;
    }

    /**
     * Test of timeAttrOut method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testTimeAttrOut() throws Exception {
        System.out.println("timeAttrOut");

        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.timeAttrOut("A", 0, 0);
        xmlOut.close();
        assertEquals("A=\"00:00:00+09:00\"", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.timeAttrOut("A", 9, 9);
        xmlOut.close();
        assertEquals("A=\"09:09:00+09:00\"", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.timeAttrOut("A", 23, 59);
        xmlOut.close();
        assertEquals("A=\"23:59:00+09:00\"", writer.toString());

        return;
    }

    /**
     * Test of dateAttrOut method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testDateAttrOut() throws Exception {
        System.out.println("dateAttrOut");

        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dateAttrOut("A", 1, 1);
        xmlOut.close();
        assertEquals("A=\"--01-01+09:00\"", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dateAttrOut("A", 2, 29);
        xmlOut.close();
        assertEquals("A=\"--02-29+09:00\"", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dateAttrOut("A", 12, 31);
        xmlOut.close();
        assertEquals("A=\"--12-31+09:00\"", writer.toString());

        return;
    }

    /**
     * Test of dateTimeAttr method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testDateTimeAttr() throws Exception {
        System.out.println("dateTimeAttr");

        Writer writer;
        XmlOut xmlOut;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dateTimeAttr("A", 0L);
        xmlOut.close();
        assertEquals("A=\"1970-01-01T09:00:00.000+09:00\"", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        xmlOut.dateTimeAttr("A", 1466871486000L);
        xmlOut.close();
        assertEquals("A=\"2016-06-26T01:18:06.000+09:00\"", writer.toString());

        return;
    }

    /**
     * Test of dumpErrorInfo method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testDumpErrorInfo() throws Exception {
        System.out.println("dumpErrorInfo");

        Writer writer;
        XmlOut xmlOut;
        DecodeErrorInfo errorInfo;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        errorInfo = new DecodeErrorInfo(0, (byte) 0x00);
        xmlOut.dumpErrorInfo(errorInfo);
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"00\" >\ufffd</rawdata>", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        errorInfo = new DecodeErrorInfo(0, (byte) 0x0f);
        xmlOut.dumpErrorInfo(errorInfo);
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"0f\" >\ufffd</rawdata>", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        errorInfo = new DecodeErrorInfo(0, (byte) 0x10);
        xmlOut.dumpErrorInfo(errorInfo);
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"10\" >\ufffd</rawdata>", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        errorInfo = new DecodeErrorInfo(0, (byte) 0xff);
        xmlOut.dumpErrorInfo(errorInfo);
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"ff\" >\ufffd</rawdata>", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        errorInfo = new DecodeErrorInfo(0, (byte) 0xee, (byte) 0xef); //92区
        xmlOut.dumpErrorInfo(errorInfo);
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"eeef\" >\u2170</rawdata>", writer.toString());

        return;
    }

    /**
     * Test of dumpDecodedContent method, of class XmlUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testDumpDecodedContent() throws Exception {
        System.out.println("dumpDecodedContent");

        Writer writer;
        XmlOut xmlOut;
        DecodedContent content;

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        content = new DecodedContent("ABC");
        xmlOut.dumpDecodedContent(content);
        xmlOut.close();
        assertEquals("ABC", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        content = new DecodedContent();
        content.append("AB");
        content.addDecodeError((byte)0x08);
        content.append("CD");
        xmlOut.dumpDecodedContent(content);
        xmlOut.close();
        assertEquals("AB<rawdata encoding=\"Shift_JIS\" hexBin=\"08\" >\ufffd</rawdata>CD", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        content = new DecodedContent();
        content.append("AB");
        content.addDecodeError((byte)0x08);
        xmlOut.dumpDecodedContent(content);
        xmlOut.close();
        assertEquals("AB<rawdata encoding=\"Shift_JIS\" hexBin=\"08\" >\ufffd</rawdata>", writer.toString());

        writer = new StringWriter();
        xmlOut = new XmlOut(writer);
        content = new DecodedContent();
        content.addDecodeError((byte)0x08);
        content.append("CD");
        xmlOut.dumpDecodedContent(content);
        xmlOut.close();
        assertEquals("<rawdata encoding=\"Shift_JIS\" hexBin=\"08\" >\ufffd</rawdata>CD", writer.toString());

        return;
    }

}
