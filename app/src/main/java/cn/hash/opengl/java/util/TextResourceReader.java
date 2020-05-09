package cn.hash.opengl.java.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Hash on 2020-04-08.
 */


public class TextResourceReader {

    public static String readTextFileFromResource(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();
        InputStream inputStream = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        String nextLine = "";
        try {

            inputStream = context.getResources().openRawResource(resourceId);
            reader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(reader);

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not open resource :" + resourceId);

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }


        }


        return body.toString();
    }


}
