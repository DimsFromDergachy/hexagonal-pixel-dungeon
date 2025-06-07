/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.VertexBuffer;
import com.watabou.utils.HexMath;
import com.watabou.utils.Rect;
import com.watabou.utils.RectF;

/**
 * <p>
 * A HexTileMap transforms a regular TileMap to the hexagonal form.
 * Each 16x16 tiles are become wider up to 18x16, than corners are cut for make hexagonal shape.
 * Corners are kept fully transparent for good paving. */

public class HexTileMap extends TileMap {

	private float cellW;
	private float cellH;

	public HexTileMap(Object tx, int SIZE, int HEX_WIDTH, int HEX_HEIGHT) {

		super(tx, new TextureFilm( tx, SIZE, SIZE ) );

		if (!HexMath.HEX_MODE)
		{
			cellW = cellH = SIZE;
			return;
		}

		Pixmap original = TextureCache.getBitmap( tx );

		if (SIZE != 16)
			throw new RuntimeException( "DEBUG ASSERT: OriginalSize should be 16x16" + SIZE + original.getHeight() + original.getWidth() + tx.toString() );

		Pixmap hexagonal = new Pixmap(
			original.getWidth() / SIZE * HEX_WIDTH,
			original.getHeight(),
			original.getFormat() );

		hexagonal.setColor( 0x00000000 ); // RGBA only?
		hexagonal.fill();

		for (int i = 0; i < original.getWidth() / SIZE; i++)
			for (int j = 0; j < original.getHeight() / SIZE; j++)
			{
				for (int px = 0; px < SIZE; px++)
					for (int py = 0; py < SIZE; py++)
					{
						int x = i * SIZE + px;
						int y = j * SIZE + py;

						int hx = i * HEX_WIDTH + px + 1;
						int hy = y;

						// cut corners for making hexagonal shape
						int d1 = 2 * (-6 + 2 * px + py) + 2 + 1;		//	-6 + 2x + y = 0
						int d2 = 2 * (26 - 2 * px + py) - 2 + 1;		//	26 - 2x + y = 0
						int d3 = 2 * (42 - 2 * px - py) - 2 - 1;		//	42 - 2x - y = 0
						int d4 = 2 * (10 + 2 * px - py) + 2 - 1;		//	10 + 2x - y = 0

						int d = Math.min( Math.min( d1, d2 ), Math.min( d3, d4 ) );

						if (d < 0)
							continue; // keep corners fully transparent

						hexagonal.drawPixel( hx, hy, original.getPixel( x, y ) );
					}

				// copy a few pixels by hands
				hexagonal.drawPixel( i * HEX_WIDTH +  0, j * HEX_HEIGHT + 7,
				  original.getPixel( i * SIZE      +  0, j * SIZE       + 7 ));
				hexagonal.drawPixel( i * HEX_WIDTH +  0, j * HEX_HEIGHT + 8,
				  original.getPixel( i * SIZE      +  0, j * SIZE       + 8 ));
				hexagonal.drawPixel( i * HEX_WIDTH + 17, j * HEX_HEIGHT + 7,
				  original.getPixel( i * SIZE      + 15, j * SIZE       + 7 ));
				hexagonal.drawPixel( i * HEX_WIDTH + 17, j * HEX_HEIGHT + 8,
				  original.getPixel( i * SIZE      + 15, j * SIZE       + 8 ));
			}

		original.dispose(); // keep it for info cells?

		this.texture = new SmartTexture( hexagonal );
		this.tileSet = new TextureFilm( hexagonal, HEX_WIDTH, HEX_HEIGHT );

		cellW = HEX_WIDTH;
		cellH = HEX_HEIGHT;

	}

	private synchronized void moveToUpdating(){
		updating = new Rect(updated);
		updated.setEmpty();
	}

	protected void updateVertices() {

		moveToUpdating();

		float x1, y1, x2, y2, y0;
		int pos;
		RectF uv;

		y0 = cellH * updating.top;

		for (int i=updating.top; i < updating.bottom; i++) {

			x1 = HexMath.RATIO * cellW * updating.left;
			x2 = x1 + cellW;

			pos = i * mapWidth + updating.left;

			for (int j=updating.left; j < updating.right; j++) {

				y1 = y0 + (HexMath.HEX_MODE ? (j & 1) * 0.5f * cellH : 0);
				y2 = y1 + cellH;

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
				x1 += cellW * HexMath.RATIO;
				x2 += cellW * HexMath.RATIO;
			}

			y0 += cellH;
		}
	}

	@Override
	public void draw() {

		super.draw();

		if (!updated.isEmpty()) {
			updateVertices();
			if (buffer == null)
				buffer = new VertexBuffer(quads);
			else {
				if (fullUpdate) {
					buffer.updateVertices(quads);
					fullUpdate = false;
				} else {
					buffer.updateVertices(quads,
							topLeftUpdating * 16,
							bottomRightUpdating * 16);
				}
			}
			topLeftUpdating = -1;
			updating.setEmpty();
		}

		NoosaScript script = script();

		texture.bind();

		script.uModel.valueM4( matrix );
		script.lighting(
				rm, gm, bm, am,
				ra, ga, ba, aa );

		script.camera( camera );

		script.drawQuadSet( buffer, size, 0 );

	}
}
