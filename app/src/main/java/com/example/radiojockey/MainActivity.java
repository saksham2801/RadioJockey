package com.example.radiojockey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Uri uri;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference mRef;
    private StorageReference storageRef;
    EditText occasion;
    EditText name;
    EditText youtubeLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user =  mAuth.getCurrentUser();
        userId = user.getUid();
        mRef = mDatabase.child("Data").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference();


        occasion = (EditText)findViewById(R.id.occasion);
        name = (EditText)findViewById(R.id.Name);
        youtubeLink = (EditText) findViewById(R.id.youtubeLink);
        Button personalVoice = (Button)findViewById(R.id.personalVoice);
        Button upload = (Button)findViewById(R.id.upload);
        personalVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,1);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String occasion_string = occasion.getText().toString();
                String name_string = name.getText().toString();
                String youtubeLink_string = youtubeLink.getText().toString();
                mRef.child("occasion").setValue(occasion_string);
                mRef.child("name").setValue(name_string);
                mRef.child("youtube_link").setValue(youtubeLink_string);
                mRef.child("Audio_uri").setValue(uri.toString());
                String path = name_string + "/" + occasion_string + "/" + youtubeLink_string + "/" + uri.toString();
                uploadData(uri,path);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                //the selected audio.
                uri = data.getData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadData(Uri videoUri, String path) {
        if(videoUri != null){
            StorageReference videoRef = storageRef.child(path);
            UploadTask uploadTask = videoRef.putFile(videoUri);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                        Toast.makeText(MainActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                    //progressBarUpload.setVisibility(View.INVISIBLE);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //updateProgress(taskSnapshot);
                }
            });
        }else {
            Toast.makeText(MainActivity.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
        }

    }
}