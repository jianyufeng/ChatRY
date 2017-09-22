package com.sanbafule.sharelock.chatting.help;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.sanbafule.sharelock.Constants;
import com.sanbafule.sharelock.UI.group.BitmapCompress;
import com.sanbafule.sharelock.chatting.Interface.GetLatestMessagesCallBack;
import com.sanbafule.sharelock.chatting.Interface.SendMessageCallBack;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.activity.TranspondMessageActivity;
import com.sanbafule.sharelock.chatting.adapter.ChattingAdapter;
import com.sanbafule.sharelock.chatting.modle.FileMessageType;
import com.sanbafule.sharelock.chatting.modle.ViewImageInfo;
import com.sanbafule.sharelock.chatting.modle.message.ShareLockMessage;
import com.sanbafule.sharelock.comm.SCode;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.util.BitmapUtil;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.FileUtil;
import com.sanbafule.sharelock.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import sj.keyboard.data.EmoticonEntity;

/**
 * Created by Administrator on 2016/10/13.
 */
public class SChattingHelp {


    /**
     * 发送文本消息
     *
     * @param conversationType
     * @param userId
     * @param contant
     */
    public static void sendTextMessage(final Context context, final Conversation.ConversationType conversationType, final String userId, final String contant, MentionedInfo mentionedInfo, final SendMessageCallBack callBack) {
        final TextMessage textMessage = TextMessage.obtain(contant);
        if (mentionedInfo != null) {
            textMessage.setMentionedInfo(mentionedInfo);
        }
        Message message = Message.obtain(userId, conversationType, textMessage);
        message.setMessageDirection(Message.MessageDirection.SEND);
        message.setSentTime(new Date().getTime());
        message.setConversationType(conversationType);
        message.setTargetId(userId);
        message.setSenderUserId(ShareLockManager.getInstance().getClentUser().getU_username());
        message.setSentStatus(Message.SentStatus.SENDING);
        final ShareLockMessage shareLockMessage = new ShareLockMessage();
        shareLockMessage.setMessage(message);
        ChattingAdapter adapter = ((GroupChattingActivity) context).getChattingAdapter();
        adapter.addData(shareLockMessage);
        Log.e("123456789", "onAttached:: " + System.currentTimeMillis());
        RongIMClient.getInstance().sendMessage(message, contant, "", new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                        Log.e("123456789", "onAttached:: " + System.currentTimeMillis());
                    }

                    @Override
                    public void onSuccess(Message message) {
                        shareLockMessage.setMessage(message);
                        callBack.onSuccess(shareLockMessage);
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        shareLockMessage.setMessage(message);
                        callBack.onError(shareLockMessage, errorCode);
                    }
                }
        );

    }

    /**
     * 消息重发
     *
     * @param message
     * @param adapter
     */
    public static void reSendMessage(ShareLockMessage message, ChattingAdapter adapter) {
        MessageContent content = message.getMessage().getContent();
        if (content instanceof TextMessage) {
            reSendTextMessage(message, adapter);
        } else if (content instanceof VoiceMessage) {
            reSendVoiceMessage(message, adapter);
        } else if (content instanceof ImageMessage) {
            reSendImageMessage(message, adapter);
        } else if (content instanceof FileMessage) {
            if (((FileMessage) content).getType().equals(FileMessageType.VIDEO.toString())) {
                reSendVideoMessage(message, adapter);
            } else if (((FileMessage) content).getType().equals(FileMessageType.GIF.toString())) {
                reSendGifMessage(message, adapter);
            }

        }
    }

    private static void reSendGifMessage(final ShareLockMessage message, final ChattingAdapter adapter) {

        RongIMClient.getInstance().sendMediaMessage(message.getMessage(), "表情", "", new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message m, int i) {
                message.setProgress(i);
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCanceled(Message message) {

            }

            @Override
            public void onAttached(Message m) {

            }

            @Override
            public void onSuccess(Message m) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Message m, RongIMClient.ErrorCode errorCode) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private static void reSendVideoMessage(final ShareLockMessage message, final ChattingAdapter adapter) {
        RongIMClient.getInstance().sendMediaMessage(message.getMessage(), "小视屏", "", new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message m, int i) {
                message.setProgress(i);
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCanceled(Message message) {

            }

            @Override
            public void onAttached(Message m) {

            }

            @Override
            public void onSuccess(Message m) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Message m, RongIMClient.ErrorCode errorCode) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 图片消息的重发
     *
     * @param message
     * @param adapter
     */
    private static void reSendImageMessage(final ShareLockMessage message, final ChattingAdapter adapter) {
        RongIMClient.getInstance().sendImageMessage(message.getMessage(), "图片", null, new RongIMClient.SendImageMessageCallback() {
            @Override
            public void onAttached(final Message m) {

            }

            @Override
            public void onError(Message m, RongIMClient.ErrorCode errorCode) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(Message m) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onProgress(Message m, int i) {

                message.setProgress(i);
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * 文本消息的重发
     *
     * @param message
     * @param adapter
     */
    public static void reSendTextMessage(final ShareLockMessage message, final ChattingAdapter adapter) {
        final int[] messageIds = {message.getMessage().getMessageId()};
        RongIMClient.getInstance().sendMessage(message.getMessage(), ((TextMessage) message.getMessage().getContent()).getContent(), null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message m) {

            }

            @Override
            public void onSuccess(Message m) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Message m, RongIMClient.ErrorCode errorCode) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 语音消息的重发
     *
     * @param message
     * @param adapter
     */
    public static void reSendVoiceMessage(final ShareLockMessage message, final ChattingAdapter adapter) {
        RongIMClient.getInstance().sendMessage(message.getMessage(), "语音", null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message m) {
            }

            @Override
            public void onSuccess(Message m) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Message m, RongIMClient.ErrorCode errorCode) {
                message.setMessage(m);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 发送语音消息
     */

    public static void sendVoiceMessage(Context context, final String userId, String url, int duration, final Conversation.ConversationType conversationType, final SendMessageCallBack callBack) {
        final VoiceMessage voiceMessage = VoiceMessage.obtain(Uri.fromFile(new File(url)), duration);
        Message message = Message.obtain(userId, conversationType, voiceMessage);
        message.setMessageDirection(Message.MessageDirection.SEND);
        message.setSentTime(new Date().getTime());
        message.setConversationType(conversationType);
        message.setTargetId(userId);
        message.setSenderUserId(ShareLockManager.getInstance().getClentUser().getU_username());
        message.setSentStatus(Message.SentStatus.SENDING);
        final ShareLockMessage shareLockMessage = new ShareLockMessage();
        shareLockMessage.setMessage(message);
        ChattingAdapter adapter = ((GroupChattingActivity) context).getChattingAdapter();
        adapter.addData(shareLockMessage);
        RongIMClient.getInstance().sendMessage(message, "语音", "", new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        shareLockMessage.setMessage(message);
                        callBack.onSuccess(shareLockMessage);
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        shareLockMessage.setMessage(message);
                        callBack.onError(shareLockMessage, errorCode);
                    }
                }

        );
    }

    /**
     * 转发文字消息
     *
     * @param message
     */
    public static void transpondTextMessage(String targetName, TextMessage message) {


        RongIMClient.getInstance().sendMessage(Conversation.ConversationType
                        .PRIVATE, targetName, message, message.getContent(), "", new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                        Log.e("123456789", "onAttached:: " + System.currentTimeMillis());
                    }

                    @Override
                    public void onSuccess(Message message) {


                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    }
                }
        );

    }

    /**
     * 转发文字消息
     *
     * @param name
     * @param contant
     */
    public static void transpondTextMessage(String name, String contant) {
        if (MyString.hasData(contant)) {
            final TextMessage textMessage = TextMessage.obtain(contant);
            RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, name, textMessage, contant, null, new IRongCallback.ISendMessageCallback() {
                        @Override
                        public void onAttached(Message message) {

                        }

                        @Override
                        public void onSuccess(Message message) {

                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                        }
                    }
            );
        }

    }

    /**
     * 发送图片消息
     *
     * @param context
     * @param conversationType
     * @param userId
     * @param filePath
     * @param callBack
     */
    public static void sendImageMessage(final Context context, final Conversation.ConversationType conversationType, final String userId, final String filePath, final SendMessageCallBack callBack) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        File imageFileSource = new File(context.getCacheDir(), Constants.FILE_TAG + timeStamp + "source.jpg");
        File imageFileThumb = new File(context.getCacheDir(), Constants.FILE_TAG + timeStamp + "thumb.jpg");
        try {
            //是否含有文件
            if (!FileUtil.checkFile(filePath)) {
                return;
            }
            if (!imageFileSource.exists()) {
                imageFileSource.createNewFile();
            }
            if (!imageFileThumb.exists()) {
                imageFileThumb.createNewFile();
            }

            // 压缩原始图片
            Bitmap bitmap = BitmapCompress.extractThumbNail(filePath, SCode.SC_MAX_WIDTH,
                    SCode.SC_MAX_HEIGHT, false);
//            // 读取图片的旋转角度
            int degree = BitmapUtil.getImageDegree(filePath);
//            // 调整图片的角度
            if (degree != 0) {
                bitmap = BitmapUtil.rotatingImageView(degree, bitmap);
            }
            // 把压缩的bitmap重新保存到文件
            FileOutputStream fosSource = new FileOutputStream(imageFileSource);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosSource);// 保存原图

            // 创建缩略图变换矩阵。
            final Matrix m = new Matrix();
            // 获取Bitmap的宽 高
            float rWidth = bitmap.getWidth();
            float rHeight = bitmap.getHeight();
            float sc = rHeight / rWidth;
            // 根据100dp 的最大宽度算出图片的宽度的最大像素
            int i = DensityUtil.dip2px(SCode.THUMB_MAX_WIDTH);
            // 根据图片的原始比压缩图片
            if (rWidth > i) rWidth = i;
            m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, rWidth, rWidth * sc), Matrix.ScaleToFit.CENTER);
            Bitmap bmpThumb = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);// 生成缩略图
            // 保存缩略图的文件
            FileOutputStream thumb = new FileOutputStream(imageFileThumb);
            bmpThumb.compress(Bitmap.CompressFormat.JPEG, 100, thumb);

            if (bitmap != null && !bitmap.isRecycled())
                bitmap.recycle();
            if (bmpThumb != null && !bmpThumb.isRecycled())
                bmpThumb.recycle();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; //只获取图像的大小
            BitmapFactory.decodeFile(imageFileThumb.getAbsolutePath(), options);
            int width = options.outWidth;
            int height = options.outHeight;

            ImageMessage imgMsg = ImageMessage.obtain(Uri.fromFile(imageFileThumb), Uri.fromFile(imageFileSource));

            JSONObject object = new JSONObject();
            object.put(SString.WIDTH, width);
            object.put(SString.HEIGHT, height);
            imgMsg.setExtra(object.toString());

            Message message = Message.obtain(userId, conversationType, imgMsg);
            message.setMessageDirection(Message.MessageDirection.SEND);
            message.setSentTime(new Date().getTime());
            message.setConversationType(conversationType);
            message.setTargetId(userId);
            message.setSenderUserId(ShareLockManager.getInstance().getClentUser().getU_username());
            message.setSentStatus(Message.SentStatus.SENDING);

            final ShareLockMessage shareLockMessage = new ShareLockMessage();
            shareLockMessage.setMessage(message);
            ChattingAdapter adapter = ((GroupChattingActivity) context).getChattingAdapter();
            adapter.addData(shareLockMessage);
            Log.e("123456789img", "onAttached: " + System.currentTimeMillis());

            RongIMClient.getInstance().sendImageMessage(conversationType, userId, imgMsg, userId, userId, new RongIMClient.SendImageMessageCallback() {

                @Override
                public void onAttached(Message message) {
                    Log.e("123456789img", "onAttached: " + System.currentTimeMillis());

                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode code) {
                    Log.e("123456789img", "onError: " + System.currentTimeMillis());
                    shareLockMessage.setMessage(message);
                    callBack.onError(shareLockMessage, code);
                }

                @Override
                public void onSuccess(Message message) {
                    Log.e("123456789img", "onSuccess: " + System.currentTimeMillis());
                    shareLockMessage.setMessage(message);
                    callBack.onSuccess(shareLockMessage);
                }

                @Override
                public void onProgress(Message message, final int progress) {
                    //发送进度
                    Log.e("123456789img", "onProgress: " + System.currentTimeMillis());
                    shareLockMessage.setMessage(message);
                    shareLockMessage.setProgress(progress);
                    callBack.onProgress(shareLockMessage, progress);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 转发图片消息
     */
    public static void transpondImageMessage(String targetId, ImageMessage imageMessage) {

        ImageMessage imgMsg = ImageMessage.obtain(imageMessage.getThumUri(), imageMessage.getLocalUri());
        imgMsg.setExtra(imageMessage.getExtra());
        Message message = Message.obtain(targetId, Conversation.ConversationType.PRIVATE, imgMsg);
        message.setSentTime(new Date().getTime());
        message.setSenderUserId(ShareLockManager.getInstance().getClentUser().getU_username());
        RongIMClient.getInstance().sendImageMessage(message, null, null, new RongIMClient.SendImageMessageCallback() {
            @Override
            public void onAttached(Message message) {
                LogUtil.i(message.getSenderUserId());
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                LogUtil.i(message.getSenderUserId() + errorCode);
            }

            @Override
            public void onSuccess(Message message) {
                LogUtil.i(message.getSenderUserId());
            }

            @Override
            public void onProgress(Message message, int progress) {
                LogUtil.i(message.getSenderUserId() + progress);
            }
        });

    }

    /**
     * 发送文件消息
     *
     * @param userId
     * @param url
     */
    public static void sendFileMessage(String userId, String url) {

    }

    /**
     * 发送小视屏
     */
    public static void sendVideoMessage(Context context, String userId, Conversation.ConversationType conversationType, String url, final SendMessageCallBack callBack) {
        File file = new File(url);
        //获取第一帧的
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MINI_KIND);
        //处理第一帧
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, DensityUtil.dip2px(45), DensityUtil.dip2px(75), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        String base64FromBitmap = io.rong.message.utils.BitmapUtil.getBase64FromBitmap(bitmap);
        Log.e("ssfs", base64FromBitmap.length() + "");
        FileMessage fileMessage = FileMessage.obtain(Uri.fromFile(file));
        fileMessage.setExtra(base64FromBitmap);
        fileMessage.setType(FileMessageType.VIDEO.toString());
        Message message = Message.obtain(userId, conversationType, fileMessage);
        message.setMessageDirection(Message.MessageDirection.SEND);
        message.setSentTime(System.currentTimeMillis());
        message.setConversationType(conversationType);
        message.setTargetId(userId);
        message.setSenderUserId(ShareLockManager.getInstance().getClentUser().getU_username());
        message.setSentStatus(Message.SentStatus.SENDING);
        final ShareLockMessage shareLockMessage = new ShareLockMessage();
        shareLockMessage.setMessage(message);
        ChattingAdapter adapter = ((GroupChattingActivity) context).getChattingAdapter();
        adapter.addData(shareLockMessage);
        RongIMClient.getInstance().sendMediaMessage(message, "小视屏", null, new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int i) {
                shareLockMessage.setMessage(message);
                shareLockMessage.setProgress(i);
                callBack.onProgress(shareLockMessage, i);
            }

            @Override
            public void onCanceled(Message message) {

            }

            @Override
            public void onAttached(Message message) {
                LogUtil.i("保存成功");
            }

            @Override
            public void onSuccess(Message message) {
                shareLockMessage.setMessage(message);
                callBack.onSuccess(shareLockMessage);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                shareLockMessage.setMessage(message);
                callBack.onError(shareLockMessage, errorCode);
            }
        });

    }


    /**
     * 小视屏的转发
     *
     * @param targetName
     * @param content
     */
    public static void transpondVideoMessage(String targetName, FileMessage content) {
        Message message = Message.obtain(targetName, Conversation.ConversationType.PRIVATE, content);
        RongIMClient.getInstance().sendMediaMessage(message, "小视屏", null, new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int i) {

            }

            @Override
            public void onCanceled(Message message) {

            }

            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {

            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {

            }
        });

    }

    /**
     * 发送gif消息
     *
     * @param userId
     * @param image
     */
    public static void sendGifMessage(Context context, Conversation.ConversationType conversationType, String userId, EmoticonEntity image, final SendMessageCallBack callBack) {

        final Uri filePath = Uri.parse(image.getIconUri());
        FileMessage fileMessage = FileMessage.obtain(filePath);
        fileMessage.setType("GIF");
        fileMessage.setExtra(image.getContent());
        Message message = Message.obtain(userId, conversationType, fileMessage);
        message.setMessageDirection(Message.MessageDirection.SEND);
        message.setSentTime(new Date().getTime());
        message.setConversationType(conversationType);
        message.setTargetId(userId);
        message.setSenderUserId(ShareLockManager.getInstance().getClentUser().getU_username());
        message.setSentStatus(Message.SentStatus.SENDING);

        final ShareLockMessage shareLockMessage = new ShareLockMessage();
        shareLockMessage.setMessage(message);

        ChattingAdapter adapter = ((GroupChattingActivity) context).getChattingAdapter();
        adapter.addData(shareLockMessage);
        RongIMClient.getInstance().sendMediaMessage(message, "表情", null, new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int i) {
                LogUtil.i(message + "");
                shareLockMessage.setMessage(message);
                shareLockMessage.setProgress(i);
                callBack.onProgress(shareLockMessage, i);
            }

            @Override
            public void onCanceled(Message message) {

            }

            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
                shareLockMessage.setMessage(message);
                LogUtil.i(message + "");
                callBack.onSuccess(shareLockMessage);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                shareLockMessage.setMessage(message);
                LogUtil.i(message + "");
                callBack.onError(shareLockMessage, errorCode);
            }
        });

    }

    /***
     * 转发Gif消息
     */
    public static void transpondGifMessage(String targetName, FileMessage fileMessage) {
        Message message = Message.obtain(targetName, Conversation.ConversationType.PRIVATE, fileMessage);
        RongIMClient.getInstance().sendMediaMessage(message, fileMessage.getExtra(), null, new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int i) {

            }

            @Override
            public void onCanceled(Message message) {

            }

            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {

            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    /**
     * 发送位置消息
     *
     * @param userId
     * @param a
     * @param b
     */
    public static void sendLocationMessage(String userId, String a, String b) {


    }

    /**
     * 根据会话类型的目标 Id，回调方式获取最新的 N 条消息实体。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     */
    public static void getLatestMessages(final Conversation.ConversationType conversationType, final String targetId, final GetLatestMessagesCallBack callBack) {
        int count = 18;
        RongIMClient.getInstance().getLatestMessages(conversationType, targetId, count, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messageList) {
                callBack.getMessagesCallBack(messageList);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtil.i("Error");
            }
        });


    }

    /**
     * 获取会话中对方的名称
     *
     * @param conversation
     * @return
     */
    public static String getConversation(Conversation conversation) {
        if (conversation.getSenderUserId().equals(ShareLockManager.getInstance().getUserName())) {
            return conversation.getTargetId();
        }
        return conversation.getSenderUserId();
    }


    /**
     * 加工预览大图的实体
     *
     * @param messageList
     * @param activity
     * @return
     */
    public static List<ViewImageInfo> getviewimageinfos(List<Message> messageList, Activity activity) {
        List<ViewImageInfo> urls = new ArrayList<>();
        ViewImageInfo info = null;
        for (int i = 0; i < messageList.size(); i++) {

            if (messageList.get(i).getContent() instanceof ImageMessage) {

                Message message = messageList.get(i);
                final ImageMessage content = (ImageMessage) message.getContent();
                info = new ViewImageInfo();
                info.setMsgLocalId(String.valueOf(message.getMessageId()));
                // 设置本地路径
                if (content.getLocalUri() != null) {
                    if (MyString.hasData(content.getLocalUri().toString())) {
                        info.setDownload(true);
                        File file = new File(content.getLocalUri().getPath());
                        info.setPicurl(file.getAbsolutePath());
                    }
                } else {
                    if (content.getRemoteUri() != null) {
                        info.setDownload(false);
                        info.setPicurl(content.getRemoteUri().toString());
                    }
                }
                if (MyString.hasData(content.getThumUri().toString())) {
                    File file = new File(content.getThumUri().getPath());
                    String fileLocalPath = file.getAbsolutePath();
                    info.setThumbnailurl(fileLocalPath);
                }

                boolean isGif = false;
                //判断是否是Gif 图片
                if (isGif) {
                    info.setGif(isGif);
                }
                info.setIndex(i);
                urls.add(info);
            }

        }

        return urls;
    }


    public static void showImageList(final Message message, final Activity context) {
        getLatestMessages(message.getConversationType(), message.getTargetId(), new GetLatestMessagesCallBack() {
            @Override
            public void getMessagesCallBack(List<Message> list) {
                if (list == null || list.isEmpty()) {
                    return;
                }
                int position = 0;
                ArrayList<ViewImageInfo> urls = (ArrayList<ViewImageInfo>) SChattingHelp.getviewimageinfos(list, context);
                list.clear();
                if (urls == null || urls.isEmpty()) {
                    return;
                }
                for (int i = 0; i < urls.size(); i++) {
                    if (urls.get(i) != null && Integer.parseInt(urls.get(i).getMsgLocalId()) == (message.getMessageId())) {
                        position = i;
                        break;
                    }
                }
                ShareLockManager.startChattingImageViewAction(context, position, urls);

            }
        });
    }

    /**
     * 文本消息的复制
     *
     * @param context
     * @param label
     * @param text
     */
    public static void copyText(Context context, String label, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ((android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE))
                    .setPrimaryClip(ClipData.newPlainText(label, text));
            return;
        }

        ((android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setText(text);
    }

    /**
     * 消息转发
     */
    public static void transmitMessage(Context context, Message message) {

        Intent intent = new Intent(context, TranspondMessageActivity.class);
        intent.putExtra("MESSAGE", message);
        context.startActivity(intent);
    }

    /**
     * 消息收藏
     */
    public static void collectMessage() {

    }

    /**
     * 消息撤回
     */
    public static void recallMessage() {

    }

    /**
     * 消息删除
     */
    public static void delectMessage() {

    }

}
