package com.zalewskiwojtczak;

/** Klasa reprezentująca fabrykę pionków
 */
public interface ShapeFactory {
    Shape getShape(String type, int x, int y, int diameter, int id);
}
