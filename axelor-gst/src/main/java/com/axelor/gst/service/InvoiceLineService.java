package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;

public interface InvoiceLineService {

  void calculateInvoiceItem(InvoiceLine invoiceLine, Invoice invoice);

  void updateInvoiceItem(InvoiceLine invoiceLine);

  void setInvoiceLine(InvoiceLine invoiceLine);

  TaxLine getTextLine(InvoiceLine invoiceLine);
}
