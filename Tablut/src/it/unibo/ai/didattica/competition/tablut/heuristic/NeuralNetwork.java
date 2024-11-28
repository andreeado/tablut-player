package it.unibo.ai.didattica.competition.tablut.heuristic;

public class NeuralNetwork {
    private static NeuralNetwork instance;
    private final float[][][] weights;
    private final float[][] biases;

    private NeuralNetwork() {
        // Initialize the weights and biases arrays
        weights = new float[3][][];
        biases = new float[3][];

        // Layer 0: 5x10
        weights[0] = new float[][] {
                { 0.121716015f, -0.140841f, -0.4203056f, -0.67573076f, 0.4201175f, 0.95176697f, -0.63991827f, -0.3461202f, 0.30928978f, -0.59151924f },
                { -0.5081034f, 0.5457888f, -0.05033888f, 0.010307443f, -0.21677175f, -0.2612076f, 0.08599998f, -0.021878565f, -0.37386358f, -0.55841804f },
                { -0.4647344f, -0.6720337f, 0.11815511f, 1.1335202f, 0.08796884f, -0.2559424f, -1.08893f, 0.7913795f, 0.14238636f, 0.18724936f },
                { 0.62081575f, -0.039089277f, -0.20228817f, -0.3246601f, -0.3683118f, 0.104113065f, 0.16101943f, 0.3200655f, 0.22747687f, -0.5611371f },
                { -0.21064363f, 0.8093145f, 1.1758137f, -1.1797007f, -0.12129176f, -0.18728761f, -0.49015346f, 0.18591121f, 0.2173247f, -0.96929187f }
        };

        biases[0] = new float[] {
                -0.12641363f, 0.34861398f, 1.3480195f, -0.5051724f, 2.2683744f, -0.20022091f,
                -0.6540433f, -0.4364572f, -0.10735238f, 0.13798437f
        };

        // Layer 1: 10x6
        weights[1] = new float[][] {
                { 0.33880067f, -0.4628753f, 0.6549317f, 0.55849415f, -0.35635948f, 0.56221807f },
                { 0.19678241f, 0.36264414f, -0.31007823f, -0.55079484f, 0.7741644f, -0.007689421f },
                { -0.6740485f, 0.27638808f, 0.15112214f, -0.69643986f, -0.70671433f, -1.7876294f },
                { 1.2837101f, 0.016086789f, -0.8982208f, -0.3985375f, 0.34014717f, -2.5764847f },
                { 2.1380424f, 1.0570813f, -0.7027527f, -2.2425604f, -0.91323584f, -0.66510946f },
                { -0.05253886f, -0.6455392f, -0.10265764f, -0.0062690293f, 0.10463209f, 0.6924416f },
                { 0.19791603f, 0.42742166f, 0.5162141f, 0.3471545f, -0.7380782f, -0.5679925f },
                { -0.11075563f, 0.28493202f, 0.58754665f, 0.4947113f, 0.2015627f, -0.267969f },
                { 0.17824224f, -1.0137599f, 0.29282686f, 0.26391318f, 0.3453943f, 0.19203012f },
                { -0.6224529f, -0.99373657f, 1.2776288f, -0.11980555f, 0.3635408f, -0.1753267f }
        };

        biases[1] = new float[] {
                -0.5170585f, 0.015241026f, 0.34003663f, -0.45365664f, 0.16334556f, -0.13374232f
        };

        // Layer 2: 6x1
        weights[2] = new float[][] {
                {-0.24752471f},
                {-0.487479f},
                {0.44469032f},
                {-0.7467882f},
                {0.34486654f},
                {0.5244637f}
        };

        biases[2] = new float[] {-0.026139447f};
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
        if (input.length != 5) {
            throw new IllegalArgumentException("Input must have 5 features");
        }

        // First dense layer with ReLU
        float[] layer1 = new float[10];
        for (int i = 0; i < 10; i++) {
            float sum = biases[0][i];
            for (int j = 0; j < 5; j++) {
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

