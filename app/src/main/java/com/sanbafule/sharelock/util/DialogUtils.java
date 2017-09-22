package com.sanbafule.sharelock.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.ImageBiz;
import com.sanbafule.sharelock.chatting.modle.FileMessageType;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.modle.ContactInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.utils.BitmapUtil;


/**
 * Created by Administrator on 2016/11/18.
 */

public final class DialogUtils {

    /**
     * 普通
     */
    public static void showBasic(Activity activity, int content, int positive, int negative, boolean cancelable, final DialogInterface dialogInterface) {
        new MaterialDialog.Builder(activity)
                .content(content)
                .positiveText(positive)
                .positiveColor(Color.parseColor("#f03791"))
                .negativeText(R.string.negative)
                .cancelable(false)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            dialogInterface.onPositiveClickListener(dialog);
                        } else if (which == DialogAction.NEGATIVE) {
                            dialogInterface.onNegativeClickListener(dialog);
                        }

                    }
                })
                .show();
    }


    /**
     * 带标题
     */
    public static void showBasicWithTile(Activity activity, int title, int content, int positive, int negative, boolean cancelable, final DialogInterface dialogInterface) {
        new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .positiveText(positive)
                .positiveColor(Color.parseColor("#f03791"))
                .negativeText(R.string.negative)
                .cancelable(false)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            dialogInterface.onPositiveClickListener(dialog);
                        } else if (which == DialogAction.NEGATIVE) {
                            dialogInterface.onNegativeClickListener(dialog);
                        }

                    }
                })
                .show();
    }


    /**
     * 带标题
     */
    public static void showBasicWithTile(Activity activity, String title, String content, int positive, int negative, boolean cancelable, final DialogInterface dialogInterface) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .positiveText(positive)
                .positiveColor(Color.parseColor("#f03791"))
                .cancelable(cancelable)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (dialogInterface != null) {
                            if (which == DialogAction.POSITIVE) {
                                dialogInterface.onPositiveClickListener(dialog);
                            } else if (which == DialogAction.NEGATIVE) {
                                dialogInterface.onNegativeClickListener(dialog);
                            }
                        }

                    }
                });
        if (negative > 0) {
            builder.negativeText(R.string.negative);
        }

        builder.show();
    }


    /**
     * 带图片
     */
    public static void showBasicWithIcon(Activity activity, int icon, int title, int content, boolean cancelable, final DialogInterface dialogInterface) {
        new MaterialDialog.Builder(activity)
                .iconRes(icon)
                .limitIconToDefaultSize()
                .title(title)
                .content(content)
                .positiveText(R.string.positive)
                .positiveColor(Color.parseColor("#f03791"))
                .negativeText(R.string.negative)
                .cancelable(false)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            dialogInterface.onPositiveClickListener(dialog);
                        } else if (which == DialogAction.NEGATIVE) {
                            dialogInterface.onNegativeClickListener(dialog);
                        }

                    }
                })
                .show();
    }

    /**
     * 带图片
     */
    public static void showBasicWithIcon(Activity activity, int icon, String title, String content, boolean cancelable, final DialogInterface dialogInterface) {
        new MaterialDialog.Builder(activity)
                .iconRes(icon)
                .limitIconToDefaultSize()
                .title(title)
                .content(content)
                .positiveText(R.string.positive)
                .positiveColor(Color.parseColor("#f03791"))
                .negativeText(R.string.negative)
                .cancelable(false)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            dialogInterface.onPositiveClickListener(dialog);
                        } else if (which == DialogAction.NEGATIVE) {
                            dialogInterface.onNegativeClickListener(dialog);
                        }

                    }
                })
                .show();
    }


    public static MaterialDialog showIndeterminateProgressDialog(Activity activity, boolean horizontal) {
        return new MaterialDialog.Builder(activity)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(horizontal)
                .show();
    }

    public interface DialogInterface {

        void onPositiveClickListener(@NonNull MaterialDialog dialog);

        void onNegativeClickListener(@NonNull MaterialDialog dialog);
    }


    /**
     * 显示单个转发的对话框
     *
     * @param activity
     * @param info
     * @param list
     * @param dialogInterface
     */
    public static void showCustomDialog(Activity activity, ContactInfo info, ArrayList<Message> list, final DialogInterface dialogInterface) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_transpond_list_message, null);
        TextView name = (TextView) view.findViewById(R.id.name);
        ImageView header = (ImageView) view.findViewById(R.id.image);
        // 图片内容
        ImageView imageContent = (ImageView) view.findViewById(R.id.imagecontent);
        // 文字内容
        TextView textContent = (TextView) view.findViewById(R.id.textcontent);
        String text = "[逐条转发]共%s条消息";
        name.setText(info.getName());
        ImageBiz.showImage(activity, header, info.getU_header());
        if (list.size() < 2) {
            MessageContent content = list.get(0).getContent();
            if (content instanceof ImageMessage) {
                imageContent.setVisibility(View.VISIBLE);
                textContent.setVisibility(View.GONE);
                ImageBiz.showImage(activity, imageContent, ((ImageMessage) content).getThumUri());
            }

        } else {
            imageContent.setVisibility(View.GONE);
            textContent.setVisibility(View.VISIBLE);
            textContent.setText(String.format(text, list.size()));

        }
        new MaterialDialog.Builder(activity).customView(view, true)
                .positiveText(R.string.positive)
                .negativeText(R.string.negative)
                .positiveColor(Color.parseColor("#f03791"))
                .cancelable(true)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            dialogInterface.onPositiveClickListener(dialog);

                        } else if (which == DialogAction.NEGATIVE) {
                            dialogInterface.onNegativeClickListener(dialog);
                        }

                    }
                })
                .show();
    }

    /**
     * 显示批量转发的消息的对话框
     *
     * @param activity
     * @param nameList
     * @param messageList
     * @param dialogInterface
     */
    public static void showCustomDialog(final Activity activity, List<String> nameList, ArrayList<Message> messageList, final DialogInterface dialogInterface) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_transpond_message_list_group, null);
        // 头像的列表
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.image);
        // 图片内容
        ImageView imageContent = (ImageView) view.findViewById(R.id.imagecontent);
        // 文字内容
        TextView textContent = (TextView) view.findViewById(R.id.textcontent);

        String text = "[逐条转发]共%s条消息";
        for (int j = 0; j < nameList.size(); j++) {
            final ImageView imageView = new ImageView(activity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
            if (j > 0) {
                layoutParams.leftMargin = 10;
            }
            imageView.setLayoutParams(layoutParams);
            ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(nameList.get(j));
            if (info != null) {
                ImageBiz.showImage(activity, imageView, info.getU_header());
            } else {
                ContactInfoBiz.getContactInfo(nameList.get(j), new GetContactInfoListener() {
                    @Override
                    public void getContactInfo(ContactInfo contactInfo) {
                        ImageBiz.showImage(activity, imageView, contactInfo.getU_header());
                    }
                });
            }

            layout.addView(imageView);
        }
        if (messageList.size() < 2) {
            MessageContent content = messageList.get(0).getContent();
            if (content instanceof ImageMessage) {
                imageContent.setVisibility(View.VISIBLE);
                textContent.setVisibility(View.GONE);
                ImageBiz.showImage(activity, imageContent, ((ImageMessage) content).getThumUri());
            }
        } else {
            imageContent.setVisibility(View.GONE);
            textContent.setVisibility(View.VISIBLE);
            textContent.setText(String.format(text, messageList.size()));
        }
        new MaterialDialog.Builder(activity).customView(view, true)
                .positiveText(R.string.positive)
                .negativeText(R.string.negative)
                .positiveColor(Color.parseColor("#f03791"))
                .cancelable(true)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            dialogInterface.onPositiveClickListener(dialog);

                        } else if (which == DialogAction.NEGATIVE) {
                            dialogInterface.onNegativeClickListener(dialog);
                        }

                    }
                })
                .show();
    }

    /**
     * 展示二维码的Dialog
     *
     * @param activity
     * @param info
     */
    public static void showContactQRCodeDialog(Activity activity, ContactInfo info) {

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_contact_qrcode, null);
        ImageView header = (ImageView) view.findViewById(R.id.head_img);
        TextView name = (TextView) view.findViewById(R.id.tv_nickname);
        TextView address = (TextView) view.findViewById(R.id.tv_address);
        ImageView gender = (ImageView) view.findViewById(R.id.sex);
        ImageView img_erweima = (ImageView) view.findViewById(R.id.img_erweima);
        TextView isShow = (TextView) view.findViewById(R.id.isShow);

        name.setText(info.getName());
        ImageBiz.showImage(activity, header, ShareLockManager.getImgUrl(info.getU_header()), R.drawable.icon_touxiang_persion_gray);
        ImageBiz.showImage(activity, img_erweima, ShareLockManager.getImgUrl(info.getU_qr_code()), R.drawable.ic_launcher);
        switch (info.getU_sex()) {
            case 0:
                gender.setImageResource(R.drawable.icon_add);
                break;
            case 1:
                gender.setImageResource(R.drawable.icon_camera);
                break;
            case 2:
                gender.setImageResource(R.drawable.icon_audio);
                break;

        }
        new MaterialDialog.Builder(activity).customView(view, true)
                .cancelable(true)
                .show();
    }


    /**
     * 转发文本消息
     *
     * @param activity
     */
    public static void showTranspondMessageDialog(Activity activity, Message message, ContactInfo info, final DialogInterface dialogInterface) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_transpond_message, null);
//        // 头像
        ImageView iv_head = (ImageView) view.findViewById(R.id.iv_head);
//        // 姓名
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
//        // 文字内容
        TextView textContent = (TextView) view.findViewById(R.id.text_content);
        // 图片内容
        ImageView imgContent = (ImageView) view.findViewById(R.id.img_content);
        ImageBiz.showImage(activity, iv_head, ShareLockManager.getImgUrl(info.getU_header()));
        tv_name.setText(info.getName());

        MessageContent content = message.getContent();
        if (content instanceof TextMessage) {
            imgContent.setVisibility(View.GONE);
            textContent.setVisibility(View.VISIBLE);
            SimpleCommonUtils.spannableEmoticonFilter(textContent, ((TextMessage) content).getContent());
        } else if (content instanceof ImageMessage) {
            imgContent.setVisibility(View.VISIBLE);
            textContent.setVisibility(View.GONE);
            ImageBiz.showImage(activity, imgContent, ((ImageMessage) content).getThumUri());
        } else if (content instanceof FileMessage) {
            // 显示gif图片
            imgContent.setVisibility(View.VISIBLE);
            textContent.setVisibility(View.GONE);
            if (((FileMessage) content).getType().equals(FileMessageType.GIF.toString())) {
                int i = DensityUtil.dip2px(activity, 75);
                File localFile = new File(FileUtil.getPhotoPathFromContentUri(activity, ((FileMessage) content).getLocalPath()));
                if (localFile.exists()) {
                    Glide.with(activity).load(((FileMessage) content).getFileUrl()).asGif().
                            error(R.drawable.ic_play_gif).
                            diskCacheStrategy(DiskCacheStrategy.SOURCE).
                            override(i, i).
                            into(imgContent);
                } else {
                    Glide.with(activity).load(((FileMessage) content).getMediaUrl()).asGif().
                            error(R.drawable.ic_play_gif).
                            diskCacheStrategy(DiskCacheStrategy.SOURCE).
                            override(i, i).
                            into(imgContent);
                }
            } else if (((FileMessage) content).getType().equals(FileMessageType.VIDEO.toString())) {
                // 显示小视频的缩略图
                String extra = ((FileMessage) content).getExtra();
                Bitmap bitmapFromBase64 = BitmapUtil.getBitmapFromBase64(extra);
                bitmapFromBase64 = ThumbnailUtils.extractThumbnail(bitmapFromBase64, DensityUtil.dip2px(45), DensityUtil.dip2px(75), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                imgContent.setImageBitmap(bitmapFromBase64);

            } else {

            }

        }
        new MaterialDialog.Builder(activity).customView(view, true)
                .positiveText(R.string.positive)
                .negativeText(R.string.negative)
                .positiveColor(Color.parseColor("#f03791"))
                .cancelable(true)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            dialogInterface.onPositiveClickListener(dialog);

                        } else if (which == DialogAction.NEGATIVE) {
                            dialogInterface.onNegativeClickListener(dialog);
                        }

                    }
                })
                .show();
    }

    /**
     * 群发消息
     *
     * @param activity
     */
    public static void showGroupTranspondMessageDialgo(final Activity activity, Message message, List<String> nameList, final DialogInterface dialogInterface) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_transpond_message_group, null);
//        // 头像列表
        LinearLayout iv_head = (LinearLayout) view.findViewById(R.id.image);
//        // 文字内容
        TextView textContent = (TextView) view.findViewById(R.id.textcontent);
        // 图片内容
        ImageView imgContent = (ImageView) view.findViewById(R.id.imagecontent);
        int size = 0;
        if (nameList.size() > 3) {
            size = 3;
        } else {
            size = nameList.size();
        }
        for (int j = 0; j < size; j++) {
            final ImageView imageView = new ImageView(activity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
            if (j > 0) {
                layoutParams.leftMargin = 10;
            }
            imageView.setLayoutParams(layoutParams);
            ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(nameList.get(j));
            if (info != null) {
                ImageBiz.showImage(activity, imageView, info.getU_header(), R.drawable.icon_touxiang_persion_gray);
            } else {
                ContactInfoBiz.getContactInfo(nameList.get(j), new GetContactInfoListener() {
                    @Override
                    public void getContactInfo(ContactInfo contactInfo) {
                        ImageBiz.showImage(activity, imageView, contactInfo.getU_header(), R.drawable.icon_touxiang_persion_gray);
                    }
                });
            }
            iv_head.addView(imageView);
        }


        MessageContent content = message.getContent();
        if (content instanceof TextMessage) {
            imgContent.setVisibility(View.GONE);
            textContent.setVisibility(View.VISIBLE);
            SimpleCommonUtils.spannableEmoticonFilter(textContent, ((TextMessage) content).getContent());
        } else if (content instanceof ImageMessage) {
            imgContent.setVisibility(View.VISIBLE);
            textContent.setVisibility(View.GONE);
            ImageBiz.showImage(activity, imgContent, ((ImageMessage) content).getThumUri());
        }
        new MaterialDialog.Builder(activity).customView(view, true)
                .positiveText(R.string.positive)
                .negativeText(R.string.negative)
                .positiveColor(Color.parseColor("#f03791"))
                .cancelable(true)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (dialogInterface != null) {
                        if (which == DialogAction.POSITIVE) {

                            dialogInterface.onPositiveClickListener(dialog);
                        } else if (which == DialogAction.NEGATIVE) {
                            dialogInterface.onNegativeClickListener(dialog);
                        }
                        }
                    }
                })
                .show();
    }
}
