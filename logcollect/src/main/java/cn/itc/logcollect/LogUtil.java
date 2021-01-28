package cn.itc.logcollect;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import cn.itc.logcollect.util.CrashHandle;
import cn.itc.logcollect.util.FileUtil;
import cn.itc.logcollect.util.Level;
import cn.itc.logcollect.util.LogConfig;
import cn.itc.logcollect.util.TimeUtil;


public class LogUtil {
    private static final String TAG = "LogUtil";
    private static LogConfig logConfig;
    /** 日志时间工具 */
    private static ThreadLocal<SimpleDateFormat> threadLocal;
    /** 全局上下文 */
    private Application application;
    /** 是否控制台输出，默认不输出 */
    private boolean printInConsole;

    private static class LogUtilHolder {
        private static LogUtil INSTANCE = new LogUtil();
    }

    public static LogUtil getInstance() {
        return LogUtilHolder.INSTANCE;
    }

    /** 初始化 */
    public void init(Application context, boolean printInConsole) {
        //初始化时间工具
        if (threadLocal == null) {
            threadLocal = new ThreadLocal<>();
        }

        this.application = context;
        this.printInConsole = printInConsole;

        LogConfig logConfig = readLogConfigFromFile();
        if (logConfig == null) {
            //初始化配置项
            LogUtilHolder.INSTANCE.logConfig = new LogConfig(TimeUtil.getCurrentTimeStamp(), 0,
                    TimeUtil.getCurrentTimeStamp(), 0);
            saveConfigMsg();
        } else {
            LogUtilHolder.INSTANCE.logConfig = logConfig;
        }

        /** 初始化文件夹配置 */
        initUtilFolder();
        saveConfigMsg();

        /** 初始化本地奔溃工具 */
        CrashHandle.getInstance().init(this.application);
    }

    /**
     * 储存配置信息
     */
    public void saveConfigMsg() {
        Log.e("edwincheng", "saveConfigMsg: " );
        File file = new File(LogConfig.ConfigPath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            Gson gson = new Gson();
            FileUtil.overrideToFile(LogConfig.ConfigPath, (new Gson()).toJson(LogUtilHolder.INSTANCE.logConfig));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取配置信息
     * @return
     */
    public LogConfig readLogConfigFromFile() {
        File file = new File(LogConfig.ConfigPath);
        if (!file.exists()) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(FileUtil.readFromFile(LogConfig.ConfigPath), LogConfig.class) ;
    }


    /** 初始化工具必要的文件夹 */
    private void initUtilFolder() {
        File file;
        file = new File(LogConfig.parentPath);
        if (!file.exists()){
            file.mkdir();
        }

        file = new File(LogConfig.optFolderPath);
        if (!file.exists()){
            file.mkdir();
        }

        file = new File(LogConfig.networkFolderPath);
        if (!file.exists()){
            file.mkdir();
        }

        file = new File(LogConfig.crashFolderPath);
        if (!file.exists()){
            file.mkdir();
        }

        file = new File(LogConfig.optFolderPath);
        if (!file.exists()){
            file.mkdir();
        }
    }


    /**
     * 输出操作流程
     * @param tag 自定义标志位
     * @param level 等级，related to {Level.class}
     * @param message 信息
     */
    public void print(String tag, String level, String message) {
        if (logConfig == null) {
            return;
        }

        /** 优先输出控制台 */
        if (this.printInConsole) {
            switch (level) {
                case Level.DEBUG:
                    Log.d(tag, message);
                    break;
                case Level.INFO:
                    Log.i(tag, message);
                    break;
                case Level.WARNING:
                    Log.w(tag, message);
                    break;
                case Level.ERROR:
                    Log.e(tag, message);
                    break;
                case Level.VERBOSE:
                    Log.v(tag, message);
                    break;
            }
        }
        writeToLocalOperationFile(tag, level, message);
    }

    /**
     * 输出网络监听
     * @param tag 自定义标志位
     * @param level 等级，related to {Level.class}
     * @param message 信息
     */
    public void printNetWorkMsg(String tag, String level, String message) {
        if (logConfig == null) {
            return;
        }

        /** 优先输出控制台 */
        if (this.printInConsole) {
            Log.d(tag, message);
        }

        writeToLocalNetWorkFile(tag, level, message);
    }

    /**
     * 写入本地文件
     * @param tag 自定义标志
     * @param level 等级
     * @param message 信息
     */
    private void writeToLocalOperationFile(String tag, String level, String message) {
        String filePath = LogConfig.optFolderPath + logConfig.getOperationFileTimeStamp() + ".txt";

        String sb = getFormatTime() +  " [" + tag + "]" +
            " [" + level + "] " + message;
        FileUtil.writeToFile(filePath, sb);
        selfCheckFileSize(filePath, 1);
    }

    /**
     * 写入本地网络监听文件
     * @param tag 自定义标志
     * @param level 等级
     * @param message 信息
     */
    private void writeToLocalNetWorkFile(String tag, String level, String message) {
        String filePath = LogConfig.networkFolderPath + logConfig.getNetworkFileTimeStamp() + ".txt";
        String sb = getFormatTime() +  " [" + tag + "]" +
                " [" + level + "] " + message;
        FileUtil.writeToFile(filePath, sb);

        selfCheckFileSize(filePath, 2);
    }

    //////////////////////// Others /////////////////////////
    @SuppressLint("SimpleDateFormat")
    public String getFormatTime() {
        //new SimpleDateFormat这个东西太费性能了,ThreadLocal优化下
        SimpleDateFormat sdf = threadLocal.get();
        if (sdf == null) {
            //精确到毫秒级别
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            threadLocal.set(sdf);
        }
        return sdf.format(new Date());
    }

    /**
     *
     * @param filePath
     * @param style 1.操作序列   2.网络数据
     */
    public void selfCheckFileSize(String filePath, int style) {
        try {
            File file;
            switch (style) {
                case 1:
                    file = new File(filePath);

                    if (file.length() >= 2 * 1024 * 1024) {
                        //TODO：新建文件夹
                        logConfig.setOperationFileSize(0);
                        logConfig.setOperationFileTimeStamp(TimeUtil.getCurrentTimeStamp());

                        file = new File(LogConfig.optFolderPath + logConfig.getOperationFileTimeStamp());
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        checkFileNum(file.getParentFile());

                    } else {
                        logConfig.setOperationFileSize(file.length());
                    }
                    break;
                case 2:
                    file = new File(filePath);

                    if (file.length() >= 2 * 1024 * 1024) {
                        //TODO：新建文件夹
                        logConfig.setNetworkFileSize(0);
                        logConfig.setNetworkFileTimeStamp(TimeUtil.getCurrentTimeStamp());

                        file = new File(LogConfig.networkFolderPath + logConfig.getNetworkFileTimeStamp());
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        checkFileNum(file.getParentFile());

                    } else {
                        logConfig.setNetworkFileSize(file.length());
                    }
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 检查文件数量是否超过20个
     * @param folderFile
     */
    private void checkFileNum(File folderFile) {
        //判断序列化文件个数是否超过20个
        if (folderFile != null && folderFile.isDirectory()) {
            File[] fileLists = folderFile.listFiles();

            if (fileLists.length == 20) {
                Arrays.sort(fileLists, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        long diff = o1.lastModified() - o2.lastModified();
                        if (diff > 0) {
                            return 1;
                        } else if (diff == 0) {
                            return 0;
                        } else {
                            //如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
                            return -1;
                        }
                    }
                });
                //TODO:删除第一个最早编辑的文件
                FileUtil.removeFile(fileLists[0].getAbsolutePath());
            }
        }
    }
}
