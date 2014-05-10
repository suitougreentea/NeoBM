package io.github.suitougreentea.NeoBM;

import java.util.logging.Logger;

public class NeoBM {
    public static final Logger logger = Logger.getLogger("io.github.suitougreentea.NeoBM");
    public static final String NAME = "NeoBM";
    public static final String MILESTONE = "M3";
    public static final String MILESTONE_DESC = "Gameplay Test";
    public static final String VERSION = "0.3.0";

    public static String getFullVersion() {
        return String.format("%s %s (%s, %s)", NAME, MILESTONE, MILESTONE_DESC, VERSION);
    }
}
