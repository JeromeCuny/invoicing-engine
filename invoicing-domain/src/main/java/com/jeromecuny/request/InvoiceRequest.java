package com.jeromecuny.request;

import com.jeromecuny.request.client.Client;
import jakarta.validation.Valid;

import java.time.LocalDate;

public class InvoiceRequest {

    private LocalDate marketDate;
    @Valid
    private Client client;
    private Double dailyRate;
    private EnergyType energyType;

    public LocalDate getMarketDate() {
        return marketDate;
    }

    public InvoiceRequest setMarketDate(LocalDate marketDate) {
        this.marketDate = marketDate;
        return this;
    }

    public Client getClient() {
        return client;
    }

    public InvoiceRequest setClient(Client client) {
        this.client = client;
        return this;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public InvoiceRequest setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
        return this;
    }

    public EnergyType getEnergyType() {
        return energyType;
    }

    public InvoiceRequest setEnergyType(EnergyType energyType) {
        this.energyType = energyType;
        return this;
    }
}
