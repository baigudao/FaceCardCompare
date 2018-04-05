package com.taisau.facecardcompare.listener;

/**
 * Created by ds  on 2017/1/4 13:25
 */

public interface MsgPushListener {
    void OnMsgPushFinish(String str);
    void OnMsgFail(String msg);
    void OnMsgGetServiceError(String msg);
}
