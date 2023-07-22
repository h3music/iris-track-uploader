package zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {
    /**
     * Create Zip file from wav files in a folder
     * @param name the name of the file
     * @param folder folder to files from
     * @throws IOException
     */
    public static void create(String name, File folder) throws IOException {

        ArrayList<File> filesList = new ArrayList<>();

        for (File file: Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().contains(".wav") && file.getName().contains("H3Music_")) {
                filesList.add(file);
            }
        }

        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(folder + "\\H3Music_" + name + ".zip"));

        for (File file : filesList) {
            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
    }
}
