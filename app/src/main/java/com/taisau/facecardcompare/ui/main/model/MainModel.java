package com.taisau.facecardcompare.ui.main.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.SparseIntArray;
import android.view.View;

import com.GFace;
import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.listener.OnCardDetectListener;
import com.taisau.facecardcompare.model.CompareInfo;
import com.taisau.facecardcompare.model.FaceList;
import com.taisau.facecardcompare.model.GroupJoin;
import com.taisau.facecardcompare.model.GroupJoinDao;
import com.taisau.facecardcompare.model.GroupList;
import com.taisau.facecardcompare.model.GroupListDao;
import com.taisau.facecardcompare.model.HistoryList;
import com.taisau.facecardcompare.model.Person;
import com.taisau.facecardcompare.model.PersonDao;
import com.taisau.facecardcompare.model.ReLoadInfo;
import com.taisau.facecardcompare.model.WFIndex;
import com.taisau.facecardcompare.ui.main.contract.MainContract;
import com.taisau.facecardcompare.ui.main.network.MainNet;
import com.taisau.facecardcompare.ui.main.utils.FeaAction;
import com.taisau.facecardcompare.ui.main.utils.listener.GetWFeaListener;
import com.taisau.facecardcompare.util.FeaUtils;
import com.taisau.facecardcompare.util.FileUtils;
import com.taisau.facecardcompare.util.IDCardUtils;
import com.taisau.facecardcompare.util.ImgUtils;
import com.taisau.facecardcompare.util.Preference;
import com.taisau.facecardcompare.util.SoundUtils;
import com.taisau.facecardcompare.util.YUVUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import static com.taisau.facecardcompare.FaceCardApplication.getApplication;
import static com.taisau.facecardcompare.util.Constant.FACE_IMG;
import static com.taisau.facecardcompare.util.Constant.LIB_DIR;


/**
 * Created by whx on 2017-09-04
 */

public class MainModel implements MainContract.Model, Camera.PreviewCallback
        , GetWFeaListener, OnCardDetectListener {

    private StringBuffer time = new StringBuffer();
    private MainContract.Presenter presenter;
    private HistoryList history;
    private static boolean isRun = false;
    private static int noFaceCount = 0;
    //是否进行人脸比对
    private static volatile boolean runDetect = false;
    //检测到人脸
    private static boolean hasFace = false;
    //是否进行人脸比对
    private static boolean runCompare = false;
    private static Bitmap faceBit;
    //检测后的数据
    private GFace.FaceInfo aa;
    private float[] modFea;
    //黑名单验证
    private static boolean isBlack = false;
    //黑名单验证状态 0为未检测 1为正在检测 2为检测完成
    private static int CBStatus = 0;
    private List<GroupList> black;
    //历史信息Model

    private static String[] card;
    private long validTime;

    private static boolean BW = false;
    private static boolean AW = false;
    private static boolean AC = false;
    private static int ACCountYL = 0;
    private static int ACCountYR = 0;
    private static int ACCountN = 0;

    private static boolean WC = false;
    private static boolean WCing = false;
    private static List<WFIndex> WFL;

    private static boolean is18 = true;

    private static boolean voiceTips = false;
    private static boolean isVoice = false;

    private static float comScore = 65;

    private static int noFace = 20;

    private Person bp = null;

    private volatile int disCount = 0;

    private FeaAction feaAction;

    private volatile boolean cpuSleeping = false;
    //语音初始化
    private SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    private SparseIntArray soundID = new SparseIntArray();

    public Handler handler = new Handler();

    private boolean ACPass = false;

    private Thread updateTimeThread, uploadFileThread, saveHistoryThread;
    private boolean isSlow = false;

    public MainModel(/*Context context,*/ MainContract.Presenter presenter2) {
        this.presenter = presenter2;
        initSoundPool();

    }

    public Camera.PreviewCallback getPreviewCallback() {
        return MainModel.this;
    }

    @Override
    public OnCardDetectListener getCardDetectListener() {
        return MainModel.this;
    }


    @Override
    public void changeSound(boolean isInit, int soundNum) {
        int current = SoundUtils.getInstance().getCurrentVolume(AudioManager.STREAM_MUSIC);
        switch (soundNum) {
            case 0: //静音
                SoundUtils.getInstance().muteSound(AudioManager.STREAM_MUSIC);
                Preference.setDevVolume("0");
                break;
            case -1: //音量 +1
                SoundUtils.getInstance().addSound(AudioManager.STREAM_MUSIC);
                current += 1;
                Preference.setDevVolume(String.valueOf(current));
                break;
            case -2: //音量 -1
                SoundUtils.getInstance().decreaseSound(AudioManager.STREAM_MUSIC);
                current -= 1;
                Preference.setDevVolume(String.valueOf(current));
                break;
            case -3: //音量最大
                SoundUtils.getInstance().maxSound(AudioManager.STREAM_MUSIC);
                int max = SoundUtils.getInstance().getMaxVolume(AudioManager.STREAM_MUSIC);
                Preference.setDevVolume(String.valueOf(max));
                break;
            default: //直接设置音量
                if (isInit) {
                    SoundUtils.getInstance().setSoundMute(AudioManager.STREAM_MUSIC, soundNum);
                } else {
                    SoundUtils.getInstance().setSound(AudioManager.STREAM_MUSIC, soundNum);
                }
                Preference.setDevVolume(String.valueOf(soundNum));
                break;
        }

    }

    @Override
    public void setRunDetect(boolean run) {
        runDetect = run;
        presenter.updateFaceFrame(changeSituation(0, 0, 0, 0), 640, 360);
    }

    private void initTime() {
        isRun = true;
        runDetect = true;
        initSetting();
        updateTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        while (isRun) {
                            time.delete(0, time.length());
                            Date curDate = new Date(System.currentTimeMillis());
                            switch (curDate.getDay()) {
                                case 1:
                                    time.append("星期一 ");
                                    break;
                                case 2:
                                    time.append("星期二 ");
                                    break;
                                case 3:
                                    time.append("星期三 ");
                                    break;
                                case 4:
                                    time.append("星期四 ");
                                    break;
                                case 5:
                                    time.append("星期五 ");
                                    break;
                                case 6:
                                    time.append("星期六 ");
                                    break;
                                case 0:
                                    time.append("星期日 ");
                                    break;
                            }
                            time.append(curDate.getHours()).append(":");
                            if (curDate.getMinutes() < 10)
                                time.append("0").append(curDate.getMinutes());
                            else
                                time.append(curDate.getMinutes());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    presenter.updateTimeToView(time.toString());
                                }
                            });
                            //等待
                            wait((60 - curDate.getSeconds()) * 1000);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //页面退出后需要中断线程
                }
            }
        });
        updateTimeThread.start();
    }

    private void stopTime() {
        isRun = false;
        runDetect = false;
        stopThread(updateTimeThread);
    }

    private void stopThread(Thread thread) {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    private void initSetting() {

        if (Preference.getAgeWarning() != null) {
            AW = Preference.getAgeWarning().equals("true");
        }
        if (Preference.getBlackWarning() != null) {
            BW = Preference.getBlackWarning().equals("true");
        }
        if (Preference.getScoreRank() != null && !Preference.getScoreRank().equals(""))
            switch (Preference.getScoreRank()) {
                case "easy":
                    comScore = 55;
                    break;
                case "hard":
                    comScore = 65;
                    break;
                default:
                    comScore = Float.parseFloat(Preference.getScoreRankValue());
                    break;
            }
        if (Preference.getAliveCheck() != null && !Preference.getAliveCheck().equals("")) {
            if (Preference.getAliveCheck().equals("true"))
                AC = true;
            else if (Preference.getAliveCheck().equals("false"))
                AC = false;
        }
        if (Preference.getNoFaceCount() != null && !Preference.getNoFaceCount().equals("")) {
            noFace = Integer.parseInt(Preference.getNoFaceCount());
        }
        WC = Preference.getWhiteCheck() != null && Preference.getWhiteCheck().equals("true");
        if (WC) {
            initWhiteFea();
        }
        if (Preference.getVoiceTips() != null) {
            if (Preference.getVoiceTips().equals("true")) {
                soundID.put(3, soundPool.load(getApplication(), R.raw.detect_face_tips, 1));
                voiceTips = true;
            } else
                voiceTips = false;
        }
    }

    private void initSoundPool() {
        soundID.put(1, soundPool.load(getApplication(), R.raw.compare_success, 1));
        soundID.put(2, soundPool.load(getApplication(), R.raw.compare_fail, 1));
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            }
        });
    }

    private void initWhiteFea() {
        List<FaceList> list = FaceCardApplication.getApplication().getDaoSession().getFaceListDao().loadAll();
        WFL = new ArrayList<>();
        for (FaceList ff : list) {
            float[] temp = null;
            if (ff.getFea_path() != null) {
                temp = FeaUtils.readFea(ff.getFea_path());
            }
            WFL.add(new WFIndex(ff.getFace_id(), ff.getPersonId(), temp, ff.getImg_path()));
        }
    }

    @Override
    public String getAdsTitle() {
        return Preference.getMainTittle();
    }

    @Override
    public String getAdsSubtitle() {
        return Preference.getSubTittle();
    }

    @Override
    public String[] getAdsImagePath() {
        return new String[]{Preference.getAdsPath()};
    }

    @Override
    public String getUserName() {
        return Preference.getCustomName();
    }


    @Override
    public void startUpdateTime() {
        initTime();
    }

    @Override
    public void stopUpdateTime() {
        stopTime();
    }

    @Override
    public void doAutoLogin() {
        new MainNet(MainNet.NetAction.DO_LOGIN, null, presenter);
    }

    private void saveHistory(final Bitmap face, final String info[], final String face_path,
                             final String com_status, final float score, final Date time) {
        saveHistoryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        history = new HistoryList();
                        history.setId(null);
                        history.setPerson_name(info[1]);
                        history.setSex(info[2]);
                        history.setEthnic(info[3]);
                        history.setBirthday(info[4]);
                        history.setAddress(info[5]);
                        history.setId_card(info[6]);
                        history.setCard_release_org(info[7]);
                        history.setValid_time(info[8]);
                        history.setCard_path(info[10]);
                        System.out.println("face path:"+face_path);
                        history.setFace_path(face_path);
                        history.setTime(time);
                        history.setCom_status(com_status);
                        history.setScore(score);
                        int size = (int) getApplication().getDaoSession().getHistoryListDao().count();
                        if (size >= 10000) {
                            HistoryList first = getApplication().getDaoSession().getHistoryListDao().queryBuilder().limit(1).list().get(0);
                            if (first.getCard_path() != null)
                                FileUtils.deleteFile(first.getCard_path());
                            if (first.getFace_path() != null)
                                FileUtils.deleteFile(first.getFace_path());
                            if (first.getCard_fea_path() != null)
                                FileUtils.deleteFile(first.getCard_fea_path());
                            if (first.getFace_fea_path() != null)
                                FileUtils.deleteFile(first.getFace_fea_path());
                            getApplication().getDaoSession().delete(first);
                        }
                        getApplication().getDaoSession().getHistoryListDao().insert(history);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        saveHistoryThread.start();
    }

    private void startUpload(ReLoadInfo reLoadInfo) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceCardApplication.getApplication().getDaoSession().getReLoadInfoDao().insert(reLoadInfo);
                MainNet net = new MainNet(MainNet.NetAction.UPLOAD_FILE, null, presenter);
            }
        }).start();
    }

    private void resetData() {
        isBlack = false;
        if (black != null)
            black.clear();
        CBStatus = 0;
    }

    private void clearMode() {
        CBStatus = 0;
        isBlack = false;
        ACCountYR = 0;
        ACCountYL = 0;
        ACCountN = 0;
        noFaceCount = 0;
        if (black != null)
            black.clear();
        presenter.updateCompareInfoView(new CompareInfo(null, null, "", null, -1, Color.RED, View.GONE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.CLEAR_All));
        runCompare = false;
        //isStartCompare = false;
        if (!IDCardUtils.isRun()) {
            IDCardUtils.getUtils().setIDCardReaderRun();
        }
    }

    private Observable checkBlackObservable() {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                QueryBuilder<Person> PB = FaceCardApplication.getApplication().getDaoSession().
                        getPersonDao().queryBuilder().where(PersonDao.Properties.Id_card.eq(card[6]));
                Person pp;
                if (PB.list().size() > 0)
                    pp = PB.list().get(0);
                else {
                    isBlack = false;
                    CBStatus = 2;
                    return Observable.just(false);
                }
                QueryBuilder<GroupList> builderB = FaceCardApplication.getApplication().getDaoSession()
                        .getGroupListDao().queryBuilder();
                builderB.where(GroupListDao.Properties.Group_type.eq("black_group"));
                builderB.join(GroupJoin.class, GroupJoinDao.Properties.Group_id)
                        .where(GroupJoinDao.Properties.Person_id.eq(pp.getPerson_id()));
                //   builderB.where( GroupListDao.Properties.Group_type.eq("black_group"));
                black = builderB.list();
                //System.out.println("finish do search black size:" + black.size());
                CBStatus = 2;
                if (black.size() > 0) {
                    bp = pp;
                    isBlack = true;
                    return Observable.just(true);
                } else {
                    isBlack = false;
                    return Observable.just(false);
                }

            }
        });
    }

    @Override
    public void onDetectCard(String[] info) {
        IDCardUtils.getUtils().setIDCardReaderStop();

        Bitmap cardBit = BitmapFactory.decodeFile(info[10]);
        cardBit = Bitmap.createScaledBitmap(cardBit, 200, 300, false);

        card = info;
        long nowTime = Long.parseLong(DateFormat.format("yyyyMMdd", Calendar.getInstance(Locale.CHINA)).toString());
        if (AW) {
            long age = Long.parseLong(card[4]);
            long max = Long.parseLong(Preference.getAgeWarningMAX());
            long min = Long.parseLong(Preference.getAgeWarningMIN());
            is18 = !(nowTime - age < min * 10000 || nowTime - age > max * 10000);
        }
        try {
            validTime = Long.parseLong(card[8].substring(9)) - nowTime;
        } catch (Exception e) {
            validTime = 0;
        }

        noFaceCount = 0;
        if (CBStatus == 0) {
            CBStatus = 1;
            if (BW)
                checkBlackObservable()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                CBStatus = 2;
                            }

                            @Override
                            public void onNext(Object o) {

                            }
                        });

            modFea = ImgUtils.getUtils().getImgFea(cardBit);
            if (modFea == null) {
                presenter.updateCompareInfoView(new CompareInfo(null, cardBit, "提取特征值失败，请重试", null, -1, Color.RED, View.GONE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.CARD_WITH_TEXT));
                IDCardUtils.getUtils().setIDCardReaderRun();
                resetData();
            } else if (modFea.length == 512) {
                String cardimgPath = LIB_DIR + "/card_img/" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + "_card_head.jpg";
                ImgUtils.getUtils().saveBitmap(cardBit, cardimgPath);
                card[10] = cardimgPath;
                runCompare = true;
                presenter.updateCompareInfoView(new CompareInfo(null, cardBit, "身份证读取完成，请正对摄像头拍照", null, -1, Color.WHITE, View.GONE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.CARD_WITH_TEXT));
            } else {
                presenter.updateCompareInfoView(new CompareInfo(null, cardBit, "提取的特征值有误", null, -1, Color.RED, View.GONE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.CARD_WITH_TEXT));
                IDCardUtils.getUtils().setIDCardReaderRun();
                resetData();
            }

        }

    }

    private byte[] temp = null;
    private float[] com_fea = null;
    private long[] situation = new long[4];

    private long[] changeSituation(long... situations) {
//        System.arraycopy(situations, 0, situation, 0, situations.length);
        situation[0] = situations[0];
        situation[1] = situations[1];
        situation[2] = situations[2];
        situation[3] = situations[3];
        return situation;
    }

    public void slowUp() {
        if (isSlow) {
            if (!cpuSleeping) {
                cpuSleeping = true;
            } else {
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        try {
                            wait(700);
                            cpuSleeping = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        if (runDetect) {
            slowUp();
            //压缩yuv图片
            feaAction = new FeaAction();
            aa = feaAction.feaPretreatment( data);
            if (aa != null) {
                presenter.updateFaceFrame(changeSituation(aa.rc[0].left, aa.rc[0].top
                        , aa.rc[0].right, aa.rc[0].bottom), 640, 360);
                hasFace = true;
                if (isSlow) {
                    isSlow = false;
                }
                if (!isVoice && voiceTips) {
                    isVoice = true;
                    soundPool.play(soundID.get(3), 1, 1, 0, 0, 1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isVoice = false;
                        }
                    }, 30 * 1000);
                }
            } else {
                presenter.updateFaceFrame(changeSituation(0, 0, 0, 0), 640, 360);
                if (noFaceCount < noFace * 10)
                    noFaceCount++;
                if (noFaceCount == noFace * 10) {
                    clearMode();
                    if (!isSlow) {
                        isSlow = true;
                    }
                }
                hasFace = false;
            }
        }
        if (hasFace) {
            if (WC && !WCing && !runCompare) {
                WCing = true;
                feaAction.doFeaAction(FeaAction.FEA_CASE.DO_CHECK_WHITE);
                faceBit= YUVUtils.yuv2Bitmap( data,1280,720);
            } else if (runCompare && !WCing) {
                if (AC && !ACPass) {
                    if (ACCountN == 0 && ACCountYR == 0 && ACCountYL == 0)
                        presenter.updateCompareInfoView(new CompareInfo(null, null, "检测到人脸，请左右摆动脸部以便通过活体检测", null, -1, Color.WHITE, View.GONE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.ONLY_TEXT));
                    if (aa.info[0].ptNose.x < (2 * aa.rc[0].left + aa.rc[0].right) / 3)
                        ACCountYL++;
                    else if (aa.info[0].ptNose.x > (aa.rc[0].left + 2 * aa.rc[0].right) / 3)
                        ACCountYR++;
                    else
                        ACCountN++;
                    if (ACCountN >= 200) {
                        clearMode();
                        return;
                    }
                    if (ACCountYR >= 1 && ACCountYL >= 1) {
                        ACPass = true;
                        presenter.updateCompareInfoView(new CompareInfo(null, null, "检测到人脸，请左右摆动脸部以便通过活体检测", null, -1, Color.WHITE, View.GONE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.ONLY_TEXT));
                    }
                    return;
                } else if (ACPass && AC && (aa.info[0].ptNose.x < (aa.rc[0].left + aa.rc[0].right)
                        / 2 - 16) || (aa.info[0].ptNose.x > (aa.rc[0].left + aa.rc[0].right) / 2 + 16)) {
                    return;
                } else
                    ACPass = false;
                if (aa.info[0].ptNose.x > (aa.rc[0].left + 2 * aa.rc[0].right) / 3 || aa.info[0].ptNose.x
                        < (2 * aa.rc[0].left + aa.rc[0].right) / 3) {
                    noFaceCount++;
                    return;
                }
                if (noFaceCount > noFace * 10) {
                    clearMode();
                }
                runCompare = false;
                noFaceCount = 0;
                faceBit= YUVUtils.yuv2Bitmap( data,1280,720);
                faceBit = ImgUtils.getUtils().adjustBitmap(faceBit, aa, 2);
                com_fea = feaAction.doFeaAction(FeaAction.FEA_CASE.DO_FACE_COMPARE);
                float score = 0;
                if (com_fea != null)
                    score = GFace.feaCompare(modFea, com_fea);
                score = (float) (Math.round(score * 100)) / 100;
                onCompareFinish(score);
            }
        }
    }

    public void onCompareFinish(float score) {
        int i = 0;
        while (CBStatus != 2 && BW) {
            synchronized (this) {
                try {
                    wait(10);
                    i++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (i > 100)
                    break;
            }
        }
        if (score <= 0 || score >= 100&&com_fea!=null) {
            presenter.updateCompareInfoView(new CompareInfo(null, null, "算法出错，请稍后重试", null, -1, Color.RED, View.GONE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.ERROR_TEXT));
            GFace.unInit();
            presenter.reLoad();
            return;
        }
        int mistakeStatus;
        String mistakeValues;
        String resInfo = "姓名：" + card[1] + "\n性别："
                + card[2] + "\n民族：" + card[3] + "\n身份证号：" +
                card[6].replace(card[6].substring(4, 14), "**********") + "\n证件有效期：" + card[8];
        if (score > comScore && !isBlack && is18 && validTime >= 0) {
            mistakeStatus = 1;
            mistakeValues = "比对通过 分值：" + score;
        } else {
            if (isBlack) {
                mistakeStatus = 3;
                mistakeValues = "比对失败(黑名单人员) 分值：" + score;
            } else if (!is18 && AW) {
                mistakeStatus = 9;
                mistakeValues = "比对失败(年龄不符合要求) 分值：" + score;
            } else if (validTime < 0) {
                mistakeStatus = 8;
                mistakeValues = "比对失败(证件过期) 分值：" + score;
            } else {
                mistakeStatus = 2;
                mistakeValues = "比对失败 分值：" + score;
            }
        }
        CompareInfo info = null;
        int resBmp = 0;
        switch (mistakeStatus) {
            case 2:
                resBmp = R.mipmap.compare_fail_error_man;
                break;
            case 3:
                resBmp = R.mipmap.compare_fail_contact_cast;
                break;
            case 8:
                resBmp = R.mipmap.compare_fail_out_date;
                break;
            case 9:
                resBmp = R.mipmap.compare_fail_age_invalid;
                break;
        }

        if (mistakeStatus == 1) {
            soundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
            info = new CompareInfo(faceBit, null, resInfo, "分值：" + score, R.mipmap.compare_pass, Color.WHITE, View.VISIBLE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.FACE_WITH_TEXT_PASS);
        } else {
            soundPool.play(soundID.get(2), 1, 1, 0, 0, 1);
            info = new CompareInfo(faceBit, null, resInfo, null, resBmp, Color.WHITE, View.GONE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.FACE_WITH_TEXT_FAIL);
        }
        presenter.updateCompareInfoView(info);
        String personID = "";
        if (isBlack) {
            List<Person> p = FaceCardApplication.getApplication().getDaoSession().getPersonDao()
                    .queryBuilder().where(PersonDao.Properties.Id_card.eq(card[6])).list();
            if (p.size() > 0)
                personID = "" + p.get(0).getPerson_id();
        }

        Date date = new Date();
        String capture_time = DateFormat.format("yyyy-MM-dd HH:mm:ss", date).toString();
        String facePath = FACE_IMG + "/" + DateFormat.format("yyyyMMdd_HH_mm_ss",
                Calendar.getInstance(Locale.CHINA)).toString() + "_face_img.jpg";
        ImgUtils.getUtils().saveBitmap(faceBit,facePath);
        startUpload(new ReLoadInfo(null, "10", card[6], card[1], personID, card[10], facePath, "" + score, "" + mistakeStatus, mistakeValues, capture_time));
        saveHistory(faceBit, card, facePath, mistakeValues, score, date);
        isBlack = false;
        is18 = true;
        ACCountYR = 0;
        ACCountYL = 0;
        ACCountN = 0;
        if (black != null)
            black.clear();
        CBStatus = 0;
        disCount++;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (IDCardUtils.isRun() && disCount == 1) {
                    presenter.updateCompareInfoView(new CompareInfo(null, null, "", "", -1, View.GONE, View.GONE, View.VISIBLE, View.GONE, CompareInfo.ChangeType.CLEAR_All));
                }
                disCount--;
            }
        }, 1500);

        IDCardUtils.getUtils().setIDCardReaderRun();
    }

    @Override
    public void onFeaGet(float[] fea, GFace.FaceInfo face) {
        String path = LIB_DIR + "/temp_white.jpg";
        faceBit = BitmapFactory.decodeFile(path);
        faceBit = ImgUtils.getUtils().adjustBitmap(faceBit, face, 2);
        Date date = new Date();
        String capture_time = DateFormat.format("yyyy-MM-dd HH:mm:ss", date).toString();
        String facePath = FACE_IMG + "/" + DateFormat.format("yyyyMMdd_HH_mm_ss",
                Calendar.getInstance(Locale.CHINA)).toString() + "_face_img.jpg";
        float score = 0;
        long lp = 0;
        String wfp = "";
        if (fea != null) {
            for (WFIndex wt : WFL) {
                float temp = 0;
                if (wt.getFace_path() != null && wt.getFea() != null)
                    temp = GFace.feaCompare(wt.getFea(), fea);
                if (temp > score) {
                    score = temp;
                    lp = wt.getPerson_id();
                    wfp = wt.getFace_path();
                }
            }
        }
        score = (float) (Math.round(score * 100)) / 100;
        if (score <= 0 || score >= 100) {

        }
        if (score > 65) {
            final float finalScore = score;
            final String finalWfp = wfp;
            final Person pp = FaceCardApplication.getApplication().getDaoSession().getPersonDao().load(lp);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    presenter.updateCompareInfoView(new CompareInfo(BitmapFactory.decodeFile(finalWfp), faceBit, "", "核查通过（白名单自动比对） 分值：" + finalScore, Color.GREEN, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, CompareInfo.ChangeType.WHITE_RES));
                    disCount++;
                }
            });
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (IDCardUtils.isRun() && disCount == 1) {
                        presenter.updateCompareInfoView(new CompareInfo(null, null, "", "", -1, View.GONE, View.GONE, View.GONE, View.GONE, CompareInfo.ChangeType.CLEAR_All));
                    }
                    disCount--;
                }
            }, 1500);
            //String data_type,String id_card,String person_name,String card_img_file,String face_img
            // ,String group_code,String score,String mistake_status
            card = new String[]{"0", pp.getPerson_name(), pp.getSex(), "", pp.getBirthday(),
                    pp.getAddress(), pp.getId_card(), "", "", "", wfp};
            startUpload(new ReLoadInfo(null, "10", card[6], card[1], "" + lp, card[10], facePath, "" + score, "4", "核查通过（白名单自动比对）分值：" + finalScore, capture_time));
            saveHistory(faceBit, card, facePath, "核查通过（白名单自动比对）分值：" + finalScore, score, date);
        }
        WCing = false;
    }
}
