package nextstep.oauth2.userinfo;

import nextstep.oauth2.userinfo.OAuth2UserRequest;

public interface OAuth2UserService {
    OAuth2User loadUser(OAuth2UserRequest userRequest);
}
