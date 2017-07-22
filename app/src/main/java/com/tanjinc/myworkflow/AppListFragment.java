package com.tanjinc.myworkflow;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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

import com.tanjinc.myworkflow.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
//                Constants.appPacketName = appInfo.appPacketName;
                ViewManager.getInstance(getActivity().getApplicationContext()).setStatus(FloatBall.TASK_RECORD);
                Utils.setAutoBoxRecording(getActivity(), appInfo.appPacketName, true);
                Utils.startApp(getContext(),appInfo.appIntent);
//                XmlUtils.saveXml(appInfo.appPacketName+".xml",appInfo.appPacketName, null);
                dismiss();

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
//                getAppList();
                queryFilterAppInfo();
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

    private void queryFilterAppInfo() {
        PackageManager pm = this.getActivity().getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> appInfos= pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的
        List<ApplicationInfo> applicationInfos=new ArrayList<>();

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
        List<ResolveInfo>  resolveinfoList = pm.queryIntentActivities(resolveIntent, 0);
        Set<String> allowPackages=new HashSet();
        for (ResolveInfo resolveInfo:resolveinfoList){
            allowPackages.add(resolveInfo.activityInfo.packageName);
        }

        for (ApplicationInfo app:appInfos) {
//            if((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)//通过flag排除系统应用，会将电话、短信也排除掉
//            {
//                applicationInfos.add(app);
//            }
//            if(app.uid > 10000){//通过uid排除系统应用，在一些手机上效果不好
//                applicationInfos.add(app);
//            }
            if (allowPackages.contains(app.packageName)){
                applicationInfos.add(app);
                AppInfo info = new AppInfo();
                info.appName = app.loadLabel(pm).toString();
                info.appPacketName = app.packageName;
                info.appIcon = app.loadIcon(pm);
                info.appIntent = pm.getLaunchIntentForPackage(app.packageName);
                mDataArray.add(info);
            }
        }
        // 获取该应用安装包的Intent，用于启动该应用


    }


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
//                 系统应用　　　　　　　　
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
