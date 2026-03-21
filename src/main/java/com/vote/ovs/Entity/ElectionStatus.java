package com.vote.ovs.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "election_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(name = "results_published", nullable = false)
    private boolean resultsPublished = false;
}
