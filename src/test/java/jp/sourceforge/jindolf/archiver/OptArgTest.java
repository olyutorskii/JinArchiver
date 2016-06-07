/*
 */

package jp.sourceforge.jindolf.archiver;

import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class OptArgTest {

    public OptArgTest() {
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
     * Test of values method, of class OptArg.
     */
    @Test
    public void testValues() {
        System.out.println("values");

        OptArg[] values;
        values = OptArg.values();

        assertEquals(5, values.length);

        int pos;
        pos = 0;
        assertEquals(OptArg.OPT_HELP,   values[pos++]);
        assertEquals(OptArg.OPT_LAND,   values[pos++]);
        assertEquals(OptArg.OPT_VID,    values[pos++]);
        assertEquals(OptArg.OPT_OUTDIR, values[pos++]);
        assertEquals(OptArg.OPT_STDOUT, values[pos++]);

        return;
    }

    /**
     * Test of valueOf method, of class OptArg.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        return;
    }

    /**
     * Test of parseOptArg method, of class OptArg.
     */
    @Test
    public void testParseOptArg() {
        System.out.println("parseOptArg");

        String arg;
        OptArg optArg;

        arg = null;
        optArg = OptArg.parseOptArg(arg);
        assertNull(optArg);

        arg = "";
        optArg = OptArg.parseOptArg(arg);
        assertNull(optArg);

        arg = "XXX";
        optArg = OptArg.parseOptArg(arg);
        assertNull(optArg);

        arg = "-";
        optArg = OptArg.parseOptArg(arg);
        assertNull(optArg);

        arg = "-h";
        optArg = OptArg.parseOptArg(arg);
        assertEquals(OptArg.OPT_HELP, optArg);

        arg = "-help";
        optArg = OptArg.parseOptArg(arg);
        assertEquals(OptArg.OPT_HELP, optArg);

        arg = "-?";
        optArg = OptArg.parseOptArg(arg);
        assertEquals(OptArg.OPT_HELP, optArg);

        arg = "-land";
        optArg = OptArg.parseOptArg(arg);
        assertEquals(OptArg.OPT_LAND, optArg);

        arg = "-vid";
        optArg = OptArg.parseOptArg(arg);
        assertEquals(OptArg.OPT_VID, optArg);

        arg = "-outdir";
        optArg = OptArg.parseOptArg(arg);
        assertEquals(OptArg.OPT_OUTDIR, optArg);

        arg = "-stdout";
        optArg = OptArg.parseOptArg(arg);
        assertEquals(OptArg.OPT_STDOUT, optArg);

        return;
    }

    /**
     * Test of getHelpMessage method, of class OptArg.
     */
    @Test
    public void testGetHelpMessage() {
        System.out.println("getHelpMessage");

        String generator = JinArchiver.GENERATOR;
        String catalog = LandUtils.getLandIdCatalog();

        String center =
          " 人狼BBS アーカイブ作成ツール\n\n"
        + "-h, -help, -?\n\tヘルプメッセージ\n"
        + "-land 国識別子\n"
        + "-vid 村番号\n"
        + "-outdir 出力ディレクトリ\n"
        + "-stdout\n\t標準出力へ出力\n\n"
        + "※ -outdir と -stdout は排他指定\n\n"
        + "利用可能な国識別子は ";

        String expResult;
        String result;

        expResult = "\n" + generator + center + catalog + "\n";
        result = OptArg.getHelpMessage(generator);
        assertEquals(expResult, result);

        expResult = "\n" + center + catalog + "\n";
        result = OptArg.getHelpMessage(null);
        assertEquals(expResult, result);

        return;
    }

    /**
     * Test of getArgList method, of class OptArg.
     */
    @Test
    public void testGetArgList() {
        System.out.println("getArgList");

        List<String> result;

        result = OptArg.OPT_HELP.getArgList();
        assertEquals(3, result.size());
        assertTrue(result.contains("-h"));
        assertTrue(result.contains("-help"));
        assertTrue(result.contains("-?"));

        result = OptArg.OPT_LAND.getArgList();
        assertEquals(1, result.size());
        assertTrue(result.contains("-land"));

        result = OptArg.OPT_VID.getArgList();
        assertEquals(1, result.size());
        assertTrue(result.contains("-vid"));

        result = OptArg.OPT_STDOUT.getArgList();
        assertEquals(1, result.size());
        assertTrue(result.contains("-stdout"));

        result = OptArg.OPT_OUTDIR.getArgList();
        assertEquals(1, result.size());
        assertTrue(result.contains("-outdir"));

        return;
    }

    /**
     * Test of iterator method, of class OptArg.
     */
    @Test
    public void testIterator() {
        System.out.println("iterator");

        Iterator<String> it;

        it = OptArg.OPT_HELP.iterator();
        assertEquals("-h", it.next());
        assertEquals("-help", it.next());
        assertEquals("-?", it.next());
        assertFalse(it.hasNext());

        return;
    }

}
