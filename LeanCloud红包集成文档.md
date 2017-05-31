# LeanCloud红包集成文档

## 集成概述

* 红包SDK分为两个版本，即钱包版红包SDK与支付宝版红包SDK，目前Demo只集成了钱包版红包SDK。
* 使用钱包版红包SDK的用户，可以使用银行卡支付或支付宝支付等第三方支付来发红包；收到的红包金额会进入到钱包余额，并支持提现到绑定的银行卡。
* 使用支付宝版红包SDK的用户，发红包仅支持支付宝支付；收到的红包金额即时入账至绑定的支付宝账号。
* 请选择希望接入的版本并下载对应的SDK进行集成，钱包版红包SDK与支付宝版红包SDK集成方式相同。
* 需要注意的是如果已经集成了钱包版红包SDK，暂不支持切换到支付宝版红包SDK（两个版本不支持互通）。
* LeanCloud Demo中使用redPacket模块集成了红包SDK相关红能。

## 红包SDK的更新

以钱包版红包SDK为例，修改com.yunzhanghu.redpacket:redpacket-wallet:3.4.5中的3.4.5为已发布的更高版本(例如3.4.6)，同步之后即可完成红包SDK的更新。

## 开始集成

### 红包相关文件说明

* libs ：包含了红包所需要的jar包。
  * alipaySdk-20160516支付宝支付
  * glide-3.6.1图片加载库
  * volley-1.0.19请求框架
* res ：包含了红包SDK和聊天页面中的资源文件。（红包SDK相关以lc_开头）
* redpacket ：此包包含红包发送接收的工具类
  * GetSignInfoCallback 获取签名接口回调
  * GetGroupMemberCallback 获取群里面的人数（app开发者需要自己处理）的接口回调
  * RedPacketUtils 发送打开红包相关的工具类
* message ：
  * LCIMRedPacketMessage 自定义红包消息
  * LCIMRedPcketAckMessage 自定义通知消息，用于领取了红包之后，回执消息发给红包者
  * InputRedPacketClickEvent 红包按钮点击事件
* viewholder ：
  * ChatItemRedPacketHolder 红包消息处理机制
  * ChatItemRedPacketAckHolder 回执消息UI展示提供者
  * ChatItemRedPacketEmptyHolder 空消息用于隐藏和自己不相关的消息

### 添加对红包工程的依赖

```java
leanchat-android的build.gradle中
dependencies {
        compile files('libs/alipaySdk-20160516.jar')
        compile files('libs/glide-3.6.1.jar')
        compile files('libs/volley-1.0.19.jar')
        compile('com.yunzhanghu.redpacket:redpacket-wallet:3.4.5')
}
allprojects {
   repositories {
      jcenter()
      maven {
              url "https://raw.githubusercontent.com/YunzhanghuOpen/redpacket-maven-repo/master/release"
      }
   }
}
leanchat-android的setting.gradle中
        include ':leanchat'
```
## 红包消息组件及初始化

* 调用示例（以App为例）

### 注册红包消息组件

```java
AVIMMessageManager.registerAVIMMessageType(LCIMRedPacketMessage.class);
AVIMMessageManager.registerAVIMMessageType(LCIMRedPacketAckMessage.class);
```
### 初始化红包上下文

```java
@Override
public void onCreate() {
  // 初始化红包操作
  RedPacket.getInstance().initRedPacket(ctx,RPConstant.AUTH_METHOD_SIGN,  new RPInitRedPacketCallback() {       
      @Override
      public void initTokenData(final RPValueCallback<TokenData> rpValueCallback) {
          //在此方法中设置Token
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
}
//控制红包SDK中Log打印
RedPacket.getInstance().setDebugMode(true);
```
* **initRedPacket(context, authMethod, callback) 参数说明**

| 参数名称       | 参数类型             | 参数说明  | 必填         |
| ---------- | ----------------------- | ----- | ---------- |
| context    | Context                 | 上下文   | 是          |
| authMethod | String                  | 授权类型  | 是**（见注1）** |
| callback   | RPInitRedPacketCallback | 初始化接口 | 是          |  

* **RPInitRedPacketCallback 接口说明**

| **initTokenData(RPValueCallback<TokenData> callback)** |
| ---------------------------------------- |
| **该方法用于初始化TokenData，在进入红包相关页面、红包Token不存在或红包Token过期时调用。TokenData是请求红包Token所需要的数据模型，建议在该方法中异步向APP服务器获取相关参数，以保证数据的有效性；不建议从本地缓存中获取TokenData所需的参数，可能导致获取红包Token无效。** |
| **initCurrentUserSync()**                |
| **该方法用于初始化当前用户信息，在进入红包相关页面时调用，需同步获取。**   |

* **注1 ：**

**使用签名方式获取红包Token时，authMethod赋值必须为RPConstant.AUTH_METHOD_SIGN。**

* **注意：App Server提供的获取签名的接口必须先验证用户身份，并保证签名的用户和该登录用户一致，防止该接口被滥用。详见云账户[REST API开发文档](http://yunzhanghu-com.oss-cn-qdjbp-a.aliyuncs.com/%E4%BA%91%E8%B4%A6%E6%88%B7%E7%BA%A2%E5%8C%85%E6%8E%A5%E5%8F%A3%E6%96%87%E6%A1%A3-v3_1_0.pdf)** 

## redpacket 红包相关方法

### 增加红包按钮

```java 
private void addRedPacketView() {
  View readPacketView = LayoutInflater.from(getContext()).inflate(R.layout.input_bottom_redpacket_view, null);
  readPacketView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      EventBus.getDefault().post(new InputRedPacketClickEvent(imConversation.getConversationId()));
    }
  });
  inputBottomBar.addActionView(readPacketView);
}
```
### 添加红包入口

```java
public void startRedPacket(final FragmentActivity activity, final AVIMConversation imConversation, final int itemType, final String toUserId, final RPSendPacketCallback callback) {
  
  final RedPacketInfo redPacketInfo = new RedPacketInfo();
  
  if (itemType == RPConstant.RP_ITEM_TYPE_GROUP) {  
  
      //发送专属红包用的,获取群组成员
      RedPacket.getInstance().setRPGroupMemberListener(new RPGroupMemberListener() {
      
          @Override
          public void getGroupMember(String s, final RPValueCallback<List<RPUserBean>> rpValueCallback) {
          
              initRpGroupMember(imConversation.getMembers(), new GetGroupMemberCallback() {
                @Override
                public void groupInfoSuccess(List<RPUserBean> rpUserList) {
                  rpValueCallback.onSuccess(rpUserList);
                }
    
                @Override
                public void groupInfoError() {
    
                }
              });
            }
          });
          
          imConversation.getMemberCount(new AVIMConversationMemberCountCallback() {
            @Override
            public void done(Integer integer, AVIMException e) {
              redPacketInfo.toGroupId = imConversation.getConversationId();
              redPacketInfo.groupMemberCount = integer;
              RPRedPacketUtil.getInstance().startRedPacket(activity, itemType, redPacketInfo, 
                                                           callback);
            }
          });
        } else {
          redPacketInfo.toUserId = toUserId;
          LeanchatUser leanchatUser = UserCacheUtils.getCachedUser(toUserId);
          if (leanchatUser != null) {
            redPacketInfo.toNickName = TextUtils.isEmpty(leanchatUser.getUsername()) ? "none" : 
            leanchatUser.getUsername();
            redPacketInfo.toAvatarUrl = TextUtils.isEmpty(leanchatUser.getAvatarUrl()) ? "" : 
            leanchatUser.getAvatarUrl();
          }
          RPRedPacketUtil.getInstance().startRedPacket(activity, itemType, redPacketInfo, callback);
        }
} 
```

### 发送红包之后数据展示

```java
 public LCIMRedPacketMessage createRPMessage(Context context, RedPacketInfo redPacketInfo) {
    String selfName = LeanchatUser.getCurrentUser().getUsername();
    String selfID = LeanchatUser.getCurrentUserId();
    
    LCIMRedPacketMessage redPacketMessage = new LCIMRedPacketMessage();
    redPacketMessage.setGreeting(redPacketInfo.redPacketGreeting);
    redPacketMessage.setRedPacketId(redPacketInfo.redPacketId);
    redPacketMessage.setSponsorName(context.getResources().getString(R.string.leancloud_luckymoney));
    redPacketMessage.setRedPacketType(redPacketInfo.redPacketType);
    redPacketMessage.setReceiverId(redPacketInfo.toUserId);
    redPacketMessage.setMoney(true);
    redPacketMessage.setSenderName(selfName);
    redPacketMessage.setSenderId(selfID);
    return redPacketMessage;
}
```

## 拆红包消息及回执消息处理

### 拆红包及消息处理

* 调用示例（以ChatItemRedPacketHolder为例）

```java 
private void openRedPacket(final Context context, final LCIMRedPacketMessage message) {
    final ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setCanceledOnTouchOutside(false);
    
    int chatType;
    if (!TextUtils.isEmpty(message.getRedPacketType())) {
      chatType = RPConstant.CHATTYPE_GROUP;
    } else {
      chatType = RPConstant.CHATTYPE_SINGLE;
    }
    
    RPRedPacketUtil.getInstance().openRedPacket(wrapperRedPacketInfo(chatType, message),(FragmentActivity) context,new RPRedPacketUtil.RPOpenPacketCallback() {
          @Override
          public void onSuccess(String senderId, String senderNickname, String myAmount) {
             String selfName = LeanchatUser.getCurrentUser().getUsername();
             String selfId = LeanchatUser.getCurrentUserId();
             RedPacketUtil.getInstance().sendRedPacketAckMsg(senderId, senderNickname, selfId, selfName, message);
          }
    
          @Override
          public void showLoading() {
             progressDialog.show();
          }
    
          @Override
          public void hideLoading() {
             progressDialog.dismiss();
          }
    
          @Override
          public void onError(String code, String message) { 
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
          }
        });
}
``` 

```java

//封装拆红包所需参数
private RedPacketInfo wrapperRedPacketInfo(int chatType, LCIMRedPacketMessage message) {
    String redPacketId = message.getRedPacketId();
    String redPacketType = message.getRedPacketType();
    RedPacketInfo redPacketInfo = new RedPacketInfo();
    redPacketInfo.redPacketId = redPacketId;
    redPacketInfo.moneyMsgDirect = getMessageDirect(message);
    redPacketInfo.chatType = chatType;
    redPacketInfo.redPacketType = redPacketType;
    //3.4.0版之前集成过红包的用户，需要增加如下参数的传入对旧版本进行兼容
    if (!TextUtils.isEmpty(redPacketType) && redPacketType.equals(RPConstant.GROUP_RED_PACKET_TYPE_EXCLUSIVE)) {
       //打开专属红包需要多传一下的参数
       redPacketInfo.specialNickname = TextUtils.isEmpty(UserCacheUtils.getCachedUser(message.getReceiverId()).getUsername()) ? 
            "" : UserCacheUtils.getCachedUser(message.getReceiverId()).getUsername();
       redPacketInfo.specialAvatarUrl = TextUtils.isEmpty(UserCacheUtils.getCachedUser(message.getReceiverId()).getAvatarUrl()) ? 
            "none" : UserCacheUtils.getCachedUser(message.getReceiverId()).getAvatarUrl();
        }
        //兼容end
        return redPacketInfo;
}
```

```java
private String getMessageDirect(LCIMRedPacketMessage message) {
    String selfId = LeanchatUser.getCurrentUserId();
    String moneyMsgDirect; /*判断发送还是接收*/
    if (message.getFrom() != null && message.getFrom().equals(selfId)) {
      moneyMsgDirect = RPConstant.MESSAGE_DIRECT_SEND;
    } else {
      moneyMsgDirect = RPConstant.MESSAGE_DIRECT_RECEIVE;
    }
    return moneyMsgDirect;
} 
```

### 会话列表回执消息的处理 

* 调用示例（以LCIMRedPcketAckMessage为例）

```java
@Override
public String getShorthand() {
   String userId=LeanchatUser.getCurrentUserId();
   if (userId.equals(senderId)&&userId.equals(recipientId)){
     return "你领取了自己的红包";
   }else if (userId.equals(senderId)&&!userId.equals(recipientId)){
     return recipientName+"领取了你的红包";
   }else if (!userId.equals(senderId)&&userId.equals(recipientId)){
     return "你领取了"+senderName+"的红包";
   }else if (!userId.equals(senderId)&&!userId.equals(recipientId)){
     if (senderId.equals(recipientId)){
       return recipientName+"领取了自己的红包";
     }else {
       return recipientName+"领取了"+senderName+"的红包";
     }
   }
   return null;
}
```

### 会话详情回执消息的处理

* 调用示例（以ChatItemRedPacketAckHolder为例）

```java
private void initRedPacketAckChatItem(String senderName, String   recipientName, boolean isSelf, boolean isSend, boolean isSingle) {
    if (isSend) {
      if (!isSingle) {
        if (isSelf) {
          contentView.setText(R.string.money_msg_take_money);
        } else {           
           contentView.setText(String.format(getContext().           
           getResources().
           getString(R.string.money_msg_take_someone_money),     
           senderName));
        }
      } else {
           contentView.setText(String.format(getContext().
           getResources().
           getString(R.string.money_msg_take_someone_money),  
           senderName));
      }
    } else {
      if (isSelf) {
           contentView.setText(String.format(getContext().
           getResources().
           getString(R.string.money_msg_someone_take_money),     
           recipientName));
      } else {
           contentView.setText(String.format(getContext().
           getResources().
           getString(R.string.money_msg_someone_take_money_same),    
           recipientName, senderName));
      }
    }
}      
```   

### 处理红包消息类型

* 调用示例（以ChatAdapter为例）

```java
//判断是什么消息类型
@Override
public int getItemViewType(int position) {
    AVIMMessage message = messageList.get(position);
    if (null != message && message instanceof AVIMTypedMessage) {
      AVIMTypedMessage typedMessage = (AVIMTypedMessage) message;
      boolean isMe = fromMe(typedMessage);
      if (typedMessage.getMessageType() == LCIMRedPacketMessage.RED_PACKET_MESSAGE_TYPE) {
        return isMe ? ITEM_RIGHT_TEXT_RED_PACKET : ITEM_LEFT_TEXT_RED_PACKET;
      } else if (typedMessage.getMessageType() ==LCIMRedPacketAckMessage.RED_PACKET_ACK_MESSAGE_TYPE) {
        return RedPacketUtils.getInstance().receiveRedPacketAckMsg((LCIMRedPacketAckMessage)typedMessage,ITEM_TEXT_RED_PACKET_NOTIFY,ITEM_TEXT_RED_PACKET_NOTIFY_MEMBER);
      }
    }
    return super.getItemViewType(position);
}
```

## 零钱页的入口

* 调用示例(以ProfileFragment为例)

```java
RPRedPacketUtil.getInstance().startChangeActivity(getActivity());
获取零钱余额接口(仅支持钱包版)

RPRedPacketUtil.getInstance().getChangeBalance(new RPValueCallback<String>() {
      @Override
      public void onSuccess(String changeBalance) {

      }

      @Override
      public void onError(String errorCode, String errorMsg) {

      }
});
```

## detachView接口

* RPRedPacketUtil.getInstance().detachView()

* 在拆红包方法所在页面销毁时调用，可防止内存泄漏。

* 调用示例(以ConversationFragment为例)

```java
@Override
public void onDestroy() {
    super.onDestroy();
    RPRedPacketUtil.getInstance().detachView();
}
```

## 拆红包音效
* 在assets目录下添加open_packet_sound.mp3或者open_packet_sound.wav文件即可(文件大小不要超过1M)。





