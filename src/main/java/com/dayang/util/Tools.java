package com.dayang.util;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * describe:
 * 工具类
 *
 * @author 230257
 * @date 2020/07/17
 */
public class Tools {

    /**
     * 按行写入文本内容
     *
     * @param fileName 文件名
     * @param content  内容
     */
    public static void write(String fileName, String content) {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content + "\r\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建文件
     *
     * @param filePath 文件路径
     * @return 文件的绝对路径
     * @throws IOException
     */
    public static String createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 返回url的集合
     * @param fileName 文件的绝对路径
     * @return url的集合
     */
    public static List<String> getTextContent(String fileName) {
        List<String> list = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            InputStream in = resource.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bf = new BufferedReader(reader);
            //按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                list.add(str);
            }
            bf.close();
            reader.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
