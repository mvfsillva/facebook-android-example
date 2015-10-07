package com.mvfsilva.facebookExampleLogin.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import co.coderiver.facebooklogin_sample.R;


public class MainActivity extends AppCompatActivity{

    private CallbackManager callbackManager;
    private TextView info;
    private ImageView profileImgView;
    private LoginButton loginButton;
    private Button takePhotoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        info            = (TextView) findViewById(R.id.info);
        profileImgView  = (ImageView) findViewById(R.id.profile_img);
        loginButton     = (LoginButton) findViewById(R.id.login_button);
        takePhotoButton = (Button) findViewById(R.id.photo_button);

        loginButton.setPublishPermissions("publish_actions");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                info.setText(message(profile));

                String userId = loginResult.getAccessToken().getUserId();
                String accessToken = loginResult.getAccessToken().getToken();

                //Pegando imagem do perfil
                String profileImgUrl = "https://graph.facebook.com/" + userId + "/picture?type=large";

                //Utilizando https://github.com/bumptech/glide
                Glide.with(MainActivity.this).load(profileImgUrl).into(profileImgView);
                takePhotoButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                info.setText("Login Cancelado!!");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
                info.setText("Erro ao tentar efetuar login");
            }
        });

        takePhotoButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SharePhotoActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        deleteAccessToken();
        Profile profile = Profile.getCurrentProfile();
        info.setText(message(profile));

        //Bitmap image = Sharedphoto photo = new SharedPhoto.Builder().setBitmap(image).build();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private String message(Profile profile) {
        StringBuilder stringBuffer = new StringBuilder();

        if (profile != null)
            stringBuffer.append("Nome: ").append(profile.getName() + "\n");
        else
            info.setText("Deu Merda");

        return stringBuffer.toString();
    }

    private void deleteAccessToken() {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null)
                    clearUserArea();
            }
        };
    }

    private void clearUserArea() {
        //info.setText("Teste");
        profileImgView.setImageDrawable(null);

    }
}
