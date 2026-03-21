package com.vote.ovs.Repository;

import com.vote.ovs.Entity.ElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionStatusRepository extends JpaRepository<ElectionStatus, Long> {
}
