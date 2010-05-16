import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EditImage {

    public static void main(String[] args) throws IOException {
        int threshold = 70;
        String imagePath = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/testandroid.jpg";
        String grayPath = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/testgray.jpg";
        String transPath = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/testbw.jpg";
        
        // Convert to grayscale
        BufferedImage imageColor = ImageIO.read(new File(imagePath));
        BufferedImage imageGray = new BufferedImage(imageColor.getWidth(), 
                imageColor.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = imageGray.getGraphics();
        g.drawImage(imageColor, 0, 0, null);
        g.dispose();
        ImageIO.write(imageGray, "jpg", new File(grayPath));
        System.out.println("Done with grayscale transform");
        
        // Convert to b&w
        Raster grayRast = imageGray.getRaster();
        WritableRaster bwRast = Raster.createWritableRaster(imageGray.getSampleModel(), null);
        int grayLevel = 0;
        int[] white = {255,255,255};
        int[] black = {0,0,0};
        for (int i = 0; i < grayRast.getWidth(); i++) {
            for (int j = 0; j < grayRast.getHeight(); j++) {
                grayLevel = grayRast.getSample(i,j,0);
                if (grayLevel > threshold) {
                    bwRast.setPixel(i, j, white);
                } else {
                    bwRast.setPixel(i, j, black);
                }
            }
        }
        imageGray.setData(bwRast);
        ImageIO.write(imageGray, "jpg", new File(transPath));
        System.out.println("Done with b&w transform");
    }
    
}
