package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.exception.AxelorException;

public interface InvoiceLineService {

  void calculateInvoiceItem(InvoiceLine invoiceLine, Invoice invoice) throws AxelorException;

  void updateInvoiceItem(InvoiceLine invoiceLine);

  void setInvoiceLine(InvoiceLine invoiceLine);

  TaxLine getTextLine(InvoiceLine invoiceLine);
}
