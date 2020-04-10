package cn.hash.opengl.java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected GLSurfaceView myGlSurfaceView;
    protected boolean rendererSet = false;

    private OpenGlRenderer openGlRenderer;

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

        openGlRenderer = new OpenGlRenderer(this);
        myGlSurfaceView.setRenderer(openGlRenderer);
        rendererSet = true;

        myGlSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {

                    // TODO: 2020-04-10 关于归一化坐标的求法：
                    //  0<event.getX()/(float)v.getWidth() <1
                    //  0<event.getY()/(float)v.getHeight()<1
                    //  那么  x= event.getX()/(float)v.getWidth();  0<x<1  0<x*2<2  -1<x*2-1<1
                    //  同理 y=event.getY()/(float)v.getHeight(); 0<y<1   0<y*2<2  -1<y*2-1<1;
                    //  那么触摸事件坐标就转换回归一化设备坐标。还需要将y轴反转，因为空间坐标y轴是默认向上的

                    final float normalizedX = event.getX() / (float) v.getWidth() * 2 - 1;
                    Log.w("MainActivity", String.format("getX(): %f, " +
                            "getWidth(): %d, normalizedX:%f", event.getX(), v.getWidth(), normalizedX));


                    final float normalizedY = -(event.getY() / (float) v.getHeight() * 2 - 1);

                    Log.w("MainActivity", String.format("getY() :%f ," +
                            " getHeight(): %d ,normalizedY %f ", event.getY(), v.getHeight(), normalizedY));

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        myGlSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                openGlRenderer.handleTouchPress(normalizedX, normalizedY);

                            }
                        });

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        myGlSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                openGlRenderer.handleTouchDrag(normalizedX, normalizedY);
                            }
                        });

                    }
                    return true;
                }


                return false;
            }
        });

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
        if (myGlSurfaceView != null) {
            myGlSurfaceView.onResume();
            ;
        }
    }
}
