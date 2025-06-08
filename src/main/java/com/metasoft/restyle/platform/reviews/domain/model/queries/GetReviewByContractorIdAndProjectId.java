package com.metasoft.restyle.platform.reviews.domain.model.queries;

import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ContractorId;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ProjectId;

public record GetReviewByContractorIdAndProjectId(ContractorId contractorId, ProjectId projectId) {

}
