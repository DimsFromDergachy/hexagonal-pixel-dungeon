/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.gltextures;

import com.badlogic.gdx.graphics.Pixmap;

public class HexSmartTexture extends SmartTexture {

	public HexSmartTexture( Pixmap bitmap ) {
		super( bitmap, NEAREST, CLAMP, false );

		// Debug asserts
		// if (bitmap.getHeight() != 16 || bitmap.getWidth() != 16)
		// 	throw new RuntimeException("DEBUG ASSERT: Bitmap should be 16x16" + bitmap.getHeight() + bitmap.getWidth());

	}
}
