/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

public class IconLoader {

    private final List<String> searchPaths;
    private final int iconSize;

    public IconLoader() {
        this.searchPaths = Arrays.asList("", "/", "images/", "/images/", "res/", "/res/");
        this.iconSize = 32;
    }

    public IconLoader(List<String> searchPaths, int iconSize) {
        this.searchPaths = (searchPaths != null && !searchPaths.isEmpty())
            ? Collections.unmodifiableList(searchPaths)
            : Arrays.asList("", "/");
        this.iconSize = (iconSize > 0) ? iconSize : 32;
    }

    public Image loadIcon(String name) {
        String path = resolvePath(name);
        if (path == null) return null;

        Image img = loadFromClasspath(path);
        if (img != null) return img;

        return loadFromFileSystem(path);
    }

    private String resolvePath(String name) {
        for (String prefix : searchPaths) {
            String candidate = prefix + name;

            // 1. Load from JAR
            if (getClass().getResource(candidate) != null) {
                return candidate;
            }

            // 2. Load from Filesystem
            if (new File(candidate).exists()) {
                return candidate;
            }
        }
        return null;
    }

    private Image loadFromClasspath(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                BufferedImage buf = ImageIO.read(is);
                return buf.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
            }
        } catch (Exception e) { }
        return null;
    }

    private Image loadFromFileSystem(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Image img = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
            return img.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        }
        return null;
    }
}
