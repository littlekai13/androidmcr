import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
	//private static final String serverPath = "/home/apf/androidmcr/audiveris/"; // SERVER
	//private static final String uploadPath = "/home/apf/androidmcr/audiveris/uploads/"; // SERVER
	private static final String serverPath = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/";
    private static final String uploadPath = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/uploads/";
    private String imageName;
       
    public RunAudiveris() {
        super();
        imageName = serverPath + "example.png";
    }
    
    private void writeScript() {
        String jpgPath = imageName;
        String midiPath = serverPath + "example.mid";
        String writeOut = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<script sheet=\""+jpgPath+"\">\n" +
            "<step name=\"SCORE\"/>\n" +
            "<midi path=\""+midiPath+"\"/>\n" +
            "</script>\n";
        try {
            FileWriter fstream = new FileWriter(serverPath+"run.script");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(writeOut);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeAudiveris() {
        try {
            String line = null;
            Process p = Runtime.getRuntime().exec(serverPath + "audiveris-launch.sh " + serverPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    // GET NOT SUPPORTED FOR MULTIPART FORMS
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<html><head>");
        out.println("<title>TestServlet</title>");
        out.println("\t<style>body { font-family: 'Lucida Grande', " +
                    "'Lucida Sans Unicode';font-size: 13px; }</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("You need to POST an image to this form!  GET doesn't work. <br />");
        out.println("</body></html>");
        out.close();
	}
	
	private void loadImage(HttpServletRequest request) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            System.err.println("File Not Uploaded");
        } else {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List items = null;

            try {
                items = upload.parseRequest(request);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
            Iterator itr = items.iterator();
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                if (item.isFormField()){
                    String name = item.getFieldName();
                    String value = item.getString();
                } else {
                    try {
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
                        String finalimage = buffer.toString()+"_"+r+domainName;

                        File savedFile = new File(uploadPath+finalimage);
                        item.write(savedFile);
                        imageName = uploadPath+finalimage;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    
	    // Handle the image upload
	    loadImage(req);
	    
	    // Write the run.script file
        writeScript();
        
        // Execute Audiveris
        executeAudiveris();
        
        // Set up the page
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<html><head>");
        out.println("<title>TestServlet</title>");
        out.println("\t<style>body { font-family: 'Lucida Grande', " +
                    "'Lucida Sans Unicode';font-size: 13px; }</style>");
        out.println("</head>");
        out.println("<body>"); 
        
        // File location
        out.println("File is at: " + imageName);
        //out.println("HELLOOOO");
        
        // Finish up
        out.println("</body></html>");
        out.close();
	}

}
