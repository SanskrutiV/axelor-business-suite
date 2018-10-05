package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;

public interface InvoiceService {

  void calculateInvoice(Invoice invoice);

  void setInvoiceAttrs(Invoice invoice);
}
