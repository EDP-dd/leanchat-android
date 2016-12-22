package com.avoscloud.chat.redpacket;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avoscloud.chat.event.RedPacketAckEvent;
import com.avoscloud.chat.model.LCIMRedPacketAckMessage;
import com.avoscloud.chat.model.LCIMRedPacketMessage;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.util.UserCacheUtils;
import com.yunzhanghu.redpacketsdk.RPRefreshSignListener;
import com.yunzhanghu.redpacketsdk.RPValueCallback;
import com.yunzhanghu.redpacketsdk.RedPacket;
import com.yunzhanghu.redpacketsdk.bean.RPUserBean;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.bean.TokenData;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;
import com.yunzhanghu.redpacketui.ui.activity.RPChangeActivity;
import com.yunzhanghu.redpacketui.ui.activity.RPRedPacketActivity;
import com.yunzhanghu.redpacketui.ui.activity.RPTransferActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import de.greenrobot.event.EventBus;

/**
 * Created by ustc on 2016/5/31.
 */
public class RedPacketUtils {

  private static RedPacketUtils mRedPacketUtil;

  private GetGroupMemberCallback mGetGroupMemberCallback;//获取群组成员的回调,可以根据自己的需要选择需不需要
  private GetUserInfoCallback mGetUserInfoCallback;//获取个人信息的回调,打开专属红包时需要,可以根据自己需要进行选择
  private GetSignInfoCallback mGetSignInfoCallback;//获取sign的回调
  private RequestQueue mQueue;
  private TokenData mTokenData;//打开红包时需要转登录时获取的数据

  private RedPacketUtils() {

  }

  public static RedPacketUtils getInstance() {
    if (mRedPacketUtil == null) {
      synchronized (RedPacketUtils.class) {
        if (mRedPacketUtil == null) {
          mRedPacketUtil = new RedPacketUtils();
        }

      }
    }
    return mRedPacketUtil;
  }

  /**
   * 打开发送红包页面使用
   *
   * @param fromNickname
   * @param fromAvatarUrl
   * @param toUserId
   * @param chatType
   * @param toGroupId
   * @param groupMemberCount
   * @return
   */
  public static RedPacketInfo initRedPacketInfo(String fromNickname, String fromAvatarUrl, String toUserId, int chatType, String toGroupId, int groupMemberCount) {
    RedPacketInfo redPacketInfo = new RedPacketInfo();
    redPacketInfo.fromAvatarUrl = fromAvatarUrl;//发送人的头像
    redPacketInfo.fromNickName = fromNickname;//发送人的名字
    redPacketInfo.chatType = chatType;//判断是否是单聊
    if (chatType == 1) {
      redPacketInfo.toUserId = toUserId;
    } else if (chatType == 2) {
      redPacketInfo.toGroupId = toGroupId;//群id
      redPacketInfo.groupMemberCount = groupMemberCount;//群成员数量
    }
    return redPacketInfo;
  }

  /**
   * 打开普通红包用
   *
   * @param fromNickname
   * @param fromAvatarUrl
   * @param moneyMsgDirect
   * @param chatType
   * @param moneyId
   * @return
   */
  public static RedPacketInfo initRedPacketInfo_received(String fromNickname, String fromAvatarUrl, String moneyMsgDirect, int chatType, String moneyId) {
    RedPacketInfo redPacketInfo = new RedPacketInfo();
    redPacketInfo.moneyMsgDirect = moneyMsgDirect;
    redPacketInfo.chatType = chatType;
    redPacketInfo.redPacketId = moneyId;
    redPacketInfo.toAvatarUrl = fromAvatarUrl;
    redPacketInfo.toNickName = fromNickname;
    return redPacketInfo;
  }

  /**
   * 发红包时用
   *
   * @param fragment
   * @param toUserId
   * @param fromNickname
   * @param fromAvatarUrl
   * @param chatType
   * @param tpGroupId
   * @param membersNum
   * @param REQUEST_CODE_SEND_MONEY
   */
  public static void selectRedPacket(Fragment fragment, String toUserId, String fromNickname, String fromAvatarUrl, int chatType, String tpGroupId, int membersNum, int REQUEST_CODE_SEND_MONEY) {
    Intent intent = new Intent(fragment.getActivity(), RPRedPacketActivity.class); /*接收者Id或者接收的群Id*/
    RedPacketInfo redpacketInfo;
    if (chatType == RPConstant.CHATTYPE_SINGLE) {
      redpacketInfo = initRedPacketInfo(fromNickname, fromAvatarUrl, toUserId, RPConstant.CHATTYPE_SINGLE, tpGroupId, membersNum);
    } else if (chatType == RPConstant.CHATTYPE_GROUP) {
      redpacketInfo = initRedPacketInfo(fromNickname, fromAvatarUrl, toUserId, RPConstant.CHATTYPE_GROUP, tpGroupId, membersNum);
    } else {
      return;
    }
    intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redpacketInfo);
    intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, RedPacketUtils.getInstance().getTokenData());
    fragment.startActivityForResult(intent, REQUEST_CODE_SEND_MONEY);
  }

  public static void selectTransfer(Fragment fragment, String toUserId, String fromNickname, String fromAvatarUrl, int REQUEST_CODE_TRANSFER) {
    Intent intent = new Intent(fragment.getActivity(), RPTransferActivity.class); /*接收者Id或者接收的群Id*/
    RedPacketInfo redPacketInfo = new RedPacketInfo();
    redPacketInfo.fromAvatarUrl = fromAvatarUrl;
    redPacketInfo.fromNickName = fromNickname;
    if (UserCacheUtils.getCachedUser(toUserId) != null) {
      //接收者Id
      redPacketInfo.toUserId = toUserId;
      if (!TextUtils.isEmpty(UserCacheUtils.getCachedUser(toUserId).getUsername())) {

        redPacketInfo.toNickName = UserCacheUtils.getCachedUser(toUserId).getUsername();
      }
      if (!TextUtils.isEmpty(UserCacheUtils.getCachedUser(toUserId).getAvatarUrl())) {

        redPacketInfo.toAvatarUrl = UserCacheUtils.getCachedUser(toUserId).getAvatarUrl();
      } else {
        redPacketInfo.toAvatarUrl = "none";
      }
    }
    intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redPacketInfo);
    intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, RedPacketUtils.getInstance().getTokenData());
    fragment.startActivityForResult(intent, REQUEST_CODE_TRANSFER);
  }

  /**
   * 群红包中发专属红包用的
   * 根据一个群成员的id集合,查出群成员的具体信息,发专属红包时需要传群成员信息
   */

  public void initRpGroupMember(List<String> ids, GetGroupMemberCallback callback) {
    mGetGroupMemberCallback = callback;
    final List<RPUserBean> rpUserList = new ArrayList<RPUserBean>();

    UserCacheUtils.fetchUsers(ids, new UserCacheUtils.CacheUserCallback() {
      RPUserBean rpUserBean;

      @Override
      public void done(List<LeanchatUser> userList, Exception e) {
        if (userList != null) {
          for (int i = 0; i < userList.size(); i++) {
            rpUserBean = new RPUserBean();
            if (!LeanchatUser.getCurrentUserId().equals(userList.get(i).getObjectId())) {

              rpUserBean.userId = userList.get(i).getObjectId();
              rpUserBean.userNickname = userList.get(i).getUsername();
              if (!TextUtils.isEmpty(userList.get(i).getAvatarUrl())) {
                rpUserBean.userAvatar = userList.get(i).getAvatarUrl();
              } else {
                rpUserBean.userAvatar = "none";
              }
              rpUserList.add(rpUserBean);
            }
          }
        }
        /**
         * 查到数据进行回调
         */
        if (rpUserList != null && rpUserList.size() > 0) {
          mGetGroupMemberCallback.groupInfoSuccess(rpUserList);
        } else {
          mGetGroupMemberCallback.groupInfoError();
        }
      }
    });

  }

  /**
   * 根据用户id,获取接收专属红包人的用户信息
   *
   * @param id
   * @param callback
   */
  public void getReceiveInfo(String id, GetUserInfoCallback callback) {
    mGetUserInfoCallback = callback;
    RPUserBean rpUserBean = new RPUserBean();
    if (!TextUtils.isEmpty(id)) {
      rpUserBean.userId = id;
    }
    if (UserCacheUtils.getCachedUser(id) != null) {
      if (!TextUtils.isEmpty(UserCacheUtils.getCachedUser(id).getUsername())) {

        rpUserBean.userNickname = UserCacheUtils.getCachedUser(id).getUsername();
      }
      if (!TextUtils.isEmpty(UserCacheUtils.getCachedUser(id).getAvatarUrl())) {

        rpUserBean.userAvatar = UserCacheUtils.getCachedUser(id).getAvatarUrl();
      } else {
        rpUserBean.userAvatar = "none";
      }
    }
    if (rpUserBean != null) {
      mGetUserInfoCallback.userInfoSuccess(rpUserBean);
    } else {
      mGetUserInfoCallback.userInfoError();
    }

  }

  /**
   * 刷新sign
   *
   * @param context
   * @param url
   */
  public void setRefreshSign(final Context context, final String url) {
    RedPacket.getInstance().setRefreshSignListener(new RPRefreshSignListener() {
      @Override
      public void onRefreshSign(final RPValueCallback<TokenData> rpValueCallback) {

        getRedPacketSign(context, url, new GetSignInfoCallback() {
          @Override
          public void signInfoSuccess(TokenData tokenData) {
            Log.e("msg", "----->红包SDK登录成功");
            rpValueCallback.onSuccess(tokenData);
          }

          @Override
          public void signInfoError(String errorMsg) {
            Log.e("msg", "----->红包SDK登录失败" + errorMsg);
          }
        });
      }
    });

  }

  /**
   * 获取sign
   *
   * @param context
   */
  public void getRedPacketSign(Context context, String mockUrl, GetSignInfoCallback callback) {
    mGetSignInfoCallback = callback;
    mQueue = Volley.newRequestQueue(context);
    StringRequest stringRequest = new StringRequest(mockUrl, new Response.Listener<String>() {
      @Override
      public void onResponse(String s) {
        try {
          if (s != null) {
            JSONObject jsonObj = new JSONObject(s);
            final String partner = jsonObj.getString("partner");
            final String userId = jsonObj.getString("user_id");
            final String timestamp = jsonObj.getString("timestamp");
            final String sign = jsonObj.getString("sign");
            /**
             * 零钱页和领取红包和发红包时都需要
             */
            initTokenData(partner, userId, timestamp, sign);
            mGetSignInfoCallback.signInfoSuccess(getTokenData());
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError volleyError) {
        mGetSignInfoCallback.signInfoError(volleyError.toString());
      }
    });
    stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
    mQueue.add(stringRequest);
  }

  /**
   * 发红包、拆红包、进入零钱页都需要
   *
   * @param authPartner
   * @param authUserId
   * @param authTimestamp
   * @param authSign
   */
  public void initTokenData(String authPartner, String authUserId, String authTimestamp, String authSign) {
    mTokenData = new TokenData();
    mTokenData.authPartner = authPartner;
    mTokenData.appUserId = authUserId;
    mTokenData.timestamp = authTimestamp;
    mTokenData.authSign = authSign;
  }

  public TokenData getTokenData() {
    if (mTokenData == null) {
      mTokenData = new TokenData();
      mTokenData.appUserId = LeanchatUser.getCurrentUserId();
    } else {
      if (!LeanchatUser.getCurrentUserId().equals(mTokenData.appUserId)) {//切换账号的时候
        mTokenData = new TokenData();
        mTokenData.appUserId = LeanchatUser.getCurrentUserId();
      }
    }
    return mTokenData;
  }

  /**
   * 进入零钱页用
   */
  public void toChangeActivity(Context mContext, String userName, String userAvatar) {
    Intent intent = new Intent(mContext, RPChangeActivity.class);
    RedPacketInfo redPacketInfo = new RedPacketInfo();
    redPacketInfo.fromNickName = userName;
    redPacketInfo.fromAvatarUrl = userAvatar;
    intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redPacketInfo);
    intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, getTokenData());
    mContext.startActivity(intent);
  }

  /**
   * 打开红包之后发送回执消息
   */
  public void sendRedPacketAckMsg(String senderId, String senderNickname, String selfId, String selfName, LCIMRedPacketMessage message) {
    LCIMRedPacketAckMessage ackMessage = new LCIMRedPacketAckMessage();
    ackMessage.setSenderId(senderId);
    ackMessage.setSenderName(senderNickname);
    ackMessage.setRecipientId(selfId);
    ackMessage.setRecipientName(selfName);
    ackMessage.setRedPacketType(message.getRedPacketType());
    ackMessage.setGreeting(message.getGreeting());
    ackMessage.setSponsorName(message.getSponsorName());
    ackMessage.setMoney(true);
    EventBus.getDefault().post(new RedPacketAckEvent(ackMessage));
  }

  /**
   * 接收回执消息
   */
  public int receiveRedPacketAckMsg(LCIMRedPacketAckMessage typedMessage, int ITEM_TEXT_RED_PACKET_NOTIFY, int ITEM_TEXT_RED_PACKET_NOTIFY_MEMBER) {
    String selfId = LCChatKit.getInstance().getCurrentUserId();
    if (!TextUtils.isEmpty(typedMessage.getSenderId()) && !TextUtils.isEmpty(typedMessage.getRecipientId())) {
      return typedMessage.getSenderId().equals(selfId) || typedMessage.getRecipientId().equals(selfId)
              ? ITEM_TEXT_RED_PACKET_NOTIFY : ITEM_TEXT_RED_PACKET_NOTIFY_MEMBER;
    } else {
      return ITEM_TEXT_RED_PACKET_NOTIFY_MEMBER;
    }
  }
}
