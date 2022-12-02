package com.quico.tech;

import android.app.Application;


public class ThisApplication extends Application {

    private static ThisApplication mInstance;
    //private SharedPref sharedPref;
    //private FirebaseAnalytics mFirebaseAnalytics;

    private int fcm_count = 0;
    private final int FCM_MAX_COUNT = 10;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //sharedPref = new SharedPref(this);

        // initialize firebase
        //FirebaseApp.initializeApp(this);

    }

    private void subscribeToTopicForNotification() {

//        if (NetworkCheck.isConnect(this) && sharedPref.isNeedRegister()) {
//            fcm_count++;
//
//
//            FirebaseMessaging.getInstance().subscribeToTopic("news").addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    sharedPref.setNeedRegister(false);
//                }
//
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    if (fcm_count > FCM_MAX_COUNT) return;
//                        subscribeToTopicForNotification();
//                }
//            });
//
//        }
    }

}
