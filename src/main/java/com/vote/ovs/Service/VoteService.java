package com.vote.ovs.Service;

import com.vote.ovs.Entity.*;
import com.vote.ovs.Repository.*;
import org.springframework.stereotype.Service;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionStatusRepository electionStatusRepository;

    public VoteService(VoteRepository voteRepository,
                       UserRepository userRepository,
                       CandidateRepository candidateRepository,
                       ElectionStatusRepository electionStatusRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.electionStatusRepository = electionStatusRepository;
    }

    public String castVote(String username, Long candidateId) {

        // Block voting if results are already published
        boolean resultsPublished = electionStatusRepository.findById(1L)
                .map(ElectionStatus::isResultsPublished)
                .orElse(false);

        if (resultsPublished) {
            throw new RuntimeException("Voting is closed");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (voteRepository.existsByUser(user)) {
            throw new RuntimeException("User already voted");
        }

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        Vote vote = Vote.builder()
                .user(user)
                .candidate(candidate)
                .build();

        voteRepository.save(vote);

        user.setHasVoted(true);
        userRepository.save(user);

        return "Vote cast successfully";
    }
}