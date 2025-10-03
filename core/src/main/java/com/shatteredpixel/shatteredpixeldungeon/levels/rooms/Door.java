package com.shatteredpixel.shatteredpixeldungeon.levels.rooms;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;

public class Door extends Point implements Bundlable {
	
	public enum Type {
		EMPTY, TUNNEL, WATER, REGULAR, UNLOCKED, HIDDEN, BARRICADE, LOCKED, CRYSTAL, WALL
	}
	public Type type = Type.EMPTY;
	
	public Door(){
	}
	
	public Door( Point p ){
		super(p);
	}
	
	public Door( int x, int y ) {
		super( x, y );
	}

	private boolean typeLocked = false;

	public void lockTypeChanges( boolean lock ){
		typeLocked = lock;
	}

	public void set( Type type ) {
		if (!typeLocked && type.compareTo( this.type ) > 0) {
			this.type = type;
		}
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put("x", x);
		bundle.put("y", y);
		bundle.put("type", type);
		bundle.put("type_locked", typeLocked);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		x = bundle.getInt("x");
		y = bundle.getInt("y");
		type = bundle.getEnum("type", Type.class);
		typeLocked = bundle.getBoolean("type_locked");
	}
}