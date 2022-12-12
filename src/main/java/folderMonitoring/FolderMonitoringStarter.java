package folderMonitoring;

import com.sun.tools.javac.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FolderMonitoringStarter {
    static final Logger logger = LoggerFactory.getLogger(FolderMonitoringStarter.class);
    private static String hotFolderPath;
    private static String saveVideoPath;
    private static int cam1;
    private static int cam2;
    private static int cam3;
    private static int cam4;
    private static int picCount;

    public static void start(int picCount, String param) {
        Scanner scanner = new Scanner(System.in);
        if (param.equals("")) {
            System.out.println("Параметры по умолчанию? [y/n/o]");
            param = scanner.nextLine();
        }
        String resultFolderPath = "";
        String sh = "";
        if (param.equals("y") | param.equals("o")) {
            hotFolderPath = "/home/andrey/Documents/hotFolder";
            resultFolderPath = "/home/andrey/Yandex.Disk/result";
            saveVideoPath = "/home/andrey/Documents/SaveVideo";
            sh = "/opt/frameCutter/cutter.sh";
        }
        if (param.equals("n")) {
            System.out.println("Путь к ключу:");
            sh = scanner.nextLine();

            File shFile = new File(sh);
            if (!shFile.exists()) {
                logger.error("Ошибка файла .sh, указанного " + sh + " не существует");
                System.exit(-1);
            }

            System.out.println("Путь к папке, в которую будут помещаться видео на обработку:");
            hotFolderPath = scanner.nextLine();

            System.out.println("Путь к итоговой папке");
            resultFolderPath = scanner.nextLine();

            System.out.println("Путь куда складвать видео-оригиналы");
            saveVideoPath = scanner.nextLine();
        }

        if (picCount == 0) {
            System.out.println("Сколько кадров нужно из видео?");
            FolderMonitoringStarter.picCount = scanner.nextInt();
        } else FolderMonitoringStarter.picCount = picCount;

//          считать кол-во папок в /Volume

//        FolderMonitoringStarter.picCount = 70;
//        hotFolderPath = "/Users/andrey/Pictures/Фотографии/testCut/hot";
//        saveVideoPath = "/Users/andrey/Pictures/Фотографии/testCut/save";
//        String resultFolderPath = "/Users/andrey/Pictures/Фотографии/testCut/result";
//        String sh = "/Users/andrey/Pictures/Фотографии/testCut/cutter.sh";

        File hotFolder = new File(hotFolderPath);
        if (!hotFolder.exists()) hotFolder.mkdirs();

        File resultFolderFile = new File(resultFolderPath);
        if (!resultFolderFile.exists()) resultFolderFile.mkdirs();

        File saveFolderFile = new File(saveVideoPath);
        if (!saveFolderFile.exists()) saveFolderFile.mkdirs();

        ArrayList<File> allFolderResult = new ArrayList<>(List.of(resultFolderFile.listFiles()));

        ArrayList<Integer> ch1List = new ArrayList<>();
        ArrayList<Integer> ch2List = new ArrayList<>();
        ArrayList<Integer> ch3List = new ArrayList<>();
        ArrayList<Integer> ch4List = new ArrayList<>();

        for (File folder : allFolderResult) {
            if (folder.getName().contains("_ch1_"))
                ch1List.add(Integer.parseInt(folder.getName().substring(0, folder.getName().indexOf('_'))));
            if (folder.getName().contains("_ch2_"))
                ch2List.add(Integer.parseInt(folder.getName().substring(0, folder.getName().indexOf('_'))));
            if (folder.getName().contains("_ch3_"))
                ch3List.add(Integer.parseInt(folder.getName().substring(0, folder.getName().indexOf('_'))));
            if (folder.getName().contains("_ch4_"))
                ch4List.add(Integer.parseInt(folder.getName().substring(0, folder.getName().indexOf('_'))));
        }

        boolean paramChosen = param.equals("o") | param.equals("n");

        if (ch1List.size() > 0) cam1 = Collections.max(ch1List);
        else {
            if (paramChosen) {
                System.out.println("Номер человека на камере 1");
                cam1 = scanner.nextInt() - 1;
            } else cam1 = 0;
        }
        if (ch2List.size() > 0) cam2 = Collections.max(ch2List);
        else {
            if (paramChosen) {
                System.out.println("Номер человека на камере 2");
                cam2 = scanner.nextInt() - 1;
            } else cam2 = 250;
        }
        if (ch3List.size() > 0) cam3 = Collections.max(ch3List);
        else {
            if (paramChosen) {
                System.out.println("Номер человека на камере 3");
                cam3 = scanner.nextInt() - 1;
            } else cam3 = 500;
        }
        if (ch4List.size() > 0) cam4 = Collections.max(ch4List);
        else {
            if (paramChosen) {
                System.out.println("Номер человека на камере 4");
                cam4 = scanner.nextInt() - 1;
            } else cam4 = 750;
        }

        HotFolderChecking hotFolderChecking = new HotFolderChecking(sh, hotFolder, resultFolderFile, FolderMonitoringStarter.picCount);
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(hotFolderChecking, 1, 3, TimeUnit.SECONDS);
    }

    public static String getHotFolderPath() {
        return hotFolderPath;
    }

    public static String getSaveVideoPath() {
        return saveVideoPath;
    }

    public static int getCam1() {
        return cam1;
    }

    public static void setCam1(int cam1) {
        FolderMonitoringStarter.cam1 = cam1;
    }

    public static int getCam2() {
        return cam2;
    }

    public static void setCam2(int cam2) {
        FolderMonitoringStarter.cam2 = cam2;
    }

    public static int getCam3() {
        return cam3;
    }

    public static void setCam3(int cam3) {
        FolderMonitoringStarter.cam3 = cam3;
    }

    public static int getCam4() {
        return cam4;
    }

    public static void setCam4(int cam4) {
        FolderMonitoringStarter.cam4 = cam4;
    }

    public static int getPicCount() {
        return picCount;
    }
}
