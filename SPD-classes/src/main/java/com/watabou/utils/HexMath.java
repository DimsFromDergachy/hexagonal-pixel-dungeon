/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 * 
 * Hexagonal Pixel Dungeon
 * Copyright (C) 2025 Dmitry Tyurnikov
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

package com.watabou.utils;

/**
 * <p>
 * Hexagonal math contains utilities.
 * Based on https://www.redblobgames.com/grids/hexagons */

 public class HexMath {

	public static boolean HEX_MODE = true;

	public static float RATIO = HEX_MODE ? 14f / 18f : 1f;

	/// Hex to pixel matrix:
	/// [ 14,  0 ]
	/// [  8, 16 ]
	private static float	A11 = 14f,	A12 =  0f;
	private static float	A21 =  8f,	A22 = 16f;

	/// Invert:
	/// [  1/14,    0 ]
	/// [ -1/28, 1/16 ]
	private static float 	H11 =  1f / 14f,	H12 = 0f;
	private static float 	H21 = -1f / 28f,	H22 = 1f / 16f;

	public static PointF HexToPixel(int x, int y)
	{
		// To cube coordinate
		int q = x;
		int r = y - x / 2;

		return new PointF(A11 * q + A12 * r, A21 * q + A22 * r);
	}

	public static Point PixelToHex(PointF point)
	{
		// TODO: Get rid of this by offseting coords to [9f, 8f]
		point = point.offset(-9f, -8f);

		// To cube coordinate
		float f_q = H11 * point.x + H12 * point.y;
		float f_r = H21 * point.x + H22 * point.y;
		float f_s = 0 - f_q - f_r;

		// Round cube
		int q = Math.round(f_q);
		int r = Math.round(f_r);
		int s = Math.round(f_s);

		float q_diff = Math.abs(q - f_q);
		float r_diff = Math.abs(r - f_r);
		float s_diff = Math.abs(s - f_s);

		if (q_diff > r_diff && q_diff > s_diff)
			q = 0 -r - s;
		if (r_diff > s_diff)
			r = 0 - q - s;
		else
			s = 0 - q - r;

		// To offset coordinate (odd-q vertical layout)
		return new Point(q, r + q / 2);
	}
}
