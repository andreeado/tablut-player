package it.unibo.ai.didattica.competition.tablut.heuristic;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.activations.Activation;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class H5NeuralNetworkPredictor {
    private MultiLayerNetwork model;

    public H5NeuralNetworkPredictor(String modelPath) throws IOException {
        // Load pre-trained model
        model = MultiLayerNetwork.load(new File(modelPath), true);
    }

    public float predict(int diffWhiteBlack, int kingEscape, int dangerMetric, int escape, int freePath) {
        // Convert parameters to a float array
        float[] inputArray = new float[] {
            diffWhiteBlack,
            kingEscape,
            dangerMetric,
            escape,
            freePath
        };

        // Create INDArray from the float array
        INDArray input = Nd4j.create(inputArray, new int[]{1, inputArray.length});

        INDArray output = model.output(input);
        float[] floatArray = output.toFloatVector();

        return floatArray[0];
    }
}
