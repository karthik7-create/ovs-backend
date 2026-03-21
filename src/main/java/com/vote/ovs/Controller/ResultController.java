package com.vote.ovs.Controller;

import com.vote.ovs.Dto.ResultResponse;
import com.vote.ovs.Service.ResultService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    // GET /api/results → Users can see results (only if admin has published them)
    @GetMapping
    public List<ResultResponse> getResults() {
        return resultService.getResults();
    }

    // GET /api/results/status → Check if results are published
    @GetMapping("/status")
    public java.util.Map<String, Boolean> getResultsStatus() {
        return java.util.Map.of("published", resultService.areResultsPublished());
    }
}
