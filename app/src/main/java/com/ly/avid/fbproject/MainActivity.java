package com.ly.avid.fbproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.SendButton;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.hola.account.HolaLogin;
import com.hola.sdk.HolaAnalysis;
import com.sober.utils.AdapterUtil;
import com.sober.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    CallbackManager callbackManager;
    GameRequestDialog gameRequestDialog;
    ShareDialog shareDialog;

    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        Intent it = getIntent();
        if (it.getData() != null){
            Uri uri = it.getData();
            String uriString = "";
            try {
                uriString = URLDecoder.decode(uri.toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Uri uri2 = Uri.parse(uriString);
            Log.e("MainActivity", uriString+"; "+uri2);
            String query = uri2.getQuery();
            String [] str = query.split("[?]");
            Uri uri3 = Uri.parse(uri2.getScheme()+"://"+uri2.getAuthority()+uri2.getPath()+"?"+str[1]);
            String scheme = uri3.getScheme();
            String host = uri3.getHost();
            String id = uri3.getQueryParameter("id");
            String name = uri3.getQueryParameter("name");
            String path = "scheme:"+scheme+", host:"+host+", id:"+id+", name:"+name;
            LogUtil.showMsg(mContext, path);
        }

        HolaAnalysis.init(this, "999999", "999999");

        callbackManager = CallbackManager.Factory.create();
        gameRequestDialog = new GameRequestDialog(this);
        gameRequestDialog.registerCallback(callbackManager, new FacebookCallback<GameRequestDialog.Result>() {
            @Override
            public void onSuccess(GameRequestDialog.Result result) {
                LogUtil.showMsg(mContext, "Facebook game request success, " + result.getRequestId());
            }

            @Override
            public void onCancel() {
                LogUtil.showMsg(mContext, "Facebook game request cancel.");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtil.showMsg(mContext, "Facebook game request error, error info:" + error.getMessage());
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LogUtil.showMsg(mContext, "Facebook login success. Token:" + loginResult.getAccessToken().getToken());

                HolaLogin.facebookLogin(MainActivity.this, "test_facebook", "fb_id", loginResult.getAccessToken().getToken());
                HolaLogin.guestLogin(MainActivity.this, "test_guest");
                HolaLogin.portalLogin(MainActivity.this, "test_portal", "portal_id");
            }

            @Override
            public void onCancel() {
                LogUtil.showMsg(mContext, "Facebook login cancel.");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtil.showMsg(mContext, "Facebook login error. error info:" + error.getMessage());
            }
        });

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                LogUtil.showMsg(mContext, "Facebook share success. " + result.getPostId());
            }

            @Override
            public void onCancel() {
                LogUtil.showMsg(mContext, "Facebook share cancel.");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtil.showMsg(mContext, "Facebook share error. error info:" + error.getMessage());
            }
        });

        bindViews();
    }

    private void bindViews(){

        final LikeView likeView = (LikeView)findViewById(R.id.fb_like_view);
        likeView.setObjectIdAndType("https://www.google.com",
                LikeView.ObjectType.PAGE);

        txt = (TextView) findViewById(R.id.txt);
        findViewById(R.id.get_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.setText(""+likeView.getDisplay().toString());
            }
        });

        //share link
        ShareLinkContent contentLink = new ShareLinkContent.Builder()
                .setContentTitle("sunshine share")
                .setContentUrl(Uri.parse("https://www.google.com"))
                .setContentDescription("This is a share test.\nJust ignore it.")
                .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag("#ConnectTheWorld")
                                    .build())
                .setQuote("Connect on a global scale")
                .build();

        if (ShareDialog.canShow(ShareLinkContent.class)){
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello Facebook")
                    .setContentDescription(
                            "The 'Hello Facebook' sample  showcases simple Facebook integration")
                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .build();
            //shareDialog.show(linkContent);
        }

        //share photo
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.mmmm);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent contentPhoto = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        //开放图谱动态
        SharePhoto photo0 = new SharePhoto.Builder()
                .setBitmap(image)
                .setUserGenerated(false)
                .build();
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "fitness.course")
                .putString("og:title", "Sample Course")
                .putString("og:description", "This is a sample course.")
                .putInt("fitness:duration:value", 100)
                .putString("fitness:duration:units", "s")
                .putInt("fitness:distance:value", 12)
                .putString("fitness:distance:units", "km")
                .putInt("fitness:speed:value", 5)
                .putString("fitness:speed:units", "m/s")
                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("fitness.walks")
                .putObject("fitness:course", object)
                .putPhoto("image", photo0)
                .build();
        ShareOpenGraphContent contentGraph = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("fitness:course")
                .setAction(action)
                .build();

        //
        SharePhoto photo1 = new SharePhoto.Builder()
                .setBitmap(image)
                .setUserGenerated(true)
                .build();
        ShareOpenGraphObject object1 = new ShareOpenGraphObject.Builder()
                .putString("og:type", "books.book")
                .putString("og:title", "A Game of Thrones")
                .putString("og:description", "In the frozen wastes to the north of Winterfell, sinister and supernatural forces are mustering.")
                .putString("books:isbn", "0-553-57340-3")
                .build();
        ShareOpenGraphAction action1 = new ShareOpenGraphAction.Builder()
                .setActionType("books.reads")
                .putObject("book", object1)
                .putPhoto("image", photo1)
                .build();
        ShareOpenGraphContent contentGraph1 = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("book")
                .setAction(action)
                .build();

        final ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share);
        //shareButton.setShareContent(contentLink);
        shareButton.setShareContent(contentPhoto);
        //shareButton.setShareContent(contentGraph);
        //shareButton.setShareContent(contentGraph1);

        //send
        SendButton sendButton = (SendButton) findViewById(R.id.fb_send);
        sendButton.setShareContent(contentPhoto);
        sendButton.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                LogUtil.showMsg(mContext, "Facebook send success. " + result.getPostId());
            }

            @Override
            public void onCancel() {
                LogUtil.showMsg(mContext, "Facebook send cancel.");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtil.showMsg(mContext, "Facebook share error. error info:" + error.getMessage());
            }
        });

        //
        final LinkedList<ItemButton> mData = new LinkedList<>();
        mData.add(new ItemButton("SharePhoto(截图)"));
        mData.add(new ItemButton("GameRequest(Game Invite)"));
        mData.add(new ItemButton("GraphRequest(Invite Friends In App)"));
        mData.add(new ItemButton("AppInvite"));
        mData.add(new ItemButton("发帖"));
        mData.add(new ItemButton("Messager"));

        AdapterUtil<ItemButton> adapterBtn = new AdapterUtil<ItemButton>(mData, R.layout.item_button) {
            @Override
            public void bindView(ViewHolder holder, final ItemButton obj) {
                holder.setText(R.id.btn_item, obj.getText());
                holder.setOnClickListener(R.id.btn_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogUtil.showMsg(mContext, obj.getText());
                        btnCallback(obj.getText());
                    }
                });
            }
        };
        ListView listButton = (ListView) findViewById(R.id.list_button);
        listButton.setAdapter(adapterBtn);
    }

    private void btnCallback(String str){
        switch (str){
            case "SharePhoto(截图)":
                onSharePhoto();
                break;
            case "GameRequest(Game Invite)":
                onClickGameRequest();
                break;
            case "GraphRequest(Invite Friends In App)":
                onClickGraphRequest();
                break;
            case "AppInvite":
                onClickAppInvite();
                break;
            case "发帖":
                onClickFeed();
                break;
            case "Messager":
                onClickMessager2();
                break;
        }
    }

    private void onSharePhoto() {
        //截图
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        Bitmap background = view.getDrawingCache();
        Bitmap front = BitmapFactory.decodeResource(getResources(), R.drawable.front);
        //合成
        Bitmap newbmp = compositeBitmap(background, front, 200, 300);
        //画文字
        Bitmap showBmp = drawTextAtBitmap(newbmp, "By Lean", 400, 450);

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(showBmp)
                .build();
        SharePhotoContent contentPhoto = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(contentPhoto);
    }

    /**
     * 图片上画文字
     *
     * @param bitmapBg  背景
     *  @param bitmapFr  前景
     * @param textX  X坐标
     * @param textY  Y坐标
     * @return Bitmap
     */
    private Bitmap compositeBitmap(Bitmap bitmapBg, Bitmap bitmapFr, float textX, float textY) {
        int bgWidth = bitmapBg.getWidth();
        int bgHeight = bitmapBg.getHeight();
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newbmp);
        canvas.drawBitmap(bitmapBg, 0, 0, null);//在 0，0坐标开始画入bg
        canvas.drawBitmap(bitmapFr, textX, textY, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.restore();//存储
        return newbmp;
    }

    /**
     * 图片上画文字
     *
     * @param bitmap
     * @param text  文字内容
     * @param textX  文字X坐标
     * @param textY  文字Y坐标
     * @return Bitmap
     */
    private Bitmap drawTextAtBitmap(Bitmap bitmap, String text, float textX, float textY) {
        int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        // 创建一个和原图同样大小的位图
        Bitmap newbit = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newbit);
        Paint paint = new Paint();
        // 在原始位置0，0插入原图
        canvas.drawBitmap(bitmap, 0, 0, paint);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20.0f);// 设置画笔粗细
        paint.setTextSize(50f);//设置文字大小
        canvas.drawText(text, textX, textY, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newbit;
    }

    private void onClickGameRequest(){
        GameRequestContent content = new GameRequestContent.Builder()
                .setMessage("Come play with me.")
                .build();
        gameRequestDialog.show(content);
    }

    private void onClickGraphRequest(){
        if (checkPermission("user_friends")){
            Bundle bundle = new Bundle();
            bundle.putString("fields", "id,name,picture");
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", bundle, HttpMethod.GET, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    JSONObject jsonObject = response.getJSONObject();
                    if (jsonObject != null){
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (jsonArray.length() > 0){
                                LinkedList<ItemUser> listUser = new LinkedList<>();
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    listUser.add(new ItemUser(object.getString("id"), object.getString("name"), object.getJSONObject("picture").getJSONObject("data").getString("url")));
                                }
                                AdapterUtil<ItemUser> adapterUtil = new AdapterUtil<ItemUser>(listUser, R.layout.item_user) {
                                    @Override
                                    public void bindView(ViewHolder holder, final ItemUser obj) {
                                        holder.setText(R.id.userid, obj.getId());
                                        holder.setText(R.id.username, obj.getName());
                                        holder.setImageUrl(R.id.picture, obj.getUrl());
                                        holder.setOnClickListener(R.id.btn_call, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                LogUtil.showMsg(mContext, obj.getId() + ";" + obj.getName());
                                                GameRequestContent content = new GameRequestContent.Builder()
                                                        .setMessage("come back")
                                                        .setTo(obj.getId())
                                                        .setActionType(GameRequestContent.ActionType.ASKFOR)
                                                        .setObjectId("1728897944066870")
                                                        .build();
                                                gameRequestDialog.show(content);
                                            }
                                        });
                                    }
                                };
                                ListView userList = (ListView)findViewById(R.id.list_friends);
                                userList.setAdapter(adapterUtil);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).executeAsync();
        }else{
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("user_friends"));
        }
    }

    private void onClickAppInvite() {
        String appLinkUrl = "https://fb.me/268583736923811?id=10010&name=sober";
        String previewImageUrl = "http://p3.qhimg.com/dmfd/400_300_/t018bf836bbffcbb8d8.jpg";

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }
    }

    private void onClickFeed() {
        if (checkPermission("publish_actions")) {
            Bundle params = new Bundle();
            params.putString("message", "This is a test message");
            params.putString("link", "http://p3.qhimg.com/dmfd/400_300_/t018bf836bbffcbb8d8.jpg");
        /* make the API call */
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/feed", params, HttpMethod.POST, new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                        /* handle the result */
                    LogUtil.showMsg(mContext, "onCompleted. " + response.toString());
                }
            }
            ).executeAsync();
        }else {
            LoginManager.getInstance().logInWithPublishPermissions(MainActivity.this, Arrays.asList("publish_actions"));
        }
    }

    private static int REQUEST_CODE_SHARE_TO_MESSENGER = 10001;
    private void onClickMessager() {
        String mimeType = "image/jpeg";

        // contentUri points to the content being shared to Messenger
        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(Uri.fromFile(new File("/sdcard/tttt.jpg")), mimeType)
                        .build();

        // Sharing from an Activity
        MessengerUtils.shareToMessenger(
                this,
                REQUEST_CODE_SHARE_TO_MESSENGER,
                shareToMessengerParams);
    }
    private void onClickMessager2() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.google.com"))
                .setContentTitle("123")
                .setContentDescription("141646").build();
        MessageDialog.show(this, content);
    }

    private boolean checkPermission(String permission) {
        Set<String> permissionsSet = AccessToken.getCurrentAccessToken().getPermissions();
        if (permissionsSet.contains(permission))
            return true;
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SHARE_TO_MESSENGER){
            LogUtil.showMsg(mContext, "Send to message success.");
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
