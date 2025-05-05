package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
public class Client extends User {
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 20)
    @Column(name = "cnp", length = 20)
    private String cnp;

    @Size(max = 20)
    @Column(name = "telephone_number", length = 20)
    private String telephoneNumber;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "client")
    private Set<Cart> carts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<OwnedGame> ownedGames = new LinkedHashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Cart> getCarts() {
        return carts;
    }

    public void setCarts(Set<Cart> carts) {
        this.carts = carts;
    }

    public Set<OwnedGame> getOwnedGames() {
        return ownedGames;
    }

    public void setOwnedGames(Set<OwnedGame> ownedGames) {
        this.ownedGames = ownedGames;
    }
}