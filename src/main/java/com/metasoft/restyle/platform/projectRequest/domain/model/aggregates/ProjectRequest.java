package com.metasoft.restyle.platform.projectRequest.domain.model.aggregates;

import com.metasoft.restyle.platform.projectRequest.domain.model.commands.CreateProjectRequestCommand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ProjectRequest extends AbstractAggregateRoot<ProjectRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer businessId;

    @Column(nullable = false)
    private Integer contractorId;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Date deadlineDate;

    @Column(nullable = false)
    private Integer budget;

    @Column(nullable = false)
    private Integer rooms;

    @Column(nullable = false)
    private String summary;

    protected ProjectRequest(){}

    public ProjectRequest(CreateProjectRequestCommand command){
        this.name = command.name();
        this.businessId = command.businessId();
        this.contractorId = command.contractorId();
        this.surname = command.surname();
        this.email = command.email();
        this.phone = command.phone();
        this.address = command.address();
        this.city = command.city();
        this.deadlineDate = command.deadlineDate();
        this.budget = command.budget();
        this.rooms = command.rooms();
        this.summary = command.summary();
    }
}
