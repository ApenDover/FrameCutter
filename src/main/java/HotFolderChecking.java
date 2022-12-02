import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.TreeSet;

public class HotFolderChecking implements Runnable{
    static final Logger logger = LoggerFactory.getLogger(HotFolderChecking.class);
    private final File hotFolder;
    private final File resultFolder;
    private static TreeSet<File> treeSetOfFiles = new TreeSet<>();
    private static TreeSet<File> treeSetOfReadyFolder = new TreeSet<>();
    FolderFilesCutter folderFilesCutter;

    public HotFolderChecking(String sh, File hotFolder, File resultFolder, String count) {
        this.hotFolder = hotFolder;
        this.resultFolder = resultFolder;
        folderFilesCutter = new FolderFilesCutter(sh, resultFolder, count);
    }

    public static TreeSet<File> getTreeSetOfFiles() {
        return treeSetOfFiles;
    }

    @Override
    public void run() {
        logger.trace("Смотрю горячую папку");
        File[] listOfFiles = hotFolder.listFiles();
        File[] listOfReadyFolder = resultFolder.listFiles();
        treeSetOfFiles = GiveVideoFolderFiles.giveVideoFiles(listOfFiles);
        treeSetOfReadyFolder = GiveVideoFolderFiles.giveVideoFiles(listOfReadyFolder);
        if (treeSetOfFiles.size() != folderFilesCutter.getCountFilesAtWork()) {
            folderFilesCutter.startCutItAll();
            logger.info("Новых файлов в папке: " + treeSetOfFiles.size() + " |" + " В очереди: " + folderFilesCutter.getQueenSize() + " |" + " В процессе: " + folderFilesCutter.getActiveCount() + " |" + " Готово: " + folderFilesCutter.getReadySize());
//            System.out.println("вижу " + (treeSetOfFiles.size() - treeSetOfReadyFolder.size()) + " новых файлов" );
        } else {
            logger.info("В папке: " + treeSetOfFiles.size() + " |" + " В очереди: " + folderFilesCutter.getQueenSize() + " |" + " В процессе: " + folderFilesCutter.getActiveCount() + " |" + " Готово: " + folderFilesCutter.getReadySize());
        }
    }



}
