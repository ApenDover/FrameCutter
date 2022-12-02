import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FolderFilesCutter {
    static final Logger logger = LoggerFactory.getLogger(FolderFilesCutter.class);
    private int it;
    private final File resultFolder;
    private final String count;
    private final String sh;
    private ThreadPoolExecutor threadPoolExecutor;
    TreeSet<File> actualFiles = new TreeSet<>();

    public FolderFilesCutter(String sh, File resultFolder, String count) {
        this.sh = sh;
        this.resultFolder = resultFolder;
        this.count = count;
        int nThreads = Runtime.getRuntime().availableProcessors() + 1;
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
        logger.trace("Смотрю горячую папку " + ++it + "..");

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
                    logger.trace("Нашел новый файл: " + file.getName());
                    Thread thread = new Thread(() -> {
                        try {
                            fileResultFolder.mkdir();
                            ShellRunner.run(sh, file.getAbsolutePath(), fileResultFolder.getAbsolutePath(), count);
                        } catch (IOException | InterruptedException e) {
                            logger.error(e.getMessage() + " - " + Arrays.toString(e.getStackTrace()));
                        }
                    });
                    logger.trace("Добавляю в очередь на обработку новый файл!");
                    threadPoolExecutor.submit(thread);
                }
            }
        }
    }
}