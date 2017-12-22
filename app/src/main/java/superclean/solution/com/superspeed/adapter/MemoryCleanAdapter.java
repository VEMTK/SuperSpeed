package superclean.solution.com.superspeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.bean.AppProcessInfo;
import superclean.solution.com.superspeed.bean.MemoryCleanListInfor;
import superclean.solution.com.superspeed.controller.IDockingController;
import superclean.solution.com.superspeed.listener.OnMemoryItemClickListener;
import superclean.solution.com.superspeed.utils.ImageUtils;
import superclean.solution.com.superspeed.utils.StorageUtil;


public class MemoryCleanAdapter extends BaseExpandableListAdapter implements IDockingController {

    private Context context;
    private LayoutInflater infater;
    private ExpandableListView mListView;
    private List<MemoryCleanListInfor> groupData;
    private OnMemoryItemClickListener onItemClickListener;

    public MemoryCleanAdapter (Context context, List<MemoryCleanListInfor> groupData, ExpandableListView mListView) {
        this.context = context;
        this.groupData = groupData;
        this.mListView = mListView;
        infater = LayoutInflater.from(context);

        if ( groupData == null ) this.groupData = new ArrayList<MemoryCleanListInfor>();
    }

    @Override
    public int getGroupCount () {
        return groupData != null ? groupData.size() : 0;
    }

    @Override
    public int getChildrenCount (int groupPosition) {
        return groupData.get(groupPosition).getAppList().size();
    }

    @Override
    public MemoryCleanListInfor getGroup (int groupPosition) {
        return groupData.get(groupPosition);
    }

    @Override
    public AppProcessInfo getChild (int groupPosition, int childPosition) {
        try {
            return groupData.get(groupPosition).getAppList().get(childPosition);
        } catch ( Exception e ) {
        }
        return null;
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
        ViewHolderGroup holder = null;
        if ( convertView == null ) {
            holder = new ViewHolderGroup();
            convertView = infater.inflate(R.layout.listview_memory_clean_group, null);
            holder.selectAll = (ImageView) convertView.findViewById(R.id.memory_child_check);
            holder.selectCounts = (TextView) convertView.findViewById(R.id.memory_select_counts);
            holder.title_textView = (TextView) convertView.findViewById(R.id.memory_chid_name);
            holder.groupStatuesLay = (LinearLayout) convertView.findViewById(R.id.memory_group_statuesLay);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderGroup) convertView.getTag();
        }
        int selectCounts = getSelectCounts(groupPosition);

        holder.selectCounts.setVisibility(View.VISIBLE);
        holder.groupStatuesLay.setVisibility(View.VISIBLE);
        holder.selectCounts.setText(selectCounts + "");

        holder.title_textView.setText(getGroup(groupPosition).getTitle());

        if ( selectCounts >= groupData.get(groupPosition).getAppList().size() ) {
            holder.selectAll.setImageDrawable(ImageUtils.tintDrawable(context, R.drawable.checkbox_checked, R.color.ringColor));
        } else if ( selectCounts <= 0 ) {
            holder.selectAll.setImageDrawable(ImageUtils.tintDrawable(context, R.drawable.checkbox_unchecked, R.color.grey));
        } else {
            holder.selectAll.setImageDrawable(ImageUtils.tintDrawable(context, R.drawable.checkbox_partialchecked, R.color.ringColor));
        }

        holder.groupStatuesLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if ( getGroup(groupPosition).getAppList().size() > 0 ) {
                    changeGroupCheckStatues(groupPosition);
                }

                if ( onItemClickListener != null ) {
                    onItemClickListener.onItemClick(groupData.get(groupPosition).getAppList());
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView (final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder holder = null;

        if ( convertView == null ) {
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_memory_clean, null);

            holder.appIcon = (ImageView) convertView.findViewById(R.id.memory_chid_icon);
            holder.appIconTV = (TextView) convertView.findViewById(R.id.memory_child_iconTV);
            holder.appName = (TextView) convertView.findViewById(R.id.memory_chid_name);
            holder.memory = (TextView) convertView.findViewById(R.id.memory_child_size);
            holder.checkLay = (LinearLayout) convertView.findViewById(R.id.memory_child_checkLay);
            holder.checkImg = (ImageView) convertView.findViewById(R.id.memory_child_check);

            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        final AppProcessInfo appInfo = getChild(groupPosition, childPosition);

        holder.appIcon.setImageBitmap(appInfo.getAppIcon());
        holder.appName.setText(appInfo.getAppName());

        holder.appIconTV.setText("");

        holder.memory.setText(StorageUtil.convertStorage(appInfo.getMemory()));
        holder.memory.setBackgroundResource(R.color.trans_00);
        holder.memory.setTextColor(context.getResources().getColor(R.color.grey));

        holder.checkLay.setVisibility(View.VISIBLE);

        if ( appInfo.isCheck() ) {
            holder.checkImg.setImageDrawable(ImageUtils.tintDrawable(context, R.drawable.checkbox_checked, R.color.ringColor));
        } else {
            holder.checkImg.setImageDrawable(ImageUtils.tintDrawable(context, R.drawable.checkbox_unchecked, R.color.grey));
        }

        holder.checkLay.setOnClickListener(new View.OnClickListener() {
            {
            }

            @Override
            public void onClick (View v) {
                boolean hasCheck = !appInfo.isCheck();
                appInfo.setCheck(hasCheck);
                if ( onItemClickListener != null ) {
                    onItemClickListener.onItemClick(groupData.get(groupPosition).getAppList());
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable (int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getDockingState (int firstVisibleGroup, int firstVisibleChild) {
        if ( firstVisibleChild == -1 && !mListView.isGroupExpanded(firstVisibleGroup) ) {
            return DOCKING_HEADER_HIDDEN;
        }
        if ( firstVisibleChild == getChildrenCount(firstVisibleGroup) - 1 ) {
            return IDockingController.DOCKING_HEADER_DOCKING;
        }
        return IDockingController.DOCKING_HEADER_DOCKED;
    }

    private void removeApp (int groupPosition, int childPosition) {

        AppProcessInfo appInfor = getChild(groupPosition, childPosition);

        if ( onItemClickListener != null ) {
            groupData.get(groupPosition).getAppList().get(childPosition).setCheck(false);
            onItemClickListener.onItemClick(groupData.get(groupPosition).getAppList());
            onItemClickListener.reLoadList(appInfor.getProcessName());
        }

        List<AppProcessInfo> allApp = groupData.get(groupPosition).getAppList();
        for ( int i = 0; i < allApp.size(); i++ ) {
            if ( allApp.get(i).getProcessName().equals(appInfor.getProcessName()) ) {
                allApp.remove(i);
                groupData.get(0).setAppList(allApp);
                notifyDataSetChanged();
                return;
            }
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener (OnMemoryItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void changeGroupCheckStatues (int groupPosition) {
        getGroup(groupPosition).setAllSelect(!getGroup(groupPosition).getAllSelect());
        for ( AppProcessInfo info : getGroup(groupPosition).getAppList() ) {
            info.setCheck(getGroup(groupPosition).getAllSelect());
        }
        notifyDataSetChanged();
    }

    public int getSelectCounts (int groupPosition) {
        if ( groupData.get(groupPosition).getAppList().size() <= 0 ) return 0;

        int count = 0;
        List<AppProcessInfo> appList = groupData.get(groupPosition).getAppList();
        for ( int i = 0; i < appList.size(); i++ ) {
            if ( appList.get(i).isCheck() ) {
                count++;
            }
        }
        return count;
    }

    public List<AppProcessInfo> getSelectedDatas () {
        List<AppProcessInfo> mlist = new ArrayList<AppProcessInfo>();
        for ( AppProcessInfo info : groupData.get(0).getAppList() ) {
            if ( info.isCheck() ) {
                mlist.add(info);
            }
        }
        return mlist;
    }

    class ViewHolderGroup {
        TextView title_textView;
        TextView selectCounts;
        ImageView selectAll;
        LinearLayout groupStatuesLay;
    }

    class ChildViewHolder {
        ImageView appIcon;
        TextView appIconTV;
        TextView appName;
        TextView memory;
        LinearLayout checkLay;
        ImageView checkImg;
    }
}