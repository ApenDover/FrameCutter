import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        logger.info("Запуск");

//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Путь к ключу:");
//        String sh = scanner.nextLine();
//
//        System.out.println("Сколько кадров нужно из видео?");
//        String picCount = scanner.nextLine();
//
//        System.out.println("Путь к папке, в которую будут помещаться видео на обработку:");
//        String hotFolderPath = scanner.nextLine();
//
//        System.out.println("Путь к итоговой папке");
//        String resultFolderPath = scanner.nextLine();

        String picCount = "72";
        String hotFolderPath = "/Users/andrey/Pictures/Фотографии/testCut/test";
        String resultFolderPath = "/Users/andrey/Pictures/Фотографии/testCut/result";
        String sh = "/Users/andrey/Pictures/Фотографии/testCut/cutter.sh";

        File hotFolder = new File(hotFolderPath);
        File resultFolder = new File(resultFolderPath);
        if (!hotFolder.exists()) hotFolder.mkdirs();
        if (!resultFolder.exists()) resultFolder.mkdirs();
        HotFolderChecking hotFolderChecking = new HotFolderChecking(sh, hotFolder, resultFolder, picCount);
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(hotFolderChecking, 0, 10, TimeUnit.SECONDS);
    }
}
