package com.vote.ovs.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "votes",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id")) // 🔥 prevents double voting
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;
}
