package com.jeromecuny.request.client;

import static com.jeromecuny.request.client.ClientType.INDIVIDUAL;

public class Individual extends ClientImpl<Individual> {

    private Civility civility;
    private String firstName;
    private String lastName;

    public Civility getCivility() {
        return civility;
    }

    public Individual setCivility(Civility civility) {
        this.civility = civility;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Individual setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Individual setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public ClientType type() {
        return INDIVIDUAL;
    }

    @Override
    public <R> R accept(ClientVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
