public class Dimension {


    MixedArray[] arrays;
    int lower;
    int upper;

    /**
     * @param lower lowest attribute value to be represented
     * @param upper highest attribute value to be represented
     *              <p>
     *              This method only stores the value and constructs the MixedArray hashmap.
     *              The method defineCardinalities() must be used before adding any data to the hashmap
     */
    public Dimension(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
        this.arrays = new MixedArray[upper - lower + 1];
    }


    /**
     * @param arraySizes array with the cardinalities for each attribute value.
     */
    public void defineArraySizesForHashMap(int[] arraySizes, int numberTuples) {
        if (arrays.length != arraySizes.length) {
            Exception e = new Exception();
            e.printStackTrace();
            System.exit(1);
        }

        for (int i = 0; i < arrays.length; ++i)
            //only creates arrays that store something
            if (arraySizes[i] > 0)
                if (arraySizes[i] > DataCube.ceiling)
                    arrays[i] = new MixedArray(true, arraySizes[i]);
                else
                    arrays[i] = new MixedArray(false, numberTuples);

    }

    public void addTid(int tid, int value) {
        arrays[value - lower].addTid(tid);
    }

    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0. Care that the array of a found value may be zero as well, so it's not a flag
     */
    public MixedArray getTidsListFromValue(int value) {
        if (value > upper || value < lower || arrays[value - lower] == null)
            return new MixedArray(false, 0);

        return arrays[value - lower];
    }
}
