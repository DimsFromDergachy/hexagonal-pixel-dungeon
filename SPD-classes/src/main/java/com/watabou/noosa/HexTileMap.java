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

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.VertexBuffer;
import com.watabou.utils.GameMath;
import com.watabou.utils.Rect;
import com.watabou.utils.RectF;

public class HexTileMap extends TileMap {

	private float cellW;
	private float cellH;

	public HexTileMap(Object tx, TextureFilm tileSet) {

		super(tx, tileSet);

		// TODO: Inject hex transformation
		// tx is string (filename of the asset)
		// tileSet has the bitmap (16x16 each tile?)

		// 1. Transform the image
		// 2. Put to the TextureCache (by the same key)

		// protected SmartTexture texture; - transform to hexagonal
		// protected TextureFilm tileSet;  - transform to hexagonal


		Pixmap original = TextureCache.getBitmap( tx );

		if (original.getHeight() % 16 != 0 || original.getWidth() % 16 != 0)
			throw new RuntimeException( "DEBUG ASSERT: Bitmap should be 16x16" + original.getHeight() + original.getWidth() + tx.toString() );

		Pixmap hexagonal = new Pixmap( original.getWidth() / 16 * 18, original.getHeight(), original.getFormat() );

		hexagonal.setColor( 0x00000000 );
		hexagonal.fill();

		for (int i = 0; i < original.getWidth() / 16; i++)
			for (int j = 0; j < original.getHeight() / 16; j++)
			{
				for (int px = 0; px < 16; px++)
					for (int py = 0; py < 16; py++)
					{
						int x = i * 16 + px;
						int y = j * 16 + py;

						int hx = i * 18 + px + 1;
						int hy = y;

						int d1 = -12 + 4 * px + 2 * py + 3;		//	2x + y =   6
						int d2 =  52 - 4 * px + 2 * py - 1;		//	2x - y =  26
						int d3 =  84 - 4 * px - 2 * py - 3;		//	2x + y =  42
						int d4 =  20 + 4 * px - 2 * py + 1;		//	2x - y = -10

						int d = Math.min( Math.min( d1, d2 ), Math.min( d3, d4 ) );

						if (d < 0)
							continue;

						hexagonal.setColor( original.getPixel( x, y ) );
						hexagonal.drawPixel( hx, hy );
					}

				hexagonal.drawPixel( i * 18 +  0, j * 16 + 7,
				  original.getPixel( i * 16 +  0, j * 16 + 7 ));
				hexagonal.drawPixel( i * 18 +  0, j * 16 + 8,
				  original.getPixel( i * 16 +  0, j * 16 + 8 ));
				hexagonal.drawPixel( i * 18 + 17, j * 16 + 7,
				  original.getPixel( i * 16 + 15, j * 16 + 7 ));
				hexagonal.drawPixel( i * 18 + 17, j * 16 + 8,
				  original.getPixel( i * 16 + 15, j * 16 + 8 ));
			}

		original.dispose(); // keep it for info cells?

		this.texture = new SmartTexture( hexagonal );
		this.tileSet = new TextureFilm( hexagonal, 18, 16 );

		RectF r = this.tileSet.get( 0 );
		cellW = this.tileSet.width( r );
		cellH = this.tileSet.height( r );

		if (cellH != 16 || cellW != 18)
			throw new RuntimeException( "DEBUG ASSERT: Bitmap should be 16x16: " + cellH + cellW + tx.toString() );

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

			x1 = GameMath.HEX_RATIO * cellW * updating.left;
			x2 = x1 + cellW;

			pos = i * mapWidth + updating.left;

			for (int j=updating.left; j < updating.right; j++) {

				y1 = y0 + (GameMath.HEX_MODE ? (j & 1) * 0.5f * cellH : 0);
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
				x1 += cellW * GameMath.HEX_RATIO;
				x2 += cellW * GameMath.HEX_RATIO;
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
