package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PartnerAddress;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {

  @Override
  public void calculateInvoice(Invoice invoice) {
    BigDecimal exTaxTotal = BigDecimal.ZERO;
    BigDecimal netSgst = BigDecimal.ZERO;
    BigDecimal netCgst = BigDecimal.ZERO;
    BigDecimal netIgst = BigDecimal.ZERO;
    BigDecimal inTaxTotal = BigDecimal.ZERO;
    List<InvoiceLine> invoiceItems = invoice.getInvoiceLineList();

    if (invoiceItems != null) {
      for (InvoiceLine invoiceLine : invoiceItems) {
        exTaxTotal = exTaxTotal.add(invoiceLine.getExTaxTotal());
        netSgst = netSgst.add(invoiceLine.getSgst());
        netCgst = netCgst.add(invoiceLine.getCgst());
        netIgst = netIgst.add(invoiceLine.getIgst());
        inTaxTotal = inTaxTotal.add(invoiceLine.getInTaxTotal());
      }
    }
    invoice.setInTaxTotal(inTaxTotal);
    invoice.setExTaxTotal(exTaxTotal);
    invoice.setNetSgst(netSgst);
    invoice.setNetIgst(netIgst);
    invoice.setNetCgst(netCgst);
  }

  @Override
  public void setInvoiceAttrs(Invoice invoice) {
    Partner partner = invoice.getPartner();
    List<PartnerAddress> partnerAddressList = new ArrayList<PartnerAddress>();
    partnerAddressList = partner.getPartnerAddressList();
    PartnerAddress invoiceAddress = null;
    PartnerAddress shippingAddress = null;
    PartnerAddress defaultAddress = null;
    if (!partnerAddressList.isEmpty()) {
      for (PartnerAddress partnerAddress : partnerAddressList) {
        if (partnerAddress.getIsInvoicingAddr()) {
          invoiceAddress = partnerAddress;
        }
        if (partnerAddress.getIsDeliveryAddr()) {
          shippingAddress = partnerAddress;
        }
        if (partnerAddress.getIsDefaultAddr()) {
          defaultAddress = partnerAddress;
        }
      }
      if (invoiceAddress == null && defaultAddress != null) {
        invoiceAddress = defaultAddress;
      }
      if (shippingAddress == null && defaultAddress != null) {
        shippingAddress = defaultAddress;
      }
      if (invoice.getUseInvoiceAddressAsShippingAddress()) {
        shippingAddress = invoiceAddress;
      }
    }
    invoice.setAddress(invoiceAddress.getAddress());
    invoice.setShippingAddress(shippingAddress.getAddress());
  }
}
