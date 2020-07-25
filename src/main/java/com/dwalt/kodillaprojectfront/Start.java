package com.dwalt.kodillaprojectfront;

import com.dwalt.kodillaprojectfront.fronclient.FrontEndClient;
import org.springframework.stereotype.Component;

@Component
public class Start {
    public Start(FrontEndClient frontEndClient) {
        System.out.println(frontEndClient.getCurrenciesRates());
    }
}
