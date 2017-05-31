package com.avoscloud.chat.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avoscloud.chat.R;
import com.avoscloud.chat.event.NewFriendItemClickEvent;
import com.avoscloud.chat.friends.AddRequest;
import com.avoscloud.chat.model.LeanchatUser;
import com.squareup.picasso.Picasso;

import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/12/3.
 */
public class NewFriendItemHolder extends LCIMCommonViewHolder<AddRequest> {

  private TextView nameView;
  private ImageView avatarView;
  private Button addBtn;
  private View agreedView;
  private AddRequest addRequest;

  public NewFriendItemHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.contact_add_friend_item);

    nameView = (TextView)itemView.findViewById(R.id.name);
    avatarView = (ImageView)itemView.findViewById(R.id.avatar);
    addBtn = (Button)itemView.findViewById(R.id.add);
    agreedView = itemView.findViewById(R.id.agreedView);

    itemView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        EventBus.getDefault().post(new NewFriendItemClickEvent(addRequest, true));
        return true;
      }
    });

    addBtn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {
        EventBus.getDefault().post(new NewFriendItemClickEvent(addRequest, false));
      }
    });
  }

  @Override
  public void bindData(final AddRequest addRequest) {
    this.addRequest = addRequest;
    LeanchatUser from = addRequest.getFromUser();
    if (null != from) {
      Picasso.with(getContext()).load(from.getAvatarUrl()).into(avatarView);
      nameView.setText(from.getUsername());
    }

    int status = addRequest.getStatus();
    if (status == AddRequest.STATUS_WAIT) {
      addBtn.setVisibility(View.VISIBLE);
      agreedView.setVisibility(View.GONE);
    } else if (status == AddRequest.STATUS_DONE) {
      addBtn.setVisibility(View.GONE);
      agreedView.setVisibility(View.VISIBLE);
    }
  }

  public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<NewFriendItemHolder>() {
    @Override
    public NewFriendItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
      return new NewFriendItemHolder(parent.getContext(), parent);
    }
  };
}
