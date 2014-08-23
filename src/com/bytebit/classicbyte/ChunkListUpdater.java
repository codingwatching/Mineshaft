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

public class ChunkListUpdater implements Runnable {

	
	public static boolean active = false;
	public static RenderChunk[] chunks_to_render = new RenderChunk[32];
	public static int chunks_to_render_size = 0;
	public static boolean disable_building = true;
	
	public void run() {
		if(ChunkListUpdater.disable_building) {
			return;
		}
		ChunkListUpdater.active = true;
		int t = RenderChunk.CHUNK_LENGTH_OF_BORDER;
		
		float[] fx = new float[3];
		float[] fy = new float[3];
		
		fx[0] = (float)(ClassicByte.view.renderer.player.getXPosition()-8*Math.cos(ClassicByte.view.renderer.player.camera_rot_x) + 8*Math.sin(ClassicByte.view.renderer.player.camera_rot_x));
		fy[0] = (float)(ClassicByte.view.renderer.player.getZPosition()-8*Math.sin(ClassicByte.view.renderer.player.camera_rot_x) - 8*Math.cos(ClassicByte.view.renderer.player.camera_rot_x));
		fx[1] = (float)(ClassicByte.view.renderer.player.getXPosition()+32*Math.cos(ClassicByte.view.renderer.player.camera_rot_x-((float)Math.PI/180F*60F)) + 32*Math.sin(ClassicByte.view.renderer.player.camera_rot_x-((float)Math.PI/180F*60F)));
		fy[1] = (float)(ClassicByte.view.renderer.player.getZPosition()+32*Math.sin(ClassicByte.view.renderer.player.camera_rot_x-((float)Math.PI/180F*60F)) - 32*Math.cos(ClassicByte.view.renderer.player.camera_rot_x-((float)Math.PI/180F*60F)));
		fx[2] = (float)(ClassicByte.view.renderer.player.getXPosition()+32*Math.cos(ClassicByte.view.renderer.player.camera_rot_x+((float)Math.PI/180F*60F)) + 32*Math.sin(ClassicByte.view.renderer.player.camera_rot_x+((float)Math.PI/180F*60F)));
		fy[2] = (float)(ClassicByte.view.renderer.player.getZPosition()+32*Math.sin(ClassicByte.view.renderer.player.camera_rot_x+((float)Math.PI/180F*60F)) - 32*Math.cos(ClassicByte.view.renderer.player.camera_rot_x+((float)Math.PI/180F*60F)));
		
		int local_x = (int)(ClassicByte.view.renderer.player.getXPosition()/t);
		int local_z = (int)(ClassicByte.view.renderer.player.getZPosition()/t);
		boolean r = false;
		
		ChunkListUpdater.chunks_to_render_size = 0;

		for(int z=0; z<(int)(ClassicByte.view.renderer.world.z_size/t+0.5F); z++) {
			for(int x=0; x<(int)(ClassicByte.view.renderer.world.x_size/t+0.5F); x++) {
				if(Polygon.contains(fx, fy, 3, x*t+t/2, z*t+t/2) || Polygon.contains(fx, fy, 3, x*t, z*t) || Polygon.contains(fx, fy, 3, x*t+t, z*t) || Polygon.contains(fx, fy, 3, x*t, z*t+t) || Polygon.contains(fx, fy, 3, x*t+t, z*t+t)) {
					chunks_to_render[chunks_to_render_size] = ClassicByte.view.renderer.world.renderchunks[x][z];
					chunks_to_render_size++;
					if(x==local_x && z==local_z) {
						r = true;
					}
				}
			}
		}
		
		if(!r) {
			chunks_to_render[chunks_to_render_size] = ClassicByte.view.renderer.world.getRenderChunkSafe((int)(ClassicByte.view.renderer.player.getXPosition()/t),(int)(ClassicByte.view.renderer.player.getZPosition()/t));
			chunks_to_render_size++;
		}
		ChunkListUpdater.active = false;
	}

}
