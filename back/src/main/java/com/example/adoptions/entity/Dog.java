package com.example.adoptions.entity;

import com.example.adoptions.jpa_listener.DogListener;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Entity
@EntityListeners(DogListener.class)
public class Dog {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Lob
    private String description;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="dog", orphanRemoval = true)
    private Appointment appointment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dog dog = (Dog) o;
        return Objects.equals(
                getId(), dog.getId())
                && Objects.equals(getName(), dog.getName())
                && Objects.equals(getDescription(), dog.getDescription())
                && Objects.equals(getAppointment(), dog.getAppointment()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getAppointment());
    }
}