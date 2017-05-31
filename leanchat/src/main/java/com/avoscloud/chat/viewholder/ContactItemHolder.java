package com.avoscloud.chat.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avoscloud.chat.R;
import com.avoscloud.chat.event.ContactItemClickEvent;
import com.avoscloud.chat.event.ContactItemLongClickEvent;
import com.avoscloud.chat.model.ContactItem;
import com.squareup.picasso.Picasso;

import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/11/24.
 */
public class ContactItemHolder extends LCIMCommonViewHolder<ContactItem> {

  TextView alpha;
  TextView nameView;
  ImageView avatarView;

  public ContactItem contactItem;

  public ContactItemHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.common_user_item);
    initView();
  }

  public void initView() {
    alpha = (TextView)itemView.findViewById(R.id.alpha);
    nameView = (TextView)itemView.findViewById(R.id.tv_friend_name);
    avatarView = (ImageView)itemView.findViewById(R.id.img_friend_avatar);

    itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EventBus.getDefault().post(new ContactItemClickEvent(contactItem.user.getObjectId()));
      }
    });

    itemView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        EventBus.getDefault().post(new ContactItemLongClickEvent(contactItem.user.getObjectId()));
        return true;
      }
    });
  }

  @Override
  public void bindData(ContactItem memberItem) {
    contactItem = memberItem;
    alpha.setVisibility(memberItem.initialVisible ? View.VISIBLE : View.GONE);
    if (!TextUtils.isEmpty(memberItem.sortContent)) {
      alpha.setText(String.valueOf(Character.toUpperCase(memberItem.sortContent.charAt(0))));
    } else {
      alpha.setText("");
    }
    Picasso.with(getContext()).load(memberItem.user.getAvatarUrl())
      .placeholder(R.drawable.lcim_default_avatar_icon).into(avatarView);
    nameView.setText(memberItem.user.getUsername());
  }

  public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<ContactItemHolder>() {
    @Override
    public ContactItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
      return new ContactItemHolder(parent.getContext(), parent);
    }
  };
}
