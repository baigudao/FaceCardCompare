//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sdt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.util.Log;

import com.taisau.facecardcompare.util.ByteUtlis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Sdtusbapi extends Activity {
    Common common = new Common();
    int debug = 0;
    UsbDeviceConnection mDeviceConnection;
    UsbEndpoint epOut;
    UsbEndpoint epIn;
    final String FILE_NAME = "/file.txt";
    RandomAccessFile raf;
    File targetFile;
    Activity instance;

    public Sdtusbapi(Activity instance) throws Exception {
        int ret = this.initUSB(instance);
        this.instance = instance;
        if(this.debug == 1) {
            this.writefile("inintUSB ret=" + ret);
        }

        if(ret != this.common.SUCCESS) {
            Exception e = new Exception();
            if(ret == this.common.ENOUSBRIGHT) {
                e.initCause(new Exception());
                this.writefile("error common.ENOUSBRIGHT");
            } else {
                e.initCause(null);
                this.writefile("error null");
            }

            throw e;
        }
    }

    @SuppressLint({"NewApi"})
    int initUSB(Activity instance) {
        this.openfile();
        UsbDevice mUsbDevice = null;
        UsbManager manager = (UsbManager)instance.getSystemService("usb");
        if(manager == null) {
            this.writefile("manager == null");
            return this.common.EUSBMANAGER;
        } else {
            if(this.debug == 1) {
                this.writefile("usb dev：" + manager.toString());
            }

            HashMap deviceList = manager.getDeviceList();
            if(this.debug == 1) {
                this.writefile("usb dev：" + String.valueOf(deviceList.size()));
            }

            Iterator deviceIterator = deviceList.values().iterator();
            ArrayList USBDeviceList = new ArrayList();

            while(deviceIterator.hasNext()) {
                UsbDevice ret = (UsbDevice)deviceIterator.next();
                USBDeviceList.add(String.valueOf(ret.getVendorId()));
                USBDeviceList.add(String.valueOf(ret.getProductId()));
                if(ret.getVendorId() == 1024 && ret.getProductId() == '썚') {
                    mUsbDevice = ret;
                    if(this.debug == 1) {
                        this.writefile("zhangmeng:find device!");
                    }
                }
            }

            int ret1 = this.findIntfAndEpt(manager, mUsbDevice);
            return ret1;
        }
    }

    int usbsendrecv(byte[] pucSendData, int uiSendLen, byte[] RecvData, int[] puiRecvLen) {
        byte iFD = 0;
        Boolean bRet = null;
        byte ucCheck = 0;
        byte[] ucRealSendData = new byte[4096];
        byte[] pucBufRecv = new byte[4096];
        int[] iOffset = new int[1];
        if(4091 < uiSendLen) {
            return -1;
        } else {
            int iRet;
            if(-1 == iFD) {
                iRet = this.common.ENOOPEN;
                return iRet;
            } else {
                int iLen = (pucSendData[0] << 8) + pucSendData[1];
                ucRealSendData[0] = ucRealSendData[1] = ucRealSendData[2] = -86;
                ucRealSendData[3] = -106;
                ucRealSendData[4] = 105;

                int uiSizeSend;
                for(uiSizeSend = 0; uiSizeSend < iLen + 1; ++uiSizeSend) {
                    ucCheck ^= pucSendData[uiSizeSend];
                }

                for(uiSizeSend = 0; uiSizeSend < iLen + 2; ++uiSizeSend) {
                    ucRealSendData[uiSizeSend + 5] = pucSendData[uiSizeSend];
                }
                ucRealSendData[iLen + 6] = ucCheck;
                uiSizeSend = iLen + 2 + 5;
                boolean uiSizeRecv = false;
                iRet = this.mDeviceConnection.bulkTransfer(this.epOut, ucRealSendData, uiSizeSend, 2500);
                this.writefile("before uiSizeRecv error iRet=" + iRet);
                // usb 读取图片 耗时800ms操作
                int var18 = this.mDeviceConnection.bulkTransfer(this.epIn, pucBufRecv, pucBufRecv.length, 2500);
                byte[] testR = new byte[var18];
                System.arraycopy(pucBufRecv, 0, testR, 0, var18);
                if(5 <= var18 && 4096 > var18) {
                    bRet = Boolean.valueOf(this.Usb_GetDataOffset(pucBufRecv, iOffset));
                    if(!bRet.booleanValue()) {
                        iRet = this.common.EDATAFORMAT;
                        this.writefile("iRet = EDATAFORMAT =" + bRet + "iOffset= " + iOffset);
                        return iRet;
                    } else {
                        iLen = (pucBufRecv[iOffset[0] + 4] << 8) + pucBufRecv[iOffset[0] + 5];
                        if(4089 < iLen) {
                            iRet = this.common.EDATALEN;
                            this.writefile("iRet = EDATALEN = " + iLen);
                            return iRet;
                        } else {
                            byte[] tempData = new byte[4096];

                            int i;
                            for(i = 0; i < pucBufRecv.length - iOffset[0] - 4; ++i) {
                                tempData[i] = pucBufRecv[i + iOffset[0] + 4];
                            }

                            bRet = Boolean.valueOf(Usb_CheckChkSum(iLen + 2, tempData));
                            if(!bRet.booleanValue()) {
                                iRet = this.common.EPCCRC;
                                this.writefile("iRet = EPCCRC");
                                return iRet;
                            } else {
                                for(i = 0; i < iLen + 1; ++i) {
                                    RecvData[i] = pucBufRecv[i + iOffset[0] + 4];
                                }

                                puiRecvLen[0] = iLen + 1;
                                this.writefile("stdapi.puiRecvLen =" + (iLen + 1));
                                return this.common.SUCCESS;
                            }
                        }
                    }
                } else {
                    iRet = this.common.EDATALEN;
                    this.writefile("uiSizeRecv error =" + var18);
                    return iRet;
                }
            }
        }
    }

    boolean Usb_GetDataOffset(byte[] dataBuffer, int[] iOffset) {
        iOffset[0] = 0;

        int iIter;
        for(iIter = 0; iIter < 7 && (dataBuffer[iIter + 0] != -86 || dataBuffer[iIter + 1] != -86 || dataBuffer[iIter + 2] != -106 || dataBuffer[iIter + 3] != 105); ++iIter) {
        }

        if(7 <= iIter) {
            return false;
        } else {
            iOffset[0] = iIter;
            return true;
        }
    }

    static boolean Usb_CheckChkSum(int uiDataLen, byte[] pucRecvData) {
        byte ucCheck = 0;

        for(int iIter = 0; iIter < uiDataLen - 1; ++iIter) {
            ucCheck ^= pucRecvData[iIter];
        }

        return ucCheck == pucRecvData[uiDataLen - 1];
    }

    private void openfile() {
        if(this.debug == 1) {
            File sdCardDir = Environment.getExternalStorageDirectory();

            try {
                this.setTargetFile(new File(sdCardDir.getCanonicalPath() + "/file.txt"));
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            try {
                this.setFile(new RandomAccessFile(this.targetFile, "rw"));
            } catch (FileNotFoundException var3) {
                var3.printStackTrace();
            }

            this.writefile("in open file()");
        }

    }

    public void writefile(String context) {
        if(this.debug == 1 && Environment.getExternalStorageState().equals("mounted")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

            try {
                this.raf.seek(this.targetFile.length());
            } catch (IOException var5) {
                var5.printStackTrace();
            }

            try {
                this.raf.writeChars("\n" + sdf.format(new Date()) + " " + context);
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

    }

    private void closefile() {
        if(this.debug == 1 && this.raf != null) {
            try {
                this.raf.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    private int findIntfAndEpt(final UsbManager manager, final UsbDevice mUsbDevice) {
        UsbInterface mInterface = null;
        if(mUsbDevice == null) {
            this.writefile("zhangmeng:no device found");
            return this.common.EUSBDEVICENOFOUND;
        } else {
            byte connection = 0;
            UsbInterface connection1;
            if(connection < mUsbDevice.getInterfaceCount()) {
                connection1 = mUsbDevice.getInterface(connection);
                mInterface = connection1;
            }

            if(mInterface != null) {
                connection1 = null;
                if(manager.hasPermission(mUsbDevice)) {
                    UsbDeviceConnection connection11 = manager.openDevice(mUsbDevice);
                    if(connection11 == null) {
                        return this.common.EUSBCONNECTION;
                    } else {
                        if(connection11.claimInterface(mInterface, true)) {
                            this.mDeviceConnection = connection11;
                            this.getEndpoint(this.mDeviceConnection, mInterface);
                        } else {
                            connection11.close();
                        }

                        return this.common.SUCCESS;
                    }
                } else {
                    this.writefile("zhangmeng:no rights");
                    (new Thread() {
                        public void run() {
                            Activity var10000 = Sdtusbapi.this.instance;
                            Sdtusbapi.this.common.getClass();
                            PendingIntent pi = PendingIntent.getBroadcast(var10000, 0, new Intent("com.android.USB_PERMISSION"), 0);
                            manager.requestPermission(mUsbDevice, pi);
                        }
                    }).start();
                    return this.common.ENOUSBRIGHT;
                }
            } else {
                this.writefile("zhangmeng:no interface");
                return this.common.ENOUSBINTERFACE;
            }
        }
    }

    private void getEndpoint(UsbDeviceConnection connection, UsbInterface intf) {
        if(intf.getEndpoint(1) != null) {
            this.epOut = intf.getEndpoint(1);
        }

        if(intf.getEndpoint(0) != null) {
            this.epIn = intf.getEndpoint(0);
        }

    }

    private void setFile(RandomAccessFile raf) {
        this.raf = raf;
    }

    private void setTargetFile(File f) {
        this.targetFile = f;
    }
}
