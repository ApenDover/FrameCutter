package usbDriveFilesCopy;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UsbDriveCheckerStarter {
    public static void start() {
        UsbDriveChecker usbDriveChecker = new UsbDriveChecker();
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(usbDriveChecker, 0, 3, TimeUnit.SECONDS);
    }
}
