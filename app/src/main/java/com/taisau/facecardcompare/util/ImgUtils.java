package com.taisau.facecardcompare.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.GFace;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.taisau.facecardcompare.util.Constant.LIB_DIR;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32FC3;
import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public class ImgUtils {
    private static final String tempFile = LIB_DIR + "/temp_mod.jpg";
    private static ImgUtils utils = new ImgUtils();

    public static ImgUtils getUtils() {
        return utils;
    }

    public float[] getImgFea(Bitmap temp) {
        int[] pixels = new int[temp.getHeight() * temp.getWidth()];
        temp.getPixels(pixels, 0, temp.getWidth(), 0, 0, temp.getWidth(), temp.getHeight());
        byte[] rgb = new byte[temp.getHeight() * temp.getWidth()];
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = (byte) RgbToGray(pixels[i]);
        }
        byte[] ret = GFace.detectFace(rgb, temp.getWidth(), temp.getHeight());
        if (ret != null && ret[0] > 0) {
            GFace.FaceInfo aa = GFace.getFaceInfo(ret);
            GFace.FacePointInfo info = aa.info[0];
            float[] fea = GFace.getFea(rgb, temp.getWidth(), temp.getHeight(), (int) info.ptEyeLeft.x, (int) info.ptEyeLeft.y, (int) info.ptEyeRight.x, (int) info.ptEyeRight.y,
                    (int) info.ptNose.x, (int) info.ptNose.y, (int) info.ptMouthLeft.x, (int) info.ptMouthLeft.y,
                    (int) info.ptMouthRight.x, (int) info.ptMouthRight.y);
            return fea;
        } else
            return null;
        // DetectInfo info=new DetectInfo();
        // Mat crop = null;
        // info.setBb(GFace.detectFace(rgb, temp.getWidth(), temp.getHeight()));

    /*if (info.getBb()!=null) {
        info.initFacePos();
        crop=cropImg(info.getPos(),tempFile);
    }
    else
        return null;
    if (crop!=null)
    {
        if (Imgcodecs.imwrite(LIB_DIR+"/temp_crop.jpg",crop))
        {
            float []res= CaffeMobile.getBlobTest(LIB_DIR+"/temp_crop.jpg");
            return res;
        }
    }*/
        // return null;
    }

    public byte[] getPixelsRGBA(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer
        byte[] temp = buffer.array(); // Get the underlying array containing the
        return temp;
    }

    public void saveBitmap(Bitmap bm, String path) {

        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    Bitmap temp;

    public int RgbToGray(int r, int g, int b) {
        return (r * 30 + g * 59 + b * 11) / 100;
    }

    public int RgbToGray(int xrgb) {
        return RgbToGray((xrgb >> 16) & 0xff,
                (xrgb >> 8) & 0xff,
                (xrgb) & 0xff);
    }

    public float[] std_points = new float[]{89.3095f, 72.9025f, 169.3095f, 72.9025f, 127.8949f, 127.0441f, 96.8796f, 184.8907f, 159.1065f, 184.7601f};

    public Mat cropImg(DetectInfo.FacePos pos, String filename) {
        //       System.out.println("sub:"+subfactor.dump());
        float[] facial_points = new float[]{
                pos.getEye_left_x(), pos.getEye_left_y(),
                pos.getEye_right_x(), pos.getEye_right_y(),
                pos.getNose_x(), pos.getNose_y(),
                pos.getMouth_left_x(), pos.getMouth_left_y(),
                pos.getMouth_right_x(), pos.getMouth_right_y()
        };
        Mat tform = getTformMatrix(std_points, facial_points);
        Mat srcImage = Imgcodecs.imread(filename);
        Mat dstImage = new Mat(256, 256, CV_8UC3);
        Imgproc.warpAffine(srcImage, dstImage, tform, dstImage.size(), 1, 0, new Scalar(0));
        dstImage.convertTo(dstImage, CV_32FC3);
        return dstImage;

    }

    public Mat cropImg(float[] facial_points, String filename) {
        //       System.out.println("sub:"+subfactor.dump());
        Mat tform = getTformMatrix(std_points, facial_points);

        Mat srcImage = Imgcodecs.imread(filename);
        Mat dstImage = new Mat(256, 256, CV_8UC3);
        Imgproc.warpAffine(srcImage, dstImage, tform, dstImage.size(), 1, 0, new Scalar(0));
        dstImage.convertTo(dstImage, CV_32FC3);
        return dstImage;

    }

    public Mat getTformMatrix(float[] std_points, float[] facial_points) {
        int points_num_ = 5;
        double sum_x = 0, sum_y = 0;
        double sum_u = 0, sum_v = 0;
        double sum_xx_yy = 0;
        double sum_ux_vy = 0;
        double sum_vx__uy = 0;
        for (int c = 0; c < points_num_; ++c) {
            int x_off = c * 2;
            int y_off = x_off + 1;
            sum_x += std_points[c * 2];
            sum_y += std_points[c * 2 + 1];
            sum_u += facial_points[x_off];
            sum_v += facial_points[y_off];
            sum_xx_yy += std_points[c * 2] * std_points[c * 2] +
                    std_points[c * 2 + 1] * std_points[c * 2 + 1];
            sum_ux_vy += std_points[c * 2] * facial_points[x_off] +
                    std_points[c * 2 + 1] * facial_points[y_off];
            sum_vx__uy += facial_points[y_off] * std_points[c * 2] -
                    facial_points[x_off] * std_points[c * 2 + 1];
        }
        double q = sum_u - sum_x * sum_ux_vy / sum_xx_yy
                + sum_y * sum_vx__uy / sum_xx_yy;
        double p = sum_v - sum_y * sum_ux_vy / sum_xx_yy
                - sum_x * sum_vx__uy / sum_xx_yy;
        double r = points_num_ - (sum_x * sum_x + sum_y * sum_y) / sum_xx_yy;
        double a = (sum_ux_vy - sum_x * q / r - sum_y * p / r) / sum_xx_yy;
        double b = (sum_vx__uy + sum_y * q / r - sum_x * p / r) / sum_xx_yy;
        double c = q / r;
        double d = p / r;
        Mat Tinv = new Mat(3, 3, CV_32F);
        Tinv.put(0, 0, a);
        Tinv.put(0, 1, b);
        Tinv.put(0, 2, 0);
        Tinv.put(1, 0, -b);
        Tinv.put(1, 1, a);
        Tinv.put(1, 2, 0);
        Tinv.put(2, 0, c);
        Tinv.put(2, 1, d);
        Tinv.put(2, 2, 1);
        Mat T = Tinv.inv();
        Mat res = T.colRange(0, 2).clone();
        return res.t();
    }

    public Bitmap adjustBitmap(Bitmap bitmap, GFace.FaceInfo aa, int scaleCount) {

        int x, right;
        if (aa.rc[0].left > 30)
            x = (int) aa.rc[0].left - 30;
        else if (aa.rc[0].left > 0 && aa.rc[0].left <= 30)
            x = (int) aa.rc[0].left;
        else
            x = 0;
        if (aa.rc[0].right < (bitmap.getWidth() / scaleCount - 30))
            right = (int) aa.rc[0].right + 30;
        else if (aa.rc[0].right >= (bitmap.getWidth() / scaleCount - 30) && aa.rc[0].right < bitmap.getWidth() / scaleCount)
            right = (int) aa.rc[0].right;
        else
            right = bitmap.getWidth() / scaleCount;
        int y, height;
        if (aa.rc[0].top > 60)
            y = (int) aa.rc[0].top - 60;
        else
            y = 0;
        if (aa.rc[0].bottom < (bitmap.getHeight() / scaleCount - 100))
            height = (int) aa.rc[0].bottom + 100;
        else
            height = bitmap.getHeight() / scaleCount;
        if (x < 0 || x > bitmap.getWidth() / scaleCount)
            x = 0;
        if (y < 0 || y > bitmap.getHeight() / scaleCount)
            y = 0;
        if (right > bitmap.getWidth() / scaleCount || right < 0)
            right = bitmap.getWidth() / scaleCount;
        if (height > bitmap.getHeight() / scaleCount || height < 0)
            height = bitmap.getHeight() / scaleCount;
        if (right - x <= bitmap.getWidth() / scaleCount && height - y <= bitmap.getHeight() / scaleCount && x >= 0 && y >= 0) {
            x = x * scaleCount;
            y = y * scaleCount;
            right = right * scaleCount;
            height = height * scaleCount;
            bitmap = Bitmap.createBitmap(bitmap, x, y, right - x, height - y);
        }
        return bitmap;
    }

    /**
     * @param bitmap
     * @param orientationDegree 0 - 360 范围
     * @return
     */
    public Bitmap adjustPhotoRotation(Bitmap bitmap, int orientationDegree) {


        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree, (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bitmap.getHeight();
            targetY = 0;
        } else {
            targetX = bitmap.getHeight();
            targetY = bitmap.getWidth();
        }


        final float[] values = new float[9];
        matrix.getValues(values);


        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];


        matrix.postTranslate(targetX - x1, targetY - y1);


        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
                Bitmap.Config.ARGB_8888);


        Paint paint = new Paint();
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawBitmap(bitmap, matrix, paint);


        return canvasBitmap;
    }
    public static void convertJpgToPng(String jpgFilePath, String pngFilePath, int quality) {
        Bitmap bitmap = BitmapFactory.decodeFile(jpgFilePath);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pngFilePath))) {
            if (bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos)) {
                bos.flush();
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
