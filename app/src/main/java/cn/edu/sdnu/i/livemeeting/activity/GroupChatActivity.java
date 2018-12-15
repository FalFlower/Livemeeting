package cn.edu.sdnu.i.livemeeting.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
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
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupSelfInfo;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.io.File;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.bmob.Message;
import cn.edu.sdnu.i.livemeeting.activity.bmob.Vote;
import cn.edu.sdnu.i.livemeeting.adapter.MsgAdapter;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.info.Msg;
import cn.edu.sdnu.i.livemeeting.util.RealPathFromUriUtils;
import cn.edu.sdnu.i.livemeeting.util.SharedPreferencesUtil;

import static cn.edu.sdnu.i.livemeeting.info.Msg.TYPE_RECEIVED;
import static cn.edu.sdnu.i.livemeeting.info.Msg.TYPE_SENT;
import static com.tencent.TIMImageType.Thumb;

public class GroupChatActivity extends AppCompatActivity {
    public static GroupChatActivity instance = null;
    //群聊中显示的消息线性表
    private List<Msg> mMsgList=new ArrayList<>();
    private static final int FROM_CAMERA = 2;
    private static final int FROM_ALBUM = 1;

    private TextView mChatName;
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView=null;
    private ImageView picture;
    private ImageView camera;
    private ImageView bac;
    private ImageView vote;
    private ImageView groupInfo;
    private LinearLayout voteLayout;

    private TIMConversation conversation;
    private TIMMessageListener timMessageListener;

    private MsgAdapter adapter;
    private String groupId;
    private Uri mCameraFileUri;
    private boolean isShow=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_group_chat);
        initView();
        updateView();
        setClick();
    }

    private void updateView() {
        //获取本地对话
        mMsgList.clear();
        conversation.getMessage(20, //获取此会话最近的20条消息
                null, //不指定从哪条消息开始获取 - 等同于从最新的消息开始往前
                new TIMValueCallBack<List<TIMMessage>>() {//回调接口
                    @Override
                    public void onError(int code, String desc) {//获取消息失败
                        Toast.makeText(GroupChatActivity.this, "获取消息失败 错误码："+code, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(List<TIMMessage> msgs) {//获取消息成功
                        //遍历取得的消息
                        for (int j = msgs.size() - 1; j >= 0; j--) {
                            for (int i=0;i<msgs.get(j).getElementCount();i++){
                                TIMElem elem=msgs.get(j).getElement(i);
                                //获取当前元素的类型
                                TIMElemType elemType = elem.getType();
                                if (!msgs.get(j).isSelf()) {
                                    if (elemType == TIMElemType.Text) {
                                        //TODO 处理文本消息
                                        TIMTextElem timTextElem=(TIMTextElem)elem;
                                        String mesContent=timTextElem.getText();
                                        String []mg=mesContent.split(",");
                                        if (mg.length==4){
                                            Log.e("哈哈哈哈",mesContent);
                                            if (mg[0].equals("vote_key")){
                                                updateVote(mg[1],mg[2],mg[3],TYPE_RECEIVED);
                                            }
                                        }else
                                            loadText(mesContent,msgs.get(i).getSender(),TYPE_RECEIVED);
                                    }else if (elem.getType() == TIMElemType.Image){
                                        //TODO 处理图片消息
                                        //图片元素
                                        loadPic(elem,TYPE_RECEIVED,msgs.get(j).getSender());
                                    }
                                }else {
                                    if (elemType == TIMElemType.Text) {
                                        //TODO 处理文本消息
                                        TIMTextElem timTextElem=(TIMTextElem)elem;
                                        String mesContent=timTextElem.getText();
                                        String []mg=mesContent.split(",");
                                        if (mg.length==4){
                                            Log.e("哈哈哈哈",mesContent);
                                            if (mg[0].equals("vote_key")){
                                                updateVote(mg[1],mg[2],mg[3],TYPE_SENT);
                                            }
                                        }else
                                            loadText(mesContent,msgs.get(i).getSender(),TYPE_SENT);
                                    }else if (elem.getType() == TIMElemType.Image){
                                        //TODO 处理图片消息
                                        //图片元素
                                        loadPic(elem,TYPE_SENT,msgs.get(j).getSender());
                                    }
                                }
                                //消息回执
                                TIMManager.getInstance().enableReadReceipt();
                            }
                        }
                    }
                });

        //创建待获取信息的群组Id列表
        List<String> groupList = new ArrayList<>();
        groupList.add(groupId);
        //获取群组详细信息
        TIMGroupManager.getInstance().getGroupDetailInfo(
                groupList, //需要获取信息的群组Id列表
                new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
                    @Override
                    public void onError(int i, String s) {
                    }
                    @Override
                    public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                        for(TIMGroupDetailInfo info : timGroupDetailInfos) {
                            mChatName.setText(info.getGroupName());
                        }
                    }
                });

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        adapter=new MsgAdapter(getApplicationContext(),mMsgList);
        msgRecyclerView.setAdapter(adapter);
    }

    private void setClick() {


        groupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 跳转到群组资料
                Intent intent=new Intent(getApplicationContext(),GroupInfoActivity.class);
                intent.putExtra("group_info_id",groupId);
                startActivity(intent);
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 发送消息
                if (inputText.getText().toString().isEmpty()){
                    Toast.makeText(GroupChatActivity.this, "请输入所需发送的消息", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(GroupChatActivity.this, "对话发送失败,错误码为："+code, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(TIMMessage msg) {//发送消息成功
                            //发送弹幕
                            sendBarrage(inputText.getText().toString());
                            Msg mesg=new Msg(inputText.getText().toString(), TYPE_SENT, LiveApplication.getApplication().getSelfProfile().getIdentifier());
                            mMsgList.add(mesg);
                            inputText.setText("");//清空输入框中的内容
                            adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
                            msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
                        }
                    });
                }
            }
        });


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 发送图片
                takePicFromAlbum();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 拍照分享
                takePicFromCamera();
            }
        });
        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 投票
                Intent intent=new Intent(GroupChatActivity.this,VoteActivity.class);
                startActivityForResult(intent,0x03);
            }
        });
        bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendBarrage(String s) {
        final Message message=new Message();
//        message.setFrom_who(Bmob.getInstance().initConfig(););
        message.setMessage(s);
        message.save(getApplicationContext(),new SaveListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
    }

    private void initView() {
        instance=this;
        bac=findViewById(R.id.group_bac);
       mChatName=findViewById(R.id.group_chat_name);
       inputText=findViewById(R.id.group_input_msg);
       send=findViewById(R.id.group_send_msg);
       msgRecyclerView=findViewById(R.id.group_recycler_view);
       picture=findViewById(R.id.group_pic);
       camera=findViewById(R.id.group_camera);
       vote=findViewById(R.id.group_vote);
       voteLayout=findViewById(R.id.group_layout_vote);
       groupInfo=findViewById(R.id.group_info);

        Intent intent=getIntent();
        groupId=intent.getStringExtra("join_ok_id");


        TIMGroupManager.getInstance().getSelfInfo(groupId, new TIMValueCallBack<TIMGroupSelfInfo>() {
            @Override
            public void onError(int i, String s) { }
            @Override
            public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                if (!(timGroupSelfInfo.getRole()== TIMGroupMemberRoleType.Owner)){
                    voteLayout.setVisibility(View.GONE);
                }
            }
        });

        //获取对话
        conversation= TIMManager.getInstance().getConversation(TIMConversationType.Group,groupId);


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


        //消息监听
        TIMManager.getInstance().addMessageListener(timMessageListener=new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                Log.e("this is ","chat");
                for (TIMMessage timMessage : list) {
                    for (int i=0;i<timMessage.getElementCount();i++){
//                        Log.e("发送者测试",timMessage.getSender()+timMessage.getSenderProfile().getNickName());
                        if (!timMessage.getSenderProfile().getIdentifier().equals(LiveApplication.getApplication().getSelfProfile().getIdentifier())){
                            TIMElem elem=timMessage.getElement(i);
                            //获取当前元素的类型
                            TIMElemType elemType = elem.getType();
                            if (!timMessage.isSelf())
                            {
                                if (elemType == TIMElemType.Text) {
                                    //TODO 处理文本消息
                                    TIMTextElem timTextElem=(TIMTextElem)elem;
                                    String mesContent=timTextElem.getText();
                                    String []mg=mesContent.split(",");
                                    if (mg.length==4){
                                        Log.e("哈哈哈哈",mesContent);
                                        if (mg[0].equals("vote_key")){
                                            updateVote(mg[1],mg[2],mg[3],TYPE_SENT);
                                        }
                                    }else
                                        loadText(mesContent,timMessage.getSenderProfile().getIdentifier(),TYPE_RECEIVED);
                                }
                                else if (elemType == TIMElemType.Image){
                                    //TODO 处理图片消息
                                    //图片元素
                                    loadPic(elem,TYPE_RECEIVED,timMessage.getSenderProfile().getIdentifier());
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

    private void updateVote(String s, String s1, String s2,int typeReceived) {
        Msg mesg=new Msg(s,s1,s2,typeReceived);
        mMsgList.add(mesg);
        inputText.setText("");//清空输入框中的内容
        adapter.notifyItemInserted(mMsgList.size() - 1);//当有新消息时，刷新RecyclerView中显示
        msgRecyclerView.scrollToPosition(mMsgList.size() - 1);//将Recycler View定位到最后一行

    }

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
        String fileName = groupId + ".jpg";
        File picFile = new File(dirPath, fileName);
        if (picFile.exists()) {
            picFile.delete();
        }
        return Uri.fromFile(picFile);
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
        //投票传回成功
        else if (requestCode==0x03){
            Log.e("投票传回成功","OK");
            if (resultCode==Activity.RESULT_OK){
                Intent intent=getIntent();
                Bundle bundle=data.getExtras();
                String voteResult=bundle.getString("VOTE_KEY");
                Log.e("哈哈哈哈或或或或或或或或",voteResult);
                String voteId="",voteResultId="",vateName="";
                String []list=voteResult.split(",");
                Log.e("传回的长度",list.length+"");
                voteId=list[0];
                voteResultId=list[1];
                vateName=list[2];
                loadVote(voteId,voteResultId,vateName);
                BmobQuery<Vote> query=new BmobQuery<>();
                BmobQuery<Vote> eq1 = new BmobQuery<>();
                eq1.addWhereEqualTo("objectId",voteId);
                eq1.findObjects(this, new FindListener<Vote>() {
                    @Override
                    public void onSuccess(List<Vote> list) {

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }



    private void doSendPic(final Uri uri,int key) {
        String path = null;
        if (key==FROM_CAMERA){
            path=uri.getPath();
        }else if (key==FROM_ALBUM){
            path= RealPathFromUriUtils.getRealPathFromUri(this, uri);//真实路径
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
                Toast.makeText(GroupChatActivity.this, "发送图片错误 错误码："+code, Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(GroupChatActivity.this, "获取发送图片失败 错误码："+i, Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onSuccess() {
                                        Msg msg=new Msg(TYPE_SENT,LiveApplication.getApplication().getSelfProfile().getIdentifier(),finalPath);
                                        mMsgList.add(msg);
                                        adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
                                        msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
    private void loadPic(final TIMElem elem, final int typeReceived, final String id) {
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
                            Toast.makeText(GroupChatActivity.this, "获取接受图片失败 错误码："+i, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess() {
                            new SharedPreferencesUtil(getApplicationContext()).doPutString(image.getUuid()+Thumb,uri.getPath());
                            Msg msg=new Msg(typeReceived,id,uri.getPath());
                            mMsgList.add(msg);
                            adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
                            msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
                        }
                    });
                }else {
                    Log.e("本地加载图片 path",path);
                    Msg msg=new Msg(typeReceived,id,path);
                    mMsgList.add(msg);
                    adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
                    msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
                }

            }
        }
    }
    private void loadText(String mesContent,String id,int status) {
        Msg msg=new Msg(mesContent, status,id);
        mMsgList.add(msg);
        inputText.setText("");//清空输入框中的内容
        adapter.notifyItemInserted(mMsgList.size()-1);//当有新消息时，刷新RecyclerView中显示
        msgRecyclerView.scrollToPosition(mMsgList.size()-1);//将Recycler View定位到最后一行
    }

    private void loadVote(final String voteId, final String voteResultId, final String voteName) {
        //TODO 发送消息
        //对话发出
        TIMMessage message = new TIMMessage();//构造一条消息
        TIMTextElem timTextElem = new TIMTextElem();//添加文本内容
        timTextElem.setText("vote_key"+","+voteId+","+voteResultId+","+voteName);
        if (message.addElement(timTextElem) != 0) {
            Log.e("添加会话消息", "addElement failed");
        }
        //发送文本消息
        conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                Toast.makeText(GroupChatActivity.this, "对话发送失败,错误码为：" + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Msg mesg=new Msg(voteId,voteResultId,voteName,TYPE_SENT);
                mMsgList.add(mesg);
                inputText.setText("");//清空输入框中的内容
                adapter.notifyItemInserted(mMsgList.size() - 1);//当有新消息时，刷新RecyclerView中显示
                msgRecyclerView.scrollToPosition(mMsgList.size() - 1);//将Recycler View定位到最后一行
            }
        });

    }

    private Uri createGetPicUri(String uuid) {
        String dirPath = Environment.getExternalStorageDirectory() + "/" + getApplication().getApplicationInfo().packageName+uuid;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = groupId + ".jpg";
        File picFile = new File(dirPath, fileName);
        if (picFile.exists()) {
            picFile.delete();
        }
        return Uri.fromFile(picFile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TIMManager.getInstance().removeMessageListener(timMessageListener);
    }


}
