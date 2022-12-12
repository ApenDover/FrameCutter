package folderMonitoring;

import usbDriveFilesCopy.UsbDriveChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.TreeSet;
import java.util.concurrent.Callable;

public class HotFolderChecking implements Runnable {
    static int c = 0;
    static final Logger logger = LoggerFactory.getLogger(HotFolderChecking.class);
    private final File hotFolder;
    private final File resultFolder;
    private static TreeSet<File> treeSetOfFiles = new TreeSet<>();
    private static TreeSet<File> treeSetOfReadyFolder = new TreeSet<>();
    FolderFilesPoolStarter folderFilesPoolStarter;

    public HotFolderChecking(String sh, File hotFolder, File resultFolder, int count) {
        this.hotFolder = hotFolder;
        this.resultFolder = resultFolder;
        folderFilesPoolStarter = new FolderFilesPoolStarter(sh, resultFolder, count);
    }

    public static TreeSet<File> getTreeSetOfFiles() {
        return treeSetOfFiles;
    }

    @Override
    public void run() {
        c++;
//        logger.trace("Смотрю горячую папку");
        if (UsbDriveChecker.isCopyProcess()) {
            logger.info("Жду завершения копирования..");
        }

        while (UsbDriveChecker.isCopyProcess()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        File[] listOfFiles = hotFolder.listFiles();
        File[] listOfReadyFolder = resultFolder.listFiles();
        treeSetOfFiles = GiveVideoFolderFiles.giveFiles(listOfFiles);
        treeSetOfReadyFolder = GiveVideoFolderFiles.giveFiles(listOfReadyFolder);
        if (treeSetOfFiles.size() != folderFilesPoolStarter.getCountFilesAtWork()) {
//            logger.info("Новых файлов в папке: " + treeSetOfFiles.size());
            logger.info("Новых файлов в папке: " + treeSetOfFiles.size() + " |" + " В очереди: " + folderFilesPoolStarter.getQueenSize() + " |" + " В процессе: " + folderFilesPoolStarter.getActiveCount() + " |" + " Готово: " + folderFilesPoolStarter.getReadySize());
            folderFilesPoolStarter.startCutItAll();
        } else {
            if (c == 5) {
                logger.info("В папке: " + treeSetOfFiles.size() + " |" + " В очереди: " + folderFilesPoolStarter.getQueenSize() + " |" + " В процессе: " + folderFilesPoolStarter.getActiveCount() + " |" + " Готово: " + folderFilesPoolStarter.getReadySize());
                c = 0;
            }
        }
    }

}
