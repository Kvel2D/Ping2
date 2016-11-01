package kvel.ping2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen implements Screen {

	Texture menuBackgroundImage;
	Texture selectImage;
	Rectangle select;

	final Ping2 game;

	OrthographicCamera camera;

	public MenuScreen(final Ping2 gam) {
		this.game = gam;

		menuBackgroundImage = new Texture(
				Gdx.files.internal("menuBackground.png"));
		selectImage = new Texture(Gdx.files.internal("select.png"));

		select = new Rectangle();
		select.x = 220;
		select.y = 160;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 640, 480);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		game.batch.begin();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.draw(menuBackgroundImage, 0, 0);
		game.batch.draw(selectImage, select.x, select.y);
		game.batch.end();

		if (Gdx.input.isKeyJustPressed(Keys.ENTER) && select.y == 160) {
			game.setScreen(new GameScreen(game));
			dispose();
		}
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) && select.y == 75)
			Gdx.app.exit();
		if (Gdx.input.isKeyJustPressed(Keys.UP))
			select.y = 160;
		if (Gdx.input.isKeyJustPressed(Keys.DOWN))
			select.y = 75;

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		menuBackgroundImage.dispose();
		selectImage.dispose();
	}

}
