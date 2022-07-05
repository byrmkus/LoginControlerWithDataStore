package com.usta.logincontrollerwithdatastore.login.controller;

import com.usta.logincontrollerwithdatastore.framework.util.ThreadUtil;

public class LoginController {

    public final static LoginController instance = new LoginController();
    private final static String DUMMY_TOKEN_FROM_SERVER = "12345as";

    private LoginController() {
    }

    public void login(String id, String password, LoginControllerDelegate loginControllerDelegate) {
        ThreadUtil.startThread(() -> {

            try {
                //let's suppose server can return in 2 seconds
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Ler's suppose id and pasword is.
            if (id.equals("bayram") && password.equals("12345as")) {
                loginControllerDelegate.onLoginSuccess(DUMMY_TOKEN_FROM_SERVER);
            } else {
                loginControllerDelegate.onLoginError();
            }
        });

    }

    public void requestUserInfo(String token, UserInfoResponseDelegate userInfoResponseDelegate) {
        ThreadUtil.startThread(() -> {

            try {
                //let's suppose server can return in 2 seconds
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (token.equals(DUMMY_TOKEN_FROM_SERVER)) {

                userInfoResponseDelegate.onSuccess("progfantasmis", "faust");

            } else {
                userInfoResponseDelegate.onError();
            }

        });

    }

    public interface LoginControllerDelegate {
        void onLoginSuccess(String token);

        void onLoginError();
    }

    public interface UserInfoResponseDelegate {
        void onSuccess(String nickname, String info);

        void onError();
    }
}

