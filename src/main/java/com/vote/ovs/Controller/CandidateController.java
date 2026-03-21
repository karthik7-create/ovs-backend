package com.vote.ovs.Controller;

import com.vote.ovs.Dto.CandidateResponse;
import com.vote.ovs.Entity.Candidate;
import com.vote.ovs.Service.CandidateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/candidates")
public class CandidateController {
    private final CandidateService candidateService;
    public CandidateController (CandidateService candidateService)
    {
        this.candidateService=candidateService;
    }

    @GetMapping
    public List<CandidateResponse> getAllCandidates() {
        return candidateService.getAllCandidates();
    }
}
