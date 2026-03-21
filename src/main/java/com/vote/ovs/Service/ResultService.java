package com.vote.ovs.Service;

import com.vote.ovs.Dto.ResultResponse;
import com.vote.ovs.Entity.Candidate;
import com.vote.ovs.Entity.ElectionStatus;
import com.vote.ovs.Entity.User;
import com.vote.ovs.Repository.CandidateRepository;
import com.vote.ovs.Repository.ElectionStatusRepository;
import com.vote.ovs.Repository.UserRepository;
import com.vote.ovs.Repository.VoteRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ResultService {

    private final ElectionStatusRepository electionStatusRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public ResultService(ElectionStatusRepository electionStatusRepository,
                         CandidateRepository candidateRepository,
                         VoteRepository voteRepository,
                         UserRepository userRepository,
                         EntityManager entityManager) {
        this.electionStatusRepository = electionStatusRepository;
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    /**
     * Get or create the single election status row.
     */
    private ElectionStatus getElectionStatus() {
        return electionStatusRepository.findById(1L)
                .orElseGet(() -> {
                    ElectionStatus status = ElectionStatus.builder()
                            .resultsPublished(false)
                            .build();
                    return electionStatusRepository.save(status);
                });
    }

    /**
     * Admin publishes the results.
     */
    public String publishResults() {
        ElectionStatus status = getElectionStatus();
        if (status.isResultsPublished()) {
            return "Results are already published";
        }
        status.setResultsPublished(true);
        electionStatusRepository.save(status);
        return "Results published successfully";
    }

    /**
     * Admin unpublishes the results (hides them again).
     */
    public String unpublishResults() {
        ElectionStatus status = getElectionStatus();
        if (!status.isResultsPublished()) {
            return "Results are already hidden";
        }
        status.setResultsPublished(false);
        electionStatusRepository.save(status);
        return "Results unpublished successfully";
    }

    /**
     * Check if results are currently published.
     */
    public boolean areResultsPublished() {
        return getElectionStatus().isResultsPublished();
    }

    /**
     * Reset everything for a new election:
     * - Delete all votes
     * - Delete all candidates
     * - Delete all non-admin users (keep admin accounts)
     * - Reset MySQL auto-increment IDs
     * - Unpublish results
     */
    @Transactional
    public String resetElection() {
        // 1. Delete all votes
        voteRepository.deleteAll();
        entityManager.flush();

        // 2. Delete all candidates
        candidateRepository.deleteAll();
        entityManager.flush();

        // 3. Delete all non-admin users (keep admin accounts so they don't need to re-register)
        List<User> allUsers = userRepository.findAll();
        List<User> nonAdminUsers = allUsers.stream()
                .filter(u -> !"ADMIN".equals(u.getRole()))
                .toList();
        userRepository.deleteAll(nonAdminUsers);

        // 4. Reset hasVoted for remaining admin users
        List<User> adminUsers = allUsers.stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .toList();
        for (User admin : adminUsers) {
            admin.setHasVoted(false);
        }
        userRepository.saveAll(adminUsers);
        entityManager.flush();

        // 5. Reset MySQL auto-increment IDs for a fresh start
        entityManager.createNativeQuery("ALTER TABLE votes AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE candidates AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate();

        // 6. Unpublish results
        ElectionStatus status = getElectionStatus();
        status.setResultsPublished(false);
        electionStatusRepository.save(status);

        return "New election started successfully. All data has been reset.";
    }

    /**
     * Get the election results with percentage, position, and vote difference — only if published.
     */
    public List<ResultResponse> getResults() {
        if (!areResultsPublished()) {
            throw new RuntimeException("Results not yet released");
        }

        // 1. Get all candidates with their vote counts
        List<Candidate> candidates = candidateRepository.findAll();

        // Build a temporary list with candidate + voteCount, sorted by votes descending
        List<CandidateVoteData> candidateVotes = candidates.stream()
                .map(c -> new CandidateVoteData(
                        c.getId(),
                        c.getName(),
                        c.getParty(),
                        voteRepository.countByCandidateId(c.getId())
                ))
                .sorted(Comparator.comparingLong(CandidateVoteData::voteCount).reversed())
                .toList();

        // 2. Calculate total votes
        long totalVotes = candidateVotes.stream()
                .mapToLong(CandidateVoteData::voteCount)
                .sum();

        // 3. Build the response with position, percentage, vote difference, and status
        List<ResultResponse> results = new ArrayList<>();

        for (int i = 0; i < candidateVotes.size(); i++) {
            CandidateVoteData current = candidateVotes.get(i);

            int position = i + 1;

            // Voting percentage (avoid division by zero)
            double percentage = totalVotes > 0
                    ? Math.round((double) current.voteCount() / totalVotes * 10000.0) / 100.0
                    : 0.0;

            // Vote difference
            long voteDifference;
            if (i == 0) {
                voteDifference = candidateVotes.size() > 1
                        ? current.voteCount() - candidateVotes.get(1).voteCount()
                        : current.voteCount();
            } else {
                voteDifference = candidateVotes.get(i - 1).voteCount() - current.voteCount();
            }

            String statusLabel = getPositionStatus(position);

            results.add(ResultResponse.builder()
                    .position(position)
                    .candidateId(current.candidateId())
                    .candidateName(current.candidateName())
                    .party(current.party())
                    .voteCount(current.voteCount())
                    .votingPercentage(percentage)
                    .voteDifference(voteDifference)
                    .status(statusLabel)
                    .build());
        }

        return results;
    }

    private String getPositionStatus(int position) {
        return switch (position) {
            case 1 -> "Winner";
            case 2 -> "Runner-up";
            case 3 -> "3rd Place";
            default -> position + "th Place";
        };
    }

    private record CandidateVoteData(Long candidateId, String candidateName, String party, long voteCount) {
    }
}
