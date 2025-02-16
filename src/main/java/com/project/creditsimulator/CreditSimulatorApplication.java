package com.project.creditsimulator;

import com.project.creditsimulator.service.CreditService;

public class CreditSimulatorApplication {
    public static void main(String[] args) {
        CreditService creditService = new CreditService();
        if (args.length > 0) {
            creditService.processFile(args[0]);
        } else {
            creditService.loadFromWebService();
        }
    }
}
