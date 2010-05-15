import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class RunAudiveris extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int MAX_IMG_SIZE = 4*1024*1024;
	private static final String EXECPATH = "/home/apf/androidmcr/audiveris/"; // SERVER
    private static final String MIDPATH = "/home/apf/androidmcr/audiveris/midi/"; // SERVER
    private static final String IMGUPLOADPATH = "/home/apf/androidmcr/audiveris/uploads/"; // SERVER
	//private static final String EXECPATH = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/"; // APF
	//private static final String MIDPATH = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/midi/"; // APF
    //private static final String IMGUPLOADPATH = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/uploads/"; // APF
       
    public RunAudiveris() {
        super();
    }
    
    /**
     * Writes out run.script, which tells Audiveris (1) where
     * the input image is, and (2) where to put the output MIDI file.
     * Might throw IOException if file writing goes wrong.
     */
    private void writeScript(String imageName) throws IOException {
        String jpgPath = IMGUPLOADPATH + imageName;
        String midiPath = MIDPATH + imageName + ".mid";
        String writeOut = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<script sheet=\""+jpgPath+"\">\n" +
            "<step name=\"SCORE\"/>\n" +
            "<midi path=\""+midiPath+"\"/>\n" +
            "</script>\n";
        
        FileWriter fstream = new FileWriter(EXECPATH+"run.script");
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(writeOut);
        out.close();
    }

    /**
     * Executes Audiveris, assuming it's in the same folder as run.script.
     * Note that audveris-launch.sh may only work on the server because it has
     * an explicit path to a certain type of Java.
     * Throws an exception if executing the process goes wrong.
     */
    private void executeAudiveris() throws IOException {
        String line = null;
        Process p = Runtime.getRuntime().exec(EXECPATH + "audiveris-launch.sh " + EXECPATH);
    }
    
    /**
     * Loads an image from a multi-part form request.
     * Checks the last few chars to see what type the file is; should be an img.
     * @throws Exception 
     */
	private String loadImage(HttpServletRequest request) throws Exception {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            throw new FileUploadException();
        } 
        if (request.getContentLength() > MAX_IMG_SIZE) {
            throw new FileUploadException();
        }
        
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List items = upload.parseRequest(request);
        Iterator itr = items.iterator();
        while (itr.hasNext()) {
            FileItem item = (FileItem) itr.next();
            if (item.isFormField()){
                String name = item.getFieldName();
                String value = item.getString();
            } else {  
                String itemName = item.getName();
                Random generator = new Random();
                int r = Math.abs(generator.nextInt());

                String reg = "[.*]";
                String replacingtext = "";
                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(itemName);
                StringBuffer buffer = new StringBuffer();

                while (matcher.find()) {
                    matcher.appendReplacement(buffer, replacingtext);
                }
                int IndexOf = itemName.indexOf("."); 
                String domainName = itemName.substring(IndexOf);
                        
                // This should be replaced with a real MIME type check
                if (!(domainName.equalsIgnoreCase(".jpg") ||
                      domainName.equalsIgnoreCase(".png") ||
                      domainName.equalsIgnoreCase(".gif") ||
                      domainName.equalsIgnoreCase(".bmp") ||
                      domainName.equalsIgnoreCase(".pdf") )) {
                            throw new Exception();
                }
                        
                String finalimage = buffer.toString()+"_"+r+domainName;

                File savedFile = new File(IMGUPLOADPATH+finalimage);
                item.write(savedFile);
                return finalimage;            
            }
        }
        return "";
	}

	/**
	 * Program entry point.  The request should be a multi-part form uploading
	 * an image.
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    
	    // Set up the response page
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<html><head><title>Run Audiveris</title>");
        out.println("\t<style>body { font-family: 'Lucida Grande', " +
                    "'Lucida Sans Unicode';font-size: 13px; }</style>");
        out.println("</head><body>"); 
	    
	    try {
	        // Load the image
	        String fileName = loadImage(req);
	        if (fileName.equals("")) { throw new IOException(); }
	        out.println("Uploaded file is at: " + IMGUPLOADPATH + fileName + "<br />");
	        try {
	            // Write out run.script
	            writeScript(fileName);
	            try {
	                // Execute Audiveris
	                executeAudiveris();
	                out.println("Midi is at: " + MIDPATH + fileName + ".mid");
	            } catch (IOException e) {
	                out.println("Audiveris could not be run.");
	                e.printStackTrace();
	            }
	        } catch(IOException e) { // writeScript failed
	            out.println("run.script could not be written.");
	            e.printStackTrace();
	        }
	    } catch (Exception e) { // loadImage failed
	        out.println("Image could not be uploaded.");
	        e.printStackTrace();
	    }
        
        // Finish up
        out.println("</body></html>");
        out.close();
	}

	/**
	 * Get is not supported.  We need a multipart POST request.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    res.setContentType("text/html");
	    PrintWriter out = res.getWriter();
	    out.println("<html><head></head><body>");
	    out.println("You need to POST an image to this form!  GET doesn't work. <br />");
	    out.println("</body></html>");
	    out.close();
	}
	    
}
