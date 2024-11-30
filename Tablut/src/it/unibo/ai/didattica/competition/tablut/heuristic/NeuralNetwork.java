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
            { 0.64171714f, -0.1909322f, 0.32189336f, 0.47207493f, 0.34443066f, -0.30555147f, -0.004308763f, 1.0001525f, 0.23699403f, 0.011549874f },
            { 0.4455863f, -0.35998484f, 0.4355657f, -0.34949365f, -0.21014014f, -0.5133887f, 0.2758164f, 0.18068908f, 0.5937308f, -0.058893908f },
            { -0.043627504f, 0.83090407f, -1.0181456f, -0.19594824f, -0.061668925f, -0.176039f, -0.24276158f, -0.53110033f, -0.5362656f, -0.345581f },
            { 0.25310093f, 0.38382822f, -0.43185058f, -0.45229295f, 0.66291004f, 0.39282164f, 0.4009479f, -0.03445511f, 0.3642783f, -0.018356906f },
            { -1.7889504f, -1.421843f, -0.6749848f, -0.10572189f, -0.2454157f, -1.3377268f, -1.4838161f, -1.5940202f, -0.056644563f, -0.07702712f }
        };
    
        biases[0] = new float[] {
            -0.32289395f, -0.5474645f, -1.0320884f, 0f, -0.36806306f, -0.024882505f, -0.28426358f, 0.95972353f, 0.03655579f, -0.05250601f
        };
    
        // Layer 1: 10x6
        weights[1] = new float[][] {
            { -0.6135103f, 0.4634879f, -0.14859146f, -0.01576257f, -0.5858554f, -0.19411561f },
            { 0.09530213f, 0.104403645f, -0.08981874f, -0.27110282f, 0.1473418f, 0.23688531f },
            { -1.2689587f, 0.46942195f, -0.5817584f, 1.2001754f, -1.0523173f, -0.2252315f },
            { -0.10643017f, -0.5566604f, 0.035916686f, 0.4267835f, -0.041685343f, -0.14273757f },
            { -0.08444921f, -0.90875494f, -0.5675781f, 0.447053f, -0.85256094f, 0.22286339f },
            { 0.23229975f, 0.44265875f, -0.14109726f, 0.1542675f, -0.23096354f, -1.0570972f },
            { 0.56880254f, -0.87016875f, 0.25460237f, -0.5762679f, -0.2022578f, 0.12628809f },
            { -0.10939837f, -0.3371707f, -0.42873624f, 0.39388052f, 1.0007455f, 0.7147157f },
            { 0.5217688f, 0.14492825f, -0.6082505f, 0.4864371f, 0.40070605f, -1.1666934f },
            { 0.18655469f, 0.2529953f, 0.5353268f, -0.107999854f, -0.2922821f, 0.21514606f }
        };
    
        biases[1] = new float[] {
            -0.4144213f, -0.16492395f, -0.09378254f, 0.67437464f, 0.7190344f, -0.064401716f
        };
    
        // Layer 2: 6x1
        weights[2] = new float[][] {
            { -0.4678362f },
            { -3.7094803f },
            { -0.7416309f },
            { 0.34954995f },
            { -2.2898355f },
            { -2.2395375f }
        };
    
        biases[2] = new float[] { 0.8193774f };
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

