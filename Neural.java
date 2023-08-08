/**
 * The Neural class represents a simple feedforward neural network.
 * It consists of input, hidden, and output layers along with weight matrices.
 * The network can be trained using backpropagation algorithm.
 */
class Neural extends Object {

    // For debug output:
    GUI MyGUI = null;

    protected int NumInputs;
    protected int NumHidden;
    protected int NumOutputs;

    protected int NumTraining;
    protected int WeightsFlag;
    protected int SpecialFlag;

    public double Inputs[];
    protected double Hidden[];
    public double Outputs[];

    protected double[][] W1;
    protected double[][] W2;

    protected double output_errors[];
    protected double hidden_errors[];

    protected double InputTraining[];
    protected double OutputTraining[];

    // mask of training examples to ignore (true -> ignore):
    public boolean IgnoreTraining[] = null;
    // mask of Input neurons to ignore:
    public boolean IgnoreInput[] = null;
    public NNfile NeuralFile = null;

    /**
     * Creates a new instance of Neural with default values.
     */
    Neural() {
        NumInputs = NumHidden = NumOutputs = 0;
    }

    /**
     * Creates a new instance of Neural and initializes it from a given file.
     *
     * @param file_name The name of the file containing the neural network configuration.
     */
    Neural(String file_name) {
        NeuralFile = new NNfile(file_name);
        NumInputs = NeuralFile.NumInput;
        NumHidden = NeuralFile.NumHidden;
        NumOutputs = NeuralFile.NumOutput;
        NumTraining = NeuralFile.NumTraining;
        WeightsFlag = NeuralFile.WeightFlag;
        SpecialFlag = NeuralFile.SpecialFlag;

        Inputs = new double[NumInputs];
        Hidden = new double[NumHidden];
        Outputs = new double[NumOutputs];
        W1 = new double[NumInputs][NumHidden];
        W2 = new double[NumHidden][NumOutputs];
        // Retrieve the weight values from the NNfile object:
        if (WeightsFlag != 0) {
            for (int i = 0; i < NumInputs; i++) {
                for (int h = 0; h < NumHidden; h++) {
                    W1[i][h] = NeuralFile.GetW1(i, h);
                }
            }
            for (int h = 0; h < NumHidden; h++) {
                for (int o = 0; o < NumOutputs; o++) {
                    W2[h][o] = NeuralFile.GetW2(h, o);
                }
            }
        } else {
            randomizeWeights();
        }

        output_errors = new double[NumOutputs];
        hidden_errors = new double[NumHidden];

        // Get the training cases (if any) from the training file:
        LoadTrainingCases();
    }

    /**
     * Loads the training cases from the training file.
     */
    public void LoadTrainingCases() {
        NumTraining = NeuralFile.NumTraining;
        if (NumTraining > 0) {
            InputTraining = new double[NumTraining * NumInputs];
            OutputTraining = new double[NumTraining * NumOutputs];
        }
        int ic = 0, oc = 0;

        for (int k = 0; k < NumTraining; k++) {
            for (int i = 0; i < NumInputs; i++)
                InputTraining[ic++] = NeuralFile.GetInput(k, i);
            for (int o = 0; o < NumOutputs; o++)
                OutputTraining[oc++] = NeuralFile.GetOutput(k, o);
        }
    }

    /**
     * Creates a new instance of Neural with specified layer sizes.
     *
     * @param i The number of input neurons.
     * @param h The number of hidden neurons.
     * @param o The number of output neurons.
     */
    Neural(int i, int h, int o) {
        System.out.println("In BackProp constructor");
        Inputs = new double[i];
        Hidden = new double[h];
        Outputs = new double[o];
        W1 = new double[i][h];
        W2 = new double[h][o];
        NumInputs = i;
        NumHidden = h;
        NumOutputs = o;
        output_errors = new double[NumOutputs];
        hidden_errors = new double[NumHidden];

        // Randomize weights here:
        randomizeWeights();
    }

    /**
     * Saves the neural network to a file.
     *
     * @param output_file The name of the file to save the network configuration.
     */
    void Save(String output_file) {
        if (NeuralFile == null) {
            System.out.println("Error: no NeuralFile object in Neual::Save");
        } else {
            for (int i = 0; i < NumInputs; i++) {
                for (int h = 0; h < NumHidden; h++) {
                    NeuralFile.SetW1(i, h, W1[i][h]);
                }
            }
            for (int h = 0; h < NumHidden; h++) {
                for (int o = 0; o < NumOutputs; o++) {
                    NeuralFile.SetW2(h, o, W2[h][o]);
                }
            }
            NeuralFile.Save(output_file);
        }
    }

    /**
     * Randomizes the weights of the neural network.
     */
    public void randomizeWeights() {
        // Randomize weights here:
        for (int ii = 0; ii < NumInputs; ii++)
            for (int hh = 0; hh < NumHidden; hh++)
                W1[ii][hh] = 0.1 * Math.random() - 0.05;
        for (int hh = 0; hh < NumHidden; hh++)
            for (int oo = 0; oo < NumOutputs; oo++)
                W2[hh][oo] = 0.1 * Math.random() - 0.05;
    }

    /**
     * Performs a forward pass through the neural network.
     */
    public void ForwardPass() {
        int i, h, o;
        for (h = 0; h < NumHidden; h++) {
            Hidden[h] = 0.0;
        }
        for (i = 0; i < NumInputs; i++) {
            for (h = 0; h < NumHidden; h++) {
                Hidden[h] += Inputs[i] * W1[i][h];
            }
        }
        for (o = 0; o < NumOutputs; o++)
            Outputs[o] = 0.0;
        for (h = 0; h < NumHidden; h++) {
            for (o = 0; o < NumOutputs; o++) {
                Outputs[o] += Sigmoid(Hidden[h]) * W2[h][o];
            }
        }
        for (o = 0; o < NumOutputs; o++)
            Outputs[o] = Sigmoid(Outputs[o]);
    }

    /**
     * Trains the neural network using backpropagation.
     *
     * @return The error after training.
     */
    public double Train() {
        return Train(InputTraining, OutputTraining, NumTraining);
    }

    /**
     * Trains the neural network using backpropagation on custom training data.
     *
     * @param ins       The input training data.
     * @param outs      The output training data.
     * @param num_cases The number of training cases.
     * @return The error after training.
     */
    public double Train(double ins[],
                        double outs[],
                        int num_cases) {
        int i, h, o;
        int in_count = 0, out_count = 0;
        double error = 0.0;
        for (int example = 0; example < num_cases; example++) {
            if (IgnoreTraining != null)
                if (IgnoreTraining[example]) continue; // skip this case
            // zero out error arrays:
            for (h = 0; h < NumHidden; h++)
                hidden_errors[h] = 0.0;
            for (o = 0; o < NumOutputs; o++)
                output_errors[o] = 0.0;
            // copy the input values:
            for (i = 0; i < NumInputs; i++) {
                Inputs[i] = ins[in_count++];
            }

            if (IgnoreInput != null) {
                for (int ii = 0; ii < NumInputs; ii++) {
                    if (IgnoreInput[ii]) {
                        for (int hh = 0; hh < NumHidden; hh++) {
                            W1[ii][hh] = 0;
                        }
                    }
                }
            }

            // perform a forward pass through the network:
            ForwardPass();

            if (MyGUI != null) MyGUI.repaint();
            for (o = 0; o < NumOutputs; o++) {
                output_errors[o] = (outs[out_count++] - Outputs[o]) * SigmoidP(Outputs[o]);
            }
            for (h = 0; h < NumHidden; h++) {
                hidden_errors[h] = 0.0;
                for (o = 0; o < NumOutputs; o++) {
                    hidden_errors[h] += output_errors[o] * W2[h][o];
                }
            }
            for (h = 0; h < NumHidden; h++) {
                hidden_errors[h] = hidden_errors[h] * SigmoidP(Hidden[h]);
            }
            // update the hidden to output weights:
            for (o = 0; o < NumOutputs; o++) {
                for (h = 0; h < NumHidden; h++) {
                    W2[h][o] +=
                            0.5 * output_errors[o] * Hidden[h];
                }
            }
            // update the input to hidden weights:
            for (h = 0; h < NumHidden; h++) {
                for (i = 0; i < NumInputs; i++) {
                    W1[i][h] +=
                            0.5 * hidden_errors[h] * Inputs[i];
                }
            }
            for (o = 0; o < NumOutputs; o++)
                error += Math.abs(output_errors[o]);
        }
        return error;
    }

    /**
     * Applies the sigmoid activation function to the given value.
     *
     * @param x The input value.
     * @return The result after applying the sigmoid function.
     */
    protected double Sigmoid(double x) {
        return (1.0 / (1.0 + Math.exp(-x))) - 0.5;
    }

    /**
     * Computes the derivative of the sigmoid function for a given value.
     *
     * @param x The input value.
     * @return The derivative of the sigmoid function at the given value.
     */
    protected double SigmoidP(double x) {
        double z = Sigmoid(x) + 0.5;
        return z * (1.0 - z);
    }
}
