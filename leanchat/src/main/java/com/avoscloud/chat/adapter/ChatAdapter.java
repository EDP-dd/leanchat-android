package com.avoscloud.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avoscloud.chat.model.LCIMRedPacketAckMessage;
import com.avoscloud.chat.model.LCIMRedPacketMessage;
import com.avoscloud.chat.model.LCIMTransferMessage;
import com.avoscloud.chat.viewholder.ChatItemRedPacketAckHolder;
import com.avoscloud.chat.viewholder.ChatItemRedPacketEmptyHolder;
import com.avoscloud.chat.viewholder.ChatItemRedPacketHolder;
import com.avoscloud.chat.viewholder.ChatItemTransferHolder;

import cn.leancloud.chatkit.adapter.LCIMChatAdapter;

/**
 * Created by wli on 16/7/11.
 */
public class ChatAdapter extends LCIMChatAdapter {

  private final int ITEM_LEFT_TEXT_RED_PACKET = 1005;//自己不是发送红包者
  private final int ITEM_RIGHT_TEXT_RED_PACKET = 2005;//自己是发送红包者
  private final int ITEM_TEXT_RED_PACKET_NOTIFY = 3000;//会话详情显示领取红包后显示的回执消息(自己是发送者或者是接收者)
  private final int ITEM_TEXT_RED_PACKET_NOTIFY_MEMBER = 3001;//会话详情页领取红包后回执的空消息(自己不是发送者也不是接收者)
  private final int ITEM_LEFT_TEXT_TRANSFER = 1006;//转账接收者
  private final int ITEM_RIGHT_TEXT_TRANSFER = 2007;//转账发送者

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case ITEM_LEFT_TEXT_RED_PACKET:
        return new ChatItemRedPacketHolder(parent.getContext(), parent, true);
      case ITEM_RIGHT_TEXT_RED_PACKET:
        return new ChatItemRedPacketHolder(parent.getContext(), parent, false);
      case ITEM_TEXT_RED_PACKET_NOTIFY:
        return new ChatItemRedPacketAckHolder(parent.getContext(), parent, false);
      case ITEM_TEXT_RED_PACKET_NOTIFY_MEMBER:
        return new ChatItemRedPacketEmptyHolder(parent.getContext(), parent);
      case ITEM_LEFT_TEXT_TRANSFER:
        return new ChatItemTransferHolder(parent.getContext(), parent, true);
      case ITEM_RIGHT_TEXT_TRANSFER:
        return new ChatItemTransferHolder(parent.getContext(), parent, false);
      default:
        return super.onCreateViewHolder(parent, viewType);
    }
  }

  /**
   * 判断是什么消息类型
   */

  @Override
  public int getItemViewType(int position) {
    AVIMMessage message = messageList.get(position);
    if (null != message && message instanceof AVIMTypedMessage) {
      AVIMTypedMessage typedMessage = (AVIMTypedMessage) message;
      boolean isMe = fromMe(typedMessage);
      if (typedMessage.getMessageType() == LCIMRedPacketMessage.RED_PACKET_MESSAGE_TYPE) {
        return isMe ? ITEM_RIGHT_TEXT_RED_PACKET : ITEM_LEFT_TEXT_RED_PACKET;
      } else if (typedMessage.getMessageType() == LCIMRedPacketAckMessage.RED_PACKET_ACK_MESSAGE_TYPE) {
//        return RedPacketUtils.getInstance().receiveRedPacketAckMsg((LCIMRedPacketAckMessage) typedMessage,ITEM_TEXT_RED_PACKET_NOTIFY,ITEM_TEXT_RED_PACKET_NOTIFY_MEMBER);
      } else if (typedMessage.getMessageType() == LCIMTransferMessage.TRANSFER_MESSAGE_TYPE) {
        return isMe ? ITEM_RIGHT_TEXT_TRANSFER : ITEM_LEFT_TEXT_TRANSFER;
      }
    }
    return super.getItemViewType(position);
  }
}
