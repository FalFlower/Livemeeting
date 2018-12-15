package cn.edu.sdnu.i.livemeeting.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMMessageReceipt;
import com.tencent.TIMMessageReceiptListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.imcore.IFileTrans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.adapter.MsgAdapter;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.info.Msg;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;
import cn.edu.sdnu.i.livemeeting.util.KeyboardUtils;
import cn.edu.sdnu.i.livemeeting.util.RealPathFromUriUtils;
import cn.edu.sdnu.i.livemeeting.util.SharedPreferencesUtil;

import static cn.edu.sdnu.i.livemeeting.info.Msg.TYPE_RECEIVED;
import static cn.edu.sdnu.i.livemeeting.info.Msg.TYPE_SENT;
import static com.tencent.TIMImageType.Large;
import static com.tencent.TIMImageType.Thumb;

public class SingleChatActivity extends AppCompatActivity {
    public static SingleChatActivity instance = null;
    private List<Msg> mMsgList=new ArrayList<>();
    private static final int FROM_CAMERA = 2;
    private static final int FROM_ALBUM = 1;

    private TextView mChatName;
    private TextView mBacText;
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView=null;
//    private LinearLayout linearLayout;
    private ImageView picture;
    private ImageView camera;
    private ImageView bac;

    private MsgAdapter adapter;
    private String myId;
    private String otherId;
    private TIMConversation conversation;
    private TIMMessageListener timMessageListener;

    private Uri mCameraFileUri;
    private TIMUserProfile mUserProfile;
    private boolean isShow=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_single_chat);
        instance=this;
        findViews();
        updateView();
        setClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==FROM_ALBUM){
            //从相册返回
            if (resultCode == Activity.RESULT_OK){
                final Uri uri=data.getData();
                doSendPic(uri,FROM_ALBUM);
            }
        }else if (requestCode == FROM_CAMERA) {
            //从相机选择返回。
            if (resultCode == Activity.RESULT_OK) {
                doSendPic(mCameraFileUri,FROM_CAMERA);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void setClick() {
        mBacText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 相册
                if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getParent(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    takePicFromAlbum();
                    isShow=false;
                    msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 拍照
                takePicFromCamera();
                isShow=false;
                msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputText.getText().toString().isEmpty()){
                    Toast.makeText(SingleChatActivity.this, "请输入所需发送的消息", Toast.LENGTH_SHORT).show();
                }else {
                    //对话发出
                    TIMMessage message=new TIMMessage();//构造一条消息
                    TIMTextElem timTextElem=new TIMTextElem();//添加文本内容
                    timTextElem.setText(inputText.getText().toString());
                    if (message.addElement(timTextElem)!=0){
                        Log.e("添加会话消息", "addElement failed");
                    }
                    //发送文本消息
                    conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {//发送消息回调
                        @Override
                        public void onError(int code, String desc) {//发送消息失败
                            Log.e("对话发送失败：",code+" "+desc);
                           //Toast.makeText(SingleChatActivity.this, "对话发送失败 "+desc, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(TIMMessage msg) {//发送消息成功
//                          Log.e("会话发送", "SendMsg ok");
                            //界面发出
                            Msg mesg=new Msg(inputText.getText().toString(), TYPE_SENT,myId);
                            mMsgList.add(mesg);
                            inputText.setText("");//清空输入框中的内容
                            adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
                            msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
                        }
                    });
                }
                KeyboardUtils.hideKeyboard(instance);
            }
        });

    }


    private void takePicFromAlbum() {
        //相册
        Intent picIntent = new Intent("android.intent.action.GET_CONTENT");
        picIntent.setType("image/*");
        startActivityForResult(picIntent,FROM_ALBUM);

    }

    private void takePicFromCamera() {
        //相机
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},222);
                return;
            }
        }

        mCameraFileUri = createAlbumUri();
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion < 24) {
            //小于7.0的版本
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri);
            startActivityForResult(intentCamera, FROM_CAMERA);

        } else {
            //大于7.0的版本
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mCameraFileUri.getPath());
            Uri uri = getImageContentUri(mCameraFileUri);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intentCamera, FROM_CAMERA);
        }
    }
    /**
     * 转换 content:// uri
     */
    public Uri getImageContentUri(Uri uri) {
        String filePath = uri.getPath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    private Uri createAlbumUri() {
        String dirPath = Environment.getExternalStorageDirectory() + "/" + getApplication().getApplicationInfo().packageName;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String id = "";
        if (mUserProfile != null) {
            id = mUserProfile.getIdentifier();
        }
        String fileName = id + ".jpg";
        File picFile = new File(dirPath, fileName);
        if (picFile.exists()) {
            picFile.delete();
        }
        return Uri.fromFile(picFile);
    }
    private void updateView() {
        //获取本地对话
        mMsgList.clear();
        conversation.getMessage(20, //获取此会话最近的20条消息
                null, //不指定从哪条消息开始获取 - 等同于从最新的消息开始往前
                new TIMValueCallBack<List<TIMMessage>>() {//回调接口
                    @Override
                    public void onError(int code, String desc) {//获取消息失败
                        Log.e("获取消息失败 错误码：",""+code);
                       // Toast.makeText(SingleChatActivity.this, "获取消息失败 错误码："+code, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(List<TIMMessage> msgs) {//获取消息成功
                        //遍历取得的消息
                        for (int j = msgs.size() - 1; j >= 0; j--) {
                            for (int i=0;i<msgs.get(j).getElementCount();i++){
//                        Log.e("发送者测试",timMessage.getSender()+timMessage.getSenderProfile().getNickName());
                                TIMConversation timConversation=TIMManager.getInstance().getConversation(TIMConversationType.C2C,otherId);
                                TIMElem elem=msgs.get(j).getElement(i);
                                //获取当前元素的类型
                                TIMElemType elemType = elem.getType();
                                if (!msgs.get(j).isSelf()) {
                                    if (elemType == TIMElemType.Text) {
                                        //TODO 处理文本消息
                                        TIMTextElem timTextElem=(TIMTextElem)elem;
                                        String mesContent=timTextElem.getText();
                                        loadText(mesContent,TYPE_RECEIVED);
                                    }else if (elem.getType() == TIMElemType.Image){
                                        //TODO 处理图片消息
                                        //图片元素
                                        loadPic(elem,TYPE_RECEIVED);
                                    }
                                }else {
                                    if (elemType == TIMElemType.Text) {
                                        //TODO 处理文本消息
                                        TIMTextElem timTextElem=(TIMTextElem)elem;
                                        String mesContent=timTextElem.getText();
                                        loadText(mesContent,TYPE_SENT);
                                    }else if (elem.getType() == TIMElemType.Image){
                                        //TODO 处理图片消息
                                        //图片元素
                                        loadPic(elem,TYPE_SENT);
                                    }
                                }
                                //消息回执
                                TIMManager.getInstance().enableReadReceipt();
                            }
                        }
                    }
                });


//        //回执监听器
//        TIMManager.getInstance().setMessageReceiptListener(new TIMMessageReceiptListener() {
//            @Override
//            public void onRecvReceipt(List<TIMMessageReceipt> list) {
//                //TODO 对方已读消息
//                for (TIMMessageReceipt timMessageReceipt : list) {
////                    timMessageReceipt.
////                    timMessageReceipt.getConversation()
//
//                }
//            }
//        });

//        //是否已读消息
//        TIMMessage message=new TIMMessage();
//        message.isPeerReaded();

        List<String> users=new ArrayList<>();
        users.add(otherId);
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                for (TIMUserProfile timUserProfile : timUserProfiles) {
                    if (!timUserProfile.getRemark().isEmpty())
                        mChatName.setText(timUserProfile.getRemark());
                    else if (!timUserProfile.getNickName().isEmpty())
                        mChatName.setText(timUserProfile.getNickName());
                    else
                        mChatName.setText("好友");
                }
            }
        });

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        adapter=new MsgAdapter(getApplicationContext(),mMsgList);
        msgRecyclerView.setAdapter(adapter);
    }

    private void findViews() {
        inputText=(EditText)findViewById(R.id.input_msg);
        send=(Button)findViewById(R.id.send_msg);
        msgRecyclerView=findViewById(R.id.msg_recycler_view);
        mChatName=findViewById(R.id.chat_name);
        picture=findViewById(R.id.single_pic);
        camera=findViewById(R.id.single_camera);
        bac=findViewById(R.id.single_bac);
        mUserProfile = LiveApplication.getApplication().getSelfProfile();
        mBacText=findViewById(R.id.single_bac_text);


        msgRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case 0:
                        startGlide(true);
                        break;
                    case 1:
                        startGlide(true);
                        break;
                    case 2:
                        startGlide(false);
                        break;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });



        Intent intent=getIntent();
        myId=new SharedPreferencesUtil(this).doGetString("MyId","iiiiiiii");
        otherId=intent.getStringExtra("chat_id");
        //获取对话
        conversation= TIMManager.getInstance().getConversation(TIMConversationType.C2C,otherId);
        //消息监听
        TIMManager.getInstance().addMessageListener(timMessageListener=new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                Log.e("this is ","chat");
                for (TIMMessage timMessage : list) {
                    for (int i=0;i<timMessage.getElementCount();i++){
//                        Log.e("发送者测试",timMessage.getSender()+timMessage.getSenderProfile().getNickName());
                        if (timMessage.getSenderProfile().getIdentifier().equals(otherId)){
                            TIMElem elem=timMessage.getElement(i);
                            //获取当前元素的类型
                            TIMElemType elemType = elem.getType();
                            if (!timMessage.isSelf())
                            {
                                if (elemType == TIMElemType.Text) {
                                    //TODO 处理文本消息
                                    TIMTextElem timTextElem=(TIMTextElem)elem;
                                    String mesContent=timTextElem.getText();
                                    loadText(mesContent,TYPE_RECEIVED);
                                }
                                else if (elemType == TIMElemType.Image){
                                    //TODO 处理图片消息
                                    //图片元素
                                    loadPic(elem,TYPE_RECEIVED);
                                }
                            }
                            //消息回执
                            TIMManager.getInstance().enableReadReceipt();
                        }
                    }
                }
                return false;
            }
        });
    }

    //我们在Recyclerview中的滑动事件中进行监听,当暂停和拖拽滑动时开启Glide加载图片,当滑动后自然沉降状态时停止Glide,其中startGlide()中的代码是:


    private boolean mAlreadyStart = true;//是否已经开启Glide加载图片
    private void startGlide(boolean start) {
        if (mAlreadyStart) {
            if (start) {
                return;
            }
            mAlreadyStart = false;
            Glide.with(getApplicationContext()).pauseRequests();
        } else {
            if (!start) {
                return;
            }
            mAlreadyStart = true;
            Glide.with(getApplicationContext()).resumeRequests();
        }
    }
    private void doSendPic(final Uri uri,int key) {
        String path = null;
        if (key==FROM_CAMERA){
            path=uri.getPath();
        }else if (key==FROM_ALBUM){
            path=RealPathFromUriUtils.getRealPathFromUri(this, uri);//真实路径
        }
        //TODO 发送图片消息
        TIMMessage msg = new TIMMessage();//构造一条消息
        TIMImageElem elem = new TIMImageElem();//添加图片
        elem.setPath(path);
        if(msg.addElement(elem) != 0) {//将elem添加到消息
            Log.d("", "addElement failed");
            return;
        }
        //发送消息
        final String finalPath = path;
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                Log.e("发送图片错误：",code+" "+desc);
                //Toast.makeText(SingleChatActivity.this, "发送图片错误 "+desc, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                for (int i = 0; i < msg.getElementCount(); i++) {
                    TIMElem timElem=msg.getElement(i);
                    if (timElem.getType()== TIMElemType.Image) {
                        //图片元素
                        TIMImageElem e = (TIMImageElem) timElem;
                        for(TIMImage image : e.getImageList()) {
                            if (image.getType().equals(Thumb)){
                                final Uri uri=createGetPicUri(image.getUuid());
                                image.getImage(uri.getPath(), new TIMCallBack() {
                                    @Override
                                    public void onError(int i, String s) {
                                        Log.e("获取发送图片失败：",i+" "+s);
//                                        Toast.makeText(SingleChatActivity.this, "获取发送图片失败 "+s, Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onSuccess() {
                                        Msg msg=new Msg(TYPE_SENT,myId,finalPath);
                                        mMsgList.add(msg);
                                        adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
                                        msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
                                    }
                                });
                            }
                        }
                    }
                }
//                Msg mesg=new Msg(finalPath,Msg.TYPE_SENT,myId);
//                mMsgList.add(mesg);
//                adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
//                msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
            }
        });
    }

    private void loadPic(TIMElem elem, final int typeReceived) {
        TIMImageElem e = (TIMImageElem) elem;
        for(final TIMImage image : e.getImageList()) {
            //获取图片类型, 大小, 宽高
            Log.d("图片信息"+image.getUuid(), "image type: " + image.getType() +
                    " image size " + image.getSize() +
                    " image height " + image.getHeight() +
                    " image width " + image.getWidth());
            if (image.getType().equals(Thumb)){
                final Uri uri=createGetPicUri(image.getUuid()+Thumb);
                final String path=new SharedPreferencesUtil(getApplication()).doGetString(image.getUuid()+Thumb,"");
                if (path.equals("")){
                    Log.e("网络加载图片 id",image.getUuid()+Thumb);
                    image.getImage(uri.getPath(), new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e("获取接受图片失败：",i+" "+s);
                            //Toast.makeText(SingleChatActivity.this, "获取接受图片失败 "+s, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess() {
                            new SharedPreferencesUtil(getApplicationContext()).doPutString(image.getUuid()+Thumb,uri.getPath());
                            Msg msg=new Msg(typeReceived,otherId,uri.getPath());
                            mMsgList.add(msg);
                            adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
                            msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
                        }
                    });
                }else {
                    Log.e("本地加载图片 path",path);
                    Msg msg=new Msg(typeReceived,otherId,path);
                    mMsgList.add(msg);
                    adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
                    msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
                }

            }
        }
    }
    private void loadText(String mesContent,int status) {
        Msg msg=new Msg(mesContent, status,otherId);
        mMsgList.add(msg);
        inputText.setText("");//清空输入框中的内容
        adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
        msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行

    }

    private Uri createGetPicUri(String uuid) {
        String dirPath = Environment.getExternalStorageDirectory() + "/" + getApplication().getApplicationInfo().packageName+uuid;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = otherId + ".jpg";
        File picFile = new File(dirPath, fileName);
        if (picFile.exists()) {
            picFile.delete();
        }
        return Uri.fromFile(picFile);
    }
}
