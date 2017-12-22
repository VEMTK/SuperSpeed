package superclean.solution.com.superspeed.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwl on 2017/11/16.
 */

public class MemoryCleanListInfor implements Parcelable {

    private String title = "";
    private boolean isAllSelect = true;
    private List<AppProcessInfo> appList = new ArrayList<AppProcessInfo>();

    public MemoryCleanListInfor () {
    }

    public MemoryCleanListInfor (String title) {
        this.title = title;
        this.isAllSelect = true;
        this.appList = new ArrayList<AppProcessInfo>();
    }

    public MemoryCleanListInfor (String title, List<AppProcessInfo> appList) {
        this.title = title;
        this.appList = appList;
    }

    public MemoryCleanListInfor (String title, boolean isAllSelect, List<AppProcessInfo> appList) {
        this.title = title;
        this.isAllSelect = isAllSelect;
        this.appList = appList;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public List<AppProcessInfo> getAppList () {
        return appList;
    }

    public void setAppList (List<AppProcessInfo> appList) {
        this.appList = appList;
    }

    public boolean getAllSelect () {
        return isAllSelect;
    }

    public void setAllSelect (boolean allSelect) {
        isAllSelect = allSelect;
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeByte(this.isAllSelect ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.appList);
    }

    protected MemoryCleanListInfor (Parcel in) {
        this.title = in.readString();
        this.isAllSelect = in.readByte() != 0;
        this.appList = in.createTypedArrayList(AppProcessInfo.CREATOR);
    }

    public static final Creator<MemoryCleanListInfor> CREATOR = new Creator<MemoryCleanListInfor>() {
        @Override
        public MemoryCleanListInfor createFromParcel (Parcel source) {
            return new MemoryCleanListInfor(source);
        }

        @Override
        public MemoryCleanListInfor[] newArray (int size) {
            return new MemoryCleanListInfor[size];
        }
    };
}
