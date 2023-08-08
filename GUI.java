import java.awt.*;

/**
 * The GUI class represents a basic graphical user interface window.
 * It provides various flags to enable/disable standard GUI components
 * and utilities for handling input and output.
 */
public class GUI extends Frame {

    // Flags to disable standard GUI components:
    public boolean NoInput       = false;
    public boolean NoOutput      = false;
    public boolean NoGraphics    = false;
    public boolean NoRunButton   = false;
    public boolean NoResetButton = false;
    public int BigText           = 0;

    public String RunLabel   = "Run";
    public String ResetLabel = "Reset";

    public String InitialInput = "";

    // Double buffering related variables
    Graphics background; // used for double buffering
    Image im;
    int width = 640;
    int height = 480;

    // GUI components
    protected Panel panel;
    TextField inputText;
    TextArea outputText;
    GUICanvas canvas;

    Color colors[];
    int NumColors = 16;

    /**
     * Returns applet information.
     *
     * @return A string representing the applet information.
     */
    public String getAppletInfo() {
        return "GUI classes for SE428";
    }

    /**
     * Initializes the GUI window and its components.
     */
    public void init() {
        // Get the dimensions of the window
        width = this.getWidth();
        height = this.getHeight();

        setSize(width, height);

        panel = new Panel();
        panel.setLayout(new FlowLayout());
        panel.setSize(width - 10, height / 2);

        if (!NoRunButton) panel.add(new Button(RunLabel));
        if (!NoResetButton) panel.add(new Button(ResetLabel));

        if (!NoInput) {
            inputText = new TextField(InitialInput, 30);
            panel.add(new Label("Input:"));
            panel.add(inputText);
        }

        if (BigText == 0) {
            if (!NoOutput) {
                outputText = new TextArea("", 5, 35);
                panel.add(outputText);
            }
            add(panel);
        } else {
            add(panel);
            if (!NoOutput) {
                if (BigText == 1) {
                    outputText = new TextArea("", 9, 45);
                } else if (BigText == 2) {
                    outputText = new TextArea("", 15, 60);
                } else {
                    outputText = new TextArea("", 20, 70);
                }
                add(outputText);
            }
        }

        if (!NoGraphics) {
            canvas = new GUICanvas();
            canvas.parent = this;

            int c_width = width - 20;
            int c_height = (3 * height) / 2;
            canvas.setSize(c_width, c_height);
            add(canvas);

            panel.setBackground(Color.darkGray);
            setBackground(Color.lightGray);

            colors = new Color[NumColors];
            for (int c = 0; c < NumColors; c++) {
                float blue = 1.0f - (float) c / (float) NumColors;
                float red = (float) c / (float) NumColors;
                colors[c] = new Color(red, 0.0f, blue);
            }

            canvas.init();
        }
    }

    /**
     * Reduces applet "flicker" by defining the update method.
     *
     * @param g The graphics context.
     */
    public void update(Graphics g) {
        if (!NoGraphics) paint(g);
    }

    /**
     * Handles painting on the GUI window.
     *
     * @param g The graphics context.
     */
    public void paint(Graphics g) {
        // This method can be overridden in subclasses to provide custom painting logic.
    }

    /**
     * Paints a grid cell on the canvas with a specific color based on a value.
     *
     * @param g      The graphics context.
     * @param x      The x-coordinate of the cell.
     * @param y      The y-coordinate of the cell.
     * @param size   The size of the cell.
     * @param value  The value to determine the cell color.
     * @param min    The minimum value for scaling.
     * @param max    The maximum value for scaling.
     */
    protected void paintGridCell(Graphics g, int x, int y, int size,
                                 double value, double min, double max) {
        int index = (int) (((value - min) * (double) NumColors) / (max - min));
        if (index < 0) index = 0;
        else if (index > (NumColors - 1)) index = NumColors - 1;
        g.setColor(colors[index]);
        g.fillRect(x, y, size, size);
        g.setColor(Color.black);
        g.drawRect(x, y, size, size);
    }

    /**
     * Paints on the double buffer for flicker-free rendering.
     *
     * @param g The graphics context.
     */
    public void paintToDoubleBuffer(Graphics g) {
        System.out.println("entered GUI::paintToDoubleBuffer\n");
        paintGridCell(g, 20, 20, 30, 0.5f, 0.0f, 1.0f);
        System.out.println("leaving GUI::paintToDoubleBuffer\n");
    }

    /**
     * Repaints the GUI components.
     */
    public void repaint() {
        if (!NoGraphics) {
            canvas.repaint();
            super.repaint();
        }
    }

    /**
     * Subclasses should redefine these three functions:
     * Called when the "Run" button is pressed.
     */
    public void doRunButton() {
        System.out.print("Default GUI::doDoRunButton()");
    }

    /**
     * Subclasses should redefine these three functions:
     * Called when the "Reset" button is pressed.
     */
    public void doResetButton() {
        System.out.print("Default GUI::doDoResetButton()");
    }

    /**
     * Subclasses should redefine these three functions:
     * Called when the mouse is clicked on the canvas.
     *
     * @param x The x-coordinate of the mouse click.
     * @param y The y-coordinate of the mouse click.
     */
    public void doMouseDown(int x, int y) {
        System.out.print("Default GUI::doMouseDown(");
        System.out.print(x);
        System.out.print(", ");
        System.out.print(y);
        System.out.println("\n");
    }

    // Utility to get the input text field:

    /**
     * Gets the content of the input text field.
     *
     * @return The content of the input text field.
     */
    public String GetInputText() {
        String s = inputText.getText();
        //P("input text:" + s + "\n");
        return s;
    }

    // Utility to set the input text field:

    /**
     * Sets the content of the input text field.
     *
     * @param s The string to set in the input text field.
     */
    public void SetInputText(String s) {
        inputText.setText(s);
    }

    // Utilities for output text field:

    /**
     * Clears the output text area.
     */
    public void ClearOutput() {
        outputText.replaceRange("", 0, 32000);
    }

    /**
     * Appends a string to the output text area.
     *
     * @param s The string to append to the output text area.
     */
    public void P(String s) {
        outputText.append(s);
    }

    /**
     * Appends an integer to the output text area.
     *
     * @param i The integer to append to the output text area.
     */
    public void P(int i) {
        StringBuffer sb = new StringBuffer();
        sb.append(i);
        String s2 = new String(sb);
        outputText.append(s2);
    }

    /**
     * Appends a double to the output text area.
     *
     * @param x The double to append to the output text area.
     */
    public void P(double x) {
        StringBuffer sb = new StringBuffer();
        sb.append(x);
        String s2 = new String(sb);
        outputText.append(s2);
    }

    /**
     * Handles GUI action events.
     *
     * @param evt The event that triggered the action.
     * @param obj The object associated with the event.
     * @return True if the action was handled, false otherwise.
     */
    public boolean action(Event evt, Object obj) {
        System.out.println(evt.id);
        if (evt.target instanceof Button) {
            String label = (String) obj;
            if (label.equals(RunLabel)) {
                System.out.println("Run button pressed\n");
                doRunButton();
                repaint();
                if (canvas != null) canvas.repaint();
                return true;
            }
            if (label.equals(ResetLabel)) {
                System.out.println("Reset button pressed\n");
                doResetButton();
                repaint();
                if (canvas != null) canvas.repaint();
                return true;
            }
        }
        if (evt.id == 1001) {
            if (!NoInput) {
                // User hit a carriage return: execute doRunButton
                doRunButton();
                return true;
            }
        }
        return false;
    }

    /**
     * Redraws the canvas.
     * Called when the canvas needs to be updated.
     */
    private void drawOnCanvas() {
        if (canvas != null) canvas.repaint();
    }

    /**
     * The GUICanvas class is a utility class used internally by the GUI class
     * to handle graphics rendering and mouse events for the canvas area.
     */
    class GUICanvas extends Canvas {
        Graphics background; // used for double buffering
        Image im;
        public GUI parent;

        /**
         * Initializes the GUICanvas for double buffering.
         */
        public void init() {
            try {
                Dimension d = getSize();
                im = createImage(d.width, d.height);
                background = im.getGraphics();
            } catch (Exception ex) {
                background = null;
            }
        }

        /**
         * Handles painting on the canvas using double buffering to reduce flickering.
         *
         * @param g The graphics context to paint on.
         */
        public void paint(Graphics g) {
            if (background == null) { // Can not use double buffering
                parent.paintToDoubleBuffer(g);
                System.out.println("No double buffer available");
            } else {
                // draw into the copy the background (double buffering)
                parent.paintToDoubleBuffer(background);
                g.drawImage(im, 0, 0, this);
            }
        }

        /**
         * Handles mouse drag events on the canvas.
         *
         * @param evt The event object.
         * @param x   The x-coordinate of the mouse drag event.
         * @param y   The y-coordinate of the mouse drag event.
         * @return False (indicating the event was not consumed).
         */
        public boolean mouseDrag(Event evt, int x, int y) {
            // Call the containing app's mouse handling function:
            parent.doMouseDown(x, y);
            return false;
        }
    }
}
