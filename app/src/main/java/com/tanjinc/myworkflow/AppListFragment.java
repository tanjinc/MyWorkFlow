package com.tanjinc.myworkflow;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanjincheng on 17/7/16.
 */
public class AppListFragment extends DialogFragment {

    private static final String TAG = "AppListFragment";

    private AppListFragment mAppListFragment;

    private ProgressBar mLoadingView;
    private RecyclerView mRecyclerView;
    private AppListAdapter mAdapter;
    private ArrayList<AppInfo> mDataArray = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppListFragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_list_layout, container);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.app_recycler_view);

        mLoadingView = (ProgressBar) view.findViewById(R.id.loading_view);
        mAdapter = new AppListAdapter();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter.setData(mDataArray);
        mAdapter.setOnItemClickListener(new AppListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AppInfo appInfo) {
                Constants.appPacketName = appInfo.appPacketName;
                Utils.setAutoBoxRecording(getActivity(), appInfo.appPacketName, true);
                Utils.startApp(getContext(),appInfo.appIntent);
//                XmlUtils.saveXml(appInfo.appPacketName+".xml",appInfo.appPacketName, null);
                dismiss();

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getAppList();
                mHandler.sendEmptyMessage(100);
            }
        }).start();

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    mLoadingView.setVisibility(View.GONE);
                    mAdapter.setData(mDataArray);
                    break;
            }
        }
    };

    private ArrayList<AppInfo> getAppList() {
        ArrayList<AppInfo> result = new ArrayList<>();
        PackageManager pm = this.getActivity().getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
//            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 非系统应用
                AppInfo info = new AppInfo();
                info.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                info.appPacketName = packageInfo.packageName;
                info.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                // 获取该应用安装包的Intent，用于启动该应用
                info.appIntent = pm.getLaunchIntentForPackage(packageInfo.packageName);
                mDataArray.add(info);
//            } else {
//                // 系统应用　　　　　　　　
//            }

        }
        return result;
    }

    public class AppInfo {
        String appName;
        String appPacketName;
        Drawable appIcon;
        Intent appIntent;
    }
}
