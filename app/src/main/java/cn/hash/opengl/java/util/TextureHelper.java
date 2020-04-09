package cn.hash.opengl.java.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.security.acl.LastOwnerException;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.texImage2D;


/**
 * Created by Hash on 2020-04-09.
 */


public class TextureHelper {


    public static final String TAG = "TextureHelper";

    /**
     * 根据resourceId 生成纹理id
     *
     * @param context
     * @param resourceId
     * @return
     */
    public static int loadTexture(Context context, int resourceId) {

        final int[] textureId = new int[1];
        glGenTextures(1, textureId, 0);
        if (textureId[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not gen a new texture obj");

            }
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; //原始数据，不进行压缩

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "ResourceId :" + resourceId + " could not decode");
            }

            //删除纹理
            glDeleteTextures(1, textureId, 0);
            return 0;
        }
        //绑定纹理，2d纹理
        glBindTexture(GL_TEXTURE_2D, textureId[0]);

        //设置纹理过滤参数
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        //加载纹理到OpenGl并返回ID
        texImage2D(GL_TEXTURE_2D,0,bitmap,0);
        //释放bitmap
        bitmap.recycle();

        //生成mip贴图
        glGenerateMipmap(GL_TEXTURE_2D);

        //解绑纹理
        glBindTexture(GL_TEXTURE_2D,0);

        return textureId[0];
    }


}
