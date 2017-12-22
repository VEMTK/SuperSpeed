package superclean.solution.com.superspeed.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.bean.AppProcessInfo;
import superclean.solution.com.superspeed.bean.RubbishItemInfor;
import superclean.solution.com.superspeed.controller.IDockingController;
import superclean.solution.com.superspeed.listener.OnRubbishClickListener;
import superclean.solution.com.superspeed.utils.ImageUtils;
import superclean.solution.com.superspeed.utils.OtherUtil;
import superclean.solution.com.superspeed.utils.StorageUtil;

/**
 * Created by admin on 2017/10/17.
 */

public class RubbishCleanExpandAdapter extends BaseExpandableListAdapter implements IDockingController {

    private Context context;
    private ExpandableListView expandListView;
    private List<RubbishItemInfor> groupListData;
    private OnRubbishClickListener rubbishListener;

    public RubbishCleanExpandAdapter (Context context, ExpandableListView expandListView, List<RubbishItemInfor> groupListData, OnRubbishClickListener rubbishListener) {
        this.context = context;
        this.expandListView = expandListView;
        this.groupListData = groupListData;
        this.rubbishListener = rubbishListener;
    }

    @Override
    public int getDockingState (int firstVisibleGroup, int firstVisibleChild) {
        if ( firstVisibleChild == -1 && !expandListView.isGroupExpanded(firstVisibleGroup) ) {
            return DOCKING_HEADER_HIDDEN;
        }
        if ( firstVisibleChild == getChildrenCount(firstVisibleGroup) - 1 ) {
            return IDockingController.DOCKING_HEADER_DOCKING;
        }
        return IDockingController.DOCKING_HEADER_DOCKED;
    }

    @Override
    public int getGroupCount () {
        return groupListData.size();
    }

    @Override
    public int getChildrenCount (int groupPosition) {
        return groupListData.get(groupPosition).getChildList().size();
    }

    @Override
    public RubbishItemInfor getGroup (int groupPosition) {
        return groupListData.get(groupPosition);
    }

    @Override
    public AppProcessInfo getChild (int groupPosition, int childPosition) {
        return groupListData.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId (int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId (int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds () {
        return false;
    }

    @Override
    public View getGroupView (final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup groupHolder;
        if ( convertView == null ) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_rubbish_groud, parent, false);
            groupHolder = new ViewHolderGroup();

            groupHolder.groupIcon = (ImageView) convertView.findViewById(R.id.rubbish_group_icon);
            groupHolder.groupName = (TextView) convertView.findViewById(R.id.rubbish_group_name);
            groupHolder.groupDirect = (ImageView) convertView.findViewById(R.id.rubbish_group_direct);
            groupHolder.groupSize = (TextView) convertView.findViewById(R.id.rubbish_group_size);
            groupHolder.groupCheckLay = (LinearLayout) convertView.findViewById(R.id.rubbish_group_checkLay);
            groupHolder.groupCheck = (ImageView) convertView.findViewById(R.id.rubbish_group_check);

            convertView.setTag(groupHolder);
        } else {
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }

        final RubbishItemInfor groupItem = groupListData.get(groupPosition);

        groupHolder.groupIcon.setImageResource(groupItem.getIconRes());
        groupHolder.groupName.setText(groupItem.getName());

        groupHolder.groupDirect.setRotation(0);
        if ( isExpanded ) {
            //            groupHolder.groupDirect.setImageResource(R.drawable.junk_group_arrow_down);
            groupHolder.groupDirect.setRotation(180);
        }

        groupHolder.groupSize.setText(StorageUtil.convertStorage(groupItem.getGroupSize(false)));

        groupHolder.groupCheck.setImageDrawable(getSelectCountRes(groupPosition));

        if ( rubbishListener != null ) rubbishListener.onSelected(getSelectSize());

        groupHolder.groupCheckLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                groupItem.setChecked(!groupItem.getChecked());

                List<AppProcessInfo> appList = groupItem.getChildList();
                for ( int i = 0; i < appList.size(); i++ ) {
                    appList.get(i).setCheck(groupItem.getChecked());
                }

                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public View getChildView (final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolderChild childHolder;
        if ( convertView == null ) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_rubbish_child, parent, false);
            childHolder = new ViewHolderChild();

            childHolder.childIcon = (ImageView) convertView.findViewById(R.id.rubbish_chid_icon);
            childHolder.childName = (TextView) convertView.findViewById(R.id.rubbish_chid_name);
            childHolder.childSize = (TextView) convertView.findViewById(R.id.rubbish_child_size);
            childHolder.childCheckLay = (LinearLayout) convertView.findViewById(R.id.rubbish_child_checkLay);
            childHolder.childCheckImg = (ImageView) convertView.findViewById(R.id.rubbish_chlid_check);

            convertView.setTag(childHolder);
        } else {
            childHolder = (ViewHolderChild) convertView.getTag();
        }

        final AppProcessInfo childItem = groupListData.get(groupPosition).getChildList().get(childPosition);

        childHolder.childIcon.setImageBitmap(childItem.getAppIcon());
        childHolder.childName.setText(childItem.getAppName());
        if ( !TextUtils.isEmpty(childItem.getPath()) && childItem.getPath().toLowerCase().endsWith(".apk") ) {
            String appInstall = "[" + getCusString(R.string.str_notinstall) + "] ";
            if ( OtherUtil.isAppAvilible(context, childItem.getAppPkg()) ) {
                appInstall = "[" + getCusString(R.string.str_install) + "] ";
            }
            String textStr = childItem.getAppName() + "<br/><small><font color=\"#a0a0a0\">" + appInstall + childItem.getVersion() + "</font></small>";
            childHolder.childName.setText(Html.fromHtml(textStr, null, null));
        }
        childHolder.childSize.setText(StorageUtil.convertStorage(childItem.getMemory()));

        if ( childItem.isCheck() ) {
            childHolder.childCheckImg.setImageDrawable(ImageUtils.tintDrawable(context, R.drawable.checkbox_checked, R.color.ringColor));
        } else {
            childHolder.childCheckImg.setImageDrawable(ImageUtils.tintDrawable(context, R.drawable.checkbox_unchecked, R.color.grey));
        }

        childHolder.childCheckLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                childItem.setCheck(!childItem.isCheck());
                notifyDataSetChanged();
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if ( groupPosition != 0 ) {
                    if ( rubbishListener != null ) rubbishListener.onItemClick(childItem);
                }
            }
        });

        return convertView;
    }

    private String getCusString (int resID) {
        return context.getResources().getString(resID);
    }

    @Override
    public boolean isChildSelectable (int groupPosition, int childPosition) {
        return true;
    }

    public Drawable getSelectCountRes (int groupPosition) {
        List<AppProcessInfo> appList = groupListData.get(groupPosition).getChildList();
        int size = 0;
        for ( int i = 0; i < appList.size(); i++ ) {
            if ( appList.get(i).isCheck() ) {
                size++;
            }
        }

        if ( size == 0 ) {
            return ImageUtils.tintDrawable(context, R.drawable.checkbox_unchecked, R.color.grey);
        } else if ( size == appList.size() ) {
            return ImageUtils.tintDrawable(context, R.drawable.checkbox_checked, R.color.ringColor);
        }
        return ImageUtils.tintDrawable(context, R.drawable.checkbox_partialchecked, R.color.ringColor);
    }

    public void rufushData (List<RubbishItemInfor> groupListData) {
        this.groupListData = groupListData;
        notifyDataSetChanged();
    }

    public List<RubbishItemInfor> getGroupListData () {
        return groupListData;
    }

    public long getCheckedSize () {
        long size = 0;
        for ( int i = 0; i < groupListData.size(); i++ ) {
            if ( groupListData.get(i).getChecked() ) size = size + groupListData.get(i).getGroupSize(true);
        }
        return size;
    }

    public long getSelectSize () {
        long size = 0;
        for ( int i = 0; i < groupListData.size(); i++ ) {
            List<AppProcessInfo> appList = groupListData.get(i).getChildList();
            for ( int k = 0; k < appList.size(); k++ ) {
                if ( appList.get(k).isCheck() ) size = size + appList.get(k).getMemory();
            }
        }
        return size;
    }

    class ViewHolderGroup {
        ImageView groupIcon;
        TextView groupName;
        ImageView groupDirect;
        TextView groupSize;
        LinearLayout groupCheckLay;
        ImageView groupCheck;
    }

    class ViewHolderChild {
        ImageView childIcon;
        TextView childName;
        TextView childSize;
        LinearLayout childCheckLay;
        ImageView childCheckImg;
    }
}