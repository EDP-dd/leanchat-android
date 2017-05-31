package com.avoscloud.chat.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avoscloud.chat.R;
import com.avoscloud.chat.model.LCIMRedPacketAckMessage;
import com.avoscloud.chat.model.LeanchatUser;

import cn.leancloud.chatkit.viewholder.LCIMChatItemHolder;

public class ChatItemRedPacketAckHolder extends LCIMChatItemHolder {

  private TextView contentView;

  public ChatItemRedPacketAckHolder(Context context, ViewGroup root, boolean isLeft) {
    super(context, root, isLeft);
  }

  @Override
  public void initView() {
    super.initView();
    conventLayout.addView(View.inflate(getContext(), R.layout.lc_chat_item_redpacket_ack, null));
    avatarView.setVisibility(View.GONE);
    contentView = (TextView) itemView.findViewById(R.id.tv_money_msg);
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    nameView.setText("");
    if (o instanceof LCIMRedPacketAckMessage) {
      LCIMRedPacketAckMessage ackMessage = (LCIMRedPacketAckMessage) o;
      initRedPacketAckChatItem(ackMessage.getSenderName(), ackMessage.getRecipientName(),
        LeanchatUser.getCurrentUserId().equals(ackMessage.getSenderId()),
        LeanchatUser.getCurrentUserId().equals(ackMessage.getFrom()),
        TextUtils.isEmpty(ackMessage.getRedPacketType()));
    }
  }

  /**
   * @param senderName    红包发送者名字
   * @param recipientName 红包接收者名字
   * @param isSelf        是不是自己领取了自己的红包
   * @param isSend        消息是不是自己发送的
   * @param isSingle      是单聊还是群聊
   */
  private void initRedPacketAckChatItem(String senderName, String recipientName, boolean isSelf, boolean isSend, boolean isSingle) {
    if (isSend) {
      if (!isSingle) {
        if (isSelf) {
          contentView.setText(R.string.money_msg_take_money);
        } else {
          contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_take_someone_money), senderName));
        }
      } else {
        contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_take_someone_money), senderName));
      }
    } else {
      if (isSelf) {
        contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_someone_take_money), recipientName));
      } else {
        contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_someone_take_money_same), recipientName, senderName));
      }
    }
  }
}