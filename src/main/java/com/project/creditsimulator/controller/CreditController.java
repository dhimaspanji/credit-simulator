package com.project.creditsimulator.controller;

import com.project.creditsimulator.service.CreditService;

public class CreditController {
    private final CreditService creditService;

    public CreditController() {
        this.creditService = new CreditService();
    }

    public void loadFromWebService() {
        creditService.loadFromWebService();
    }

    public void processFile(String filename) {
        creditService.processFile(filename);
    }

}
