package it.unibo.ai.didattica.competition.tablut.heuristic;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.activations.Activation;

import java.io.File;
import java.io.IOException;

public class H5NeuralNetworkPredictor {
    private MultiLayerNetwork model;

    public H5NeuralNetworkPredictor(String modelPath) throws IOException {
        // Load pre-trained model
        model = MultiLayerNetwork.load(new File(modelPath), true);
    }

    public INDArray predict(INDArray input) {
        // Perform prediction
        return model.output(input);
    }
}
