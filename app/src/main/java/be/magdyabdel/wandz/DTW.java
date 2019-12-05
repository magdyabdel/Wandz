package be.magdyabdel.wandz;

import android.util.Log;

import java.util.Arrays;

import static java.lang.Math.abs;

public final class DTW {
    public double computeDTWError(final double[] recognitionSet, final double[] trainingSet) { //input all the samples in the rows for 3 columns(x,y,z)
        // Declare the amount of iterations(amount of rows and columns of warping table)
        final int recognitionLength = recognitionSet.length; //amount of rows of sample set
        final int trainingLength = trainingSet.length;       // amount of rows of trainingset
        double totalError = 0;                                    //initialise total error

        // Ensure the recognition samples are valid.
        if (recognitionLength == 0 || trainingLength == 0) {
            // Assert a bad result, return -1
            return -1;
        }


            // Declare the warping/cost matrix
            final double[][] costMatrix = new double[recognitionLength][trainingLength];
            int i, j;
            // Initialize the matrix (by filling in the first row and column)
            costMatrix[0][0] = this.getDistanceBetween(recognitionSet[0], trainingSet[0]);

            for (i = 1; i < recognitionLength; i++) { //first column
                costMatrix[i][0] = this.getDistanceBetween(recognitionSet[i], trainingSet[0]) + costMatrix[i - 1][0];
            }

            for (j = 1; j < trainingLength; j++) { //first row
                costMatrix[0][j] = this.getDistanceBetween(recognitionSet[0], trainingSet[j]) + costMatrix[0][j - 1];
            }

            //calculate the warping/cost matrix
            for (i = 1; i < recognitionLength; i++) {
                for (j = 1; j < trainingLength; j++) {
                    // Accumulate the path.
                    costMatrix[i][j] = (Math.min(Math.min(costMatrix[i - 1][j], costMatrix[i - 1][j - 1]), costMatrix[i][j - 1])) + this.getDistanceBetween(recognitionSet[i], trainingSet[j]);
                }
            }
            totalError += costMatrix[recognitionLength - 1][trainingLength - 1];

        // Return the distance
        return totalError / ((double) recognitionLength + (double) trainingLength); //the distance is the value in the top right corner, divide by total amount of distances
    }

    public double[] normalize(final double[] pSample, final double[] pTemplate) {
        // Declare Iteration Constants.
        final int lN = pSample.length;
        final int lM = pTemplate.length;
        // Ensure the samples are valid.
        if(lN == 0 || lM == 0) {
            // Assert a bad result.
            return  null;
        }
        // Define the Scalar Qualifier.
        int lK = 1;
        // Allocate the Warping Path. (Math.max(N, M) <= K < (N + M).
        int[][]    lWarpingPath  = new int[lN + lM][2];
        // Declare the Local Distances.
        final double[][] lL            = new double[lN][lM];
        // Declare the Global Distances.
        final double[][] lG            = new double[lN][lM];
        // Declare the MinimaBuffer.
        final double[]   lMinimaBuffer = new double[3];
        // Declare iteration variables.
        int i, j;
        // Iterate the Sample.
        for(i = 0; i < lN; i++) {
            // Fetch the Sample.
            final double lSample = pSample[i];
            // Iterate the Template.
            for(j = 0; j < lM; j++) {
                // Calculate the Distance between the Sample and the Template for this Index.
                lL[i][j] = this.getDistanceBetween(lSample, pTemplate[j]);
            }
        }

        // Initialize the Global.
        lG[0][0] = lL[0][0];

        for(i = 1; i < lN; i++) {
            lG[i][0] = lL[i][0] + lG[i - 1][0];
        }

        for(j = 1; j < lM; j++) {
            lG[0][j] = lL[0][j] + lG[0][j - 1];
        }

        for (i = 1; i < lN; i++) {
            for (j = 1; j < lM; j++) {
                // Accumulate the path.
                lG[i][j] = (Math.min(Math.min(lG[i-1][j], lG[i-1][j-1]), lG[i][j-1])) + lL[i][j];
            }
        }

        // Update iteration varaibles.
        i = lWarpingPath[lK - 1][0] = (lN - 1);
        j = lWarpingPath[lK - 1][1] = (lM - 1);

        // Whilst there are samples to process...
        while ((i + j) != 0) {
            // Handle the offset.
            if(i == 0) {
                // Decrement the iteration variable.
                j -= 1;
            }
            else if(j == 0) {
                // Decrement the iteration variable.
                i -= 1;
            }
            else {
                // Update the contents of the MinimaBuffer.
                lMinimaBuffer[0] = lG[i - 1][j];
                lMinimaBuffer[1] = lG[i][j - 1];
                lMinimaBuffer[2] = lG[i - 1][j - 1];
                // Calculate the Index of the Minimum.
                final int lMinimumIndex = this.getMinimumIndex(lMinimaBuffer);
                // Declare booleans.
                final boolean lMinIs0 = (lMinimumIndex == 0);
                final boolean lMinIs1 = (lMinimumIndex == 1);
                final boolean lMinIs2 = (lMinimumIndex == 2);
                // Update the iteration components.
                i -= (lMinIs0 || lMinIs2) ? 1 : 0;
                j -= (lMinIs1 || lMinIs2) ? 1 : 0;
            }
            // Increment the qualifier.
            lK++;
            // Update the Warping Path.
            lWarpingPath[lK - 1][0] = i;
            lWarpingPath[lK - 1][1] = j;
        }
        //Log.i("warpingpath", " "+ Arrays.deepToString(lWarpingPath));
        lWarpingPath = reverse(lWarpingPath, lK);
        //Log.i("warpingpath", " "+Arrays.deepToString(lWarpingPath));
        double[] normalized = new double[pTemplate.length];
        int counter = 0;
        int jprev = 0;
        for(int k=0 ;k<lWarpingPath.length;k++){
            i = lWarpingPath[k][0];
            j = lWarpingPath[k][1];
            if(j != jprev){
                counter++;
                normalized[counter] = pSample[i];
            }
            jprev = j;
        }

        // Return the Result. (Calculate the Warping Path and the Distance.)
        return normalized;
    }

    /** Finds the index of the minimum element from the given array. */
    protected final int getMinimumIndex(final double[] pArray) {
        // Declare iteration variables.
        int    lIndex = 0;
        double lValue = pArray[0];
        // Iterate the Array.
        for(int i = 1; i < pArray.length; i++) {
            // .Is the current value smaller?
            final boolean lIsSmaller = pArray[i] < lValue;
            // Update the search metrics.
            lValue = lIsSmaller ? pArray[i] : lValue;
            lIndex = lIsSmaller ?         i : lIndex;
        }
        // Return the Index.
        return lIndex;
    }

    /** Changes the order of the warping path, in increasing order. */
    private int[][] reverse(final int[][] pPath, final int pK) {
        // Allocate the Path.
        final int[][] lPath = new int[pK][2];
        // Iterate.
        for(int i = 0; i < pK; i++) {
            // Iterate.
            for (int j = 0; j < 2; j++) {
                // Update the Path.
                lPath[i][j] = pPath[pK - i - 1][j];
            }
        }
        // Return the Allocated Path.
        return lPath;
    }

    /**
     * Computes a distance between two points.
     */
    protected double getDistanceBetween(double p1, double p2) {
        // Calculate the error
        return abs((p1 - p2));
    }
}

