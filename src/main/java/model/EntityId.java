package model;

import jakarta.persistence.*;

import java.io.Serializable;

@MappedSuperclass
public abstract class EntityId<ID extends Serializable> implements Identifiable<ID> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private ID id;

    @Override
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}