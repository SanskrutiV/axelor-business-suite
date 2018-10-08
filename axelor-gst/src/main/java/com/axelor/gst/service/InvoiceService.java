package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.exception.AxelorException;

public interface InvoiceService {

  Invoice calculateInvoice(Invoice invoice) throws AxelorException;

  void setInvoiceAttrs(Invoice invoice);
}
