package com.example.weiwenjie.sassistbot;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AccEditActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;//can be any code

    Boolean authCheck=false;
    ImageView imageView;
    EditText editText;
    FirebaseUser user;
    Uri uriProfileImage;
    ProgressBar progressBar;
    Button veri;

    String profileImageUrl;
    Bitmap bmap;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_edit);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        byte[] byteArray = extras.getByteArray("Picture");
        String nameIn=extras.getString("Name");

        veri = findViewById(R.id.btnVerified);
        if (user.isEmailVerified())
        veri.setText("Email Verified");
        else veri.setText("Email Not Verified (Click to Verify)");

        bmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bmap);

        editText = findViewById(R.id.editTextDisplayName);
        editText.setText(nameIn);

        progressBar = findViewById(R.id.progressbar);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });


        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToFirebaseStorage();//save data is call inside
            }
        });
    }

    public void accResetPass(View view){
        if(user.isEmailVerified()) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = user.getEmail();

            assert emailAddress != null;
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AccEditActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(AccEditActivity.this, "Your have not ReAuth or Email has not verify.", Toast.LENGTH_SHORT).show();
        }

    }

    public void accDelete(){

        AlertDialog alert = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(AccEditActivity.this);
        alert = builder.setIcon(R.mipmap.ic_launcher)
                .setTitle("WARNING：")
                .setMessage("YOU ARE ABOUT TO DELETE THIS ACCOUNT!\nARE YOU SURE TO PROCEED!")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AccEditActivity.this, "Action Cancel~", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AccEditActivity.this,"User account deleted.",Toast.LENGTH_LONG).show();
                                            ProfileActivity.pa.finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AccEditActivity.this,"Fail to delete account \nTry Again later.",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .create();
        alert.show();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, AccMenu.class));
        }
    }

    public void veriAcc(View view){
        if (user.isEmailVerified()) {
            veri.setText("Email Verified");
            Toast.makeText(AccEditActivity.this, "Email Already Verified", Toast.LENGTH_SHORT).show();
        } else {
            veri.setText("Email Not Verified (Click to Verify)");
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AccEditActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }






    private void saveUserInformation() {
        String displayName = editText.getText().toString();

        if (displayName.isEmpty()) {
            editText.setError("Name required");
            editText.requestFocus();
            return;
        }



        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            progressBar.setVisibility(View.VISIBLE);

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AccEditActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else if (user !=null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
            progressBar.setVisibility(View.VISIBLE);

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AccEditActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" +user.getEmail()+ ".jpg");

        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
                                    profileImageUrl=downloadUrl.toString();
                                    saveUserInformation();

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AccEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else saveUserInformation();
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }
    public Boolean reAuth(View view) {


        AlertDialog.Builder builder = new AlertDialog.Builder(AccEditActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(AccEditActivity.this, R.layout.dialog_login, null);
        //设置对话框布局
        dialog.setView(dialogView);
        dialog.show();
        final EditText etName = dialogView.findViewById(R.id.et_name);
        final EditText etPwd = dialogView.findViewById(R.id.et_pwd);
        Button btnLogin = dialogView.findViewById(R.id.btn_login);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etPwd.getText().toString()) && TextUtils.isEmpty(etName.getText().toString())) {
                    Toast.makeText(AccEditActivity.this, "Email and Password Should not be empty", Toast.LENGTH_SHORT).show();
                    authCheck = false;
                    return;
                }

                AuthCredential credential = EmailAuthProvider
                        .getCredential(etName.getText().toString(), etPwd.getText().toString());

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AccEditActivity.this, "User re-authenticated.", Toast.LENGTH_SHORT).show();
                                authCheck = true;
                                accDelete();
                                dialog.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        authCheck = false;
                    }
                });

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return authCheck;
    }

}
