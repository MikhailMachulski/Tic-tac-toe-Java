package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class Controller {
    private static final int SIZE_OF_SQUARE = 100;
    private static final int UNIT_X = 3;
    private static final int UNIT_Y = UNIT_X;
    private static final int RESOLUTION_X = UNIT_X * SIZE_OF_SQUARE;
    private static final int RESOLUTION_Y = UNIT_Y * SIZE_OF_SQUARE;
    private static final int WINNING_COUNT = 3;

    private View view;
    private Graphics graphics;
    private Figure[][] figures = new Figure[UNIT_X][UNIT_Y];
    private Set<Point> winningPoints = new HashSet<>();
    private boolean isCrossTurn = true;

    public void start() {
        view.create(RESOLUTION_X, RESOLUTION_Y);
        fillEmptyField();
        renderImage();
    }

    private void checkVictory() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y <= UNIT_Y - WINNING_COUNT; y++) {
                checkLine(x, y, 0, 1);
            }
        }
        for (int x = 0; x <= UNIT_X - WINNING_COUNT; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                checkLine(x, y, 1, 0);
            }
        }
        for (int x = 0; x <= UNIT_X - WINNING_COUNT; x++) {
            for (int y = 0; y <= UNIT_Y - WINNING_COUNT; y++) {
                checkLine(x, y, 1, 1);
            }
        }
        for (int x = WINNING_COUNT - 1; x < UNIT_X; x++) {
            for (int y = 0; y <= UNIT_Y - WINNING_COUNT; y++) {
                checkLine(x, y, -1, 1);
            }
        }
    }

    private void checkLine(int x, int y, int dx, int dy) {
        Figure firstFigure = figures[x][y];
        if (firstFigure == Figure.NONE) {
            return;
        }
        for (int i = 1; i < WINNING_COUNT; i++) {
            if (figures[x + dx * i][y + dy * i] != firstFigure) {
                return;
            }
        }
        for (int i = 0; i < WINNING_COUNT; i++) {
            winningPoints.add(new Point(x + dx * i, y + dy * i));
        }
    }

    public void handleMouseClick(int mouseX, int mouseY) {
        if (!winningPoints.isEmpty()) {
            return;
        }
        int x = mouseX / SIZE_OF_SQUARE;
        int y = mouseY / SIZE_OF_SQUARE;
        if (figures[x][y] != Figure.NONE) {
            return;
        }
        figures[x][y] = isCrossTurn ? Figure.CROSS : Figure.CIRCLE;
        checkVictory();
        isCrossTurn = !isCrossTurn;
        renderImage();
    }

    private void fillEmptyField() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                figures[x][y] = Figure.NONE;
            }
        }
    }

    private BufferedImage createSquare() {
        BufferedImage image = new BufferedImage(SIZE_OF_SQUARE, SIZE_OF_SQUARE, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.drawRect(0, 0, SIZE_OF_SQUARE - 1, SIZE_OF_SQUARE - 1);
        return image;
    }

    private BufferedImage createCross(boolean isWinning) {
        BufferedImage image = new BufferedImage(SIZE_OF_SQUARE, SIZE_OF_SQUARE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(isWinning ? Color.GREEN : Color.WHITE);
        graphics.drawLine(0, 0, SIZE_OF_SQUARE - 1, SIZE_OF_SQUARE - 1);
        graphics.drawLine(0, SIZE_OF_SQUARE - 1, SIZE_OF_SQUARE - 1, 0);
        return image;
    }

    private BufferedImage createCircle(boolean isWinning) {
        BufferedImage image = new BufferedImage(SIZE_OF_SQUARE, SIZE_OF_SQUARE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(isWinning ? Color.GREEN : Color.WHITE);
        graphics.drawOval(0, 0, SIZE_OF_SQUARE, SIZE_OF_SQUARE);
        return image;
    }

    private void draw(int x, int y, BufferedImage image) {
        graphics.drawImage(image, x * SIZE_OF_SQUARE, y * SIZE_OF_SQUARE, null);
    }

    private void renderImage() {
        BufferedImage image = new BufferedImage(RESOLUTION_X, RESOLUTION_Y, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();
        drawField();
        drawFigures();
        view.setImage(image);
    }

    private void drawFigures() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                boolean isWinning = winningPoints.contains(new Point(x, y));
                if (figures[x][y] == Figure.CROSS) {
                    draw(x, y, createCross(isWinning));
                }
                if (figures[x][y] == Figure.CIRCLE) {
                    draw(x, y, createCircle(isWinning));
                }
            }
        }
    }

    private void drawField() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                draw(x, y, createSquare());
            }
        }
    }

    public void setView(View view) {
        this.view = view;
    }
}
