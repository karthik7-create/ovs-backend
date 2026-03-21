package com.vote.ovs.Repository;

import com.vote.ovs.Entity.Vote;
import com.vote.ovs.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByUser(User user); // check already voted

    long countByCandidateId(Long candidateId); // count votes per candidate

    void deleteByCandidateId(Long candidateId); // delete votes for a candidate
}