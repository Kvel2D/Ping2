package kvel.ping2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen implements Screen {
	final Ping2 game;
	Sound sound;
	Sound hit;
	Texture paddleImage;
	Texture ballImage;
	Texture rectImage;
	float diff;
	int pc_score;
	int player_score;
	int rand1;
	int rand2;
	int rand3;
	int speed;
	int count = 0;
	SpriteBatch batch;
	Sprite ballSprite;
	Sprite paddleSprite;
	Sprite pcPaddleSprite;
	Sprite rect1Sprite;
	Sprite rect2Sprite;
	Body leftEdge;
	Body rightEdge;
	Body bottomEdge;
	Body topEdge;
	Body ball;
	Body rect1;
	Body rect2;
	Vector2 ballVector;
	Body paddle;
	Body pcPaddle;
	World world;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;
	BitmapFont font;
	final float PIXELS_TO_METERS = 100f;

	public GameScreen(final Ping2 gam) {
		this.game = gam;

		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 640, 480);

		sound = Gdx.audio.newSound(Gdx.files.internal("sound.wav"));
		hit = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
		// get textures and assign them to sprites, set sprite positions
		ballImage = new Texture(Gdx.files.internal("ball.png"));
		ballSprite = new Sprite(ballImage);
		ballSprite.setPosition(320 - ballImage.getWidth() / 2, 240);
		paddleImage = new Texture(Gdx.files.internal("paddle.png"));
		paddleSprite = new Sprite(paddleImage);
		paddleSprite.setPosition(320 - paddleImage.getWidth() / 2, 0);
		pcPaddleSprite = new Sprite(paddleImage);
		pcPaddleSprite.setPosition(320 - paddleImage.getWidth() / 2,
				480 - paddleImage.getHeight());
		rectImage = new Texture(Gdx.files.internal("rect.png"));
		rect1Sprite = new Sprite(rectImage);

		rect2Sprite = new Sprite(rectImage);
		rect1Sprite.setPosition(200 - rectImage.getWidth() / 2,
				240 - rectImage.getHeight() / 2);
		rect2Sprite.setPosition(440 - rectImage.getWidth() / 2,
				240 - rectImage.getHeight() / 2);

		world = new World(new Vector2(0, 0), true);

		BodyDef bodyDef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fixtureDef = new FixtureDef();

		// BALL \\
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set((ballSprite.getX() + ballImage.getWidth() / 2)
				/ PIXELS_TO_METERS,
				(ballSprite.getY() + ballImage.getHeight() / 2)
						/ PIXELS_TO_METERS);
		ball = world.createBody(bodyDef);
		shape.setAsBox(ballImage.getWidth() / 2 / PIXELS_TO_METERS,
				ballImage.getHeight() / 2 / PIXELS_TO_METERS);
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		fixtureDef.restitution = 1.0f;
		fixtureDef.friction = 0f;
		ball.createFixture(fixtureDef);
		ball.setFixedRotation(true);// turn off rotation
		// apply force to ball
		ball.applyForceToCenter(new Vector2(0, -2f), false);

		// PADDLE \\
		bodyDef.position.set((paddleSprite.getX() + paddleImage.getWidth() / 2)
				/ PIXELS_TO_METERS,
				(paddleSprite.getY() + paddleImage.getHeight() / 2)
						/ PIXELS_TO_METERS);
		shape.setAsBox(paddleImage.getWidth() / 2 / PIXELS_TO_METERS,
				paddleImage.getHeight() / 2 / PIXELS_TO_METERS);
		paddle = world.createBody(bodyDef);
		fixtureDef.shape = shape;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		paddle.createFixture(fixtureDef);

		// PC PADDLE \\
		bodyDef.position.set(
				(pcPaddleSprite.getX() + paddleImage.getWidth() / 2)
						/ PIXELS_TO_METERS,
				(pcPaddleSprite.getY() + paddleImage.getHeight() / 2)
						/ PIXELS_TO_METERS);
		pcPaddle = world.createBody(bodyDef);
		shape.setAsBox(paddleImage.getWidth() / 2 / PIXELS_TO_METERS,
				paddleImage.getHeight() / 2 / PIXELS_TO_METERS);
		fixtureDef.shape = shape;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		pcPaddle.createFixture(fixtureDef);

		// BOUNDARIES \\
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		bodyDef2.position.set(0, 0);
		FixtureDef fixtureDef2 = new FixtureDef();
		EdgeShape edgeShape = new EdgeShape();
		float w = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
		float h = Gdx.graphics.getHeight() / PIXELS_TO_METERS;
		edgeShape.set(-1, h, w + 1, h);
		fixtureDef2.shape = edgeShape;
		topEdge = world.createBody(bodyDef2);
		topEdge.createFixture(fixtureDef2);
		edgeShape.set(0, -1, 0, h + 1);
		fixtureDef2.shape = edgeShape;
		leftEdge = world.createBody(bodyDef2);
		leftEdge.createFixture(fixtureDef2);
		edgeShape.set(-1, 0, w + 1, 0);
		fixtureDef2.shape = edgeShape;
		bottomEdge = world.createBody(bodyDef2);
		bottomEdge.createFixture(fixtureDef2);
		edgeShape.set(w, -1, w, h + 1);
		fixtureDef2.shape = edgeShape;
		rightEdge = world.createBody(bodyDef2);
		rightEdge.createFixture(fixtureDef2);

		// RECTANGLES \\
		shape.setAsBox(rectImage.getWidth() / 2 / PIXELS_TO_METERS,
				rectImage.getHeight() / 2 / PIXELS_TO_METERS);
		bodyDef2.position.set((rect1Sprite.getX() + rectImage.getWidth() / 2)
				/ PIXELS_TO_METERS,
				(rect1Sprite.getY() + rectImage.getHeight() / 2)
						/ PIXELS_TO_METERS);
		rect1 = world.createBody(bodyDef2);
		shape.setAsBox(rectImage.getWidth() / 2 / PIXELS_TO_METERS,
				rectImage.getHeight() / 2 / PIXELS_TO_METERS);
		fixtureDef2.shape = shape;
		rect1.createFixture(fixtureDef2);

		bodyDef2.position.set((rect2Sprite.getX() + rectImage.getWidth() / 2)
				/ PIXELS_TO_METERS,
				(rect2Sprite.getY() + rectImage.getHeight() / 2)
						/ PIXELS_TO_METERS);
		rect2 = world.createBody(bodyDef2);
		rect2.createFixture(fixtureDef2);

		shape.dispose();
		edgeShape.dispose();

		// prepare score font and set default scores
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(5f, 5f);
		pc_score = 0;
		player_score = 0;

		// ContactListener for Ball-Edge contact checks
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				// Check to see if the collision is between the ball and the
				// bottom of the screen
				// If so modify pc_score
				// play collision sounds
				if ((contact.getFixtureA().getBody() == bottomEdge && contact
						.getFixtureB().getBody() == ball)
						|| (contact.getFixtureA().getBody() == ball && contact
								.getFixtureB().getBody() == bottomEdge)) {
					pc_score++;
					sound.play();
				}
				if ((contact.getFixtureA().getBody() == topEdge && contact
						.getFixtureB().getBody() == ball)
						|| (contact.getFixtureA().getBody() == ball && contact
								.getFixtureB().getBody() == topEdge)) {
					player_score++;
					sound.play();
				}
				/*
				 * if ((contact.getFixtureA().getBody() == leftEdge && contact
				 * .getFixtureB().getBody() == ball) ||
				 * (contact.getFixtureA().getBody() == ball && contact
				 * .getFixtureB().getBody() == leftEdge)) { hit.play(); } if
				 * ((contact.getFixtureA().getBody() == rightEdge && contact
				 * .getFixtureB().getBody() == ball) ||
				 * (contact.getFixtureA().getBody() == ball && contact
				 * .getFixtureB().getBody() == rightEdge)) { hit.play(); } if
				 * ((contact.getFixtureA().getBody() == rect1 && contact
				 * .getFixtureB().getBody() == ball) ||
				 * (contact.getFixtureA().getBody() == ball && contact
				 * .getFixtureB().getBody() == rect1)) { hit.play(); } if
				 * ((contact.getFixtureA().getBody() == rect2 && contact
				 * .getFixtureB().getBody() == ball) ||
				 * (contact.getFixtureA().getBody() == ball && contact
				 * .getFixtureB().getBody() == rect2)) { hit.play(); }
				 */
				if ((contact.getFixtureA().getBody() == paddle && contact
						.getFixtureB().getBody() == ball)
						|| (contact.getFixtureA().getBody() == ball && contact
								.getFixtureB().getBody() == paddle)) {
					// implementing pong physics
					diff = ballSprite.getX() - paddleSprite.getX();

					if (diff <= paddleImage.getWidth() / 8)
						ball.setLinearVelocity(new Vector2(-6f, 4f));

					if (paddleImage.getWidth() / 8 < diff
							&& diff <= paddleImage.getWidth() * 3 / 8)
						ball.setLinearVelocity(new Vector2(-4f, 4f));

					if (paddleImage.getWidth() * 3 / 8 < diff
							&& diff < paddleImage.getWidth() * 5 / 8)
						ball.setLinearVelocity(new Vector2(0, 5f));

					if (paddleImage.getWidth() * 5 / 8 < diff
							&& diff < paddleImage.getWidth() * 7 / 8)
						ball.setLinearVelocity(new Vector2(4f, 4f));

					if (diff > paddleImage.getWidth() * 7 / 8)
						ball.setLinearVelocity(new Vector2(6f, 4f));

					hit.play();
				}
				if ((contact.getFixtureA().getBody() == pcPaddle && contact
						.getFixtureB().getBody() == ball)
						|| (contact.getFixtureA().getBody() == ball && contact
								.getFixtureB().getBody() == pcPaddle)) {
					diff = ballSprite.getX() - pcPaddleSprite.getX();

					if (diff <= paddleImage.getWidth() / 8)
						ball.setLinearVelocity(new Vector2(-6f, -4f)); // -6

					if (paddleImage.getWidth() / 8 < diff
							&& diff <= paddleImage.getWidth() * 3 / 8)
						ball.setLinearVelocity(new Vector2(-4f, -4f));// -4

					if (paddleImage.getWidth() * 3 / 8 < diff
							&& diff < paddleImage.getWidth() * 5 / 8)
						ball.setLinearVelocity(new Vector2(0, -6f));

					if (paddleImage.getWidth() * 5 / 8 < diff
							&& diff < paddleImage.getWidth() * 7 / 8)
						ball.setLinearVelocity(new Vector2(4f, -4f));// 4

					if (diff > paddleImage.getWidth() * 7 / 8)
						ball.setLinearVelocity(new Vector2(6f, -4f));// 6
					hit.play();
				}
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		});
		// debugRenderer = new Box2DDebugRenderer();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		world.step(1f / 60f, 6, 2);

		// batch.setProjectionMatrix(camera.combined);
		// debugMatrix =
		// batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,PIXELS_TO_METERS,
		// 0);

		game.batch.begin();

		// draw ball
		ballSprite.setPosition(
				(ball.getPosition().x * PIXELS_TO_METERS)
						- ballImage.getWidth() / 2,
				(ball.getPosition().y * PIXELS_TO_METERS)
						- ballImage.getHeight() / 2);
		game.batch.draw(ballImage, ballSprite.getX(), ballSprite.getY());

		// draw paddles
		paddle.setTransform((paddleSprite.getX() + paddleImage.getWidth() / 2)
				/ PIXELS_TO_METERS,
				(paddleSprite.getY() + paddleImage.getHeight() / 2)
						/ PIXELS_TO_METERS, 0);
		game.batch.draw(paddleImage, paddleSprite.getX(), paddleSprite.getY());
		pcPaddle.setTransform(
				(pcPaddleSprite.getX() + paddleImage.getWidth() / 2)
						/ PIXELS_TO_METERS,
				(pcPaddleSprite.getY() + paddleImage.getHeight() / 2)
						/ PIXELS_TO_METERS, 0);
		game.batch.draw(pcPaddleSprite, pcPaddleSprite.getX(),
				pcPaddleSprite.getY());

		// draw rectangles
		game.batch.draw(rect1Sprite, rect1Sprite.getX(), rect1Sprite.getY());
		game.batch.draw(rect2Sprite, rect2Sprite.getX(), rect2Sprite.getY());

		// draw score
		if (pc_score == 1)
			font.draw(game.batch, "" + pc_score, 15, // offset ones
					Gdx.graphics.getHeight() / 2 + 70); // so it looks better
		else
			font.draw(game.batch, "" + pc_score, 10,
					Gdx.graphics.getHeight() / 2 + 70);
		if (player_score == 1)
			font.draw(game.batch, "\n" + player_score, 15, // offset ones
					Gdx.graphics.getHeight() / 2 + 70); // so it looks better
		else
			font.draw(game.batch, "\n" + player_score, 10,
					Gdx.graphics.getHeight() / 2 + 70);

		game.batch.end();

		// paddle controls
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			paddleSprite.setPosition(
					paddleSprite.getX() - 700 * Gdx.graphics.getDeltaTime(), 0);
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			paddleSprite.setPosition(
					paddleSprite.getX() + 700 * Gdx.graphics.getDeltaTime(), 0);
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
			game.setScreen(new MenuScreen(game));
		// keep paddle inside boundaries
		if (paddleSprite.getX() < 0)
			paddleSprite.setPosition(0, paddleSprite.getY());
		if (paddleSprite.getX() > 640 - paddleImage.getWidth())
			paddleSprite.setPosition(640 - paddleImage.getWidth(),
					paddleSprite.getY());

		// move pc paddle || +-5 is for decreased tolerance
		if (ballSprite.getX() - ballImage.getWidth() + 10 > pcPaddleSprite.getX() + paddleImage.getWidth()){
			pcPaddleSprite.setPosition(pcPaddleSprite.getX() + 600
					* Gdx.graphics.getDeltaTime(), pcPaddleSprite.getY());
			speed = -speed;
		} else if (ballSprite.getX() + ballImage.getWidth() - 10 < pcPaddleSprite.getX()){
			pcPaddleSprite.setPosition(pcPaddleSprite.getX() - 600
					* Gdx.graphics.getDeltaTime(), pcPaddleSprite.getY());
			speed = -speed;
		} else {
			if (count != 0) {
				pcPaddleSprite.setPosition(pcPaddleSprite.getX() + 5*speed*Gdx.graphics.getDeltaTime(),
						pcPaddleSprite.getY());
				count--;
			}
			if (count == 0) {
				rand1 = (int)(Math.random()*2);
				rand2 = (int)(Math.random() * 81) - 41 ;
				speed = rand2*rand1;
				rand3 = (int)(Math.random() * 5) + 5;

				count = rand3;
			}
		}

		// keep pc paddle inside boundaries
		if (pcPaddleSprite.getX() < 0)
			pcPaddleSprite.setPosition(0, pcPaddleSprite.getY());
		if (pcPaddleSprite.getX() > 640 - paddleImage.getWidth())
			pcPaddleSprite.setPosition(640 - paddleImage.getWidth(),
					pcPaddleSprite.getY());

		// debugRenderer.render(world, debugMatrix);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		ballImage.dispose();
		paddleImage.dispose();
		rectImage.dispose();
		sound.dispose();
		hit.dispose();
	}

}
