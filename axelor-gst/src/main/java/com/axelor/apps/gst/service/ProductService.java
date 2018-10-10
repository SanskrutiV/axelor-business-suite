package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.InvoiceLine;
import java.util.List;

public interface ProductService {

  List<Integer> getAllProductIds();

  List<InvoiceLine> getInvoiceItems(List<Integer> list);

  String getProductIdsString(List<Integer> selectedProductsIds);
}
