package com.vote.ovs.Entity;

import jakarta.persistence.*;
import  lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor          //generates the constructor with no arguments
@AllArgsConstructor         //generates the constructor with all arguments
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "has_voted")
    private boolean hasVoted = false;

    @Column(nullable = false)
    private String role = "USER";


}
