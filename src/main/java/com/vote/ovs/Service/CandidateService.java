package com.vote.ovs.Service;

import com.vote.ovs.Dto.CandidateResponse;
import com.vote.ovs.Repository.CandidateRepository;
import com.vote.ovs.Repository.VoteRepository;
import com.vote.ovs.Entity.Candidate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;

    public CandidateService(CandidateRepository candidateRepository,
                            VoteRepository voteRepository) {
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
    }

    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(c -> CandidateResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .party(c.getParty())
                        .build())
                .toList();
    }

    public CandidateResponse addCandidate(String name, String party) {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Candidate name is required");
        }
        if (party == null || party.isBlank()) {
            throw new RuntimeException("Party name is required");
        }

        Candidate candidate = Candidate.builder()
                .name(name.trim())
                .party(party.trim())
                .build();

        candidate = candidateRepository.save(candidate);

        return CandidateResponse.builder()
                .id(candidate.getId())
                .name(candidate.getName())
                .party(candidate.getParty())
                .build();
    }

    @Transactional
    public String removeCandidate(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        // Delete all votes for this candidate first
        voteRepository.deleteByCandidateId(candidateId);

        candidateRepository.delete(candidate);

        return "Candidate '" + candidate.getName() + "' removed successfully";
    }
}
