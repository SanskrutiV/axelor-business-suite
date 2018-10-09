package com.axelor.apps.gst.web;

import com.axelor.app.AppSettings;
import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.InvoiceLineTax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.apps.gst.service.InvoiceService;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.exception.AxelorException;
import com.axelor.exception.service.TraceBackService;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class InvoiceController {

  @Inject InvoiceService invoiceService;

  @Inject InvoiceLineService invoiceLineService;

  public void calculateInvoice(ActionRequest request, ActionResponse response)
      throws AxelorException {
    Invoice invoice = request.getContext().asType(Invoice.class);
    try {
      invoice = invoiceService.compute(invoice);
      response.setValues(invoice);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void setInvoiceAttrs(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    invoiceService.setInvoiceAttrs(invoice);
    response.setValue("shippingAddress", invoice.getShippingAddress());
  }

  public void setInvoiceLineAttrs(ActionRequest request, ActionResponse response)
      throws AxelorException {
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
    List<InvoiceLineTax> invoiceLineTaxList = new ArrayList<InvoiceLineTax>();
    InvoiceLineTax invoiceLineTax = new InvoiceLineTax();
    TaxLine taxLine = null;

    if (invoiceLineList != null) {
      for (InvoiceLine invoiceLine : invoiceLineList) {
        invoiceLineService.setInvoiceLine(invoiceLine);
        invoiceLineService.updateInvoiceItem(invoiceLine);
        taxLine = invoiceLineService.getTextLine(invoiceLine);
        invoiceLine.setTaxLine(taxLine);
        invoiceLineTax.setTaxLine(taxLine);
        invoiceLineTaxList.add(invoiceLineTax);
      }
      response.setValue("invoiceLineList", invoiceLineList);
      response.setValue("invoiceLineTaxList", invoiceLineTaxList);
    }
  }

  public void getImagePath(ActionRequest request, ActionResponse response) {
    String imagePath = AppSettings.get().getPath("file.upload.dir", "");
    imagePath = imagePath.endsWith(File.separator) ? imagePath : imagePath + File.separator;
    request.getContext().put("imagePath", imagePath);
  }

  public void printInvoice(ActionRequest request, ActionResponse response) throws AxelorException {
    Invoice invoice = request.getContext().asType(Invoice.class);
    String name = "Invoice";
    String fileLink =
        ReportFactory.createReport(IReport.INVOICE, name + "-${date}")
            .addParam("InvoiceId", invoice.getId())
            .addParam("Locale", ReportSettings.getPrintingLocale(null))
            .generate()
            .getFileLink();

    // logger.debug("Printing " + name);

    response.setView(ActionView.define(name).add("html", fileLink).map());
  }
}
