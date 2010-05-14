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
       
    public RunAudiveris() {
        super();
    }
    
    private void writeScript() {
        String jpgPath = "/Users/apf/Documents/projects/music/repo/trunk/omr/example/example.png";
        String midiPath = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/example.mid";
        String writeOut = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<script sheet=\""+jpgPath+"\">\n" +
            "<step name=\"SCORE\"/>\n" +
            "<midi path=\""+midiPath+"\"/>\n" +
            "</script>\n";
        try {
            FileWriter fstream = new FileWriter("/Users/apf/Documents/projects/music/repo/trunk/audiveris/run.script");
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
            Process p = Runtime.getRuntime().exec("/Users/apf/Documents/projects/music/repo/trunk/audiveris/audiveris-launch.sh");
            BufferedReader stdInput = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((line = stdInput.readLine()) != null) {
                System.err.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    
	    // Write the run.script file
        writeScript();
        
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

        try {
            String line = null;
            Process p = Runtime.getRuntime().exec("/Users/apf/Documents/projects/music/repo/trunk/audiveris/audiveris-launch.sh /Users/apf/Documents/projects/music/repo/trunk/audiveris/");
            BufferedReader stdInput = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((line = stdInput.readLine()) != null) {
                out.print(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        // Finish up
        out.println("doo doo doo");
        out.println("</body></html>");
        out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
