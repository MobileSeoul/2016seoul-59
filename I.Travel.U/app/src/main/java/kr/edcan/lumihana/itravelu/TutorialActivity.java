package kr.edcan.lumihana.itravelu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

/**
 * Created by kimok_000 on 2016-10-31.
 */
public class TutorialActivity extends AppIntro2 {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);

        sharedPreferences = getSharedPreferences("Setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        addSlide(AppIntro2Fragment.newInstance(getString(R.string.app_name), "Welcome to I.Travel.U", R.drawable.logo_login, getResources().getColor(R.color.colorPrimary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addSlide(AppIntro2Fragment.newInstance("Permission Required", "Android M\nLocation Permission request", R.mipmap.ic_launcher, getResources().getColor(R.color.colorPrimary)));

        }
        showSkipButton(false);
        showDoneButton(true);
        setProgressButtonEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void toSign() {
        Intent intent = new Intent(TutorialActivity.this, SignActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        editor.putBoolean("isFirst", false);
        editor.commit();
        toSign();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
