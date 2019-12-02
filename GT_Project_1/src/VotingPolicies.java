import java.util.List;

/**
 *  Plurality.java
 *  This class illustrates a voting policies superclass; including Plurality and Borda..
 *  @authors Georgios M. Moschovis (p3150113@aueb.gr)
 */
public abstract class VotingPolicies {
    /**
     *	This method computes election results; depending on the voting policy.
     */
	public abstract List<Vec2<String,Integer>> getVotingResults(List<List<String>> playerOptions);

    /**
     *	This method examines if one candidate is included in the intermediate ranking results.
     */
    protected boolean isIncluded(String candidate, List<Vec2<String, Integer>> candidateRankings) {
        for(Vec2<String, Integer> playerRanking: candidateRankings) {
            if(candidate.equals(playerRanking.getTValue())) return true;
        }
        return false;
    }

    /**
     *	This method returns instance of one candidate included in the intermediate ranking results.
     */
    protected Vec2<String, Integer> getCandidate (String candidate, List<Vec2<String, Integer>> candidateRankings) {
        for(Vec2<String, Integer> playerRanking: candidateRankings) {
            if(candidate.equals(playerRanking.getTValue())) return playerRanking;
        }
        return null;
    }

    /**
     *	This method returns relative position of one candidate included in the intermediate ranking results.
     */
    protected int getCandidatePositionML (String candidate, List<Vec2<String, Integer>> candidateRankings) {
        for(int pos = 0; pos < candidateRankings.size(); pos++) {
            Vec2<String, Integer> playerRanking = candidateRankings.get(pos);
            if(candidate.equals(playerRanking.getTValue())) return pos;
        }
        return -1;
    }

    /**
     *	This method returns position (degree of preference) of one candidate in one voter's ranked preferences.
     */
    protected int getCandidatePosition (String candidate, List<String> playerRankings) {
        for(int pos = 0; pos < playerRankings.size(); pos++) {
            if(candidate.equals(playerRankings.get(pos))) return pos;
        }
        return -1;
    }
}