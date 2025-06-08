package com.metasoft.restyle.platform.iam.infrastructure.hashing.bcrypt;

import com.metasoft.restyle.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {

	String encode(String rawPassword);

	boolean matches(String rawPassword, String encodedPassword);
}
