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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES20;

public class RenderChunk {
	
	public static final int CHUNK_LENGTH_OF_BORDER = 16;
	
	public int x_start_position = 0;
	public int z_start_position = 0;
	
	private int chunk_size = 0;
	
	private ByteBuffer vertices;
	private ByteBuffer texturecoords;
	
	private World world;
	
	public int vbo_index_vertices = -1;
	public int vbo_index_texture = -1;
	public int vbo_size = 0;
	
	public RenderChunk(World w) {
		this.world = w;
	}
	
	public void init() {
		
	}
	
	public void init(int size_in_kb) {
		ByteBuffer v = ByteBuffer.allocateDirect(size_in_kb*1024*(CHUNK_LENGTH_OF_BORDER/16));
		this.vbo_size = size_in_kb*1024*(CHUNK_LENGTH_OF_BORDER/16);
		v.order(ByteOrder.nativeOrder());
		this.vertices = v;//.asShortBuffer();
		ByteBuffer v2 = ByteBuffer.allocateDirect(size_in_kb*1024/3*2*(CHUNK_LENGTH_OF_BORDER/16));
		v2.order(ByteOrder.nativeOrder());
		this.texturecoords = v2;//.asFloatBuffer();
	}
	
	public void buildVBO() {
		Logger.log(this, "Building vbo at position "+(this.x_start_position/16)+" - "+(this.z_start_position/16));
		if(this.vbo_index_vertices!=-1) {
			int[] f = {this.vbo_index_texture, this.vbo_index_vertices};
			GLES20.glDeleteBuffers(2, f, 0);
			this.vbo_index_vertices = -1;
			this.vbo_index_texture = -1;
			this.vbo_size = 0;
			Logger.log(this, "Deleted vbo at position "+(this.x_start_position/16)+" - "+(this.z_start_position/16));
		}
		this.chunk_size = 0;
		int max_blocks_to_be_rendered = 0;
		for(int z=this.z_start_position;z!=this.z_start_position+16;z++) {
			for(int x=this.x_start_position;x!=this.x_start_position+16;x++) {
				for(int y=0;y!=this.world.y_size;y++) {
					int id = this.world.getBlock(x, y, z);
					if(id!=0) {
						if(Block.isTranslucent(this.world.getBlock(x, y-1, z))) {
							max_blocks_to_be_rendered++;
						}
						if(Block.isTranslucent(this.world.getBlock(x, y+1, z))) {
							max_blocks_to_be_rendered++;
						}
						if(Block.isTranslucent(this.world.getBlock(x-1, y, z))) {
							max_blocks_to_be_rendered++;
						}
						if(Block.isTranslucent(this.world.getBlock(x+1, y, z))) {
							max_blocks_to_be_rendered++;
						}
						if(Block.isTranslucent(this.world.getBlock(x, y, z-1))) {
							max_blocks_to_be_rendered++;
						}
						if(Block.isTranslucent(this.world.getBlock(x, y, z+1))) {
							max_blocks_to_be_rendered++;
						}
					}
				}
			}
		}
		this.init((int)((max_blocks_to_be_rendered*256)/1024.0F)+1);
		this.texturecoords.position(0);
		this.vertices.position(0);
		for(int z2=this.z_start_position;z2!=this.z_start_position+16;z2++) {
			for(int x2=this.x_start_position;x2!=this.x_start_position+16;x2++) {
				for(int y2=0;y2!=this.world.y_size;y2++) {
					int id = this.world.getBlock(x2, y2, z2);
					if(id!=0) {
						
						int pos = Block.getBlockPositionInTextureMap(id);
						int r = (pos >> 16) & 0xFF;
						int g = (pos >> 8) & 0xFF;
						//int b = (pos >> 0) & 0xFF;
						
						float b = 1.0F/16.0F;
						float k = b*0.5F;
					    
					    float xt_up = r;
					    float yt_up = g;
					    
					    float xt_down = r;
					    float yt_down = g;
					    
					    float xt_x_up = r;
					    float yt_x_up = g;
					    
					    float xt_x_down = r;
					    float yt_x_down = g;
					    
					    float xt_z_up = r;
					    float yt_z_up = g;
					    
					    float xt_z_down = r;
					    float yt_z_down = g;
					    
					    if(id==Block.BLOCK_GRASS) {
					    	xt_up = 0;
					    	yt_up = 0;
					    	xt_down = 2;
					    	yt_down = 0;
					    }
					    
					    if(id==Block.BLOCK_LOG) {
					    	xt_up = 5;
					    	yt_up = 1;
					    	xt_down = 5;
					    	yt_down = 1;
					    }
					    
					    if(id==Block.BLOCK_BOOKSHELF) {
					    	xt_up = 4;
					    	yt_up = 0;
					    	xt_down = 4;
					    	yt_down = 0;
					    }
					    
					    if(id==Block.BLOCK_TNT) {
					    	xt_up = 9;
					    	yt_up = 0;
					    	xt_down = 10;
					    	yt_down = 0;
					    }
					    
					    if(id==Block.BLOCK_DOUBLE_SLAP) {
					    	xt_up = 6;
					    	yt_up = 0;
					    	xt_down = 6;
					    	yt_down = 0;
					    }
					    
					    if(id==Block.BLOCK_SLAB) {
					    	xt_up = 6;
					    	yt_up = 0;
					    	xt_down = 6;
					    	yt_down = 0;
					    }
					    
					    if(id==Block.BLOCK_SANDSTONE) {
					    	xt_up = 9;
					    	yt_up = 1;
					    	xt_down = 9;
					    	yt_down = 3;
					    }
					    
					    if(id==Block.BLOCK_PILLAR) {
					    	xt_up = 10;
					    	yt_up = 1;
					    	xt_down = 10;
					    	yt_down = 3;
					    }
					    
						if(!this.world.canBlockSeeTheSky(x2, y2, z2)) {
							yt_up = yt_up + 6;
							yt_down = yt_down + 6; 
						}
						
						if(!this.world.canBlockSeeTheSky(x2+1, y2, z2)) {
							yt_x_up = yt_x_up + 6;
						}
						
						if(!this.world.canBlockSeeTheSky(x2-1, y2, z2)) {
							yt_x_down = yt_x_down + 6;
						}
						
						if(!this.world.canBlockSeeTheSky(x2, y2, z2+1)) {
							yt_z_up = yt_z_up + 6;
						}
						
						if(!this.world.canBlockSeeTheSky(x2, y2, z2-1)) {
							yt_z_down = yt_z_down + 6;
						}
					    
					    
					    
					    xt_up = xt_up*b;
					    yt_up = yt_up*b;

					    xt_down = xt_down*b;
					    yt_down = yt_down*b;
					    
					    xt_x_up = xt_x_up*b;
					    yt_x_up = yt_x_up*b;
					    
					    xt_x_down = xt_x_down*b;
					    yt_x_down = yt_x_down*b;
					    
					    xt_z_up = xt_z_up*b;
					    yt_z_up = yt_z_up*b;
					    
					    xt_z_down = xt_z_down*b;
					    yt_z_down = yt_z_down*b;
					    
					    int model = Block.getModel(id);
					    
					    int x = x2*2;
					    int y = y2*2;
					    int z = z2*2;
						
						if(model==0) {
							if(Block.isTranslucent(this.world.getBlock(x2, y2-1, z2))) {
								short d[] = {
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(0+y),(short)(0+z),
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(0+y),(short)(0+z),
										(short)(0+x),(short)(0+y),(short)(0+z)
								};
								float c[] = {
										xt_down,yt_down+b,
										xt_down+b,yt_down+b,
										xt_down+b,yt_down,
										xt_down,yt_down+b,
										xt_down+b,yt_down,
										xt_down,yt_down
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							if(Block.isTranslucent(this.world.getBlock(x2, y2+1, z2))) {
								short d[] = {
										(short)(0+x),(short)(2+y),(short)(2+z),
										(short)(2+x),(short)(2+y),(short)(2+z),
										(short)(2+x),(short)(2+y),(short)(0+z),
										(short)(0+x),(short)(2+y),(short)(2+z),
										(short)(2+x),(short)(2+y),(short)(0+z),
										(short)(0+x),(short)(2+y),(short)(0+z)
								};
								float c[] = {
										xt_up,yt_up+b,
										xt_up+b,yt_up+b,
										xt_up+b,yt_up,
										xt_up,yt_up+b,
										xt_up+b,yt_up,
										xt_up,yt_up
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							if(Block.isTranslucent(this.world.getBlock(x2+1, y2, z2))) {
								short d[] = {
										(short)(2+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(2+y),(short)(2+z),
										(short)(2+x),(short)(2+y),(short)(0+z),
										(short)(2+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(2+y),(short)(0+z),
										(short)(2+x),(short)(0+y),(short)(0+z)
								};
								float c[] = {
										xt_x_up,yt_x_up+b,
										xt_x_up+b,yt_x_up+b,
										xt_x_up+b,yt_x_up,
										xt_x_up,yt_x_up+b,
										xt_x_up+b,yt_x_up,
										xt_x_up,yt_x_up
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							if(Block.isTranslucent(this.world.getBlock(x2-1, y2, z2))) {
								short d[] = {
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(0+x),(short)(2+y),(short)(2+z),
										(short)(0+x),(short)(2+y),(short)(0+z),
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(0+x),(short)(2+y),(short)(0+z),
										(short)(0+x),(short)(0+y),(short)(0+z)
								};
								float c[] = {
										xt_x_down,yt_x_down+b,
										xt_x_down+b,yt_x_down+b,
										xt_x_down+b,yt_x_down,
										xt_x_down,yt_x_down+b,
										xt_x_down+b,yt_x_down,
										xt_x_down,yt_x_down
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							
							if(Block.isTranslucent(this.world.getBlock(x2, y2, z2+1))) {
								short d[] = {
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(2+y),(short)(2+z),
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(2+y),(short)(2+z),
										(short)(0+x),(short)(2+y),(short)(2+z)
								};
								float c[] = {
										xt_z_up,yt_z_up+b,
										xt_z_up+b,yt_z_up+b,
										xt_z_up+b,yt_z_up,
										xt_z_up,yt_z_up+b,
										xt_z_up+b,yt_z_up,
										xt_z_up,yt_z_up
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							if(Block.isTranslucent(this.world.getBlock(x2, y2, z2-1))) {
								short d[] = {
										(short)(0+x),(short)(0+y),(short)(0+z),
										(short)(2+x),(short)(0+y),(short)(0+z),
										(short)(2+x),(short)(2+y),(short)(0+z),
										(short)(0+x),(short)(0+y),(short)(0+z),
										(short)(2+x),(short)(2+y),(short)(0+z),
										(short)(0+x),(short)(2+y),(short)(0+z)
								};
								float c[] = {
										xt_z_down,yt_z_down+b,
										xt_z_down+b,yt_z_down+b,
										xt_z_down+b,yt_z_down,
										xt_z_down,yt_z_down+b,
										xt_z_down+b,yt_z_down,
										xt_z_down,yt_z_down
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
						}
						
						if(model==2) {
							short d[] = {
									(short)(0+x),(short)(0+y),(short)(0+z),
									(short)(2+x),(short)(0+y),(short)(2+z),
									(short)(2+x),(short)(2+y),(short)(2+z),
									(short)(0+x),(short)(0+y),(short)(0+z),
									(short)(2+x),(short)(2+y),(short)(2+z),
									(short)(0+x),(short)(2+y),(short)(0+z)
							};
							float c[] = {
									xt_up+b,yt_up+b,
									xt_up,yt_up+b,
									xt_up,yt_up,
									xt_up+b,yt_up+b,
									xt_up,yt_up,
									xt_up+b,yt_up
							};
							this.putShortsIntoBuffer(this.vertices, d);
							this.putFloatsIntoBuffer(this.texturecoords, c);
							
							short d2[] = {
									(short)(0+x),(short)(0+y),(short)(2+z),
									(short)(2+x),(short)(0+y),(short)(0+z),
									(short)(2+x),(short)(2+y),(short)(0+z),
									(short)(0+x),(short)(0+y),(short)(2+z),
									(short)(2+x),(short)(2+y),(short)(0+z),
									(short)(0+x),(short)(2+y),(short)(2+z)
							};
							float c2[] = {
									xt_up+b,yt_up+b,
									xt_up,yt_up+b,
									xt_up,yt_up,
									xt_up+b,yt_up+b,
									xt_up,yt_up,
									xt_up+b,yt_up
							};
							this.putShortsIntoBuffer(this.vertices, d2);
							this.putFloatsIntoBuffer(this.texturecoords, c2);
							
							this.chunk_size = this.chunk_size + 2;
						}
						
						
						
						if(model==1) {
							if(Block.isTranslucent(this.world.getBlock(x2, y2-1, z2))) {
								short d[] = {
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(0+y),(short)(0+z),
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(0+y),(short)(0+z),
										(short)(0+x),(short)(0+y),(short)(0+z)
								};
								float c[] = {
										xt_down,yt_down+b,
										xt_down+b,yt_down+b,
										xt_down+b,yt_down,
										xt_down,yt_down+b,
										xt_down+b,yt_down,
										xt_down,yt_down
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							if(Block.isTranslucent(this.world.getBlock(x2, y2+1, z2))) {
								short d[] = {
										(short)(0+x),(short)(1+y),(short)(2+z),
										(short)(2+x),(short)(1+y),(short)(2+z),
										(short)(2+x),(short)(1+y),(short)(0+z),
										(short)(0+x),(short)(1+y),(short)(2+z),
										(short)(2+x),(short)(1+y),(short)(0+z),
										(short)(0+x),(short)(1+y),(short)(0+z)
								};
								float c[] = {
										xt_up,yt_up+b,
										xt_up+b,yt_up+b,
										xt_up+b,yt_up,
										xt_up,yt_up+b,
										xt_up+b,yt_up,
										xt_up,yt_up
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							if(Block.isTranslucent(this.world.getBlock(x2+1, y2, z2))) {
								short d[] = {
										(short)(2+x),(short)(0+y),(short)(0+z),
										(short)(2+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(1+y),(short)(2+z),
										(short)(2+x),(short)(0+y),(short)(0+z),
										(short)(2+x),(short)(1+y),(short)(2+z),
										(short)(2+x),(short)(1+y),(short)(0+z)
								};
								float c[] = {
										xt_x_up,yt_x_up+k,
										xt_x_up+k,yt_x_up+k,
										xt_x_up,yt_x_up,
										xt_x_up+k,yt_x_up+k,
										xt_x_up+k,yt_x_up,
										xt_x_up,yt_x_up
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							if(Block.isTranslucent(this.world.getBlock(x2-1, y2, z2))) {
								short d[] = {
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(0+x),(short)(0+y),(short)(0+z),
										(short)(0+x),(short)(1+y),(short)(0+z),
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(0+x),(short)(1+y),(short)(0+z),
										(short)(0+x),(short)(1+y),(short)(2+z)
								};
								float c[] = {
										xt_x_down,yt_x_down+k,
										xt_x_down+k,yt_x_down+k,
										xt_x_down,yt_x_down,
										xt_x_down+k,yt_x_down+k,
										xt_x_down+k,yt_x_down,
										xt_x_down,yt_x_down
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							
							if(Block.isTranslucent(this.world.getBlock(x2, y2, z2+1))) {
								short d[] = {
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(1+y),(short)(2+z),
										(short)(0+x),(short)(0+y),(short)(2+z),
										(short)(2+x),(short)(1+y),(short)(2+z),
										(short)(0+x),(short)(1+y),(short)(2+z)
								};
								float c[] = {
										xt_z_up,yt_z_up+k,
										xt_z_up+b,yt_z_up+k,
										xt_z_up+b,yt_z_up,
										xt_z_up,yt_z_up+k,
										xt_z_up+b,yt_z_up,
										xt_z_up,yt_z_up
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
							if(Block.isTranslucent(this.world.getBlock(x2, y2, z2-1))) {
								short d[] = {
										(short)(0+x),(short)(0+y),(short)(0+z),
										(short)(2+x),(short)(0+y),(short)(0+z),
										(short)(2+x),(short)(1+y),(short)(0+z),
										(short)(0+x),(short)(0+y),(short)(0+z),
										(short)(2+x),(short)(1+y),(short)(0+z),
										(short)(0+x),(short)(1+y),(short)(0+z)
								};
								float c[] = {
										xt_z_down,yt_z_down+k,
										xt_z_down+b,yt_z_down+k,
										xt_z_down+b,yt_z_down,
										xt_z_down,yt_z_down+k,
										xt_z_down+b,yt_z_down,
										xt_z_down,yt_z_down
								};
								this.putShortsIntoBuffer(this.vertices, d);
								this.putFloatsIntoBuffer(this.texturecoords, c);
								this.chunk_size++;
								//Logger.log(this, this.chunk_size+" - "+max_blocks_to_be_rendered);
							}
						}
						
						
						
						
					}
				}
			}
		}
	    this.vertices.position(0);
	    this.texturecoords.position(0);
	    Logger.log(this, "Built vbo at position "+(this.x_start_position/16)+" - "+(this.z_start_position/16)+" with a chunk size of "+this.chunk_size);
	    
	    int[] f = new int[1];
	    f[0] = -1;
		GLES20.glGenBuffers(1, f, 0);
		this.vbo_index_vertices = f[0];
		f[0] = -1;
		GLES20.glGenBuffers(1, f, 0);
		this.vbo_index_texture = f[0];
		if(GLES10.glGetError()!=0 || this.vbo_index_vertices==-1 || this.vbo_index_texture==-1) {
			ClassicByte.view.renderer.networkManager.disconnect();
			ClassicByte.view.renderer.networkManager.disconnectAlert("Sorry", "There was an error while trying to generate the vbos.");
			return;
		}
		Logger.log(this, "Vbo index at position "+(this.x_start_position/16)+" - "+(this.z_start_position/16)+" is "+vbo_index_vertices);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.vbo_index_vertices);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.vbo_size, this.vertices, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.vbo_index_texture);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.vbo_size/3*2, this.texturecoords, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		this.destroy();
	}
	
	public void putShortsIntoBuffer(ByteBuffer buffer, short[] x) {
		for(int k=0;k!=x.length;k++) {
			buffer.putShort(x[k]);
		}
	}
	public void putFloatsIntoBuffer(ByteBuffer buffer, float[] x) {
		for(int k=0;k!=x.length;k++) {
			buffer.putFloat(x[k]);
		}
	}
	
	public void renderVBO() {
		/*Logger.log(this, "Rendering vbo at position "+(this.x_start_position/16)+" - "+(this.z_start_position/16));
		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
	    GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
	    GLES10.glVertexPointer(3, GL10.GL_SHORT, 0, this.vertices);
	    GLES10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.texturecoords);
	    GLES10.glDisable(GLES10.GL_CULL_FACE);
	    GLES10.glColor4f(1.0F,1.0F,1.0F,1.0F);
	    GLES10.glDrawArrays(GL10.GL_TRIANGLES, 0, this.chunk_size*6);
	    GLES10.glEnable(GLES10.GL_CULL_FACE);*/
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.vbo_index_vertices);
		GLES11.glVertexPointer(3, GL10.GL_SHORT, 0, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.vbo_index_texture);
		GLES11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0);
	    GLES10.glDrawArrays(GL10.GL_TRIANGLES, 0, this.chunk_size*6);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	
	public void destroy() {
		Logger.log(this, "Destorying data of vbo at position "+(this.x_start_position/16)+" - "+(this.z_start_position/16));
		if(this.vertices!=null) {
			this.vertices = null;
		}
		if(this.texturecoords!=null) {
			this.texturecoords = null;
		}
		System.gc();
	}

	public void setRange(int x_range, int z_range) {
		Logger.log(this, "Setting a vbo to position "+(x_range/16)+" - "+(z_range/16));
		this.x_start_position = x_range;
		this.z_start_position = z_range;
	}
}
