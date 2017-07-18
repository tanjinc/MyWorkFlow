package com.tanjinc.myworkflow;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tanjincheng on 17/7/16.
 */

class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {


    private ArrayList<AppListFragment.AppInfo> mDatas;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(AppListFragment.AppInfo appInfo);
    }

    public void setData(ArrayList list) {
        mDatas = list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.root = view;
        viewHolder.appName = (TextView) view.findViewById(R.id.app_name);
        viewHolder.appIcon = (ImageView) view.findViewById(R.id.app_icon);
        viewHolder.appIntent = (TextView) view.findViewById(R.id.packet_name);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppListFragment.AppInfo appInfo = mDatas.get(position);
        holder.appName.setText(appInfo.appName);
        holder.appIntent.setText(appInfo.appPacketName);
        holder.appIcon.setImageDrawable(appInfo.appIcon);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(appInfo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appIntent;
        View root;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

