/*
Ace of Spades remake
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

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.opengl.GLES10;

public class IngameRenderer {
	public static boolean isPaused = false;

	public static String CHAT_STATUS_1 = "";
	public static String CHAT_STATUS_2 = "";
	public static String CHAT_STATUS_3 = "";
	public static String CHAT_BOTTOMRIGHT_1 = "";
	public static String CHAT_BOTTOMRIGHT_2 = "";
	public static String CHAT_BOTTOMRIGHT_3 = "";
	public static String CHAT_ANNOUNCEMENT = "";
	public static String CHAT_TOPLEFT = "";
	
	public static int current_button = 0;
	
	public static Cube cube = new Cube();
	
	public static boolean show_block_selection = false;
	
	public static void render3D(GL10 gl) {
		int pos = Block.getBlockPositionInTextureMap(ClassicByte.view.renderer.player.getHeldBlock());
		int r = (pos >> 16) & 0xFF;
		int g = (pos >> 8) & 0xFF;
		
		IngameRenderer.cube.prepaireData((short)0, (short)0, (short)0, r, g);
		IngameRenderer.cube.draw(gl, 1);
		IngameRenderer.cube.draw(gl, 2);
		IngameRenderer.cube.draw(gl, 3);
		IngameRenderer.cube.draw(gl, 4);
		IngameRenderer.cube.draw(gl, 5);
		IngameRenderer.cube.draw(gl, 6);
	}
	
	public static void render2D(Canvas c) {
		ClassicByte.view.standard_paint.setColor(Color.WHITE);
		ClassicByte.view.standard_paint.setTextSize(ClassicByte.view.renderer.height*0.04F);
		IngameRenderer.CHAT_TOPLEFT = (char)14+"DEBUG: x: "+ClassicByte.view.renderer.player.getXPosition()+" y: "+ClassicByte.view.renderer.player.getYPosition()+" z: "+ClassicByte.view.renderer.player.getZPosition();
		IngameRenderer.drawMinecraftStyleTextColored(c, 0, (int)(ClassicByte.view.renderer.height*0.04F), IngameRenderer.CHAT_TOPLEFT, (int)(ClassicByte.view.renderer.height*0.006F));
		ClassicByte.view.standard_paint.setColor(Color.WHITE);
		Rect r = new Rect();
		ClassicByte.view.standard_paint.setTextSize(ClassicByte.view.renderer.height*0.08F);
		ClassicByte.view.standard_paint.getTextBounds(IngameRenderer.CHAT_ANNOUNCEMENT, 0, IngameRenderer.CHAT_ANNOUNCEMENT.length(), r);
		IngameRenderer.drawMinecraftStyleTextColored(c, (int)((ClassicByte.view.renderer.width-r.width())*0.5F), (int)(ClassicByte.view.renderer.height*0.16F), IngameRenderer.CHAT_ANNOUNCEMENT, (int)(ClassicByte.view.renderer.height*0.006F));
		ClassicByte.view.standard_paint.setTextSize(ClassicByte.view.renderer.height*0.04F);
		
		ClassicByte.view.standard_paint.getTextBounds(IngameRenderer.CHAT_STATUS_1, 0, IngameRenderer.CHAT_STATUS_1.length(), r);
		IngameRenderer.drawMinecraftStyleTextColored(c, ClassicByte.view.renderer.width-r.width(), (int)(ClassicByte.view.renderer.height*0.04F), IngameRenderer.CHAT_STATUS_1, (int)(ClassicByte.view.renderer.height*0.006F));
		ClassicByte.view.standard_paint.getTextBounds(IngameRenderer.CHAT_STATUS_2, 0, IngameRenderer.CHAT_STATUS_2.length(), r);
		IngameRenderer.drawMinecraftStyleTextColored(c, ClassicByte.view.renderer.width-r.width(), (int)(ClassicByte.view.renderer.height*0.04F)*2, IngameRenderer.CHAT_STATUS_2, (int)(ClassicByte.view.renderer.height*0.006F));
		ClassicByte.view.standard_paint.getTextBounds(IngameRenderer.CHAT_STATUS_3, 0, IngameRenderer.CHAT_STATUS_3.length(), r);
		IngameRenderer.drawMinecraftStyleTextColored(c, ClassicByte.view.renderer.width-r.width(), (int)(ClassicByte.view.renderer.height*0.04F)*3, IngameRenderer.CHAT_STATUS_3, (int)(ClassicByte.view.renderer.height*0.006F));
		
		ClassicByte.view.standard_paint.getTextBounds(IngameRenderer.CHAT_BOTTOMRIGHT_1, 0, IngameRenderer.CHAT_BOTTOMRIGHT_1.length(), r);
		IngameRenderer.drawMinecraftStyleTextColored(c, ClassicByte.view.renderer.width-r.width(), ClassicByte.view.renderer.height, IngameRenderer.CHAT_BOTTOMRIGHT_1, (int)(ClassicByte.view.renderer.height*0.006F));
		ClassicByte.view.standard_paint.getTextBounds(IngameRenderer.CHAT_BOTTOMRIGHT_2, 0, IngameRenderer.CHAT_BOTTOMRIGHT_2.length(), r);
		IngameRenderer.drawMinecraftStyleTextColored(c, ClassicByte.view.renderer.width-r.width(), ClassicByte.view.renderer.height-(int)(ClassicByte.view.renderer.height*0.04F), IngameRenderer.CHAT_BOTTOMRIGHT_2, (int)(ClassicByte.view.renderer.height*0.006F));
		ClassicByte.view.standard_paint.getTextBounds(IngameRenderer.CHAT_BOTTOMRIGHT_1, 0, IngameRenderer.CHAT_BOTTOMRIGHT_3.length(), r);
		IngameRenderer.drawMinecraftStyleTextColored(c, ClassicByte.view.renderer.width-r.width(), ClassicByte.view.renderer.height-(int)(ClassicByte.view.renderer.height*0.04F)*2, IngameRenderer.CHAT_BOTTOMRIGHT_1, (int)(ClassicByte.view.renderer.height*0.006F));
		
		
		//ClassicByte.view.standard_paint.setColor(Color.RED);
		//c.drawLine(ClassicByte.view.look_pointer_center_x, ClassicByte.view.look_pointer_center_y, ClassicByte.view.last_touch_x, ClassicByte.view.last_touch_y, ClassicByte.view.standard_paint);
		
		ClassicByte.view.standard_paint.setTextSize(ClassicByte.view.renderer.height*0.04F);
		
		ClassicByte.view.standard_paint.setColor(Color.WHITE);
		
		for(int k=11;k!=20;k++) {
			if(k>=ClassicByte.view.renderer.chatmananger.lines.size()) {
				break;
			}
			if(ClassicByte.view.renderer.chatmananger.lines.get(k)!=null && Options.colored_chat) {
				int char_pos = 0;
				String message = ClassicByte.view.renderer.chatmananger.lines_colored.get(k);
				int char_offset = 0;
				while(true && char_pos<ClassicByte.view.renderer.chatmananger.lines.get(k).length()) {
					char chr = message.charAt(char_pos);
					if(chr>15) {
						IngameRenderer.drawMinecraftStyleText(c, char_offset, (int)((k+1-11)*ClassicByte.view.renderer.height*0.04F+ClassicByte.view.renderer.height-10*ClassicByte.view.renderer.height*0.04F)-TextureManager.getBitmap(27).getHeight(), ""+chr, (int)(ClassicByte.view.renderer.height*0.006F));
						if(chr!=32) {
							Rect bounds = new Rect();
							ClassicByte.view.standard_paint.getTextBounds(chr+"", 0, 1, bounds);
							char_offset = (int)(char_offset + bounds.width());
							ClassicByte.view.standard_paint.getTextBounds("/", 0, 1, bounds);
							char_offset = (int)(char_offset + bounds.width()*0.2F);
						} else {
							Rect bounds = new Rect();
							ClassicByte.view.standard_paint.getTextBounds("r", 0, 1, bounds);
							char_offset = char_offset + bounds.width();
						}
					} else {
						if(chr==0)  { ClassicByte.view.standard_paint.setColor(0xFF000000); }
						if(chr==1)  { ClassicByte.view.standard_paint.setColor(0xFF0000AA); }
						if(chr==2)  { ClassicByte.view.standard_paint.setColor(0xFF00AA00); }
						if(chr==3)  { ClassicByte.view.standard_paint.setColor(0xFF00AAAA); }
						if(chr==4)  { ClassicByte.view.standard_paint.setColor(0xFFAA0000); }
						if(chr==5)  { ClassicByte.view.standard_paint.setColor(0xFFAA00AA); }
						if(chr==6)  { ClassicByte.view.standard_paint.setColor(0xFFFFAA00); }
						if(chr==7)  { ClassicByte.view.standard_paint.setColor(0xFFAAAAAA); }
						if(chr==8)  { ClassicByte.view.standard_paint.setColor(0xFF555555); }
						if(chr==9)  { ClassicByte.view.standard_paint.setColor(0xFF5555FF); }
						if(chr==10) { ClassicByte.view.standard_paint.setColor(0xFF55FF55); }
						if(chr==11) { ClassicByte.view.standard_paint.setColor(0xFF55FFFF); }
						if(chr==12) { ClassicByte.view.standard_paint.setColor(0xFFFF5555); }
						if(chr==13) { ClassicByte.view.standard_paint.setColor(0xFFFF55FF); }
						if(chr==14) { ClassicByte.view.standard_paint.setColor(0xFFFFFF55); }
						if(chr==15) { ClassicByte.view.standard_paint.setColor(0xFFFFFFFF); }
					}
					char_pos++;
					if(char_pos==message.length()) {
						break;
					}
				}
			}
			if(ClassicByte.view.renderer.chatmananger.lines.get(k)!=null && !Options.colored_chat) {
				IngameRenderer.drawMinecraftStyleText(c, 0, (int)((k+1-11)*ClassicByte.view.renderer.height*0.04F+ClassicByte.view.renderer.height-10*ClassicByte.view.renderer.height*0.04F)-TextureManager.getBitmap(27).getHeight(), ClassicByte.view.renderer.chatmananger.lines.get(k), (int)(ClassicByte.view.renderer.height*0.006F));
			}
		}
		
		ClassicByte.view.standard_paint.setColor(Color.WHITE);
		
		if(IngameRenderer.current_button==1) {
			//c.drawBitmap(TextureManager.getBitmap(28),0,ClassicByte.view.renderer.height-TextureManager.getBitmap(27).getHeight(),ClassicByte.view.standard_paint);
		} else {
			//c.drawBitmap(TextureManager.getBitmap(27),0,ClassicByte.view.renderer.height-TextureManager.getBitmap(27).getHeight(),ClassicByte.view.standard_paint);
		}
		
		if(IngameRenderer.current_button==2) {
			//c.drawBitmap(TextureManager.getBitmap(30),(ClassicByte.view.renderer.width-TextureManager.getBitmap(29).getWidth())/2,ClassicByte.view.renderer.height-TextureManager.getBitmap(29).getHeight(),ClassicByte.view.standard_paint);
		} else {
			//c.drawBitmap(TextureManager.getBitmap(29),(ClassicByte.view.renderer.width-TextureManager.getBitmap(29).getWidth())/2,ClassicByte.view.renderer.height-TextureManager.getBitmap(29).getHeight(),ClassicByte.view.standard_paint);
		}
		
		if(IngameRenderer.current_button==3) {
			ClassicByte.view.standard_paint.setColor(Color.WHITE);
			ClassicByte.view.standard_paint.setTextSize(ClassicByte.view.renderer.height*0.04F);
			int x_offset = -(int) (ClassicByte.view.renderer.width*0.075F);
			int y_offset = (int) (ClassicByte.view.renderer.height*0.08F);
			for(int k=0;k!=ClassicByte.view.renderer.players.size();k++) {
				if(k>=ClassicByte.view.renderer.players.size()) {
					return;
				}
				if(x_offset>ClassicByte.view.renderer.width*0.3F) {
					x_offset = -(int) (ClassicByte.view.renderer.width*0.075F);
					y_offset = (int) (y_offset + ClassicByte.view.renderer.height*0.06F);
				}
				String name = ClassicByte.view.renderer.players.get(k).getName();
				x_offset = (int) (x_offset + ClassicByte.view.renderer.width*0.3F);
				
				ClassicByte.view.standard_paint.setColor(0xAA555555);
				c.drawRect(x_offset-ClassicByte.view.renderer.width*0.01F, y_offset-ClassicByte.view.renderer.height*0.04F, x_offset+ClassicByte.view.renderer.width*0.28F, y_offset, ClassicByte.view.standard_paint);
				ClassicByte.view.standard_paint.setColor(Color.WHITE);
				
				IngameRenderer.drawMinecraftStyleText(c, x_offset, y_offset, name, (int)(ClassicByte.view.renderer.height*0.006F));
			}
			//c.drawBitmap(TextureManager.getBitmap(33),TextureManager.getBitmap(32).getHeight(),ClassicByte.view.renderer.height-TextureManager.getBitmap(32).getHeight(),ClassicByte.view.standard_paint);
		} else {
			//c.drawBitmap(TextureManager.getBitmap(32),TextureManager.getBitmap(32).getHeight(),ClassicByte.view.renderer.height-TextureManager.getBitmap(32).getHeight(),ClassicByte.view.standard_paint);
		}
		
		if(IngameRenderer.current_button==4) {
			c.drawBitmap(TextureManager.getBitmap(35),(ClassicByte.view.renderer.width-TextureManager.getBitmap(35).getWidth()),ClassicByte.view.renderer.height-TextureManager.getBitmap(35).getHeight(),ClassicByte.view.standard_paint);
		} else {
			c.drawBitmap(TextureManager.getBitmap(34),(ClassicByte.view.renderer.width-TextureManager.getBitmap(34).getWidth()),ClassicByte.view.renderer.height-TextureManager.getBitmap(34).getHeight(),ClassicByte.view.standard_paint);
		}
		
		c.drawBitmap(TextureManager.getBitmap(36), 0, ClassicByte.view.renderer.height-(int)(ClassicByte.view.renderer.width*0.2F), ClassicByte.view.standard_paint);
		c.drawBitmap(TextureManager.getBitmap(37), (int)((ClassicByte.view.renderer.width*0.2F-ClassicByte.view.renderer.width*0.08F)*0.5F), ClassicByte.view.renderer.height-(int)(ClassicByte.view.renderer.width*0.2F)+(int)((ClassicByte.view.renderer.width*0.2F-ClassicByte.view.renderer.width*0.08F)*0.5F), ClassicByte.view.standard_paint);
		
		if(IngameRenderer.show_block_selection) {
			ClassicByte.view.standard_paint.setColor(0xC8646487);
			c.drawRect(ClassicByte.view.renderer.width*0.15F, ClassicByte.view.renderer.height*0.1F, ClassicByte.view.renderer.width*0.85F, ClassicByte.view.renderer.height*0.8F, ClassicByte.view.standard_paint);
			ClassicByte.view.standard_paint.setColor(Color.WHITE);
			ClassicByte.view.standard_paint.setTextSize(ClassicByte.view.renderer.height*0.07F);
			ClassicByte.view.standard_paint.getTextBounds("Select block", 0, "Select block".length(), r);
			IngameRenderer.drawMinecraftStyleText(c, (int)((ClassicByte.view.renderer.width-r.width())*0.5F), (int)(ClassicByte.view.renderer.height*0.11F+ClassicByte.view.standard_paint.getTextSize()), "Select block", (int)(ClassicByte.view.renderer.height*0.006F));
		}
		
		if(IngameRenderer.isPaused) {
			c.drawColor(0xAAEEEEEE);
			ClassicByte.view.standard_paint.setColor(Color.WHITE);
			ClassicByte.view.standard_paint.setTextSize(ClassicByte.view.renderer.height*0.12F);
			ClassicByte.view.standard_paint.getTextBounds("Game menu", 0, "Game menu".length(), r);
			IngameRenderer.drawMinecraftStyleText(c, (int)((ClassicByte.view.renderer.width-r.width())*0.5F), (int)(ClassicByte.view.renderer.height*0.2F), "Game menu", (int)(ClassicByte.view.renderer.height*0.006F));
			ClassicByte.view.standard_paint.setTextSize(ClassicByte.view.renderer.height*0.04F);
			
			ClassicByte.view.standard_paint.setColor(Color.RED);
			ClassicByte.view.standard_paint.getTextBounds("Disconnect", 0, "Disconnect".length(), r);
			IngameRenderer.drawMinecraftStyleText(c, (int)((ClassicByte.view.renderer.width-r.width())*0.5F), (int)(ClassicByte.view.renderer.height*0.5F), "Disconnect", (int)(ClassicByte.view.renderer.height*0.006F));
			ClassicByte.view.standard_paint.setColor(Color.WHITE);
		}
	}
	
	public static void render2DSecondPass() {
		if(IngameRenderer.show_block_selection) {
			GLES10.glDepthRangef(0.0F, 0.01F);
			GLES10.glPushMatrix();
			GLES10.glScalef(0.4F, 0.4F, 0.4F);
			GLES10.glRotatef((float)((Math.sin(System.currentTimeMillis()*0.008))*0.1F),1.0F,1.0F,1.0F);
			IngameRenderer.cube.prepaireData(((short)0), ((short)0), ((short)0), 1, 0);
			IngameRenderer.cube.draw(null, 1);
			IngameRenderer.cube.draw(null, 2);
			IngameRenderer.cube.draw(null, 3);
			IngameRenderer.cube.draw(null, 4);
			IngameRenderer.cube.draw(null, 5);
			IngameRenderer.cube.draw(null, 6);
			GLES10.glPopMatrix();
			GLES10.glDepthRangef(0.0F, 1.0F);
		}
	}
	
	public static void drawMinecraftStyleTextColored(Canvas c, int pos_x, int pos_y, String text, int difference) {
		int char_pos = 0;
		String message = text;
		int char_offset = 0;
		ClassicByte.view.standard_paint.setColor(0xFFFFFFFF);
		while(true && char_pos<text.length()) {
			char chr = message.charAt(char_pos);
			if(chr>15) {
				IngameRenderer.drawMinecraftStyleText(c, char_offset+pos_x, pos_y, ""+chr, difference);
				if(chr!=32) {
					Rect bounds = new Rect();
					ClassicByte.view.standard_paint.getTextBounds(chr+"", 0, 1, bounds);
					char_offset = (int)(char_offset + bounds.width());
					ClassicByte.view.standard_paint.getTextBounds("/", 0, 1, bounds);
					char_offset = (int)(char_offset + bounds.width()*0.2F);
				} else {
					Rect bounds = new Rect();
					ClassicByte.view.standard_paint.getTextBounds("r", 0, 1, bounds);
					char_offset = char_offset + bounds.width();
				}
			} else {
				if(chr==0)  { ClassicByte.view.standard_paint.setColor(0xFF000000); }
				if(chr==1)  { ClassicByte.view.standard_paint.setColor(0xFF0000AA); }
				if(chr==2)  { ClassicByte.view.standard_paint.setColor(0xFF00AA00); }
				if(chr==3)  { ClassicByte.view.standard_paint.setColor(0xFF00AAAA); }
				if(chr==4)  { ClassicByte.view.standard_paint.setColor(0xFFAA0000); }
				if(chr==5)  { ClassicByte.view.standard_paint.setColor(0xFFAA00AA); }
				if(chr==6)  { ClassicByte.view.standard_paint.setColor(0xFFFFAA00); }
				if(chr==7)  { ClassicByte.view.standard_paint.setColor(0xFFAAAAAA); }
				if(chr==8)  { ClassicByte.view.standard_paint.setColor(0xFF555555); }
				if(chr==9)  { ClassicByte.view.standard_paint.setColor(0xFF5555FF); }
				if(chr==10) { ClassicByte.view.standard_paint.setColor(0xFF55FF55); }
				if(chr==11) { ClassicByte.view.standard_paint.setColor(0xFF55FFFF); }
				if(chr==12) { ClassicByte.view.standard_paint.setColor(0xFFFF5555); }
				if(chr==13) { ClassicByte.view.standard_paint.setColor(0xFFFF55FF); }
				if(chr==14) { ClassicByte.view.standard_paint.setColor(0xFFFFFF55); }
				if(chr==15) { ClassicByte.view.standard_paint.setColor(0xFFFFFFFF); }
			}
			char_pos++;
			if(char_pos==message.length()) {
				break;
			}
		}
	}
	
	public static void drawMinecraftStyleText(Canvas c, int pos_x, int pos_y, String text, int difference) {
		int color = ClassicByte.view.standard_paint.getColor();
		ClassicByte.view.standard_paint.setColor(Color.DKGRAY);
		c.drawText(text, pos_x+difference, pos_y+difference, ClassicByte.view.standard_paint);
		ClassicByte.view.standard_paint.setColor(color);
		c.drawText(text, pos_x, pos_y, ClassicByte.view.standard_paint);
	}
}
