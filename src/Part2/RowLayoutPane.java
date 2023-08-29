package Part2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;

import java.util.ArrayList;
import java.util.List;

public class RowLayoutPane extends Pane {

    public static enum Position {TOP, CENTER, FILL}

    private static final int LINE_WIDTH = 1;
    private int inset = 5;
    private List<RowCell> rCells; //1:1 relationship with Widgets.
    private final Canvas canvas;
    private final GraphicsContext gc;

    /**
     * Creates an empty RowLayoutPane.
     */
    public RowLayoutPane() {
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        rCells = new ArrayList<>();
        getChildren().add(canvas);
    }

    /**
     * Creates a RowLayoutPane and places a widget inside at Position.TOP.
     * @param w Widget to add to the RowLayoutPane.
     */
    public RowLayoutPane(Widget w) {
        inset = 5;
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        rCells = new ArrayList<>();
        getChildren().add(canvas);
        addWidget(w);
    }

    /**
     * Adds a widget to the RowLayoutPane. Default width of widget is set to preferred size.
     * The RowLayoutPane grows by the amount of this widget.
     *
     * @param w A Widget that is added to a RowCell
     */
    public void addWidget(Widget w) {
        RowLayoutPane.RowCell rCell = this.new RowCell(w);
        rCells.add(rCell);
        rCell.width = w.getPrefSize().getWidth();
        this.canvas.setWidth(canvas.getWidth() + w.getWidth());
    }

    /**
     * Removes a widget from the RowLayoutPane.
     * The RowLayoutPane shrinks by the amount of this widget.
     *
     * @param w Widget that is added to a RowCell
     * @return true if the Widget is removed from the container, false otherwise.
     */
    public boolean removeWidget(Widget w) {
        if (rCells.isEmpty())
            return false;
        for (RowCell rc : rCells) {
            if (rc.getWidget().equals(w)) {
                canvas.setWidth(canvas.getWidth()-w.getWidth());
                rCells.remove(rc); //might be nice to remove widget from rc -> object is garbage.
                return true;
            }
        }
        return false;
    }

    /**
     * Set's the vertical position of a Widget.
     *
     * @param w The widget to assign Position
     * @param position Position for the Widget (TOP,CENTER,FILL);
     * @return true if widgets Position was set properly, false otherwise.
     */
    public boolean setVerticalPosition(Widget w, Position position) {
        for (RowCell l : rCells) {
            if (l.getWidget().equals(w)) {
                l.setWidget(w, position);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if all widgets are at maximum width.
     *
     * @return true if all widgets are at maximum width, false otherwise.
     */
    boolean widgetsMaxed() {
        for (RowCell rc : rCells) {
            if (rc.getWidget().getWidth() != rc.getWidget().getMaxWidth())
                return false;
        }
        return true;
    }

    /**
     *Checks to see if all widgets are at minimum width.
     *
     * @return true if all widgets are at minimum width, false otherwise.
     */
    boolean widgetsMin() {
        for (RowCell rc : rCells) {
            if (rc.getWidget().getWidth() != rc.getWidget().getMinWidth())
                return false;
        }
        return true;
    }

    /**
     * Calculates the total value for a width property of the Widget.
     *
     * @param totalType Type of width property.  Valid values are: "actualWidth", "minWidth", "maxWidth", "prefWidth"
     * @return The combined total width of the width property selected by totalType.
     */
    double widgetTotals(String totalType) {
        double w = 0;
        for (RowCell rc: rCells) {
            switch (totalType) {
                case "actualWidth":
                    w = w + rc.getWidget().getActualSize().getWidth();
                    break;
                case "minWidth":
                    w = w + rc.getWidget().getMinSize().getWidth();
                    break;
                case "maxWidth":
                    w = w + rc.getWidget().getMaxSize().getWidth();
                    break;
                case "prefWidth":
                    w = w + rc.getWidget().getPrefSize().getWidth();
                    break;
                default:
                    w = 0;
            }
        }
        return w;
    }

    /**
     * Draws the RowCells and their associated contents to the canvas.
     * Draws textual information about the width of the canvas and the Widgets.
     *
     * @param gc The GraphicsContext used for drawing to the main canvas.
     */
    private void drawRow(GraphicsContext gc) {
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight()); //clear before redraw
        for (RowCell rc : rCells) {
            rc.draw(gc);
        }
        gc.setFontSmoothingType(FontSmoothingType.LCD);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(LINE_WIDTH);
        gc.strokeText("\nMin Width: " + widgetTotals("minWidth")
                + "\nPref Width: " + widgetTotals("prefWidth")
                + "\nMax Width: " + widgetTotals("maxWidth")
                + "\nWidth: " + canvas.getWidth(), 10,20 );
    }

    /**
     * Sets the width dimensions of the rowCells. Grows and shrinks appropriately.
     *NOTE: RowCell method layoutChildren() is called in the grow(z) and shrink(z) helper methods.
     */
    @Override
    public void layoutChildren() {
        double diff;
        canvas.setWidth(this.getWidth());
        canvas.setHeight(this.getHeight());
        diff = canvas.getWidth() - widgetTotals("actualWidth");

        //Growing:
        if ( diff >= 0) {
            grow(diff);

        //Shrinking:
        }else if (diff < 0) {
            shrink(diff);
            //If we maximize and then minimize the widgets may be at minimum size, but the canvas may still be larger since
            //it was not dragged.  Regrow with current widths & snap to clip.
            if (widgetsMin() && canvas.getWidth() > widgetTotals("minWidth")) {
               diff = canvas.getWidth() - widgetTotals("minWidth"); //re-calc diff
               grow(diff);
            }
        }
        drawRow(gc);
    }

    /**
     * Grows all RowCells proportionately with an increase in canvas size.
     * when the window gets wider, all widgets grow proportionally until they reach their max width.
     * if all widgets are at their max width, the canvas extends but the widgets do not change.
     * when the window gets taller, only widgets with position of FILL change height.
     *
     * @param z Double representing the change in canvas width.
     */
    private void grow(double z) {
        double w = 0; //the width of the current RowCell. Used to set the next RowCell's horizontal position.
        double equalProp; //ratio value for calculating width proportions.
        double itemsRem; //items remaining in the list.
        itemsRem = rCells.size();
        if (itemsRem > 0) {
            equalProp = z / itemsRem; //+ve growing, or 0
        }else
          return;

        for (RowCell rc : rCells) {
            rc.height = canvas.getHeight();

            //max width, no change. Change equalProp ratio for remaining items.
            if (rc.width == rc.getWidget().getMaxWidth()) {
                //z doesn't change
                itemsRem--;
                if (itemsRem > 0)
                    equalProp = z / itemsRem;

            //below maxWidth but not enough room to grow fully. Expand to max and change equalProp ratio for remaining space/items.
            }else if (rc.width < rc.getWidget().getMaxWidth() && rc.width + equalProp > rc.getWidget().getMaxWidth()) {
                    //z will get smaller
                    z = z - (rc.getWidget().getMaxWidth() - rc.width);
                    rc.width = rc.getWidget().getMaxWidth();
                    itemsRem--;
                    if (itemsRem>0)
                        equalProp = z/itemsRem;

            //below maxWidth and enough size to grow. Grow.
            }else if (rc.width < rc.getWidget().getMaxWidth()){
                    rc.width = rc.width+equalProp;
                    itemsRem--;
            }

            //set the position of this element and increment for next item. layout RowCell.
            rc.setPosition(w,0);
            w += rc.width;
            rc.layoutChildren();
        }
    }

    /**
     * Shrinks all RowCells proportionately with a decrease in canvas size.
     * if all widgets are at their min width and the window gets smaller, the window should clip at the right side
     * when the window gets taller, only widgets with position of FILL change height.
     *
     * @param z Double representing the change in canvas width.
     */
    private void shrink(double z) {
        double w = 0; //the width of the current RowCell. Used to set the next RowCell's horizontal position.
        double equalProp; //ratio value for calculating width proportions.
        double itemsRem; //items remaining in the list.
        itemsRem=rCells.size();
        if (itemsRem > 0) {
            equalProp = z / itemsRem; //-ve shrinking
        }else
            return;

        for (RowCell rc : rCells) {
            rc.height = canvas.getHeight();

            //min width no change. Change equalProp ratio for remaining items.
            if (rc.width == rc.getWidget().getMinWidth()) {
                //z doesn't change
                itemsRem--;
                if (itemsRem > 0)
                    equalProp = z / itemsRem;

            //above minWidth but not enough room to shrink fully. Shrink to min. Change equalProp ratio for remaining space/items.
            }else if (rc.width > rc.getWidget().getMinWidth() && rc.width + equalProp < rc.getWidget().getMinWidth()) {
                //z will get smaller
                z -= Math.abs(rc.getWidget().getMinWidth() - rc.width);
                rc.width = rc.getWidget().getMinWidth();
                itemsRem--;
                if (itemsRem>0)
                    equalProp = z/itemsRem;

            //below minWidth and enough room to shrink. Shrink.
            }else if (rc.width > rc.getWidget().getMinWidth()){
                rc.width += equalProp;
                itemsRem--;
            }
            //set the position of this element and increment for next item. layout RowCell.
            rc.setPosition(w,0);
            w += rc.width;
            rc.layoutChildren();
        }
    }

    /**
     * Never used, for reference only.
     * This method grows Widgets proportionately based on propVal.
     *
     * @param z - double representing the change in Canvas width.
     * @param propVal - valid values are 0 or 1 or 2.  0 sizes widget based on it's minWidth.  1 sizes widget based on it's maxWidth. 2 current width.
     */
    private void growProp(double z, int propVal) {
        double w = 0; //the width of the current RowCell. Used to set the next RowCell's horizontal position.
        double propW = 0; //ratio value for calculating width proportions.
        double totalWidth = widgetTotals("actualWidth");

        for (RowCell rc : rCells) {
            rc.height = canvas.getHeight();
            switch(propVal) {
                case 0: default:
                    if(totalWidth > 0)
                        propW = (rc.getWidget().getMinWidth()/totalWidth)*z;
                    break;
                case 1:
                    if(totalWidth > 0)
                        propW = (rc.getWidget().getMaxWidth()/totalWidth)*z;
                    break;
                case 2:
                    if(totalWidth > 0)
                        propW = (rc.width/totalWidth)*z;
                    break;
            }

            //max width, no change. Decrement totalWidth.
            if (rc.width == rc.getWidget().getMaxWidth()) {
                //z doesn't change
                totalWidth -= rc.getWidget().getMaxWidth();

            //below maxWidth but not enough growth room. Expand to max & decrement totalWidth.
            }else if (rc.width < rc.getWidget().getMaxWidth() && rc.width + propW > rc.getWidget().getMaxWidth()) {
                //totalWidth remaining is now smaller.
                totalWidth -= (rc.getWidget().getMaxWidth() - rc.width);
                rc.width = rc.getWidget().getMaxWidth();

            //below maxWidth and enough size to grow.
            }else if (rc.width < rc.getWidget().getMaxWidth()){
                rc.width = rc.width+propW;
            }
            rc.setPosition(w,0);
            w += rc.width;
            rc.layoutChildren();
        }
    }

    /**
     * Never used, for reference only.
     * This method shrinks Widgets proportionately based on propVal input.
     *
     * @param z - double representing the change in Canvas width.
     * @param propVal - valid values are 0 or 1 or 2.  0 sizes widget based on it's minWidth.  1 sizes widget based on it's maxWidth, 2 current width.
     */
    private void shrinkProp(double z, int propVal) {
        double w = 0; //the width of the current RowCell. Used to set the next RowCell's horizontal position.
        double propW = 0; //ratio value for calculating width proportions.
        double totalWidth = widgetTotals("actualWidth");

        for (RowCell rc : rCells) {
            rc.height = canvas.getHeight();
            switch(propVal) {
                case 0: default:
                    if(totalWidth > 0)
                        propW = (rc.getWidget().getMinWidth()/totalWidth)*z;
                    break;
               case 1:
                   if(totalWidth > 0)
                       propW = (rc.getWidget().getMaxWidth() / totalWidth)*z;
                       break;
                case 2:
                    if(totalWidth > 0)
                        propW = (rc.width/totalWidth)*z;
                    break;
            }

            //min width, no change. Decrement totalWidth.
            if (rc.width == rc.getWidget().getMinWidth()) {
                //z doesn't change
                totalWidth -= rc.getWidget().getMinWidth();

            //above minWidth but not enough to shrink fully. Shrink to minWidth and decrement totalWidth.
            }else if (rc.width > rc.getWidget().getMinWidth() && rc.width + propW < rc.getWidget().getMinWidth()) {
                //totalWidth remaining is now smaller.
                totalWidth -= Math.abs(rc.width - rc.getWidget().getMinWidth());
                rc.width = rc.getWidget().getMinWidth();

            //above minWidth enough room to shrink.
            }else if (rc.width > rc.getWidget().getMinWidth()){
                rc.width = rc.width+propW;
            }
            rc.setPosition(w,0);
            w += rc.width;
            rc.layoutChildren();
        }
    }

    /********************************************************************************************************
    INNER CLASS:  RowCell - Holds Widgets and positions them accordingly.
    ********************************************************************************************************/

    private class RowCell {
        Widget w;
        Position p;
        double x, y;
        double height, width;

        public RowCell(Widget w) {
            x = y = 0;
            this.w = w;
            p = Position.TOP; //DEFAULT POSITION
        }

        /**
         * Associates a *widget* object with this cell, and sets its vertical position.
         *
         * @param w The Widget associated with this RowCell.
         * @param p The vertical Position (TOP,CENTER,FILL) value of the Widget.
         */
        void setWidget(Widget w, Position p) {
            this.w = w;
            this.p = p;
        }

        Widget getWidget() {
            return w;
        }

        /**
         * Sets the vertical position and height of the *widget*, given its positioning constraint.
         *
         * @param p The Position (TOP,CENTER,FILL) to set the widget.  This method will also align the horizontal
         *          x RowCell coordinate to the Widget by calling the Widgets method: setPos(x,y).
         *
         */
        public void positionWidgetVertical(Position p) {
            double x,y;
            switch (p) {
                case TOP: default:
                    x = this.x;
                    y = 0;
                    break;
                case CENTER:
                    x = this.x;
                    y = this.height / 2 - w.getActualSize().getHeight() / 2;
                    break;
                case FILL:
                    x = this.x;
                    y = 0;
                    w.setActualSize(w.getWidth(), this.height);
                    break;
            }
            w.setPos(x, y);
        }

        /**
         * Sets the position of the *RowCell* relative to the entire pane.
         *
         * @param x The x position of the RowCell.
         * @param y The y position of the RowCell.
         */
        void setPosition(double x, double y) {
            this.x = x;
            this.y = y;
        }

       /**
         * Draws a white oval on a gray background to indicate the bounds of this cell, then asks the widget to
         * draw itself.
         *
         * @param gc The GraphicsContext used for drawing to the canvas.
         */
        public void draw(GraphicsContext gc) {
            gc.setFill(Color.GRAY);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(LINE_WIDTH);
            gc.strokeOval(x, y, width-inset , height-inset);
            w.draw(gc); //tell widget to draw itself.
        }

        /**
         * Sets the position and dimensions of the widget (hint: might be called by the RowLayoutPane’s layoutChildren)
         * Called by RowLayoutPane layoutChildren method indirectly via private helper method resize(double z).
         */
        public void layoutChildren() {
            //width = Math.floor(width * 1000) / 1000;  //limit precision?
            w.setActualSize(this.width, w.getHeight());
            positionWidgetVertical(p); //This also positions Widgets aligned to cell.
            setColor();
            //draw(gc); //Initially used layoutChildren() to do all the RowCell and Widget Drawing.
        }

        /**
         * Used to set the color of a Widget. Value is dependent on the Widget's width properties.
         *
         */
        void setColor() {
            double precision = 0.1; //tough to get "green" on preferred width sometimes as doubles hard to equate on mouse drag.
            if (Math.abs(w.getWidth() - w.getPrefSize().getWidth()) < precision)
                w.setWidgetColor(Color.GREEN);
            else if (Math.abs(w.getWidth() - w.getMinWidth()) < precision)
                w.setWidgetColor(Color.RED);
            else if (Math.abs(w.getWidth() - w.getMaxWidth()) < precision)
                w.setWidgetColor(Color.BLUE);
            else if (w.getWidth() > w.getPrefSize().getWidth() && w.getWidth() < w.getMaxWidth())
                w.setWidgetColor(Color.PURPLE);
            else if (w.getWidth() > w.getMinWidth() && w.getWidth() < w.getPrefSize().getWidth())
                w.setWidgetColor(Color.ORANGE);
            }
    }
}


