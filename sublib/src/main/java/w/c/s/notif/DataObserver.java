//package w.c.s.notif;
//
//import android.database.ContentObserver;
//import android.os.Handler;
//
//public class DataObserver extends ContentObserver {
//
//    private SubNotif a;
//
//    public DataObserver (SubNotif a, Handler handler) {
//        super(handler);
//        this.a=a;
//    }
//    @Override
//    public void onChange(boolean selfChange) {
//        super.onChange(selfChange);
//        a.dataObserverChange();
//    }
//}
