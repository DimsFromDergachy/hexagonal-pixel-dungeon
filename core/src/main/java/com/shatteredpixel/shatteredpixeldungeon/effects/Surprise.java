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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;

public class Surprise extends Image {

	private static final float TIME_TO_FADE = 1f;

	private float time;

	public Surprise() {
		super(Effects.get(Effects.Type.EXCLAMATION));
		origin.set(width / 2, height / 2);
	}

	public void reset(Visual v) {

		revive();
		point( v.point().offset( v.width / 2 - width / 2, - v.height / 2 - height / 2) );
		time = TIME_TO_FADE;

	}

	@Override
	public void update() {
		super.update();

		if ((time -= Game.elapsed) <= 0) {
			kill();
		} else {
			float p = time / TIME_TO_FADE;
			alpha((float) Math.sqrt(p));
			scale.y = 1f + p;
			scale.x = 1f + p/4f;
		}
	}

	public static void hit(Char ch) {
		hit(ch, 0);
	}

	public static void hit(Char ch, float angle) {
		if (ch.sprite != null && ch.sprite.parent != null) {
			Surprise s = (Surprise) ch.sprite.parent.recycle(Surprise.class);
			ch.sprite.parent.bringToFront(s);
			s.reset(ch.sprite);
			s.angle = angle;
		}
	}

}
