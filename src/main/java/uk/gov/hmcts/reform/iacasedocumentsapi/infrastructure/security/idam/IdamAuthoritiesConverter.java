package uk.gov.hmcts.reform.iacasedocumentsapi.infrastructure.security.idam;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;

import feign.FeignException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.iacasedocumentsapi.infrastructure.clients.IdamApi;
import uk.gov.hmcts.reform.iacasedocumentsapi.infrastructure.clients.model.idam.UserInfo;

@Slf4j
@Component
public class IdamAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final String REGISTRATION_ID = "oidc";

    static final String TOKEN_NAME = "tokenName";

    private final IdamApi idamApi;

    public IdamAuthoritiesConverter(IdamApi idamApi) {
        this.idamApi = idamApi;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (jwt.hasClaim(TOKEN_NAME) && jwt.getClaim(TOKEN_NAME).equals(ACCESS_TOKEN)) {
            authorities.addAll(getUserRoles(jwt.getTokenValue()));
        }
        return authorities;
    }

    private List<GrantedAuthority> getUserRoles(String authorization) {

        try {

            UserInfo userInfo = idamApi.userInfo("Bearer " + authorization);

            return userInfo
                .getRoles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        } catch (FeignException e) {
            throw new IdentityManagerResponseException("Could not get user details from IDAM", e);
        }

    }

}
