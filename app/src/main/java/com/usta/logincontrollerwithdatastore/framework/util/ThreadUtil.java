package com.usta.logincontrollerwithdatastore.framework.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static Handler handler = new Handler(Looper.getMainLooper());


    public static void startThread(Runnable runnable) {
        executorService.submit(runnable);
    }

    public static void startUIThread(int delayMillis, Runnable runnable) {
        handler.postDelayed(runnable, delayMillis);
    }

    @Override
    protected void finalize() throws Throwable {
        if (executorService != null && executorService.isShutdown()) {
            executorService.shutdown();
        }

        super.finalize();
    }
}
