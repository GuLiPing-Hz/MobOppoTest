package com.example.myapplication1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.VerifyResult;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionSDWithIMEI();
            }
        });

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mob.mobverify.Helper.uTest1JNI(MainActivity.this);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecVerify.preVerify(new PreVerifyCallback() {
                    @Override
                    public void onComplete(Void data) {
                        //TODO处理成功的结果
                        SecVerify.verify(new VerifyCallback() {
                            @Override
                            public void onOtherLogin() {
                                Log.i(TAG, "用户点击“其他登录方式”，处理自己的逻辑");
                            }

                            @Override
                            public void onUserCanceled() {
                                Log.i(TAG, "用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑");
                            }

                            @Override
                            public void onComplete(VerifyResult data) {
                                // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
                                //这里使用data.getOpToken()把获取到的token搭配服务端API进行验证

                                Log.i(TAG, "获取授权码成功=" + data.toJSONString());
                            }

                            @Override
                            public void onFailure(VerifyException e) {
                                //TODO处理失败的结果
                                Log.i(TAG, "登录失败");
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void onFailure(VerifyException e) {
                        //TODO处理失败的结果
                        Log.i(TAG, "预先登录失败");
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {

        }
    }

    //android6.0之后要动态获取权限
    void checkPermissionSDWithIMEI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Storage Permissions
            String permission = Manifest.permission.ACCESS_NETWORK_STATE;
//            permission = Manifest.permission.ACCESS_WIFI_STATE;
//            permission = Manifest.permission.CHANGE_NETWORK_STATE;
//            permission = Manifest.permission.CHANGE_WIFI_MULTICAST_STATE;
//            permission = Manifest.permission.CHANGE_WIFI_STATE;
//            permission = Manifest.permission.INTERNET;
            permission = Manifest.permission.READ_PHONE_STATE;
            String[] PERMISSIONS = {permission};

            //检测是否有写的权限
            int permission1 = ActivityCompat.checkSelfPermission(this, permission);
            if (permission1 != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                Log.i(TAG, "check permission=" + permission);
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE);
            }
        }

        MobSDK.submitPolicyGrantResult(true, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void aVoid) {
                Log.i(TAG, "隐私同意成功");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "隐私同意失败");
                throwable.printStackTrace();
            }
        });
    }
}
