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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

public class ServerSelectedScreen extends Screen {

	public String server_name = "A nice server";
	public String server_ip = "127.0.0.1";
	public int server_port = 25565;
	public boolean map_initalized = false;
	private Bitmap map_overview;
	private boolean close_screen = false;
	private String hack_state = "";
	
	public ServerSelectedScreen(ClassicByteView v, String server_name, String server_data, boolean connect) {
		super(v);
		this.server_name = server_name;
		this.server_ip = server_data.substring(0,server_data.indexOf(":"));
		this.server_port = (int)Long.parseLong(server_data.substring(server_data.indexOf(":")+1,server_data.indexOf("/")));
		String name = server_data.substring(server_data.indexOf("/")+1,server_data.indexOf("/",server_data.indexOf("/")+1));
		if(connect) {
			boolean success = this.parent.renderer.networkManager.connect(this.server_ip, this.server_port, name, server_data.substring(server_data.indexOf("/",server_data.indexOf("/")+1)+1));
			if(!success) {
				this.parent.renderer.networkManager.disconnect();
				this.close_screen = true;
				((ClassicByte)ClassicByte.view.getContext()).openAlert("Sorry", "The server you're trying to connect to is probably down. Please ask the server host for help.", true, "just_close");
			}
		}
	}

	public void draw(Canvas c) {
		if(this.close_screen) {
			this.parent.setScreen(new ScreenMainMenu(this.parent));
		}
		
		this.parent.standard_paint.setColor(Color.WHITE);
		
	    for(int i=0;i!=9;i++) {
	    	for(int k=0;k!=10;k++) {
	    		c.drawBitmap(TextureManager.getBitmap(3), k*(this.parent.renderer.width/10), (i+1)*(this.parent.renderer.width/10), this.parent.standard_paint);
	    	}
	    }
	    
	    for(int k=0;k!=10;k++) {
	    	c.drawBitmap(TextureManager.getBitmap(2), k*TextureManager.getBitmap(2).getWidth(), 0, this.parent.standard_paint);
	    }
	    
	    c.drawBitmap(TextureManager.getBitmap(4), this.parent.renderer.width*0.05F, 0, this.parent.standard_paint);
		
		//this.parent.standard_paint.setColor(Color.WHITE);
		this.parent.standard_paint.setTextSize(this.parent.renderer.width*0.04F);
		c.drawBitmap(TextureManager.getBitmap(10), (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.12F, this.parent.standard_paint);
		c.drawText(this.server_name, (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.12F, this.parent.standard_paint);
		c.drawBitmap(TextureManager.getBitmap(11), (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+TextureManager.getBitmap(10).getWidth()-TextureManager.getBitmap(11).getWidth(), this.parent.renderer.width*0.02F+this.parent.renderer.width*0.12F+this.parent.renderer.width*0.10F, this.parent.standard_paint);
		c.drawBitmap(TextureManager.getBitmap(21), (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.12F+this.parent.renderer.width*0.10F,this.parent.standard_paint);
		if(!this.parent.renderer.networkManager.map_completed && !this.map_initalized) {
			Rect bounds = new Rect();
			String f = this.parent.renderer.networkManager.map_complete_in_percent+"%";
			this.parent.standard_paint.getTextBounds(f, 0, f.length(), bounds);
			int h = (int)(bounds.width()*0.5F);
			c.drawText(f,(this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+TextureManager.getBitmap(10).getWidth()-TextureManager.getBitmap(11).getWidth()+TextureManager.getBitmap(11).getWidth()*0.05F+Math.round(TextureManager.getBitmap(11).getWidth()*0.9F)*0.5F-h, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.12F+this.parent.renderer.width*0.10F+TextureManager.getBitmap(11).getHeight()*0.05F+this.parent.standard_paint.getTextSize()*0.5F+Math.round(TextureManager.getBitmap(11).getWidth()*0.9F)*0.5F, this.parent.standard_paint);
		}
		
		if(this.parent.renderer.networkManager.map_completed && !this.map_initalized) {
			this.map_overview = Bitmap.createBitmap(this.parent.renderer.world.x_size, this.parent.renderer.world.z_size, Config.ARGB_8888);
			for(int z=0;z!=this.parent.renderer.world.z_size;z++) {
				for(int x=0;x!=this.parent.renderer.world.x_size;x++) {
					int a = this.parent.renderer.world.y_size;
					for(int k=this.parent.renderer.world.y_size;k!=0;k--) {
						a = this.parent.renderer.world.getBlock(x, k, z);
						if(a!=Block.BLOCK_AIR && a!=Block.BLOCK_GLASS) {
							break;
						}
					}
					int pos = Block.getBlockPositionInTextureMap(a);
					int r = (pos >> 16) & 0xFF;
					int g = (pos >> 8) & 0xFF;
					//int b = (pos >> 0) & 0xFF;
					int color = TextureManager.getBitmap(12).getPixel(r, g);
					this.map_overview.setPixel(x, z, color);
				}
			}
			this.map_overview = Bitmap.createScaledBitmap(this.map_overview, Math.round(TextureManager.getBitmap(11).getWidth()*0.9F), Math.round(TextureManager.getBitmap(11).getHeight()*0.9F), true);
			this.map_initalized = true;
		}
		if(this.parent.renderer.networkManager.map_completed) {
			c.drawBitmap(this.map_overview, (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+TextureManager.getBitmap(10).getWidth()-TextureManager.getBitmap(11).getWidth()+TextureManager.getBitmap(11).getWidth()*0.05F, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.12F+this.parent.renderer.width*0.10F+TextureManager.getBitmap(11).getHeight()*0.05F, this.parent.standard_paint);
		}
		
		this.parent.standard_paint.setTextSize(this.parent.renderer.width*0.03F);
		String motd = ClassicByte.view.renderer.networkManager.current_server_motd;
		this.hack_state = "Rules: none";
		if(motd.contains("hax")) {
			this.hack_state = "Rules: "; 
		}
		if(motd.contains("+ophax")) {
			this.hack_state = this.hack_state+"ops, ";
		}
		if(motd.contains("+hax")) {
			this.hack_state = this.hack_state+"everyone, ";
		}
		if(motd.contains("-hax")) {
			this.hack_state = this.hack_state+"nobody, ";
		}
		if(motd.contains("hax")) {
			this.hack_state = this.hack_state+"can fly";
		}
		c.drawText(motd, (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.12F+this.parent.standard_paint.getTextSize()*3.0F, this.parent.standard_paint);
		//c.drawText(motd, (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.12F+this.parent.standard_paint.getTextSize()*4.0F, this.parent.standard_paint);
		c.drawText(this.hack_state, (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.12F+this.parent.standard_paint.getTextSize()*5.5F, this.parent.standard_paint);
		if(ClassicByte.view.renderer.player.isOp()) {
			c.drawText("Permission: op", (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.12F+this.parent.standard_paint.getTextSize()*6.5F, this.parent.standard_paint);
		} else {
			c.drawText("Permission: none", (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.12F+this.parent.standard_paint.getTextSize()*6.5F, this.parent.standard_paint);
		}
		c.drawText("Players: "+ClassicByte.view.renderer.networkManager.online_players+"/128", (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.12F+this.parent.standard_paint.getTextSize()*7.5F, this.parent.standard_paint);
	}
	
	public void onTouch(int x, int y, boolean up, boolean down) {
		if(!down && up && x<this.parent.renderer.width/2) {
			this.parent.renderer.networkManager.disconnect();
			this.parent.setScreen(new ScreenMainMenu(this.parent));
		}
		
		if(!down && up && x>this.parent.renderer.width/2 && this.parent.renderer.networkManager.map_completed) {
			this.parent.setScreen(null);
		}
	}
}
