import java.util.ArrayList;
import java.util.List;

/**
 *  Plurality.java
 *  This class illustrates Plurality voting policy; according to which each one voter's most
 *  preferable candidate is assigned one vote, according to his position in each voter's preferences
 *  list, the rest get no votes.
 *  @authors Georgios M. Moschovis (p3150113@aueb.gr)
 */
public class Plurality extends VotingPolicies {
	/**
	 *	This method computes election results; according to Plurality voting policy.
	 */
	public List<Vec2<String,Integer>> getVotingResults(List<List<String>> playerOptions) {
		List<Vec2<String,Integer>> candidateRankings = new ArrayList<Vec2<String,Integer>>();
		for(List<String> playerVotes: playerOptions) {
			String playerVote = playerVotes.get(0);
			if(isIncluded(playerVote, candidateRankings))
				getCandidate(playerVote, candidateRankings).setYValue(getCandidate(playerVote, candidateRankings).getYValue() + 1);
			else candidateRankings.add(new Vec2<String,Integer>(playerVote, 1));
		}
		return candidateRankings;
	}
}