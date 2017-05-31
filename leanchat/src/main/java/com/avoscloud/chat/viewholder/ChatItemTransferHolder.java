package com.avoscloud.chat.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avoscloud.chat.R;
import com.avoscloud.chat.model.LCIMTransferMessage;
import com.avoscloud.chat.redpacket.RedPacketUtils;

import cn.leancloud.chatkit.viewholder.LCIMChatItemHolder;

/**
 * 转账
 */
public class ChatItemTransferHolder extends LCIMChatItemHolder {

  private TextView mTvTransfer;

  private LCIMTransferMessage transferMessage;

  public ChatItemTransferHolder(Context context, ViewGroup root, boolean isLeft) {
    super(context, root, isLeft);
  }

  @Override
  public void initView() {
    super.initView();
    if (isLeft) {
      conventLayout.addView(View.inflate(getContext(),
              R.layout.lc_chat_item_left_text_transfer_layout, null));
    } else {
      conventLayout.addView(View.inflate(getContext(),
              R.layout.lc_chat_item_right_text_transfer_layout, null));
    }
    RelativeLayout transferLayout = (RelativeLayout) itemView.findViewById(R.id.transfer_layout);
    mTvTransfer = (TextView) itemView.findViewById(R.id.tv_transfer_amount);

    transferLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != transferMessage) {
          RedPacketUtils.getInstance().openTransfer(getContext(), transferMessage);
        }
      }
    });
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    AVIMMessage message = (AVIMMessage) o;
    if (message instanceof LCIMTransferMessage) {
      transferMessage = (LCIMTransferMessage) message;
      mTvTransfer.setText(String.format("%s元", transferMessage.getTransferAmount()));
    }
  }

}
