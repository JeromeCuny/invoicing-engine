package com.jeromecuny.request.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = ClientConstants.COMPANY, value = Company.class),
        @JsonSubTypes.Type(name = ClientConstants.INDIVIDUAL, value = Individual.class)
})
public interface Client {

    @JsonIgnore
    ClientType type();

    <R> R accept(ClientVisitor<R> visitor);
}
