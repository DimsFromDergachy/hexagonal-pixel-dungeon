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

package com.watabou.noosa;

import java.nio.Buffer;
import java.util.Arrays;

import com.watabou.utils.GameMath;
import com.watabou.utils.Rect;
import com.watabou.utils.RectF;

public class HexTileMap extends TileMap {

	private float cellW;
	private float cellH;

	public HexTileMap(Object tx, TextureFilm tileSet) {
		super(tx, tileSet);

		RectF r = tileSet.get( 0 );
		cellW = tileSet.width( r );
		cellH = tileSet.height( r );
	}

	private synchronized void moveToUpdating(){
		updating = new Rect(updated);
		updated.setEmpty();
	}

	protected void updateVertices() {

		moveToUpdating();

		float x1, y1, x2, y2;
		int pos;
		RectF uv;

		y1 = cellH * (updating.top + GameMath.PIXEL);
		y2 = y1 + cellH * (1 - GameMath.PIXEL);

		for (int i=updating.top; i < updating.bottom; i++) {

			x1 = cellW * (updating.left + GameMath.PIXEL);
			x1 += (i % 2 == 0) ? 0 : 0.5 * cellW;
			x2 = x1 + cellW * (1 - GameMath.PIXEL);

			pos = i * mapWidth + updating.left;

			for (int j=updating.left; j < updating.right; j++) {

				if (topLeftUpdating == -1)
					topLeftUpdating = pos;

				bottomRightUpdating = pos + 1;

				((Buffer)quads).position(pos*16);

				uv = tileSet.get(data[pos]);

				if (needsRender(pos) && uv != null) {

					vertices[0] = x1;
					vertices[1] = y1;

					vertices[2] = uv.left;
					vertices[3] = uv.top;

					vertices[4] = x2;
					vertices[5] = y1;

					vertices[6] = uv.right;
					vertices[7] = uv.top;

					vertices[8] = x2;
					vertices[9] = y2;

					vertices[10] = uv.right;
					vertices[11] = uv.bottom;

					vertices[12] = x1;
					vertices[13] = y2;

					vertices[14] = uv.left;
					vertices[15] = uv.bottom;

				} else {

					//If we don't need to draw this tile simply set the quad to size 0 at 0, 0.
					// This does result in the quad being drawn, but we are skipping all
					// pixel-filling. This is better than fully skipping rendering as we
					// don't need to manage a buffer of drawable tiles with insertions/deletions.
					Arrays.fill(vertices, 0);
				}

				quads.put(vertices);

				pos++;
				x1 += cellW;
				x2 += cellW;
			}

			y1 += cellH;
			y2 += cellH;
		}

	}
}
