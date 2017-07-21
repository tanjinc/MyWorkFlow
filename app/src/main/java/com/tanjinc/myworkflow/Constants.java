package com.tanjinc.myworkflow;

/**
 * Created by tanjincheng on 17/7/16.
 */
public class Constants {
    static String appPacketName;
    // 时间常量
    public class TIME {
        public static final int MINUTE = 60 * 1000;         // 1分钟
        public static final int SECOND = 1000;              // 1秒

        // 转换相关
        public static final int HOURS_OF_DAY = 24;          // 1天24小时
        public static final int MINUTES_OF_HOUR = 60;       // 1小时60分钟
        public static final int SECONDS_OF_MINUTE = 60;      // 1分钟60秒
        public static final int MILLS_OF_SECOND = 1000;      // 1秒1000毫秒
    }

    public class ACTION{
        public static final String ACTION_AUTOBOX_TASK = "intent.action.autobox.task";
    }
}
