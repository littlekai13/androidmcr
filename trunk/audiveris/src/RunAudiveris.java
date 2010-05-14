import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
        String jpgPath = "/Users/apf/Documents/projects/music/repo/trunk/omr/example/twinkle.jpg";
        String midiPath = "/Users/apf/Documents/projects/music/repo/trunk/audiveris/twinkle.mid";
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

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    
	    // Run Audiveris
        writeScript();
        String[] my_args = {"-batch", "-script", "/Users/apf/Documents/projects/music/repo/trunk/audiveris/run.script"};
        omr.Main.main(my_args);     
	    
	    // Set up the page
	    res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<html><head>");
        out.println("<title>TestServlet</title>");
        out.println("\t<style>body { font-family: 'Lucida Grande', " +
                    "'Lucida Sans Unicode';font-size: 13px; }</style>");
        out.println("</head>");
        out.println("<body>");

        // Finish up
        out.println("Ran Audiveris");
        out.println("</body></html>");
        out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
