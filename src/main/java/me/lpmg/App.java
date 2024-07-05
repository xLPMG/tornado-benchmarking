package me.lpmg;

import java.util.Random;

import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

public class App 
{
    public static void main( String[] args )
    {
        // read input arguments
        int size = 4096;
        int iterations = 10;
        if (args.length > 0){
            size = Integer.parseInt(args[0]);
        }
        if (args.length > 1){
            iterations = Integer.parseInt(args[1]);
        }
        double floatingPointOperations = 2 * Math.pow(size, 3) * iterations;
        System.out.println("Running GEMM benchmark with size " + size + " for " + iterations + " iterations");

        Compute compute = new Compute();
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        
        // set up matrices
        Matrix2DFloat A = new Matrix2DFloat(size, size);
        A.fill(rand.nextFloat());
        Matrix2DFloat B = new Matrix2DFloat(size, size);
        B.fill(rand.nextFloat());
        Matrix2DFloat C = new Matrix2DFloat(size, size);
        C.fill(rand.nextFloat());

        // sequential execution
        double startTimeSeq = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++){
            compute.run(A, B, C, size, false);
        }
        double timeSeq = System.currentTimeMillis() - startTimeSeq;
        double seqOperationsPerSecond = floatingPointOperations * 1000 / timeSeq;
        double seqGflops = (double)seqOperationsPerSecond / 1000000000;
        double seqTflops = seqGflops / 1000;
        System.out.println("Sequential execution time: " + timeSeq + "ms");
        System.out.println("Sequential GFLOPS: " + seqGflops);
        System.out.println("Sequential TFLOPS: " + seqTflops);

        System.out.println("====================================");

        // parallel execution
        double startTimePar = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++){
            compute.run(A, B, C, size, true);
        }
        double timePar = System.currentTimeMillis() - startTimePar;
        double parOperationsPerSecond = floatingPointOperations * 1000 / timePar;
        double parGflops = (double)parOperationsPerSecond / 1000000000;
        double parTflops = parGflops / 1000;
        System.out.println("Parallel execution time: " + timePar + "ms");
        System.out.println("Parallel GFLOPS: " + parGflops);
        System.out.println("Parallel TFLOPS: " + parTflops);

        System.out.println("Speedup: " + (float)(timeSeq / timePar));
    }
}
