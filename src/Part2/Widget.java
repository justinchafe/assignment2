package Part2;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Widget {
    private double minWidth, minHeight;
    private double maxWidth, maxHeight;
    private double prefWidth, prefHeight;
    private double actualWidth, actualHeight;
    private double xPos, yPos;
    final private SimpleObjectProperty<Color> widgetColor = new SimpleObjectProperty<>();

    public Widget(Dimension2D minSize, Dimension2D maxSize, Dimension2D prefSize) {
        setPrefSize(prefSize.getWidth(), prefSize.getHeight());
        setActualSize(prefSize.getWidth(), prefSize.getHeight());
        setMinSize(minSize.getWidth(), minSize.getHeight());
        setMaxSize(maxSize.getWidth(), maxSize.getHeight());
        xPos = yPos = 0;
        widgetColor.set(Color.GREEN);
    }

    public double getWidth() {
        return actualWidth;
    }

    public double getHeight() {
        return actualHeight;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public double getMinWidth() {
        return minWidth;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public double getMinHeight() {
        return minHeight;
    }

    public void setMinSize(double newMinWidth, double newMinHeight) {
        minWidth = newMinWidth;
        minHeight = newMinHeight;
    }

    public void setMaxSize(double newMaxWidth, double newMaxHeight) {
        maxWidth = newMaxWidth;
        maxHeight = newMaxHeight;
    }

    public void setPrefSize(double newPrefWidth, double newPrefHeight) {
        prefWidth =newPrefWidth;
        prefHeight =newPrefHeight;
    }

    public void setActualSize(double newWidth, double newHeight) {
        actualWidth = newWidth;
        actualHeight = newHeight;
    }

    public Dimension2D getMinSize() {
        return new Dimension2D(minWidth,minHeight);
    }

    public Dimension2D getMaxSize() {
        return new Dimension2D(maxWidth, maxHeight);
    }

    public Dimension2D getPrefSize() {
        return new Dimension2D(prefWidth, prefHeight);
    }

    public Dimension2D getActualSize() {
        return new Dimension2D(actualWidth, actualHeight);
    }

    /**
     * Draws the Widget to canvas with it's associated Color property.
     *
     * @param gc The GraphicsContext used to draw the Widget on a canvas.
     */
    void draw(GraphicsContext gc) {
        gc.setFill(getWidgetColor());
        gc.fillRect(xPos, yPos, actualWidth, actualHeight);
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(xPos, yPos, actualWidth, actualHeight);
        gc.strokeLine(xPos,yPos,xPos+actualWidth, yPos+actualHeight);
        gc.strokeLine(xPos, yPos+actualHeight,xPos+actualWidth, yPos);
        gc.setStroke(Color.WHITE);
        gc.strokeText("\nMin Width: " + minWidth + "\nPref Width: " + prefWidth +"\nMax Width: " + maxWidth +
                "\nWidth: "  + String.format("%.2f", actualWidth), xPos, yPos);
    }

    /**
     * Sets the Widgets position.
     * @param x double value indicating horizontal position on canvas.
     * @param y double value indicating vertical position on canvas.
     */
    public void setPos(double x, double y) {
        xPos = x;
        yPos = y;
    }

    public final Color getWidgetColor(){
        return widgetColor.get();
    }

    public final void setWidgetColor(Color c){ widgetColor.set(c);}

    public SimpleObjectProperty<Color> getWidgetColorProperty() {
        return widgetColor;
    }


}
