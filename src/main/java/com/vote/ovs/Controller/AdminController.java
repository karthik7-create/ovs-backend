package com.vote.ovs.Controller;

import com.vote.ovs.Dto.CandidateRequest;
import com.vote.ovs.Dto.CandidateResponse;
import com.vote.ovs.Service.CandidateService;
import com.vote.ovs.Service.ResultService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ResultService resultService;
    private final CandidateService candidateService;

    public AdminController(ResultService resultService, CandidateService candidateService) {
        this.resultService = resultService;
        this.candidateService = candidateService;
    }

    // ===== RESULTS MANAGEMENT =====

    // POST /api/admin/publish-results
    @PostMapping("/publish-results")
    public Map<String, String> publishResults() {
        return Map.of("message", resultService.publishResults());
    }

    // POST /api/admin/unpublish-results
    @PostMapping("/unpublish-results")
    public Map<String, String> unpublishResults() {
        return Map.of("message", resultService.unpublishResults());
    }

    // ===== CANDIDATE MANAGEMENT =====

    // GET /api/admin/candidates — list all candidates (admin view)
    @GetMapping("/candidates")
    public List<CandidateResponse> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    // POST /api/admin/candidates — add a new candidate
    @PostMapping("/candidates")
    public CandidateResponse addCandidate(@RequestBody CandidateRequest request) {
        return candidateService.addCandidate(request.getName(), request.getParty());
    }

    // DELETE /api/admin/candidates/{id} — remove a candidate
    @DeleteMapping("/candidates/{id}")
    public Map<String, String> removeCandidate(@PathVariable Long id) {
        return Map.of("message", candidateService.removeCandidate(id));
    }

    // ===== ELECTION MANAGEMENT =====

    // POST /api/admin/new-election — reset everything for a new election
    @PostMapping("/new-election")
    public Map<String, String> newElection() {
        return Map.of("message", resultService.resetElection());
    }
}

