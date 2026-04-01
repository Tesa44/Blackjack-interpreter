package blackjack.sim;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class BalancePlotter {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;
    private static final int LEFT_PAD = 70;
    private static final int RIGHT_PAD = 30;
    private static final int TOP_PAD = 50;
    private static final int BOTTOM_PAD = 70;

    public String plot(SimulationResult simulationResult) {
        List<Integer> balanceHistory = simulationResult.getBalanceHistory();
        if (balanceHistory.isEmpty()) {
            throw new IllegalStateException("No balance history available to plot.");
        }

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawBackground(graphics);
            drawAxes(graphics);
            drawBalanceLine(graphics, balanceHistory);
            drawLabels(graphics, balanceHistory);

            File plotsDir = new File("out/plots");
            if (!plotsDir.exists() && !plotsDir.mkdirs()) {
                throw new IllegalStateException("Could not create plots directory.");
            }

            File outputFile = new File(plotsDir, "balance_plot.png");
            ImageIO.write(image, "png", outputFile);
            return outputFile.getPath();
        } catch (IOException e) {
            throw new RuntimeException("Could not save balance plot.", e);
        } finally {
            graphics.dispose();
        }
    }

    private void drawBackground(Graphics2D graphics) {
        graphics.setColor(new Color(248, 249, 250));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(LEFT_PAD, TOP_PAD, WIDTH - LEFT_PAD - RIGHT_PAD, HEIGHT - TOP_PAD - BOTTOM_PAD);
    }

    private void drawAxes(Graphics2D graphics) {
        graphics.setColor(new Color(120, 120, 120));
        graphics.setStroke(new BasicStroke(2f));
        graphics.drawLine(LEFT_PAD, HEIGHT - BOTTOM_PAD, WIDTH - RIGHT_PAD, HEIGHT - BOTTOM_PAD);
        graphics.drawLine(LEFT_PAD, TOP_PAD, LEFT_PAD, HEIGHT - BOTTOM_PAD);
    }

    private void drawBalanceLine(Graphics2D graphics, List<Integer> balanceHistory) {
        int minBalance = balanceHistory.stream().min(Integer::compareTo).orElse(0);
        int maxBalance = balanceHistory.stream().max(Integer::compareTo).orElse(0);
        int balanceRange = Math.max(1, maxBalance - minBalance);
        int plotWidth = WIDTH - LEFT_PAD - RIGHT_PAD;
        int plotHeight = HEIGHT - TOP_PAD - BOTTOM_PAD;
        int pointCount = balanceHistory.size();

        graphics.setColor(new Color(24, 119, 242));
        graphics.setStroke(new BasicStroke(3f));

        for (int i = 1; i < pointCount; i++) {
            int previousX = LEFT_PAD + (int) (((double) (i - 1) / Math.max(1, pointCount - 1)) * plotWidth);
            int currentX = LEFT_PAD + (int) (((double) i / Math.max(1, pointCount - 1)) * plotWidth);

            int previousY = TOP_PAD + plotHeight
                    - (int) (((double) (balanceHistory.get(i - 1) - minBalance) / balanceRange) * plotHeight);
            int currentY = TOP_PAD + plotHeight
                    - (int) (((double) (balanceHistory.get(i) - minBalance) / balanceRange) * plotHeight);

            graphics.drawLine(previousX, previousY, currentX, currentY);
        }
    }

    private void drawLabels(Graphics2D graphics, List<Integer> balanceHistory) {
        int minBalance = balanceHistory.stream().min(Integer::compareTo).orElse(0);
        int maxBalance = balanceHistory.stream().max(Integer::compareTo).orElse(0);

        graphics.setColor(new Color(40, 40, 40));
        graphics.setFont(new Font("SansSerif", Font.BOLD, 18));
        graphics.drawString("Player Balance Over Time", LEFT_PAD, 30);

        graphics.setFont(new Font("SansSerif", Font.PLAIN, 12));
        graphics.drawString("Rounds", WIDTH / 2 - 20, HEIGHT - 20);
        graphics.drawString("Balance", 10, TOP_PAD - 10);

        graphics.drawString(String.valueOf(maxBalance), 20, TOP_PAD + 5);
        graphics.drawString(String.valueOf(minBalance), 20, HEIGHT - BOTTOM_PAD + 5);
        graphics.drawString("0", LEFT_PAD - 5, HEIGHT - BOTTOM_PAD + 20);
        graphics.drawString(String.valueOf(balanceHistory.size() - 1), WIDTH - RIGHT_PAD - 20, HEIGHT - BOTTOM_PAD + 20);
    }
}
