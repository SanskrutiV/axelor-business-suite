package com.axelor.gst.web;

import com.axelor.app.AppSettings;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Product;
import com.axelor.gst.service.ProductService;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ProductController {
  @Inject ProductService productService;

  public void setProductGstRate(ActionRequest request, ActionResponse response) {
    Product product = request.getContext().asType(Product.class);
    BigDecimal gstRate = productService.getGstRate(product);
    response.setValue("gstRate", gstRate);
  }

  public void getPrintIds(ActionRequest request, ActionResponse response) {
    @SuppressWarnings("unchecked")
    List<Integer> selectedProductsIds =
        (List<Integer>) request.getContext().getOrDefault("_ids", new ArrayList<Integer>());
    String selectedProducts = null;
    if (selectedProductsIds.isEmpty()) {
      selectedProductsIds = productService.getAllProductIds();
    }
    selectedProducts = productService.getProductIdsString(selectedProductsIds);
    request.getContext().put("selectedProducts", selectedProducts);

    String path = AppSettings.get().getPath("file.upload.dir", "");
    path = path.endsWith(File.separator) ? path : path + File.separator;
    request.getContext().put("path", path);
  }

  public void setSelectedProductsId(ActionRequest request, ActionResponse response) {
    @SuppressWarnings("unchecked")
    List<Integer> selectedProductIds =
        (List<Integer>) request.getContext().getOrDefault("_ids", new ArrayList<Integer>());
    List<InvoiceLine> invoiceItems = new ArrayList<InvoiceLine>();
    if (!selectedProductIds.isEmpty()) {
      invoiceItems = productService.getInvoiceItems(selectedProductIds);
      response.setView(
          ActionView.define("Invoice")
              .model(Invoice.class.getName())
              .add("form", "gst-invoice-form")
              .context("invoiceLineList", invoiceItems)
              .context("_operationTypeSelect", 3)
              .context("todayDate", "__config__.app.getTodayDate()")
              .map());
    } else {
      response.setNotify("Select Product to Create Invoice");
    }
  }
}
