package me.lpmg;

import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

public class App 
{
    public static void main( String[] args )
    {
        int size = 4096;
        int iterations = 10;

        if (args.length > 0){
            size = Integer.parseInt(args[0]);
        }
        if (args.length > 1){
            iterations = Integer.parseInt(args[1]);
        }

        System.out.println("Running GEMM benchmark with size " + size + " for " + iterations + " iterations");

        Compute compute = new Compute();

        Matrix2DFloat A = new Matrix2DFloat(size, size);
        A.fill(1.234f);
        Matrix2DFloat B = new Matrix2DFloat(size, size);
        B.fill(5.623f);
        Matrix2DFloat C = new Matrix2DFloat(size, size);
        C.fill(36.322f);

        long startTimeSeq = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++){
            compute.run(A, B, C, size, false);
        }
        long endTimeSeq = System.currentTimeMillis();
        System.out.println("Sequential execution time: " + (endTimeSeq - startTimeSeq) + "ms");

        long startTimePar = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++){
            compute.run(A, B, C, size, true);
        }
        long endTimePar = System.currentTimeMillis();
        System.out.println("Parallel execution time: " + (endTimePar - startTimePar) + "ms");

        System.out.println("Speedup: " + (float)(endTimeSeq - startTimeSeq) / (endTimePar - startTimePar));
    }
}
