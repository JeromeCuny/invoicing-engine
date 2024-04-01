package com.jeromecuny.controller;

import com.jeromecuny.request.InvoiceRequest;
import com.jeromecuny.response.InvoiceResponse;
import com.jeromecuny.service.InvoicingService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${invoicing.controller.base-path}" + InvoicingController.INVOICING_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class InvoicingController {

    static final String INVOICING_ENDPOINT = "/v1/invoices";

    private final InvoicingService invoicingService;

    public InvoicingController(InvoicingService invoicingService) {
        this.invoicingService = invoicingService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> compute(@RequestBody @Valid InvoiceRequest request) {
        final InvoiceResponse response = invoicingService.compute(request);
        return ResponseEntity.ok()
                .body(response);
    }
}
