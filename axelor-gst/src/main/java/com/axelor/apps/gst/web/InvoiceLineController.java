package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.exception.AxelorException;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.ibm.icu.math.BigDecimal;
import javax.inject.Inject;

public class InvoiceLineController {

  @Inject InvoiceLineService invoiceLineService;

  @Inject TaxLineRepository taxLineRepo;

  public void setInvoiceItem(ActionRequest request, ActionResponse response)
      throws AxelorException {
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    Invoice invoice = request.getContext().getParent().asType(Invoice.class);
    if (invoiceLine.getProduct() != null) {
      // invoiceLineService.updateInvoiceItem(invoiceLine);
      if (invoice.getAddress() != null) {
        invoiceLineService.calculateInvoiceItem(invoiceLine, invoice);
        response.setValue("igst", invoiceLine.getIgst());
        response.setValue("cgst", invoiceLine.getCgst());
        response.setValue("sgst", invoiceLine.getSgst());
      }
      // response.setValue("gstRate", invoiceLine.getGstRate());
    } else {
      response.setValue("igst", BigDecimal.ZERO);
      response.setValue("cgst", BigDecimal.ZERO);
      response.setValue("sgst", BigDecimal.ZERO);
      response.setValue("gstRate", BigDecimal.ZERO);
    }
    response.setValue("hsbn", invoiceLine.getHsbn());
  }

  public void setTaxLine(ActionRequest request, ActionResponse response) {
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    TaxLine taxLine = invoiceLineService.getTextLine(invoiceLine);
    if (taxLine != null) {
      response.setValue("taxLine", taxLine);
    }
  }
}
