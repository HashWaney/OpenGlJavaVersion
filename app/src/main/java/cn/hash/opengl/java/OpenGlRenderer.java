package cn.hash.opengl.java;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import org.w3c.dom.Text;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.hash.opengl.java.util.TextResourceReader;

import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Created by Hash on 2020-04-08.
 */


public class OpenGlRenderer implements GLSurfaceView.Renderer {

    private Context context;

    public OpenGlRenderer(Context context) {
        this.context = context;
    }

    //when surface create invoke this function, maybe device wakeup or
    // switch back form another activity this function also be invoked.
    // so this function can invoke at least once
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        glClearColor(1.0f, 0f, 0f, 0f);

    }

    // when surface change size , vertical or horizontal switch surface size
    // change so this function invoke
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //set the opengl viewport to fill the entire surface
        glViewport(0, 0, width, height);
        Log.i("Hash", "width: " + width + " height:" + height);


    }

    //when draw per frame this function invoke,
    //also we should draw some thing, even if clear screen
    @Override
    public void onDrawFrame(GL10 gl) {
        //clear the  rendering surface  and invoke the glClearColor to fill the screen
        glClear(GLES20.GL_COLOR_BUFFER_BIT);
        String fragment_shader_source_code = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragmet_shader);
        String vertex_shader_source_code = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);





    }
}
