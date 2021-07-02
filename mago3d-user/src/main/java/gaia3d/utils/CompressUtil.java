//package gaia3d.utils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.compress.archivers.ArchiveInputStream;
//import org.apache.commons.compress.archivers.ArchiveOutputStream;
//import org.apache.commons.compress.archivers.ArchiveStreamFactory;
//import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
//import org.apache.commons.compress.utils.IOUtils;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FilenameUtils;
//
///**
// * 파일 압축용 Util
// */
//public class CompressUtil {
//    /**
//     * 하나 파일 압축
//     * @param sourceFile : 압축할 파일명 혹은 폴더명
//     * @param targetZipFile : 압축 후 생성할 파일명
//     * @throws Exception
//     */
//    public static void makeZipFile(File sourceFile, File targetZipFile) throws Exception {
//        makeZipFile(new File[] { sourceFile }, targetZipFile);
//    }
//
//    /**
//     * 같은 폴더에 있는 특정 파일들을 선택하여 압축
//     * @param inFiles : 압축할 파일 목록, 같은 폴더에 있어야 됨.
//     * @param zipFile : 압축 후 생성할 파일명
//     * @throws Exception
//     */
//    public static void makeZipFile(File[] sourceFiles, File targetZipFile) throws Exception {
//        FileOutputStream fileOutputStream = null;
//        ArchiveOutputStream archiveOutputStream = null;
//        String zipStartPath= null;
//
//        if(sourceFiles!=null && sourceFiles.length>0) zipStartPath = sourceFiles[0].getParent();
//        else return;
//
//        try {
//            FileUtils.forceMkdir(targetZipFile.getParentFile());
//            fileOutputStream = new FileOutputStream(targetZipFile);
//            archiveOutputStream = new ArchiveStreamFactory().createArchiveOutputStream("zip", fileOutputStream);
//            for (int i = 0; i < sourceFiles.length; i++)  recursiveCompress(sourceFiles[i], zipStartPath, archiveOutputStream);
//        } finally {
//            if (archiveOutputStream != null) archiveOutputStream.close();
//            if (fileOutputStream != null) fileOutputStream.close();
//        }
//    }
//
//    /**
//     * 하위 폴더를 (재귀) 순회하면서, zipOutPut에 파일들을 추가.
//     * @param inFile
//     * @param inPath
//     * @param zipOutput
//     * @throws Exception
//     */
//    private static void recursiveCompress(File sourceFile, String zipStartPath, ArchiveOutputStream zipOutput) throws Exception {
//        FileInputStream fis = null;
//        try {
//            if (sourceFile.isDirectory()) {
//                File[] fileArray = sourceFile.listFiles();
//                for (int i = 0; i < fileArray.length; i++) recursiveCompress(fileArray[i], zipStartPath, zipOutput);
//            } else {
//                String inFilePath = FilenameUtils.separatorsToUnix(sourceFile.getPath());
//                String zipEntryName = inFilePath.substring(zipStartPath.endsWith("/") ? zipStartPath.length() + 1 : zipStartPath.length(), inFilePath.length());
//
//                ZipArchiveEntry zentry = new ZipArchiveEntry(zipEntryName);
//                zentry.setTime(sourceFile.lastModified());
//                zipOutput.putArchiveEntry(zentry);
//                fis = new FileInputStream(sourceFile);
//                IOUtils.copy(fis, zipOutput);
//                zipOutput.closeArchiveEntry();
//            }
//        } finally {
//            if (fis != null) fis.close();
//        }
//    }
//
//    /**
//     * 압축 풀기
//     * @param extractDir
//     * @param sourceZipFile
//     * @return
//     * @throws Exception
//     */
//    public static List<File> decompress(File extractDir, File sourceZipFile) throws Exception {
//        InputStream inputStream = null;
//        ArchiveInputStream zipInputStream = null;
//        List<File> fileList = new ArrayList<File>();
//
//        try {
//            inputStream = new FileInputStream(sourceZipFile);
//            zipInputStream = new ArchiveStreamFactory().createArchiveInputStream("zip", inputStream);
//            ZipArchiveEntry entry = null;
//            while ((entry = (ZipArchiveEntry) zipInputStream.getNextEntry()) != null) {
//                File entryFile = new File(extractDir, entry.getName());
//                if (entry.isDirectory()) {
//                    FileUtils.forceMkdir(entryFile);
//                } else {
//                    FileUtils.forceMkdir(entryFile.getParentFile());
//                    OutputStream outputStream = new FileOutputStream(entryFile);
//                    try {
//                        IOUtils.copy(zipInputStream, outputStream);
//                    } finally {
//                        outputStream.close();
//                    }
//                }
//                fileList.add(entryFile);
//            }
//            return fileList;
//        } finally {
//            if (zipInputStream != null) zipInputStream.close();
//            if (inputStream != null) inputStream.close();
//        }
//    }
//}