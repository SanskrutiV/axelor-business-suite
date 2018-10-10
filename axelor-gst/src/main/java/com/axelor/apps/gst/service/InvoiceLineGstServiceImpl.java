package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.tax.AccountManagementService;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class InvoiceLineGstServiceImpl extends InvoiceLineSupplychainService
    implements InvoiceLineService {

  @Inject
  public InvoiceLineGstServiceImpl(
      AccountManagementService accountManagementService,
      CurrencyService currencyService,
      PriceListService priceListService,
      AppAccountService appAccountService,
      AnalyticMoveLineService analyticMoveLineService,
      AccountManagementAccountService accountManagementAccountService,
      PurchaseProductService purchaseProductService) {
    super(
        accountManagementService,
        currencyService,
        priceListService,
        appAccountService,
        analyticMoveLineService,
        accountManagementAccountService,
        purchaseProductService);
    // TODO Auto-generated constructor stub
  }

  @Inject TaxLineRepository taxLineRepo;

  @Inject TaxRepository taxRepo;

  @Override
  public void calculateInvoiceItem(InvoiceLine invoiceLine, Invoice invoice)
      throws AxelorException {
    Product product = invoiceLine.getProduct();
    BigDecimal exTaxTotal =
        invoiceLine
            .getPrice()
            .multiply(invoiceLine.getQty())
            .setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_EVEN);
    BigDecimal sgst = BigDecimal.ZERO;
    BigDecimal cgst = BigDecimal.ZERO;
    BigDecimal igst = BigDecimal.ZERO;
    BigDecimal inTaxTotal =
        exTaxTotal.add(exTaxTotal.multiply(invoiceLine.getTaxLine().getValue()));
    BigDecimal gstRate = product.getGstRate();
    String hsbn = product.getHsbn();
    invoiceLine.setExTaxTotal(exTaxTotal);
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
          igst = new BigDecimal(0);
        } else {

          igst = exTaxTotal.multiply(gstRate);
          sgst = new BigDecimal(0);
          cgst = new BigDecimal(0);
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
  public void setInvoiceLine(InvoiceLine invoiceLine) {
    invoiceLine.setGstRate(invoiceLine.getProduct().getGstRate());
    invoiceLine.setQty(new BigDecimal(1));
    invoiceLine.setProductName(invoiceLine.getProduct().getName());
    invoiceLine.setUnit(invoiceLine.getProduct().getUnit());
  }

  @Override
  public TaxLine getTextLine(InvoiceLine invoiceLine) {
    if (invoiceLine.getProduct() != null) {
      Tax tax = taxRepo.all().filter("self.code='GST'").fetchOne();
      TaxLine taxLine =
          taxLineRepo
              .all()
              .filter("self.tax=? AND self.value=?", tax, invoiceLine.getProduct().getGstRate())
              .fetchOne();
      return taxLine;
    }
    return null;
  }
}
