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

import android.graphics.Color;

public class Block {
	
	/*--------------Default blocks--------------*/
	
	public static final int BLOCK_AIR = 0;
	public static final int BLOCK_STONE = 1;
	public static final int BLOCK_GRASS = 2;
	public static final int BLOCK_DIRT = 3;
	public static final int BLOCK_COBBLESTONE = 4;
	public static final int BLOCK_WOOD = 5;
	public static final int BLOCK_SAPLING = 6;
	public static final int BLOCK_BEDROCK = 7;
	public static final int BLOCK_WATER = 8;
	public static final int BLOCK_WATER_STATIC = 9;
	public static final int BLOCK_LAVA = 10;
	public static final int BLOCK_LAVA_STATIC = 11;
	public static final int BLOCK_SAND = 12;
	public static final int BLOCK_GRAVEL = 13;
	public static final int BLOCK_GOLD_ORE = 14;
	public static final int BLOCK_IRON_ORE = 15;
	public static final int BLOCK_COAL_ORE = 16;
	public static final int BLOCK_LOG = 17;
	public static final int BLOCK_LEAVES = 18;
	public static final int BLOCK_SPONGE = 19;
	public static final int BLOCK_GLASS = 20;
	public static final int BLOCK_RED_CLOTH = 21;
	public static final int BLOCK_ORANGE_CLOTH = 22;
	public static final int BLOCK_YELLOW_CLOTH = 23;
	public static final int BLOCK_LIME_CLOTH = 24;
	public static final int BLOCK_GREEN_CLOTH = 25;
	public static final int BLOCK_AQUA_GREEN_CLOTH = 26;
	public static final int BLOCK_CYAN_CLOTH = 27;
	public static final int BLOCK_BLUE_CLOTH = 28;
	public static final int BLOCK_PURPLE_CLOTH = 29;
	public static final int BLOCK_INDIGO_CLOTH = 30;
	public static final int BLOCK_VIOLET_CLOTH = 31;
	public static final int BLOCK_MAGENTA_CLOTH = 32;
	public static final int BLOCK_PINK_CLOTH = 33;
	public static final int BLOCK_BLACK_CLOTH = 34;
	public static final int BLOCK_GRAY_CLOTH = 35;
	public static final int BLOCK_WHITE_CLOTH = 36;
	public static final int BLOCK_DANDELION = 37;
	public static final int BLOCK_ROSE = 38;
	public static final int BLOCK_BROWN_MUSHROOM = 39;
	public static final int BLOCK_RED_MUSHROOM = 40;
	public static final int BLOCK_GOLD_BLOCK = 41;
	public static final int BLOCK_IRON_BLOCK = 42;
	public static final int BLOCK_DOUBLE_SLAP = 43;
	public static final int BLOCK_SLAB = 44;
	public static final int BLOCK_BRICK_BLOCK = 45;
	public static final int BLOCK_TNT = 46;
	public static final int BLOCK_BOOKSHELF = 47;
	public static final int BLOCK_MOSSSTONE = 48;
	public static final int BLOCK_OBSIDIAN = 49;
	
	/*----------Block Support Level 1----------*/
	
	public static final int BLOCK_COBBLESTONESLAB = 0x32;
	public static final int BLOCK_ROPE = 0x33;
	public static final int BLOCK_SANDSTONE = 0x34;
	public static final int BLOCK_SNOW = 0x35;
	public static final int BLOCK_FIRE = 0x36;
	public static final int BLOCK_LIGHTPINKWOOL = 0x37;
	public static final int BLOCK_FORESTGREENWOOL = 0x38;
	public static final int BLOCK_BROWNWOOL = 0x39;
	public static final int BLOCK_DEEPBLUE = 0x3A;
	public static final int BLOCK_TURQUOISE = 0x3B;
	public static final int BLOCK_ICE = 0x3C;
	public static final int BLOCK_CERAMICTILE = 0x3D;
	public static final int BLOCK_MAGMA = 0x3E;
	public static final int BLOCK_PILLAR = 0x3F;
	public static final int BLOCK_CRATE = 0x40;
	public static final int BLOCK_STONEBRICK = 0x41;
	
	public static boolean canGoTrough(int block_id) {
		if(block_id==Block.BLOCK_WATER || block_id==Block.BLOCK_WATER_STATIC || block_id==Block.BLOCK_LAVA || block_id==Block.BLOCK_LAVA_STATIC || block_id==Block.BLOCK_AIR || block_id==Block.BLOCK_FIRE || block_id==Block.BLOCK_ROPE || block_id==Block.BLOCK_SNOW || block_id==Block.BLOCK_SAPLING || block_id==Block.BLOCK_RED_MUSHROOM || block_id==Block.BLOCK_BROWN_MUSHROOM || block_id==Block.BLOCK_DANDELION || block_id==Block.BLOCK_ROSE) {
			return true;
		}
		return false;
	}
	
	public static int getModel(int block_id) {
		if(block_id==Block.BLOCK_SLAB || block_id==Block.BLOCK_COBBLESTONESLAB || block_id==Block.BLOCK_SNOW) {
			return 1;
		}
		if(block_id==Block.BLOCK_DANDELION || block_id==Block.BLOCK_ROSE || block_id==Block.BLOCK_ROPE || block_id==Block.BLOCK_RED_MUSHROOM || block_id==Block.BLOCK_BROWN_MUSHROOM || block_id==Block.BLOCK_FIRE || block_id==Block.BLOCK_SAPLING) {
			return 2;
		}
		
		return 0;
	}
	
	public static String getName(int block_id) {
		switch(block_id) {
			case BLOCK_STONE: return "Stone";
			case BLOCK_GRASS: return "Grass";
			case BLOCK_DIRT: return "Dirt";
			case BLOCK_COBBLESTONE: return "Cobblestone";
			case BLOCK_WOOD: return "Wood";
			case BLOCK_SAPLING: return "Sapling";
			case BLOCK_BEDROCK: return "Bedrock";
			case BLOCK_WATER: return "Water";
			case BLOCK_WATER_STATIC: return "Static Water";
			case BLOCK_LAVA: return "Lava";
			case BLOCK_LAVA_STATIC: return "Static Lava";
			case BLOCK_SAND: return "Sand";
			case BLOCK_GRAVEL: return "Gravel";
			case BLOCK_GOLD_ORE: return "Gold Ore";
			case BLOCK_IRON_ORE: return "Iron Ore";
			case BLOCK_COAL_ORE: return "Coal Ore";
			case BLOCK_LOG: return "Log";
			case BLOCK_LEAVES: return "Leaves";
			case BLOCK_SPONGE: return "Sponge";
			case BLOCK_GLASS: return "Glass";
			case BLOCK_RED_CLOTH: return "Red Cloth";
			case BLOCK_ORANGE_CLOTH: return "Orange Cloth";
			case BLOCK_YELLOW_CLOTH: return "Yellow Cloth";
			case BLOCK_LIME_CLOTH: return "Lime Cloth";
			case BLOCK_GREEN_CLOTH: return "Green Cloth";
			case BLOCK_AQUA_GREEN_CLOTH: return "Aqua green Cloth";
			case BLOCK_CYAN_CLOTH: return "Cyan Cloth";
			case BLOCK_BLUE_CLOTH: return "Blue Cloth";
			case BLOCK_PURPLE_CLOTH: return "Purple Cloth";
			case BLOCK_INDIGO_CLOTH: return "Indigo Cloth";
			case BLOCK_VIOLET_CLOTH: return "Violet Cloth";
			case BLOCK_MAGENTA_CLOTH: return "Magenta Cloth";
			case BLOCK_PINK_CLOTH: return "Pink Cloth";
			case BLOCK_BLACK_CLOTH: return "Black Cloth";
			case BLOCK_GRAY_CLOTH: return "Gray Cloth";
			case BLOCK_WHITE_CLOTH: return "White Cloth";
			case BLOCK_DANDELION: return "Dandelion";
			case BLOCK_ROSE: return "Rose";
			case BLOCK_BROWN_MUSHROOM: return "Brown Mushroom";
			case BLOCK_RED_MUSHROOM: return "Red Mushroom";
			case BLOCK_GOLD_BLOCK: return "Gold Block";
			case BLOCK_IRON_BLOCK: return "Iron Block";
			case BLOCK_DOUBLE_SLAP: return "Double slap";
			case BLOCK_SLAB: return "Slap";
			case BLOCK_BRICK_BLOCK: return "Brick";
			case BLOCK_TNT: return "TNT";
			case BLOCK_BOOKSHELF: return "Bookshelf";
			case BLOCK_MOSSSTONE: return "Mossy Cobblestone";
			case BLOCK_OBSIDIAN: return "Obsidian";
			case BLOCK_COBBLESTONESLAB: return "Cobblestone Slap";
			case BLOCK_ROPE: return "Rope";
			case BLOCK_SANDSTONE: return "Sandstone";
			case BLOCK_SNOW: return "Snow";
			case BLOCK_FIRE: return "Fire";
			case BLOCK_LIGHTPINKWOOL: return "Light Pink Cloth";
			case BLOCK_FORESTGREENWOOL: return "Forest Green Cloth";
			case BLOCK_BROWNWOOL: return "Brown Cloth";
			case BLOCK_DEEPBLUE: return "Deep Blue Cloth";
			case BLOCK_TURQUOISE: return "Turquoise Cloth";
			case BLOCK_ICE: return "Ice";
			case BLOCK_CERAMICTILE: return "Ceramic Tile";
			case BLOCK_MAGMA: return "Magma";
			case BLOCK_PILLAR: return "Pillar";
			case BLOCK_CRATE: return "Crate";
			case BLOCK_STONEBRICK: return "Stonebrick";
		}
		return "Future Block";
	}
	
	public static boolean isTranslucent(int block_id) {
		if(Options.fancy_graphics) {
			if(block_id==Block.BLOCK_GLASS) {
				return true;
			}
			if(block_id==Block.BLOCK_LEAVES) {
				return true;
			}
			if(block_id==Block.BLOCK_WATER) {
				return true;
			}
			if(block_id==Block.BLOCK_WATER_STATIC) {
				return true;
			}
			if(block_id==Block.BLOCK_LAVA) {
				return true;
			}
			if(block_id==Block.BLOCK_LAVA_STATIC) {
				return true;
			}
		}
		
		if(block_id==Block.BLOCK_SLAB) {
			return true;
		}
		
		if(block_id==Block.BLOCK_COBBLESTONESLAB) {
			return true;
		}
		
		if(block_id==Block.BLOCK_SNOW) {
			return true;
		}
		
		if(block_id==Block.BLOCK_AIR) {
			return true;
		}
		
		if(block_id==Block.BLOCK_ROPE || block_id==Block.BLOCK_ROSE || block_id==Block.BLOCK_DANDELION || block_id==Block.BLOCK_BROWNWOOL || block_id==Block.BLOCK_RED_MUSHROOM || block_id==Block.BLOCK_FIRE || block_id==Block.BLOCK_SAPLING) {
			return true;
		}
		
		return false;
	}
	
	public static int getBlockPositionInTextureMap(int block_id) {
		switch(block_id) {
			case BLOCK_STONE: return Color.rgb(1, 0, 0);
			case BLOCK_GRASS: return Color.rgb(3, 0, 0);
			case BLOCK_DIRT: return Color.rgb(2, 0, 0);
			case BLOCK_COBBLESTONE: return Color.rgb(0, 1, 0);
			case BLOCK_WOOD: return Color.rgb(4, 0, 0);
			case BLOCK_SAPLING: return Color.rgb(15, 0, 0);
			case BLOCK_BEDROCK: return Color.rgb(1, 1, 0);
			case BLOCK_WATER: return Color.rgb(14, 0, 0);
			case BLOCK_WATER_STATIC: return Color.rgb(14, 0, 0);
			case BLOCK_LAVA: return Color.rgb(14, 1, 0);
			case BLOCK_LAVA_STATIC: return Color.rgb(14, 1, 0);
			case BLOCK_SAND: return Color.rgb(2, 1, 0);
			case BLOCK_GRAVEL: return Color.rgb(3, 1, 0);
			case BLOCK_GOLD_ORE: return Color.rgb(0, 2, 0);
			case BLOCK_IRON_ORE: return Color.rgb(1, 2, 0);
			case BLOCK_COAL_ORE: return Color.rgb(2, 2, 0);
			case BLOCK_LOG: return Color.rgb(4, 1, 0);
			case BLOCK_LEAVES: return Color.rgb(6, 1, 0);
			case BLOCK_SPONGE: return Color.rgb(0, 3, 0);
			case BLOCK_GLASS: return Color.rgb(1, 3, 0);
			case BLOCK_RED_CLOTH: return Color.rgb(0, 4, 0);
			case BLOCK_ORANGE_CLOTH: return Color.rgb(1, 4, 0);
			case BLOCK_YELLOW_CLOTH: return Color.rgb(2, 4, 0);
			case BLOCK_LIME_CLOTH: return Color.rgb(3, 4, 0);
			case BLOCK_GREEN_CLOTH: return Color.rgb(4, 4, 0);
			case BLOCK_AQUA_GREEN_CLOTH: return Color.rgb(5, 4, 0);
			case BLOCK_CYAN_CLOTH: return Color.rgb(6, 4, 0);
			case BLOCK_BLUE_CLOTH: return Color.rgb(7, 4, 0);
			case BLOCK_PURPLE_CLOTH: return Color.rgb(8, 4, 0);
			case BLOCK_INDIGO_CLOTH: return Color.rgb(9, 4, 0);
			case BLOCK_VIOLET_CLOTH: return Color.rgb(10, 4, 0);
			case BLOCK_MAGENTA_CLOTH: return Color.rgb(11, 4, 0);
			case BLOCK_PINK_CLOTH: return Color.rgb(12, 4, 0);
			case BLOCK_BLACK_CLOTH: return Color.rgb(13, 4, 0);
			case BLOCK_GRAY_CLOTH: return Color.rgb(14, 4, 0);
			case BLOCK_WHITE_CLOTH: return Color.rgb(15, 4, 0);
			case BLOCK_DANDELION: return Color.rgb(13, 0, 0);
			case BLOCK_ROSE: return Color.rgb(12, 0, 0);
			case BLOCK_BROWN_MUSHROOM: return Color.rgb(13, 1, 0);
			case BLOCK_RED_MUSHROOM: return Color.rgb(12, 1, 0);
			case BLOCK_GOLD_BLOCK: return Color.rgb(8, 1, 0);
			case BLOCK_IRON_BLOCK: return Color.rgb(7, 1, 0);
			case BLOCK_DOUBLE_SLAP: return Color.rgb(5, 0, 0);
			case BLOCK_SLAB: return Color.rgb(5, 0, 0);
			case BLOCK_BRICK_BLOCK: return Color.rgb(7, 0, 0);
			case BLOCK_TNT: return Color.rgb(8, 0, 0);
			case BLOCK_BOOKSHELF: return Color.rgb(3, 2, 0);
			case BLOCK_MOSSSTONE: return Color.rgb(4, 2, 0);
			case BLOCK_OBSIDIAN: return Color.rgb(5, 2, 0);
			case BLOCK_COBBLESTONESLAB: return Color.rgb(0, 1, 0);
			case BLOCK_ROPE: return Color.rgb(11, 0, 0);
			case BLOCK_SANDSTONE: return Color.rgb(9, 2, 0);
			case BLOCK_SNOW: return Color.rgb(2, 3, 0);
			case BLOCK_FIRE: return Color.rgb(6, 2, 0);
			case BLOCK_LIGHTPINKWOOL: return Color.rgb(0, 5, 0);
			case BLOCK_FORESTGREENWOOL: return Color.rgb(1, 5, 0);
			case BLOCK_BROWNWOOL: return Color.rgb(2, 5, 0);
			case BLOCK_DEEPBLUE: return Color.rgb(3, 5, 0);
			case BLOCK_TURQUOISE: return Color.rgb(4, 5, 0);
			case BLOCK_ICE: return Color.rgb(3, 3, 0);
			case BLOCK_CERAMICTILE: return Color.rgb(6, 3, 0);
			case BLOCK_MAGMA: return Color.rgb(6, 5, 0);
			case BLOCK_PILLAR: return Color.rgb(10, 2, 0);
			case BLOCK_CRATE: return Color.rgb(5, 3, 0);
			case BLOCK_STONEBRICK: return Color.rgb(4, 3, 0);
		}
		return Color.rgb(15, 15, 0);
	}
	
}