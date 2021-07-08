package gaia3d.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioConstants;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@SuppressWarnings("unchecked")
public abstract class ZipUtils extends BaseSystemUtils {

    /**
     * DecompressCallBack.
     *
     * @author 임 성천.
     */
    public interface DecompressCallBack {
        /**
         * 압축 해제.
         * @param filePath 파일 경로.
         * @param destDir 압축 해제할 디렉토리 경로.
         * @throws IOException
         * @throws ArchiveException
         */
        public void doInDecompress(File filePath, File destDir) throws IOException,
                ArchiveException;
    }

    /**
     * 압축해제.
     *
     * @param decompressCallBack DecompressCallBack 객체.
     * @param srcFilePath 파일 경로.
     * @param destDir 압축 해제할 디렉토리 경로.
     * @return 압축이 정상적으로 해제되면  true.
     */
    protected static boolean processDecompress(final DecompressCallBack decompressCallBack,
                                               String srcFilePath, String destDir) {
        // check zipFile
        final File file = new File(srcFilePath);
        if (!file.exists() || !file.canRead()) {
            log.error(
                    "압축을 해제할 수 없습니다. {}가 존재하지 않거나 읽을 수 없습니다.",
                    file.getAbsolutePath());
            return false;
        }

        final File dest = (destDir == null) ? file.getParentFile() : new File(destDir);
        if (!dest.exists()) {
            dest.mkdirs();
        }

        return processIO(new IOCallback<Boolean>() {
            public Boolean doInProcessIO() throws IOException, Exception {
                decompressCallBack.doInDecompress(file, dest);
                return true;
            }
        });
    }

    /**
     * 압축 파일 내의 파일 및 디렉토리를 복사함.
     *
     * @param destDir 복사할 디렉토리 경로.
     * @param is InputStream
     * @param isDirectory 디렉토리 여부.
     * @param entryName 엔트리명.
     * @throws IOException
     */
    protected static void processEachContent(final File destDir,
                                             InputStream is, boolean isDirectory, String entryName)
            throws IOException {
        File itemFile = new File(destDir, entryName);
        if (isDirectory) {
            itemFile.mkdirs();
        } else {
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                fos = new FileOutputStream(itemFile);
                bos = new BufferedOutputStream(fos);
                IOUtils.copy(is, bos);
            } catch (IOException e) {
                log.error("fail to process each content.", e);
                throw e;
            } finally {
                if (bos != null) {
                    bos.flush();
                }
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(fos);
            }
        }
    }

    /**
     * zip파일 압축 해제.
     *
     * @param zipFilePath 압축 파일 경로.
     * @return 압축해제가 성공했을 경우 true.
     */
    public static boolean decompressZip(String zipFilePath) {
        return decompressZip(zipFilePath, null, null);
    }

    /**
     * zip파일 압축 해제.
     *
     * @param zipFilePath 압축 파일 경로.
     * @param destDir 압축해제할 경로.
     * @return 압축해제가 성공했을 경우 true.
     */
    public static boolean decompressZip(String zipFilePath, String destDir) {
        return decompressZip(zipFilePath, destDir, null);
    }

    /**
     * zip파일 압축 해제.
     *
     * @param zipFilePath 압축파일 경로.
     * @param destDir 압축해제할 경로.
     * @param encoding 인코딩값.(System.getProperty("file.encoding"))
     * @return 압축해제가 성공했을 경우 true.
     * @see org.apache.commons.compress.archivers.zip.ZipFile
     */
    public static boolean decompressZip(String zipFilePath, String destDir, final String encoding) {
        return processDecompress(new DecompressCallBack() {
            public void doInDecompress(File file, File destDir) throws IOException {
                ZipFile zipFile = new ZipFile(file, encoding, true);

                Enumeration<ZipArchiveEntry> entries = zipFile.getEntriesInPhysicalOrder();
                ZipArchiveEntry entry = null;

                while (entries.hasMoreElements()) {
                    entry = entries.nextElement();
                    InputStream content = zipFile.getInputStream(entry);
                    processEachContent(destDir, content, entry.isDirectory(),
                            entry.getName());
                    IOUtils.closeQuietly(content);
                }
                ZipFile.closeQuietly(zipFile);
            }
        }, zipFilePath, destDir);
    }

    /**
     * Gzip파일 압축 해제.
     *
     * @param gzipFile gzip파일.
     * @return 파일.
     * @throws IOException
     */
    public static File decompressGzip(File gzipFile) throws IOException {
        FileInputStream fis = new FileInputStream(gzipFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        File archiveFile = new File(GzipUtils.getUncompressedFilename(gzipFile.getAbsolutePath()));
        FileOutputStream fos = new FileOutputStream(archiveFile);
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(bis);

        IOUtils.copy(gzIn, fos);

        if (fos != null) {
            fos.flush();
        }

        IOUtils.closeQuietly(gzIn);
        IOUtils.closeQuietly(fos);
        IOUtils.closeQuietly(bis);
        IOUtils.closeQuietly(fis);

        return archiveFile;
    }

    /**
     * 압축 해제. (Zip/Jar/Tar/Ar/Cpio).
     *
     * @param filePath 압축파일 경로.
     * @return 압축해제에 성공했을 경우 true.
     * @see org.apache.commons.compress.archivers.ArchiveStreamFactory
     */
    public static boolean decompressArchive(String filePath) {
        return decompressArchive(filePath, null);
    }

    /**
     * 압축 해제. (Zip/Jar/Tar/Ar/Cpio).
     *
     * @param filePath 압축파일 경로.
     * @param destDir 압축을 해제할 경로.
     * @return 압축해제에 성공했을 경우 true.
     * @see ArchiveStreamFactory
     */
    public static boolean decompressArchive(String filePath, String destDir) {
        return processDecompress(new DecompressCallBack() {
            public void doInDecompress(File file, File destDir)
                    throws IOException, ArchiveException {
                if (GzipUtils.isCompressedFilename(file.getName())) {
                    file = decompressGzip(file);
                }
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);

                ArchiveInputStream ais = (ArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream(bis);
                ArchiveEntry entry = null;

                while ((entry = ais.getNextEntry()) != null) {
                    processEachContent(destDir, ais, entry.isDirectory(),
                            entry.getName());
                }

                IOUtils.closeQuietly(ais);
                IOUtils.closeQuietly(bis);
                IOUtils.closeQuietly(fis);
            }
        }, filePath, destDir);
    }

    /**
     * 압축해제. (java.util.zip)
     *
     * @param zipFilePath zip파일 경로.
     * @return 압축해제에 성공했을 경우 true.
     */
    public static boolean decompressJavaZip(String zipFilePath) {
        return decompressJavaZip(zipFilePath, null);
    }

    /**
     * 압축해제. (java.util.zip)
     *
     * @param zipFilePath zip파일 경로.
     * @param destDir 압축을 해제할 경로.
     * @return 압축해제에 성공했을 경우 true.
     */
    public static boolean decompressJavaZip(String zipFilePath, String destDir) {
        return processDecompress(new DecompressCallBack() {
            public void doInDecompress(File file, File destDir)
                    throws IOException, ArchiveException {
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);

                ZipInputStream zis = new ZipInputStream(bis);
                ZipEntry entry = null;

                while ((entry = zis.getNextEntry()) != null) {
                    processEachContent(destDir, zis, entry.isDirectory(),
                            entry.getName());
                    zis.closeEntry();
                }

                IOUtils.closeQuietly(zis);
                IOUtils.closeQuietly(bis);
                IOUtils.closeQuietly(fis);
            }
        }, zipFilePath, destDir);
    }

    /**
     * CompressCallBack interface
     *
     * @author 임 성천.
     */
    public interface CompressCallBack {
        public ArchiveOutputStream createArchiveOutputStream(OutputStream os);
        public ArchiveEntry createArchiveEntry(File targetDirFile, File entryFile);
    }

    /**
     * 파일 압축.
     *
     * @param filePath 파일 또는 디렉토리 경로.
     * @param destArchivePath destination archive
     * @param extension 확장자.(zip, tar, gz등.)
     * @return 압축된 파일.
     * @throws IOException
     */
    private static File createDestArchiveFile(File filePath,
                                              String destArchivePath, String extension) throws IOException {
        // check dest archive file
        File destArchiveFile = (destArchivePath == null) ? new File(
                filePath.getParent(), filePath.getName() + "."
                + extension) : new File(destArchivePath);
        if (!destArchiveFile.exists()) {
            File parentDir = new File(destArchiveFile.getAbsolutePath())
                    .getParentFile();
            if (!parentDir.exists()) {
                FileUtils.forceMkdir(parentDir);
            }
        }

        return destArchiveFile;
    }

    /**
     * 파일 압축.
     *
     * @param compressCallBack CompressCallBack 객체.
     * @param path 경로.
     * @param extension 확장자.(zip, jar, tar, ar, cpio)
     *
     * @return 압축이 성공했을 경우 true.
     */
    protected static boolean processCompress(final CompressCallBack compressCallBack,
                                             final String path, final String destArchivePath,
                                             final String extension) {
        return processIO(new IOCallback<Boolean>() {
            public Boolean doInProcessIO() throws IOException, Exception {
                final File targetDirFile = new File(path);
                if (!targetDirFile.exists() || !targetDirFile.canRead()) {
                    log.error(
                            "압축실패. {}가 존재하지 않거나 읽을 수 없습니다.",
                            targetDirFile.getAbsolutePath());
                    return false;
                }

                final File destArchiveFile = createDestArchiveFile(targetDirFile, destArchivePath, extension);

                FileOutputStream fos = new FileOutputStream(destArchiveFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                Iterator<File> iter = null;

                if (targetDirFile.isDirectory()) {
                    iter = getNestedFileAndDirListAll(targetDirFile, new ArrayList<File>()).iterator();
                } else {
                    iter = Arrays.asList(new File[] { targetDirFile }).iterator();
                }

                ArchiveOutputStream aos = compressCallBack.createArchiveOutputStream(bos);

                while (iter.hasNext()) {
                    File entryFile = iter.next();
                    if (entryFile.isFile()) {
                        FileInputStream entryFis = new FileInputStream(entryFile);
                        BufferedInputStream entryBis = new BufferedInputStream(entryFis);
                        ArchiveEntry entry = compressCallBack.createArchiveEntry(targetDirFile, entryFile);

                        aos.putArchiveEntry(entry);

                        IOUtils.copy(entryBis, aos);

                        aos.closeArchiveEntry();
                        IOUtils.closeQuietly(entryBis);
                        IOUtils.closeQuietly(entryFis);
                    } else {
                        aos.putArchiveEntry(compressCallBack.createArchiveEntry(targetDirFile, entryFile));
                        aos.closeArchiveEntry();
                    }
                }

                if (aos != null) {
                    aos.flush();
                }

                IOUtils.closeQuietly(aos);
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(fos);

                return true;
            }
        });
    }

    /**
     * 파일 압축. (디렉토리 내의 모든 파일을 압축함.)
     *
     * @param path 경로.
     * @param files 리턴할 파일 객체 목록.
     * @return 파일 객체 목록.
     */
    public static List<File> getNestedFileAndDirListAll(File path, List<File> files) {
        for (File dirOrFile : path.listFiles()) {
            if (dirOrFile.isDirectory() && dirOrFile.canRead()) {
                files.add(dirOrFile);
                getNestedFileAndDirListAll(dirOrFile, files);
            } else {
                files.add(dirOrFile);
            }
        }

        return files;
    }

    /**
     * 경로변환. (원본 base.)
     *
     * @param file file 파일객체.
     * @param entryFile 엔트리 파일 객체.
     * @return 파일 경로.
     */
    private static String toRelativePath(File file, File entryFile) {
        String relativePath = entryFile.getAbsolutePath().substring(
                file.getAbsolutePath().length());
        if (relativePath.length() == 0) {
            relativePath = entryFile.getName();
        }

        if (entryFile.isDirectory() && !relativePath.endsWith("/")) {
            relativePath += "/";
        }
        return relativePath.replace(File.separatorChar, '/').replaceFirst("^/", "");
    }

    /**
     * 파일 압축. (zip.)
     *
     * @param path  경로.
     * @return 압축이 성공했을 경우 true.
     */
    public static boolean compressZip(String path) {
        return compressZip(path, null, null);
    }

    /**
     * 파일 압축. (zip.)
     *
     * @param path 경로.
     * @param destArchivePath 압축파일 경로.
     * @return 압축이 성공했을 경우 true.
     */
    public static boolean compressZip(String path, String destArchivePath) {
        return compressZip(path, destArchivePath, null);
    }

    /**
     * 파일 압축. (zip.)
     *
     * @param path 경로.
     * @param destArchivePath 압축파일 경로.
     * @param encoding 인코딩.
     * @return 압축이 성공했을 경우 true.
     * @see ZipArchiveOutputStream, ZipArchiveEntry
     */
    public static boolean compressZip(String path, String destArchivePath, final String encoding) {
        return processCompress(new CompressCallBack() {
            public ArchiveOutputStream createArchiveOutputStream(OutputStream os) {
                ZipArchiveOutputStream zos = new ZipArchiveOutputStream(os);
                zos.setEncoding(encoding);
                return zos;
            }

            public ArchiveEntry createArchiveEntry(File targetDirFile, File entryFile) {
                ZipArchiveEntry entry = new ZipArchiveEntry(toRelativePath(
                        targetDirFile, entryFile));
                entry.setSize(entryFile.length());
                return entry;
            }
        }, path, destArchivePath, "zip");
    }

    /**
     * 파일 압축. (jar.)
     *
     * @param path 경로.
     * @return 압축이 성공했을 경우 true.
     */
    public static boolean compressJar(String path) {
        return compressJar(path, null, null);
    }

    /**
     * 파일 압축. (jar.)
     *
     * @param path 경로.
     * @param destArchivePath 압축파일 경로.
     * @return 압축이 성공했을 경우 true.
     */
    public static boolean compressJar(String path, String destArchivePath) {
        return compressJar(path, destArchivePath, null);
    }

    /**
     * 파일 압축. (jar.)
     *
     * @param path 경로.
     * @param destArchivePath 압축파일 경로.
     * @param encoding 인코딩.
     * @return 압축이 성공했을 경우 true.
     * @see JarArchiveOutputStream, JarArchiveEntry
     */
    public static boolean compressJar(String path, String destArchivePath, final String encoding) {
        return processCompress(new CompressCallBack() {
            public ArchiveOutputStream createArchiveOutputStream(OutputStream os) {
                JarArchiveOutputStream jos = new JarArchiveOutputStream(os);
                jos.setEncoding(encoding);
                return jos;
            }

            public ArchiveEntry createArchiveEntry(File targetDirFile,
                                                   File entryFile) {
                JarArchiveEntry entry = new JarArchiveEntry(toRelativePath(
                        targetDirFile, entryFile));
                entry.setSize(entryFile.length());
                return entry;
            }
        }, path, destArchivePath, "jar");
    }

    /**
     * 파일 압축. (tar.)
     *
     * @param path 경로.
     * @return 압축이 성공했을 경우 true.
     */
    public static boolean compressTar(String path) {
        return compressTar(path, null);
    }

    /**
     * 파일 압축. (tar.)
     *
     * @param path 경로.
     * @param destArchivePath 압축파일 경로.
     * @return 압축이 성공했을 경우 true.
     * @see TarArchiveOutputStream, TarArchiveEntry
     */
    public static boolean compressTar(String path, String destArchivePath) {
        return processCompress(new CompressCallBack() {
            public ArchiveOutputStream createArchiveOutputStream(OutputStream os) {
                TarArchiveOutputStream tos = new TarArchiveOutputStream(os);
                return tos;
            }

            public ArchiveEntry createArchiveEntry(File targetDirFile,
                                                   File entryFile) {
                TarArchiveEntry entry = new TarArchiveEntry(toRelativePath(
                        targetDirFile, entryFile));
                entry.setSize(entryFile.length());
                return entry;
            }
        }, path, destArchivePath, "tar");
    }

    /**
     * 파일 압축. (gzip.)
     *
     * @param file 압축할 파일 객체.
     * @param destArchivePath 압축파일 경로.
     * @return 압축된 파일 객체.
     * @throws CompressorException
     * @throws IOException
     */
    public static File compressGzip(File file,
                                    String destArchivePath) throws CompressorException, IOException {

        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);

        File gzipFile = createDestArchiveFile(file,
                destArchivePath, "gz");
        FileOutputStream fos = new FileOutputStream(gzipFile);

        CompressorOutputStream cos = new CompressorStreamFactory()
                .createCompressorOutputStream(CompressorStreamFactory.GZIP, fos);

        IOUtils.copy(bis, cos);

        if (cos != null) {
            cos.flush();
        }

        IOUtils.closeQuietly(cos);
        IOUtils.closeQuietly(fos);
        IOUtils.closeQuietly(bis);
        IOUtils.closeQuietly(fis);

        return gzipFile;
    }

    /**
     * 파일 압축. (tar.gz)
     *
     * @param path 경로.
     * @return 압축이 성공했을 경우 true.
     */
    public static boolean compressTarGz(String path) {
        return compressTarGz(path, null);
    }

    /**
     * 파일 압축. (tar.gz)
     *
     * @param path 경로.
     * @param destTarGzPath 압축파일 경로.
     * @return 압축이 성공했을 경우 true.
     */
    public static boolean compressTarGz(final String path, final String destTarGzPath) {
        return processIO(new IOCallback<Boolean>() {
            public Boolean doInProcessIO() throws IOException, Exception {
                File targetDirFile = new File(path);
                File targetTarFile = null;

                if (destTarGzPath == null) {
                    targetTarFile = new File(targetDirFile.getAbsoluteFile()
                            .getParentFile(), targetDirFile.getName() + ".tar");
                } else {
                    targetTarFile = new File(new File(destTarGzPath)
                            .getAbsoluteFile().getParentFile()
                            .getAbsolutePath(), targetDirFile.getName()
                            + ".tar");
                }

                if (!compressTar(path, targetTarFile.getAbsolutePath())) {
                    return false;
                }

                compressGzip(targetTarFile, destTarGzPath);

                return true;
            }
        });
    }

//    /**
//     * ar 압축.
//     *
//     * @param filePath 파일 경로.
//     * @return 압축이 성공한 경우 true.
//     */
//    public static boolean compressAr(String filePath) {
//        return compressAr(filePath, null);
//    }

//    /**
//     * ar 압축.
//     *
//     * @param filePath 파일 경로.
//     * @param destArchivePath 압축한 파일을 위치시킬 경로.
//     * @return 압축이 성공한 경우 true.
//     * @see ArArchiveOutputStream, ArchiveEntry
//     */
//    public static boolean compressAr(String filePath, String destArchivePath) {
//        final boolean isUnix = DefaultScriptExecutor.getOsType().is(OsType.Unix);
//
//        return processCompress(new CompressCallBack() {
//            public ArchiveOutputStream createArchiveOutputStream(OutputStream os) {
//                ArArchiveOutputStream aros = new ArArchiveOutputStream(os);
//                return aros;
//            }
//
//            public ArchiveEntry createArchiveEntry(File targetDirFile,
//                                                   File entryFile) {
//                return new ArArchiveEntry(toRelativePath(targetDirFile,
//                        entryFile), isUnix && entryFile.isDirectory() ? 0
//                        : entryFile.length());
//            }
//        }, filePath, destArchivePath, "ar");
//    }

    /**
     * Cpio 압축.
     *
     * @param filePath 파일 경로.
     * @return 압축 성공시 true.
     */
    public static boolean compressCpio(String filePath) {
        return compressCpio(filePath, null);
    }

    /**
     * Cpio 압축.
     *
     * @param filePath target directory
     * @param destArchivePath destination archive
     * @return true if compress process is successful, false if not
     * @see CpioArchiveOutputStream, CpioArchiveEntry
     */
    public static boolean compressCpio(String filePath, String destArchivePath) {
        return processCompress(new CompressCallBack() {
            public ArchiveOutputStream createArchiveOutputStream(OutputStream os) {
                CpioArchiveOutputStream cpioos = new CpioArchiveOutputStream(os);
                return cpioos;
            }

            public ArchiveEntry createArchiveEntry(File targetDirFile,
                                                   File entryFile) {
                CpioArchiveEntry entry = new CpioArchiveEntry(toRelativePath(
                        targetDirFile, entryFile));
                if (entryFile.isDirectory()) {
                    entry.setMode(CpioConstants.C_ISDIR);
                } else {
                    entry.setSize(entryFile.length());
                    entry.setMode(CpioConstants.C_ISREG);
                }
                return entry;
            }
        }, filePath, destArchivePath, "cpio");
    }
}