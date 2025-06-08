package com.metasoft.restyle.platform.profiles.domain.model.aggregates;

import com.metasoft.restyle.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.metasoft.restyle.platform.profiles.domain.model.valueobjects.PersonName;
import com.metasoft.restyle.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private EmailAddress email;

    private String password;

    private String type;

    @Embedded
    private PersonName name;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public Profile(String email, String password, String type, String firstName, String paternalSurname, String maternalSurname) {
        this.email = new EmailAddress(email);
        this.password = password;
        this.type = type;
        this.name = new PersonName(firstName, paternalSurname, maternalSurname);
    }

    public Profile() {

    }

    public void updateName(String firstName, String paternalSurname, String maternalSurname) {
        this.name = new PersonName(firstName, paternalSurname, maternalSurname);
    }

    public String getFullName() {
        return this.name.getFullName();
    }

    public String getEmailAddress() {
        return this.email.address();
    }

}
