package randomMusicGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


/* RandomMusicGenerator:
 * 
 * The RandomMusicGenerator uses a Markov chain process in order to 'randomly'
 * create music in the same style as defined corpus of music! Music is written and read
 * in abc notation (abcnotation.com), a text based language that can be used
 * to express western music. Because of its format, abc notation can easily
 * be converted into a MIDI audio file or even sheet music!
 * 
 * There are many people around the world who have transcribed music into this
 * format. Two good libraries of abc notation songs can be found at 
 * [lotroinfo.com/abc_library] and [sites.google.com/site/lotroabc/songs].
 * 
 * As input, the program requires an abc 'corpus'. Each abc song contains 
 * two parts: a header and the music. An abc corpus is defined as a collection
 * of many different abc songs' music combined into one text file (w/o header).
 * In order to create one, check the above websites for some songs that 
 * have something in common (same artist etc.), open them in your favorite 
 * text editor and copy all the music part into one text file. Drop that
 * file into the folder of this program, and run it!
 * 
 * The program will export a composed abc file which you can convert to audio at
 * [http://www.concertina.net/tunes_convert.html] 
 * or [http://www.mandolintab.net/abcconverter.php]
 * 
 * So go ahead and make your own Beethoven Sonata or addition to the
 * Zelda soundtrack...
 */
public class RandomMusicGenerator {

	public static final int NUM_MEASURES = 50; //Number of measures of music to generate
	public static final int WORDS_PER_MEASURE = 2; //Number of musical 'words' per measure
	public static final String ABC_WORD_DELIMITER = "\\s|\\|"; //Regex delimiter between 'words' in abc notation (whitespace or '|')
	public static final String NON_ALPHA_REGEX = "[^A-Za-z]"; //Regex for all non alphabetical chars
	
	public static void main(String[] args) {
		try{
		/* Read and analyze input */
		BufferedReader input = GetFile("Enter an abc corpus filename: ");
		MarkovMap map = new MarkovMap();
		mapAbcFile(map, input);
		input.close();
		
		/* Get output info from user and create File */
		StringBuilder songName = new StringBuilder();
		File outFile = CreateFile(songName);
		BufferedWriter output = new BufferedWriter(new FileWriter(outFile));
		
		/* Write Markov generated abc file */
		GenerateFileHeader(output, songName.toString());
		GenerateMusic(output, map, map.mostCommonSeed());
		output.close();
		Conclusion(outFile.getPath());
		}catch(IOException e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	
	/* Function: Conclusion
	 * --------------------
	 * A conclusion message to the user.
	 */
	private static void Conclusion(String filePath) {
		System.out.println();
		System.out.println("Congratulations! You have composed a new randomly generated abc file.");
		System.out.println("Your song exists as an abc file at: " + filePath);
		System.out.println("To convert the song into a MIDI file and sheet music, please visit http://www.mandolintab.net/abcconverter.php");
	}

	/* Function: GenerateFileHeader
	 * ----------------------------
	 * This function generates a FileHeader based on abc notation conventions
	 * (see abcnotation.com). There are currently a few restrictions on the
	 * type of song to be exported: It must be in 4/4 time and in C major.
	 * A more thorough analysis in order to remove these limitations will come
	 * soon...
	 */
	private static void GenerateFileHeader(BufferedWriter output, String songName) throws IOException {
		output.write("X:1\n");
		output.write("T:" + songName + '\n');
		output.write("C:RandomMusicGenerator\n");
		output.write("M:4/4\n");
		output.write("L:1/8\n");
		output.write("Q:1/4=100\n");
		output.write("K:Cmaj");
	}

	/* Function: CreateFile
	 * Usage: StringBuilder sb = new StringBuilder();
	 * 		  File f = CreateFile(sb);
	 * ----------------------------------------------
	 * This file asks for the user to define a song name, and creates
	 * a File in the current directory with the same name (without any
	 * reserved pathname characters) with a .abc extension. If a file
	 * with the requested name already exists, the function will reprompt
	 * the user. 
	 * 
	 * Besides returning the created File, the user must pass an empty
	 * StringBuilder as an outparameter for the song name.
	 */
	private static File CreateFile(StringBuilder outSongName) throws IOException {
		File f; String songName;
		while(true){
			songName = ReadLine("What would you like to call your song? ");
			String cleanName = songName.replaceAll("[<>:\\\"/|?*\\\\]", ""); //remove restricted pathname chars
			f = new File(cleanName + ".abc"); 
			if(!f.exists()) break;
			System.out.println("Sorry, that song name is already taken.");
		}
		f.createNewFile();
		outSongName.append(songName); //return the songname as an outparam
		return f.getAbsoluteFile();
	}

	/* Function: GenerateMusic
	 * -----------------------
	 * Given a MarkovMap and a seed to start with, this function will
	 * generate a sequence of abc notation text biased on the probabilities
	 * given in the MarkovMap. The first key is defined by parameter
	 * 'firstSeed'. The next key is the Markov transition from the previous key.
	 * 
	 * The generation will walk through in this way for NUM_MEASURES measures
	 * , or until a certain seed contains no mappings.
	 * 
	 */
	private static void GenerateMusic(BufferedWriter output, MarkovMap map, String firstSeed) throws IOException {
		if(firstSeed == null || map == null || map.isEmpty()) return;
		
		String seed = firstSeed;
		for(int i=0; i<NUM_MEASURES; i++){
			if(i%6 == 0) output.newLine(); //Add a newline every 6 measures for style
			for(int j=0; j<WORDS_PER_MEASURE; j++){
				seed = map.next(seed.replaceAll(NON_ALPHA_REGEX, "")); //Get the next seed based on an alpha-only current seed
				if(seed == null) return;//If there are no mappings, simply return what we have
				output.write(seed + " ");
			}
			output.write("| "); //Add a bar delimiter after every measure
			
		}
	}

	
	/* Function: mapAbcFile 
	 * ------------------
	 * Scans through a .abc file given by param 'br' and maps transition
	 * probabilities of each word to the next word in param 'map'. 'Words' are
	 * defined by what delimits them, given by the regex \\s|\\|
	 * (all white space and bars ('|')).
	 * 
	 * Moreover, in order to generalize seeds to match mappings in various .abc
	 * songs, all keys will be first removed of all non-alpha characters before
	 * being mapped. In abc notation, non-alpha characters dictate how fast or
	 * how many times a certain note should be played. As the same sequence of
	 * keys may be played at a different speed or different amount of times, a
	 * simple mapping from word->word would never locate many commonalities between
	 * different songs. Thus all non-alpha characters in keys are removed, although
	 * are preserved in values. 
	 * 
	 * For example: A file with contents "a c | a2 de" will add a mapping of a->c
	 * with 50% probability, a->de with 50% probability and c->a2 with 100% probability.
	 * 
	 */
	private static void mapAbcFile(MarkovMap map, BufferedReader br) throws IOException{
		String key = null, value = null, line;
		
		/* For each word in br */
		while ((line = br.readLine()) != null){
		    for (String word : line.split(ABC_WORD_DELIMITER)){
	        	
		    	/* Initialize key or value */
		    	if(key == null) key = word; //For first word
	        	else value = word;
	        	
	        	/* If key and value are valid */
	        	if(value != null && !value.isEmpty()){
        			key = key.replaceAll(NON_ALPHA_REGEX, ""); //Remove non-alpha chars
        			map.mapTransition(key, value);
    				key = value; //Get next key (current value)
        		}
	        	
		    }
		}
	}


	/*
	 * Function: GetFile
	 * Usage: BufferedReader bf = GetFile("Enter a filename: ");
	 * ----------------------------------------------------------
	 * Opens a Scanner of a user defined file. The filename
	 * is obtained from the console using a argument defined prompt
	 * 
	 * NOTE: It is the caller's responsibility to close the returned BufferedReader
	 */
	public static BufferedReader GetFile(String prompt) throws IOException {
		while(true){
			String filename = ReadLine(prompt);
			try {
				return new BufferedReader(new FileReader(filename));
			} catch (FileNotFoundException e) {
				System.out.println("Sorry, we couldn't find that file.");
			}
		}
	}
	
	/*
	 * Function: ReadLine
	 * Usage: String textFromUser = ReadLine("Please enter text")
	 * ----------------------------------------------------------
	 * Reads a line of text from the user through the console. 
	 */
	public static String ReadLine(String prompt) throws IOException{
		System.out.print(prompt);
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		return userInput.readLine();
		
	}

}


	
