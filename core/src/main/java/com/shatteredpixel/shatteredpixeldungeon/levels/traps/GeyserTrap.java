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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistic;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileMap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.PathFinder.Neighbor;

import java.util.ArrayList;

public class GeyserTrap extends Trap {

	{
		color = TEAL;
		shape = DIAMOND;
	}

	public int centerKnockBackDirection = -1;
	public Object source = this;

	@Override
	public void activate() {
		Splash.at( DungeonTileMap.tileCenterToWorld( pos ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 100, 0.01f);
		Sample.INSTANCE.play(Assets.Sounds.GAS, 1f, 0.75f);

		Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
		PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), 2 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] == 2 && Random.Int(3) > 0){
				Dungeon.level.setCellToWater(true, i);
				if (fire != null){
					fire.clear(i);
				}
			} else if (PathFinder.distance[i] < 2){
				Dungeon.level.setCellToWater(true, i);
				if (fire != null){
					fire.clear(i);
				}
			}
		}

		for (int i : Dungeon.level.neighbors( Neighbor.NEIGHBORS_6, pos )){
			Char ch = Actor.findChar(pos + i);
			if (ch != null){

				if (source == this && ch instanceof Mob){
					Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
				}

				//does the equivalent of a bomb's damage against fiery enemies.
				if (Char.hasProp(ch, Char.Property.FIERY)){
					int dmg = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth()*2);
					dmg *= 0.67f;
					if (!ch.isImmune(GeyserTrap.class)){
						ch.damage(dmg, this);
					}
				}

				if (ch.isAlive()) {
					if (ch.buff(Burning.class) != null){
						ch.buff(Burning.class).detach();
					}

					//trace a ballistica to our target (which will also extend past them)
					Ballistic trajectory = new Ballistic(pos, ch.pos, Ballistic.STOP_TARGET);
					//trim it to just be the part that goes past them
					trajectory = new Ballistic(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistic.PROJECTILE);
					//knock them back along that ballistica
					WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, source);
				}
			}
		}

		Char ch = Actor.findChar(pos);
		if (ch != null){
			if (source == this && ch instanceof Mob){
				Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
			}
			int targetPos = -1;
			if (centerKnockBackDirection != -1){
				targetPos = centerKnockBackDirection;
			} else if (ch == Dungeon.hero){
				//if it is the hero, random direction that isn't into a hazard
				ArrayList<Integer> candidates = new ArrayList<>();

				// TODO: A little bit messy, probably there is a more elegant way to do this
				int[] neighbors1 = Dungeon.level.neighbors( Neighbor.NEIGHBORS_6, pos );
				int[] neighbors2 = Dungeon.level.neighbors( Neighbor.NEIGHBORS_6_x2, pos);
				for (int i = 0; i < neighbors1.length; i++){
					//add as a candidate if both cells on the trajectory are safe
					if (!Dungeon.level.avoid[pos + neighbors1[i]] && !Dungeon.level.avoid[pos + neighbors2[i]]){
						candidates.add(pos + i);
					}
				}
				if (!candidates.isEmpty()){
					targetPos = Random.element(candidates);
				}
			} else {
				//random direction if it isn't the hero
				targetPos = pos + Dungeon.level.neighbors( Neighbor.NEIGHBORS_6, pos )[Random.Int(6)];
			}

			//does the equivalent of a bomb's damage against fiery enemies.
			if (Char.hasProp(ch, Char.Property.FIERY)){
				int dmg = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth()*2);
				if (!ch.isImmune(GeyserTrap.class)){
					ch.damage(dmg, this);
				}
			}

			if (ch.isAlive() && targetPos != -1){
				if (ch.buff(Burning.class) != null){
					ch.buff(Burning.class).detach();
				}
				//trace a ballistic in the direction of our target
				Ballistic trajectory = new Ballistic(pos, targetPos, Ballistic.MAGIC_BOLT);
				//knock them back along that ballistic
				WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, source);
			}
		}
	}
}
