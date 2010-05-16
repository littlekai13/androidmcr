import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditImages {

    public static void main(String[] args) throws IOException {
        int threshold = 20;
        String imagePath = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/testandroid.jpg";
        String outputPath = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/testout.jpg";
        FileInputStream input = new FileInputStream(imagePath);
        FileOutputStream output = new FileOutputStream(outputPath);
        int c = 0;
        while ((c = input.read()) != -1) {
            if (c > threshold) {
                output.write(255);
            } else {
                output.write(c);
            }
        }
        input.close();
        output.close();
        //FileOutputStream fos = new FileOutputStream(outputPath);
        //fos.write(baf.toByteArray());
        //fos.close();
    }
    
}
