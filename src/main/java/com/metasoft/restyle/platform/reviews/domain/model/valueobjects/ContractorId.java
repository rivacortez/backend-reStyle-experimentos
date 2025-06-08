package com.metasoft.restyle.platform.reviews.domain.model.valueobjects;

public record ContractorId(Integer contractorId) {

    // validate that the contractorId is equal to or greater than 1
    public ContractorId {
        if (contractorId < 1) {
            throw new IllegalArgumentException("ContractorId must be greater than or equal to 1");
        }
    }
}
