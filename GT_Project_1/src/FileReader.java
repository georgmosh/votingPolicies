import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FileReader {
    private List<String> Result_data_filenames;
    private HashMap<String,BestResponseDynamics> playerOptions;
    private String inputDataRepository, outputDataRepository;

    /**
     * Overloaded Constructor.
     * @param inputRepository The name of the directory to find initial results' contained filepaths.
     */
    public FileReader(String inputRepository, String outputDataRepository) {
        this.inputDataRepository = inputRepository;
        this.outputDataRepository = outputDataRepository;
        playerOptions = new HashMap<String,BestResponseDynamics>();
    }

    /**
     *  Method to Execute!
     *  A method performing all detected; written down election proposals contained in a given directory.
     *  Best Response Dynamics is used combined with a specified voting policy (Plurality or Borda).
     *  In case none of the above is specified; an IllegalArgumentException is thrown!
     */
    public void PerformElectionsSeries() {
        GenerateElectionsData();
        Set<String> electionSources = playerOptions.keySet();
        for(String source: electionSources) {
            System.out.println("\nFile: " + source);
            playerOptions.get(source).performElections();
        }
    }

    /**
     * A method detecting and writing down all election proposals contained in a given directory.
     */
    public void GenerateElectionsData() {
        Result_data_filenames = listAllFiles(".\\" + inputDataRepository);

        /*
         * Searching for the above listed files.
         */
        int numOfTargetFiles = Result_data_filenames.size();
        for (int file = 0; file < numOfTargetFiles; file++) {
            Vec2<Vec2<String, VotingPolicies>,List<List<String>>> readingOutput =
                    ReadTargetFile(inputDataRepository + "/" + Result_data_filenames.get(file));
            playerOptions.put(readingOutput.getTValue().getTValue(),
                    new BestResponseDynamics(readingOutput.getYValue(), readingOutput.getTValue().getYValue(), Result_data_filenames.get(file), outputDataRepository, (readingOutput.getYValue().size()*100)));
        }
    }

    /**
     * A method creating a dynamic container of all filepaths contained in a given directory.
     * @param path The directory to find all contained filepaths.
     */
    public static List<String> listAllFiles(String path){
        List<String> filenames = new ArrayList<String>();

        /*
         * Listing given repository's contained files.
         */
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        System.out.println("Folder: " + folder);
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) filenames.add(listOfFiles[i].getName());
        }

        return filenames;
    }

    /**
     * A method reading a specific results file and generating its memory equivalent.
     * @param filePath The relative (to the project directory) path of the results file.
     * @throws IllegalArgumentException for invalid specified voting policies/
     */
    public Vec2<Vec2<String, VotingPolicies>,List<List<String>>> ReadTargetFile(String filePath) {
        BufferedReader br = null;
        String sCurrentLine, votingPolicy;
        String filename = filePath.split("/")[1];
        Vec2<String, VotingPolicies> electionParameters = new Vec2<String, VotingPolicies>();
        electionParameters.setTValue(filename);
        try {
            br = new BufferedReader(new java.io.FileReader(filePath));
            List<List<String>> allTheVotersOptions = new ArrayList<List<String>>();
            votingPolicy = br.readLine().trim().replaceAll(" +", " ");
            if(votingPolicy.toLowerCase().equals("plurality"))
                electionParameters.setYValue(new Plurality());
            else if(votingPolicy.toLowerCase().equals("borda"))
                electionParameters.setYValue(new Borda());
            else throw new IllegalArgumentException();
            while ((sCurrentLine = br.readLine()) != null) {
                String[] mCurrentLine = sCurrentLine.split(" ");
                List<String> currentVoterOptions = new ArrayList<String>();
                for(int vote = 0; vote < mCurrentLine.length; vote++) {
                    currentVoterOptions.add(mCurrentLine[vote]);
                }
                allTheVotersOptions.add(currentVoterOptions);
            }
            return new Vec2<Vec2<String, VotingPolicies>, List<List<String>>>(electionParameters, allTheVotersOptions);
        } catch (IOException exc) {
            exc.printStackTrace();
            return null;
        } catch (IllegalArgumentException exc2) {
            System.err.println("Please specify a valid input file.");
            exc2.printStackTrace();
            return null;
        }
    }
}
