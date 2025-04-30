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

package com.watabou.utils;

public class HexMath {

	public static boolean HEX_MODE = true;

	public static float RATIO = HEX_MODE ? 14f / 18f : 1f;

	/// Hex to pixel matrix:
	/// [ 14,  0 ]
	/// [  8, 16 ]

	public static Point PixelToHex(PointF point)
	{
		float f_q = + (point.x - 9f) / 14f;
		float f_r = - (point.x - 9f) / 28f + (point.y - 8f) / 16f;
		float f_s = 0 - f_q - f_r;

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

		return new Point(q, r + q / 2);
	}
}
