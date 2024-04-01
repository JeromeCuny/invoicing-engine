package com.jeromecuny.request.client;

public interface ClientVisitor<T> {

    T visit(Company client);
    T visit(Individual client);
}
