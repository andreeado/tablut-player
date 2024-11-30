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
            { 8.0962449e-01f, -4.0039492e-01f, 1.2057384e-02f, -4.9156162e-01f, 5.7938015e-01f, 1.5437837e-01f, -1.6143520e-01f, 2.6592219e-01f, -5.6366944e-01f, -8.0154532e-01f},
            { 1.3383806e-01f, -4.4125029e-01f, 5.8108735e-01f, 3.8119578e-01f, -5.1743805e-01f, -4.1578516e-01f, 1.5070812e-01f, 8.0507830e-02f, 9.7547755e-02f, -1.7248407e-01f},
            { 5.2148122e-01f, 5.8467180e-01f, 1.5226402e+00f, -1.0567571e+00f, 8.8233370e-01f, 1.1191323e-01f, 1.9061421e-01f, 4.6384028e-01f, 6.3302263e-04f, 1.9178096e+00f},
            { 5.4596256e-02f, -8.2094193e-02f, 6.0852345e-02f, -7.3404461e-01f, 1.3481794e-01f, 5.0548011e-01f, -3.5108560e-01f, -3.3186957e-01f, 2.5895661e-01f, -5.1506436e-01f},
            {-2.2131884e-01f, 1.1092396e+00f, -2.0516191e-01f, -3.9852783e-01f, 1.5524768e-02f, -2.8932795e-02f, 1.2850258e+00f, -7.4083918e-01f, -6.6212070e-01f, 8.2491495e-02f},
            {-5.0944686e-02f, -5.7986987e-01f, 8.1039011e-02f, 3.1066847e-01f, 5.4436880e-01f, -3.0136091e-01f, -5.3066182e-01f, -6.7731142e-03f, -2.4451044e-01f, 5.3806084e-01f}
        };    
        
        biases[0] = new float[] {
            0.27146974f, 0.42544568f, -0.23265995f, -0.1147929f, 0.6757284f, 
            -0.45038992f, 1.079442f, 1.2990657f, -0.5243513f, -0.1228927f
        };
    
        // Layer 1: 10x6
        weights[1] = new float[][] {
            { 0.048554532f, -0.5686938f, 0.7634235f, 0.5138414f, 0.19724335f, 0.38547185f },
            { -0.5086201f, -0.011233968f, 0.36165774f, -0.4461208f, -0.7508375f, -1.0384184f },
            { -0.24711286f, 0.031350832f, -1.0908239f, -0.54494286f, -0.11695876f, 0.060445912f },
            { -1.1494062f, -1.2271309f, -0.2728961f, 0.016661406f, 0.6485796f, 0.43645573f },
            { -0.03976921f, -1.017185f, -0.32977292f, 0.954268f, 0.5775741f, -0.044449065f },
            { -0.40820956f, -1.1740645f, 0.19186673f, -0.7136024f, 0.17986055f, 0.40227738f },
            { 0.6176192f, 0.07431358f, 0.6049076f, -0.457296f, 0.65430397f, 0.048444934f },
            { 1.506629f, 0.68388504f, 0.025975697f, 0.31361392f, 0.2466716f, 0.06224588f },
            { -0.17895761f, -1.054883f, 0.07047004f, 0.24826075f, 0.75568336f, -0.2680223f },
            { 0.49846742f, 0.81953216f, 0.26940018f, 0.577645f, 0.84686863f, 1.3559618f }
        };
    
        biases[1] = new float[] {
            1.1056076f, 0.023025906f, 0.2957304f, -1.0867902f, -0.81980383f, -0.32932636f
        };
    
        // Layer 2: 6x1
        weights[2] = new float[][] {
            { -2.1197233f },
            { 1.8240418f },
            { 0.5626965f },
            { 1.4376823f },
            { -0.64286864f },
            { 0.6538068f }
        };
    
        biases[2] = new float[] { 1.0497036f };
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

