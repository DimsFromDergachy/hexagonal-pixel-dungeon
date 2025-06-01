
https://www.redblobgames.com/grids/hexagons/

Build:
> ./gradlew build -p desktop

Run:
> ./gradlew desktop:debug

Adjust tiles:
> python3 ./image_tools/process_tiles.py

Build for android:
> ./gradlew assembleDebug

### TODO:
- [ ] Hex: Repaint levels on the hexagonal way
- [ ] Hex: Change name of game, e-mail, version
- [ ] Bug: Fix search pattern
- [ ] Bug: Cursed wands lead to crash (in the original game too)
- [ ] Bug: Fix wall-assist
- [ ] Hex: Update assets for hexagonal pattern (walls, chasm, stairs, etc)