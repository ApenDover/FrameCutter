package folderMonitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class ShellRunner {
    static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);

    public static void run(String sh, String file, String resultFolderPath, String count) throws IOException, InterruptedException {
        logger.info("Приступаю к расщиплению " + file.substring(file.lastIndexOf('/') + 1));
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", sh, file, resultFolderPath, count);
        Process p = pb.start();

        p.waitFor();

        ArrayList<File> cutterFile = new ArrayList<>(List.of(new File(resultFolderPath).listFiles()));
        if (cutterFile.size() < FolderMonitoringStarter.getPicCount())
        {
            logger.error("В папке " + resultFolderPath + " - " + cutterFile.size() + " файлов");
        }

        File f = new File(resultFolderPath);
        File[] listOfFiles = f.listFiles();
        String folderFileName = file.substring(file.lastIndexOf('/') + 1, file.lastIndexOf('.'));
        TreeSet<File> treeSetOfFiles = GiveVideoFolderFiles.giveFiles(listOfFiles);
        File jpgFolder = new File(resultFolderPath.substring(0, resultFolderPath.lastIndexOf('/')) + "/" + folderFileName + "/" + "JPEG");
        File pngFolder = new File(resultFolderPath.substring(0, resultFolderPath.lastIndexOf('/')) + "/" + folderFileName + "/" + "PNG");
        jpgFolder.mkdir();
        pngFolder.mkdir();
        for (File fi : treeSetOfFiles) {
            String nameFull = fi.getName();
            String name = nameFull.substring(0, nameFull.lastIndexOf('.'));
            File pngFile = new File(pngFolder.getAbsolutePath() + "/" + folderFileName + "_" + name + ".png");
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(fi);
                ImageIO.write(bufferedImage, "png", pngFile);
                fi.renameTo(new File(jpgFolder + "/" + folderFileName + "_" + nameFull));
            } catch (IOException e) {
                logger.error(fi.getAbsolutePath() + " : " + e.getMessage() + " - " + Arrays.toString(e.getStackTrace()));
            }

        }
        logger.info("Раскадровка " + file.substring(file.lastIndexOf('/') + 1) + " завершено");
    }
}
