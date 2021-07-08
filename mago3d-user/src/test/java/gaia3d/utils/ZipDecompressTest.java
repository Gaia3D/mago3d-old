package gaia3d.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ZipDecompressTest {

    private static final int BUFFER = 2048;

    @Test
    void test() throws Exception {
        String source = "d:\\test\\source\\3.zip";
        String dest = "d:\\test\\dest\\";

        ZipUtils.decompressZip(source, dest, "CP949");
    }
}
