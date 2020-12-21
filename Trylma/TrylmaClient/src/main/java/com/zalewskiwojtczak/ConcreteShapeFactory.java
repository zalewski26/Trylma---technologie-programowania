package com.zalewskiwojtczak;

/** Klasa reprezentująca konkretną fabrykę pionków
 */
public class ConcreteShapeFactory implements ShapeFactory {

    @Override
    public Shape getShape(String type, int x, int y, int diameter, int id) {
        if (type.equalsIgnoreCase("CIRCLE")) {
            return new Circle(x, y, diameter, id);
        }
        //} else if (type.equalsIgnoreCase("...")) {
        //    return new ...;
        return null;
    }
}
