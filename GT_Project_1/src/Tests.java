import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests.java
 * @author Georgios M. Moschovis (p3150113@aueb.gr)
 */
public class Tests {
    public static void main(String[] args) {
        (new Tests()).generateDemosTest();
        //(new Tests()).testFromFile();
    }

    public void generateDemosTest() {
        List<Integer> n = new ArrayList<Integer>(); n.add(5); //n.add(5); n.add(10);
        List<Integer> m = new ArrayList<Integer>(); /*m.add(5);*/ m.add(10);// m.add(5);
        (new DemoFileWriter("demos2", n, m)).generateDemoData();
        (new FileReader("demos2", "results2")).PerformElectionsSeries();
    }

    public void testFromFile() {
        (new FileReader("tests", "results")).PerformElectionsSeries();
    }
}
