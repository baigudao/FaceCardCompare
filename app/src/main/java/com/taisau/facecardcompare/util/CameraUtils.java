package com.taisau.facecardcompare.util;

/**
 * Created by Administrator on 2016/12/26 0026.
 */

import android.hardware.Camera;

import java.util.List;

/**
 * 此类为相机的生成帮助类
 * 主要调用 android.hardware.Camera包下所提供的API来配置相机
 * 可以对此类修改以修改相机配置
 */
public class CameraUtils {
    public static boolean isPreview = false;
    public static Camera camera;

    //摄像头参数
    public int cameraFrontIndex = -1;
    public int cameraBackIndex = -1;
    public int cameraIndex = -1;

    public boolean support720 = false;
    public boolean support1080 = false;
    public boolean supportVGA = false;
    public boolean supportQVGA = false;
    public int supportType = 0;
    public static int status;
    private static final int REQUEST_GET_ACCOUNT = 112;
    private static final int PERMISSION_REQUEST_CODE = 200;
    public CameraUtils() {
        setCameraIndex();
        status = initCamera();
    }

    public static int getStatus() {
        return status;
    }

    public static void setStatus(int status) {
        CameraUtils.status = status;
    }

    /**
     * 获知相机预览生成的YUV尺寸种类
     *
     * @return
     */
    public int getSupportType() {
        return supportType;
    }

    /**
     * 获取当前相机预览画面所使用的摄像头
     *
     * @return
     */
    public int getCameraIndex() {
        return cameraIndex;
    }

    /**
     * 获取android.hardware.Camera 生成的实例对象
     *
     * @return
     */

    public Camera getCamera() {
        return camera;
    }


    /**
     * 设置相机为前置摄像头
     *
     * @return true为设置成功 false为没有前置摄像头
     */
    public boolean setFrontIndex() {
        if (cameraFrontIndex != -1) {
            cameraIndex = cameraFrontIndex;
            return true;
        } else
            return false;
    }


    /**
     * 设置相机为前置摄像头
     *
     * @return true为设置成功 false为没有前置摄像头
     */
    public boolean setBackIndex() {
        if (cameraFrontIndex != -1) {
            cameraIndex = cameraFrontIndex;
            return true;
        } else
            return false;
    }

    /**
     * @return -1尚未生成系统camera
     * 1设置尺寸成功
     */
    public int checkSupportPreviewSize() {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            for (Camera.Size size : sizes) {
                if (!supportQVGA && size.width == 320 && size.height == 240)
                    supportQVGA = true;
                if (!supportVGA && size.width == 640 && size.height == 480)
                    supportVGA = true;
                if (!support720 && size.width == 1280 && size.height == 720)
                    support720 = true;
                if (!supportVGA && size.width == 1920 && size.height == 1080)
                    support1080 = true;
            }
            return 0;
        } else
            return -1;
    }


    /**
     * 用于获取手机摄像头信息
     * 判断手机是否有摄像头
     * cameraFrontIndex为前摄像头
     * cameraBackIndex为后置摄像头
     * 默认打开前置摄像头,如果没有则为后置
     */
    public void setCameraIndex() {
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraFrontIndex = i;
            }
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraBackIndex = i;
            }
            if (cameraFrontIndex != -1)
                cameraIndex = cameraFrontIndex;
            else if (cameraBackIndex != -1)
                cameraIndex = cameraBackIndex;
        }
    }

    public boolean switchCamera() {
        if (cameraFrontIndex != -1 && cameraBackIndex != -1) {
            releaseCamera();
            if (cameraIndex == cameraFrontIndex)
                cameraIndex = cameraBackIndex;
            else
                cameraIndex = cameraFrontIndex;
            initCamera();
            return true;
        } else
            return false;
    }

    // 初始化Camera
    public int initCamera() {
        if (!isPreview) {
            //打开摄像头
            System.out.println("ds>>>>   打开摄像头");
            if (cameraIndex == -1)
                return -1;
            try {
                camera = Camera.open(cameraIndex);
                if (camera != null) {
                    //   camera.setDisplayOrientation(90);
                    //设置旋转90度
                    // camera.setDisplayOrientation(180);
                    // 通过SurfaceView显示取景画面
                    checkSupportPreviewSize();
                    Camera.Parameters parameters = camera.getParameters();
                    if (support720) {
                        parameters.setPreviewSize(1280, 720);
                        supportType = 3;
                    } else if (supportVGA) {
                        parameters.setPreviewSize(640, 480);
                        supportType = 2;
                    } else if (supportQVGA) {
                        parameters.setPreviewSize(320, 240);
                        supportType = 1;
                    } else if (support1080) {
                        parameters.setPreviewSize(1920, 1080);
                        supportType = 4;
                    }
                    //     parameters.setPictureSize(1920,1080);
                    /*    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
                        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT);
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);*/
                    //parameters.setExposureCompensation();
                       /* System.out.println("facedetect support:"+camera.getParameters().getMaxNumDetectedFaces());
                        System.out.println("iso:"+parameters.get("iso-values")+" now："+parameters.get("iso"));
                        // parameters.set("iso","ISO3200");
                        System.out.println("support type:"+supportType);*/
                    camera.setParameters(parameters);
                    // 开始预览
                    // 自动对焦
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ds>>>>   打开相机失败");
                return -2;
            }
        }
        return 0;
    }

    // 释放摄像头图像显示
    public void releaseCamera() {
        // 释放摄像头
        if (camera != null) {
            System.out.println("ds>>>>  释放摄像头  camera = " + camera);
            camera.setPreviewCallback(null);
            if (isPreview) {
                camera.stopPreview();
            }
            camera.release();
            camera = null;
            isPreview = false;
        }
    }
}
