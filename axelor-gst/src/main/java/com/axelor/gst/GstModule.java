package com.axelor.gst;

import com.axelor.app.AxelorModule;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.gst.service.InvoiceLineServiceImpl;
import com.axelor.gst.service.InvoiceService;
import com.axelor.gst.service.InvoiceServiceImpl;
import com.axelor.gst.service.ProductService;
import com.axelor.gst.service.ProductServiceImpl;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(ProductService.class).to(ProductServiceImpl.class);
    bind(InvoiceService.class).to(InvoiceServiceImpl.class);
    bind(InvoiceLineService.class).to(InvoiceLineServiceImpl.class);
  }
}
