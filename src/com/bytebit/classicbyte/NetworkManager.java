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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import android.media.MediaPlayer;

public class NetworkManager implements Runnable {
	public String current_server_ip = "127.0.0.1";
	public int current_server_port = 25565;
	public String current_server_name = "A nice server";
	public String current_server_motd = "Message of the day";
	public String current_server_session = "################################################################";
	
	public int user_permission = 0x64; //0x00: normal user / 0x64: operator
	
	public boolean already_connected = false;
	public Socket client_socket = null;
	public DataInputStream in;
	public DataOutputStream out;
	private ByteArrayOutputStream map_data;
	
	private long position_update_timer = System.currentTimeMillis();
	
	public int map_complete_in_percent = 0;
	
	public boolean output_idle = false;
	
	public boolean map_completed = false;
	
	public ClassicByteRenderer parent;
	
	public int online_players = 0;
	
	private int server_extension_count = 0;
	private int current_extension_index = 0;
	
	private static String server_extensions = "";
	
	private MediaPlayer beep_sound;
	
	public NetworkManager(ClassicByteRenderer p) {
		this.parent = p;
	}
	
	public void disconnect() {
		if(this.already_connected) {
			if(this.output_idle) {
				while(ClassicByte.view.renderer.networkManager.output_idle) {}
			}
			try { this.client_socket.close(); } catch(Exception e) {}
			this.client_socket = null;
			this.already_connected = false;
		}
	}
	
	public boolean connect(String ip, int port, String username, String session_id) {
		return this.connect(ip, port, username, session_id, Options.time_out);
	}
	
	public boolean connect(String ip, int port, String username, String session_id, int time_out) {
		if(this.already_connected) {
			this.disconnect();
		}
		this.current_server_ip = ip;
		this.current_server_port = port;
		this.map_complete_in_percent = 0;
		this.online_players = 0;
		try {
			this.parent.players.clear();
			this.parent.players = new ArrayList<EntityPlayer>();
			
			this.parent.selections.clear();
			this.parent.selections = new ArrayList<Selection>();
			
			NetworkManager.deleteServerExtensions();
			
			this.parent.chatmananger.clear();
			IngameRenderer.CHAT_ANNOUNCEMENT = "";
			IngameRenderer.CHAT_BOTTOMRIGHT_1 = "";
			IngameRenderer.CHAT_BOTTOMRIGHT_2 = "";
			IngameRenderer.CHAT_BOTTOMRIGHT_3 = "";
			IngameRenderer.CHAT_STATUS_1 = "";
			IngameRenderer.CHAT_STATUS_2 = "";
			IngameRenderer.CHAT_STATUS_3 = "";
			IngameRenderer.CHAT_TOPLEFT = "";
			
			this.client_socket = new Socket();
			this.client_socket.connect(new InetSocketAddress(this.current_server_ip, this.current_server_port), time_out);
			this.in = new DataInputStream(this.client_socket.getInputStream());
			this.out = new DataOutputStream(this.client_socket.getOutputStream());
			
			this.parent.player.setName(username); //set the player's name
			this.sendPlayerIdentificationPacket(this.parent.player.getName(), session_id);
			
			this.map_completed = false;
			this.current_server_session = session_id;
			this.already_connected = true;
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			this.client_socket = null;
			this.current_server_ip = "127.0.0.1";
			this.current_server_port = 25565;
			this.current_server_session = "################################################################";
			this.already_connected = false;
			return false;
		}
	}
	
	public void sendPlayerIdentificationPacket(String name, String sessiontoken) throws Exception {
		if(this.output_idle) {
			while(ClassicByte.view.renderer.networkManager.output_idle) {}
		}
		this.output_idle = true;
		this.out.writeByte(0x00);
		this.out.writeByte(0x07); //protocol version 7
		this.writeString(name, this.out);
		this.writeString(sessiontoken, this.out); //session token
		this.out.writeByte(0x42); //0x00 for vanilla minecraft client without cpe/ 0x42 for a client which supports cpe
		this.output_idle = false;
	}
	
	public void sendCustomDataPacket(String data_name, byte[] data) throws Exception {
		if(!NetworkManager.hasServerExtension(Extension.EXTENSION_CUSTOM_DATA, 1)) {
			return;
		}
		if(this.output_idle) {
			while(ClassicByte.view.renderer.networkManager.output_idle) {}
		}
		this.output_idle = true;
		this.out.writeByte(0xFA);
		this.writeString(data_name, this.out);
		this.out.writeLong(data.length);
		this.out.write(data);
		this.output_idle = false;
	}
	
	public void sendChatPacket(String message) throws Exception {
		if(this.output_idle) {
			while(ClassicByte.view.renderer.networkManager.output_idle) {}
		}
		this.output_idle = true;
		this.out.writeByte(0x0D);
		this.out.writeByte(0xFF);
		this.writeString(message, this.out);
		this.output_idle = false;
	}
	
	public void sendPositionPacket(float x, float y, float z, float yaw, float pitch) {
		if(this.output_idle) {
			while(ClassicByte.view.renderer.networkManager.output_idle) {}
		}
		this.output_idle = true;
		try {
			this.out.writeByte(0x08);
			if(NetworkManager.hasServerExtension(Extension.EXTENSION_HELD_BLOCK, 1)) {
				this.out.writeByte(ClassicByte.view.renderer.player.getHeldBlock());
			} else {
				this.out.writeByte(0x00);
			}
			this.out.writeShort((short)(x*32.0F));
			this.out.writeShort((short)(y*32.0F));
			this.out.writeShort((short)(z*32.0F));
			this.out.writeByte((byte)(yaw/360.0F*256.0F));
			this.out.writeByte((byte)(pitch/360.0F*256.0F));
		} catch(Exception e) {
			this.output_idle = false;
			ClassicByte.view.setScreen(new ScreenMainMenu(this.parent.parent));
			this.disconnect();
			this.disconnectAlert("Sorry", "An error occurred while trying to send a position update packet. Please try to reconnect and if this alert appears further, please ask the server host for help!");
		}
		this.output_idle = false;
	}
	
	public void sendExtInfoPacket(String app_name, int extension_count) throws Exception {
		if(this.output_idle) {
			while(ClassicByte.view.renderer.networkManager.output_idle) {}
		}
		this.output_idle = true;
		this.out.writeByte(0x10);
		this.writeString(app_name, this.out);
		this.out.writeShort(extension_count);
		this.output_idle = false;
	}
	
	public void sendExtEntryPacket(String extension_name, int version) throws Exception {
		if(this.output_idle) {
			while(ClassicByte.view.renderer.networkManager.output_idle) {}
		}
		this.output_idle = true;
		this.out.writeByte(0x11);
		this.writeString(extension_name, this.out);
		this.out.writeInt(version);
		this.output_idle = false;
	}
	
	public void sendBlockPacket(int x, int y, int z, int block_type) throws Exception {
		if(this.output_idle) {
			while(ClassicByte.view.renderer.networkManager.output_idle) {}
		}
		this.output_idle = true;
		this.out.writeByte(0x05);
		this.out.writeShort(x);
		this.out.writeShort(y);
		this.out.writeShort(z);
		if(block_type==0x00) {
			this.out.writeByte(0x00); //block destroyed
			this.out.writeByte(0x00);
		} else {
			this.out.writeByte(0x01); //block created
			this.out.writeByte(block_type);
		}
		this.output_idle = false;
	}
	
	public void sendCustomBlockSupportLevelPacket(int level) throws Exception {
		if(!NetworkManager.hasServerExtension(Extension.EXTENSION_CUSTOM_BLOCKS, 1)) {
			return;
		}
		if(this.output_idle) {
			while(ClassicByte.view.renderer.networkManager.output_idle) {}
		}
		this.output_idle = true;
		this.out.writeByte(0x13);
		this.out.writeByte(level);
		this.output_idle = false;
	}

	public void writeString(String message, DataOutputStream out) throws Exception {
		String result = message;
		if(message.length()>=64) {
			result = new String(message.substring(0,64));
		}
		while(result.length()!=64) {
			result = result + " ";
		}
		out.write(result.getBytes());
	}
	
	public String readString(DataInputStream in) throws Exception {
		String result = "";
		while(result.length()!=64) {
			result = result + (char)in.readByte();
		}
		
		return result.trim();
	}
	
	public void waitForData(int amount) throws Exception {
		while(this.in.available()<amount) {}
	}
	
	public void wasteData(int amount) throws Exception {
		this.in.skip(amount);
	}
	
	public void disconnectAlert(String title, String reason) {
		ClassicByte.information_a = title;
		ClassicByte.information_b = reason;
		Runnable f = new Runnable() {
            public void run() {
        		((ClassicByte)ClassicByte.view.getContext()).openAlert(ClassicByte.information_a, ClassicByte.information_b, true, "just_close");
            }
        };
		((ClassicByte)this.parent.parent.getContext()).runOnUiThread(f);
	}
	
	public static void deleteServerExtensions() {
		NetworkManager.server_extensions = "";
	}
	
	public static void addServerExtension(String name, int version) {
		NetworkManager.server_extensions = NetworkManager.server_extensions + name.toLowerCase() + version + "~";
	}
	
	public static boolean hasServerExtension(String name, int version) {
		return NetworkManager.server_extensions.contains(name.toLowerCase()+version+"~");
	}
	
	public void run() {
			if(this.already_connected) {
				try {
					long time_out = System.currentTimeMillis();
					while(this.in.available()<0) {
						if(System.currentTimeMillis()-time_out>Options.time_out) {
							ClassicByte.view.setScreen(new ScreenMainMenu(this.parent.parent));
							this.output_idle = false;
							this.disconnect();
							this.disconnectAlert("Disconnected: Reached timeout", "The server you're trying to connect to is probably down. Please ask the server host for help.");
						}
					}
					if(this.in.available()>0) {
						int packet_id = this.in.readByte();
						if(packet_id==0x00 && this.in.available()>=131) { //server identification
							this.waitForData(131);
							int protocol_version = this.in.readByte(); //must be 0x07
							if(protocol_version!=0x07) {
								this.disconnectAlert("Warning", "The server's protocol version differs from the default (0x07). This is not a critical error, so the client will still try to connect.");
							}
							this.current_server_name = this.readString(this.in);
							this.current_server_motd = this.readString(this.in);
							this.current_server_motd = this.current_server_motd.replaceAll("&0","");
							this.current_server_motd = this.current_server_motd.replaceAll("&1","");
							this.current_server_motd = this.current_server_motd.replaceAll("&2","");
							this.current_server_motd = this.current_server_motd.replaceAll("&3","");
							this.current_server_motd = this.current_server_motd.replaceAll("&4","");
							this.current_server_motd = this.current_server_motd.replaceAll("&5","");
							this.current_server_motd = this.current_server_motd.replaceAll("&6","");
							this.current_server_motd = this.current_server_motd.replaceAll("&7","");
							this.current_server_motd = this.current_server_motd.replaceAll("&8","");
							this.current_server_motd = this.current_server_motd.replaceAll("&9","");
							this.current_server_motd = this.current_server_motd.replaceAll("&a","");
							this.current_server_motd = this.current_server_motd.replaceAll("&b","");
							this.current_server_motd = this.current_server_motd.replaceAll("&c","");
							this.current_server_motd = this.current_server_motd.replaceAll("&d","");
							this.current_server_motd = this.current_server_motd.replaceAll("&e","");
							this.current_server_motd = this.current_server_motd.replaceAll("&f","");
							int isOp = this.in.readByte(); //0x00 for normal and 0x64 for op user
							if(isOp==0x64) {
								ClassicByte.view.renderer.player.setOp(true);
							} else {
								ClassicByte.view.renderer.player.setOp(false);
							}
							Logger.log(this, "Connected to: "+this.current_server_name+" motd: "+this.current_server_motd);
							Logger.log(this, "The server uses protocol version "+protocol_version+" and the current player has permission "+isOp);
						}
						if(packet_id==0x03 && this.in.available()>=1027) { //level chunk
							this.waitForData(1027);
							int len = this.in.readShort();
							for(int k=0;k!=len;k++) {
								this.map_data.write(this.in.readByte());
							}
							if(len!=1024) {
								this.wasteData(1024-len);
							}
							int complete = this.in.readByte();
							this.map_complete_in_percent = complete;
							Logger.log(this, "Level data chunk! "+complete+"%");
						}
						if(packet_id==0x04 && this.in.available()>=6) { //level finalize
							this.waitForData(6);
							int x_size = this.in.readShort();
							int y_size = this.in.readShort();
							int z_size = this.in.readShort();
							this.parent.world.resize(x_size, y_size, z_size);
							GZIPInputStream gzip_in = new GZIPInputStream(new ByteArrayInputStream(this.map_data.toByteArray()));
							DataInputStream d_in = new DataInputStream(gzip_in);
							int l = d_in.readInt();
							d_in.readFully(this.parent.world.block_array, 0, l);
							ClassicByte.view.renderer.force_chunk_update = true;
							ClassicByte.view.renderer.force_chunk_update_x = -1;
							ClassicByte.view.renderer.force_chunk_update_z = -1;
							this.map_completed = true;
							Logger.log(this, "Map transfer finished! Map dimensions: "+x_size+"."+y_size+"."+z_size);
						}
						if(packet_id==0x02) { //level initalize
							this.map_data = new ByteArrayOutputStream();
							
							this.parent.selections.clear();
							this.parent.selections = new ArrayList<Selection>();
							
							this.map_complete_in_percent = 0;
							this.map_completed = false;
							this.online_players = 0;
							
							if(ClassicByteView.current_screen==null) {
								ClassicByte.view.setScreen(new ServerSelectedScreen(this.parent.parent,this.current_server_name,this.current_server_ip+":"+this.current_server_port+"/"+this.parent.player.getName()+"/"+this.current_server_session,false));
							}
						}
						if(packet_id==0x0D && this.in.available()>=65) { //message packet
							this.waitForData(65);
							int player_id = this.in.readByte();
							String message = this.readString(this.in);
							message = message.replaceAll("&0",""+(char)0);
							message = message.replaceAll("&1",""+(char)1);
							message = message.replaceAll("&2",""+(char)2);
							message = message.replaceAll("&3",""+(char)3);
							message = message.replaceAll("&4",""+(char)4);
							message = message.replaceAll("&5",""+(char)5);
							message = message.replaceAll("&6",""+(char)6);
							message = message.replaceAll("&7",""+(char)7);
							message = message.replaceAll("&8",""+(char)8);
							message = message.replaceAll("&9",""+(char)9);
							message = message.replaceAll("&a",""+(char)10);
							message = message.replaceAll("&b",""+(char)11);
							message = message.replaceAll("&c",""+(char)12);
							message = message.replaceAll("&d",""+(char)13);
							message = message.replaceAll("&e",""+(char)14);
							message = message.replaceAll("&f",""+(char)15);
							if(NetworkManager.hasServerExtension(Extension.EXTENSION_MESSAGE_TYPES, 1)) {
								if(player_id==Chat.LOCATION_DEFAULT) {
									this.parent.chatmananger.addLine(message);
								}
								if(player_id==Chat.LOCATION_ANNOUNCEMENT) {
									IngameRenderer.CHAT_ANNOUNCEMENT = message;
								}
								if(player_id==Chat.LOCATION_BOTTOM1) {
									IngameRenderer.CHAT_BOTTOMRIGHT_1 = message;
								}
								if(player_id==Chat.LOCATION_BOTTOM2) {
									IngameRenderer.CHAT_BOTTOMRIGHT_2 = message;
								}
								if(player_id==Chat.LOCATION_BOTTOM3) {
									IngameRenderer.CHAT_BOTTOMRIGHT_3 = message;
								}
								if(player_id==Chat.LOCATION_STATUS1) {
									IngameRenderer.CHAT_STATUS_1 = message;
								}
								if(player_id==Chat.LOCATION_STATUS2) {
									IngameRenderer.CHAT_STATUS_2 = message;
								}
								if(player_id==Chat.LOCATION_STATUS3) {
									IngameRenderer.CHAT_STATUS_3 = message;
								}
								if(player_id==Chat.LOCATION_TOPLEFT) {
									IngameRenderer.CHAT_TOPLEFT = message;
								}
							} else {
								this.parent.chatmananger.addLine(message);
							}
						}
						if(packet_id==0x08 && this.in.available()>=9) { //player teleport
							this.waitForData(9);
							int player_id = this.in.readByte();
							float x_pos = this.in.readShort()/32.0F;
							float y_pos = (this.in.readShort()/32.0F)-1.59375F;
							float z_pos = this.in.readShort()/32.0F;
							float yaw = this.in.readByte()/256.0F*360.0F;
							float pitch = this.in.readByte()/256.0F*360.0F;
							
							if(player_id==-1) { //own position, so teleport player
								this.parent.player.setPosition(x_pos, y_pos, z_pos);
								this.parent.player.camera_rot_x = (float) (yaw/360.0F*(Math.PI*2.0F));
								this.parent.player.camera_rot_y = (float) (pitch/360.0F*(Math.PI*2.0F));
							} else {
								for(int k=0;k!=ClassicByte.view.renderer.players.size();k++) {
									if(k>=ClassicByte.view.renderer.players.size()) {
										break;
									}
									if(ClassicByte.view.renderer.players.get(k).getID()==player_id) {
										ClassicByte.view.renderer.players.get(k).setPosition(x_pos, y_pos, z_pos);
										ClassicByte.view.renderer.players.get(k).setRotation(yaw, pitch);
										break;
									}
								}
							}
						}
						
						if(packet_id==0x09 && this.in.available()>=6) { //player position and rotation change
							this.waitForData(6);
							int player_id = this.in.readByte();
							float x_change = this.in.readByte()/32.0F;
							float y_change = this.in.readByte()/32.0F;
							float z_change = this.in.readByte()/32.0F;
							float yaw = this.in.readByte()/256.0F*360.0F;
							float pitch = this.in.readByte()/256.0F*360.0F;
							
							if(player_id==-1) { //own position, so teleport player
								this.parent.player.setPosition(this.parent.player.getXPosition()+x_change, this.parent.player.getYPosition()+y_change, this.parent.player.getZPosition()+z_change);
								this.parent.player.camera_rot_x = (float) (yaw/360.0F*(Math.PI*2.0F));
								this.parent.player.camera_rot_y = (float) (pitch/360.0F*(Math.PI*2.0F));
							} else {
								for(int k=0;k!=ClassicByte.view.renderer.players.size();k++) {
									if(k>=ClassicByte.view.renderer.players.size()) {
										break;
									}
									if(ClassicByte.view.renderer.players.get(k).getID()==player_id) {
										ClassicByte.view.renderer.players.get(k).setPosition(ClassicByte.view.renderer.players.get(k).getXPosition()+x_change, ClassicByte.view.renderer.players.get(k).getYPosition()+y_change, ClassicByte.view.renderer.players.get(k).getZPosition()+z_change);
										ClassicByte.view.renderer.players.get(k).setRotation(yaw, pitch);
										break;
									}
								}
							}
						}
						
						if(packet_id==0x0A && this.in.available()>=4) { //player position change
							this.waitForData(4);
							int player_id = this.in.readByte();
							float x_change = this.in.readByte()/32.0F;
							float y_change = this.in.readByte()/32.0F;
							float z_change = this.in.readByte()/32.0F;
							
							if(player_id==-1) { //own position, so teleport player
								this.parent.player.setPosition(this.parent.player.getXPosition()+x_change, this.parent.player.getYPosition()+y_change, this.parent.player.getZPosition()+z_change);
							} else {
								for(int k=0;k!=ClassicByte.view.renderer.players.size();k++) {
									if(k>=ClassicByte.view.renderer.players.size()) {
										break;
									}
									if(ClassicByte.view.renderer.players.get(k).getID()==player_id) {
										ClassicByte.view.renderer.players.get(k).setPosition(ClassicByte.view.renderer.players.get(k).getXPosition()+x_change, ClassicByte.view.renderer.players.get(k).getYPosition()+y_change, ClassicByte.view.renderer.players.get(k).getZPosition()+z_change);
										break;
									}
								}
							}
						}
						
						if(packet_id==0x0B && this.in.available()>=3) { //player rotation change
							this.waitForData(3);
							int player_id = this.in.readByte();
							float yaw = this.in.readByte()/256.0F*360.0F;
							float pitch = this.in.readByte()/256.0F*360.0F;
							
							if(player_id==-1) { //own position, so teleport player
								this.parent.player.camera_rot_x = (float) (yaw/360.0F*(Math.PI*2.0F));
								this.parent.player.camera_rot_y = (float) (pitch/360.0F*(Math.PI*2.0F));
							} else {
								for(int k=0;k!=ClassicByte.view.renderer.players.size();k++) {
									if(k>=ClassicByte.view.renderer.players.size()) {
										break;
									}
									if(ClassicByte.view.renderer.players.get(k).getID()==player_id) {
										ClassicByte.view.renderer.players.get(k).setRotation(yaw, pitch);
										break;
									}
								}
							}
						}
						
						if(packet_id==0x06 && this.in.available()>=7) { //set block
							this.waitForData(7);
							int x_pos = this.in.readShort();
							int y_pos = this.in.readShort();
							int z_pos = this.in.readShort();
							byte type = this.in.readByte();
							this.parent.world.setBlock(x_pos, y_pos, z_pos, type);
						}
						if(packet_id==0x0E && this.in.available()>=64) { //disconnect
							this.waitForData(64);
							String reason = this.readString(this.in);
							Logger.log(this, "Kicked from server! Reason: "+reason);
							this.disconnect();
							this.parent.parent.setScreen(new ScreenMainMenu(this.parent.parent));
							this.disconnectAlert("Disconnected: Kicked",reason);
							return;
						}
						if(packet_id==0x13 && NetworkManager.hasServerExtension(Extension.EXTENSION_CUSTOM_BLOCKS, 1) && this.in.available()>=1) { //customblock support
							this.waitForData(1);
							int level = this.in.readByte();
							Logger.log(this, "Customblock support of level "+level);
							Logger.log(this, "Now sending own support-level...");
							this.sendCustomBlockSupportLevelPacket(1);
						}
						if(packet_id==0x10 && this.in.available()>=66) { //Ext-Info
							this.waitForData(66);
							String app_name = this.readString(this.in);
							int extension_count = this.in.readShort();
							Logger.log(this, "Server is running software named "+app_name+" and it supports up to "+extension_count+" extensions!");
							this.server_extension_count = extension_count;
							this.current_extension_index = 0;
							NetworkManager.deleteServerExtensions();
						}
						if(packet_id==0x11 && this.in.available()>=68) { //Ext-Entry
							this.waitForData(68);
							String extension_name = this.readString(this.in);
							int version = this.in.readInt();
							NetworkManager.addServerExtension(extension_name, version);
							Logger.log(this, "The server supports "+extension_name+" at version "+version);
							this.current_extension_index++;
							if(this.current_extension_index==this.server_extension_count) {
								this.sendExtInfoPacket("ClassicByte",10);
								Logger.log(this, "The client will now send the locally supported extensions");
								this.sendExtEntryPacket(Extension.EXTENSION_CLICK_DISTANCE, 1);
								this.sendExtEntryPacket(Extension.EXTENSION_CUSTOM_BLOCKS, 1);
								this.sendExtEntryPacket(Extension.EXTENSION_HELD_BLOCK, 1);
								this.sendExtEntryPacket(Extension.EXTENSION_EMOTE_FIX, 1);
								//this.sendExtEntryPacket(Extension.EXTENSION_TEXT_HOT_KEY, 1);
								this.sendExtEntryPacket(Extension.EXTENSION_EXT_PLAYER_LIST, 1);
								this.sendExtEntryPacket(Extension.EXTENSION_ENV_COLORS, 1);
								this.sendExtEntryPacket(Extension.EXTENSION_SELECTION_CUBOID, 1);
								//this.sendExtEntryPacket(Extension.EXTENSION_BLOCK_PERMISSIONS, 1);
								//this.sendExtEntryPacket(Extension.EXTENSION_CHANGE_MODEL, 1);
								//this.sendExtEntryPacket(Extension.EXTENSION_ENV_MAP_APPEARANCE, 1);
								this.sendExtEntryPacket(Extension.EXTENSION_ENV_WEATHER_TYPE, 1);
								//this.sendExtEntryPacket(Extension.EXTENSION_HACK_CONTROL, 1);
								this.sendExtEntryPacket(Extension.EXTENSION_MESSAGE_TYPES, 1);
								this.sendExtEntryPacket(Extension.EXTENSION_CUSTOM_DATA, 1);
							}
						}
						if(packet_id==0xFA && NetworkManager.hasServerExtension(Extension.EXTENSION_CUSTOM_DATA, 1)) { //custom data packet
							String data_name = this.readString(this.in);
							long data_len = this.in.readLong();
							if(data_len>0) {
								byte[] data = new byte[(int)data_len];
								this.in.read(data);
							}
							
							if(data_name.equalsIgnoreCase("playsound")) {
								this.beep_sound = MediaPlayer.create(this.parent.parent.getContext(), R.drawable.beep);
								this.beep_sound.setLooping(false);
								this.beep_sound.start();
							}
							
							if(data_name.equalsIgnoreCase("exitgame")) {
								this.disconnectAlert("Exit game", "The server forces ClassicByte to exit");
								this.disconnect();
								System.exit(0);
							}
						}
						if(packet_id==0x14 && NetworkManager.hasServerExtension(Extension.EXTENSION_HELD_BLOCK, 1) && this.in.available()>=2) { //hold this
							this.waitForData(2);
							int block_to_hold = this.in.readByte();
							int prevent_change = this.in.readByte();
							if(prevent_change==1) {
								ClassicByte.view.renderer.player.setAllowBlockChange(false);
							} else {
								ClassicByte.view.renderer.player.setAllowBlockChange(true);
							}
							ClassicByte.view.renderer.player.setHeldBlock(block_to_hold);
						}
						if(packet_id==0x19 && NetworkManager.hasServerExtension(Extension.EXTENSION_ENV_COLORS, 1) && this.in.available()>=7) { //environment colors
							this.waitForData(7);
							int variable = this.in.readByte();
							int red = this.in.readShort();
							int green = this.in.readShort();
							int blue = this.in.readShort();
							if(variable==0) { //sky color
								ClassicByte.view.renderer.world.setSkyColor(red, green, blue);
							}
						}
						if(packet_id==0x1F && NetworkManager.hasServerExtension(Extension.EXTENSION_ENV_WEATHER_TYPE, 1) && this.in.available()>=1) { //environment weather type
							this.waitForData(1);
							int weather_type = this.in.readByte();
							if(weather_type==0) {
								ClassicByte.view.renderer.world.setSunny();
							}
							if(weather_type==1) {
								ClassicByte.view.renderer.world.setRaining();
							}
							if(weather_type==2) {
								ClassicByte.view.renderer.world.setSnowing();
							}
						}
						if(packet_id==0x07 && this.in.available()>=73) { //spawn player
							this.waitForData(73);
							int player_id = this.in.readByte();
							String name = ChatManager.deleteColorCodes(this.readString(this.in));
							float x_pos = this.in.readShort()/32.0F;
							float y_pos = this.in.readShort()/32.0F;
							float z_pos = this.in.readShort()/32.0F;
							float yaw = this.in.readByte()/256.0F*360.0F;
							float pitch = this.in.readByte()/256.0F*360.0F;
							if(player_id!=-1) {
								Logger.log(this, "Player "+name+" connected!");
								EntityPlayer player = new EntityPlayer(player_id);
								player.setName(name);
								player.setPosition(x_pos, y_pos, z_pos);
								player.setRotation(yaw, pitch);
								ClassicByte.view.renderer.players.add(player);
								this.online_players++;
							} else {
								ClassicByte.view.renderer.player.setPosition(Math.round(x_pos), Math.round(y_pos), Math.round(z_pos));
								ClassicByte.view.renderer.player.setSpawn(Math.round(x_pos), Math.round(y_pos), Math.round(z_pos));
							}
						}
						if(packet_id==0x0C && this.in.available()>=1) { //despawn player
							this.waitForData(1);
							int player_id = this.in.readByte();
							
							for(int k=0;k!=ClassicByte.view.renderer.players.size();k++) {
								if(k>=ClassicByte.view.renderer.players.size()) {
									break;
								}
								if(ClassicByte.view.renderer.players.get(k).getID()==player_id) {
									ClassicByte.view.renderer.players.remove(k);
									break;
								}
							}
							this.online_players--;
						}
						if(packet_id==0x0F && this.in.available()>=1) { //op status
							this.waitForData(1);
							int isOp = this.in.readByte();
							if(isOp==0x64) {
								ClassicByte.view.renderer.player.setOp(true);
								ClassicByte.view.renderer.chatmananger.addLine((char)14+"You're now an operator!");
							} else {
								ClassicByte.view.renderer.player.setOp(false);
								ClassicByte.view.renderer.chatmananger.addLine((char)14+"You're no longer an operator!");
							}
						}
						if(packet_id==0x12 && NetworkManager.hasServerExtension(Extension.EXTENSION_CLICK_DISTANCE, 1) && this.in.available()>=2) { //click distance
							this.waitForData(2);
							float range = this.in.readShort()/32.0F;
							this.parent.player.setClickDistance(range);
						}
						if(packet_id==0x15 && NetworkManager.hasServerExtension(Extension.EXTENSION_TEXT_HOT_KEY, 1) && this.in.available()>=133) { //set texthotkey packet
							this.waitForData(133);
							String label = this.readString(this.in);
							String action = this.readString(this.in);
							int key_code = this.in.readInt();
							int key_mods = this.in.readByte();
							//NOT SUPPORTED ON ANDROID DEVICES DUE THE FACT THAT THEY DON'T HAVE KEYBOARDS
							//IT'S RIGHT, YOU COULD PLUG ONE IN BUT I COULD BET THAT NO ONE KNOWS THAT
							//(APART FROM DEVELOPERS)
						}
						if(packet_id==0x16 && NetworkManager.hasServerExtension(Extension.EXTENSION_EXT_PLAYER_LIST, 1) && this.in.available()>=378) { // ExtAddPlayerName packet
							this.waitForData(387);
							int name_id = this.in.readShort();
							String player_name = this.readString(this.in);
							String list_name = this.readString(this.in);
							String group_name = this.readString(this.in);
							int group_rank = this.in.readByte();
							for(int k=0;k!=this.parent.players.size();k++) {
								if(k>=this.parent.players.size()) {
									break;
								}
								if(this.parent.players.get(k).getID()==name_id) {
									this.parent.players.get(k).setName(player_name);
									break;
								}
							}
						}
						if(packet_id==0x17 && NetworkManager.hasServerExtension(Extension.EXTENSION_EXT_PLAYER_LIST, 1) && this.in.available()>=129) { //ExtAddEntity packet
							this.waitForData(129);
							int entity_id = this.in.readByte();
							String ingame_name = this.readString(this.in);
							String skin_name = this.readString(this.in);
						}
						if(packet_id==0x18 && NetworkManager.hasServerExtension(Extension.EXTENSION_EXT_PLAYER_LIST, 1) && this.in.available()>=2) { //ExtRemovePlayerName packet
							this.waitForData(2);
							int name_id = this.in.readShort();
						}
						if(packet_id==0x1A && NetworkManager.hasServerExtension(Extension.EXTENSION_SELECTION_CUBOID, 1) && this.in.available()>=149) { //MakeSelection packet
							this.waitForData(149);
							int id = this.in.readByte();
							String label = this.readString(this.in);
							int start_x = this.in.readShort();
							int start_y = this.in.readShort();
							int start_z = this.in.readShort();
							int end_x = this.in.readShort();
							int end_y = this.in.readShort();
							int end_z = this.in.readShort();
							int red = this.in.readShort();
							int green = this.in.readShort();
							int blue = this.in.readShort();
							int opacity = this.in.readShort();
							for(int k=0;k!=this.parent.selections.size();k++) {
								if(k>=this.parent.selections.size()) {
									break;
								}
								if(this.parent.selections.get(k).getID()==id) {
									this.parent.selections.remove(k);
									break;
								}
							}
							Selection s = new Selection();
							s.setID(id);
							s.setStartPosition(start_x, start_y, start_z);
							s.setEndPosition(end_x, end_y, end_z);
							s.setName(label);
							s.setColor(red, green, blue);
							s.setOpacity(opacity);
							this.parent.selections.add(s);
						}
						if(packet_id==0x1B && NetworkManager.hasServerExtension(Extension.EXTENSION_SELECTION_CUBOID, 1) && this.in.available()>=1) { //RemoveSelection packet
							this.waitForData(1);
							int id = this.in.readByte();
							for(int k=0;k!=this.parent.selections.size();k++) {
								if(k>=this.parent.selections.size()) {
									break;
								}
								if(this.parent.selections.get(k).getID()==id) {
									this.parent.selections.remove(k);
									break;
								}
							}
						}
						if(packet_id==0x1C && NetworkManager.hasServerExtension(Extension.EXTENSION_BLOCK_PERMISSIONS, 1) && this.in.available()>=3) { //SetBlockPermission packet
							this.waitForData(3);
							int block_type = this.in.readByte();
							int allow_placement = this.in.readByte();
							int allow_deletion = this.in.readByte();
						}
						if(packet_id==0x1D && NetworkManager.hasServerExtension(Extension.EXTENSION_CHANGE_MODEL, 1) && this.in.available()>=129) { //ChangeModel Packet
							this.waitForData(129);
							int entity_id = this.in.readByte();
							String model_name = this.readString(this.in);
						}
						if(packet_id==0x1E && NetworkManager.hasServerExtension(Extension.EXTENSION_ENV_MAP_APPEARANCE, 1) && this.in.available()>=132) { //EnvSetMapAppearance packet
							this.waitForData(132);
							String texture_url = this.readString(this.in);
							int side_block = this.in.readByte();
							int edge_block = this.in.readByte();
							int side_level = this.in.readShort();
						}
						if(packet_id==0x20 && NetworkManager.hasServerExtension(Extension.EXTENSION_HACK_CONTROL, 1) && this.in.available()>=7) { //HackControl Packet
							this.waitForData(7);
							this.wasteData(7);
						}
					}
					if(System.currentTimeMillis()-this.position_update_timer>62.5F && this.map_completed && !Options.save_bandwidth_on_mobile_networks) {
						this.sendPositionPacket(this.parent.player.getXPosition(),this.parent.player.getYPosition()+this.parent.player.getPlayerEyeHeight(),this.parent.player.getZPosition(),(float)(this.parent.player.camera_rot_x/(Math.PI*2.0F)*360.0F)+45.0F,(float)(this.parent.player.camera_rot_y/(Math.PI*2.0F)*360.0F)-90.0F);
						this.position_update_timer = System.currentTimeMillis();
					}
					if(System.currentTimeMillis()-this.position_update_timer>1000 && this.map_completed && Options.save_bandwidth_on_mobile_networks) {
						this.sendPositionPacket(this.parent.player.getXPosition(),this.parent.player.getYPosition()+this.parent.player.getPlayerEyeHeight(),this.parent.player.getZPosition(),(float)(this.parent.player.camera_rot_x/(Math.PI*2.0F)*360.0F)+45.0F,(float)(this.parent.player.camera_rot_y/(Math.PI*2.0F)*360.0F)-90.0F);
						this.position_update_timer = System.currentTimeMillis();
					}
				} catch(Exception e) {
					e.printStackTrace();
					this.disconnect();
					this.parent.parent.setScreen(new ScreenMainMenu(this.parent.parent));
				}
			}
	}
}
