import java.util.ArrayList;
import java.util.List;

/**
 *  Borda.java
 *  This class illustrates Borda voting policy; according to which each candidate is assigned
 *  a number of votes, according to his position in each voter's preferences list, except for his
 *  least preferable candidate.
 *  @authors Georgios M. Moschovis (p3150113@aueb.gr)
 */
public class Borda extends VotingPolicies {
	/**
	 *	This method computes election results; according to Borda voting policy.
	 */
	public List<Vec2<String,Integer>> getVotingResults (List<List<String>> playerOptions) {
		List<Vec2<String,Integer>> candidateRankings = new ArrayList<Vec2<String,Integer>>();
		for(List<String> playerVotes: playerOptions) {
			int rep = 1;
			for(String playerVote: playerVotes) {
				if(isIncluded(playerVote, candidateRankings))
					getCandidate(playerVote, candidateRankings).setYValue(getCandidate(playerVote, candidateRankings).getYValue() + (playerOptions.size() - (rep++)));
				else candidateRankings.add(new Vec2<String,Integer>(playerVote, (playerOptions.size() - (rep++))));
			}
		}
		return candidateRankings;
	}
}