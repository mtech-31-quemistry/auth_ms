package com.quemistry.auth_ms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 500)
    private String path;

    @Column(length = 50)
    private String method;

    //@ManyToMany(mappedBy = "grantedWith")
    //Set<Role> grantedTo;
}
