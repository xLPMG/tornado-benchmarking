package me.lpmg;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.TornadoExecutionResult;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.exceptions.TornadoExecutionPlanException;
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

public class Compute {
    private static void mxmLoopParallel(Matrix2DFloat A, Matrix2DFloat B, Matrix2DFloat C, final int size) {
        for (@Parallel int i = 0; i < size; i++) {
            for (@Parallel int j = 0; j < size; j++) {
                float sum = 0.0f;
                for (int k = 0; k < size; k++) {
                    sum += A.get(i, k) * B.get(k, j);
                }
                C.set(i, j, sum);
            }
        }
    }
    
    public void mxmLoopSequential(Matrix2DFloat A, Matrix2DFloat B, Matrix2DFloat C, final int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                float sum = 0.0f;
                for (int k = 0; k < size; k++) {
                    sum += A.get(i, k) * B.get(k, j);
                }
                C.set(i, j, sum);
            }
        }
    }

    public void run(Matrix2DFloat A, Matrix2DFloat B, Matrix2DFloat C, final int size, boolean parallelize) {

        if(parallelize){
            // Create a task-graph with multiple tasks. Each task points to an exising Java method
            // that can be accelerated on a GPU/FPGA
            TaskGraph taskGraph = new TaskGraph("myCompute")
            .transferToDevice(DataTransferMode.FIRST_EXECUTION, A, B)  // Transfer data from host to device only in the first execution
            .task("mxm", Compute::mxmLoopParallel, A, B, C, size)     // Each task points to an existing Java method
            .transferToHost(DataTransferMode.EVERY_EXECUTION, C);     // Transfer data from device to host

        // Create an immutable task-graph
        ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();

        // Create an execution plan from an immutable task-graph
        try (TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph)) {

            // Run the execution plan on the default device
            TornadoExecutionResult executionResult = executionPlan.execute();

        } catch (TornadoExecutionPlanException e) {
            // handle exception
            // ...
        }
        }else{
            mxmLoopSequential(A, B, C, size);
        }
    }
}
