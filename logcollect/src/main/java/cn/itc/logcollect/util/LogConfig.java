package cn.itc.logcollect.util;

public class LogConfig {
    /** 父文件储存路径 */
    public final static String parentPath = "/sdcard/Android/LogUtil/";
    /** 配置文件路径 */
    public final static String ConfigPath = parentPath + "config.txt/";
    /** 当前定义的网络访问文件夹后缀时间戳  */
    private long operationFileTimeStamp;
    private long operationFileSize;
    /** 当前定义的网络访问文件夹后缀时间戳 */
    private long networkFileTimeStamp;
    private long networkFileSize;

    public LogConfig(long operationFileTimeStamp, long operationFileSize, long networkFileTimeStamp, long networkFileSize) {
        this.operationFileTimeStamp = operationFileTimeStamp;
        this.operationFileSize = operationFileSize;
        this.networkFileTimeStamp = networkFileTimeStamp;
        this.networkFileSize = networkFileSize;
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
