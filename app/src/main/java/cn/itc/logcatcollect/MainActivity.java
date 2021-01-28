package cn.itc.logcatcollect;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import cn.itc.logcollect.LogUtil;
import cn.itc.logcollect.util.Level;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvOperation;
    private TextView tvNetwork;
    private TextView tvCheckFileNum;

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

    }

    private void initData() {
        tvOperation.setOnClickListener(this);
        tvNetwork.setOnClickListener(this);
        tvCheckFileNum.setOnClickListener(this);

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
                LogUtil.getInstance().printNetWorkMsg(getLocalClassName(), Level.DEBUG, "测试网路消息");
                break;
            case R.id.tv_check_file_num:
                break;
        }
    }
}