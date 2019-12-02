import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * DemoFileWriter.java
 * A class creating demo files for BestResponseDynamics, in purpose to evaluate and compare
 * their Borda and Plurality implementations, using one of Java's pseudorandom  number producers.
 * Check out the attached project report for further details in terms of the idea.
 * @authors Georgios M. Moschovis (p3150113@aueb.gr)
 */
public class DemoFileWriter {
    private String outputDataRepository;
    private List<Integer> votersAmt, candidatesAmt;

    /**
     * Overloaded Constructor.
     * @param out The repository to write sample demos.
     * @param n The amount of voters participating in the demo election.
     * @param m The amount of the demo election candidates.
     */
    public DemoFileWriter(String out,  List<Integer> n,  List<Integer> m) {
        this.outputDataRepository = out;
        this.votersAmt = n; this.candidatesAmt = m;
    }

    /**
     * Basic implementation of the demos creation procedure.
     * When executed 20 different test files per combination of n, m are created in the given directory.
     * Then above test files are filled with pseudorandom using concurrent ThreadLocalRandom.
     */
    public void generateDemoData() {
        for (int values = 0; values < votersAmt.size(); values++) {
            int n = votersAmt.get(values),
                    m = candidatesAmt.get(values);
            for (int game = 1; game <= 20; game++) {
                try {
                    String gameContent = "";
                    for (int i = 0; i < n; i++) {
                        // Mapping values so that voters can vote each candidate strictly once.
                        List<String> candidatesPerVoter = new ArrayList<String>();
                        for (int j = 0; j < m; j++) {
                            int x = ThreadLocalRandom.current().nextInt(1, m);
                            while (candidatesPerVoter.contains("C"+x)) x = ThreadLocalRandom.current().nextInt(1, m + 1);
                            candidatesPerVoter.add("C" + x);
                        }
                        for (String candidate : candidatesPerVoter) {
                            gameContent += (candidate + " ");
                        }
                        if (i < n - 1) gameContent += "\n";
                    }

                    String writingPolicy = "plurality";
                    PrintWriter pw1 = new PrintWriter(outputDataRepository + "/game" + game + "_" + n + "_" + m + "_" + writingPolicy + "_results.txt");
                    pw1.write(writingPolicy + "\n" + gameContent); pw1.flush(); pw1.close();

                    writingPolicy = "borda";
                    PrintWriter pw2 = new PrintWriter(outputDataRepository + "/game" + game + "_" + n + "_" + m + "_" + writingPolicy + "_results.txt");
                    pw2.write(writingPolicy + "\n" + gameContent); pw2.flush(); pw2.close();
                } catch (FileNotFoundException exc) {
                    System.err.println("Error writing file to disk; please check relevant permissions.");
                    exc.printStackTrace();
                }
            }
        }
    }
}
