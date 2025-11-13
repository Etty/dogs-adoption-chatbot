package com.example.adoptions.entity;

import com.example.adoptions.jpa_listener.AppointmentListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "APPOINTMENT_PHONE", columnList = "phone")
})
@EntityListeners(AppointmentListener.class)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Phone number cannot be blank")
    private String phone;

    @JsonFormat(pattern = "YYYY-MM-DD HH:mm")
    private LocalDateTime time;

    @Lob
    private String comment;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dog_id", nullable = false, unique = true)
    private Dog dog;

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", time=" + time +
                ", dog=" + dog +
                '}';
    }
}
