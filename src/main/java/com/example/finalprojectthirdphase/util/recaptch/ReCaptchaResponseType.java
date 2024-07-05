package com.example.finalprojectthirdphase.util.recaptch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReCaptchaResponseType {

    private boolean success;
    private String challenge_ts;
    private String hostname;

    public boolean isSuccess() {
        return success;
    }
}
