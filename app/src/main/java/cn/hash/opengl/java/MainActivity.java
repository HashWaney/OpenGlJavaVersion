package cn.hash.opengl.java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected GLSurfaceView myGlSurfaceView;
    protected boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1。 init GLSurfaceView instance

        myGlSurfaceView = new GLSurfaceView(this);


        //1.2 check for support Es2
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        boolean supportEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        if (!supportEs2) {
            Toast.makeText(this,
                    "This device is not support Opengl es2 ", Toast.LENGTH_SHORT).show();
            return;
        }

        myGlSurfaceView.setEGLContextClientVersion(2);
        myGlSurfaceView.setRenderer(new OpenGlRenderer());
        rendererSet = true;

        //2. set GlSurfaceView  to ContentView
        setContentView(myGlSurfaceView);
    }


    @Override
    protected void onPause() {
        super.onPause();
        //to order activity lifecycle  so can avoid crash
        if (myGlSurfaceView != null) {
            myGlSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myGlSurfaceView!=null)
        {
            myGlSurfaceView.onResume();;
        }
    }
}
