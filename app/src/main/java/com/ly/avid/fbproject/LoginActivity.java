package com.ly.avid.fbproject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hola.account.HolaLogin;
import com.sober.utils.LogUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Holaverse on 2017/1/22.
 */

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    private Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                LogUtil.showMsg(mContext, Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        findViewById(R.id.btn_fb_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"/*, "user_friends"*/));
            }
        });

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                HolaLogin.facebookLogin(LoginActivity.this, "test_facebook", loginResult.getAccessToken().getUserId(), loginResult.getAccessToken().getToken());
//                HolaLogin.guestLogin(LoginActivity.this, "test_guest");
//                HolaLogin.portalLogin(LoginActivity.this, "test_portal", "portal_id");

                LogUtil.showMsg(mContext, "Facebook login success. Token:" + loginResult.getAccessToken().getToken());
                startActivity(new Intent(mContext, MainActivity.class));
            }

            @Override
            public void onCancel() {
                LogUtil.showMsg(mContext, "Facebook login cancel.");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtil.showMsg(mContext, "Facebook login error. error info:" + error.getMessage());
                AccessToken.setCurrentAccessToken(null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
