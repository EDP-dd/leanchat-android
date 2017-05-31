package com.avoscloud.chat.model;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import cn.leancloud.chatkit.LCChatMessageInterface;

/**
 * Created by wli on 16/7/14.
 * 红包发送被别人接收后的 tip message
 */
@AVIMMessageType(type = LCIMRedPacketAckMessage.RED_PACKET_ACK_MESSAGE_TYPE)
public class LCIMRedPacketAckMessage extends AVIMTypedMessage implements LCChatMessageInterface {
  public LCIMRedPacketAckMessage() {
  }

  public static final Creator<LCIMRedPacketAckMessage> CREATOR = new AVIMMessageCreator<>(LCIMRedPacketAckMessage.class);

  public static final int RED_PACKET_ACK_MESSAGE_TYPE = 4;

  /**
   * 红包的发送者 id
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_SENDER_ID)
  private String senderId;

  /**
   * 红包的发送者 name
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_SENDER_NAME)
  private String senderName;

  /**
   * 红包的接收者 id
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_RECEIVER_ID)
  private String recipientId;

  /**
   * 红包的接收者 name
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_RECEIVER_NAME)
  private String recipientName;

  /**
   * 红包的类型
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_TYPE)
  private String redPacketType;

  /**
   * 祝福语
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_GREETING)
  private String greeting;

  /**
   * 祝福语前缀(如LeanCloud红包)
   */
  @AVIMMessageField(name = RPConstant.EXTRA_SPONSOR_NAME)
  private String sponsorName;

  /**
   * 是否是红包消息
   */
  @AVIMMessageField(name = RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)
  private boolean isMoney;

  @Override
  public String getShorthand() {
    String userId = LeanchatUser.getCurrentUserId();
    if (userId.equals(senderId) && userId.equals(recipientId)) {
      return "你领取了自己的红包";
    } else if (userId.equals(senderId) && !userId.equals(recipientId)) {
      return recipientName + "领取了你的红包";
    } else if (!userId.equals(senderId) && userId.equals(recipientId)) {
      return "你领取了" + senderName + "的红包";
    } else if (!userId.equals(senderId) && !userId.equals(recipientId)) {
      return "[" + sponsorName + "]" + greeting;
    }
    return null;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  public String getRecipientName() {
    return recipientName;
  }

  public void setRecipientName(String recipientName) {
    this.recipientName = recipientName;
  }

  public String getRedPacketType() {
    return redPacketType;
  }

  public void setRedPacketType(String redPacketType) {
    this.redPacketType = redPacketType;
  }

  public String getGreeting() {
    return greeting;
  }

  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }

  public String getSponsorName() {
    return sponsorName;
  }

  public void setSponsorName(String sponsorName) {
    this.sponsorName = sponsorName;
  }

  public boolean isMoney() {
    return isMoney;
  }

  public void setMoney(boolean money) {
    isMoney = money;
  }
}
