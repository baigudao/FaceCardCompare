package com.taisau.facecardcompare.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/3/20 0020.
 */

public class FeaUtils {

    public static boolean saveFea(String path,float[]modFea){
        File file=new File(path);
        FileOutputStream fos = null;
        DataOutputStream dos = null;
        try{
            if (!file.exists())
                file.createNewFile();
            // create file output stream
            fos = new FileOutputStream(path);
            // create data output stream
            dos = new DataOutputStream(fos);
            // for each byte in the buffer
            for (float b:modFea)
            {
                // write float to the data output stream
                dos.writeFloat(b);
            }
            // force bytes to the underlying stream
            dos.flush();
            // create file input stream
        }catch(Exception e){
            // if any I/O error occurs
            e.printStackTrace();
        }finally{
            // releases all system resources from the streams
            try {

                if(dos!=null)
                    dos.close();
                if(fos!=null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static float[] readFea(String path)
    {
        float []fea=new float[256];
        InputStream is = null;
        DataInputStream dis = null;
        try{
            is = new FileInputStream(path);
            // create new data input stream
            dis = new DataInputStream(is);
            int i=0;
            while(dis.available()>0)
            {
                // read character
                float c = dis.readFloat();
                // print
                fea[i]=c;
                i++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(is!=null)
                    is.close();
                if(dis!=null)
                    dis.close();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
        return fea;
        // read till end of the stream
    }
}
