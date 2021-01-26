package cn.itc.logcollect.util;

import android.app.Application;

public class LogConfig {
    /** 父文件储存路径 */
    public final static String parentPath = "/sdcard/Android/LogUtil/";
    /** 操作记录文件夹路径 */
    public final static String optFolderPath = parentPath + "opt/";
    /** 网络记录文件夹路径 */
    public final static String networkFolderPath = parentPath + "network/";
    /** 崩溃日志采集文件夹路径 */
    public final static String crashFolderPath = parentPath + "crash/";
    /** 配置文件路径 */
    public final static String ConfigPath = parentPath + "config.txt/";
    /** 全局上下文 */
    private Application application;
    /** 是否控制台输出，默认不输出 */
    private boolean printInConsole;
    /** 当前定义的网络访问文件夹后缀时间戳  */
    private long operationFileTimeStamp;
    private long operationFileSize;
    /** 当前定义的网络访问文件夹后缀时间戳 */
    private long networkFileTimeStamp;
    private long networkFileSize;

    public LogConfig(Application application, long operationFileTimeStamp, long operationFileSize, long networkFileTimeStamp, long networkFileSize) {
        this.application = application;
        this.operationFileTimeStamp = operationFileTimeStamp;
        this.operationFileSize = operationFileSize;
        this.networkFileTimeStamp = networkFileTimeStamp;
        this.networkFileSize = networkFileSize;
    }

    public Application getApplication() {
        return application;
    }

    public boolean isPrintInConsole() {
        return printInConsole;
    }


    public long getOperationFileTimeStamp() {
        return operationFileTimeStamp;
    }

    public long getOperationFileSize() {
        return operationFileSize;
    }

    public long getNetworkFileTimeStamp() {
        return networkFileTimeStamp;
    }

    public long getNetworkFileSize() {
        return networkFileSize;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void setPrintInConsole(boolean printInConsole) {
        this.printInConsole = printInConsole;
    }

    public void setOperationFileTimeStamp(long operationFileTimeStamp) {
        this.operationFileTimeStamp = operationFileTimeStamp;
    }

    public void setOperationFileSize(long operationFileSize) {
        this.operationFileSize = operationFileSize;
    }

    public void setNetworkFileTimeStamp(long networkFileTimeStamp) {
        this.networkFileTimeStamp = networkFileTimeStamp;
    }

    public void setNetworkFileSize(long networkFileSize) {
        this.networkFileSize = networkFileSize;
    }
}
