# -*- coding: utf-8 -*-
# Requires Pillow for Python 3

import os
import sys
from PIL import Image

# --- Configuration ---

# Directory containing your original tile images
INPUT_FOLDER = './input_tiles'

# Directory where the modified images will be saved
OUTPUT_FOLDER = './output_tiles'

# Type of mirroring to apply: 'horizontal', 'vertical', or None to disable
MIRROR_TYPE = 'horizontal'

# --- Pixel Modification Functions (Customize these!) ---

def modify_pixel_logic(x, y, original_color_rgba):
    """
    Define your custom logic for changing a single pixel's color here.
    Args:
        x (int): The x-coordinate of the pixel.
        y (int): The y-coordinate of the pixel.
        original_color_rgba (tuple): The original color (R, G, B, A).
    Returns:
        tuple: The new color (R, G, B, A), or None to keep the original.
    """
    # Example: Make pixel at (5, 5) red
    # if x == 5 and y == 5:
    #     return (255, 0, 0, 255) # RGBA for red

    # Example: Make pixels in the top-left 2x2 corner blue
    if x < 2 and y < 2:
        return (0, 0, 255, 255) # RGBA for blue

    # Example: Invert colors (excluding alpha)
    # r, g, b, a = original_color_rgba
    # return (255 - r, 255 - g, 255 - b, a)

    # Return None to make no change to this pixel
    return None


# --- Core Processing Functions (Usually no need to change) ---

def process_single_image(input_path, output_path, mirror_direction):
    """Loads, processes, and saves a single image."""
    try:
        print(f" Processing: {os.path.basename(input_path)}")
        img = Image.open(input_path)

        # Convert to RGBA to handle transparency consistently and allow pixel edits
        img = img.convert('RGBA')
        pixels = img.load() # Load pixel data for efficient access

        # 1. Apply mirroring (if enabled)
        if mirror_direction == 'horizontal':
            print("  Mirroring horizontally")
            img = img.transpose(Image.FLIP_LEFT_RIGHT)
        elif mirror_direction == 'vertical':
            print("  Mirroring vertically")
            img = img.transpose(Image.FLIP_TOP_BOTTOM)
        elif mirror_direction is not None:
            print(f"  Warning: Invalid mirror direction specified: {mirror_direction}")

        # Reload pixels if mirroring was applied, as the image object might have changed
        if mirror_direction in ['horizontal', 'vertical']:
             pixels = img.load()

        # 2. Apply custom pixel modifications
        print("  Applying custom pixel logic...")
        modified_pixels_count = 0
        for y in range(img.height):
            for x in range(img.width):
                original_color = pixels[x, y]
                new_color = modify_pixel_logic(x, y, original_color)
                if new_color is not None:
                    try:
                         pixels[x, y] = new_color
                         modified_pixels_count += 1
                    except Exception as e:
                         print(f"   Error setting pixel at ({x}, {y}): {e}")

        if modified_pixels_count > 0:
             print(f"  Modified {modified_pixels_count} pixels.")
        else:
             print("  No pixels modified by custom logic.")


        # 3. Save the processed image
        img.save(output_path)
        print(f"  Saved to: {output_path}")

    except IOError as e:
        print(f" Error processing file {os.path.basename(input_path)}: {e}")
    except Exception as e:
         print(f" An unexpected error occurred processing {os.path.basename(input_path)}: {e}")


def main():
    """Main function to find images and start processing."""
    if not os.path.isdir(INPUT_FOLDER):
        print(f"Error: Input directory '{INPUT_FOLDER}' not found. Please create it and add your images.")
        sys.exit(1)

    if not os.path.exists(OUTPUT_FOLDER):
        print(f"Creating output directory: {OUTPUT_FOLDER}")
        os.makedirs(OUTPUT_FOLDER)

    image_extensions = ('.png', '.jpg', '.jpeg', '.bmp', '.gif', '.tiff')
    print("Starting image processing...")
    print(f"Input folder: {INPUT_FOLDER}")
    print(f"Output folder: {OUTPUT_FOLDER}")
    print(f"Mirror type: {MIRROR_TYPE if MIRROR_TYPE else 'None'}")

    for filename in os.listdir(INPUT_FOLDER):
        if filename.lower().endswith(image_extensions):
            input_path = os.path.join(INPUT_FOLDER, filename)
            output_path = os.path.join(OUTPUT_FOLDER, filename) # Save with the same name
            process_single_image(input_path, output_path, MIRROR_TYPE)

    print("Processing complete.")

# --- Run the script ---
if __name__ == "__main__":
    main() 