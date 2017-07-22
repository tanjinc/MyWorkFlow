package com.tanjinc.myworkflow;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanjinc.myworkflow.utils.XmlUtils;

import java.util.ArrayList;

/**
 * Created by tanjincheng on 17/7/20.
 */
public class TaskListLayout extends FrameLayout {

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(TaskInfo taskInfo);
    }

    private ArrayList<TaskInfo> mTaskInfoArrayList = new ArrayList<>();


    public TaskListLayout(Context context) {
        super(context);
        init(context);

    }

    public TaskListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public TaskListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View.inflate(context, R.layout.task_list_layout, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.task_recycler_view);
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        updateData();
    }

    public void updateData() {
        mTaskInfoArrayList.clear();
        for (String s: XmlUtils.queryXmlFiles() ) {
            mTaskInfoArrayList.add(new TaskInfo(s));
        }
        mAdapter.setData(mTaskInfoArrayList);
    }

    public void addTask(TaskInfo taskInfo) {
        mTaskInfoArrayList.add(taskInfo);
        mAdapter.setData(mTaskInfoArrayList);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    static public class TaskInfo {
        String taskName;
        public TaskInfo(String taskName) {
            this.taskName = taskName;
        }
    }



    class Adapter extends RecyclerView.Adapter<TaskViewHolder> {

        private ArrayList<TaskInfo> mTaskInfos;

        private int[] colors = {R.drawable.rectangle, R.drawable.rectangle1,
                R.drawable.rectangle2, R.drawable.rectangle3, R.drawable.rectangle4, R.drawable.rectangle5,
                R.drawable.rectangle6, R.drawable.rectangle7};
        private int[] icons = {R.drawable.weibo, R.drawable.weixin, R.drawable.camera, R.drawable.music};

        public void setData(ArrayList<TaskInfo> data) {
            mTaskInfos = data;
            notifyDataSetChanged();
        }

        @Override
        public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, null);;
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskViewHolder holder, int position) {
            if (mTaskInfos != null && mTaskInfos.size() > 0) {
                final TaskInfo taskInfo = mTaskInfos.get(position);
                holder.mTaskNameTv.setText(taskInfo.taskName.split("\\.")[0]);
                holder.mTaskNameTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(taskInfo);
                        }
                    }
                });

                if (taskInfo.taskName.equals("微信")) {
                    holder.root.setBackground(getResources().getDrawable(R.drawable.rectangle3));
                    holder.mIcon.setImageDrawable(getResources().getDrawable(R.drawable.weixin));
                } else {
                    holder.root.setBackground(getResources().getDrawable(colors[position % colors.length]));
                    holder.mIcon.setImageDrawable(getResources().getDrawable(icons[position % 4]));
                }
            }

        }

        @Override
        public int getItemCount() {
            return mTaskInfos != null ? mTaskInfos.size() : 0;
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView mTaskNameTv;
        ImageView mIcon;
        ViewGroup root;

        public TaskViewHolder(View itemView) {
            super(itemView);
            root = (ViewGroup) itemView.findViewById(R.id.task_name_layout);
            mTaskNameTv = (TextView) itemView.findViewById(R.id.task_name_id);
            mIcon = (ImageView) itemView.findViewById(R.id.task_icon);
        }
    }
}
