package cn.itc.logcatcollect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import cn.itc.logcollect.LogUtil;
import cn.itc.logcollect.UploadListener;
import cn.itc.logcollect.util.Level;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvOperation;
    private TextView tvNetwork;
    private TextView tvCheckFileNum;
    private TextView tvUploadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PowerUtil.verifyStoragePermissions(this);

        initView();
        initData();
    }

    private void initView() {
        tvOperation = findViewById(R.id.tv_operation);
        tvNetwork = findViewById(R.id.tv_network);
        tvCheckFileNum = findViewById(R.id.tv_check_file_num);
        tvUploadFile = findViewById(R.id.tv_upload_file);

    }

    private void initData() {
        tvOperation.setOnClickListener(this);
        tvNetwork.setOnClickListener(this);
        tvCheckFileNum.setOnClickListener(this);
        tvUploadFile.setOnClickListener(this);

        if(PowerUtil.checkIsStoragePermissionsGranted(this,1011)){
            LogUtil.getInstance().init(getApplication(), false);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_operation:
                LogUtil.getInstance().print(getLocalClassName(), Level.DEBUG, "测试操作消息");
                break;
            case R.id.tv_network:
                LogUtil.getInstance().printNetWorkMsg(getLocalClassName(), Level.SEND, "测试网路消息");
                break;
            case R.id.tv_check_file_num:
                break;
            case R.id.tv_upload_file:
                String accessKey = "";
                String secretKey = "";
                String bucket = "";

                LogUtil.getInstance().uploadToQiNiuYun(accessKey, secretKey, bucket, "云控",
                        new UploadListener() {
                            @Override
                            public void uploadResult(boolean result, String msg) {
                                if (result) {
                                    Log.i("upload", "success");
                                } else {
                                    Log.i("upload", "fail ");
                                }
                            }
                        });
                break;
        }
    }
}