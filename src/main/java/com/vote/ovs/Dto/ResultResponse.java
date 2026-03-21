package com.vote.ovs.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {

    private int position;           // 1st, 2nd, 3rd...
    private Long candidateId;
    private String candidateName;
    private String party;
    private long voteCount;
    private double votingPercentage; // e.g. 45.50
    private long voteDifference;     // margin from the candidate ranked above
    private String status;           // "Winner", "Runner-up", "3rd Place", etc.
}
