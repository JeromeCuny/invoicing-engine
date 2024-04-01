package com.jeromecuny.service;

import com.jeromecuny.request.InvoiceRequest;
import com.jeromecuny.response.InvoiceResponse;

public interface InvoicingService {
    InvoiceResponse compute(InvoiceRequest request);
}
