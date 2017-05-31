package com.avoscloud.chat.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avoscloud.chat.R;
import com.avoscloud.chat.model.LCIMRedPacketMessage;
import com.avoscloud.chat.redpacket.RedPacketUtils;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import cn.leancloud.chatkit.viewholder.LCIMChatItemHolder;

/**
 * 点击红包消息，领取红包或者查看红包详情
 */
public class ChatItemRedPacketHolder extends LCIMChatItemHolder {

  private TextView mTvGreeting;

  private TextView mTvSponsorName;

  private TextView mTvPacketType;

  private LCIMRedPacketMessage mRedPacketMessage;

  public ChatItemRedPacketHolder(Context context, ViewGroup root, boolean isLeft) {
    super(context, root, isLeft);
  }

  @Override
  public void initView() {
    super.initView();
    if (isLeft) {
      conventLayout.addView(View.inflate(getContext(),
              R.layout.lc_chat_item_left_text_redpacket_layout, null));
    } else {
      conventLayout.addView(View.inflate(getContext(),
              R.layout.lc_chat_item_right_text_redpacket_layout, null)); /*红包view*/
    }
    RelativeLayout redPacketLayout = (RelativeLayout) itemView.findViewById(R.id.red_packet_layout);
    mTvGreeting = (TextView) itemView.findViewById(R.id.tv_money_greeting);
    mTvSponsorName = (TextView) itemView.findViewById(R.id.tv_sponsor_name);
    mTvPacketType = (TextView) itemView.findViewById(R.id.tv_packet_type);

    redPacketLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != mRedPacketMessage) {
          RedPacketUtils.getInstance().openRedPacket(getContext(), mRedPacketMessage);
        }
      }
    });
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    AVIMMessage message = (AVIMMessage) o;
    if (message instanceof LCIMRedPacketMessage) {
      mRedPacketMessage = (LCIMRedPacketMessage) message;
      mTvGreeting.setText(mRedPacketMessage.getGreeting());
      mTvSponsorName.setText(mRedPacketMessage.getSponsorName());

      String redPacketType = mRedPacketMessage.getRedPacketType();
      if (!TextUtils.isEmpty(redPacketType) && redPacketType.equals(
              RPConstant.GROUP_RED_PACKET_TYPE_EXCLUSIVE)) {
        mTvPacketType.setVisibility(View.VISIBLE);
        mTvPacketType.setText(getContext().getResources().getString(
                R.string.exclusive_red_envelope));
      } else {
        mTvPacketType.setVisibility(View.GONE);
      }
    }
  }

}
