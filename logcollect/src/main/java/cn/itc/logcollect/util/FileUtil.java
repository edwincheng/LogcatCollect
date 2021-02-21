package cn.itc.logcollect.util;

import android.content.Context;
import android.os.Environment;
import android.telecom.Conference;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    /**
     * 将存放在sourceFilePath目录下的源文件,打包成fileName名称的ZIP文件,并存放到zipFilePath
     * @param zipFilePath    压缩后存放路径
     * @param zipFileName    压缩后文件的名称
     * @return flag
     */
    public static boolean fileToZip(String zipFilePath, String zipFileName) {
        String sourceFilePath = LogConfig.parentPath;
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        if (!sourceFile.exists()) {
            System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath + " 不存在. <<<<<<");
        } else {
            try {
                File zipFile = new File(zipFilePath + "/" + zipFileName );
                if (zipFile.exists()) {
                    System.out.println(">>>>>> " + zipFilePath + " 目录下存在名字为："
                            + zipFileName  + " 打包文件. <<<<<<");
                } else {
                    File[] sourceFiles = sourceFile.listFiles();
                    if (null == sourceFiles || sourceFiles.length < 1) {
                        System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath
                                + " 里面不存在文件,无需压缩. <<<<<<");
                    } else {
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024 * 10];
                        for (int i = 0; i < sourceFiles.length; i++) {
                            // 创建ZIP实体,并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            // 读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(sourceFiles[i]);
                            bis = new BufferedInputStream(fis, 1024 * 10);
                            int read = 0;
                            while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                zos.write(bufs, 0, read);
                            }
                        }
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                // 关闭流
                try {
                    if (null != bis) {
                        bis.close();
                    }
                    if (null != zos) {
                        zos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }



}