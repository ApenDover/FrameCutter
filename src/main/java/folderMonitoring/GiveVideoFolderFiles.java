package folderMonitoring;

import audio.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import usbDriveFilesCopy.UsbDriveChecker;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class GiveVideoFolderFiles {
    static final Logger logger = LoggerFactory.getLogger(GiveVideoFolderFiles.class);

    public static TreeSet<File> giveFiles(File[] listOfFiles) {
        TreeSet<File> files = new TreeSet<>();
        TreeSet<File> removeDirectory = new TreeSet<>();
        if (listOfFiles.length != 0) {
            for (File file : listOfFiles) {
                if(file.isDirectory() & !file.getName().contains("_Store") & !file.getName().contains("System Volume Information") & file.getName().charAt(0) != '.' & !file.getName().contains("SmartPlayer"))
                {
                    files.addAll(giveFiles(file.listFiles()));
                }
                if (!file.getName().contains("_Store") & !file.getName().contains("System Volume Information") & file.getName().charAt(0) != '.' & !file.getName().contains("SmartPlayer") & !file.getName().equals("NVR")) {
                    files.add(file);
                }
            }
        }
        files.forEach(file -> {
            if (file.isDirectory()) {
                removeDirectory.add(file);
            }
        });
        files.removeAll(removeDirectory);
        return files;
    }
}
