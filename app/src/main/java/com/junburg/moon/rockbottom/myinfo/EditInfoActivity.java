package com.junburg.moon.rockbottom.myinfo;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.junburg.moon.rockbottom.R;
import com.junburg.moon.rockbottom.login.LoginActivity;
import com.junburg.moon.rockbottom.login.UserData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Junburg on 2018. 2. 26..
 */

public class EditInfoActivity extends AppCompatActivity {
    private static final int GALLERY_CODE = 11;
    private ArrayList<EditInfoRecyclerData> dataList = new ArrayList<>();
    private Map<String, String> editDataMap;
    private EditInfoRecyclerData editInfoRecyclerData;
    private RecyclerView editInfoRecyclerView;
    private Toolbar editInfoToolbar;
    private ImageView editInfoSelfieImg;
    private Button editInfoSelfieBtn;
    private CollapsingToolbarLayout editInfoCollapsingToolbarLayout;
    private Uri selfieUri;
    private String selfieString;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String userId;
    private FirebaseStorage storage;

    private UserData userData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        storage = FirebaseStorage.getInstance();

        setContentView(R.layout.activity_edit_info);
        editInfoCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.edit_info_collapsing_tool_bar_layout);
        editInfoCollapsingToolbarLayout.setTitle("내 정보");
        editInfoToolbar = (Toolbar) findViewById(R.id.edit_info_tool_bar);
        setSupportActionBar(editInfoToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editInfoSelfieImg = (ImageView) findViewById(R.id.edit_info_selfie_img);
        editInfoSelfieBtn = (Button) findViewById(R.id.edit_info_selfie_btn);
        editInfoRecyclerView = (RecyclerView) findViewById(R.id.edit_info_recycler);
        editInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        EditInfoRecyclerAdapter adapter = new EditInfoRecyclerAdapter(dataList);
        editInfoRecyclerView.setAdapter(adapter);
        editInfoSelfieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickUpPicture();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.edit_menu_logout:
                auth.signOut();
                finish();
                startActivity(new Intent(EditInfoActivity.this, LoginActivity.class));
                break;
            case R.id.edit_menu_signout:

                break;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            selfieUri = data.getData();
            selfieString = selfieUri.toString();
            setSelfieImg(selfieUri);
            Glide.with(this)
                    .load(selfieUri)
                    .crossFade()
                    .into(editInfoSelfieImg);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
                putUserData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void pickUpPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }


//    private void alertDialogForEdit(String title, String message, final int id) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//
//        alert.setTitle(title);
//        alert.setMessage(message);
//
//        final EditText editName = new EditText(this);
//        alert.setView(editName);
//
//        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                String text = editName.getText().toString();
//
//            }
//        });
//
//        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//
//        alert.show();
//    }

    private void putUserData() {
        for (int i = 0; i < 4; i++) {
            editInfoRecyclerData = new EditInfoRecyclerData();
            editDataMap = new HashMap<>();
            switch (i) {
                case 0:
                    editDataMap.put("닉네임", userData.getNickName().toString());
                    break;
                case 1:
                    editDataMap.put("메세지", userData.getMessage().toString());
                    break;
                case 2:
                    editDataMap.put("소속", userData.getTeamName().toString());
                    break;
                case 3:
                    editDataMap.put("Github", userData.getGithub().toString());
                    break;
            }
            editInfoRecyclerData.setEditDataMap(editDataMap);
            dataList.add(editInfoRecyclerData);
        }
    }

    private void getData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            userData = new UserData();
            userData.setSelfieUri(ds.child(userId).getValue(UserData.class).getSelfieUri());
            userData.setNickName(ds.child(userId).getValue(UserData.class).getNickName());
            userData.setMessage(ds.child(userId).getValue(UserData.class).getMessage());
            userData.setTeamName(ds.child(userId).getValue(UserData.class).getTeamName());
            userData.setGithub(ds.child(userId).getValue(UserData.class).getGithub());
            userData.setPoints(ds.child(userId).getValue(UserData.class).getPoints());
            userData.setRanking(ds.child(userId).getValue(UserData.class).getRanking());

            if (userData.getSelfieUri() != null) {
                Glide.with(this)
                        .load(userData.getSelfieUri())
                        .crossFade()
                        .into(editInfoSelfieImg);
            }


        }
    }
    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }

    private void setSelfieImg(Uri selfieUri) {
        StorageReference storageReference = storage.getReferenceFromUrl("gs://rockbottom-2bc4e.appspot.com");
        Uri file = Uri.fromFile(new File(getPath(selfieUri)));
        StorageReference riversRef = storageReference.child("selfieImages/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                database.getReference().child("users").child(userId).child("selfieUri").setValue(downloadUrl);

            }
        });
    }
}
