package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Product;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

  BigDecimal getGstRate(Product product);

  List<Integer> getAllProductIds();

  List<InvoiceLine> getInvoiceItems(List<Integer> list);

  String getProductIdsString(List<Integer> selectedProductsIds);
}
