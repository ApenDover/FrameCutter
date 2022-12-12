import folderMonitoring.FolderMonitoringStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import usbDriveFilesCopy.UsbDriveCheckerStarter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static int countFrame = 0;
    private static String param = "";

    public static void main(String[] args){

        if (args.length > 0)
        {
            countFrame = Integer.parseInt(args[0]);
        }
        if (args.length > 1)
        {
            param = args[1];
        }
        logger.info("Запуск");

        UsbDriveCheckerStarter.start();
        FolderMonitoringStarter.start(countFrame, param);
    }
}
