/*
 */

package jp.sourceforge.jindolf.archiver;

import jp.sourceforge.jindolf.corelib.LandDef;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class LandUtilsTest {

    public LandUtilsTest() {
    }

    /**
     * Test of getLandDef method, of class LandUtils.
     */
    @Test
    public void testGetLandDef() {
        System.out.println("getLandDef");

        String landId;
        LandDef landDef;

        landId = "";
        landDef = LandUtils.getLandDef(landId);
        assertNull(landDef);

        landId = "XXX";
        landDef = LandUtils.getLandDef(landId);
        assertNull(landDef);

        landId = "wolf";
        landDef = LandUtils.getLandDef(landId);
        assertNotNull(landDef);
        assertEquals("人狼BBS 古国（旧人狼BBS 2）　過去ログのみ",
                landDef.getDescription());

        landId = "wolfg";
        landDef = LandUtils.getLandDef(landId);
        assertNotNull(landDef);
        assertEquals("人狼BBS G国",
                landDef.getDescription());

        return;
    }

    /**
     * Test of getLandIdCatalog method, of class LandUtils.
     */
    @Test
    public void testGetLandIdCatalog() {
        System.out.println("getLandIdCatalog");

        String result;
        String expResult;

        expResult = "wolf wolf0 wolfa wolfb wolfc wolfd wolfe wolff wolfg";
        result = LandUtils.getLandIdCatalog();
        assertEquals(expResult, result);

        return;
    }

}
