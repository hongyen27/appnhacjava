package com.example.cuoiky;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import retrofit2.Response;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.internal.Sleeper;

import java.io.File;


public class Upload extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    TextView filename;
    Button upload;
    EditText songname,artist;
    File file;
    String fullPath;
    Song song;
    static final int SELECT_MP3 = 1;
    StorageReference storageReference;
    DatabaseHelper databaseHelper;
    Uri u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        setControl();
        setEvent();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(Upload.this,MainActivity.class);
        startActivity(intent1);
        finish();
    }
    private void setControl() {
        filename = findViewById(R.id.filename);
        upload = findViewById(R.id.upload);
        songname = findViewById(R.id.upload_songname);
        artist = findViewById(R.id.upload_artistname);
        databaseHelper = new DatabaseHelper(Upload.this);
        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        storageReference = FirebaseStorage.getInstance().getReference();



    }

    private void setEvent() {
        filename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                startActivityForResult(Intent.createChooser(intent, "Select mp3 file"), SELECT_MP3);
//                new MaterialFilePicker()
//                        .withActivity(Upload.this)
//                        .withRequestCode(10)
//                        .start();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Upload.this, "Uploading...", Toast.LENGTH_SHORT).show();
                Uri file = Uri.fromFile(new File(fullPath));
                StorageReference riversRef = storageReference.child("audio/"+file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                song = new Song();
                                song.setUrl(uri.toString());
                                song.setName(songname.getText().toString());
                                song.setArtist(artist.getText().toString());
                                song.setId(0);
                                song.setStatus(1);
                                song.setCount(0);
                                databaseHelper.insertSong(song);
                            }
                        });
                        Toast.makeText(Upload.this, "Upload Done", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Upload.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                u = data.getData();
                file = new File(u.getPath());
                fullPath = UriUtils.getPathFromUri(this,u);
                filename.setText(fullPath);
                songname.setText(getName(u));
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
        public String getName(Uri uri) {
            if( uri == null ) {
                // TODO perform some logging or show user feedback
                return null;
            }
//            String[] projection = { MediaStore.Audio.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if(cursor != null){


                cursor.moveToFirst();
                String path = cursor.getString(2);
                cursor.close();
                return path;
            }
            // this is our fallback here
            return uri.getPath();
        }

}

