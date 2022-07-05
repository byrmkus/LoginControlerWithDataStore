package com.usta.logincontrollerwithdatastore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.usta.logincontrollerwithdatastore.databinding.ActivityMainBinding;
import com.usta.logincontrollerwithdatastore.framework.dataStoreManager.DataStoreManager;
import com.usta.logincontrollerwithdatastore.framework.util.ThreadUtil;
import com.usta.logincontrollerwithdatastore.login.controller.LoginController;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DataStoreManager.instance.init(this);
        setListener();
        checkShouldAutoSignInWhenAppStart();
    }

    private void setListener() {
        binding.buttonLogin.setOnClickListener(view -> {
            login();
        });

        binding.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                Log.d("??", "should auto sign in");
            } else {
                Log.d("??", "should not auto sign in");

            }
            DataStoreManager.instance.saveValue("shouldAutoSignIn", b);

        });
    }

    private void checkShouldAutoSignInWhenAppStart() {
        DataStoreManager.instance.getBooleanValue("shouldAutoSignIn", b -> {
            if (b) {
                binding.checkBox.setChecked(true);
                DataStoreManager.instance.getStringValue("token", this::requestUserInfo);
            }else {
                binding.checkBox.setChecked(false);
            }
        });
    }

    private void login() {
        String id = binding.editTextTextEmailAddress.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "Input id!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        String pw = binding.editTextPassword.getText().toString();
        if (pw.isEmpty()) {
            Toast.makeText(this, "input password!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress();

        // this method run on thread
        LoginController.instance.login(id, pw, new LoginController.LoginControllerDelegate() {
            @Override
            public void onLoginSuccess(String token) {

                ThreadUtil.startUIThread(0, () -> {
                    Toast.makeText(MainActivity.this, "login success", Toast.LENGTH_SHORT).show();
                    //save token to data store
                    DataStoreManager.instance.saveValue("token", token);
                    //back on main thread
                    requestUserInfo(token);

                });
            }

            @Override
            public void onLoginError() {
                ThreadUtil.startUIThread(0, () -> {
                    hideProgress();
                    Toast.makeText(MainActivity.this, "login error, check your id or password!!!", Toast.LENGTH_SHORT).show();
                });

            }
        });

    }

    private void requestUserInfo(String token) {
        showProgress();
        LoginController.instance.requestUserInfo(token, new LoginController.UserInfoResponseDelegate() {
            @Override
            public void onSuccess(String nickname, String info) {
                ThreadUtil.startUIThread(0, () -> {
                    hideProgress();
                    binding.textInfo.setText(info);
                    binding.textNickName.setText(nickname);

                });
            }

            @Override
            public void onError() {
                ThreadUtil.startUIThread(0, () -> {
                    hideProgress();
                    Toast.makeText(MainActivity.this, "request user info error, check server!!!", Toast.LENGTH_SHORT).show();
                });

            }
        });
    }

    private void showProgress() {
        binding.frameLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        binding.frameLayout.setVisibility(View.GONE);
    }
}