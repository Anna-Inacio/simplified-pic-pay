package com.simplified_pic_pay.service.transaction;

import com.simplified_pic_pay.domain.user.User;
import com.simplified_pic_pay.exception.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class AuthorizationTransaction {

    @Autowired
    private RestTemplate restTemplate;

    public boolean authorizeTransaction(User sender, BigDecimal value) {
        String url = "https://util.devi.tools/api/v2/authorize";
        ResponseEntity<String> authorizationResponse = restTemplate.getForEntity(url, String.class);

        if (authorizationResponse.getBody() == null || !authorizationResponse.getBody().contains("success")) {
            //todo MELHORAR exception personalizada
            throw new AuthorizationException("Transaction unauthorized by external service");
        }
        return true;
    }
}
