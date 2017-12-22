//package w.c.s.notif;
//
//import android.database.ContentObserver;
//import android.os.Handler;
//
///**
// * Created by xlc on 2017/5/24.
// */
//
//public class ScreenObserver extends ContentObserver {
//
//    private SubNotif aObject;
//
//    public ScreenObserver (SubNotif a, Handler handler) {
//        super(handler);
//        this.aObject = a;
//    }
//    @Override
//    public void onChange(boolean selfChange) {
//        super.onChange(selfChange);
//        aObject.sceenObserverChange();
//    }
//}
//
