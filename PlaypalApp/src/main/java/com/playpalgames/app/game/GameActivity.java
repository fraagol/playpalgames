package com.playpalgames.app.game;

import android.app.Activity;
import android.os.Bundle;

import com.playpalgames.app.R;
import com.playpalgames.library.GameControllerImpl;


public class GameActivity extends Activity {
GameControllerImpl gameController=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameController= GameControllerImpl.getInstance();
    }



}
