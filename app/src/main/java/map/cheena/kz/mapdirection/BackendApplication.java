package map.cheena.kz.mapdirection;

import android.app.Application;

import com.backendless.Backendless;

public class BackendApplication extends Application {


    public static final String APPLICATION_ID = "172CBB59-B551-1AC6-FFE3-CCE06C159000";
    public static final String API_KEY = "660F172C-3307-F6B4-FF7D-2B8A942A1C00";
    public static final String SERVER_URL = "https://api.backendless.com";
    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(getApplicationContext(), APPLICATION_ID, API_KEY);

    }
}
