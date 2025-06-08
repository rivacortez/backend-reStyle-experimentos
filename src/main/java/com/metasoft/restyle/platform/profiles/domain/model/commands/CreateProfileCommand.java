package com.metasoft.restyle.platform.profiles.domain.model.commands;

public record CreateProfileCommand(
        String email,
        String password,
        String typeUser,
        String firstName,
        String paternalSurname,
        String maternalSurname
) {}
