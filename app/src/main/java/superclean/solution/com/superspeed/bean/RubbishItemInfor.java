package superclean.solution.com.superspeed.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwl on 2017/11/29.
 */

public class RubbishItemInfor implements Parcelable {

    private int iconRes = -1;
    private String name = "";
    private boolean check = true;
    private List<AppProcessInfo> childList = new ArrayList<AppProcessInfo>();

    public RubbishItemInfor () {
    }

    public RubbishItemInfor (int iconRes, String name, boolean check, List<AppProcessInfo> childList) {
        this.iconRes = iconRes;
        this.name = name;
        this.check = check;
        this.childList = childList;
    }

    public int getIconRes () {
        return iconRes;
    }

    public void setIconRes (int iconRes) {
        this.iconRes = iconRes;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public boolean getChecked () {
        return check;
    }

    public void setChecked (boolean check) {
        this.check = check;
    }

    public List<AppProcessInfo> getChildList () {
        return childList;
    }

    public void setChildList (List<AppProcessInfo> childList) {
        this.childList = childList;
    }

    public long getGroupSize (boolean falg) {
        long size = 0;
        for ( int i = 0; i < childList.size(); i++ ) {
            if ( falg ) {
                if ( childList.get(i).isCheck() ) size = size + childList.get(i).getMemory();
            } else {
                size = size + childList.get(i).getMemory();
            }
        }
        return size;
    }


    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeInt(this.iconRes);
        dest.writeString(this.name);
        dest.writeByte(this.check ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.childList);
    }

    protected RubbishItemInfor (Parcel in) {
        this.iconRes = in.readInt();
        this.name = in.readString();
        this.check = in.readByte() != 0;
        this.childList = in.createTypedArrayList(AppProcessInfo.CREATOR);
    }

    public static final Creator<RubbishItemInfor> CREATOR = new Creator<RubbishItemInfor>() {
        @Override
        public RubbishItemInfor createFromParcel (Parcel source) {
            return new RubbishItemInfor(source);
        }

        @Override
        public RubbishItemInfor[] newArray (int size) {
            return new RubbishItemInfor[size];
        }
    };
}
