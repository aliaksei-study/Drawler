package com.example.drawler;

import android.graphics.Path;

public class FingerPath {
    private int color;
    private boolean emboss;
    private boolean blur;
    private int strokeWidth;
    private Path path;
    private Figure figure;

    public FingerPath(int color, boolean emboss, boolean blur, int strokeWidth, Path path, Figure figure) {
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
        this.figure = figure;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean getEmboss() {
        return emboss;
    }

    public void setEmboss(boolean emboss) {
        this.emboss = emboss;
    }

    public boolean getBlur() {
        return blur;
    }

    public void setBlur(boolean blur) {
        this.blur = blur;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }
}
