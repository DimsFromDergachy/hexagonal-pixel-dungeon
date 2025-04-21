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

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.VertexBuffer;
import com.watabou.utils.Rect;
import com.watabou.utils.RectF;

import java.nio.FloatBuffer;

public class TileMap extends Visual {

	protected SmartTexture texture;
	protected TextureFilm tileSet;

	protected int[] data;
	protected int mapWidth;
	protected int mapHeight;
	protected int size;

	private float cellW;
	private float cellH;

	protected float[] vertices;
	protected FloatBuffer quads;
	protected VertexBuffer buffer;

	protected volatile Rect updated;
	protected boolean fullUpdate;
	protected Rect updating;
	protected int topLeftUpdating;
	protected int bottomRightUpdating;

	public TileMap( Object tx, TextureFilm tileSet ) {

		super( 0, 0, 0, 0 );

		this.texture = TextureCache.get( tx );
		this.tileSet = tileSet;

		RectF r = tileSet.get( 0 );
		cellW = tileSet.width( r );
		cellH = tileSet.height( r );

		vertices = new float[16];

		updated = new Rect();
	}

	public void map( int[] data, int cols ) {

		this.data = data;

		mapWidth = cols;
		mapHeight = data.length / cols;
		size = mapWidth * mapHeight;

		width = cellW * mapWidth;
		height = cellH * mapHeight;

		quads = Quad.createSet( size );

		updateMap();
	}
	
	public Image image(int x, int y){
		if (!needsRender(x + mapWidth*y)){
			return null;
		} else {
			Image img = new Image(texture);
			img.frame(tileSet.get(data[x + mapWidth * y]));
			return img;
		}
	}

	//forces a full update, including new buffer
	public synchronized void updateMap(){
		updated.set( 0, 0, mapWidth, mapHeight );
		fullUpdate = true;
	}

	public synchronized void updateMapCell(int cell){
		updated.union( cell % mapWidth, cell / mapWidth );
	}

	protected NoosaScript script(){
		return NoosaScriptNoLighting.get();
	}

	@Override
	public void destroy() {
		super.destroy();
		if (buffer != null)
			buffer.delete();
	}

	protected boolean needsRender(int pos){
		return data[pos] >= 0;
	}
}
