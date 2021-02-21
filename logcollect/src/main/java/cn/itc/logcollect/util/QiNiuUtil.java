package cn.itc.logcollect.util;

import android.util.Log;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;

import org.json.JSONObject;

public class QiNiuUtil {

    /**
     * 上传文件到七牛云云储存
     * @param accessKey
     * @param secretKey
     * @param bucketName
     * @param localPath
     * @param remoteName
     */
    public static void uploadFile(String accessKey, String secretKey, String bucketName, String localPath, String remoteName,
                                  UpCompletionHandler handler) {
        Configuration config = new Configuration.Builder()
                .connectTimeout(90)              // 链接超时。默认90秒
                .useHttps(true)                  // 是否使用https上传域名
                .useConcurrentResumeUpload(true) // 使用并发上传，使用并发上传时，除最后一块大小不定外，其余每个块大小固定为4M，
                .concurrentTaskCount(3)          // 并发上传线程数量为3
                .responseTimeout(90)             // 服务器响应超时。默认90秒
                .zone(FixedZone.zone2)           // 设置区域，不指定会自动选择。指定不同区域的上传域名、备用域名、备用IP。
                .build();

        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        UploadManager uploadManager = new UploadManager(config);

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = remoteName;
        //自处理签名设计
        Auth auth = Auth.create(accessKey, secretKey);
        //上传到七牛云
        uploadManager.put(localPath, remoteName, auth.uploadToken(bucketName), handler, null);
    }

}
