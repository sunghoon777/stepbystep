package com.stepbystep.foodtruck;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application{
    private static GlobalApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        KakaoSdk.init(this, "6232353e6f5320d1e435a8513a004b3e");
    }

}
