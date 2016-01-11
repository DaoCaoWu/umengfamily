package com.umeng.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.Map;
import java.util.Set;

/**
 * create by adao12.vip@gmail.com on 15/11/28
 *
 * @author JiaYing.Cheng
 * @version 1.0
 */
public class LoginManager {

    private static final LoginManager instance = new LoginManager();
    private static final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    public static LoginManager getInstance() {
        return instance;
    }

    public static UMSocialService getController() {
        return mController;
    }

    public void login(Context context, SHARE_MEDIA platform) {
        if (platform == SHARE_MEDIA.SINA) {
            loginBySina(context);
        } else if (platform == SHARE_MEDIA.QQ) {
            loginByQQ(context);
        } else if (platform == SHARE_MEDIA.WEIXIN) {
            loginByWx(context);
        }
    }

    private void loginBySina(final Context context) {
        mController.doOauthVerify(context, SHARE_MEDIA.SINA, new DefaultUMAuthListener(context));
    }

    private void loginByQQ(final Context context) {
        String appId = "1104994100";
        String appKey = "rbiIbFRGStVTCuyY";
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity)context, appId, appKey);
        qqSsoHandler.addToSocialSDK();
        mController.doOauthVerify(context, SHARE_MEDIA.QQ, new DefaultUMAuthListener(context));

    }

    private void loginByWx(final Context context) {
        String appId = "wx446d4be2ca284dc4";
        String appSecret = "df137947f569fea18d8892224f13e4c8";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context, appId, appSecret);
        wxHandler.addToSocialSDK();
        mController.doOauthVerify(context, SHARE_MEDIA.WEIXIN, new DefaultUMAuthListener(context));
    }

    public class DefaultUMAuthListener implements SocializeListeners.UMAuthListener {

        private Context context;

        public DefaultUMAuthListener(Context context) {
            this.context = context;
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {
            Toast.makeText(context, "授权开始", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onError(SocializeException e, SHARE_MEDIA platform) {
            Toast.makeText(context, "授权错误", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onComplete(Bundle value, SHARE_MEDIA platform) {
            Toast.makeText(context, "授权完成", Toast.LENGTH_SHORT).show();
            //获取相关授权信息
            mController.getPlatformInfo(context, platform, new SocializeListeners.UMDataListener() {
                @Override
                public void onStart() {
                    Toast.makeText(context, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onComplete(int status, Map<String, Object> info) {
                    if(status == 200 && info != null){
                        StringBuilder sb = new StringBuilder();
                        Set<String> keys = info.keySet();
                        for(String key : keys){
                            sb.append(key+"="+info.get(key).toString()+"\r\n");
                        }
                        Log.d("TestData", sb.toString());
                    }else{
                        Log.d("TestData","发生错误："+status);
                    }
                }
            });
        }
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context, "授权取消", Toast.LENGTH_SHORT).show();
        }
    }
}
