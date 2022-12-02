import java.io.File;
import java.util.TreeSet;

public class GiveVideoFolderFiles {
    public static TreeSet<File> giveVideoFiles(File[] listOfFiles) {
        TreeSet<File> files = new TreeSet<>();
        if (listOfFiles.length != 0) {
            for (File file : listOfFiles) {
                if (!file.getName().contains("_Store"))
                {
                    files.add(file);
                }
            }
        }
        return files;
    }
}
