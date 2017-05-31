package com.avoscloud.chat.model;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import cn.leancloud.chatkit.LCChatMessageInterface;

/**
 * Created by wli on 16/7/11.
 */

@AVIMMessageType(type = LCIMRedPacketMessage.RED_PACKET_MESSAGE_TYPE)
public class LCIMRedPacketMessage extends AVIMTypedMessage implements LCChatMessageInterface {

  public static final int RED_PACKET_MESSAGE_TYPE = 3;

  public LCIMRedPacketMessage() {
  }

  /**
   * 红包id
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_ID)
  private String redPacketId;

  /**
   * 红包祝福语
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_GREETING)
  private String greeting;

  /**
   * 祝福语前缀(如LeanCloud红包)
   */
  @AVIMMessageField(name = RPConstant.EXTRA_SPONSOR_NAME)
  private String sponsorName;

  /**
   * 红包的类型
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_TYPE)
  private String redPacketType;

  /**
   * 红包接收者id
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_RECEIVER_ID)
  private String receiverId;

  /**
   * 是否是红包消息
   */
  @AVIMMessageField(name = RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)
  private boolean isMoney;

  /**
   * 红包发送者name
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_SENDER_NAME)
  private String senderName;

  /**
   * 红包发送者id
   */
  @AVIMMessageField(name = RPConstant.EXTRA_RED_PACKET_SENDER_ID)
  private String senderId;

  public static final Creator<LCIMRedPacketMessage> CREATOR = new AVIMMessageCreator<>(LCIMRedPacketMessage.class);

  @Override
  public String getShorthand() {
    return "[" + sponsorName + "]" + greeting;
  }

  public String getRedPacketId() {
    return redPacketId;
  }

  public void setRedPacketId(String redPacketId) {
    this.redPacketId = redPacketId;
  }

  public String getSponsorName() {
    return sponsorName;
  }

  public void setSponsorName(String sponsorName) {
    this.sponsorName = sponsorName;
  }

  public String getGreeting() {
    return greeting;
  }

  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }

  public String getRedPacketType() {
    return redPacketType;
  }

  public void setRedPacketType(String redPacketType) {
    this.redPacketType = redPacketType;
  }

  public String getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(String receiverId) {
    this.receiverId = receiverId;
  }

  public boolean isMoney() {
    return isMoney;
  }

  public void setMoney(boolean money) {
    isMoney = money;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }
}