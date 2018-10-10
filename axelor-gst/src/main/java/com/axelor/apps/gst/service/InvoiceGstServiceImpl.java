package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InvoiceGstServiceImpl extends InvoiceServiceProjectImpl implements InvoiceService {

  @Inject
  public InvoiceGstServiceImpl(
      ValidateFactory validateFactory,
      VentilateFactory ventilateFactory,
      CancelFactory cancelFactory,
      AlarmEngineService<Invoice> alarmEngineService,
      InvoiceRepository invoiceRepo,
      AppAccountService appAccountService) {
    super(
        validateFactory,
        ventilateFactory,
        cancelFactory,
        alarmEngineService,
        invoiceRepo,
        appAccountService);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Invoice compute(Invoice invoice) throws AxelorException {
    BigDecimal netSgst = BigDecimal.ZERO;
    BigDecimal netCgst = BigDecimal.ZERO;
    BigDecimal netIgst = BigDecimal.ZERO;
    List<InvoiceLine> invoiceItems = invoice.getInvoiceLineList();
    if (invoice.getPartner() != null && invoiceItems != null) {
      invoice = super.compute(invoice);
    }

    if (invoiceItems != null) {
      for (InvoiceLine invoiceLine : invoiceItems) {
        netSgst = netSgst.add(invoiceLine.getSgst());
        netCgst = netCgst.add(invoiceLine.getCgst());
        netIgst = netIgst.add(invoiceLine.getIgst());
      }
    }
    invoice.setNetSgst(netSgst);
    invoice.setNetIgst(netIgst);
    invoice.setNetCgst(netCgst);

    return invoice;
  }

  public void setInvoiceAttrs(Invoice invoice) {
    Partner partner = invoice.getPartner();

    List<PartnerAddress> partnerAddressList = new ArrayList<PartnerAddress>();
    partnerAddressList = partner.getPartnerAddressList();
    PartnerAddress shippingAddress = null;
    if (!partnerAddressList.isEmpty()) {
      for (PartnerAddress partnerAddress : partnerAddressList) {
        if (invoice.getUseInvoiceAddressAsShippingAddress()
            && partnerAddress.getIsInvoicingAddr()) {
          shippingAddress = partnerAddress;
        } else {
          if (partnerAddress.getIsDeliveryAddr()) {
            shippingAddress = partnerAddress;
          }
        }
      }
    }
    invoice.setShippingAddress(shippingAddress.getAddress());
  }
}
