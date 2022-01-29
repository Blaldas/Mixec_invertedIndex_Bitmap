import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class Main {
    static DataCube mainCube;
    static int lowerValue = 1;
    static boolean verbose = false;
    static int ceiling;


    public static int inquired, instantiated;

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("fragCubing_java.jar <dataset name> <ceiling>");
            System.exit(1);
        }

        ceiling = Integer.parseInt(args[1]);

        Scanner sc = new Scanner(System.in);
        String path = "forest";//args[0];
        load(path);
        System.gc();

        System.out.println("Total memory used:\t" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes");

        //every single memory check and garbage collector call must be made inside this loop on order to avoid to have errors
        String input;
        do {
            System.out.println(">");
            input = sc.next();
            input += sc.nextLine();

            if (input.charAt(0) == 'q') {
                query(input);
                System.out.println("Used memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            } else if (input.toLowerCase().equals("sair") || input.toLowerCase().equals("exit") || input.toLowerCase().equals("x") || input.toLowerCase().equals("quit"))
                break;
            else if (input.toLowerCase().equals("v"))
                changeVerbose();
            else if (input.toLowerCase().equals("s"))
                showDataCube();
            else
                System.out.println("Unknown Command");

            System.gc();        //used to be able to do multiple operations in a single run.
        } while (true);


    }

    private static void showDataCube() {
        int dimnum = 1;
        for (Dimension d : mainCube.dimension) {

            System.out.println("\nDIM " + dimnum);
            ++dimnum;

            for (MixedArray m : d.arrays) {
                if(m != null)
                    m.show();
            }


        }

    }

    private static void load(String filename) {

        System.out.println("Loading <" + filename + ">...");

        Date startDate = new Date(), endDate;

        getCardinalities(filename);
        addInfoToDataCube(filename);
        //generalReadFromDisk(filename);

        endDate = new Date();
        long numSeconds = ((endDate.getTime() - startDate.getTime()));
        //System.gc();
        System.out.println("Miliseconds Used to Load the data\t" + numSeconds);             //tempo
        System.out.println("Dimensions loaded\t" + mainCube.getNumberDimensions());          //num dimensões
        System.out.println("number of tuples loaded\t" + mainCube.getNumberTuples());


        System.out.println("load end");


    }

    private static void getCardinalities(String filename) {

        Path path = Path.of(filename);

        try {
            String line = null;                                         //the information will be read here
            String[] values;
            //int [] sizes;       //size[0] -> num of tuple //else num of diferent values
            int numberTuples, numDimns;

            InputStream in = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            //first read -> reads the number of objects
            line = reader.readLine();
            values = line.split(" ");

            numberTuples = Integer.parseInt(values[0]);
            numDimns = values.length - 1;

            //get the highest values of each dimension and initializes it
            int[] highestValue = new int[numDimns];    //usado para guardar o maior valor de cada dimensão
            int[][] storeAttValueUsage = new int[numDimns][]; //used to store the usage of each attribute value
            mainCube = new DataCube(numDimns); //initializes the number of dimensions

            mainCube.setNumberTuples(numberTuples);

            instantiated = lowerValue - 1;
            inquired = lowerValue - 2;

            //initializes the cardinality of each dimension
            for (int i = 0; i < values.length - 1; i++) {
                highestValue[i] = Integer.parseInt(values[i + 1]);
                mainCube.initDim(i, highestValue[i], lowerValue);
                storeAttValueUsage[i] = new int[highestValue[i]];
            }

            //reads the tuples in order to obtain the frequency of each attribute value
            for (int i = 0; i < numberTuples; ++i) {

                line = reader.readLine(); //reads the line

                values = line.split(" ");           //splits the line read into X Strings

                //tests if the tuple has the right number of attribute values
                if (values.length != numDimns) {
                    System.out.println("tuple id = " + i + " doesn't have the same number of dimensions");
                    System.exit(1);
                }

                //increases the counter of each atttribute value that belongs to the tuple being processed
                for (int n = 0; n < numDimns; n++) {
                    ++storeAttValueUsage[n][Integer.parseInt(values[n]) - 1];
                }
            }

            //após obter a frequencia de cada atributo de valor;
            //adiciona a frequencia às dimensões
            for (int i = 0; i < numDimns; ++i)
                mainCube.defineArraySizesForHashMap(i, storeAttValueUsage[i]);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addInfoToDataCube(String filename) {
        Path path = Path.of(filename);

        try {
            String line = null;                                         //the information will be read here
            String[] values;
            int[] tupleValues;

            InputStream in = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            line = reader.readLine(); // reads first line
            tupleValues = new int[line.split(" ").length - 1];  //used to store the tuple value in each dimention

            int numberTuples = Integer.parseInt(line.split(" ")[0]);

            for (int i = 0; i < numberTuples; ++i) {
                //reads and splits the line
                line = reader.readLine();
                values = line.split(" ");


                //transforms the tuple values into integers
                for (int n = 0; n < tupleValues.length; n++)
                    tupleValues[n] = Integer.parseInt(values[n]);

                //ads the tuple
                mainCube.addTuple(i, tupleValues);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param input user input. Something like "q 1 2 3"
     */
    private static void query(String input) {

        String[] stringValues = input.split(" ");   // faz split de cada uma das diemnsões
        int[] values = new int[stringValues.length - 1];

        boolean subCubeFlag = false;

        //coloca cada um dos valores no aray values
        for (int i = 1; i < stringValues.length; i++) {
            try {
                values[i - 1] = Integer.parseInt(stringValues[i]);
            } catch (Exception e) {
                //same as String.equals() since java 7
                switch (stringValues[i]) {
                    case "?":
                        values[i - 1] = inquired;    // lowerValue-1-> inquire (?)
                        subCubeFlag = true;
                        break;
                    case "*":
                        values[i - 1] = instantiated;     //lowerValue-2 -> !instantiation (*)
                        break;
                    default:
                        System.out.println("Invalid value in query");
                        return;
                }
            }
        }

        Date startDate = new Date(), endDate;           //incia as datas para fazer contagem do tempo
        if (subCubeFlag) {                  //caso seja um subcube
            // mainCube.getSubCube(values);
            ;
        } else {
            int searchResult = mainCube.pointQueryCounter(values); //returns array of ids
            if (searchResult == -1)
                System.out.println("Bad Query formation");
            else
                System.out.println("Query answers:\t" + searchResult);
        }
        endDate = new Date();
        long numSeconds = ((endDate.getTime() - startDate.getTime()));
        System.out.println("Query executed in " + numSeconds + " ms.");
        //System.out.println("Biggest ammount of memory used: " +  maxMemory + " bytes");

    }


    /**
     * Changes the verbose variable and signals its effects to the user as a printf
     */
    private static void changeVerbose() {
        verbose = !verbose;
        System.out.println("verbose: " + (verbose ? "showing results" : "not showing results"));
    }

}

