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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import java.nio.Buffer;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileMap;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.VertexBuffer;
import com.watabou.noosa.Camera;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.NoosaScript;
import com.watabou.utils.HexMath;
import com.watabou.utils.PointF;

public class Sprite extends MovieClip {

	public static final int SIZE	= 16;

	//the amount the sprite is raised from flat when viewed in a raised perspective
	protected float perspectiveRaise    = 5 / 16f; //5 pixels

	//the width and height of the shadow are a percentage of sprite size
	//offset is the number of pixels the shadow is moved down or up (handy for some animations)
	protected boolean renderShadow  = false;
	protected float shadowWidth     = 1f;
	protected float shadowHeight    = 0.25f;
	protected float shadowOffset    = 0.5f;

	public Sprite() {
		super();
	}
	
	public Sprite( Object tx ) {
		super( tx );
	}

	public PointF worldToCamera( int cell ) {
		final int size = DungeonTileMap.SIZE;

		int posX = cell % Dungeon.level.width();
		int posY = cell / Dungeon.level.width();

		if (HexMath.HEX_MODE)
			return new PointF(
				PixelScene.align(Camera.main, HexMath.RATIO * ((posX + 0.5f) * 18 - width() * 0.5f)),
				PixelScene.align(Camera.main, (1f + posY + (posX & 1) * 0.5f) * size - height() - size * perspectiveRaise)
			);
		else
			return new PointF(
				PixelScene.align(Camera.main, (posX + 0.5f) * size - width() * 0.5f),
				PixelScene.align(Camera.main, (posY + 1.0f) * size - height() - size * perspectiveRaise)
			);
		}

	public void place( int cell ) {
		point( worldToCamera( cell ) );
	}

	private float[] shadowMatrix = new float[16];

	@Override
	protected void updateMatrix() {
		super.updateMatrix();
		Matrix.copy(matrix, shadowMatrix);
		Matrix.translate(shadowMatrix,
				(width() * (1f - shadowWidth)) / 2f,
				(height() * (1f - shadowHeight)) + shadowOffset);
		Matrix.scale(shadowMatrix, shadowWidth, shadowHeight);
	}

	@Override
	public void draw() {
		if (texture == null || (!dirty && buffer == null))
			return;

		if (renderShadow) {
			if (dirty) {
				((Buffer)verticesBuffer).position(0);
				verticesBuffer.put(vertices);
				if (buffer == null)
					buffer = new VertexBuffer(verticesBuffer);
				else
					buffer.updateVertices(verticesBuffer);
				dirty = false;
			}

			NoosaScript script = script();

			texture.bind();

			script.camera(camera());

			updateMatrix();

			script.uModel.valueM4(shadowMatrix);
			script.lighting(
					0, 0, 0, am * .6f,
					0, 0, 0, aa * .6f);

			script.drawQuad(buffer);
		}

		super.draw();

	}

}
