package com.shatteredpixel.shatteredpixeldungeon.sprites;

import java.nio.Buffer;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.VertexBuffer;
import com.watabou.noosa.Camera;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.NoosaScript;
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
		final int csize = DungeonTilemap.SIZE;

		int posX = cell % Dungeon.level.width();
		int posY = cell / Dungeon.level.width();
		
		return new PointF(
			PixelScene.align(Camera.main, (posX + (posY % 2 == 0 ? 0.5f : 1.0f)) * csize - width() * 0.5f),
			PixelScene.align(Camera.main, (posY + 1.0f) * csize - height() - csize * perspectiveRaise)
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
