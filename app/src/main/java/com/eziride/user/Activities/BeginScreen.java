package com.eziride.user.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eziride.user.Helper.LocaleUtils;
import com.eziride.user.R;
import com.splunk.mint.Mint;

public class BeginScreen extends AppCompatActivity {

    TextView enter_ur_mailID, changeLanguage;
    LinearLayout social_layout, lnrBegin;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(this.getApplication(), "3c1d6462");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_begin);
        enter_ur_mailID = (TextView) findViewById(R.id.enter_ur_mailID);
        changeLanguage = (TextView) findViewById(R.id.changelanguage);
        social_layout = (LinearLayout) findViewById(R.id.social_layout);
        lnrBegin = (LinearLayout) findViewById(R.id.lnrBegin);
        enter_ur_mailID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(BeginScreen.this, ActivityEmail.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        social_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(BeginScreen.this, ActivitySocialLogin.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent languageIntent = new Intent(BeginScreen.this, ChangeLanguage.class);
                languageIntent.putExtra("isLogin", false);
                startActivity(languageIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

    }


}