package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;

public interface InvoiceService extends com.axelor.apps.account.service.invoice.InvoiceService {

  void setInvoiceAttrs(Invoice invoice);
}
