package com.jeromecuny.service;

import com.jeromecuny.common.Amount;
import com.jeromecuny.request.client.Client;
import com.jeromecuny.request.client.ClientVisitor;
import com.jeromecuny.request.client.Company;
import com.jeromecuny.request.client.Individual;
import com.jeromecuny.request.EnergyType;
import com.jeromecuny.request.InvoiceRequest;
import com.jeromecuny.response.InvoiceResponse;

import java.util.Currency;

import static com.jeromecuny.request.EnergyType.ELECTRICITY;

public class InvoicingServiceImpl implements InvoicingService {
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final double TOP_COMPANY_MIN_REVENUE_EUR = 1000000;
    private static final double INDIVIDUAL_ELECTRICITY_COST_EUR = 0.133;
    private static final double INDIVIDUAL_GAZ_COST_EUR = 0.108;
    private static final double TOP_COMPANY_ELECTRICITY_COST_EUR = 0.110;
    private static final double TOP_COMPANY_GAZ_COST_EUR = 0.123;
    private static final double COMPANY_ELECTRICITY_COST_EUR = 0.112;
    private static final double COMPANY_GAZ_COST_EUR = 0.117;

    @Override
    public InvoiceResponse compute(InvoiceRequest request) {
        final int totalDays = request.getMarketDate().lengthOfMonth();
        final double quantity = totalDays * request.getDailyRate();
        return new InvoiceResponse()
                .setMarketDate(request.getMarketDate())
                .setPrice(price(request.getClient(), quantity, request.getEnergyType()))
                .setDailyRate(request.getDailyRate())
                .setTotalDays(totalDays);
    }

    private Amount price(Client client, double quantity, EnergyType type) {
        return client.accept(new ClientVisitor<Amount>() {
            @Override
            public Amount visit(Company client) {
                return new Amount()
                        .setCurrency(EUR)
                        .setValue(quantity * companyCost(type, client.getRevenue()));
            }

            @Override
            public Amount visit(Individual client) {
                return new Amount()
                        .setCurrency(EUR)
                        .setValue(quantity * individualCost(type));
            }
        });
    }

    private static double companyCost(EnergyType type, double revenue) {
        final boolean electricity = ELECTRICITY.equals(type);
        if (revenue >= TOP_COMPANY_MIN_REVENUE_EUR) return electricity ? TOP_COMPANY_ELECTRICITY_COST_EUR : TOP_COMPANY_GAZ_COST_EUR;
        return electricity ? COMPANY_ELECTRICITY_COST_EUR : COMPANY_GAZ_COST_EUR;
    }

    private static double individualCost(EnergyType type) {
        return ELECTRICITY.equals(type)
                ? INDIVIDUAL_ELECTRICITY_COST_EUR
                : INDIVIDUAL_GAZ_COST_EUR;
    }
}
