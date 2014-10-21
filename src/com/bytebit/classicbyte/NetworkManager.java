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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import android.media.MediaPlayer;

public class NetworkManager {
	public String current_server_ip = "127.0.0.1";
	public int current_server_port = 25565;
	public String current_server_name = "";
	public String current_server_motd = "";
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
	
	private byte[] input_buffer = new byte[16384*4];
	private int input_buffer_index = 0;
	private int[] packet_len = {130,0,0,1027,6,0,7,73,9,6,4,3,1,65,64,1,66,68,2,1,2,133,195,0,2,7,85,1,3,65,68,1,7,137};
	
	private MediaPlayer beep_sound;
	
	private ByteArrayOutputStream test_out = new ByteArrayOutputStream();
	private DataInputStream test_in = null;
	
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
			this.current_server_name = "";
			this.current_server_motd = "";
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
			
			this.parent.world.setCloudColor(255, 255, 255);
			this.parent.world.setSkyColor(156, 205, 255);
			
			this.current_server_name = "";
			this.current_server_motd = "";
			
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
		this.out.writeByte(0x42); //0x00 for vanilla minecraft client without cpe/0x42 for a client which supports cpe
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
		Logger.log(this, "Sending extension support for "+extension_name);
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
	
	/*public String readString(DataInputStream in) throws Exception {
		String result = "";
		while(result.length()!=64) {
			result = result + (char)in.readByte();
		}
		
		return result.trim();
	}*/
	
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
	
	public String readString(DataInputStream in) throws Exception {
		String result = "";
		while(result.length()!=64) {
			result = result + (char)this.test_in.readByte();
		}
		
		return result.trim();
	}
	
	public void update() throws Exception {
			if(this.already_connected) {
				int available = this.in.available();
				if(available>0) {
					byte[] available_data = new byte[available];
					this.in.read(available_data);
					this.test_out.write(available_data);
				}
				if(System.currentTimeMillis()-this.position_update_timer>62.5F && this.map_completed && !Options.save_bandwidth_on_mobile_networks) {
					this.sendPositionPacket(this.parent.player.getXPosition(),this.parent.player.getYPosition()+this.parent.player.getPlayerEyeHeight(),this.parent.player.getZPosition(),(float)(this.parent.player.camera_rot_x/(Math.PI*2.0F)*360.0F)+45.0F,(float)(this.parent.player.camera_rot_y/(Math.PI*2.0F)*360.0F)-90.0F);
					this.position_update_timer = System.currentTimeMillis();
				}
				if(System.currentTimeMillis()-this.position_update_timer>1000 && this.map_completed && Options.save_bandwidth_on_mobile_networks) {
					this.sendPositionPacket(this.parent.player.getXPosition(),this.parent.player.getYPosition()+this.parent.player.getPlayerEyeHeight(),this.parent.player.getZPosition(),(float)(this.parent.player.camera_rot_x/(Math.PI*2.0F)*360.0F)+45.0F,(float)(this.parent.player.camera_rot_y/(Math.PI*2.0F)*360.0F)-90.0F);
					this.position_update_timer = System.currentTimeMillis();
				}
				if(this.test_out.toByteArray().length>0 && this.test_out.toByteArray()[0]>-1 && this.test_out.toByteArray()[0]<256 && this.test_out.size()>=this.packet_len[this.test_out.toByteArray()[0]]+1) {
					//--> Verarbeiten
					this.test_in = new DataInputStream(new ByteArrayInputStream(this.test_out.toByteArray()));
					byte[] b = this.test_out.toByteArray();
					this.test_out.close();
					this.test_out = new ByteArrayOutputStream();
					this.test_out.write(b, this.packet_len[b[0]]+1, b.length-(this.packet_len[b[0]])-1);
					
					int packet_id = this.test_in.readByte();
					if(packet_id==0x00) { //server identification
						int protocol_version = this.test_in.readByte(); //must be 0x07
						if(protocol_version!=0x07) {
							this.parent.chatmananger.addLine((char)12+"~~~Warning~~~");
							this.parent.chatmananger.addLine((char)12+"The server's protocol version differs from the default (0x07).");
							this.parent.chatmananger.addLine((char)12+"This is a critical error, through the client will still try to connect.");
							this.parent.chatmananger.addLine((char)12+"~~~Warning~~~");
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
						int isOp = this.test_in.readByte(); //0x00 for normal and 0x64 for op user
						if(isOp==0x64) {
							ClassicByte.view.renderer.player.setOp(true);
						} else {
							ClassicByte.view.renderer.player.setOp(false);
						}
						Logger.log(this, "Connected to: "+this.current_server_name+" motd: "+this.current_server_motd);
						Logger.log(this, "The server uses protocol version "+protocol_version+" and the current player has permission "+isOp);
					}
					if(packet_id==0x03) { //level chunk
						int len = this.test_in.readShort();
						for(int k=0;k!=len;k++) {
							this.map_data.write(this.test_in.readByte());
						}
						if(len!=1024) {
							this.test_in.skip(1024-len);
						}
						int complete = this.test_in.readByte();
						this.map_complete_in_percent = complete;
						Logger.log(this, "Level data chunk! "+complete+"%"+" length: "+len);
					}
					if(packet_id==0x06 && !this.parent.force_chunk_update) { //set block (really important to put it here, or the force_chunk_update flags will become overridden by the set block packet)
						int x_pos = this.test_in.readShort();
						int y_pos = this.test_in.readShort();
						int z_pos = this.test_in.readShort();
						byte type = this.test_in.readByte();
						this.parent.world.setBlock(x_pos, y_pos, z_pos, type);
					}
					if(packet_id==0x04) { //level finalize
						int x_size = this.test_in.readShort();
						int y_size = this.test_in.readShort();
						int z_size = this.test_in.readShort();
						this.parent.world.resize(x_size, y_size, z_size);
						Logger.log(this, "Map transfer finished! Map dimensions: "+x_size+"."+y_size+"."+z_size);
						GZIPInputStream gzip_in = new GZIPInputStream(new ByteArrayInputStream(this.map_data.toByteArray()));
						this.map_data.close();
						DataInputStream d_in = new DataInputStream(gzip_in);
						int l = d_in.readInt();
						d_in.readFully(this.parent.world.block_array, 0, l);
						d_in.close();
						ClassicByte.view.renderer.force_chunk_update = true;
						ClassicByte.view.renderer.force_chunk_update_x = -1;
						ClassicByte.view.renderer.force_chunk_update_z = -1;
						this.parent.world.setSideLevel(y_size/2);
						this.map_completed = true;
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
					if(packet_id==0x0D) { //message packet
						int player_id = this.test_in.readByte();
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
					if(packet_id==0x08) { //player teleport
						int player_id = this.test_in.readByte();
						float x_pos = this.test_in.readShort()/32.0F;
						float y_pos = (this.test_in.readShort()/32.0F)-1.59375F;
						float z_pos = this.test_in.readShort()/32.0F;
						float yaw = this.test_in.readByte()/256.0F*360.0F;
						float pitch = this.test_in.readByte()/256.0F*360.0F;
						
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
					
					if(packet_id==0x09) { //player position and rotation change
						int player_id = this.test_in.readByte();
						float x_change = this.test_in.readByte()/32.0F;
						float y_change = this.test_in.readByte()/32.0F;
						float z_change = this.test_in.readByte()/32.0F;
						float yaw = this.test_in.readByte()/256.0F*360.0F;
						float pitch = this.test_in.readByte()/256.0F*360.0F;
						
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
					
					if(packet_id==0x0A) { //player position change
						int player_id = this.test_in.readByte();
						float x_change = this.test_in.readByte()/32.0F;
						float y_change = this.test_in.readByte()/32.0F;
						float z_change = this.test_in.readByte()/32.0F;
						
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
					
					if(packet_id==0x0B) { //player rotation change
						int player_id = this.test_in.readByte();
						float yaw = this.test_in.readByte()/256.0F*360.0F;
						float pitch = this.test_in.readByte()/256.0F*360.0F;
						
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
					if(packet_id==0x0E) { //disconnect
						String reason = this.readString(this.in);
						Logger.log(this, "Kicked from server! Reason: "+reason);
						this.disconnect();
						this.parent.parent.setScreen(new ScreenMainMenu(this.parent.parent));
						this.disconnectAlert("Disconnected: Kicked",reason);
						return;
					}
					if(packet_id==0x13 && NetworkManager.hasServerExtension(Extension.EXTENSION_CUSTOM_BLOCKS, 1)) { //customblock support
						int level = this.test_in.readByte();
						Logger.log(this, "Customblock support of level "+level);
						Logger.log(this, "Now sending own support-level...");
						this.sendCustomBlockSupportLevelPacket(1);
					}
					if(packet_id==0x10) { //Ext-Info
						String app_name = this.readString(this.in);
						int extension_count = this.test_in.readShort();
						Logger.log(this, "Server is running software named "+app_name+" and it supports up to "+extension_count+" extensions!");
						this.server_extension_count = extension_count;
						this.current_extension_index = 0;
						NetworkManager.deleteServerExtensions();
					}
					if(packet_id==0x11) { //Ext-Entry
						String extension_name = this.readString(this.in);
						int version = this.test_in.readInt();
						NetworkManager.addServerExtension(extension_name, version);
						Logger.log(this, "The server supports "+extension_name+" at version "+version);
						this.current_extension_index++;
						if(this.current_extension_index==this.server_extension_count) {
							this.sendExtInfoPacket("ClassiCube Client",16);
							Logger.log(this, "The client will now send locally supported extensions");
							this.sendExtEntryPacket("ExtInfo", 1);
							this.sendExtEntryPacket("ExtEntry", 1);
							
							this.sendExtEntryPacket(Extension.EXTENSION_CLICK_DISTANCE, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_CUSTOM_BLOCKS, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_HELD_BLOCK, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_EMOTE_FIX, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_TEXT_HOT_KEY, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_EXT_PLAYER_LIST, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_ENV_COLORS, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_SELECTION_CUBOID, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_BLOCK_PERMISSIONS, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_CHANGE_MODEL, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_ENV_MAP_APPEARANCE, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_ENV_WEATHER_TYPE, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_HACK_CONTROL, 1);
							this.sendExtEntryPacket(Extension.EXTENSION_MESSAGE_TYPES, 1);
							//this.sendExtEntryPacket(Extension.EXTENSION_CUSTOM_DATA, 1);
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
					if(packet_id==0x14 && NetworkManager.hasServerExtension(Extension.EXTENSION_HELD_BLOCK, 1)) { //hold this
						int block_to_hold = this.test_in.readByte();
						int prevent_change = this.test_in.readByte();
						if(prevent_change==1) {
							ClassicByte.view.renderer.player.setAllowBlockChange(false);
						} else {
							ClassicByte.view.renderer.player.setAllowBlockChange(true);
						}
						ClassicByte.view.renderer.player.setHeldBlock(block_to_hold);
					}
					if(packet_id==0x19 && NetworkManager.hasServerExtension(Extension.EXTENSION_ENV_COLORS, 1)) { //environment colors
						int variable = this.test_in.readByte();
						int red = this.test_in.readShort();
						int green = this.test_in.readShort();
						int blue = this.test_in.readShort();
						if(variable==0) { //sky color
							ClassicByte.view.renderer.world.setSkyColor(red, green, blue);
						}
						if(variable==1) { //cloud color
							ClassicByte.view.renderer.world.setCloudColor(red, green, blue);
						}
					}
					if(packet_id==0x1F && NetworkManager.hasServerExtension(Extension.EXTENSION_ENV_WEATHER_TYPE, 1)) { //environment weather type
						int weather_type = this.test_in.readByte();
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
					if(packet_id==0x07) { //spawn player
						int player_id = this.test_in.readByte();
						String name = ChatManager.deleteColorCodes(this.readString(this.in));
						float x_pos = this.test_in.readShort()/32.0F;
						float y_pos = this.test_in.readShort()/32.0F-1.59375F;
						float z_pos = this.test_in.readShort()/32.0F;
						float yaw = this.test_in.readByte()/256.0F*360.0F;
						float pitch = this.test_in.readByte()/256.0F*360.0F;
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
					if(packet_id==0x0C) { //despawn player
						int player_id = this.test_in.readByte();
						
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
					if(packet_id==0x0F) { //op status
						int isOp = this.test_in.readByte();
						if(isOp==0x64) {
							ClassicByte.view.renderer.player.setOp(true);
							ClassicByte.view.renderer.chatmananger.addLine((char)14+"You're now an operator!");
						} else {
							ClassicByte.view.renderer.player.setOp(false);
							ClassicByte.view.renderer.chatmananger.addLine((char)14+"You're no longer an operator!");
						}
					}
					if(packet_id==0x12 && NetworkManager.hasServerExtension(Extension.EXTENSION_CLICK_DISTANCE, 1)) { //click distance
						float range = this.test_in.readShort()/32.0F;
						this.parent.player.setClickDistance(range);
					}
					if(packet_id==0x15 && NetworkManager.hasServerExtension(Extension.EXTENSION_TEXT_HOT_KEY, 1)) { //set texthotkey packet
						String label = this.readString(this.in);
						String action = this.readString(this.in);
						int key_code = this.test_in.readInt();
						int key_mods = this.test_in.readByte();
						//NOT SUPPORTED ON ANDROID DEVICES DUE THE FACT THAT THEY DON'T HAVE KEYBOARDS
						//IT'S RIGHT, YOU COULD PLUG ONE IN BUT I COULD BET THAT NO ONE KNOWS THAT
						//(APART FROM DEVELOPERS)
					}
					if(packet_id==0x16 && NetworkManager.hasServerExtension(Extension.EXTENSION_EXT_PLAYER_LIST, 1)) { // ExtAddPlayerName packet
						int name_id = this.test_in.readShort();
						String player_name = this.readString(this.in);
						String list_name = this.readString(this.in);
						String group_name = this.readString(this.in);
						int group_rank = this.test_in.readByte();
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
					if(packet_id==0x17 && NetworkManager.hasServerExtension(Extension.EXTENSION_EXT_PLAYER_LIST, 1)) { //ExtAddEntity packet
						int entity_id = this.test_in.readByte();
						String ingame_name = this.readString(this.in);
						String skin_name = this.readString(this.in);
					}
					if(packet_id==0x18 && NetworkManager.hasServerExtension(Extension.EXTENSION_EXT_PLAYER_LIST, 1)) { //ExtRemovePlayerName packet
						int name_id = this.test_in.readShort();
					}
					if(packet_id==0x1A && NetworkManager.hasServerExtension(Extension.EXTENSION_SELECTION_CUBOID, 1)) { //MakeSelection packet
						int id = this.test_in.readByte();
						String label = this.readString(this.in);
						int start_x = this.test_in.readShort();
						int start_y = this.test_in.readShort();
						int start_z = this.test_in.readShort();
						int end_x = this.test_in.readShort();
						int end_y = this.test_in.readShort();
						int end_z = this.test_in.readShort();
						int red = this.test_in.readShort();
						int green = this.test_in.readShort();
						int blue = this.test_in.readShort();
						int opacity = this.test_in.readShort();
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
					if(packet_id==0x1B && NetworkManager.hasServerExtension(Extension.EXTENSION_SELECTION_CUBOID, 1)) { //RemoveSelection packet
						int id = this.test_in.readByte();
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
					if(packet_id==0x1C && NetworkManager.hasServerExtension(Extension.EXTENSION_BLOCK_PERMISSIONS, 1)) { //SetBlockPermission packet
						int block_type = this.test_in.readByte();
						int allow_placement = this.test_in.readByte();
						int allow_deletion = this.test_in.readByte();
					}
					if(packet_id==0x1D && NetworkManager.hasServerExtension(Extension.EXTENSION_CHANGE_MODEL, 1)) { //ChangeModel Packet
						int entity_id = this.test_in.readByte();
						String model_name = this.readString(this.in);
					}
					if(packet_id==0x1E && NetworkManager.hasServerExtension(Extension.EXTENSION_ENV_MAP_APPEARANCE, 1)) { //EnvSetMapAppearance packet
						String texture_url = this.readString(this.in);
						int side_block = this.test_in.readByte();
						int edge_block = this.test_in.readByte();
						int side_level = this.test_in.readShort();
						this.parent.world.setSideLevel(side_level);
					}
					if(packet_id==0x20 && NetworkManager.hasServerExtension(Extension.EXTENSION_HACK_CONTROL, 1)) { //HackControl Packet
						this.wasteData(7);
					}
				}
			}
	}
}