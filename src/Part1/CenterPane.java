package Part1;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class CenterPane extends Pane {

    final private SimpleObjectProperty<Color> centerPaneColor = new SimpleObjectProperty<>();
    final private Canvas canvasFG, canvasBG;
    final private GraphicsContext gcBG, gcFG;
    private int inset = 5;
    private int lineWidth = 5;

    //Convenience variables:
    private double wRadius;
    private double hRadius;

    public CenterPane(Color c) {
        centerPaneColor.set(c);
        canvasFG = new Canvas();
        canvasBG = new Canvas();
        gcFG = canvasFG.getGraphicsContext2D();
        gcBG = canvasBG.getGraphicsContext2D();
        wRadius = this.getWidth()/2;
        hRadius = this.getHeight()/2;
        getChildren().addAll(canvasBG, canvasFG);
    }

    public int getInset() {return inset;}

    public void setInset(int inset) {this.inset = inset;}

    /**
     * Draws the pattern to the canvas with the selected color property.
     * The pattern displayed in the CenterPane consists of a large Oval shape that is centered and fills the available space with a 5 pixel
     * border on all sides. There is a white oval inside that is 2/3 the size of the first oval, and another colored oval that is 1/3 the size
     * of the first oval.
     */
    private void draw() {
        drawBackground(gcFG); //clear FG
        drawBackground(gcBG); //redraw BG
        gcFG.setStroke(Color.WHITE);
        gcFG.setLineWidth(lineWidth);
        gcFG.strokeOval(inset,inset,canvasFG.getWidth()-inset*2, canvasFG.getHeight()-inset*2);

        //Fill entire 2/3 oval in white:
        gcFG.setFill(Color.WHITE);
        gcFG.fillOval(wRadius  - (wRadius*2/3),hRadius - (hRadius*2/3),wRadius*2/3*2, hRadius*2/3*2);

        //Fill inner 1/3 oval with color property:
        gcFG.setFill(centerPaneColor.get());
        gcFG.fillOval(wRadius-wRadius/3, hRadius-hRadius/3, wRadius/3*2, hRadius/3*2);
    }

    /**
     * Draws the background to the canvas with color specified from the color property.
     * @param gc The graphics context for the background.
     */
    private void drawBackground(GraphicsContext gc) {
        gc.setFill(centerPaneColor.get());
        gc.fillRect(0,0,canvasBG.getWidth(), canvasBG.getHeight());
    }

    /**
     * When the Pane size changes this method resizes the canvas background and foreground.
     * calls the draw() method on Pane resize.
     */
    @Override
    public void layoutChildren() {
        canvasBG.setWidth(this.getWidth());
        canvasBG.setHeight(this.getHeight());
        canvasFG.setWidth(this.getWidth());
        canvasFG.setHeight(this.getHeight());
        wRadius = this.getWidth()/2;
        hRadius = this.getHeight()/2;
        draw();
    }

    /**
     * Gets the CenterPane Color
     * @return Color associated with the Pane.
     */
    public final Color getCenterPaneColor(){
        return centerPaneColor.get();
    }

    /**
     * Sets the CenterPane Color.
     * @param c Color to use for CenterPane.
     */
    public final void setCenterPaneColor(Color c){
        centerPaneColor.set(c);
        draw();
    }

    /**
     * Gets the CenterPane Color Property
     * @return SimpleObjectProperty of type Color
     */
    public SimpleObjectProperty<Color> getCenterPaneProperty() {
        return centerPaneColor;
    }

}
