
public final class Audiveris
{

    private Audiveris ()
    {
    }
    
    public static void main (final String[] args)
    {
    
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
    
    
    	//String[] my_args = {"-batch", "-step", "score", "-sheet", "../omr/example/twinkle.jpg"};
    	//String[] my_args = {"-sheet", "../omr/example/twinkle.jpg"};
    	String[] my_args = {"-batch", "-script", "/Users/apf/Documents/projects/music/repo/trunk/audiveris/run.script"};
        omr.Main.main(my_args);
    }
}

