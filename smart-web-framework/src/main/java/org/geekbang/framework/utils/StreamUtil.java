package org.geekbang.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 流操作工具类
 */
public final class StreamUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamUtil.class);

    public static String getString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String         line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("get string from stream failure", e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    /**
     * 将输入流 复制 到输出流中
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     */
    public static void copyStream(InputStream inputStream, OutputStream outputStream) {
        int    length;
        byte[] buffer = new byte[4 * 1024];

        try {
            // length: the total number of bytes read into the buffer, or
            //      *             <code>-1</code> if there is no more data because the end of
            //      *             the stream has been reached.
            while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error(" copy stream failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                LOGGER.error(" close stream failure", e);
            }
        }
    }
}
