import java.util.Arrays;

public class DataCube {
    public static int ceiling = 4;
    Dimension[] dimension;
    int numberTuples;


    public DataCube(int numDim) {
        dimension = new Dimension[numDim];
    }

    /**
     * @param maxValue   the highest attribute value to be stored
     * @param lowerValue the highest attribute value to be stored
     *                   <p>
     *                   This method constructs a dimension, giving it the lowest and highest values
     *                   Note that the method defineCardinalities() must be used to further build the dimensions.
     */
    public void initDim(int dimnNumber, int maxValue, int lowerValue) {
        dimension[dimnNumber] = new Dimension(lowerValue, maxValue);
    }


    /**
     * @param dimensionNumber number of the dimension being called (having n dimensions -> [0; n-1])
     * @param sizes           array with the cardinality of
     */
    public void defineArraySizesForHashMap(int dimensionNumber, int[] sizes) {
        this.dimension[dimensionNumber].defineArraySizesForHashMap(sizes, this.numberTuples);
    }


    /**
     * @param tid         this tuple id
     * @param tupleValues Array of values to each dimension.
     */
    public void addTuple(int tid, int[] tupleValues) {
        for (int i = 0; i < dimension.length; i++) {
            dimension[i].addTid(tid, tupleValues[i]);
        }
    }


    public int getNumberDimensions() {
        return dimension.length;
    }

    public int getNumberTuples() {
        return numberTuples;
    }

    public void setNumberTuples(int numberTuples) {
        this.numberTuples = numberTuples;
    }

    /**
     * @param query the query being made
     * @return all the tids obtained from such query
     */
    public int pointQueryCounter(int[] query) {
        MixedArray mat = pointQuerySeach(query);
        if (mat == null)
            return -1;
        return mat.getNumberUsedTids();

    }


    /**
     * @param query the query being made
     * @return array with the tuple ids, null if query.length != shellFragmentList.length
     */
    public MixedArray pointQuerySeach(int[] query) {

        if (query.length != dimension.length)
            return null;

        int instanciated = 0;
        MixedArray[] tidsList = new MixedArray[dimension.length];
        for (int i = 0; i < query.length; i++) {
            if (query[i] != Main.instantiated && query[i] != Main.inquired) {
                MixedArray secundary = dimension[i].getTidsListFromValue(query[i]);
                if (secundary.getNumberTids() == 0)       //se o valor colocado nao der resultados
                    return secundary;
                if (instanciated == 0)          //se ainda nada tiver sido instanciado
                    tidsList[0] = secundary;
                else {
                    //the tid lists are being stored in this array orderly, allowing for some runtime saves
                    for (int n = instanciated - 1; n >= 0; n--) {
                        if (tidsList[n].getNumberTids() > secundary.getNumberTids()) {
                            tidsList[n + 1] = tidsList[n];
                            if (n == 0)
                                tidsList[0] = secundary;
                        } else {
                            tidsList[n + 1] = secundary;
                            break;
                        }
                    }

                }
                instanciated++;
            }
        }
        MixedArray result;
        if (instanciated > 0) {
            result = tidsList[0];
            for (int i = 1; i < instanciated; i++) {

                result = intersect(result, tidsList[i]);

                if (result.getNumberTids() == 0)
                    return result;
            }
            return result;
        } else {
            result = new MixedArray(false, numberTuples);
            for (int i = 0; i < numberTuples; i++)
                result.addTid(i);
        }

        return result;
    }

    private MixedArray intersect(MixedArray arrayA, MixedArray arrayB) {
        if (!arrayA.surpassCeiling && !arrayB.surpassCeiling) {                             //if both bitmap
            MixedArray result = new MixedArray(false, arrayA.numberTids);        //creates bitmap
            for (int i = 0; i < result.numberTids; ++i)                                     //for each bit
                if (arrayA.bitmap[i] && arrayB.bitmap[i])                                   //if is true in both
                    result.bitmap[i] = true;                                                  //adds to the resulting array
            return result;                                                                  //returns array

        } else if (arrayA.surpassCeiling && arrayB.surpassCeiling) {                        //if both are inverted index
            return new MixedArray(intersectArrays(arrayA.invertedIndex, arrayB.invertedIndex));//does inverted index intersection method
        } else {
            //creates resulting bitmap
            int[] resultingArray = new int[arrayA.getNumberUsedTids() > arrayB.getNumberUsedTids() ? arrayB.getNumberUsedTids() : arrayA.getNumberUsedTids()];
            int a = 0;

            if (arrayA.surpassCeiling) {
                for (int i = 0; i < arrayA.numberTids; i++)
                    if (arrayB.bitmap[arrayA.invertedIndex[i]])
                        resultingArray[a++] = arrayA.invertedIndex[i];
            } else
                for (int i = 0; i < arrayB.numberTids; i++)
                    if (arrayA.bitmap[arrayB.invertedIndex[i]])
                        resultingArray[a++] = arrayB.invertedIndex[i];

            MixedArray result = new MixedArray(true, a);
            for (int i = 0; i < a; result.invertedIndex[i] = resultingArray[i++]) ;

            return result;
        }

    }

    /**
     * @param arrayA
     * @param arrayB
     * @return chamar conuntos com o menor numero de tuples possivel
     */
    private static int[] intersectArrays(int[] arrayA, int[] arrayB) {
        int[] c = new int[Math.min(arrayA.length, arrayB.length)];
        int ai = 0, bi = 0, ci = 0;

        while (ai < arrayA.length && bi < arrayB.length) {
            if (arrayA[ai] == arrayB[bi]) {
                if (ci == 0 || arrayA[ai] != c[ci - 1]) {
                    c[ci++] = arrayA[ai];
                }
                ai++;
                bi++;
            } else if (arrayA[ai] > arrayB[bi]) {
                bi++;
            } else if (arrayA[ai] < arrayB[bi]) {
                ai++;
            }
        }

        return Arrays.copyOfRange(c, 0, ci);
    }
}
