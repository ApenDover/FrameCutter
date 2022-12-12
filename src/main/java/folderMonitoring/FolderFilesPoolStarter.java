package folderMonitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class FolderFilesPoolStarter {
    static final Logger logger = LoggerFactory.getLogger(FolderFilesPoolStarter.class);
    private int it;
    private final File resultFolder;
    private final int count;
    private final String sh;
    private ThreadPoolExecutor threadPoolExecutor;
    TreeSet<File> actualFiles = new TreeSet<>();

    public FolderFilesPoolStarter(String sh, File resultFolder, int count) {
        this.sh = sh;
        this.resultFolder = resultFolder;
        this.count = count;
//        int nThreads = Runtime.getRuntime().availableProcessors() + 1;
        int nThreads = 5;
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        it = 0;
    }

    public long getQueenSize() {
        return threadPoolExecutor.getQueue().size();
    }

    public long getReadySize() {
        return threadPoolExecutor.getTaskCount() - (getActiveCount() + getQueenSize());
    }

    public int getActiveCount() {
        return threadPoolExecutor.getActiveCount();
    }

    public int getCountFilesAtWork() {
        return actualFiles.size();
    }

    public void startCutItAll() {
//        logger.trace("Смотрю горячую папку " + ++it + "..");
        TreeSet<File> treeSetOfFiles = HotFolderChecking.getTreeSetOfFiles();
        treeSetOfFiles.removeAll(actualFiles);
        logger.info("# " + ++it + " # НОВЫЕ ФАЙЛЫ: " + treeSetOfFiles.size());
        for (File file : treeSetOfFiles) {
            actualFiles.add(file);
            String fullFileName = file.getName();
            String fileName = fullFileName.substring(0, fullFileName.lastIndexOf('.'));

            if (file.isFile()) {
                File fileResultFolder = new File(resultFolder.getAbsolutePath() + "/" + fileName);
                if (!fileResultFolder.exists()) {
                    Thread thread = new Thread(() -> {
                        try {
                            fileResultFolder.mkdir();
                            ShellRunner.run(sh, file.getAbsolutePath(), fileResultFolder.getAbsolutePath(), String.valueOf(count));
                        } catch (IOException | InterruptedException e) {
                            logger.error(e.getMessage() + " - " + Arrays.toString(e.getStackTrace()));
                        }
                    });
                    logger.trace("Добавляю в очередь на обработку: " + file.getName());
                    threadPoolExecutor.submit(thread);
                }
            }
        }
    }
}