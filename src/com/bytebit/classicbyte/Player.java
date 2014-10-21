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

public class Player {
	private float posx = 0.0F;
	private float posy = 0.0F;
	private float posz = 0.0F;
	
	private float spawnx = 0.5F;
	private float spawny = 32.0F;
	private float spawnz = 0.5F;
	
	public float camera_rot_x = 0.0F;
	public float camera_rot_y = 0.0F;
	
	private int heldblocktype = Block.BLOCK_STONE;
	
	private String name = "Steve";
	
	private boolean isOp = false;
	
	private boolean isMoving = false;
	private long moving_timer = System.currentTimeMillis();
	
	private boolean allow_block_change = true;
	
	private float click_distance = 5;
	
	public void setClickDistance(float distance) {
		this.click_distance = distance;
	}
	
	public float getClickDistance() {
		return this.click_distance;
	}
	
	public void setAllowBlockChange(boolean allow) {
		this.allow_block_change = allow;
	}
	
	public boolean isBlockChangeAllowed() {
		return this.allow_block_change;
	}
	
	public void run() {
		if(System.currentTimeMillis()-this.moving_timer>500) {
			this.setMoving(false);
			this.moving_timer = System.currentTimeMillis();
		}
	}
	
	public void setMoving(boolean x) {
		this.isMoving = x;
	}
	
	public boolean isMoving() {
		return this.isMoving;
	}
	
	public void setOp(boolean op) {
		this.isOp = op;
	}
	
	public boolean isOp() {
		return this.isOp;
	}
	
	public void setSpawn(float x, float y, float z) {
		this.spawnx = x;
		this.spawny = y;
		this.spawnz = z;
	}
	
	public void setSpawn(int x, int y, int z) {
		this.setSpawn(x+0.5F,y,z+0.5F);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public float getXPosition() {
		return this.posx;
	}
	
	public float getYPosition() {
		return this.posy;
	}
	
	public float getZPosition() {
		return this.posz;
	}
	
	public void setPosition(float x, float y, float z) {
		this.posx = x;
		this.posy = y;
		this.posz = z;
		this.setMoving(true);
	}
	
	public float getSpawnX() {
		return this.spawnx;
	}
	
	public float getSpawnY() {
		return this.spawny;
	}
	
	public float getSpawnZ() {
		return this.spawnz;
	}
	
	public void setHeldBlock(int type) {
		this.heldblocktype = type;
	}
	
	public int getHeldBlock() {
		return this.heldblocktype;
	}
	
	public float getPlayerEyeHeight() {
		return 1.75F;
	}
}
