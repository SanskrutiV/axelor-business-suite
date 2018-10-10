package com.axelor.apps.gst.web;

import com.axelor.app.AppSettings;
import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.gst.service.ProductService;
import com.axelor.exception.AxelorException;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ProductController {
  @Inject ProductService productService;

  public void printProducts(ActionRequest request, ActionResponse response) throws AxelorException {
    @SuppressWarnings("unchecked")
    List<Integer> selectedProductsIds =
        (List<Integer>) request.getContext().getOrDefault("_ids", new ArrayList<Integer>());
    String selectedProducts = null;
    if (selectedProductsIds.isEmpty()) {
      selectedProductsIds = productService.getAllProductIds();
    }
    selectedProducts = productService.getProductIdsString(selectedProductsIds);
    String path = AppSettings.get().getPath("file.upload.dir", "");
    path = path.endsWith(File.separator) ? path : path + File.separator;
    String name = "Products";
    String fileLink =
        ReportFactory.createReport(IReport.PRODUCT, name + "-${date}")
            .addParam("selectedProducts", selectedProducts)
            .addParam("path", path)
            .generate()
            .getFileLink();
    response.setView(ActionView.define(name).add("html", fileLink).map());
  }

  public void createInvoice(ActionRequest request, ActionResponse response) {
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
