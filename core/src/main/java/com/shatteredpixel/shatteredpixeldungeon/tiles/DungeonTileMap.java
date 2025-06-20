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

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.noosa.HexTileMap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.noosa.Image;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.GameMath;
import com.watabou.utils.HexMath;
import com.watabou.utils.PathFinder.Neighbor;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

public abstract class DungeonTileMap extends HexTileMap {

	public static final int SIZE = 16;

	public static final int WIDTH  = HexMath.HEX_MODE ? 18 : 16;
	public static final int HEIGHT = 16;

	protected int[] map;

	public DungeonTileMap(String tex) {
		super(tex, SIZE, WIDTH, HEIGHT );
	}

	@Override
	// we need to retain two arrays, map is the dungeon tileMap which we can reference.
	// Data is our own internal image representation of the tiles, which may differ.
	public void map(int[] data, int cols) {
		map = data;
		super.map(new int[data.length], cols);
	}

	@Override
	public synchronized void updateMap() {
		for (int i = 0; i < data.length; i++)
			data[i] = getTileVisual(i ,map[i], false);
		super.updateMap();
	}

	@Override
	public synchronized void updateMapCell(int cell) {
		//update in a 3x3 grid to account for neighbors which might also be affected
		if (Dungeon.level.insideMap(cell)) {
			for (int i : Dungeon.level.neighbors( Neighbor.NEIGHBORS_7, cell )) {
				data[cell + i] = getTileVisual(cell + i, map[cell + i], false);
			}
			super.updateMapCell(cell - mapWidth - 1);
			super.updateMapCell(cell + mapWidth + 1);

		//unless we're at the level's edge, then just do the one tile.
		} else {
			data[cell] = getTileVisual(cell, map[cell], false);
			super.updateMapCell(cell);
		}
	}

	protected abstract int getTileVisual(int pos, int tile, boolean flat);

	// public int screenToTile(int x, int y ){
	// 	return screenToTile(x, y, false);
	// }

	// wall assist is used to make raised perspective tapping a bit easier.
	// If the pressed tile is a wall tile, the tap can be 'bumped' down into a none-wall tile.
	// currently this happens if the bottom 1/4 of the wall tile is pressed.
	public int screenToTile(PointF point, boolean wallAssist ) {

		Point p = HexMath.PixelToHex(point);

		// snap to the edges of the tileMap
		p.x = GameMath.gate(0, p.x, Dungeon.level.width() - 1);
		p.y = GameMath.gate(0, p.y, Dungeon.level.height() - 1);

		int cell = p.x + p.y * Dungeon.level.width();


		// wall assist is used to make raised perspective tapping a bit easier.
		// If the pressed tile is a wall tile, the tap can be 'bumped' down into a none-wall tile.
		// currently this happens if the bottom 1/4 of the wall tile is pressed.
		if (wallAssist
				&& map != null
				&& isWallAssistable(cell)){

			// TODO: Fix wall assist
			// if (cell + mapWidth < size
			// 		// && p.y % 1 >= 0.75f
			// 		&& !DungeonTileSheet.wallStitcheable(map[cell + mapWidth])){
			// 	cell += mapWidth;
			// }

		}

		return cell;

	}

	private boolean isWallAssistable(int cell){
		if (map == null || cell >= size){
			return false;
		}

		if (DungeonTileSheet.wallStitcheable(map[cell])){
			return true;
		}

		//caves region deco is very wall-like, so it counts
		if (Dungeon.depth >= 10 && Dungeon.depth <= 15
				&& (map[cell] == Terrain.REGION_DECO || map[cell] == Terrain.REGION_DECO_ALT)) {
			return true;
		}

		return false;
	}
	
	@Override
	public boolean overlapsPoint( float x, float y ) {
		return true;
	}
	
	public void discover( int pos, int oldValue ) {

		int visual = getTileVisual( pos, oldValue, false);
		if (visual < 0) return;
		
		final Image tile = new Image( texture );
		tile.frame( tileSet.get( getTileVisual( pos, oldValue, false)));
		tile.point( tileToWorld( pos ) );

		parent.add( tile );
		
		parent.add( new AlphaTweener( tile, 0, 0.6f ) {
			protected void onComplete() {
				tile.killAndErase();
				killAndErase();
			}
		} );
	}

	// inline
	private static PointF tileToWorld( int pos, float adjustX, float adjustY ) {

		int x = pos % Dungeon.level.width();
		int y = pos / Dungeon.level.width();

		return HexMath.HexToPixel(x, y).offset(adjustX, adjustY);

	}
	
	public static PointF tileToWorld( int pos ) {
		return tileToWorld(pos, 0, 0);
	}

	public static PointF tileCenterToWorld( int pos ) {
		return tileToWorld(pos, 0.5f * WIDTH, 0.5f * HEIGHT);
	}

	public static PointF raisedTileCenterToWorld( int pos ) {
		return tileToWorld(pos, 0.5f * WIDTH, 0.1f * HEIGHT);
	}
	
	@Override
	public boolean overlapsScreenPoint( int x, int y ) {
		return true;
	}

}
