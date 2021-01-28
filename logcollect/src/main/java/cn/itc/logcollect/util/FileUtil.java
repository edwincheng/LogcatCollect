package cn.itc.logcollect.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;

public class FileUtil {

    /**
     * 这个路径被Android系统认定为应用程序的缓存路径，当程序被卸载的时候，这里的数据也会一起被清除掉
     *
     * @return 路径例如： /SD卡/Android/data/程序的包名/cache/uniqueName
     */
    public static String getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath + File.separator + uniqueName;
    }

    /**
     * 写入文件、追加
     *
     * @param filePath
     * @param content
     */
    public static void writeToFile(String filePath, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(filePath, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeUTF(content + "\r\n");
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入文件、覆盖
     *
     * @param filePath
     * @param content
     */
    public static boolean overrideToFile(String filePath, String content) {
        PrintStream stream = null;
        try {

            stream=new PrintStream(filePath);//写入的文件path
            stream.print(content);//写入的字符串
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取文件内容，默认使用UTF-8编码
     * @param filePath
     * @return
     */
    public static String readFromFile(String filePath) {
        StringBuffer stringBuffer = new StringBuffer();//用来拼接字符串
        try {
            //获取文件
            File file = new File(filePath);
            //获取文件的读取器
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF-8"));
            //读取文件
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                stringBuffer.append(tempStr+"\n");//[\n]换行
            }
            //关闭读取器
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //返回
        return stringBuffer.toString();
    }


    /**
     * 删除文件
     * @param filePath
     */
    public static void removeFile(String filePath) {
        //获取文件
        File file = new File(filePath);
        if (file != null) {

            if (file.isFile()) {
                file.delete();
            }
        }
    }

}