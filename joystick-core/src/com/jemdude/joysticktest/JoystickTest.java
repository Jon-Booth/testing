package com.jemdude.joysticktest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Array;

public class JoystickTest implements ApplicationListener{
	Stage stage;
    private SpriteBatch batch;
	Touchpad touchpad;
	Sprite blockSprite, bulletSprite, backgroundSprite;
	Texture blockTexture, background, bulletTexture;
	private float blockSpeed, blockRotation, knobpercentX, knobpercentY, jetCurX, jetCurY;
	ImageButton shooter, circleshooter;
	Array<Bullet> bulletArray;
	OrthographicCamera camera;
	Integer firerate, screensize10, circlefirerate;
	public void create () {
		camera = new OrthographicCamera(1280, 720);
		batch = new SpriteBatch();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		shooter = new ImageButton(skin);
		circleshooter = new ImageButton(skin, "circleshot");
		touchpad = new Touchpad(0, skin);
		circleshooter.setBounds(Gdx.graphics.getWidth() - Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/7, Gdx.graphics.getWidth()/10, Gdx.graphics.getWidth()/10);
		shooter.setBounds(Gdx.graphics.getWidth() - Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()/4, Gdx.graphics.getWidth()/10, Gdx.graphics.getWidth()/10);
		touchpad.setBounds(Gdx.graphics.getWidth()/15, Gdx.graphics.getHeight()/15, Gdx.graphics.getWidth()/5, Gdx.graphics.getWidth()/5);
		touchpad.getStyle().knob.setMinHeight(Gdx.graphics.getWidth()/20);
		touchpad.getStyle().knob.setMinWidth(Gdx.graphics.getWidth()/20);
		stage.addActor(touchpad);
		stage.addActor(circleshooter);
		stage.addActor(shooter);
		circleshooter.addListener(new InputListener() {
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		        fireCircle();
		        return true;
		    }
		});
        blockTexture = new Texture(Gdx.files.internal("data/block.png"));
        background = new Texture(Gdx.files.internal("data/StarBackground.png"));
        bulletTexture = new Texture(Gdx.files.internal("data/bullet.png"));
        backgroundSprite = new Sprite(background);
        backgroundSprite.setOrigin(0,0);
        backgroundSprite.setPosition(-backgroundSprite.getWidth()/2,-backgroundSprite.getHeight()/2);
        blockSprite = new Sprite(blockTexture);
        blockSprite.setPosition(-blockSprite.getWidth()/2, -blockSprite.getHeight()/2);
        blockSpeed = 10;
        blockRotation = 0;
        firerate = 2;
        circlefirerate = 30;
        jetCurX = 0;
        jetCurY = 0;
        screensize10 = Gdx.graphics.getWidth()/5;
        bulletArray = new Array<Bullet>();
		bulletArray.add(new Bullet(blockSprite.getX(),blockSprite.getY(),(float)9999, (float)9999, new Sprite(bulletTexture)));  
	}

	public void render () {
		knobpercentX = touchpad.getKnobPercentX();
		knobpercentY = touchpad.getKnobPercentY();
		if(jetCurX + knobpercentX*blockSpeed > -1466 && jetCurX+ knobpercentX*blockSpeed < 1466){
			blockSprite.translate(knobpercentX*blockSpeed, 0);
		}
		if(jetCurY+ knobpercentY*blockSpeed > -615 && jetCurY+ knobpercentY*blockSpeed < 615){
			blockSprite.translate(0, knobpercentY*blockSpeed);
		}
		jetCurX = blockSprite.getX();
		jetCurY = blockSprite.getY();
	    Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(jetCurX > -860 && jetCurX < 860){
        	camera.translate(-camera.position.x, 0, 0);
        	camera.translate(jetCurX + blockSprite.getWidth()/2 , 0, 0);
        }
        if(jetCurY > -291 && jetCurY < 291){
        	//camera.translate(0, knobpercentY*blockSpeed);
        	camera.translate(0, -camera.position.y, 0);
        	camera.translate(0, jetCurY+ blockSprite.getHeight()/2, 0);
        }
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        backgroundSprite.draw(batch);
        blockSprite.draw(batch);
        batch.end();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		if(touchpad.isTouched()==true){
			//blockRotation = -1 * (float)( Math.toDegrees(Math.atan((knobpercentY)/(knobpercentX))) - 90);
			blockRotation = -1 * (float)(Math.atan((knobpercentY)/(knobpercentX)) - Math.PI/2);
			if(knobpercentX < 0){
				blockRotation += Math.PI;
			} 
		}
		Gdx.app.log("bullet number", Integer.toString(bulletArray.size));
		if(shooter.isPressed() == true){
			mainGunPressed();
		}		
		--firerate;
		--circlefirerate;
		batch.begin();
		moveAllBullets();
		batch.end();

        blockSprite.setRotation((float) (Math.toDegrees(blockRotation) * -1));
	}

	private void moveAllBullets() {
		// TODO Auto-generated method stub
		for(Bullet bullets:bulletArray){
			bullets.bullet.draw(batch);
			if(bullets.moveBullet()){
				bulletArray.removeIndex(bulletArray.indexOf(bullets, true));;
			}
			//if(bullets.bullet.getY() > 687 || bullets.bullet.getY() < -687 ||bullets.bullet.getX() > 1536 || bullets.bullet.getX() < -1536){
			//	bulletArray.removeIndex(bulletArray.indexOf(bullets, true));
			//}
		}
	}
	public void addBullet(float bulletxco, float bulletyco, float bulletxdir, float bulletydir, Sprite bullettype){
		bulletArray.add(new Bullet(bulletxco, bulletyco, bulletxdir, bulletydir, bullettype));

	}
	public void fireCircle(){
		if(firerate <= 0){
			addBullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation + Math.PI) * 20, (float)Math.cos(blockRotation + Math.PI) * 20, new Sprite(bulletTexture));
			addBullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation + Math.PI/2) * 20, (float)Math.cos(blockRotation + Math.PI/2) * 20, new Sprite(bulletTexture));
			addBullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation + Math.PI/4) * 20, (float)Math.cos(blockRotation + Math.PI/4) * 20, new Sprite(bulletTexture));
			addBullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation + Math.PI/4) * -20, (float)Math.cos(blockRotation + Math.PI/4) * -20, new Sprite(bulletTexture));
			addBullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation - Math.PI/2) * 20, (float)Math.cos(blockRotation - Math.PI/2) * 20, new Sprite(bulletTexture));
			addBullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation - Math.PI/4) * 20, (float)Math.cos(blockRotation - Math.PI/4) * 20, new Sprite(bulletTexture));
			addBullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation - Math.PI/4) * -20, (float)Math.cos(blockRotation - Math.PI/4) * -20, new Sprite(bulletTexture));
			addBullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation) * 20, (float)Math.cos(blockRotation) * 20, new Sprite(bulletTexture));
			firerate = 15;
		}
	}
	public void mainGunPressed(){
		if(firerate <= 0){
			addBullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation) * 20, (float)Math.cos(blockRotation) * 20, new Sprite(bulletTexture));
			//bulletArray.add(new Bullet((jetCurX + blockSprite.getWidth()/2),(jetCurY + blockSprite.getHeight()/2), (float)Math.sin(blockRotation) * 20, (float)Math.cos(blockRotation) * 20, new Sprite(bulletTexture)));
			firerate = 1;
		}
	}
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void dispose () {
		stage.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}