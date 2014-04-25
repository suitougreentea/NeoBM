package io.github.suitougreentea.NeoBM.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import io.github.suitougreentea.NeoBM.NBM.NBMData;
import io.github.suitougreentea.NeoBM.NBM.NBMLoader;
import io.github.suitougreentea.NeoBM.NBM.NBMSyntaxError;
import io.github.suitougreentea.NeoBM.NBM.sequence.EventNote;
import io.github.suitougreentea.NeoBM.NBM.sequence.EventTempo;
import io.github.suitougreentea.NeoBM.NBM.sequence.EventTime;

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
        assertEquals(d.getHeaderMap().get("title"), "テスト");
        assertEquals(d.getHeaderMap().get("subtitle"), "(Sample Mix)");
        assertEquals(d.getHeaderMap().get("artist"), "サブは空");
        assertEquals(d.getHeaderMap().get("subartist"), "");
        assertEquals(d.getHeaderMap().get("genre"), "♥");

        assertEquals(d.getSoundMap().get(128), "b.ogg");
        assertEquals(d.getImageMap().get(2), "e.png");

        assertEquals(((EventTime)(d.getSequence().get(0))).getBeat(), 4);
        assertEquals(((EventTempo)(d.getSequence().get(1))).getTempo(), 320f, 0f);
        assertEquals(((EventNote)(d.getSequence().get(3))).getLane(), 1);
    }



}
