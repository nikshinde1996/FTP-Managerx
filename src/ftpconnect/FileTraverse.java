package ftpconnect;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;

/**
 * Created by Nikhil Shinde on 8/3/2016.
 */
public class FileTraverse extends SimpleFileVisitor<Path> {
    protected static List<File> fileList = new ArrayList<>();

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) {
        fileList.removeAll(fileList);
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (!attr.isDirectory()) fileList.add(file.toFile());
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path file, IOException e) {

        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path file, IOException e) {
        e.printStackTrace();
        return FileVisitResult.CONTINUE;
    }

    protected static void triggerTraverse(Path path) throws IOException {
        FileTraverse fileTraverse = new FileTraverse();
        Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), 1, fileTraverse);
    }
}