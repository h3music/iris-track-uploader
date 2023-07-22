package video;

import java.io.File;
import java.util.ArrayList;

public record VideoRecord(
        ArrayList<Integer> impactFrames,
        File sourceFile,
        File audio,
        File outputFile
) {}
