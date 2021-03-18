package com.example.grip_task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    FrameLayout llSocialMedia;
    LinearLayout llBoth,llHorizontal;
    String Type;
    ImageView ivGoogle,ivFacebook;
    TextView tvGoogle,tvFacebook;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private ProgressDialog progressDialog;
    private static final int FB_REQUEST_CODE=102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivGoogle=findViewById(R.id.ivGoogle);
        ivFacebook=findViewById(R.id.ivFacebook);
        tvGoogle=findViewById(R.id.tvGoogle);
        tvFacebook=findViewById(R.id.tvFacebook);
        llBoth=findViewById(R.id.llBoth);
        llSocialMedia=findViewById(R.id.llSocialMedia);
        llHorizontal=findViewById(R.id.llHorizontal);
        llSocialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAnimation();
            }
        });

//      ----------------------FACEBOOK LOGIN--------------------------
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading..");
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Type="Facebook";
                progressDialog.show();
                loginManager.logInWithReadPermissions(
                        MainActivity.this,
                         Arrays.asList(
                                "email",
                                "public_profile"));
                checkLoginStatus();
            }
        });

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                String Info=loginResult.getAccessToken().getUserId();
//                String ImgUrl="https://graph.facebook.com/"+loginResult.getAccessToken().getUserId()+"/picture?return_ssl_resources=1";
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onCancel() {
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onError(FacebookException error) {
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
//      -----------------------GOOGLE LOGIN------------------------
        //Initialize Sign In Options
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("135750906913-l72deah9r90iohftms5u51quuba5b353.apps.googleusercontent.com")
                .requestEmail()
                .build();

        //Initialize SignIn Client
        googleSignInClient= GoogleSignIn.getClient(MainActivity.this,
                googleSignInOptions);

        ivGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize sign in intent
                Intent intent=googleSignInClient.getSignInIntent();
                //Start Activity For Result
                startActivityForResult(intent,101);
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser getCurrentUser=firebaseAuth.getCurrentUser();
        if(getCurrentUser!=null){
            Type="Google";
            Intent intent=new Intent(MainActivity.this,
                    ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("name", "Anuj Agrawal");
            intent.putExtra("email", "anujagr2001@gmail.com");
            intent.putExtra("picstring",  "No Picture found");
            intent.putExtra("social", Type);
            startActivity(intent);
        }
//        ----------------------------------------
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check Condition
        if(requestCode==FB_REQUEST_CODE)
            callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101)
        {
            //Initialize task
            Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            if(signInAccountTask.isSuccessful()) {
                //SignIn successful
                Toast.makeText(MainActivity.this,"Google Sign In Successful",Toast.LENGTH_LONG).show();
                try {
                    //Initialize signIn account
                    GoogleSignInAccount googleSignInAccount=signInAccountTask.getResult(ApiException.class);
                    //check Condition
                    if(googleSignInAccount!=null) {
                        //Initialize auth credential
                        AuthCredential authCredential= GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken(),null);
                        //check credential
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Type="Google";
                                            Intent intent=new Intent(MainActivity.this,
                                                    ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("name", googleSignInAccount.getDisplayName());
                                            intent.putExtra("email", googleSignInAccount.getEmail());
                                            intent.putExtra("picstring", googleSignInAccount.getPhotoUrl() != null ? googleSignInAccount.getPhotoUrl().toString() : "No Picture found");
                                            intent.putExtra("social", Type);
                                            startActivity(intent);
                                            Toast.makeText(MainActivity.this,"Firebase Authentication Successful",Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this,"Authentication Failed : %1$s"+task.getException(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    AccessTokenTracker accessTokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null) {
                    Toast.makeText(MainActivity.this,"User Logged Out",Toast.LENGTH_LONG).show();
                }
                else{
                loaduserProfile(currentAccessToken);
            }
        }
    };

    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            loaduserProfile(AccessToken.getCurrentAccessToken());
        }
    }

    private void loaduserProfile(AccessToken newAccessToken){
        Bundle parameters=new Bundle();
        parameters.putBoolean("redirect", false);
        parameters.putString("fields",
                "first_name, last_name, email, id, picture.type(normal)");

        new GraphRequest(
                newAccessToken,
                "me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(response != null) {
                            try {
                                JSONObject object = response.getJSONObject();
                                String name = object.getString("first_name") + " " + object.getString("last_name");
                                String email = object.getString("email");
                                String id = object.getString("id");
                                //String birthday = object.getString("birthday");
                                //String profileLink = object.getString("link");
                                //String gender = object.getString("gender");

                                String link = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                Type="Facebook";
                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("id", id);
                                intent.putExtra("picstring", link);
                                intent.putExtra("social", Type);
                                if(progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                            catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                Log.i("Error", "SOME ERROR" + e.toString());
                                e.printStackTrace();
                            }
                        }

                    }
                }
        ).executeAsync();
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

    private void AddAnimation(){
        llSocialMedia.setVisibility(View.GONE);
        llBoth.setVisibility(View.VISIBLE);
        llHorizontal.setVisibility(View.VISIBLE);
        Animation animation_LeftToRight= AnimationUtils.loadAnimation(MainActivity.this,R.anim.lefttoright);
        Animation animation_RightToLeft= AnimationUtils.loadAnimation(MainActivity.this,R.anim.righttoleft);
        Animation animation_ZoomIn= AnimationUtils.loadAnimation(MainActivity.this,R.anim.zoomin);
        ivGoogle.startAnimation(animation_LeftToRight);
        ivFacebook.startAnimation(animation_RightToLeft);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvGoogle.setVisibility(View.VISIBLE);
                tvFacebook.setVisibility(View.VISIBLE);
                tvGoogle.startAnimation(animation_ZoomIn);
                tvFacebook.startAnimation(animation_ZoomIn);
            }
        },1000);
    }
}