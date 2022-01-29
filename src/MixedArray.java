import java.util.Arrays;

public class MixedArray {
    /*
      This class is used to store either inverted index arrays or bitmap arrays.
      A global variable is used to define a cardinality ceiling.
      If the cardinality is bellow the ceiling, bitmap arrays are used.
      Otherwise, inverted index arrays are used.

      Assuming an int being 4bytes -> 32 bits and a boolean being 1 bit,
      If the cardinality is above 32, inverted index arrays can save memory
      If the cardinality is bellow 32, bitmap arrays can save memory.
      If the cardinality is equal to 32, bitmap arrays have the advantage of having a faster intersecton method
     */

    public boolean surpassCeiling = false; //used to know if uses inverted index or bitmap. false = bitmap; true = inverted index

    public boolean[] bitmap;
    public int[] invertedIndex;
    public int numberTids;

    /**
     * @param surpassCeiling used to know if uses inverted index or bitmap. false = bitmap; true = inverted index
     * @param size           the size of the inverted index/ bitmap
     */
    public MixedArray(boolean surpassCeiling, int size) {
        this.surpassCeiling = surpassCeiling;

        if (surpassCeiling) {
            bitmap = null;
            invertedIndex = new int[size];
            numberTids = 0;
        } else {
            invertedIndex = null;
            bitmap = new boolean[size];
            numberTids = size;
        }
    }

    public MixedArray(int[] array) {
        this.surpassCeiling = true;
        invertedIndex = array;
        numberTids = array.length;
    }


    /**
     * @param newTid new vale being added
     */
    public void addTid(int newTid) {
        if (surpassCeiling) {
            invertedIndex[numberTids] = newTid;
            ++numberTids;
        } else {
            bitmap[newTid] = true;
        }
    }

    public int getNumberTids() {
        return numberTids;
    }

    public int getNumberUsedTids() {
        if (surpassCeiling)
            return numberTids;
        int num = 0;
        for (boolean n : bitmap)
            if (n)
                ++num;
        return num;
    }

    public void show() {
        if(surpassCeiling)
            System.out.println(Arrays.toString(invertedIndex));
        else
        {
            int num = 0;
            for(boolean n : bitmap)
                if(n)
                    ++num;
            int invert[] = new int[num];
            int t = 0;
            for(int i = 0; i < numberTids; ++i)
                if(bitmap[i])
                    invert[t++] = i;
            System.out.println(Arrays.toString(invert));
        }
    }
}
