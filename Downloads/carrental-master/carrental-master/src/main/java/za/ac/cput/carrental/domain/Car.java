/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.cput.carrental.domain;

/**
 *
 * @author krubennaidoo
 */
public class Car {
    private String make;
    private int yearModel;
    private String price;

    public Car() {
    }

    public Car(String make, int yearModel, String price) {
        this.make = make;
        this.yearModel = yearModel;
        this.price = price;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public int getYearModel() {
        return yearModel;
    }

    public void setYearModel(int yearModel) {
        this.yearModel = yearModel;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Car{" + "make=" + make + ", yearModel=" + yearModel + ", price=" + price + '}';
    }
}
