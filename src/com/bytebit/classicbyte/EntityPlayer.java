/*
Mineshaft
Copyright (C) 2014 ByteBit

This program is free software; you can redistribute it and/or modify it under the terms of
the GNU General Public License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program;
if not, see <http://www.gnu.org/licenses/>.
*/


package com.bytebit.classicbyte;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES10;

public class EntityPlayer {
	private float pos_x = 0.0F;
	private float pos_y = 0.0F;
	private float pos_z = 0.0F;
	
	private float yaw = 0.0F;
	private float pitch = 0.0F;
	
	private String name = "Steve";
	
	private String model_name = "humanoid";
	
	private int player_id = -1;
	
	private Cube model = new Cube();
	
	private boolean cached_skin = false;
	private Bitmap chached_skin_bitmap = null;
	
	public EntityPlayer(int id) {
		this.player_id = id;
	}
	
	public int getID() {
		return this.player_id;
	}
	
	public float getXPosition() {
		return this.pos_x;
	}
	
	public float getYPosition() {
		return this.pos_y;
	}
	
	public float getZPosition() {
		return this.pos_z;
	}
	
	public static Bitmap fetchSkin(String username) {
		Socket s = new Socket();
		try {
			s.connect(new InetSocketAddress("classicube.net",80), 3000);
			PrintWriter out = new PrintWriter(s.getOutputStream());
			out.println("GET /static/"+username+".png HTTP/1.1");
			out.println("Host: www.classicube.net");
			out.close();
			String content = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null)
			{
			    str.append(line);
			}
			reader.close();
			content = str.toString();
			
			System.err.println(content);
			
			s.close();
		} catch(Exception e) {
			Logger.log(null,"Could not download skin for player named "+username);
			Logger.log(null,"Using default skin instead (returning null)");
			return null;
		}
		
		return null;
	}
	
	public void setPosition(float x, float y, float z) {
		this.pos_x = x;
		this.pos_y = y;
		this.pos_z = z;
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public void setRotation(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String username) {
		this.name = username;
	}
	
	public void setPlayerModel(String modelname) {
		this.model_name = modelname;
	}
	
	public String getPlayerModel() {
		return this.model_name;
	}
	
	public void addQuad(float offset_x, float offset_y, float offset_z, float w, float h, float ax, float ay, float aw, float ah) {
		Tesselator.INSTANCE.glTexCoord2f(ax, ay);
		Tesselator.INSTANCE.glVertex3f(0.0F+offset_x, 0.0F+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax+aw, ay);
		Tesselator.INSTANCE.glVertex3f(w+offset_x, 0.0F+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax+aw, ay+ah);
		Tesselator.INSTANCE.glVertex3f(w+offset_x, h+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax, ay);
		Tesselator.INSTANCE.glVertex3f(0.0F+offset_x, 0.0F+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax, ay+ah);
		Tesselator.INSTANCE.glVertex3f(0.0F+offset_x, h+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax+aw, ay+ah);
		Tesselator.INSTANCE.glVertex3f(w+offset_x, h+offset_y, 0.0F+offset_z);
	}
	
	public void addQuad2(float offset_x, float offset_y, float offset_z, float w, float h, float ax, float ay, float aw, float ah) {
		Tesselator.INSTANCE.glTexCoord2f(ax, ay);
		Tesselator.INSTANCE.glVertex3f(0.0F+offset_x, 0.0F+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax+aw, ay);
		Tesselator.INSTANCE.glVertex3f(w+offset_x, 0.0F+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax+aw, ay+ah);
		Tesselator.INSTANCE.glVertex3f(w+offset_x, offset_y, h+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax, ay);
		Tesselator.INSTANCE.glVertex3f(0.0F+offset_x, 0.0F+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax, ay+ah);
		Tesselator.INSTANCE.glVertex3f(0.0F+offset_x, offset_y, h+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax+aw, ay+ah);
		Tesselator.INSTANCE.glVertex3f(w+offset_x, offset_y, h+offset_z);
	}
	
	public void addQuad3(float offset_x, float offset_y, float offset_z, float w, float h, float ax, float ay, float aw, float ah) {
		ax = ax + aw;
		ay = ay + ah;
		aw = -aw;
		ah = -ah;
		Tesselator.INSTANCE.glTexCoord2f(ax, ay);
		Tesselator.INSTANCE.glVertex3f(0.0F+offset_x, 0.0F+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax+aw, ay);
		Tesselator.INSTANCE.glVertex3f(offset_x, w+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax+aw, ay+ah);
		Tesselator.INSTANCE.glVertex3f(offset_x, w+offset_y, h+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax, ay);
		Tesselator.INSTANCE.glVertex3f(0.0F+offset_x, 0.0F+offset_y, 0.0F+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax, ay+ah);
		Tesselator.INSTANCE.glVertex3f(0.0F+offset_x, offset_y, h+offset_z);
		Tesselator.INSTANCE.glTexCoord2f(ax+aw, ay+ah);
		Tesselator.INSTANCE.glVertex3f(offset_x, w+offset_y, h+offset_z);
	}
	
	public void render(GL10 gl) {
		if(!this.cached_skin) {
			Bitmap skin = null;//EntityPlayer.fetchSkin(this.getName());
			if(skin==null) {
				skin = TextureManager.getBitmap(31);
			}
			this.chached_skin_bitmap = skin;
			this.cached_skin = true;
		}
		if(this.chached_skin_bitmap!=null) {
			TextureManager.loadTextureFromBitmap(this.chached_skin_bitmap, 127);
			TextureManager.bindTexture(127);
		}
		GLES10.glPushMatrix();
		GLES10.glTranslatef(this.getXPosition()-0.5F,this.getYPosition()+2.0F,this.getZPosition()-0.5F);
		GLES10.glRotatef(180.0F,1.0F,0.0F,0.0F);
		GLES10.glRotatef(this.yaw,0.0F,1.0F,0.0F);
		GLES10.glTranslatef(-0.5F,0.0F,-0.5F);
		
		float a = 1.0F/16.0F;
		
		float c = 1.0F/64.0F;
		float d = 1.0F/32.0F;
		
		Tesselator.INSTANCE.glBegin(GLES10.GL_TRIANGLES);
		this.addQuad(4*a,8*a,a*4,8*a,12*a, c*20, d*20, c*8, d*12); //body front
		this.addQuad(0*a,8*a,a*4,4*a,12*a, c*44, d*20, c*4, d*12); //left arm front
		this.addQuad(12*a,8*a,a*4,4*a,12*a, c*44, d*20, c*4, d*12); //right arm front
		this.addQuad(4*a,20*a,a*4,4*a,12*a, c*4, d*20, c*4, d*12); //left leg front
		this.addQuad(8*a,20*a,a*4,4*a,12*a, c*4, d*20, c*4, d*12); //right leg front
		this.addQuad(4*a,0*a,a*6,8*a,8*a, c*8, d*8, c*8, d*8); //head front
		Tesselator.INSTANCE.glEnd();
		
		
		
		Tesselator.INSTANCE.glBegin(GLES10.GL_TRIANGLES);
		this.addQuad(4*a,8*a,0,8*a,12*a, c*32, d*20, c*8, d*12); //body back
		this.addQuad(0*a,8*a,0,4*a,12*a, c*52, d*20, c*4, d*12); //left arm back
		this.addQuad(12*a,8*a,0,4*a,12*a, c*52, d*20, c*4, d*12); //right arm back
		this.addQuad(4*a,20*a,0,4*a,12*a, c*12, d*20, c*4, d*12); //left leg back
		this.addQuad(8*a,20*a,0,4*a,12*a, c*12, d*20, c*4, d*12); //right leg back
		this.addQuad(4*a,0*a,a*(-2),8*a,8*a, c*24, d*8, c*8, d*8); //head back
		Tesselator.INSTANCE.glEnd();
		
		Tesselator.INSTANCE.glBegin(GLES10.GL_TRIANGLES);
		this.addQuad2(4*a,0*a,(-2)*a,8*a,8*a, c*8, d*0, c*8, d*8); //head up
		this.addQuad2(4*a,8*a,(-2)*a,8*a,8*a, c*16, d*0, c*8, d*8); //head down
		this.addQuad2(0*a,8*a,0*a,4*a,4*a, c*44, d*16, c*4, d*4); //left arm up
		this.addQuad2(0*a,20*a,0*a,4*a,4*a, c*48, d*16, c*4, d*4); //left arm down
		this.addQuad2(12*a,8*a,0*a,4*a,4*a, c*44, d*16, c*4, d*4); //right arm up
		this.addQuad2(12*a,20*a,0*a,4*a,4*a, c*48, d*16, c*4, d*4); //right arm down
		this.addQuad2(4*a,8*a,0*a,8*a,4*a, c*20, d*16, c*8, d*4); //body up
		this.addQuad2(4*a,20*a,0*a,8*a,4*a, c*28, d*16, c*8, d*4); //body down
		this.addQuad2(4*a,20*a,0*a,4*a,4*a, c*4, d*16, c*4, d*4); //left leg up
		this.addQuad2(4*a,32*a,0*a,4*a,4*a, c*8, d*16, c*4, d*4); //left leg down
		this.addQuad2(8*a,20*a,0*a,4*a,4*a, c*4, d*16, c*4, d*4); //right leg up
		this.addQuad2(8*a,32*a,0*a,4*a,4*a, c*8, d*16, c*4, d*4); //left leg up
		Tesselator.INSTANCE.glEnd();
		
		Tesselator.INSTANCE.glBegin(GLES10.GL_TRIANGLES);
		this.addQuad3(4*a, 0*a, (-2)*a, 8*a, 8*a, c*0, d*8, c*8, d*8);
		this.addQuad3(12*a, 0*a, (-2)*a, 8*a, 8*a, c*16, d*8, c*8, d*8);
		Tesselator.INSTANCE.glEnd();
		
		GLES10.glPopMatrix();
	}
}
