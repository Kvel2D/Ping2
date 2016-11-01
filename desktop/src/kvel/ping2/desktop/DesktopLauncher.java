package kvel.ping2.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import kvel.ping2.Ping2;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Ping 2";
        config.width = 640;
        config.height = 480;
		new LwjglApplication(new Ping2(), config);
	}
}

