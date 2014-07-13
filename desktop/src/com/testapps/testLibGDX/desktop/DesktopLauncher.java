package com.testapps.testLibGDX.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.testapps.testLibGDX.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Far Far East";
        config.width = 800;
        config.height = 480;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
