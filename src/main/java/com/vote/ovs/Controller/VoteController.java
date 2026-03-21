package com.vote.ovs.Controller;

import com.vote.ovs.Dto.VoteRequest;
import com.vote.ovs.Service.VoteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vote")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public Map<String, String> vote(@RequestBody VoteRequest request,
                       Authentication authentication) {

        String username = authentication.getName(); // from JWT

        return Map.of("message", voteService.castVote(username, request.getCandidateId()));
    }
}