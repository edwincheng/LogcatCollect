package cn.itc.logcollect.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandle implements Thread.UncaughtExceptionHandler {
    private Context context;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    //1.私有化构造方法
    private CrashHandle() {
    }

    private static class Singleton {
        //静态初始化器，由JVM来保证线程安全
        private static CrashHandle instance = new CrashHandle();
    }

    public static CrashHandle getInstance() {
        return Singleton.instance;
    }

    public void init(Context context) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
            // 1.获取当前程序的版本号. 版本的id
            String versioninfo = getVersionInfo();
            // 2.获取手机的硬件信息.
            String mobileInfo = getMobileInfo();
            // 3.把错误的堆栈信息 获取出来
            String errorinfo = getErrorInfo(e);
            try {
                long timestamp = System.currentTimeMillis();
                String time = dataFormat.format(new Date());
                String fileName = "crash" + time + "-" + timestamp + ".txt";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    FileOutputStream fos = new FileOutputStream(LogConfig.crashFolderPath + fileName);
                    Log.e("--异常文件创建--", FileUtil.getDiskCacheDir(context, fileName));
                    fos.write((versioninfo + mobileInfo + errorinfo).getBytes());
                    fos.close();
                }
            } catch (Exception ex) {
                Log.e("pds", "...", ex);
            }
            System.exit(0);
    }

    /**
     * 获取错误的信息
     */
    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        return writer.toString();
    }

    /**
     * 获取手机的硬件信息
     */
    private String getMobileInfo() {
        StringBuilder sb = new StringBuilder();
        //通过反射获取系统的硬件信息
        try {

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                //暴力反射 ,获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name).append("=").append(value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取手机的版本信息
     */
    private String getVersionInfo() {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }
}
