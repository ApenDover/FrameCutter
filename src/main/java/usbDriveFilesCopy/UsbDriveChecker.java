package usbDriveFilesCopy;

import audio.Sound;
import folderMonitoring.FolderMonitoringStarter;
import folderMonitoring.GiveVideoFolderFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class UsbDriveChecker implements Runnable {

    static final Logger logger = LoggerFactory.getLogger(UsbDriveChecker.class);
    private static boolean copyProcess = false;
    private static TreeSet<File> driveList = new TreeSet<>(); // основные железяки
    private static TreeSet<File> newDriveList = new TreeSet<>(); // основные железяки
    private static TreeSet<File> plugUsbDevices = new TreeSet<>(); // новые актуально подключенные
    private static TreeSet<File> oldUsbDevices = new TreeSet<>(); // подключенные не актуальные
    private static File driveCatalog;
    private static String plugSound;
    private static String errorSound;
    private static String finishedCopySound;

    public UsbDriveChecker() {
        if (WhatYourSystem.getOperatingSystem().equals(WhatYourSystem.OS.MAC)) {
            plugSound = "/Users/andrey/Music/jarSound/usbInput.wav";
            errorSound = "/Users/andrey/Music/jarSound/error.wav";
            finishedCopySound = "/Users/andrey/Music/jarSound/copyDone.wav";
            driveCatalog = new File("/Volumes");
        }
        if (WhatYourSystem.getOperatingSystem().equals(WhatYourSystem.OS.LINUX)) {
            plugSound = "/opt/jarSound/usbInput.wav";
            finishedCopySound = "/opt/jarSound/copyDone.wav";
            errorSound = "/opt/jarSound/error.wav";
            driveCatalog = new File("/media/andrey/");
        }

        driveList.addAll(List.of(driveCatalog.listFiles()));
        //            driveList.addAll(List.of(File.listRoots()));
//            plugSound = "C:/jarSound/usbInput.wav";
//            finishedCopySound = "C:/jarSound/copyDone.wav"
    }

    @Override
    public void run() {
        TreeSet<File> driveList = new TreeSet<>();
//        смотрим на данный момент список файлов
        driveList.addAll(List.of(driveCatalog.listFiles()));
        // смотрим отключенные
        TreeSet<File> unplug = new TreeSet<>();
        plugUsbDevices.forEach(file -> {
            if (driveList.contains(file)) {
                oldUsbDevices.add(file);
            } else unplug.add(file);
        });
        plugUsbDevices.removeAll(unplug);
        oldUsbDevices.removeAll(unplug);
        newDriveList.removeAll(unplug);

        if (driveList.size() != UsbDriveChecker.driveList.size()) {
            plugUsbDevices.addAll(driveList);
            plugUsbDevices.removeAll(UsbDriveChecker.driveList);
            newDriveList.addAll(plugUsbDevices);
            newDriveList.removeAll(oldUsbDevices);
//            System.out.println("Воткнуты устройства: ");
//            plugUsbDevices.forEach(file -> System.out.println(file.getAbsolutePath()));
//            System.out.println("Новые: ");
//            newDriveList.forEach(file -> System.out.println(file.getAbsolutePath()));
//            System.out.println();
        }


        if (newDriveList.size() > 0) {
            logger.info("Новая флешка: " + newDriveList.stream().findFirst().get().getAbsolutePath());
//            soundPlay(plugSound);
            for (File file : newDriveList) {
                copyProcess = true;
                TreeSet<File> driveFilesList = new TreeSet<>();
                if (file.listFiles().length > 0) {
                    driveFilesList.addAll(GiveVideoFolderFiles.giveFiles(file.listFiles()));
                }
                StringBuffer stringBuffer = new StringBuffer();
                driveFilesList.forEach(file1 -> {
                    stringBuffer.append(file1.getName());
                });
                logger.info("Начинаю копировать с устройства: " + file.getAbsolutePath() + " # " + stringBuffer);
                for (File sourceFile : driveFilesList) {
                    int number = 0;
                    if (sourceFile.getName().contains("_ch1_")) {
                        FolderMonitoringStarter.setCam1(FolderMonitoringStarter.getCam1() + 1);
                        number = FolderMonitoringStarter.getCam1();
                    }
                    if (sourceFile.getName().contains("_ch2_")) {
                        FolderMonitoringStarter.setCam2(FolderMonitoringStarter.getCam2() + 1);
                        number = FolderMonitoringStarter.getCam2();
                    }
                    if (sourceFile.getName().contains("_ch3_")) {
                        FolderMonitoringStarter.setCam3(FolderMonitoringStarter.getCam3() + 1);
                        number = FolderMonitoringStarter.getCam3();
                    }
                    if (sourceFile.getName().contains("_ch4_")) {
                        FolderMonitoringStarter.setCam4(FolderMonitoringStarter.getCam4() + 1);
                        number = FolderMonitoringStarter.getCam4();
                    }
                    File destFileSave = new File(FolderMonitoringStarter.getSaveVideoPath() + "/" + number + "_" + sourceFile.getName());
                    String type = sourceFile.getName().substring(sourceFile.getName().lastIndexOf('.'));
                    String nameNumber = String.format("%04d", number);
                    File destFile = new File(FolderMonitoringStarter.getHotFolderPath() + "/" + nameNumber + type);
                    try {
                        copyFileUsingChannel(sourceFile, destFile);
                        copyFileUsingChannel(sourceFile, destFileSave);
                    } catch (IOException e) {
                        logger.error(e.getMessage() + " # " + Arrays.toString(e.getStackTrace()));
                        logger.error(stringBuffer.toString());
                    }
                }
                copyProcess = false;
                logger.info("Копирование завершено.");

                try {
                    soundPlay(finishedCopySound);
                    FileUtils.cleanDirectory(newDriveList.stream().findFirst().get());
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        if (!dest.exists()) {
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;
            try {
                sourceChannel = new FileInputStream(source).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } finally {
                sourceChannel.close();
                destChannel.close();
            }
        }
    }

    public static void soundPlay(String path) {
//        Sound.playSound(path);
        Thread thread = new Thread(() -> {
            Sound sound = new Sound(new File(path));
            sound.setVolume(100);
            while (!sound.isReleased()) {
            }
            sound.play(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage() + " # " + Arrays.toString(e.getStackTrace()));
            }
            sound.close();
        });
        thread.run();
    }

    public static boolean isCopyProcess() {
        return copyProcess;
    }
}
