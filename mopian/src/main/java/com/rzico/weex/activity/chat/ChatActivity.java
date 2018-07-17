package com.rzico.weex.activity.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.adapter.chat.ChatAdapter;
import com.rzico.weex.db.DbUtils;
import com.rzico.weex.db.bean.Redis;
import com.rzico.weex.model.chat.ChatInfo;
import com.rzico.weex.model.chat.CustomMessage;
import com.rzico.weex.model.chat.FileMessage;
import com.rzico.weex.model.chat.ImageMessage;
import com.rzico.weex.model.chat.Message;
import com.rzico.weex.model.chat.TextMessage;
import com.rzico.weex.model.chat.UGCMessage;
import com.rzico.weex.model.chat.VideoMessage;
import com.rzico.weex.model.chat.VoiceMessage;
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.model.info.IMMessage;
import com.rzico.weex.utils.BarTextColorUtils;
import com.rzico.weex.utils.SharedUtils;
import com.rzico.weex.utils.Utils;
import com.rzico.weex.utils.chat.FileUtil;
import com.rzico.weex.utils.chat.MediaUtil;
import com.rzico.weex.utils.chat.MessageFactory;
import com.rzico.weex.utils.chat.RecorderUtil;
import com.tencent.qcloud.ui.EmojiManager;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMMessageDraft;
import com.tencent.imsdk.ext.message.TIMMessageExt;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.qcloud.presentation.presenter.ChatPresenter;
import com.tencent.qcloud.presentation.viewfeatures.ChatView;
import com.tencent.qcloud.ui.ChatInput;
import com.tencent.qcloud.ui.TemplateTitle;
import com.tencent.qcloud.ui.VoiceSendingView;

import org.greenrobot.eventbus.EventBus;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends BaseActivity implements ChatView {
    private static final String TAG = "ChatActivity";
    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
    private ChatPresenter presenter;
    private ChatInput input;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int IMAGE_PREVIEW = 400;
    private static final int VIDEO_RECORD = 500;
    private Uri fileUri;
    private VoiceSendingView voiceSendingView;
    private String identify;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;
    private String titleStr;
    private ChatInfo chatInfo;
    private Handler handler = new Handler();

    private WXApplication wxApplication;
    private boolean isBlack = true;

    public static void navToChat(final Context context, final String identify, final TIMConversationType type){
        if(identify != null && identify.equals("")) return;
        if(context == null)return;
        if (!Utils.checkConnection(context)) {//如果网络不通
            try {
                Redis redis = DbUtils.find("chatInfoCache", SharedUtils.readImId() + identify);

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("identify", identify);
                intent.putExtra("type", type);
                if(redis!=null){
                    ChatInfo chatInfo = new Gson().fromJson(redis.getValue(), ChatInfo.class);
                    if(chatInfo!=null){
                        intent.putExtra("chatInfo", chatInfo);
                    }
                }
                context.startActivity(intent);
            } catch (DbException e) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("identify", identify);
                intent.putExtra("type", type);
                e.printStackTrace();
            }
            return;
        }
        List<String> users = new ArrayList<>();
        users.add(identify);
        users.add(SharedUtils.readImId());
        //获取用户资料
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
            @Override
            public void onError(int code, String desc){
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("identify", identify);
                intent.putExtra("type", type);
                context.startActivity(intent);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result){
                if(result != null && result.size() == 2){
                    TIMUserProfile userProfile1 = result.get(0);
                    TIMUserProfile userProfile2 = result.get(1);
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("identify", identify);
                    intent.putExtra("type", type);
                    ChatInfo chatInfo = new ChatInfo();
                    if(userProfile1.getIdentifier().equals(identify)){
                        //表示 这个人是对方
                        chatInfo.setUserNikeName(userProfile1.getNickName());
                        chatInfo.setUserHeadImg(userProfile1.getFaceUrl());
                        chatInfo.setMyHeadImg(userProfile2.getFaceUrl());
                    }else{
                        //表示这个人是自己
                        chatInfo.setUserNikeName(userProfile2.getNickName());
                        chatInfo.setUserHeadImg(userProfile2.getFaceUrl());
                        chatInfo.setMyHeadImg(userProfile1.getFaceUrl());
                    }
                    intent.putExtra("chatInfo", chatInfo);
                    try {
                        //保存缓存
                        DbUtils.save("chatInfoCache",SharedUtils.readImId() + identify, new Gson().toJson(chatInfo), identify, identify);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("identify", identify);
                    intent.putExtra("type", type);
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wxApplication = (WXApplication) this.getApplicationContext();
        BarTextColorUtils.StatusBarLightMode(this, R.color.wxColor);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        EmojiManager.init(this);
        setContentView(R.layout.activity_chat);
        identify = getIntent().getStringExtra("identify");
        type = (TIMConversationType) getIntent().getSerializableExtra("type");
        chatInfo = (ChatInfo) getIntent().getSerializableExtra("chatInfo");


        presenter = new ChatPresenter(this, identify, type);
        input = (ChatInput) findViewById(R.id.input_panel);
        input.setChatView(this);


        adapter = new ChatAdapter(this, R.layout.item_message, messageList, chatInfo);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });
        //将聊天记录移到最下面

        input.setOnTouchEditTextListener(new ChatInput.OnTouchEditTextListener() {
            @Override
            public void onTouch() {
                listView.setSelection(listView.getCount() - 1);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int firstItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter.getMessage(messageList.size() > 0 ? messageList.get(0).getMessage() : null);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });
        registerForContextMenu(listView);
        TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
        switch (type) {
            case C2C:

                title.setTitleText(titleStr = chatInfo == null ? identify : chatInfo.getUserNikeName());
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;

        }
        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
        presenter.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //退出聊天界面时输入框有内容，保存草稿
        TextMessage message = new TextMessage(input.getText());
        if (input.getText().length() > 0){
            presenter.saveDraft(message.getMessage());
            onMessage(message.getMessage(), true);
        }else{
            presenter.saveDraft(null);
            if(messageList != null && messageList.size() > 0){
                onMessage(messageList.get(messageList.size() - 1).getMessage(), true);
            }
        }
//        RefreshEvent.getInstance().onRefresh();
        presenter.readMessages();
        MediaUtil.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }


    /**
     * 显示消息
     *
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {
        if (message == null) {
            adapter.notifyDataSetChanged();
        } else {
            Message mMessage = MessageFactory.getMessage(message);
            if (mMessage != null) {
                if (mMessage instanceof CustomMessage){
                    CustomMessage.Type messageType = ((CustomMessage) mMessage).getType();
                    switch (messageType){
                        case TYPING:
                            TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
                            title.setTitleText(getString(R.string.chat_typing));
                            handler.removeCallbacks(resetTitle);
                            handler.postDelayed(resetTitle,3000);
                            break;
                        default:
                            break;
                    }
                }else{
                    if (messageList.size()==0){
                        mMessage.setHasTime(null);
                    }else{
                        mMessage.setHasTime(messageList.get(messageList.size()-1).getMessage());
                    }
                    messageList.add(mMessage);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(adapter.getCount()-1);
                }

            }
        }

    }

    /**
     * 显示消息
     *
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i){
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted) continue;
            if (mMessage instanceof CustomMessage && (((CustomMessage) mMessage).getType() == CustomMessage.Type.TYPING ||
                    ((CustomMessage) mMessage).getType() == CustomMessage.Type.INVALID)) continue;
            ++newMsgNum;
            if (i != messages.size() - 1){
                mMessage.setHasTime(messages.get(i+1));
                messageList.add(0, mMessage);
            }else{
                mMessage.setHasTime(null);
                messageList.add(0, mMessage);
            }
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(newMsgNum);
    }

    @Override
    public void showRevokeMessage(TIMMessageLocator timMessageLocator) {
        for (Message msg : messageList) {
            TIMMessageExt ext = new TIMMessageExt(msg.getMessage());
            if (ext.checkEquals(timMessageLocator)) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 清除所有消息，等待刷新
     */
    @Override
    public void clearAllMessage() {
        messageList.clear();
    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (Message msg : messageList){
            if (msg.getMessage().getMsgUniqueId() == id){
                switch (code){
                    case 80001:
                        //发送内容包含敏感词
                        msg.setDesc(getString(R.string.chat_content_bad));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
        adapter.notifyDataSetChanged();

    }
    /**
     * 发送图片消息
     */
    @Override
    public void sendImage() {
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }
    public void onMessage(TIMMessage msg){
        onMessage(msg, false);
    }

    public void onMessage(TIMMessage msg, final boolean isDraft){
        if(!(MessageFactory.getMessage(msg) instanceof CustomMessage)){//如果不是自定义消息则提示 是自己的信息的话就把对方的信息发送
            final com.rzico.weex.model.chat.Message message = MessageFactory.getMessage(msg);
            if(message == null) return;
            System.out.println("send:" + message.getSender());
            System.out.println("content:" + message.getSummary());
//                        获取会话扩展实例
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, message.getSender());
            final TIMConversationExt conExt = new TIMConversationExt(con);
            System.out.println("unRead:" + conExt.getUnreadMessageNum());
            List<String> users = new ArrayList<>();
            users.add(identify);

            //获取好友资料
            TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
                @Override
                public void onError(int code, String desc){
                    //错误码code和错误描述desc，可用于定位请求失败原因
                    //错误码code列表请参见错误码表
                    Log.e("weex", "getFriendsProfile failed: " + code + " desc");
                    com.rzico.weex.model.info.Message onMessage = new com.rzico.weex.model.info.Message();
                    onMessage.setType("success");
                    onMessage.setContent("您有一条新消息");
                    IMMessage imMessage = new IMMessage();
                    imMessage.setContent(message.getSummary());
                    imMessage.setDraft(isDraft);
                    imMessage.setId(identify);
                    imMessage.setLogo("");
                    imMessage.setNickName("");
                    imMessage.setUnRead(0);
                    imMessage.setCreateDate(message.getMessage().timestamp());
                    onMessage.setData(imMessage);
                    Map<String, Object> params = new HashMap<>();
                    params.put("data", onMessage);

                    EventBus.getDefault().post(new MessageBus(MessageBus.Type.GLOBAL, "onMessage", params));

                }

                @Override
                public void onSuccess(List<TIMUserProfile> result){
                    TIMUserProfile user = result.get(0);
                    com.rzico.weex.model.info.Message onMessage = new com.rzico.weex.model.info.Message();
                    onMessage.setType("success");
                    onMessage.setContent("您有一条新消息");
                    IMMessage imMessage = new IMMessage();
                    imMessage.setUnRead(conExt.getUnreadMessageNum());
                    imMessage.setContent(message.getSummary());
                    imMessage.setId(user.getIdentifier());
                    imMessage.setLogo(user.getFaceUrl());
                    imMessage.setNickName(user.getNickName());
                    imMessage.setDraft(isDraft);
                    imMessage.setCreateDate(message.getMessage().timestamp());
                    onMessage.setData(imMessage);
                    Map<String, Object> params = new HashMap<>();
                    params.put("data", onMessage);
                    EventBus.getDefault().post(new MessageBus(MessageBus.Type.GLOBAL, "onMessage", params));

                }
            });

        }
    }
    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {
        Message message = new TextMessage(input.getText().toString());
        presenter.sendMessage(message.getMessage());
        onMessage(message.getMessage());
        input.setText("");
    }

    /**
     * 发送文件
     */
    @Override
    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_CODE);
    }


    /**
     * 开始发送语音消息
     */
    @Override
    public void startSendVoice() {
        voiceSendingView.setVisibility(View.VISIBLE);
        voiceSendingView.showRecording();
        recorder.startRecording();

    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void endSendVoice() {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else if (recorder.getTimeInterval() > 60) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_long), Toast.LENGTH_SHORT).show();
        } else {
            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
            presenter.sendMessage(message.getMessage());
            onMessage(message.getMessage());
        }
    }

    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
        Message message = new VideoMessage(fileName);
        presenter.sendMessage(message.getMessage());
        onMessage(message.getMessage());
    }


    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice() {

    }

    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (type == TIMConversationType.C2C){
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            presenter.sendOnlineMessage(message.getMessage());
        }
    }

    /**
     * 显示草稿
     */
    @Override
    public void showDraft(TIMMessageDraft draft) {
        input.getText().append(TextMessage.getString(draft.getElems(), this));
    }

    @Override
    public void videoAction() {
//        Intent intent = new Intent(this, TCVideoRecordActivity.class);
//        startActivityForResult(intent, VIDEO_RECORD);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Message message = messageList.get(info.position);
        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del));
        if (message.isSendFail()){
            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend));
        }else if (message.getMessage().isSelf()){
            menu.add(0, 4, Menu.NONE, getString(R.string.chat_pullback));
        }
        if (message instanceof ImageMessage || message instanceof FileMessage){
            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = messageList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                message.remove();
                messageList.remove(info.position);
                adapter.notifyDataSetChanged();
                break;
            case 2:
                messageList.remove(message);
                presenter.sendMessage(message.getMessage());
                onMessage(message.getMessage());
                break;
            case 3:
                message.save();
                break;
            case 4:
                presenter.revokeMessage(message.getMessage());
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && fileUri != null) {
                showImagePreview(fileUri.getPath());
            }
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(this, data.getData()));
            }

        } else if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {
                sendFile(FileUtil.getFilePath(this, data.getData()));
            }
        } else if (requestCode == IMAGE_PREVIEW){
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri",false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists()){
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    if (file.length() == 0 && options.outWidth == 0) {
                        Toast.makeText(this, getString(R.string.chat_file_not_exist),Toast.LENGTH_SHORT).show();
                    }else {
                        if (file.length() > 1024 * 1024 * 10){
                            Toast.makeText(this, getString(R.string.chat_file_too_large),Toast.LENGTH_SHORT).show();
                        }else{
                            Message message = new ImageMessage(path,isOri);
                            presenter.sendMessage(message.getMessage());
                            onMessage(message.getMessage());
                        }
                    }
                }else{
                    Toast.makeText(this, getString(R.string.chat_file_not_exist),Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == VIDEO_RECORD) {
            if (resultCode == RESULT_OK) {
                String videoPath = data.getStringExtra("videoPath");
                String coverPath = data.getStringExtra("coverPath");
                long duration = data.getLongExtra("duration", 0);
                Message message = new UGCMessage(videoPath, coverPath, duration);
                presenter.sendMessage(message.getMessage());
                onMessage(message.getMessage());
            }
        }

    }


    private void showImagePreview(String path){
        if (path == null) return;
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void sendFile(String path){
        if (path == null) return;
        File file = new File(path);
        if (file.exists()){
            if (file.length() > 1024 * 1024 * 10){
                Toast.makeText(this, getString(R.string.chat_file_too_large),Toast.LENGTH_SHORT).show();
            }else{
                Message message = new FileMessage(path);
                presenter.sendMessage(message.getMessage());
                onMessage(message.getMessage());
            }
        }else{
            Toast.makeText(this, getString(R.string.chat_file_not_exist),Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 将标题设置为对象名称
     */
    private Runnable resetTitle = new Runnable() {
        @Override
        public void run() {
            TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
            title.setTitleText(titleStr);
        }
    };


}
