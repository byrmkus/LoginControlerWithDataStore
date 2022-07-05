package com.usta.logincontrollerwithdatastore.framework.dataStoreManager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import org.reactivestreams.Subscription;

import io.reactivex.FlowableSubscriber;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class DataStoreManager {

    public static final DataStoreManager instance = new DataStoreManager();
    private static final Handler dataStoreManagerHandler = new Handler(Looper.getMainLooper());
    private RxDataStore<Preferences> dataStore;

    private DataStoreManager() {
    }

    /**
     * should call this before use
     */
    public void init(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, "whatever_name.datastore").build();
    }

    /**
     * save String type
     *
     * @param keyName
     * @param value
     */

    public void saveValue(String keyName, String value) {
        Preferences.Key<String> key = new Preferences.Key<>(keyName);

        dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            String currentKey = preferences.get(key);

            /*
            Preferences.Key key always return null when first time
            so call saveValue again, any on know how to fix it...?
             */

            if (currentKey == null) {
                saveValue(keyName, value);
            }
            mutablePreferences.set(key, currentKey != null ? value : ""); // default value ""

            return Single.just(mutablePreferences);
        }).subscribe();
    }

    /**
     * save Boolean type
     *
     * @param keyName
     * @param value
     */

    public void saveValue(String keyName, Boolean value) {
        Preferences.Key<Boolean> key = new Preferences.Key<>(keyName);

        dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            Boolean currentKey = preferences.get(key);

            /*
            Preferences.Key key always return null when first time
            so call saveValue again, any on know how to fix it...?
             */

            if (currentKey == null) {
                saveValue(keyName, value);
            }
            mutablePreferences.set(key, currentKey != null ? value : false); // default value false

            return Single.just(mutablePreferences);
        }).subscribe();
    }

    public void getStringValue(String keyName, StringValueDelegate stringValueDelegate) {
        Preferences.Key<String> key = new Preferences.Key<>(keyName);

        dataStore.data().map(preferences -> preferences.get(key))
                .subscribeOn(Schedulers.newThread())
                .subscribe(new FlowableSubscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);
                    }

                    @Override
                    public void onNext(String s) {
                        dataStoreManagerHandler.post(() -> stringValueDelegate.onGetvalue(s));
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getBooleanValue(String keyName, BooleanValueDelegate booleanValueDelegate) {
        Preferences.Key<Boolean> key = new Preferences.Key<>(keyName);

        dataStore.data().map(preferences -> preferences.get(key))
                .subscribeOn(Schedulers.newThread())
                .subscribe(new FlowableSubscriber<Boolean>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);
                    }

                    @Override
                    public void onNext(Boolean s) {
                        dataStoreManagerHandler.post(() -> booleanValueDelegate.onGetvalue(s));
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public interface StringValueDelegate {
        void onGetvalue(String s);
    }

    public interface BooleanValueDelegate {
        void onGetvalue(Boolean s);
    }
}
