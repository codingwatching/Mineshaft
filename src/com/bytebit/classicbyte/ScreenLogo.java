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

import android.graphics.Canvas;

public class ScreenLogo extends Screen {

	private long start_timer = System.currentTimeMillis();
	
	public ScreenLogo(ClassicByteView v) {
		super(v);
	}

	public void draw(Canvas c) {
		c.drawRGB(255, 255, 255);
		long delta_time = System.currentTimeMillis()-this.start_timer;
		if(delta_time>4000) {
			this.parent.setScreen(new ScreenLogin(this.parent));
		}
		if(delta_time>2100) {
			delta_time = 2101;
		}
		c.drawBitmap(TextureManager.getBitmap(41), (this.parent.renderer.width-Math.round(this.parent.renderer.width*0.6F))/2.0F, this.parent.renderer.height-(delta_time*0.0005F)*this.parent.renderer.height, this.parent.standard_paint);
	}
}