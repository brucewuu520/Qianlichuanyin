/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.brucewuu.android.qlcy.util.io;

import android.annotation.SuppressLint;
import android.database.Cursor;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.channels.Selector;
import java.nio.charset.Charset;


/**
 * General IO stream manipulation utilities.
 */
@SuppressLint("NewApi")
public final class IOUtils {
    /**
     * Instances should NOT be constructed in standard programming.
     */
    private IOUtils() {
    }

    public static void close(URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }

    // read char[]
    //-----------------------------------------------------------------------

    public static void closeQuietly(Reader input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(Writer output) {
        closeQuietly((Closeable) output);
    }

    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable) output);
    }

    // read readString
    //-----------------------------------------------------------------------

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignored) {
            // ignore
        }
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeQuietly(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {

            }
        }
    }

    public static void closeQuietly(Socket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException ignored) {
                // ignored
            }
        }
    }

    public static void closeQuietly(Selector selector) {
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException ignored) {
                // ignored
            }
        }
    }

    public static void closeQuietly(ServerSocket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException ignored) {
                // ignored
            }
        }
    }
    
    public static void closeQuietly(Cursor cursor) {
    	if(cursor != null) {
    		cursor.close();
    	}
    }

    public static String readString(String filePath, Charset charset) throws IOException {
        return readString(new File(filePath), charset);
    }

    public static String readString(String filePath, String charsetName) throws IOException {
        return readString(new File(filePath), Charsets.toCharset(charsetName));
    }

    public static String readString(File file, String charsetName) throws IOException {
        return readString(file, Charsets.toCharset(charsetName));
    }

    /**
     * read file
     *
     * @param file        file
     * @param charset The name of a supported {@link Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws IOException if an error occurs while operator BufferedReader
     */
    public static String readString(File file, Charset charset) throws IOException {
        if (file == null || !file.isFile()) {
            return null;
        }
        StringBuilder fileContent = new StringBuilder();

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charset);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent.toString();
        } finally {
            closeQuietly(reader);
        }
    }

    public static boolean writeString(String filePath, String content) throws IOException {
        return writeString(filePath, content, false);
    }

    /**
     * write file
     *
     * @param filePath
     * @param content
     * @param append   is append, if true, write to the end of file, else clear content of file and write into it
     * @return return true
     * @throws IOException if an error occurs while operator FileWriter
     */
    public static boolean writeString(String filePath, String content, boolean append) throws IOException {
        return writeString(filePath != null ? new File(filePath) : null, content, append);
    }

    public static boolean writeString(File file, String content) throws IOException {
        return writeString(file, content, false);
    }

    public static boolean writeString(File file, String content, boolean append) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } finally {
            closeQuietly(fileWriter);
        }
    }

}
