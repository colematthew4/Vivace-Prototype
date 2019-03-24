package cole.matthew.vivace;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public final class VivaceApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            // this process is dedicated to LeakCanary for heap analysis. You should not init your app in this process.
            LeakCanary.install(this);
        }
    }
}
