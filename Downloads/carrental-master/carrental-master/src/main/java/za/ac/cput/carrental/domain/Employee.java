/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.cput.carrental.domain;

/**
 *
 * @author DELL
 */
public class Employee {
    private int empNum;
    private String firstName;
    private String lastName;
    private String email;
    private int phoneNum;

    public Employee() {
    }

    public Employee(int empNum, String firstName, String lastName, String email, int phoneNum) {
        this.empNum = empNum;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNum = phoneNum;
    }

    public int getEmpNum() {
        return empNum;
    }

    public void setEmpNum(int empNum) {
        this.empNum = empNum;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Override
    public String toString() {
        return "Employee{" + "EmpNum= #" + empNum + ", First Name= " + firstName 
                + ", Last Name= " + lastName + ", Email= " + email + ", Phone Num= " + phoneNum + '}';
    }
    
    
}
