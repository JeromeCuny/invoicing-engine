package com.jeromecuny.request.client;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public abstract class ClientImpl<T extends ClientImpl<T>> implements Client {

    @Pattern(regexp = "EKW\\d{9}", message = "Invalid reference")
    protected String reference;

    public String getReference() {
        return reference;
    }

    @SuppressWarnings("unchecked")
    public T setReference(String reference) {
        this.reference = reference;
        return (T) this;
    }
}
