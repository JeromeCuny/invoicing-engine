package com.jeromecuny.response;

import com.jeromecuny.common.Amount;

import java.time.LocalDate;
import java.util.Objects;

public class InvoiceResponse {

    private LocalDate marketDate;
    private Amount price;

    private double dailyRate;
    private int totalDays;

    public LocalDate getMarketDate() {
        return marketDate;
    }

    public InvoiceResponse setMarketDate(LocalDate marketDate) {
        this.marketDate = marketDate;
        return this;
    }

    public Amount getPrice() {
        return price;
    }

    public InvoiceResponse setPrice(Amount price) {
        this.price = price;
        return this;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public InvoiceResponse setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
        return this;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public InvoiceResponse setTotalDays(int totalDays) {
        this.totalDays = totalDays;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvoiceResponse response)) return false;
        return Double.compare(dailyRate, response.dailyRate) == 0 && totalDays == response.totalDays &&
                Objects.equals(marketDate, response.marketDate) && Objects.equals(price, response.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marketDate, price, dailyRate, totalDays);
    }
}
