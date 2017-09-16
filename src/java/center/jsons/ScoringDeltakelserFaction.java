package center.jsons;
import center.jsons.ScoringGamesession;
import center.jsons.ScoringPlayer;
import org.json.simple.JSONObject;

/**
 *
 * @author Steinar
 * 
 */
public class ScoringDeltakelserFaction extends ScoringDeltakelser{
    

    public ScoringFaction fraksjonen;

    
 
    public ScoringDeltakelserFaction(ScoringGamesession tses, float tpo, int top, ScoringFaction fraks){
        super(tses, null, top, top);
        fraksjonen = fraks;
    }
    
}