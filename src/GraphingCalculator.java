import javax.swing.*;
import java.awt.*;

public class GraphingCalculator extends JFrame {
    private GraphPanel graphPanel;
    private JTextField functionInput;

    public GraphingCalculator() {
        setTitle("Graphing Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        graphPanel = new GraphPanel();
        functionInput = new JTextField("sin(x)", 30);

        functionInput.addActionListener(e -> {
            String input = functionInput.getText();
            boolean ok = graphPanel.setFunction(input);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Invalid function.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("f(x) ="));
        topPanel.add(functionInput);

        add(topPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GraphingCalculator().setVisible(true);
        });
    }
}
