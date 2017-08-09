package np.edu.kathford.www.image;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FilenameUtils.getExtension;


/**
 * This class provides methods for manipulating individual pixels of
 * an image. The original image can be read from a {@code .jpg}, {@code .gif},
 * or {@code .png} file or the user can create a blank image of a given size.
 * This class includes methods for displaying the image in a window on
 * the screen or saving it to a file.
 * <p>
 * Pixel (<em>col</em>, <em>row</em>) is column <em>col</em> and row <em>row</em>.
 * By default, the origin (0, 0) is the pixel in the top-left corner,
 * which is a common convention in image processing.
 * The method {@code setOriginLowerLeft()} change the origin to the lower left.
 * <p>
 * For additional documentation, see
 * <a href="http://introcs.cs.princeton.edu/31datatype">Section 3.1</a> of
 * <i>Computer Science: An Interdisciplinary Approach</i>
 * by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class Picture {
    private BufferedImage image;               // the rasterized image
    private final int width, height;           // width and height

    /**
     * Initializes a picture by reading from a file or URL.
     *
     * @param filename the name of the file (.png, .gif, or .jpg) or URL.
     * @throws IllegalArgumentException if cannot read image
     * @throws IllegalArgumentException if {@code filename} is {@code null}
     */
    public Picture(String filename) throws IOException {
        image = ImageIO.read(new File(filename));

        if (image == null) {
            throw new IllegalArgumentException("could not read image file: " + filename);
        }

        width = image.getWidth(null);
        height = image.getHeight(null);
    }

    /**
     * Returns the height of the picture.
     *
     * @return the height of the picture (in pixels)
     */
    public int height() {
        return height;
    }

    /**
     * Returns the width of the picture.
     *
     * @return the width of the picture (in pixels)
     */
    public int width() {
        return width;
    }

    /**
     * Returns the color of pixel ({@code col}, {@code row}).
     *
     * @param col the column index
     * @param row the row index
     * @return the color of pixel ({@code col}, {@code row})
     */
    public Color get(int col, int row) {
        return new Color(image.getRGB(col, row));
    }

    /**
     * Sets the color of pixel ({@code col}, {@code row}) to given color.
     *
     * @param col   the column index
     * @param row   the row index
     * @param color the color
     */
    public void set(int col, int row, Color color) {
        image.setRGB(col, row, color.getRGB());
    }

    /**
     *
     * @param filename Filename of the the output image
     * @throws IOException if
     */
    public void save(String filename) throws IOException {
        System.out.println(filename);
        ImageIO.write(image, getExtension(filename), new File(filename));
    }
}
