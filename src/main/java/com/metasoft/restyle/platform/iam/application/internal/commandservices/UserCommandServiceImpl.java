package com.metasoft.restyle.platform.iam.application.internal.commandservices;

import com.metasoft.restyle.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.metasoft.restyle.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.commands.SignInCommand;
import com.metasoft.restyle.platform.iam.domain.model.commands.SignUpCommand;
import com.metasoft.restyle.platform.iam.domain.model.commands.UpdateUserCommand;
import com.metasoft.restyle.platform.iam.domain.services.UserCommandService;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService{

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        if(userRepository.existsByUsername(command.username()))
            throw new RuntimeException("Username already exists");

        var roles = command.roles().stream().map(role -> roleRepository.findByName(role.getName())
                .orElseThrow(() -> new RuntimeException("Role not found"))).toList();
        var user = new User(command.username(), hashingService.encode(command.password()), roles, command.email(), command.firstName(), command.paternalSurname(), command.maternalSurname(), command.description(), command.phone(), command.image());
        userRepository.save(user);
        return userRepository.findByUsername(command.username());
    }

    @Override
    public Optional<User> hadle(UpdateUserCommand command) {
        var user = userRepository.findById(command.id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.updateUserInfo(command.email(), command.description(), command.phone(), command.image());

        userRepository.save(user);
        return userRepository.findById(command.id());
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!hashingService.matches(command.password(), user.getPassword()))
            throw new RuntimeException("Invalid password");
        var token = tokenService.generateToken(user.getUsername());
        return Optional.of(new ImmutablePair<>(user, token));
    }
}
