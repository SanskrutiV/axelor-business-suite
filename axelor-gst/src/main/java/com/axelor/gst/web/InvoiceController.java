package com.axelor.gst.web;

import com.axelor.app.AppSettings;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.gst.service.InvoiceService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class InvoiceController {

  @Inject InvoiceService invoiceService;

  @Inject InvoiceLineService invoiceLineService;

  public void calculateInvoice(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    invoiceService.calculateInvoice(invoice);

    response.setValue("invoiceItems", invoice.getInvoiceLineList());
    response.setValue("exTaxTotal", invoice.getExTaxTotal());
    response.setValue("netIgst", invoice.getNetIgst());
    response.setValue("netSgst", invoice.getNetSgst());
    response.setValue("netCgst", invoice.getNetCgst());
    response.setValue("inTaxTotal", invoice.getInTaxTotal());
  }

  public void setInvoiceAttrs(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    invoiceService.setInvoiceAttrs(invoice);
    response.setValue("contactPartner", invoice.getContactPartner());
    response.setValue("address", invoice.getAddress());
    response.setValue("shippingAddress", invoice.getShippingAddress());
  }

  public void setInvoiceLineAttrs(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    if (invoice.getInvoiceLineList() != null) {
      for (InvoiceLine invoiceLine : invoice.getInvoiceLineList()) {
        invoiceLineService.calculateInvoiceItem(invoiceLine, invoice);
      }

      response.setValue("invoiceLineList", invoice.getInvoiceLineList());
    }
  }

  @SuppressWarnings("unchecked")
  public void setInvoiceLineList(ActionRequest request, ActionResponse response) {
    List<InvoiceLine> invoiceLineList = new ArrayList<InvoiceLine>();
    invoiceLineList = (List<InvoiceLine>) request.getContext().get("invoiceLineList");

    if (invoiceLineList != null) {
      for (InvoiceLine invoiceLine : invoiceLineList) {
        invoiceLineService.setInvoiceLine(invoiceLine);
        invoiceLineService.updateInvoiceItem(invoiceLine);
        TaxLine taxLine = invoiceLineService.getTextLine(invoiceLine);
        invoiceLine.setTaxLine(taxLine);
      }
      response.setValue("invoiceLineList", invoiceLineList);
    }
  }

  public void getImagePath(ActionRequest request, ActionResponse response) {
    String imagePath = AppSettings.get().getPath("file.upload.dir", "");
    imagePath = imagePath.endsWith(File.separator) ? imagePath : imagePath + File.separator;
    request.getContext().put("imagePath", imagePath);
  }
}
