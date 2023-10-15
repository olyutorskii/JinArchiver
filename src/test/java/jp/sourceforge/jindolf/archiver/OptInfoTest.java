/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.sourceforge.jindolf.archiver;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class OptInfoTest {

    public OptInfoTest() {
    }

    /**
     * Test of parseOptInfo method, of class OptInfo.
     */
    @Test
    public void testParseOptInfo() {
        System.out.println("parseOptInfo");

        List<String> argList;
        OptInfo result;

        argList = Arrays.asList();
        result = OptInfo.parseOptInfo(argList);
        assertTrue(result.isHelp());
        assertFalse(result.hasError());

        argList = Arrays.asList("X");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertEquals("不正なオプションです。 X", result.getErrMsg());
        assertTrue(result.hasError());

        argList = Arrays.asList("-help#");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertEquals("不正なオプションです。 -help#", result.getErrMsg());

        argList = Arrays.asList("-help");
        result = OptInfo.parseOptInfo(argList);
        assertTrue(result.isHelp());

        argList = Arrays.asList("-h");
        result = OptInfo.parseOptInfo(argList);
        assertTrue(result.isHelp());

        argList = Arrays.asList("-?");
        result = OptInfo.parseOptInfo(argList);
        assertTrue(result.isHelp());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999", "-outdir", "/tmp");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertNull(result.getErrMsg());
        assertEquals("wolfg", result.getLandDef().getLandId());
        assertEquals(999, result.getVid());
        assertEquals("/tmp", result.getOutdir());
        assertFalse(result.isStdout());

        result = OptInfo.parseOptInfo("-land", "wolfg", "-vid", "999", "-outdir", "/tmp");
        assertFalse(result.isHelp());
        assertNull(result.getErrMsg());
        assertEquals("wolfg", result.getLandDef().getLandId());
        assertEquals(999, result.getVid());
        assertEquals("/tmp", result.getOutdir());
        assertFalse(result.isStdout());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999", "-stdout");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertNull(result.getErrMsg());
        assertEquals("wolfg", result.getLandDef().getLandId());
        assertEquals(999, result.getVid());
        assertNull(result.getOutdir());
        assertTrue(result.isStdout());

        argList = Arrays.asList("-land", "wolf@", "-vid", "999", "-outdir", "/tmp");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertEquals("不正な国識別子です。 wolf@", result.getErrMsg());

        argList = Arrays.asList("-land", "wolfg", "-vid", "ZZZ", "-outdir", "/tmp");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertEquals("不正な村番号です。 ZZZ", result.getErrMsg());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999", "-outdir", "/tmp", "-help");
        result = OptInfo.parseOptInfo(argList);
        assertTrue(result.isHelp());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999", "-outdir");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertEquals("オプション -outdir に引数がありません。", result.getErrMsg());

        argList = Arrays.asList("-vid", "999", "-outdir", "/tmp");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertEquals("-land オプションで国識別子を指定してください。", result.getErrMsg());

        argList = Arrays.asList("-land", "wolfg", "-outdir", "/tmp");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertEquals("-vid オプションで村番号を指定してください。", result.getErrMsg());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertEquals("-outdir か -stdout のどちらか一方を指定してください。", result.getErrMsg());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999", "-outdir", "/tmp", "-stdout");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertNull(result.getOutdir());
        assertTrue(result.isStdout());
        assertNull(result.getErrMsg());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999", "-stdout", "-outdir", "/tmp");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertEquals("/tmp", result.getOutdir());
        assertFalse(result.isStdout());
        assertNull(result.getErrMsg());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999", "-outdir", "/tmp", "-land", "wolff");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertNull(result.getErrMsg());
        assertEquals("wolff", result.getLandDef().getLandId());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999", "-outdir", "/tmp", "-vid", "777");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertNull(result.getErrMsg());
        assertEquals(777, result.getVid());

        argList = Arrays.asList("-land", "wolfg", "-vid", "999", "-outdir", "/tmp", "-outdir", "/var/tmp");
        result = OptInfo.parseOptInfo(argList);
        assertFalse(result.isHelp());
        assertNull(result.getErrMsg());
        assertEquals("/var/tmp", result.getOutdir());

        return;
    }

}
