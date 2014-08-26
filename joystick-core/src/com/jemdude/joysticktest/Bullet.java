package com.jemdude.joysticktest;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Bullet{
	
	Float xDirection, yDirection, xCoOrdinate, yCoOrdinate;
    Sprite bullet;
    
	public Bullet(Float xCo, Float yCo, Float xDir, Float yDir, Sprite bullet){
		this.xDirection = xDir;
		this.yDirection = yDir;
		this.xCoOrdinate = xCo;
		this.yCoOrdinate = yCo;
		this.bullet = bullet;
        this.bullet.setX(xCoOrdinate);
        this.bullet.setY(yCoOrdinate);
	}
	public boolean moveBullet(){
		this.xCoOrdinate += this.xDirection;
		this.yCoOrdinate += this.yDirection;
        this.bullet.setX(xCoOrdinate);
        this.bullet.setY(yCoOrdinate);
        if(this.bullet.getY() > 687 || this.bullet.getY() < -687 ||this.bullet.getX() > 1536 || this.bullet.getX() < -1536){
        	return true;
        }else{
        	return false;
        }
	}
}