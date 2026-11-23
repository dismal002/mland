package com.dismal.marshmallowland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.andexert.library.RippleView;
import com.dismal.marshmallowland.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    public static long LastClickTimeStamp = retrunTimestamp();
    public static FloatingActionButton about;
    public static FloatingActionButton difficulty;
    public static FloatingActionButton sound;
    public static ViewGroup bonusView;
    public static FloatingActionMenu menu2;
    public static boolean mute;
    public static View playerSetup;
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor prefs_edit;
    public static TextView taptoplay;

    MLand mLand;
    boolean CanStart = true;

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.about) {
                menu2.toggle(true);
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            } else if (id == R.id.difficulty) {
                int difficulty_level = prefs.getInt("difficulty", 1);
                prefs_edit = prefs.edit();
                switch (difficulty_level) {
                    case 0:
                        prefs_edit.putInt("difficulty", 1);
                        break;
                    case 1:
                        prefs_edit.putInt("difficulty", 2);
                        break;
                    case 2:
                        prefs_edit.putInt("difficulty", 0);
                        break;
                }
                prefs_edit.commit();
                RefreshLevel();
            } else if (id == R.id.sound) {
                mute = prefs.getBoolean("mute", false);
                prefs_edit = prefs.edit();
                if (mute) {
                    mute = false;
                    sound.setImageResource(R.drawable.ic_sound);
                    prefs_edit.putBoolean("mute", mute);
                } else {
                    mute = true;
                    sound.setImageResource(R.drawable.ic_sound_muted);
                    prefs_edit.putBoolean("mute", mute);
                }
                prefs_edit.commit();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.mLand = findViewById(R.id.world);
        this.mLand.setScoreFieldHolder(findViewById(R.id.scores));
        bonusView = findViewById(R.id.bonus_view);
        View welcome = findViewById(R.id.welcome);
        this.mLand.setSplash(welcome);
        int numControllers = this.mLand.getGameControllers().size();
        if (numControllers > 0) {
            this.mLand.setupPlayers(numControllers);
        }
        prefs = getSharedPreferences("prefs", 0);
        about = findViewById(R.id.about);
        sound = findViewById(R.id.sound);
        difficulty = findViewById(R.id.difficulty);
        taptoplay = findViewById(R.id.tap_to_play_text);
        playerSetup = findViewById(R.id.player_setup);
        RefreshLevel();
        mute = prefs.getBoolean("mute", false);
        about.setOnClickListener(this.clickListener);
        sound.setOnClickListener(this.clickListener);
        difficulty.setOnClickListener(this.clickListener);
        menu2 = findViewById(R.id.menu2);
        menu2.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    taptoplay.animate().alpha(0.0f);
                    MainActivity.this.CanStart = false;
                } else {
                    taptoplay.animate().alpha(1.0f);
                    MainActivity.this.CanStart = true;
                }
            }
        });
        RippleView taptoplay2 = findViewById(R.id.more);
        taptoplay2.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                MainActivity.this.mLand.stop();
                MainActivity.this.mLand.showSplash();
                MainActivity.this.mLand.reset();
                MainActivity.this.mLand.start(false);
            }

            @Override
            public void onSwipeLeft() {
                MainActivity.this.mLand.stop();
                MainActivity.this.mLand.showSplash();
                MainActivity.this.mLand.reset();
                MainActivity.this.mLand.start(false);
            }
        });
        ShowSwipeTip();
    }

    public void ShowSwipeTip() {
        int SwipeTipCount = prefs.getInt("swipecount", 0);
        if (SwipeTipCount <= 4) {
            prefs_edit = prefs.edit();
            prefs_edit.putInt("swipecount", SwipeTipCount + 1);
            prefs_edit.commit();
            Snackbar.make(findViewById(android.R.id.content), "Swipe to change scenery", 0).show();
        }
    }

    public static long retrunTimestamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public void RefreshLevel() {
        int difficulty_level = prefs.getInt("difficulty", 1);
        switch (difficulty_level) {
            case 0:
                MLand.Difficulty = MLand.DifficultyLevels.Easy;
                difficulty.setImageResource(R.drawable.ic_easy_level);
                break;
            case 1:
                MLand.Difficulty = MLand.DifficultyLevels.Normal;
                difficulty.setImageResource(R.drawable.ic_normal_level);
                break;
            case 2:
                MLand.Difficulty = MLand.DifficultyLevels.Hard;
                difficulty.setImageResource(R.drawable.ic_hard_level);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mLand.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mLand.onAttachedToWindow();
        this.mLand.showSplash();
        if (prefs.getBoolean("mute", false)) {
            sound.setImageResource(R.drawable.ic_sound_muted);
        } else {
            sound.setImageResource(R.drawable.ic_sound);
        }
        getWindow().getDecorView().setSystemUiVisibility(3846);
    }

    public void startButtonPressed(View v) {
        if (!this.CanStart) {
            menu2.toggle(true);
        }
        if (retrunTimestamp() - LastClickTimeStamp >= 1 && this.CanStart) {
            int numControllers = this.mLand.getGameControllers().size();
            if (numControllers > 0) {
                this.mLand.setupPlayers(numControllers);
            }
            this.mLand.start(true);
        }
    }
}
