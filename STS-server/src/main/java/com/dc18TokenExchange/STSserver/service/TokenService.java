package com.dc18TokenExchange.STSserver.service;


import com.dc18TokenExchange.STSserver.TokenGenerator;
import com.dc18TokenExchange.STSserver.TokenValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;
import java.util.Map;


@Service
public class TokenService {

    @Value("${approved.idp.issuer}")
    private String issuer;

    @Value("${approved.idp.jwk}")
    private String jwkUrl;

    @Value("${approved.aud}")
    private String aud;

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    TokenValidation tokenValidation;

    @Autowired
    WorkplaceService workplaceService;

    @Autowired
    UserInfoService userInfoService;


    public ResponseEntity<String> generateToken(String accessToken) throws Exception {
        //Recieves and prints the access token.
        String atSubstring = "access_token=";
        accessToken = accessToken.substring(atSubstring.length());

        //Verifies token signature
        String kid = JwtHelper.headers(accessToken).get("kid");
        Jwt tokenDecoded = JwtHelper.decodeAndVerify(accessToken, tokenValidation.verifyTokenSignature(kid, jwkUrl));

        //Verifies token claims
        Map<String, Object> authInfo = new ObjectMapper()
                .readValue(tokenDecoded.getClaims(), Map.class);
        tokenValidation.verifyTokenClaims(authInfo, issuer, aud);

        //Token to Map of diff parts
        Map<String, Object> headerMap = tokenGenerator.getTokenParts(accessToken, 0);
        Map<String, Object> bodyMap = tokenGenerator.getTokenParts(accessToken, 1);

        //Gets user ID (personnummer) and checks the authentication resource to find out where he/she works and what his/her name is.
        String userPid = bodyMap.get("pid").toString();
        Long userPidLong = Long.parseLong(userPid);

        String userWorkplaceName = workplaceService.getDistinctWorkplaceNameByUserIdAsString(userPidLong);
        Long userWorkplaceNum = workplaceService.getDistinctWorkplaceNumByUserIdAsString(userPidLong);

        String firstName = userInfoService.getUserInfoFirstNameAsString(userPidLong);
        String lastName = userInfoService.getUserInfoLastNameAsString(userPidLong);

        //Generates the token with workplace name and number
        String tokenNew = tokenGenerator.getNewToken(headerMap, bodyMap, userWorkplaceName, userWorkplaceNum, firstName, lastName);

        return new ResponseEntity<>(tokenNew, HttpStatus.OK);
    }
}