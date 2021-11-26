package com.eziride.user.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.eziride.user.Helper.CustomDialog;
import com.eziride.user.Helper.LocaleUtils;
import com.eziride.user.Helper.SharedHelper;
import com.eziride.user.R;

public class ChangeLanguage extends AppCompatActivity {

    private ImageView backArrow;

    private RadioButton radioEnglish, radioJapanese, radioChinese;

    private LinearLayout lnrEnglish, lnrJapanese, lnrChinese;
    private CustomDialog customDialogNew;
    private boolean isFromLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        radioEnglish = (RadioButton) findViewById(R.id.radioEnglish);
        radioJapanese = (RadioButton) findViewById(R.id.radioJapanese);
        radioChinese = (RadioButton) findViewById(R.id.radioChinese);

        lnrEnglish = (LinearLayout) findViewById(R.id.lnrEnglish);
        lnrJapanese = (LinearLayout) findViewById(R.id.lnrJapanese);
        lnrChinese = (LinearLayout) findViewById(R.id.lnrChinese);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        isFromLogin = getIntent().getBooleanExtra("isLogin", true);

        if (SharedHelper.getKey(ChangeLanguage.this, "language").equalsIgnoreCase("en")) {
            radioEnglish.setChecked(true);
        } else if (SharedHelper.getKey(ChangeLanguage.this, "language").equalsIgnoreCase("ja")) {
            radioJapanese.setChecked(true);
        } else if (SharedHelper.getKey(ChangeLanguage.this, "language").equalsIgnoreCase("zh")) {
            radioChinese.setChecked(true);
        } else {
            radioEnglish.setChecked(true);
        }

        lnrEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioJapanese.setChecked(false);
                radioChinese.setChecked(false);
                radioEnglish.setChecked(true);
            }
        });

        lnrJapanese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEnglish.setChecked(false);
                radioChinese.setChecked(false);
                radioJapanese.setChecked(true);
            }
        });
        lnrChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEnglish.setChecked(false);
                radioChinese.setChecked(true);
                radioJapanese.setChecked(false);
            }
        });

        radioJapanese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    radioEnglish.setChecked(false);
                    SharedHelper.putKey(ChangeLanguage.this, "language", "ja");
                    setLanguage();
//                    recreate();
                    GoToMainActivity();
                }
            }
        });

        radioEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    radioJapanese.setChecked(false);
                    SharedHelper.putKey(ChangeLanguage.this, "language", "en");
                    setLanguage();
//                    recreate();
                    GoToMainActivity();
                }
            }
        });
        radioChinese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    radioJapanese.setChecked(false);
                    SharedHelper.putKey(ChangeLanguage.this, "language", "zh");
                    setLanguage();
//                    recreate();
                    GoToMainActivity();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(ChangeLanguage.this, "language");
        LocaleUtils.setLocale(getApplicationContext(), languageCode);
    }

    public void GoToMainActivity() {
        customDialogNew = new CustomDialog(ChangeLanguage.this, getResources().getString(R.string.language_update));
        if (customDialogNew != null)
            customDialogNew.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                customDialogNew.dismiss();
                Intent mainIntent;

                if (isFromLogin) {
                    mainIntent = new Intent(ChangeLanguage.this, MainActivity.class);
                } else {
                    mainIntent = new Intent(ChangeLanguage.this, BeginScreen.class);
                }
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        }, 3000);
    }
}
