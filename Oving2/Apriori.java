package no.ntnu.idi.tdt4300.apriori;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This is the main class of the association rule generator.
 * <p>
 * It's a dummy reference program demonstrating the accepted command line arguments, input file format and standard output
 * format also required from your implementation. The generated standard output follows the CSV (comma-separated values) format.
 * <p>
 * It's up to you if you use this program as your base, however, it's very important to strictly follow the given formatting
 * of the inputs and outputs. Your assignment will be partly automatically evaluated, therefore keep the input arguments
 * and output format identical.
 * <p>
 * Alright, I believe it's enough to stress three times the importance of the input and output formatting. Four times...
 *
 * @author tdt4300-undass@idi.ntnu.no
 */
public class Apriori {

    /**
     * Loads the transaction from the ARFF file.
     *
     * @param filepath relative path to ARFF file
     * @return list of transactions as sets
     * @throws java.io.IOException signals that I/O error has occurred
     */
    public static List<List> readTransactionsFromFile(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        List<String> attributeNames = new ArrayList<String>();
        List<SortedSet<String>> itemSets = new ArrayList<SortedSet<String>>();
        List<List> transactionList = new ArrayList<List>(); //DENNE
     
        
        String line = reader.readLine();
        while (line != null) {
            if (line.contains("#") || line.length() < 2) {
                line = reader.readLine();
                continue;
            }
            if (line.contains("attribute")) {
                int startIndex = line.indexOf("'");
                if (startIndex > 0) {
                    int endIndex = line.indexOf("'", startIndex + 1);
                    attributeNames.add(line.substring(startIndex + 1, endIndex));
                }
            } else {
                SortedSet<String> is = new TreeSet<String>();
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                int attributeCounter = 0;
                String itemSet = "";
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken().trim();
                    if (token.equalsIgnoreCase("t")) {
                        String attribute = attributeNames.get(attributeCounter);
                        itemSet += attribute + ",";
                        is.add(attribute);
                    }
                    attributeCounter++;
                }
                itemSets.add(is);
            }
            line = reader.readLine();
        }
        reader.close();
        transactionList.add(itemSets); //denne
        transactionList.add(attributeNames); //denne
        return transactionList;    
    }

    /**
     * Generates the frequent itemsets given the support threshold. The results are returned in CSV format.
     *
     * @param transactions list of transactions
     * @param support      support threshold
     * @return frequent itemsets in CSV format with columns size and items; columns are semicolon-separated and items are comma-separated
     */
   
 
    public static double getSup(List<SortedSet<String>> transactions, SortedSet<String> itemSet){
    	double sup = 0.0;
    	boolean inTransaction = true;
    	for(int i = 0; i<transactions.size(); i++){
    		for(String j :itemSet){
    			if(transactions.contains(j)){inTransaction=false; break;}
    		} if(inTransaction){sup++;}
    	}
    	return (sup/transactions.size());
    }

    public static boolean compatible(SortedSet<String> set_1, SortedSet<String> set_2){
    if(set_1.size()==1){return true;}
    else{
    	SortedSet<String> set_k1 = new TreeSet<String>(set_1.subSet(set_1.first(),set_1.last()));
    	SortedSet<String> set_k2 = new TreeSet<String>(set_2.subSet(set_2.first(),set_2.last()));
        return(set_k1.equals(set_k2));
    	}
    }
    
    public static SortedSet<String> combine(SortedSet<String> set_1, SortedSet<String> set_2){
        SortedSet<String> combined_set = new TreeSet<String>(set_1);
        combined_set.add(set_2.last());
        return combined_set;
    }
    
    public static String generateFrequentItemsets(List<SortedSet<String>> transactions, SortedSet<String> attributes,double minsup) {
        // TODO: Generate and print frequent itemsets given the method parameters.
        List<ArrayList<SortedSet<String>>> frequent_sets = new ArrayList<ArrayList<SortedSet<String>>>();
        ArrayList<SortedSet<String>> frequent_set = new ArrayList<SortedSet<String>>();
       
        for(String attribute : attributes){
            SortedSet<String> item_set = new TreeSet<String>();
            item_set.add(attribute);
            if(getSup(transactions, item_set)>=minsup) frequent_set.add(item_set);
        }
       
        frequent_sets.add(frequent_set);
       
        while(frequent_sets.get(frequent_sets.size()-1).size()>1){
            ArrayList<SortedSet<String>> check_set  = frequent_sets.get(frequent_sets.size()-1);
            ArrayList<SortedSet<String>> sets = new ArrayList<SortedSet<String>>();
            int num=1;
            for(SortedSet<String> set : check_set){
                for(int i=num; i<check_set.size(); i++){
                    SortedSet<String> against = check_set.get(i);
                    if(compatible(set,against)){
                        SortedSet<String> combinedItemSet = combine(set, against);
                        if(getSup(transactions, combinedItemSet)>=minsup){
                        	sets.add(combinedItemSet);}
                    }
                }num+=1;
            }frequent_sets.add(sets);
        }
       
      
        String result = "size;items\n";
        for(ArrayList<SortedSet<String>> sets : frequent_sets){
            for(SortedSet<String> set : sets){
                result+=set.size() + ";";
                for(String item : set){
                    result+=item + ",";
                }
                result=result.substring(0, result.length()-1);
                result+="\n";
            }
        }
        return result;
    }
    	
    /**
     * Generates the association rules given the support and confidence threshold. The results are returned in CSV
     * format.
     *
     * @param transactions list of transactions
     * @param support      support threshold
     * @param confidence   confidence threshold
     * @return association rules in CSV format with columns antecedent, consequent, confidence and support; columns are semicolon-separated and items are comma-separated
     */
    public static String generateAssociationRules(List<SortedSet<String>> transactions, double support, double confidence) {
        // TODO: Generate and print association rules given the method parameters.

        return "antecedent;consequent;confidence;support\n" +
                "diapers;beer;0.6;0.5\n" +
                "beer;diapers;1.0;0.5\n" +
                "diapers;bread;0.8;0.67\n" +
                "bread;diapers;0.8;0.67\n" +
                "milk;bread;0.8;0.67\n" +
                "bread;milk;0.8;0.67\n" +
                "milk;diapers;0.8;0.67\n" +
                "diapers;milk;0.8;0.67\n" +
                "diapers,milk;bread;0.75;0.5\n" +
                "bread,milk;diapers;0.75;0.5\n" +
                "bread,diapers;milk;0.75;0.5\n" +
                "bread;diapers,milk;0.6;0.5\n" +
                "milk;bread,diapers;0.6;0.5\n" +
                "diapers;bread,milk;0.6;0.5\n";
    }

    /**
     * Main method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // definition of the accepted command line arguments
        Options options = new Options();
        options.addOption(Option.builder("f").argName("file").desc("input file with transactions").hasArg().required(true).build());
        options.addOption(Option.builder("s").argName("support").desc("support threshold").hasArg().required(true).build());
        options.addOption(Option.builder("c").argName("confidence").desc("confidence threshold").hasArg().required(false).build());
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            // extracting filepath and support threshold from the command line arguments
            String filepath = cmd.getOptionValue("f");
            double support = Double.parseDouble(cmd.getOptionValue("s"));

            // reading transaction from the file
            List<SortedSet<String>> transactions = readTransactionsFromFile(filepath).get(0);
            List<String> attributes = readTransactionsFromFile(filepath).get(1);
            
            SortedSet<String> sorted = new TreeSet<String>(attributes);
 
            if (cmd.hasOption("c")) {
                // extracting confidence threshold
                double confidence = Double.parseDouble(cmd.getOptionValue("c"));

                // printing generated association rules
                System.out.println(generateAssociationRules(transactions, support, confidence));
            } else {
                // printing generated frequent itemsets
                System.out.println(generateFrequentItemsets(transactions,sorted, support));
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.setOptionComparator(null);
            helpFormatter.printHelp("java -jar apriori.jar", options, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	//private static char[] generateFrequentItemsets(List<List> transactions,
		//	double support) {
		// TODO Auto-generated method stub
		//return null;
	//}

}
