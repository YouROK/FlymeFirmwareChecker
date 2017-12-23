package ru.yourok.utils;

/**
 * Created by yourok on 29.09.17.
 */

public class RemoveScript {
    public static final String Script =
            "#!/system/bin/sh\n" +
                    "\n\n" +
                    "mount -o rw,remount /system\n" +
                    "\n" +
                    "rm -rf \"/system/priv-app/EBook\"\n" +
                    "rm -rf \"/system/priv-app/AliAppAccount\"\n" +
                    "rm -rf \"/system/priv-app/MzAccountPlugin\"\n" +
                    "rm -rf \"/system/priv-app/NetContactService\"\n" +
                    "rm -rf \"/system/priv-app/VoiceAssistant\"\n" +
                    "rm -rf \"/system/priv-app/LuckyMoney\"\n" +
                    "rm -rf \"/system/priv-app/AliLifeCenterProvider\"\n" +
                    "rm -rf \"/system/priv-app/Wallet\"\n" +
                    "rm -rf \"/system/priv-app/Browser\"\n" +
                    "rm -rf \"/system/priv-app/YellowPage\"\n" +
                    "rm -rf \"/system/priv-app/GameSDKService\"\n" +
                    "rm -rf \"/system/priv-app/Feedback\"\n" +
                    "rm -rf \"/system/priv-app/MeizuPay\"\n" +
                    "\n" +
                    "rm -rf \"/system/app/AppCenter\"\n" +
                    "rm -rf \"/system/app/MzCompaign\"\n" +
                    "rm -rf \"/system/app/GameCenter\"\n" +
                    "rm -rf \"/system/app/CloudDisk\"\n" +
                    "rm -rf \"/system/app/Map\"\n" +
                    "#rm -rf \"/system/app/Search\"\n" +
                    "rm -rf \"/system/app/IflytekSpeechService\"\n" +
                    "rm -rf \"/system/app/AlipaySec\"\n" +
                    "rm -rf \"/system/app/Life\"\n" +
                    "rm -rf \"/system/app/MzMPay\"\n" +
                    "rm -rf \"/system/app/Reader\"\n" +
                    "rm -rf \"/system/app/Weather\"\n" +
                    "rm -rf \"/system/app/O2OService\"\n" +
                    "rm -rf \"/system/app/AlipayNewmsp\"\n" +
                    "rm -rf \"/system/app/MzCloudService\"\n" +
                    "rm -rf \"/system/app/MzPay\"\n" +
                    "#rm -rf \"/system/app/MzUpdate\"\n" +
                    "\n" +
                    "rm -rf \"/data/app/BaiduMap\"\n" +
                    "rm -rf \"/data/app/CTrip\"\n" +
                    "rm -rf \"/data/app/DianPing\"\n" +
                    "rm -rf \"/data/app/com.dianping.v1\"\n" +
                    "rm -rf \"/data/app/com.dianping.v1-1\"\n" +
                    "rm -rf \"/data/app/com.dianping.v1-2\"\n" +
                    "rm -rf \"/data/app/com.jingdong.app.mall\"\n" +
                    "rm -rf \"/data/app/com.jingdong.app.mall-1\"\n" +
                    "rm -rf \"/data/app/com.jingdong.app.mall-2\"\n" +
                    "rm -rf \"/data/app/Meituan\"\n" +
                    "rm -rf \"/data/app/NeteaseNews\"\n" +
                    "rm -rf \"/data/app/SogouInput\"\n" +
                    "rm -rf \"/data/app/TingPhone\"\n" +
                    "rm -rf \"/data/app/tmall\"\n" +
                    "rm -rf \"/data/app/Weibo\"\n" +
                    "rm -rf \"/data/app/com.sina.weibo\"\n" +
                    "rm -rf \"/data/app/com.sina.weibo-1\"\n" +
                    "rm -rf \"/data/app/com.sina.weibo-2\"\n" +
                    "rm -rf \"/data/app/com.meizu.media.ebook\"\n" +
                    "rm -rf \"/data/app/com.meizu.media.ebook-1\"\n" +
                    "rm -rf \"/data/app/com.meizu.media.ebook-2\"\n" +
                    "rm -rf \"/data/app/com.ximalaya.ting.android\"\n" +
                    "rm -rf \"/data/app/com.ximalaya.ting.android-1\"\n" +
                    "rm -rf \"/data/app/com.ximalaya.ting.android-2\"\n" +
                    "rm -rf \"/data/app/amap.apk\"\n" +
                    "rm -rf \"/data/app/Dianping.apk\"\n" +
                    "rm -rf \"/data/app/JD.apk\"\n" +
                    "rm -rf \"/data/app/newsreader.apk\"\n" +
                    "rm -rf \"/data/app/Qunar.apk\"\n" +
                    "rm -rf \"/data/app/TingPhone.apk\"\n" +
                    "rm -rf \"/data/app/vipshop.apk\"\n" +
                    "rm -rf \"/data/app/com.achievo.vipshop\"\n" +
                    "rm -rf \"/data/app/com.achievo.vipshop-1\"\n" +
                    "rm -rf \"/data/app/com.achievo.vipshop-2\"\n" +
                    "rm -rf \"/data/app/com.autonavi.minimap\"\n" +
                    "rm -rf \"/data/app/com.autonavi.minimap-1\"\n" +
                    "rm -rf \"/data/app/com.autonavi.minimap-2\"\n" +
                    "rm -rf \"/data/app/com.netease.newsreader.activity\"\n" +
                    "rm -rf \"/data/app/com.netease.newsreader.activity-1\"\n" +
                    "rm -rf \"/data/app/com.netease.newsreader.activity-2\"\n" +
                    "rm -rf \"/data/app/com.Qunar\"\n" +
                    "rm -rf \"/data/app/com.Qunar-2\"\n" +
                    "rm -rf \"/data/app/SogouInput\"\n" +
                    "rm -rf \"/data/app/SogouInput-1\"\n" +
                    "rm -rf \"/data/app/SogouInput-2\"\n" +
                    "rm -rf \"/data/app/SogouInput.apk\"\n" +
                    "rm -rf \"/data/app/SogouInput-1.apk\"\n" +
                    "rm -rf \"/data/app/SogouInput-2.apk\"\n" +
                    "rm -rf \"/data/app/Weibo.apk\"\n" +
                    "rm -rf \"/data/app/com.sohu.inputmethod.sogou\"\n" +
                    "rm -rf \"/data/app/com.sohu.inputmethod.sogou-1\"\n" +
                    "rm -rf \"/data/app/com.sohu.inputmethod.sogou-2\"\n" +
                    "rm -rf \"/data/app/com.meizu.media.life\"\n" +
                    "rm -rf \"/data/app/com.meizu.media.life-1\"\n" +
                    "rm -rf \"/data/app/NewsArticle.apk\"\n" +
                    "rm -rf \"/data/app/TaoBao.apk\"\n" +
                    "rm -rf \"/data/app/MeiTuan.apk\"\n" +
                    "rm -rf \"/data/app/momo.apk\"\n" +
                    "rm -rf \"/data/app/com.meizu.media.reader-1/base.apk\"\n" +
                    "rm -rf \"/data/app/qzone.apk\"\n" +
                    "\n" +
                    "rm -rf /custom/3rd-party/apk/*\n" +
                    "\n\n" +
                    "#reboot" +
                    "\n\n\n\n";
}
