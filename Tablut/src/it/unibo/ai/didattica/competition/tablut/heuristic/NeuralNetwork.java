package it.unibo.ai.didattica.competition.tablut.heuristic;

public class NeuralNetwork {
    private static NeuralNetwork instance;
    private final float[][][] weights;
    private final float[][] biases;

    private NeuralNetwork() {
        // Initialize the weights and biases arrays
        weights = new float[3][][];
        biases = new float[3][];
    
        // Layer 0: 6x10
        weights[0] = new float[][] {
            { -0.16164246f,  0.57262623f,  0.02616116f,  0.59656376f, -0.16636768f, -0.29203764f,  1.0323339f, -0.29066345f,  0.17721607f, -0.50735414f},
            { -0.3048994f, -0.6714727f, -0.25842f,  0.7419988f,  0.15045094f, -0.06974003f,  0.05317762f,  0.11607911f, -0.15804827f,  0.04674517f},
            { -0.05818384f, -1.0113971f,  0.25714815f,  0.51954585f,  1.0969472f,  1.2241161f, -1.5972052f, -1.0155716f,  0.60191786f,  0.06745017f},
            {  0.40482363f,  0.37265906f,  0.43845338f, -0.16746055f,  0.18426858f, -0.8862309f,  0.03879194f,  0.05864451f, -0.31069812f,  0.2565982f},
            {  0.5380642f,  0.6664832f, -0.9544701f,  0.8277755f, -0.34981596f,  0.42234543f, -1.6738309f, -1.7601453f,  0.87940717f, -0.883382f},
            { -0.6016915f,  0.07263303f, -0.2774198f,  0.30502802f, -0.21358612f,  0.24961215f, -0.38690826f, -0.01379055f,  0.2000634f,  0.19693357f}
        };
    
        biases[0] = new float[] {
            0.51233137f, -0.48740196f, -0.62602127f, -0.19870257f, -0.61581624f, 
            0.7904913f, -0.50772274f, -0.96529436f, 2.3598757f, -0.41649786f
        };
    
        // Layer 1: 10x6
        weights[1] = new float[][] {
            { -0.09745111f, -0.456237f,  0.10058206f, -0.15032761f,  0.06833268f,  0.19518209f},
            {  0.12422194f, -0.6476744f,  0.6569642f, -0.5386451f,  0.35254556f,  0.09717131f},
            {  0.11769965f, -0.9913869f, -0.40216073f,  0.27599806f,  0.24943227f, -0.14176671f},
            { -0.9970741f, -0.1183638f, -0.2664684f, -0.1145225f, -0.81475985f,  0.8026256f},
            { -0.09020171f, -0.2606317f, -0.93026674f,  0.57669926f, -0.2135552f,  0.2809964f},
            { -1.6317139f, -0.71481013f, -0.27395746f,  0.2728962f, -0.01611833f,  0.7370719f},
            { -1.5374938f,  0.52819353f,  0.7668914f,  0.1996556f, -3.8104165f, -0.64552695f},
            {  0.2140273f, -0.51557493f,  0.7111083f,  0.45918238f, -1.5391128f, -0.51103103f},
            {  1.0609492f,  1.2091458f,  1.0999612f, -0.82331556f, -4.7553487f, -2.6479921f},
            {  0.36825702f,  0.6788201f, -0.18328166f,  0.2382693f, -0.2732641f, -0.12726575f}
        };
    
        biases[1] = new float[] {
            -0.06846511f, 0.6802835f, -0.5854377f, -0.93680847f, 0.8891448f, 0.6853511f
        };
    
        // Layer 2: 6x1
        weights[2] = new float[][] {
            { -0.27705517f },
            { -1.2396933f },
            {  0.5438475f },
            { -0.45381936f },
            { -0.41410965f },
            {  0.31315938f }
        };
    
        biases[2] = new float[] { 0.60303503f };
    }

    public static NeuralNetwork getInstance() {
        if (instance == null) {
            instance = new NeuralNetwork();
        }
        return instance;
    }

    private float relu(float x) {
        return Math.max(0, x);
    }

    public float predict(float[] input) {
        if (input.length != 6) {
            throw new IllegalArgumentException("Input must have 6 features");
        }

        // First dense layer with ReLU
        float[] layer1 = new float[10];
        for (int i = 0; i < 10; i++) {
            float sum = biases[0][i];
            for (int j = 0; j < 6; j++) {
                sum += input[j] * weights[0][j][i];
            }
            layer1[i] = relu(sum);
        }

        // Second dense layer with ReLU
        float[] layer2 = new float[6];
        for (int i = 0; i < 6; i++) {
            float sum = biases[1][i];
            for (int j = 0; j < 10; j++) {
                sum += layer1[j] * weights[1][j][i];
            }
            layer2[i] = relu(sum);
        }

        // Output layer (no activation)
        float output = biases[2][0];
        for (int i = 0; i < 6; i++) {
            output += layer2[i] * weights[2][i][0];
        }

        return output;
    }
}

