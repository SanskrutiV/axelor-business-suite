package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.apps.gst.service.InvoiceGstServiceImpl;
import com.axelor.apps.gst.service.InvoiceLineGstServiceImpl;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.apps.gst.service.InvoiceService;
import com.axelor.apps.gst.service.ProductService;
import com.axelor.apps.gst.service.ProductServiceImpl;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(ProductService.class).to(ProductServiceImpl.class);
    bind(InvoiceService.class).to(InvoiceGstServiceImpl.class);
    bind(InvoiceServiceProjectImpl.class).to(InvoiceGstServiceImpl.class);
    bind(InvoiceLineService.class).to(InvoiceLineGstServiceImpl.class);
    bind(InvoiceLineSupplychainService.class).to(InvoiceLineGstServiceImpl.class);
  }
}
