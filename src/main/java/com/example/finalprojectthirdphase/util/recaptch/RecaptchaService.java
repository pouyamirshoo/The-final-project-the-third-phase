package com.example.finalprojectthirdphase.util.recaptch;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {
    private static final String GOOGLE_RECAPTCHA_ENDPOINT = "https://www.google.com/recaptcha/api/siteverify";

    public boolean validateCaptcha(String captchaResponse) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        String RECAPTCHA_SECRET = "6LdgnwYqAAAAAEwR8TFprzUaD_dbSgcjfJ6Thu_u";
        requestMap.add("secret", RECAPTCHA_SECRET);
        requestMap.add("response", captchaResponse);

        ReCaptchaResponseType apiResponse = restTemplate.postForObject(GOOGLE_RECAPTCHA_ENDPOINT, requestMap, ReCaptchaResponseType.class);
        if (apiResponse == null) {
            return false;
        }

        return Boolean.TRUE.equals(apiResponse.isSuccess());
    }
}
