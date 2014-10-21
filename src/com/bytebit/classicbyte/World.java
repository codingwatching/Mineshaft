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

import java.util.Random;

public class World {
	public int x_size = 256;
	public int y_size = 64;
	public int z_size = 256;
	public byte[] block_array = new byte[this.x_size*this.y_size*this.z_size];
	private int weather = 0;
	private int sky_color_red = -1;
	private int sky_color_green = -1;
	private int sky_color_blue = -1;
	private int cloud_color_red = -1;
	private int cloud_color_green = -1;
	private int cloud_color_blue = 1;
	private int side_level = 32;
	public Random rand = new Random();
	
	public RenderChunk[][] renderchunks = new RenderChunk[(int)(this.x_size/16.0F+0.5F)][(int)(this.z_size/16.0F+0.5F)];
	
	public void resize(int x, int y, int z) {
		this.x_size = x;
		this.y_size = y;
		this.z_size = z;
		this.block_array = new byte[x*y*z];
		this.renderchunks = new RenderChunk[(int)(this.x_size/16.0F+0.5F)][(int)(this.z_size/16.0F+0.5F)];
		for(int z2=0;z2!=(int)(this.z_size/16.0F+0.5F);z2++) {
			for(int x2=0;x2!=(int)(this.x_size/16.0F+0.5F);x2++) {
				this.renderchunks[x2][z2] = new RenderChunk(this);
				this.renderchunks[x2][z2].setRange(x2*16,z2*16);
				this.renderchunks[x2][z2].init();
			}
		}
	}
	
	public void setBlock(int x, int y, int z, int block_type) {
		Logger.log(this, "Set block("+Block.getName(block_type)+") at "+x+"-"+y+"-"+z);
		if(x>-1 && x<this.x_size && y>-1 && y<this.y_size && z>-1 && z<this.z_size) {
			this.block_array[(y*this.z_size+z)*this.x_size+x] = (byte)block_type;
			ClassicByte.view.renderer.force_chunk_update_x = (int) (x/(float)RenderChunk.CHUNK_LENGTH_OF_BORDER);
			ClassicByte.view.renderer.force_chunk_update_z = (int) (z/(float)RenderChunk.CHUNK_LENGTH_OF_BORDER);
			ClassicByte.view.renderer.force_chunk_update = true;
		}
	}
	
	public boolean canBlockSeeTheSky(int x, int y, int z) {
		int a;
		for(int k=y+1;k!=this.y_size;k++) {
			if(!this.isAir(x, k, z)) {
				return false;
			}
		}
		return true;
	}
	
	public int getBlock(int x, int y, int z) {
		if(x>-1 && x<x_size && y>-1 && y<y_size && z>-1 && z<z_size) {
			return block_array[(y*z_size+z)*x_size+x];
		} else {
			if(y<y_size) {
				return Block.BLOCK_UNDEFINIED;
			} else {
				return Block.BLOCK_AIR;
			}
		}
	}
	
	public boolean isAir(int x, int y, int z) {
		if(this.getBlock(x, y, z)==0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void clear() {
		this.clear(0);
	}
	
	public void clear(int block_type) {
		for(int k=0;k!=this.block_array.length;k++) {
			this.block_array[k] = (byte)block_type;
		}
		ClassicByte.view.renderer.force_chunk_update_x = -1;
		ClassicByte.view.renderer.force_chunk_update_z = -1;
		ClassicByte.view.renderer.force_chunk_update = true;
	}
	
	public RenderChunk getRenderChunkSafe(int x, int z) {
		if(x>=0 && z>=0 && x<(int)(this.x_size/16.0F+0.5F) && z<(int)(this.z_size/16.0F+0.5F)) {
			return this.renderchunks[x][z];
		}
		return null;
	}
	
	public void setRaining() {
		this.weather = 1;
	}
	
	public void setSnowing() {
		this.weather = 2;
	}
	
	public void setSunny() {
		this.weather = 0;
	}
	
	public boolean isRaining() {
		if(this.weather==1) {
			return true;
		}
		return false;
	}
	
	public boolean isSnowing() {
		if(this.weather==2) {
			return true;
		}
		return false;
	}
	
	public boolean isSunny() {
		if(this.weather==0) {
			return true;
		}
		return false;
	}
	
	public void setSkyColor(int red, int green, int blue) {
		this.sky_color_red = red;
		this.sky_color_green = green;
		this.sky_color_blue = blue;
	}
	
	public void setCloudColor(int red, int green, int blue) {
		this.cloud_color_red = red;
		this.cloud_color_green = green;
		this.cloud_color_blue = blue;
	}
	
	public int getSkyColorRedValue() {
		return this.sky_color_red;
	}
	
	public int getSkyColorGreenValue() {
		return this.sky_color_green;
	}
	
	public int getSkyColorBlueValue() {
		return this.sky_color_blue;
	}
	
	public int getCloudColorRedValue() {
		return this.cloud_color_red;
	}
	
	public int getCloudColorGreenValue() {
		return this.cloud_color_green;
	}
	
	public int getCloudColorBlueValue() {
		return this.cloud_color_blue;
	}
	
	public void setSideLevel(int side_level) {
		this.side_level = side_level;
	}
	
	public int getSideLevel() {
		return this.side_level;
	}
}
