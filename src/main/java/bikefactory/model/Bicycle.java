package bikefactory.model;

import bikefactory.util.Validator;

import java.util.Date;

public class Bicycle extends Vehicle {
    private String type;
    private int wheelDiameter;
    private int gears;
    private double weight;

    public Bicycle(int id, String type, String model, int wheelDiameter, int gears,
                   double weight, Date releaseDate, double price) {
        super(id, model, releaseDate, price);
        this.type = type;
        this.wheelDiameter = wheelDiameter;
        this.gears = gears;
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWheelDiameter() {
        return wheelDiameter;
    }

    public void setWheelDiameter(int wheelDiameter) {
        this.wheelDiameter = wheelDiameter;
    }

    public int getGears() {
        return gears;
    }

    public void setGears(int gears) {
        this.gears = gears;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toFileLine() {
        return getId() + ";" + type + ";" + getModel() + ";" + wheelDiameter + ";" + gears + ";"
                + weight + ";" + Validator.formatDate(getReleaseDate()) + ";" + getPrice();
    }

    @Override
    public String toString() {
        return "Велосипед [id=" + getId()
                + ", тип=" + type
                + ", модель=" + getModel()
                + ", колёса=" + wheelDiameter + "\""
                + ", передач=" + gears
                + ", вес=" + weight + " кг"
                + ", выпущен=" + Validator.formatDate(getReleaseDate())
                + ", цена=" + getPrice() + "]";
    }
}
