package com.jeromecuny.request.client;

import static com.jeromecuny.request.client.ClientType.COMPANY;

public class Company extends ClientImpl<Company> {

    private int siret;
    private LegalName legalName;
    private double revenue;

    public int getSiret() {
        return siret;
    }

    public Company setSiret(int siret) {
        this.siret = siret;
        return this;
    }

    public LegalName getLegalName() {
        return legalName;
    }

    public Company setLegalName(LegalName legalName) {
        this.legalName = legalName;
        return this;
    }

    public double getRevenue() {
        return revenue;
    }

    public Company setRevenue(double revenue) {
        this.revenue = revenue;
        return this;
    }

    @Override
    public ClientType type() {
        return COMPANY;
    }

    @Override
    public <R> R accept(ClientVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
