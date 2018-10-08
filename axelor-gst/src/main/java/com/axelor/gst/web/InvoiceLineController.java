package com.axelor.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.ibm.icu.math.BigDecimal;
import javax.inject.Inject;

public class InvoiceLineController {

  @Inject InvoiceLineService invoiceLineService;

  @Inject TaxLineRepository taxLineRepo;

  public void setInvoiceItem(ActionRequest request, ActionResponse response) {
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    Invoice invoice = request.getContext().getParent().asType(Invoice.class);
    if (invoiceLine.getProduct() != null) {
      invoiceLineService.updateInvoiceItem(invoiceLine);
      if (invoice.getAddress() != null) {
        invoiceLineService.calculateInvoiceItem(invoiceLine, invoice);
        response.setValue("inTaxTotal", invoiceLine.getInTaxTotal());
        response.setValue("igst", invoiceLine.getIgst());
        response.setValue("cgst", invoiceLine.getCgst());
        response.setValue("sgst", invoiceLine.getSgst());
        response.setValue("hsbn", invoiceLine.getHsbn());
      }
      response.setValue("exTaxTotal", invoiceLine.getExTaxTotal());
      response.setValue("gstRate", invoiceLine.getGstRate());
    } else {
      response.setValue("inTaxTotal", BigDecimal.ZERO);
      response.setValue("igst", BigDecimal.ZERO);
      response.setValue("cgst", BigDecimal.ZERO);
      response.setValue("sgst", BigDecimal.ZERO);
      response.setValue("exTaxTotal", BigDecimal.ZERO);
      response.setValue("gstRate", BigDecimal.ZERO);
      response.setValue("hsbn", invoiceLine.getHsbn());
    }
  }

  public void setTaxLine(ActionRequest request, ActionResponse response) {
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    TaxLine taxLine = invoiceLineService.getTextLine(invoiceLine);
    if (taxLine != null) {
      response.setValue("taxLine", taxLine);
    }
  }
}
