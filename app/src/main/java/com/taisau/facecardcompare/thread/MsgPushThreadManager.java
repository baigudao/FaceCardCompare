package com.taisau.facecardcompare.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.taisau.facecardcompare.listener.MsgPushListener;
import com.taisau.facecardcompare.util.NetworkUtil;
import com.taisau.facecardcompare.util.Preference;

import java.io.IOException;


/**
 * Created by ds  on 2017/1/4 14:04
 */

public class MsgPushThreadManager {
    public static MsgPushThreadManager manager = new MsgPushThreadManager();
    public MsgPushListener listener;
    private static boolean isRun = false;
    private static boolean isRecevie = false;
    private static Context ctx;
    private static int status = 0;
    private Channel channel;
    private static Connection connection;
    private static int waitTime = 10000;
    private static ConnectionFactory factory;
    private String sid;
    private String message="";

    public static MsgPushThreadManager getInstance() {
        return manager;
    }


    public void runMsgPushService(Context ctx, MsgPushListener listener) {
        this.listener = listener;
        MsgPushThreadManager.ctx = ctx;
        factory = new ConnectionFactory();
        factory.setHost(Preference.getServerIp());
       // factory.setHost(/*Preference.getServerIp()*/"192.168.2.25");
        factory.setPort(5672);
        factory.setUsername("taisau");
        factory.setPassword("taisau");
        new Thread(runTcpGetMsg).start();
    }

    Runnable runTcpGetMsg = new Runnable() {
        @Override
        public void run() {
            if (!isRun) {
                try {
                    connection=factory.newConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isRun = true;
                isRecevie = true;
                System.out.println("MQ Runnable启动");
                while (isRun) {
                    if (NetworkUtil.isConnected(ctx)) {
                        try {
                            if (connection==null) {
                                connection=factory.newConnection();
                            }
                            if (!connection.isOpen()) {
                                connection=null;
                                connection=factory.newConnection();
                            }
                            sid=Preference.getSid();
                            String rabbitQueueName = sid;
                            String severity = sid;
                            //渠道相关信息
                            channel = connection.createChannel();
                            //持久化
                            channel.queueDeclare(rabbitQueueName, true, false, false, null);
                            //将消息队列绑定到Exchange
                            channel.queueBind(rabbitQueueName, "taisau-mq-exchange-facenew", severity);
                            QueueingConsumer consumer = new QueueingConsumer(channel);
                            channel.basicConsume(rabbitQueueName, true, consumer);
                            while (isRecevie) {
                                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                                String message = new String(delivery.getBody());
                                //解析dada
                                try {
                                    status = 1;
                                    MsgPushThreadManager.this.message=message;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    status = -1;
                                }
                                Message msg = new Message();
                                msgGetHandler.sendMessage(msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e instanceof ShutdownSignalException) {
                                ShutdownSignalException s = (ShutdownSignalException) e;
                                if (!s.isHardError())
                                    break;
                            }
                            synchronized (this) {
                                try {
                                    wait(10000);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    } else {
                        synchronized (this) {
                            try {
                                wait(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                System.out.println("MQ Runnable停止");
            }
        }
    };

    private Handler msgGetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (status == 1) {
                listener.OnMsgPushFinish(   MsgPushThreadManager.this.message);
            } else if (status == -1) {
                listener.OnMsgFail("fail");
            } else if (status == -2) {
                listener.OnMsgGetServiceError("reconnection timeout");
            }
        }
    };

    public void stopMsgPushThread() {
        isRecevie = false;
        isRun = false;
        try {
            if (channel.isOpen())
                channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (connection.isOpen())
                connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = null;

    }
}
