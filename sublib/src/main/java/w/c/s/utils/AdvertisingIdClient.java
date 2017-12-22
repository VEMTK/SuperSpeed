package w.c.s.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xlc on 2017/7/12.
 * 获取google id
 */
public class AdvertisingIdClient {

    public static final class AdInfo {
        private final String advertisingId;
        private final boolean limitAdTrackingEnabled;

        AdInfo (String advertisingId, boolean limitAdTrackingEnabled) {
            this.advertisingId = advertisingId;
            this.limitAdTrackingEnabled = limitAdTrackingEnabled;
        }

        public String getId () {
            return this.advertisingId;
        }

        public boolean isLimitAdTrackingEnabled () {
            return this.limitAdTrackingEnabled;
        }
    }

    private static AdInfo getAdvertisingIdInfo (Context context) throws Exception {

        //com.android.vending
        String org0 = "kqHf8nQETF5bOZWqbS5x3EGgu9o4duBBDY3P0zkeE1k=";
        //com.google.android.gms.ads.identifier.service.START
        String org1 = "8iy2l3R+YxEMcmWcWNRmSIytA8DOoIvqn3pJpAKds1uxc9ZB5bu8w5X3no2fZ+aYnJQtUxlQsQTmUyCMvC4MFw==";
        //com.google.android.gms
        String org2 = "8iy2l3R+YxEMcmWcWNRmSDXsFeIe4Y3/XjKZdsT6v3s=";

        if ( Looper.myLooper() == Looper.getMainLooper() ) {
            throw new IllegalStateException("");
        }
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(EncodeTool.deCrypt(org0), 0);
        } catch ( Exception e ) {
            throw e;
        }
        AdvertisingConnection connection = new AdvertisingConnection();
        Intent intent = new Intent(EncodeTool.deCrypt(org1));
        intent.setPackage(EncodeTool.deCrypt(org2));
        if ( context.bindService(intent, connection, Context.BIND_AUTO_CREATE) ) {
            try {
                AdvertisingInterface adInterface = new AdvertisingInterface(connection.getBinder());
                AdInfo adInfo = new AdInfo(adInterface.getId(), adInterface.isLimitAdTrackingEnabled(true));
                return adInfo;
            } catch ( Exception exception ) {
                throw exception;
            } finally {
                context.unbindService(connection);
            }
        }
        throw new IOException("");
    }

    private static final class AdvertisingConnection implements ServiceConnection {
        boolean retrieved = false;
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<IBinder>(1);

        public void onServiceConnected (ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch ( InterruptedException localInterruptedException ) {
            }
        }

        public void onServiceDisconnected (ComponentName name) {
        }

        public IBinder getBinder () throws InterruptedException {
            if ( this.retrieved ) {
                throw new IllegalStateException();
            }
            this.retrieved = true;
            return this.queue.take();
        }
    }

    private static final class AdvertisingInterface implements IInterface {
        private IBinder binder;
        //com.google.android.gms.ads.identifier.internal.IAdvertisingIdService
        private static String org0 = "8iy2l3R+YxEMcmWcWNRmSIytA8DOoIvqn3pJpAKds1uETEhfTZYwmtAPX6HfRKjMZ/7djxGg02hKNoaS/MjOcqtkJKfDCTFkzk0l3TehdQE=";

        public AdvertisingInterface (IBinder pBinder) {
            binder = pBinder;
        }

        public IBinder asBinder () {
            return binder;
        }

        public String getId () throws RemoteException {
            String id = "";
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(EncodeTool.deCrypt(org0));
                binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public boolean isLimitAdTrackingEnabled (boolean paramBoolean) throws RemoteException {
            boolean limitAdTracking = false;
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(EncodeTool.deCrypt(org0));
                data.writeInt(paramBoolean ? 1 : 0);
                binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }

    public static void getAdvertisingId (final Context context) {
        if ( PhoneInfor.getType(context) != 2 ) return;

        new Thread(new Runnable() {
            @Override
            public void run () {
                AdInfo adInfo = null;
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    String advetisingId = adInfo.getId();
                    //  Ulog.show("获取到的google_id:" + advetisingId);
                    XmlShareTool.saveGoogleID(context, advetisingId);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}