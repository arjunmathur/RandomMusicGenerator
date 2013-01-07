package randomMusicGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * MarkovMap: <p>
 * 
 * The MarkovMap data structure is an extension of the HashMap that
 * can represent a Markov chain of Strings. A Markov chain is a statistical
 * model of state transitions where each subsequent state is dependent only
 * on the current state (see <a href="http://en.wikipedia.org/wiki/Markov_chain">Wikipedia</a>
 * for more information). Each call to {@code mapTransition(key, value)} increases
 * the probability of the state transition from key->value. A call to next(seed)
 * will return the next state transition based on these probabilities. <p>
 * 
 * For example: Given a file of 'words', the MarkovMap can map the probabilities of all
 * transitions between a word and the next word. Each 'word' can be defined
 * by how words are delimited. For the English language, this would be whitespace,
 * periods, commas etc. For <a href="abcnotation.com">abc notation</a>, this
 * would be whitespace and bars ('|'). Etcetera.
 * 
 * @author Arjun Mathur
 *
 */
public class MarkovMap extends HashMap<String, ArrayList<String>> {
	
	/**  Quick-Fix generated serial UID  */
	private static final long serialVersionUID = 2199934985144546855L;
	
	//TODO: scan null check
	public MarkovMap() {
		
	}
	
		
	/** Function: mapTransition <br>
	 * ----------------- <br>
	 * Based on other current mapped transitions from param 'key'
	 * mapTransition("key", "value") will increase the probability of the
	 * Markov state transition from "key"->"value" <p>
	 * 
	 * If either key or value are null, nothing is done.
	 * 
	 * @param key An existing or new key in the MarkovMap
	 * @param value A String to map key to
	 */
	public void mapTransition(String key, String value){
		if(key == null || value == null) return;
		
		/* Find an existing value ArrayList else create a new one */
		ArrayList<String> values = get(key);
		if(values == null){
			values = new ArrayList<String>();
			put(key, values);
		}
		
		/* Increasing probabilities are determined by the number of copies 
		 * of a value in the ArrayList, so just add the value */
		values.add(value);
	}
	
	/** Function: mostCommonSeed <br>
	 * ------------------------- <br>
	 * Returns the seed (key) in the MarkovMap with
	 * the most mapped values. In the event that there
	 * is more than one possible result, any one of the
	 * values will be returned.
	 */
	public String mostCommonSeed() {
		String result = null;
		int maxOccurences = 0;
		for(String seed : keySet()){
			int occurences = get(seed).size();
			if(occurences > maxOccurences){
				result = seed;
				maxOccurences = occurences;
			}
		}
		return result;
	}
	
	/** Function: next <br>
	 *  Usage: String str = map.next("key") <br>
	 * -----------------------------------------<br>
	 * Returns the next Markov state weighted on the frequency of its 
	 * mapping from param 'seed'. <p> 
	 * 
	 * For Example: If "seed" is mapped only to "abc" 6 times and "def"
	 * 4 times, next("seed") will return "abc" 60% of the time and 
	 * "def" 40% of the time.
	 * 
	 * @param seed A key in the MarkovMap
	 * 
	 * @return statistical transition from Markov state 
	 * 'seed', or null if no mappings from 'seed' exist
	 */
	public String next(String seed){
		seed = seed.replaceAll("[^A-Za-z]", "");
		ArrayList<String> choices = get(seed);
		if(choices == null) return null;
		
		/* Since a value exists in the ArrayList as many
		 * times as it has been mapped from this specific
		 * seed, simply getting a random element from choices
		 * will return the proper statistical transition */
		Random rand = new Random();
		int index = rand.nextInt(choices.size());
		return choices.get(index);
	}
}
