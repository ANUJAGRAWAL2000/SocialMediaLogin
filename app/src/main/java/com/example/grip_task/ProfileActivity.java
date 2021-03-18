package com.example.grip_task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvName,tvLocation,tvAnuj,tvDate,tvMale,tvEmailAddress,tvProfile;
    FrameLayout btLogout;
    FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    String Name,Email,UserId,PicURL,Type,Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ivProfile=findViewById(R.id.ivProfile);
        tvName=findViewById(R.id.tvName);
        tvLocation=findViewById(R.id.tvLocation);
        tvAnuj=findViewById(R.id.tvAnuj);
        tvDate=findViewById(R.id.tvDate);
        tvMale=findViewById(R.id.tvMale);
        tvEmailAddress=findViewById(R.id.tvEmailAddress);
        tvProfile=findViewById(R.id.tvProfile);
        btLogout=findViewById(R.id.FrLogout);
        mAuth=FirebaseAuth.getInstance();

        Intent intent = getIntent();
        Name = intent.getStringExtra("name");
        Email = intent.getStringExtra("email");
        UserId = intent.getStringExtra("id");
        PicURL = intent.getStringExtra("picstring");
        Type = intent.getStringExtra("social");
        Location = intent.getStringExtra("location");

        if(PicURL.equals("No Picture found")) {
            ivProfile.setImageResource(R.drawable.ic_baseline_supervised_user_circle_24);
        } else {
            Glide.with(ProfileActivity.this)
                    .load(PicURL)
                    .into(ivProfile);
        }
        if(!Name.equals(""))
            tvName.setText(Name);
//        if(!Location.equals(""))
//            tvLocation.setText(Location);
//        if(!UserId.equals(""))
//            tvAnuj.setText(UserId);
        if(!Email.equals(""))
            tvEmailAddress.setText(Email);

        googleSignInClient= GoogleSignIn.getClient(ProfileActivity.this
        , GoogleSignInOptions.DEFAULT_SIGN_IN);

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Type.equals("Google")) {
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mAuth.signOut();
                                Toast.makeText(ProfileActivity.this, "Logout successful", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                }
                if(Type.equals("Facebook")){
                    disconnectFromFacebook();
                    Toast.makeText(ProfileActivity.this,"User Logged out" , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                }
            }
        });
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return;
        }
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/permissions/",
                null,
                HttpMethod.DELETE,
                new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                    }
                })
                .executeAsync();
    }
}