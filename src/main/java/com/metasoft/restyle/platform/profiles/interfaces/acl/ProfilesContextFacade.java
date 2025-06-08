package com.metasoft.restyle.platform.profiles.interfaces.acl;

import com.metasoft.restyle.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.metasoft.restyle.platform.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.metasoft.restyle.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.metasoft.restyle.platform.profiles.domain.services.ProfileCommandService;
import com.metasoft.restyle.platform.profiles.domain.services.ProfileQueryService;
import org.springframework.stereotype.Service;

@Service
public class ProfilesContextFacade {
    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfilesContextFacade(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    public Long createProfile(String email, String password, String typeUser, String firstName, String paternalSurname, String maternalSurname) {
        var createProfileCommand = new CreateProfileCommand(email,password, typeUser, firstName, paternalSurname, maternalSurname);
        return profileCommandService.handle(createProfileCommand);
    }

    public Long getProfileIdByEmail(String email) {
        var getProfileByEmailQuery = new GetProfileByEmailQuery(new EmailAddress(email));
        var profile = profileQueryService.handle(getProfileByEmailQuery);
        if (profile.isEmpty()) return 0L;
        return profile.get().getId();
    }
}