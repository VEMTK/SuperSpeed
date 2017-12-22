package superclean.solution.com.superspeed.bean;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppProcessInfo implements Parcelable, Comparable<AppProcessInfo> {

    private int dbID = -1;
    private String appName = "";
    private String processName = "";
    private int pid = -1;
    private int uid = -1;
    private Bitmap appIcon = null;
    private long memory = -1;
    private String cpu = "";
    private String status = "";
    private String threadsCount = "";
    private boolean check = false;
    private boolean isSystem = false;
    private String appPkg = "";
    private String path = "";
    private String version = "";
    //1:缓存、2:日志、3:临时、4:apk、5:其他文件
    private int type = 4;
    private String fileName="";

    public AppProcessInfo () {
    }

    public AppProcessInfo (String appPkg) {
        this.appPkg = appPkg;
    }

    public AppProcessInfo (int dbID, String path, int type) {
        this.dbID = dbID;
        this.path = path;
        this.type = type;
    }

    public AppProcessInfo (String processName, int pid, int uid) {
        this.processName = processName;
        this.appPkg = processName;
        this.pid = pid;
        this.uid = uid;
    }

    public AppProcessInfo (String appName, Bitmap appIcon, long memory, String appPkg, String path) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.memory = memory;
        this.appPkg = appPkg;
        this.path = path;
    }

    public AppProcessInfo (String appName, Drawable appIcon, long memory, String appPkg, String path, int type, boolean check) {
        this.appName = appName;
        this.appIcon = drawableToBitmap(appIcon);
        this.memory = memory;
        this.appPkg = appPkg;
        this.path = path;
        this.type = type;
        this.check = check;
    }

    public AppProcessInfo (String appName, String processName, int pid, int uid, Bitmap appIcon, long memory, String cpu, String status, String threadsCount, boolean check, boolean isSystem, String appPkg) {
        this.appName = appName;
        this.processName = processName;
        this.pid = pid;
        this.uid = uid;
        this.appIcon = appIcon;
        this.memory = memory;
        this.cpu = cpu;
        this.status = status;
        this.threadsCount = threadsCount;
        this.check = check;
        this.isSystem = isSystem;
        this.appPkg = appPkg;
    }

    public AppProcessInfo (String appName, String processName, int pid, int uid, Bitmap appIcon, long memory, String cpu, String status, String threadsCount, boolean check, boolean isSystem, String appPkg, String path) {
        this(appName, processName, pid, uid, appIcon, memory, cpu, status, threadsCount, check, isSystem, appPkg);
        this.path = path;
    }

    public int getDbID () {
        return dbID;
    }

    public void setDbID (int dbID) {
        this.dbID = dbID;
    }

    public String getAppName () {
        return appName;
    }

    public void setAppName (String appName) {
        this.appName = appName;
    }

    public String getProcessName () {
        return processName;
    }

    public void setProcessName (String processName) {
        this.processName = processName;
        this.appPkg = processName;
    }

    public int getPid () {
        return pid;
    }

    public void setPid (int pid) {
        this.pid = pid;
    }

    public int getUid () {
        return uid;
    }

    public void setUid (int uid) {
        this.uid = uid;
    }

    public Bitmap getAppIcon () {
        return appIcon;
    }

    public void setAppIcon (Bitmap appIcon) {
        this.appIcon = appIcon;
    }

    public void setAppIcon (Drawable appIcon) {
        this.appIcon = drawableToBitmap(appIcon);
    }

    public long getMemory () {
        return memory;
    }

    public void setMemory (long memory) {
        this.memory = memory;
    }

    public String getCpu () {
        return cpu;
    }

    public void setCpu (String cpu) {
        this.cpu = cpu;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getThreadsCount () {
        return threadsCount;
    }

    public void setThreadsCount (String threadsCount) {
        this.threadsCount = threadsCount;
    }

    public boolean isCheck () {
        return check;
    }

    public void setCheck (boolean check) {
        this.check = check;
    }

    public boolean isSystem () {
        return isSystem;
    }

    public void setSystem (boolean system) {
        isSystem = system;
    }

    public String getAppPkg () {
        return appPkg;
    }

    public void setAppPkg (String appPkg) {
        this.appPkg = appPkg;
    }

    public String getPath () {
        return path;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public String getVersion () {
        return version;
    }

    public void setVersion (String version) {
        this.version = version;
    }

    public int getType () {
        return type;
    }

    public void setType (int type) {
        this.type = type;
    }

    public Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),//
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public String getFileName () {
        return fileName;
    }

    public void setFileName (String fileName) {
        this.fileName = fileName;
    }

    public int compareTo (AppProcessInfo another) {
        if ( this.processName.compareTo(another.processName) == 0 ) {
            if ( this.memory < another.memory ) {
                return 1;
            } else if ( this.memory == another.memory ) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return this.processName.compareTo(another.processName);
        }
    }

    @Override
    public String toString () {
        return appName + "{ processName='" + processName + '\'' + ", pid=" + pid + ", uid=" + uid + ", appIcon=" + appIcon +//
                "\r\nmemory=" + memory + ", cpu='" + cpu + '\'' + ", status='" + status + '\'' + ", threadsCount='" + threadsCount + '\'' +//
                ", check=" + check + ", isSystem=" + isSystem + ", appPkg='" + appPkg + '\'' + '}';
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeInt(this.dbID);
        dest.writeString(this.appName);
        dest.writeString(this.processName);
        dest.writeInt(this.pid);
        dest.writeInt(this.uid);
        dest.writeParcelable(this.appIcon, flags);
        dest.writeLong(this.memory);
        dest.writeString(this.cpu);
        dest.writeString(this.status);
        dest.writeString(this.threadsCount);
        dest.writeByte(this.check ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSystem ? (byte) 1 : (byte) 0);
        dest.writeString(this.appPkg);
        dest.writeString(this.path);
        dest.writeString(this.version);
        dest.writeInt(this.type);
        dest.writeString(this.fileName);
    }

    protected AppProcessInfo (Parcel in) {
        this.dbID = in.readInt();
        this.appName = in.readString();
        this.processName = in.readString();
        this.pid = in.readInt();
        this.uid = in.readInt();
        this.appIcon = in.readParcelable(Bitmap.class.getClassLoader());
        this.memory = in.readLong();
        this.cpu = in.readString();
        this.status = in.readString();
        this.threadsCount = in.readString();
        this.check = in.readByte() != 0;
        this.isSystem = in.readByte() != 0;
        this.appPkg = in.readString();
        this.path = in.readString();
        this.version = in.readString();
        this.type = in.readInt();
        this.fileName = in.readString();
    }

    public static final Creator<AppProcessInfo> CREATOR = new Creator<AppProcessInfo>() {
        @Override
        public AppProcessInfo createFromParcel (Parcel source) {
            return new AppProcessInfo(source);
        }

        @Override
        public AppProcessInfo[] newArray (int size) {
            return new AppProcessInfo[size];
        }
    };
}
