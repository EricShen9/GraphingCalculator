import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class GraphPanel extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {
    private Expression expression;
    private double scaleX = 40.0;
    private double scaleY = 40.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private Point lastMousePosition;

    // Optional manual tick overrides
    private double manualTickX = -1;
    private double manualTickY = -1;

    public GraphPanel() {
        setBackground(Color.WHITE);
        setFunction("sin(x)");
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public boolean setFunction(String input) {
        try {
            this.expression = new ExpressionBuilder(input).variable("x").build();
            repaint();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setManualTicks(double tickX, double tickY) {
        this.manualTickX = tickX;
        this.manualTickY = tickY;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (expression == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int centerX = width / 2 + (int) offsetX;
        int centerY = height / 2 + (int) offsetY;

        // Determine tick spacing (auto or manual)
        double tickSpacingX = manualTickX > 0 ? manualTickX : calculateAutoTickSpacing(scaleX, getWidth());
        double tickSpacingY = manualTickY > 0 ? manualTickY : calculateAutoTickSpacing(scaleY, getHeight());

        // Grid lines
        g2.setColor(new Color(230, 230, 230));
        drawGridLines(g2, width, height, centerX, centerY, tickSpacingX, tickSpacingY);

        // Axes
        g2.setColor(Color.GRAY);
        g2.drawLine(0, centerY, width, centerY);
        g2.drawLine(centerX, 0, centerX, height);

        // Axis ticks & numbers
        g2.setColor(Color.DARK_GRAY);
        drawAxisTicks(g2, width, height, centerX, centerY, tickSpacingX, tickSpacingY);

        // Graph
        g2.setColor(Color.BLUE);
        // Draw graph as connected lines for smoother curve
        int prevPx = 0;
        int prevPy = 0;
        boolean firstPoint = true;

        for(int px = 0; px < width; px++) {
            double x = (px - centerX) / scaleX;
            double y;
            try {
                y = expression.setVariable("x", x).evaluate();
                if (Double.isNaN(y) || Double.isInfinite(y)) {
                    firstPoint = true; // skip this point, break the line
                    continue;
                }
            } catch (Exception e) {
                firstPoint = true; // skip on error, break the line
                continue;
            }
            int py = (int) (centerY - y * scaleY);

            if (!firstPoint) {
                g2.drawLine(prevPx, prevPy, px, py);
            } else {
                firstPoint = false;
            }

            prevPx = px;
            prevPy = py;
        }
    }

    private double calculateAutoTickSpacing(double scale, int pixels) {
        double desiredTicks = 10.0;
        double idealSpacing = pixels / (scale * desiredTicks);

        double[] steps = { 0.1, 0.2, 0.5, 1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 1000000};
        for (double step : steps) {
            if (step >= idealSpacing) return step;
        }
        return Double.MAX_VALUE; // fallback
    }


    private void drawGridLines(Graphics2D g2, int width, int height, int centerX, int centerY, double tickSpacingX, double tickSpacingY) {
        int maxXTicks = (int) ((width + Math.abs(offsetX)) / scaleX / tickSpacingX) + 2;
        int maxYTicks = (int) ((height + Math.abs(offsetY)) / scaleY / tickSpacingY) + 2;

        for (int i = -maxXTicks; i <= maxXTicks; i++) {
            int x = (int) (centerX + i * tickSpacingX * scaleX);
            g2.drawLine(x, 0, x, height);
        }
        for (int i = -maxYTicks; i <= maxYTicks; i++) {
            int y = (int) (centerY - i * tickSpacingY * scaleY);
            g2.drawLine(0, y, width, y);
        }
    }

    private void drawAxisTicks(Graphics2D g2, int width, int height, int centerX, int centerY, double tickSpacingX, double tickSpacingY) {
        Font font = new Font("Arial", Font.PLAIN, 10);
        g2.setFont(font);

        int maxXTicks = (int) ((width + Math.abs(offsetX)) / scaleX / tickSpacingX) + 2;
        int maxYTicks = (int) ((height + Math.abs(offsetY)) / scaleY / tickSpacingY) + 2;

        for (int i = -maxXTicks; i <= maxXTicks; i++) {
            double worldX = i * tickSpacingX;
            int x = (int) (centerX + worldX * scaleX);
            g2.drawLine(x, centerY - 4, x, centerY + 4);
            if (Math.abs(worldX) > 1e-6) {
                g2.drawString(formatTick(worldX), x - 10, centerY + 15);
            }
        }

        for (int i = -maxYTicks; i <= maxYTicks; i++) {
            double worldY = i * tickSpacingY;
            int y = (int) (centerY - worldY * scaleY);
            g2.drawLine(centerX - 4, y, centerX + 4, y);
            if (Math.abs(worldY) > 1e-6) {
                g2.drawString(formatTick(worldY), centerX + 6, y + 4);
            }
        }
    }

    private String formatTick(double value) {
        if (Math.abs(value) >= 1 || value == 0) return String.format("%.0f", value);
        else return String.format("%.2f", value);
    }

    // Zoom
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double scrollAmount = e.getPreciseWheelRotation();
        double zoomFactor = Math.pow(1.1, scrollAmount);

        int mouseX = e.getX();
        int mouseY = e.getY();

        double beforeZoomX = (mouseX - getWidth() / 2.0 - offsetX) / scaleX;
        double beforeZoomY = (getHeight() / 2.0 + offsetY - mouseY) / scaleY;

        scaleX *= zoomFactor;
        scaleY *= zoomFactor;

        double afterZoomX = (mouseX - getWidth() / 2.0 - offsetX) / scaleX;
        double afterZoomY = (getHeight() / 2.0 + offsetY - mouseY) / scaleY;

        offsetX += (afterZoomX - beforeZoomX) * scaleX;
        offsetY += (afterZoomY - beforeZoomY) * scaleY;

        repaint();
    }

    // Pan
    @Override
    public void mousePressed(MouseEvent e) {
        lastMousePosition = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point current = e.getPoint();
        if (lastMousePosition != null) {
            offsetX += current.getX() - lastMousePosition.getX();
            offsetY += current.getY() - lastMousePosition.getY();
            lastMousePosition = current;
            repaint();
        }
    }

    // Unused
    @Override public void mouseReleased(MouseEvent e) { lastMousePosition = null; }
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
