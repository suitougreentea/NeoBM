package io.github.suitougreentea.NeoBM.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import io.github.suitougreentea.NeoBM.NBMData;
import io.github.suitougreentea.NeoBM.NBMLoader;
import io.github.suitougreentea.NeoBM.NBMSyntaxError;

import org.junit.Test;

public class NBMTest {

    @Test
    public void testParse() {
        NBMData d = null;
        try {
            d = NBMLoader.loadNBM("test/loadtest.nbm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NBMSyntaxError e) {
            System.out.println(e.getMessage());
        }
        assertNotNull(d);
        assertEquals(d.getDocType(), "neobm");
        assertEquals(d.getHeader().getTitle(), "テスト");
        assertEquals(d.getHeader().getSubtitle(), "(Sample Mix)");
        assertEquals(d.getHeader().getArtist(), "サブは空");
        assertEquals(d.getHeader().getSubartist(), "");
        assertEquals(d.getHeader().getGenre(), "♥");
    }



}
