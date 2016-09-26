package com.sobattani.app;

import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.sobattani.helper.MyPreferenceManager;
import org.json.JSONObject;

public class MyApplication extends MultiDexApplication {

    public static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication mInstance;
    private MyPreferenceManager pref;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .autoPromptLocation(true)
                .init();
    }

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey;

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

            // The following can be used to open an Activity of your choice.

            // Intent intent = new Intent(getApplication(), YourActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);

            // Follow the instructions in the link below to prevent the launcher Activity from starting.
            // https://documentation.onesignal.com/docs/android-notification-customizations#changing-the-open-action-of-a-notification
        }
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }
        return pref;
    }
}
