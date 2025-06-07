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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CircleWallRoom;
import com.watabou.utils.Random;
import com.watabou.utils.PathFinder.Neighbor;

public class CircleWallEntranceRoom extends CircleWallRoom {

	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 11);
	}

	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 11);
	}

	@Override
	public float[] sizeCatProbs() {
		return new float[]{0, 1, 0};
	}

	@Override
	public boolean isEntrance() {
		return true;
	}

	@Override
	public void paint(Level level) {
		super.paint(level);

		int cell = level.pointToCell( center() );

		int[] neighbor_6_x1 = level.neighbors( Neighbor.NEIGHBORS_6, cell );
		int[] neighbor_6_x2 = level.neighbors( Neighbor.NEIGHBORS_6_x2, cell );

		for (int i = 0; i < neighbor_6_x1.length; i++) {
			if (level.map[cell + neighbor_6_x2[i]] == Terrain.WALL) {
				Painter.set(level, cell + neighbor_6_x1[i], Terrain.EMPTY);
			}
		}

		Painter.set(level, cell, Terrain.ENTRANCE);
		level.transitions.add(new LevelTransition(level, cell, LevelTransition.Type.REGULAR_ENTRANCE));

		int d = Random.Int( 6 );

		cell = cell + neighbor_6_x2[d];
		while (level.map[cell] == Terrain.WALL){
			Painter.set( level, cell, Terrain.EMPTY );
			cell = cell + level.neighbors( Neighbor.NEIGHBORS_6, cell )[d];
		}

	}
}
