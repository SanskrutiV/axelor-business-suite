package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.tax.AccountManagementService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;

public class InvoiceLineServiceImpl
    extends com.axelor.apps.account.service.invoice.InvoiceLineServiceImpl
    implements InvoiceLineService {

  @Inject
  public InvoiceLineServiceImpl(
      AccountManagementService accountManagementService,
      CurrencyService currencyService,
      PriceListService priceListService,
      AppAccountService appAccountService,
      AnalyticMoveLineService analyticMoveLineService,
      AccountManagementAccountService accountManagementAccountService) {
    super(
        accountManagementService,
        currencyService,
        priceListService,
        appAccountService,
        analyticMoveLineService,
        accountManagementAccountService);
    // TODO Auto-generated constructor stub
  }

  @Inject TaxLineRepository taxLineRepo;

  @Override
  public void calculateInvoiceItem(InvoiceLine invoiceLine, Invoice invoice)
      throws AxelorException {
    Product product = invoiceLine.getProduct();
    BigDecimal exTaxTotal = invoiceLine.getExTaxTotal();
    BigDecimal sgst = BigDecimal.ZERO;
    BigDecimal cgst = BigDecimal.ZERO;
    BigDecimal igst = BigDecimal.ZERO;
    BigDecimal inTaxTotal = BigDecimal.ZERO;
    BigDecimal gstRate = product.getGstRate();
    String hsbn = product.getHsbn();
    exTaxTotal = invoiceLine.getPrice().multiply(invoiceLine.getQty());
    invoiceLine.setExTaxTotal(exTaxTotal.setScale(2));
    Company company = invoice.getCompany();
    if (company == null) {
      return;
    }
    Address companyAddress = company.getAddress();
    Address invoiceAddress = invoice.getAddress();
    if (invoiceAddress != null && companyAddress != null) {
      if (gstRate != BigDecimal.ZERO && companyAddress.getState() != null) {

        if (companyAddress.getState().equals(invoiceAddress.getState())) {
          sgst = exTaxTotal.multiply(gstRate).divide(new BigDecimal(2));
          cgst = exTaxTotal.multiply(gstRate).divide(new BigDecimal(2));
          inTaxTotal = sgst.add(cgst).add(exTaxTotal);
          igst = new BigDecimal(0);
        } else {

          igst = exTaxTotal.multiply(gstRate);
          sgst = new BigDecimal(0);
          cgst = new BigDecimal(0);
          inTaxTotal = igst.add(exTaxTotal);
        }
      }

      invoiceLine.setHsbn(hsbn);
      invoiceLine.setCgst(cgst);
      invoiceLine.setIgst(igst);
      invoiceLine.setSgst(sgst);
      invoiceLine.setInTaxTotal(inTaxTotal);
      invoiceLine.setCompanyExTaxTotal(getCompanyExTaxTotal(exTaxTotal, invoice));
      invoiceLine.setCompanyInTaxTotal(getCompanyExTaxTotal(inTaxTotal, invoice));
    }
  }

  @Override
  public void updateInvoiceItem(InvoiceLine invoiceLine) {
    Product product = invoiceLine.getProduct();
    invoiceLine.setGstRate(product.getGstRate());
  }

  @Override
  public void setInvoiceLine(InvoiceLine invoiceLine) {
    invoiceLine.setQty(new BigDecimal(1));
    invoiceLine.setProductName(invoiceLine.getProduct().getName());
    invoiceLine.setUnit(invoiceLine.getProduct().getUnit());
  }

  @Override
  public TaxLine getTextLine(InvoiceLine invoiceLine) {
    if (invoiceLine.getProduct() != null) {
      String query =
          "self.tax.code='GST"
              + invoiceLine
                  .getProduct()
                  .getGstRate()
                  .multiply(new java.math.BigDecimal(100))
                  .intValue()
              + "'";
      TaxLine taxLine = taxLineRepo.all().filter(query).fetchOne();
      return taxLine;
    }
    return null;
  }
}
