package be.magdyabdel.wandz;

import static java.lang.Math.abs;

public final class DTW {
    public double computeDTWError(final float[][] recognitionSet, final float[][] trainingSet) { //input all the samples in the rows for 3 columns(x,y,z)
        // Declare the amount of iterations(amount of rows and columns of warping table)
        final int recognitionLength = recognitionSet.length; //amount of rows of sample set
        final int trainingLength = trainingSet.length;       // amount of rows of trainingset
        double totalError = 0;                                    //initialise total error

        // Ensure the recognition samples are valid.
        if (recognitionLength == 0 || trainingLength == 0) {
            // Assert a bad result, return -1
            return -1;
        }

        for (int k = 0; k < recognitionSet[0].length; k++) {
            // Declare the warping/cost matrix
            final double[][] costMatrix = new double[recognitionLength][trainingLength];
            int i, j;
            // Initialize the matrix (by filling in the first row and column)
            costMatrix[0][0] = this.getDistanceBetween(recognitionSet[0][k], trainingSet[0][k]);

            for (i = 1; i < recognitionLength; i++) { //first column
                costMatrix[i][0] = this.getDistanceBetween(recognitionSet[i][k], trainingSet[0][k]) + costMatrix[i - 1][0];
            }

            for (j = 1; j < trainingLength; j++) { //first row
                costMatrix[0][j] = this.getDistanceBetween(recognitionSet[0][k], trainingSet[j][k]) + costMatrix[0][j - 1];
            }

            //calculate the warping/cost matrix
            for (i = 1; i < recognitionLength; i++) {
                for (j = 1; j < trainingLength; j++) {
                    // Accumulate the path.
                    costMatrix[i][j] = (Math.min(Math.min(costMatrix[i - 1][j], costMatrix[i - 1][j - 1]), costMatrix[i][j - 1])) + this.getDistanceBetween(recognitionSet[i][k], trainingSet[j][k]);
                }
            }
            totalError += costMatrix[recognitionLength - 1][trainingLength - 1];
        }
        // Return the distance
        return totalError / ((double) recognitionLength + (double) trainingLength); //the distance is the value in the top right corner, divide by total amount of distances
    }

    /**
     * Computes a distance between two points.
     */
    protected double getDistanceBetween(double p1, double p2) {
        // Calculate the error
        return abs((p1 - p2));
    }
}

