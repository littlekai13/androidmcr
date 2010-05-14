import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RunAudiveris extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String serverPath = "/home/apf/androidproj/repo/audiveris/";
       
    public RunAudiveris() {
        super();
    }
    
    private void writeScript() {
        String jpgPath = serverPath + "/example.png";
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
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    // Write the run.script file
        //writeScript();
        
        // Execute Audiveris
        //executeAudiveris();
        
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
        //out.println("File is at: " + serverPath + "/example.mid");
        out.println("HELLOOOO");
        
        // Finish up
        out.println("</body></html>");
        out.close();
	}

}
