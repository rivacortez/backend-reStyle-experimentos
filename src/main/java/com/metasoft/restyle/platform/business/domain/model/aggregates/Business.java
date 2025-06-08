package com.metasoft.restyle.platform.business.domain.model.aggregates;

import com.metasoft.restyle.platform.business.domain.model.commands.CreateBusinessCommand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Business  extends AbstractAggregateRoot<Business> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String image;

    @Column(nullable = false)
    private String expertise;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer remodelerId;

    protected Business(){}

    public Business(CreateBusinessCommand command){
        this.name = command.name();
        this.image = command.image();
        this.expertise = command.expertise();
        this.address = command.address();
        this.city = command.city();
        this.description = command.description();
        this.remodelerId = command.remodelerId();
    }
}
