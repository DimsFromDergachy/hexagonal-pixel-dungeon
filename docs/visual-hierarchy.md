# Visual Class Hierarchy

This diagram shows the class hierarchy for visual elements in Shattered Pixel Dungeon.

## Key Components

1. `Visual` - Base class for all visual elements
   - Handles basic positioning, scaling, and rendering
   - Provides matrix transformations for rendering

2. `Image` - Base class for textured elements
   - Manages texture coordinates and rendering
   - Supports flipping and frame management

3. `MovieClip` - Handles animated elements
   - Manages animation states and playback
   - Controls frame timing and sequences

4. `CharSprite` - Base class for game characters
   - Implements character-specific animations
   - Handles movement and state effects

5. Supporting Classes
   - `SmartTexture`: Advanced texture management
   - `TextureFilm`: Sprite sheet handling
   - `TileMap`: Map rendering
   - `RenderedText`: Text display
   - `SkinnedBlock`: Repeating textures
   - `NinePatch`: UI elements with borders

## Usage

This diagram can be viewed with any Mermaid-compatible markdown viewer. The source file is in Mermaid format, which is a simple and widely supported diagramming language.

To view this diagram:
1. Use a Mermaid-compatible markdown viewer
2. Or visit [Mermaid Live Editor](https://mermaid.live)
3. Or use GitHub, which natively supports Mermaid diagrams

## Notes

- Inheritance is shown with arrows pointing from child to parent
- Only key methods and properties are shown for clarity
- Some utility classes and interfaces are omitted 