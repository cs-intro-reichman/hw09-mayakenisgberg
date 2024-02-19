import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    static HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;


    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
        String window="";
        char c;
        In in = new In(fileName);
        for (int i=0;i<this.windowLength;i++)
        {
        window+=in.readChar();
        }
        while(!in.isEmpty())
        {
            c=in.readChar();
            if(LanguageModel.CharDataMap.get(window)==null)
            {
               List probs = new List();
               CharDataMap.put(window,probs);
            }            
            LanguageModel.CharDataMap.get(window).update(c);//just probs?
            window=window.substring(1)+c;
        }
        for(List probs : LanguageModel.CharDataMap.values() )
        {
            this.calculateProbabilities(probs);
        }

        
	}
    

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */

	public  void calculateProbabilities(List probs) {				
		double countChar = 0;
        for(int i=0; i<probs.getSize();i++)
        {
            countChar+= probs.get(i).count;
        }
        for(int i=0;i<probs.getSize();i++)
        {
            probs.get(i).p= (double) (1/countChar) * probs.get(i).count;
        }
        probs.getFirst().cp=probs.getFirst().p;
        for(int n=1;n<probs.getSize();n++)
        {
            probs.get(n).cp=probs.get(n-1).cp+probs.get(n).p;
        }


	}
    // Returns a random character from the given probabilities list.
	public  char getRandomChar(List probs) {
        double r = randomGenerator.nextDouble();
        for(int i=0;i<probs.getSize();i++)
        {
            if(probs.get(i).cp>r)
            return probs.get(i).chr;
        }
        return ' ';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
        if (textLength<windowLength)
        {
            return initialText;
        }
        String window=initialText.substring(initialText.length()-windowLength);
        String genText=window;
        while(textLength!=genText.length()-windowLength)
        {
            if(CharDataMap.get(window)==null)
            {
                return genText;
            }
            List probs = LanguageModel.CharDataMap.get(window);
            char c=getRandomChar(probs);
            genText=genText+c;
            window=genText.substring(genText.length()-windowLength);
        }
        return genText;

	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}
    

    public static void main(String[] args) {
     int windowLength = Integer.parseInt(args[0]);
     String initalText = args [1];
     int generatedTextLength = Integer.parseInt(args[2]);
     boolean randomGeneration = args[3].equals("random");
     String fileName = args[4];
     
     LanguageModel lm;
     if (randomGeneration)
     {
        lm = new LanguageModel(windowLength);
    
     }
     else
     {
        lm= new LanguageModel(windowLength, 20);
     }
      lm.train(fileName);
      System.out.println(lm.generate(initalText, generatedTextLength));	
    }
    // my main for testing
        // List test = new List();
        //  String str = "committee ";
        //  for(int i=0;i<str.length();i++)
        //  {
        //      test.update(str.charAt(i));
        //  }
        //  calculateProbabilities(test);
        //  //System.out.println(test.toString());
        //  int [] arr = new int [test.getSize()];
        //  for(int i=0;i<1000000;i++)
        //  {
        //    char chr=getRandomChar(test);
        //    arr[test.indexOf(chr)]++;
        //  }
        //  for (int i=0; i<arr.length;i++)
        //  {
        //  System.out.print(test.get(i).p+" ");
        //  }
        //  System.out.println();

        //  for (int i=0; i<arr.length;i++)
        //  {
        //  System.out.print(arr[i]+" ");
        //  }
        // LanguageModel test = new LanguageModel(2);
        // test.train("sentence.txt");
        // System.out.println(test.toString());
        


}
