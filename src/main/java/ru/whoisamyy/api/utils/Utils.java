package ru.whoisamyy.api.utils;

import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.whoisamyy.api.gd.objects.Account;
import ru.whoisamyy.api.gd.objects.Level;
import ru.whoisamyy.core.Core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static Path resources = createPath();

    public static Logger logger = Core.logger;
    public static boolean emailRegex(final String input) {
        final Pattern pattern = Pattern.compile("[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[A-Za-z0-9](?:[-A-Za-z0-9]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[-A-Za-z0-9]*[A-Za-z0-9])?", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private static Path createPath() {
        //Path res = Paths.get(Utils.class.getResource("/").toString().substring(6)+"/main/resources"); //fakin shit
        //Path res = Paths.get("/core/data");
        File file = new File("core");
        Path res = file.toPath();
        return res;
    }

    public static void createDirs() {
        File file = new File(Level.getLevelResourcesPath().toUri());
        file.mkdirs();
        file = new File(Account.getAccountsResourcesPath().toUri());
        file.mkdirs();
    }

    public static File createDirs(String path) throws IOException {
        File file = new File(resources.toString()+"/"+path);
        String[] pathStringArr = file.getPath().split("/");
        String s = pathStringArr[pathStringArr.length - 1];
        File file2 = new File(file.getPath().replace(s, ""));
        file2.mkdirs();
        return file2;
    }

    public static void createFile(String name, File file) throws IOException {
        file = new File(file.getPath()+name);
        file.createNewFile();
    }


    /**
     * @param file the file where data will be written
     * @param data data to be written in file
     * @return true if writing is successful and false if writing was not successful
     */
    public static boolean writeToFile(File file, String data) {
        try {
            // Проверяем, существует ли файл
            if (!file.exists()) {
                // Если файл не существует, создаем директории к файлу
                File parentDir = file.getParentFile();
                if (parentDir != null) {
                    parentDir.mkdirs();
                }
                // Создаем сам файл
                file.createNewFile();
            }

            // Преобразуем строку data в байты
            byte[] dataBytes = data.getBytes();

            // Записываем data в файл
            Files.write(file.toPath(), dataBytes, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param file the file where data will be written
     * @param data data to be written in file
     * @param b64 if true uses base64 for encoding if false does not use any encoding
     * @return true if writing is successful and false if writing was not successful
     */
    public static boolean writeToFile(File file, String data, boolean b64) {
        if (!b64) return writeToFile(file, data);
        return writeToFile(file, Base64.encodeBase64String(data.getBytes()));
    }

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(new Random().nextInt(52)+'a');
        }
        return sb.toString();
    }

    public static String SHA1(String input, String salt)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] messageDigest = md.digest((input+salt).getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        }

        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String SHA1(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] messageDigest = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        }

        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String genSolo(String levelstring) {
        String hash = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        int len = levelstring.length();
        int divided = len / 40;
        int p = 0;

        for (int k = 0; k < len; k += divided) {
            if (k >= len) {
                break;
            }
            if (p > 39) {
                break;
            }
            char character = levelstring.charAt(k);
            hash = hash.substring(0, p) + character + hash.substring(p + 1);
            p++;
        }

        String concatenatedString = hash + "xI25fpAapCQg";
        String sha1Hash = SHA1(concatenatedString);
        return sha1Hash;
    }

    public static String base64UrlSafeEncode(String data) {
        return Base64.encodeBase64URLSafeString(data.getBytes());
    }

    public static String base64UrlSafeEncode(byte[] data) {
        return Base64.encodeBase64URLSafeString(data);
    }

    public static String base64UrlSafeDecode(String data) {
        return new String(Base64.decodeBase64URLSafe(data));
    }

    public static String base64UrlSafeDecode(byte[] data) {
        return new String(Base64.decodeBase64URLSafe(new String(data)));
    }

    public static class GJP {
        public static String createGJP(String text) {
            //key for gjp xor is always 37526
            byte[] key = "37526".getBytes();
            return base64UrlSafeEncode(new String(cyclicXOR(text.getBytes(), key)));
        }

        public static String createGJPHash(String text) {
            return BCrypt.hashpw(createGJP(text), Core.SALT);
        }

        public static byte[] cyclicXOR(byte[] data, byte[] key) {
            byte[] encrypted = new byte[data.length];
            for (int i = 0; i < data.length; i++) {
                encrypted[i] = (byte) (data[i] ^ key[i % key.length]);
            }
            return encrypted;
        }

        public static String singularXOR(String input, int key) {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                output.append((char) (input.charAt(i) ^ key));
            }
            return output.toString();
        }

    }

    public static class DataDecoder {
        //робтоп даун
        public static byte[] gzdecode(byte[] compressed) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try (GzipCompressorInputStream gzis = new GzipCompressorInputStream(bais)) {
                IOUtils.copy(gzis, baos);
            }

            return baos.toByteArray();
        }


        /**
         * decodes level/account data
         * @param data data string encoded with Base64
         * @return decoded string (usually in .xml format because gmd robtop)
         * @throws IOException
         * @see DataDecoder#gzdecode(byte[])
         */
        public static String decode(String data) throws IOException {
            return new String(gzdecode(Base64.decodeBase64URLSafe(data)));
        }

        public static String decodeLevel(String data, boolean isOfficialLevel) throws IOException { //хз на сервере мне это вообще не нужно, иметь полезно, но зач
            if (isOfficialLevel) data = "H4sIAAAAAAAAA" + data;
            return decode(data);
        }

        public static String decodeAccData(String data) throws IOException {
            return decode(data);
        }
        public static byte[] decodeAccData(byte[] data) throws IOException {
            return gzdecode(data);
        }

        @Deprecated // this may not work but use carefully
        public static byte[] zlibDecompress(byte[] compressedData) throws IOException {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DeflateCompressorInputStream deflateInputStream = new DeflateCompressorInputStream(byteArrayInputStream)) {
                IOUtils.copy(deflateInputStream, byteArrayOutputStream);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static class DataEncoder {
        public static byte[] gzencode(byte[] data) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (GzipCompressorOutputStream gzipOutputStream = new GzipCompressorOutputStream(byteArrayOutputStream)) {
                IOUtils.copy(new ByteArrayInputStream(data),
                        gzipOutputStream);
            }
            return byteArrayOutputStream.toByteArray();
        }

        public static String encode(String data) throws IOException {
            return new String(gzencode(data.getBytes()));
        }

        public static String encode(String data, boolean b64) throws IOException {
            if (!b64) return encode(data);
            return encode(base64UrlSafeEncode(data));
        }

        public static String encode(byte[] data) throws IOException {
            return new String(gzencode(data));
        }

        public static String encode(byte[] data, boolean b64) throws IOException {
            if (!b64) return encode(data);
            return encode(base64UrlSafeEncode(data));
        }

        @Deprecated // this may not work but use carefully
        public static byte[] zlibCompress(byte[] data) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DeflateCompressorOutputStream deflateOutputStream = new DeflateCompressorOutputStream(byteArrayOutputStream)) {
                deflateOutputStream.write(data);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}
