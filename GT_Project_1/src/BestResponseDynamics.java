import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Collections.*;
import java.util.ArrayList;
import java.util.List;

public class BestResponseDynamics {
    private String electionSampleName, outputDataRepository;
    private List<List<Vec2<String,Integer>>> roundCandidateRankings;
    private List<List<String>> playerOptions;
    private VotingPolicies rankingOptions;
    private int Tmax;

    /**
     * Overloaded Constructor.
     */
    public BestResponseDynamics(List<List<String>> playerOptions, VotingPolicies policy, String name, String out, int Tmax) {
        roundCandidateRankings = new ArrayList<List<Vec2<String,Integer>>>();
        this.playerOptions = playerOptions;
        this.electionSampleName = name;
        this.outputDataRepository = out;
        this.rankingOptions = policy;
        this.Tmax = Tmax;
    }

    /**
     * Basic implementation of the elections performance in rounds.
     */
    public void performElections() {
        boolean playNextRound = false;
        String wasSwappedPreviously = null;
        List<Vec2<String,Integer>> descOrderSortedCandidateRankings = null;
        int round = 0;
        do {
            /*
             * Sorting the actual ranking results.
             */
            descOrderSortedCandidateRankings = sortActualRankings(wasSwappedPreviously);
            roundCandidateRankings.add(descOrderSortedCandidateRankings);
            String roundWinningCandidate = roundCandidateRankings.get(round).get(0).getTValue();

            /*
             * Iterating ranking results; to seek for Best Response Dynamics.
             */
            boolean getNextPlayer = true;
            for(int pPla = 0; (pPla < playerOptions.size() && getNextPlayer); pPla++) {
                int roundWinningCandidatePosition = rankingOptions.getCandidatePosition(roundWinningCandidate, playerOptions.get(pPla));
                for(int pCan = 1; pCan < roundWinningCandidatePosition; pCan++) {
                    List<List<String>> playersOptionsRemake = makePossibleRerankings(roundWinningCandidatePosition, pPla, pCan);
                    List<Vec2<String,Integer>> repSortedDescOrderCandidateRankings = sortPossibleRerankings(playersOptionsRemake, round);
                    String repWinningCandidate = testRerankingsUsability(repSortedDescOrderCandidateRankings, playersOptionsRemake, pPla);

                    /*
                     * Examining if it's worth to move on; or we're in a Nash Equilibrium.
                     */
                    playNextRound = false;
                    if(repWinningCandidate.equals(playersOptionsRemake.get(pPla).get(0))) {
                        wasSwappedPreviously = playersOptionsRemake.get(pPla).get(0);
                        playerOptions = playersOptionsRemake;
                        getNextPlayer = false;
                        playNextRound = true;
                        break;
                    }
                }
            }
        } while(playNextRound && (round++) <= Tmax);
        System.out.println("Round winners :" + roundCandidateRankings);
        try {
            String sRoundCandidateRankings = roundCandidateRankings.toString();
            sRoundCandidateRankings = sRoundCandidateRankings.substring(2, sRoundCandidateRankings.length() - 2);
            sRoundCandidateRankings = sRoundCandidateRankings.replaceAll("\\], \\[", "\n"); // 1 round per line
            PrintWriter pw = new PrintWriter(outputDataRepository + "/" + electionSampleName + "_results.txt");
            pw.write(sRoundCandidateRankings); pw.flush(); pw.close();
        } catch (FileNotFoundException exc) {
            System.err.println("Error writing file to disk; please check relevant permissions.");
            exc.printStackTrace();
        }
    }

    /**
     * This method sorts the actual rankings of the current round; in descending order.
     */
    public List<Vec2<String,Integer>> sortActualRankings(String wasPreviouslySwapped) {
        List<Vec2<String, Integer>> unsortedCandidateRankings = rankingOptions.getVotingResults(playerOptions),
                descOrderSortedCandidateRankings = new ArrayList<Vec2<String, Integer>>();
        unsortedCandidateRankings.sort(new DefaultComparator());
        boolean wasPreviouslySwapped_Flag = false;
        if (wasPreviouslySwapped != null){
            Vec2<String, Integer> wasPreviouslySwapped_Vec = rankingOptions.getCandidate(wasPreviouslySwapped, unsortedCandidateRankings);
            descOrderSortedCandidateRankings.add(wasPreviouslySwapped_Vec);
            wasPreviouslySwapped_Flag = true;
        }
        for(int rank = unsortedCandidateRankings.size() - 1; rank >= 0; rank--) {
            if(!wasPreviouslySwapped_Flag || !unsortedCandidateRankings.get(rank).getTValue().equals(wasPreviouslySwapped))
                    descOrderSortedCandidateRankings.add(unsortedCandidateRankings.get(rank));
        }
        return descOrderSortedCandidateRankings;
    }

    /**
     * This method computes the possible rerankings; where exists a Best Response Dynamic.
     * Players can probably perform such "tricks", lying for their true preferences, in ascending
     * order (P1, if no changes made from P1 then P2, then P3...). Except for the one preference
     * examined if is included in a Best Response Dynamics; the rest remain in their relative
     * position based on the player's initial preferences.
     * @return The proposed reranking.
     */
    public List<List<String>> makePossibleRerankings(int roundWinningCandidatePosition, int pPla, int pCan) {
        List<List<String>> playersOptionsRemake = new ArrayList<List<String>>(playerOptions);
        List<String> changePlayerOptionsRemake = new ArrayList<String>();
        changePlayerOptionsRemake.add(playerOptions.get(pPla).get(pCan));
        for(int rCan = 1; rCan < playerOptions.get(pPla).size(); rCan++) {
            if (rCan <= pCan) changePlayerOptionsRemake.add(playerOptions.get(pPla).get(rCan-1));
            else changePlayerOptionsRemake.add(playerOptions.get(pPla).get(rCan));
        }
        playersOptionsRemake.set(pPla, changePlayerOptionsRemake);
        return playersOptionsRemake;
    }

    /**
     * This method sorts the possible reranking of the current round; in descending order.
     * @return The desc. order sorted ranking list.
     */
    public List<Vec2<String,Integer>> sortPossibleRerankings(List<List<String>> playersOptionsRemake, int round) {
        List<Vec2<String,Integer>> repUnsortedCandidateRankings = rankingOptions.getVotingResults(playersOptionsRemake),
                repSortedDescOrderCandidateRankings = new ArrayList<Vec2<String,Integer>>();
        repUnsortedCandidateRankings.sort(new DefaultComparator());
        for(int rank = repUnsortedCandidateRankings.size() - 1; rank >= 0; rank--) {
            repSortedDescOrderCandidateRankings.add(repUnsortedCandidateRankings.get(rank));
        }
        return repSortedDescOrderCandidateRankings;
    }

    /**
     * This method examines if a proposed ranking is worth keeping for the player lying in the
     * current round. If a candidate's preference swapped to the first position of the list is the new
     * winner; then it is and that player becomes winner;
     * @return The new winner name.
     */
    public String testRerankingsUsability(List<Vec2<String,Integer>> repSortedDescOrderCandidateRankings, List<List<String>> playersOptionsRemake, int pPla) {
        String repPlayerFirstChoiceCandidate = playersOptionsRemake.get(pPla).get(0);
        String repWinningCandidate = repSortedDescOrderCandidateRankings.get(0).getTValue();
        Vec2<String,Integer> repPlayerFirstChoiceCandidate_Configuration = rankingOptions.getCandidate(repPlayerFirstChoiceCandidate, repSortedDescOrderCandidateRankings);
        Vec2<String,Integer> repWinningCandidate_Configuration = repSortedDescOrderCandidateRankings.get(0);
        if(repPlayerFirstChoiceCandidate_Configuration.getYValue() == repWinningCandidate_Configuration.getYValue()) {
            int repPlayerFirstChoiceCandidate_Configuration_Position = rankingOptions.getCandidatePositionML(repPlayerFirstChoiceCandidate, repSortedDescOrderCandidateRankings);
            int repWinningCandidate_Configuration_Position = rankingOptions.getCandidatePositionML(repWinningCandidate, repSortedDescOrderCandidateRankings);
            swapCandidateVectors(repSortedDescOrderCandidateRankings, repPlayerFirstChoiceCandidate_Configuration_Position, repWinningCandidate_Configuration_Position);
            repWinningCandidate = repPlayerFirstChoiceCandidate;
        }
        return repWinningCandidate;
    }

    /**
     * This method swaps two 2D vectors in the given positions of a vector list.
     */
    public void swapCandidateVectors(List<Vec2<String,Integer>> repSortedDescOrderCandidateRankings, int pos1, int pos2) {
        Vec2<String, Integer> repWinningCandidate_vec2 = repSortedDescOrderCandidateRankings.get(pos1);
        repSortedDescOrderCandidateRankings.set(pos1, repSortedDescOrderCandidateRankings.get(pos2));
        repSortedDescOrderCandidateRankings.set(pos2, repWinningCandidate_vec2);
    }
}
