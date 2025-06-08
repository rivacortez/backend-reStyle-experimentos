package com.metasoft.restyle.platform.information.profiles.domain.model.aggregates;

import com.metasoft.restyle.platform.profiles.domain.model.aggregates.Profile;
import com.metasoft.restyle.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Remodeler extends AuditableAbstractAggregateRoot<Remodeler> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Profile profile;

    private String phone;
    private String description;
    private String subscription;

    public Remodeler() {}

    public Remodeler(String description, String phone, String subscription) {
        this.description = description;
        this.subscription = subscription;
        this.phone = phone;
    }

    public Remodeler(String description, String phone) {}

}
