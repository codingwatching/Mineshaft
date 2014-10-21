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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

public class ScreenMainMenu extends Screen {
	
	private int menu_button_highlighted = 0;
	private int menu_current_tab = 1;
	
	private List<String> server_list_name = new ArrayList<String>();
	private List<String> server_list_data = new ArrayList<String>();
	private List<String> server_list_online_data = new ArrayList<String>();
	public int server_list_y_offset = 0;
	public int server_list_y_offset_static = 0;
	public boolean server_list_moving = false;
	
	public int scrollbar_offset = 0;
	public int scrollbar_offset_static = 0;
	public boolean scrollbar_moving = false;

	public ScreenMainMenu(ClassicByteView v) {
		super(v);
		this.refreshServerList();
	}

	public void draw(Canvas c) {
		this.parent.standard_paint.setColor(Color.WHITE);
		
	    for(int i=0;i!=9;i++) {
	    	for(int k=0;k!=10;k++) {
	    		c.drawBitmap(TextureManager.getBitmap(3), k*(this.parent.renderer.width/10), (i+1)*(this.parent.renderer.width/10), this.parent.standard_paint);
	    	}
	    }
	    
	    if(this.menu_current_tab==1) {
	    	Rect bounds = new Rect();
	    	String g;
	    	for(int k=0;k!=this.server_list_name.size();k++) {
	    		if(k>=this.server_list_name.size()) {
	    			break;
	    		}
	    		if((this.parent.renderer.width*0.12F+this.parent.renderer.width*0.1F*k+this.server_list_y_offset)>=0 && (this.parent.renderer.width*0.12F+this.parent.renderer.width*0.1F*k+this.server_list_y_offset)<=this.parent.renderer.height) {
	    			this.parent.standard_paint.setColor(Color.WHITE);
	    			this.parent.standard_paint.setTextSize(this.parent.renderer.width*0.04F);
	    			c.drawBitmap(TextureManager.getBitmap(10), (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.12F+this.parent.renderer.width*0.1F*k+this.server_list_y_offset, this.parent.standard_paint);
	    			g = this.server_list_name.get(k);
	    			this.parent.standard_paint.getTextBounds(this.server_list_name.get(k), 0, this.server_list_name.get(k).length(), bounds);
	    			if(bounds.width()>TextureManager.getBitmap(10).getWidth()*0.85F) {
	    				while(bounds.width()>TextureManager.getBitmap(10).getWidth()*0.85F) {
	    					this.parent.standard_paint.getTextBounds(g+" ..", 0, (g+" ..").length(), bounds);
	    					g = g.substring(0,g.length()-1);
	    				}
	    				g = g + " ..";
	    			}
	    			c.drawText(g, (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.12F+this.parent.renderer.width*0.1F*k+this.server_list_y_offset, this.parent.standard_paint);
	    			this.parent.standard_paint.setColor(Color.YELLOW);
	    			this.parent.standard_paint.getTextBounds(this.server_list_online_data.get(k), 0, this.server_list_online_data.get(k).length(), bounds);
	    			c.drawText(this.server_list_online_data.get(k), this.parent.renderer.width-(this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2-this.parent.renderer.width*0.02F-bounds.width(), this.parent.renderer.width*0.08F+this.parent.renderer.width*0.12F+this.parent.renderer.width*0.1F*k+this.server_list_y_offset, this.parent.standard_paint);
	    		}
	    	}
	    	c.drawBitmap(TextureManager.getBitmap(26), 0, TextureManager.getBitmap(2).getHeight()+TextureManager.getBitmap(22).getHeight()+this.scrollbar_offset, this.parent.standard_paint);
		    if(this.menu_button_highlighted==3) {
		    	c.drawBitmap(TextureManager.getBitmap(25), 0, TextureManager.getBitmap(2).getHeight(), this.parent.standard_paint);
		    } else {
		    	c.drawBitmap(TextureManager.getBitmap(24), 0, TextureManager.getBitmap(2).getHeight(), this.parent.standard_paint);
		    }
		    if(this.menu_button_highlighted==4) {
		    	c.drawBitmap(TextureManager.getBitmap(23), 0, this.parent.renderer.height-TextureManager.getBitmap(22).getHeight(), this.parent.standard_paint);
		    } else {
		    	c.drawBitmap(TextureManager.getBitmap(22), 0, this.parent.renderer.height-TextureManager.getBitmap(22).getHeight(), this.parent.standard_paint);
		    }
	    }
	    if(this.menu_current_tab==2) {
	    	Rect bounds = new Rect();
	    	this.parent.standard_paint.setColor(Color.WHITE);
	    	this.parent.standard_paint.setTextSize(this.parent.renderer.height*0.25F);
	    	this.parent.standard_paint.getTextBounds("Nothing here", 0, "Nothing here".length(), bounds);
	    	c.drawText("Nothing here", (this.parent.renderer.width-bounds.width())/2, this.parent.renderer.height*0.5F, this.parent.standard_paint);
	    }
	    
	    for(int k=0;k!=10;k++) {
	    	c.drawBitmap(TextureManager.getBitmap(2), k*TextureManager.getBitmap(2).getWidth(), 0, this.parent.standard_paint);
	    }
	    
	    c.drawBitmap(TextureManager.getBitmap(4), this.parent.renderer.width*0.05F, 0, this.parent.standard_paint);
	    if(this.menu_button_highlighted==1) {
	    	c.drawBitmap(TextureManager.getBitmap(8), this.parent.renderer.width*0.1F*6.5F, this.parent.renderer.width*0.01F, this.parent.standard_paint);
	    } else {
	    	c.drawBitmap(TextureManager.getBitmap(7), this.parent.renderer.width*0.1F*6.5F, this.parent.renderer.width*0.01F, this.parent.standard_paint);
	    }
	    if(this.menu_button_highlighted==2) {
	    	c.drawBitmap(TextureManager.getBitmap(6), this.parent.renderer.width*0.1F*8.75F, this.parent.renderer.width*0.01F, this.parent.standard_paint);
	    } else {
	    	c.drawBitmap(TextureManager.getBitmap(5), this.parent.renderer.width*0.1F*8.75F, this.parent.renderer.width*0.01F, this.parent.standard_paint);
	    }
	    
	    //c.drawBitmap(TextureManager.getBitmap(9), this.parent.renderer.width-TextureManager.getBitmap(9).getWidth(), this.parent.renderer.height-TextureManager.getBitmap(9).getHeight(), this.parent.standard_paint);
	}
	
	private int getAsPositive(int x) {
		if(x<0) {
			return -x;
		}
		return x;
	}
	
	public void refreshServerList() {
		try {
			HttpClient client = LoginManager.INSTANCE.client;
			HttpGet request = new HttpGet("http://www.classicube.net/server/list/");
			HttpResponse response = client.execute(request);

			String html = "";
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null)
			{
			    str.append(line);
			}
			in.close();
			html = str.toString();
			int search_offset = html.indexOf("<table id=\"servers\">");
			int end_offset = html.indexOf("</table>",search_offset);
			this.server_list_name.clear();
			this.server_list_data.clear();
			this.server_list_name.add("Localhost at 10.0.2.2");
			this.server_list_data.add("10.0.2.2:25565/"+LoginManager.INSTANCE.username+"/################################");
			this.server_list_online_data.add("");
			this.server_list_name.add("Localhost at 192.168.178.36");
			this.server_list_data.add("192.168.178.36:25565/"+LoginManager.INSTANCE.username+"/################################");
			this.server_list_online_data.add("");
			while(true) {
				if(search_offset>=end_offset) {
					break;
				}
				int start = html.indexOf("<strong>",search_offset);
				if(start==-1) {
					break;
				}
				start = html.indexOf("\">",start);
				int end = html.indexOf("</a></strong>",start);
				search_offset = html.indexOf("</noscript>",end);
				String server_name = new String(html.substring(start+2,end).replaceAll("&#39;", "'"));
				String server_ip_string = new String(html.substring(html.indexOf("<strong><a href=\"mc://",end)+22,html.indexOf("\">Direct",html.indexOf("<strong><a href=\"mc://",end))));
				int online_players = Integer.parseInt(html.substring(html.indexOf("<td class=\"players\">[",end)+21,html.indexOf("/",html.indexOf("<td class=\"players\">[",end))));
				int max_players = Integer.parseInt(html.substring(html.indexOf("["+online_players+"/",end)+("["+online_players+"/").length(),html.indexOf("]",html.indexOf("["+online_players+"/",end))));
				this.server_list_name.add(server_name);
				this.server_list_data.add(server_ip_string);
				this.server_list_online_data.add(online_players+"/"+max_players);
				Logger.log(this, server_name);
				Logger.log(this, "Online: "+online_players+"/"+max_players);
				Logger.log(this, server_ip_string);
			}
			this.server_list_y_offset = 0;
			this.server_list_y_offset_static = 0;
			this.scrollbar_offset = 0;
			this.scrollbar_offset_static = 0;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onTouch(int x, int y, boolean up, boolean down) {
		if(down && x>=0 && x<this.parent.renderer.width && y<this.parent.renderer.height && y>this.parent.renderer.width*0.12F && x>TextureManager.getBitmap(25).getWidth()) {
			this.server_list_y_offset_static = y;
			this.server_list_moving = true;
		}
		
		if(this.server_list_moving && this.menu_current_tab==1 &&!down && up && x>=0 && x<this.parent.renderer.width && y<this.parent.renderer.width && y>this.parent.renderer.width*0.12F && x>TextureManager.getBitmap(25).getWidth()) {
	    	for(int k=0;k!=this.server_list_name.size();k++) {
	    		if(k>=this.server_list_name.size()) {
	    			break;
	    		}
	    		int xpos = (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2;
	    		int ypos = (int) (this.parent.renderer.width*0.02F+this.parent.renderer.width*0.12F+this.parent.renderer.width*0.1F*k+this.server_list_y_offset);
	    		if((this.parent.renderer.width*0.12F+this.parent.renderer.width*0.1F*k+this.server_list_y_offset)>=0 && (this.parent.renderer.width*0.12F+this.parent.renderer.width*0.1F*k+this.server_list_y_offset)<=this.parent.renderer.height && x>xpos && x<xpos+TextureManager.getBitmap(10).getWidth() && y>ypos && y<ypos+TextureManager.getBitmap(10).getHeight()) {
	    			this.parent.renderer.chatmananger.clear();
	    			this.parent.setScreen(new ServerSelectedScreen(this.parent, this.server_list_name.get(k), this.server_list_data.get(k), true));
	    		}
	    	}
	    }
		
		if(!down && !up && x>=0 && x<this.parent.renderer.width && y<this.parent.renderer.height && y>this.parent.renderer.width*0.12F && x>TextureManager.getBitmap(25).getWidth() && this.getAsPositive(this.server_list_y_offset_static-y)>=Options.pointer_size*0.125F) {
			if(this.server_list_y_offset_static-y>0) {
				this.server_list_y_offset = this.server_list_y_offset - this.getAsPositive(this.server_list_y_offset_static-y);
			}
			if(this.server_list_y_offset_static-y<0) {
				this.server_list_y_offset = this.server_list_y_offset + this.getAsPositive(this.server_list_y_offset_static-y);
			}
			if(this.server_list_y_offset>0) {
				this.server_list_y_offset = 0;
			}
			if(this.server_list_y_offset<-(this.server_list_name.size()*this.parent.renderer.width*0.1F+this.parent.renderer.width/10-this.parent.renderer.height+this.parent.renderer.width*0.1F)) {
				this.server_list_y_offset = (int) (-(this.server_list_name.size()*this.parent.renderer.width*0.1F+this.parent.renderer.width/10-this.parent.renderer.height+this.parent.renderer.width*0.1F));
			}
			this.server_list_y_offset_static = y;
			float p = (this.parent.renderer.height-TextureManager.getBitmap(22).getHeight())-(TextureManager.getBitmap(2).getHeight()+TextureManager.getBitmap(22).getHeight())-TextureManager.getBitmap(26).getHeight();
			this.scrollbar_offset = Math.round(p*(this.server_list_y_offset/(-(this.server_list_name.size()*this.parent.renderer.width*0.1F+this.parent.renderer.width/10-this.parent.renderer.height+this.parent.renderer.width*0.1F))));
			this.server_list_moving = false;
		}
		
		
		//------------ SCROLLBAR STUFF --------------------
		if(down && x>=0 && x<this.parent.renderer.width && y<this.scrollbar_offset+TextureManager.getBitmap(26).getHeight()+TextureManager.getBitmap(2).getHeight()+TextureManager.getBitmap(25).getHeight() && y>this.scrollbar_offset+TextureManager.getBitmap(2).getHeight()+TextureManager.getBitmap(25).getHeight() && x<TextureManager.getBitmap(25).getWidth()) {
			this.scrollbar_offset_static = y;
			this.scrollbar_moving = true;
		}
		
		if(!down && !up && x>=0 && x<this.parent.renderer.width && y<this.scrollbar_offset+TextureManager.getBitmap(26).getHeight()+TextureManager.getBitmap(2).getHeight()+TextureManager.getBitmap(25).getHeight() && y>this.scrollbar_offset+TextureManager.getBitmap(2).getHeight()+TextureManager.getBitmap(25).getHeight() && x<TextureManager.getBitmap(25).getWidth()) {
			if(this.scrollbar_offset_static-y>0) {
				this.scrollbar_offset = this.scrollbar_offset - this.getAsPositive(this.scrollbar_offset_static-y);
			}
			if(this.scrollbar_offset_static-y<0) {
				this.scrollbar_offset = this.scrollbar_offset + this.getAsPositive(this.scrollbar_offset_static-y);
			}
			if(this.scrollbar_offset<0) {
				this.scrollbar_offset = 0;
			}
			float p = (this.parent.renderer.height-TextureManager.getBitmap(22).getHeight())-(TextureManager.getBitmap(2).getHeight()+TextureManager.getBitmap(22).getHeight())-TextureManager.getBitmap(26).getHeight();
			if(this.scrollbar_offset>p) {
				this.scrollbar_offset = Math.round(p);
			}
			this.scrollbar_offset_static = y;
			float c = this.scrollbar_offset/p;
			this.server_list_y_offset = Math.round(c*(-(this.server_list_name.size()*this.parent.renderer.width*0.1F+this.parent.renderer.width/10-this.parent.renderer.height+this.parent.renderer.width*0.1F)));
			this.scrollbar_moving = false;
		}
		
		
		//------------------------------------------------
		
		int flag_001 = 0;
		
		if(x>this.parent.renderer.width*0.1F*6.5F && x<this.parent.renderer.width*0.1F*6.5F+TextureManager.getBitmap(8).getWidth() && y>this.parent.renderer.width*0.01F && y<this.parent.renderer.width*0.01F+TextureManager.getBitmap(8).getHeight()) {
			if(!up) {
				flag_001 = 1;
			} else {
				this.menu_current_tab = 1;
				this.refreshServerList();
			}
		}
		
		if(x>this.parent.renderer.width*0.1F*8.75F && x<this.parent.renderer.width*0.1F*8.75F+TextureManager.getBitmap(5).getWidth() && y>this.parent.renderer.width*0.01F && y<this.parent.renderer.width*0.01F+TextureManager.getBitmap(5).getHeight()) {
			if(!up) {
				flag_001 = 2;
			} else {
				this.menu_current_tab = 2;
				//this.parent.getUserInput("Enter your username", "", false, "ask_for_password");
				//Just do nothing
			}
		}
		
		if(x>0 && x<TextureManager.getBitmap(25).getWidth() && y>TextureManager.getBitmap(2).getHeight() && y<TextureManager.getBitmap(2).getHeight()+TextureManager.getBitmap(25).getHeight()) {
			if(!up) {
				flag_001 = 3;
			} else {
				this.server_list_y_offset = 0;
				this.scrollbar_offset = 0;
			}
		}
		
		if(x>0 && x<TextureManager.getBitmap(22).getWidth() && y>this.parent.renderer.height-TextureManager.getBitmap(22).getHeight() && y<this.parent.renderer.height) {
			if(!up) {
				flag_001 = 4;
			} else {
				this.server_list_y_offset = (int) (-(this.server_list_name.size()*this.parent.renderer.width*0.1F+this.parent.renderer.width/10-this.parent.renderer.height+this.parent.renderer.width*0.1F));
				float p = (this.parent.renderer.height-TextureManager.getBitmap(22).getHeight())-(TextureManager.getBitmap(2).getHeight()+TextureManager.getBitmap(22).getHeight())-TextureManager.getBitmap(26).getHeight();
				this.scrollbar_offset = Math.round(p*(this.server_list_y_offset/(-(this.server_list_name.size()*this.parent.renderer.width*0.1F+this.parent.renderer.width/10-this.parent.renderer.height+this.parent.renderer.width*0.1F))));
			}
		}
		
		this.menu_button_highlighted = flag_001;
	}
}
