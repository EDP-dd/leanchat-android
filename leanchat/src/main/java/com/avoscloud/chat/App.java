package com.avoscloud.chat;

import android.app.Application;
import android.os.StrictMode;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avoscloud.chat.friends.AddRequest;
import com.avoscloud.chat.model.LCIMRedPacketMessage;
import com.avoscloud.chat.model.LCIMRedPacketAckMessage;
import com.avoscloud.chat.model.LCIMTransferMessage;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.model.UpdateInfo;
import com.avoscloud.chat.redpacket.GetSignInfoCallback;
import com.avoscloud.chat.redpacket.RedPacketUtils;
import com.avoscloud.chat.service.PushManager;
import com.avoscloud.chat.util.LeanchatUserProvider;
import com.avoscloud.chat.util.Utils;
import com.baidu.mapapi.SDKInitializer;
import com.yunzhanghu.redpacketsdk.RPInitRedPacketCallback;
import com.yunzhanghu.redpacketsdk.RPValueCallback;
import com.yunzhanghu.redpacketsdk.RedPacket;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.bean.TokenData;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by lzw on 14-5-29.
 */
public class App extends Application {
  public static boolean debug = true;
  public static App ctx;

  @Override
  public void onCreate() {
    super.onCreate();
    ctx = this;
    Utils.fixAsyncTaskBug();

    String appId = "x3o016bxnkpyee7e9pa5pre6efx2dadyerdlcez0wbzhw25g";
    String appKey = "057x24cfdzhffnl3dzk14jh9xo2rq6w1hy1fdzt5tv46ym78";

    LeanchatUser.alwaysUseSubUserClass(LeanchatUser.class);

    AVObject.registerSubclass(AddRequest.class);
    AVObject.registerSubclass(UpdateInfo.class);

    // 节省流量
    AVOSCloud.setLastModifyEnabled(true);

    AVIMMessageManager.registerAVIMMessageType(LCIMRedPacketMessage.class);
    AVIMMessageManager.registerAVIMMessageType(LCIMRedPacketAckMessage.class);
    AVIMMessageManager.registerAVIMMessageType(LCIMTransferMessage.class);
    LCChatKit.getInstance().setProfileProvider(new LeanchatUserProvider());
    LCChatKit.getInstance().init(this, appId, appKey);

    // 初始化红包操作
    RedPacket.getInstance().initRedPacket(ctx, RPConstant.AUTH_METHOD_SIGN, new RPInitRedPacketCallback() {
      @Override
      public void initTokenData(final RPValueCallback<TokenData> rpValueCallback) {
        RedPacketUtils.getInstance().getRedPacketSign(ctx, new GetSignInfoCallback() {
          @Override
          public void signInfoSuccess(TokenData tokenData) {
            rpValueCallback.onSuccess(tokenData);
          }

          @Override
          public void signInfoError(String errorMsg) {
          }
        });
      }

      @Override
      public RedPacketInfo initCurrentUserSync() {
        //这里需要同步设置当前用户id、昵称和头像url
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.fromUserId = LeanchatUser.getCurrentUserId();
        redPacketInfo.fromAvatarUrl = LeanchatUser.getCurrentUser().getAvatarUrl();
        redPacketInfo.fromNickName = LeanchatUser.getCurrentUser().getUsername();
        return redPacketInfo;
      }
    });
    //控制红包SDK中Log输出
    RedPacket.getInstance().setDebugMode(false);

    PushManager.getInstance().init(ctx);
    AVOSCloud.setDebugLogEnabled(debug);
    AVAnalytics.enableCrashReport(this, !debug);
    initBaiduMap();
    if (App.debug) {
      openStrictMode();
    }
  }

  public void openStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()   // or .detectAll() for all detectable problems
            .penaltyLog()
            .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            //.penaltyDeath()
            .build());
  }

  private void initBaiduMap() {
    SDKInitializer.initialize(this);
  }
}
