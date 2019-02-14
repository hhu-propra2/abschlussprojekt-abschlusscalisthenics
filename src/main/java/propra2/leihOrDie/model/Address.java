package propra2.leihOrDie.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Embeddable
public class Address {
    private int postcode;
    private String street;
    private String houseNumber;
    private String city;

    public Address() {}

    public Address(int postcode, String street, String houseNumber, String city) {
        this.postcode = postcode;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
    }
}