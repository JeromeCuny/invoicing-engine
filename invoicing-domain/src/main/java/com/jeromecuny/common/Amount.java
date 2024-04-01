package com.jeromecuny.common;

import java.util.Currency;
import java.util.Objects;

public class Amount {

    private Currency currency;
    private double value;

    public Currency getCurrency() {
        return currency;
    }

    public Amount setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public double getValue() {
        return value;
    }

    public Amount setValue(double value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Amount amount)) return false;
        return Double.compare(value, amount.value) == 0 && Objects.equals(currency, amount.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currency) + Objects.hashCode(value);
    }
}
