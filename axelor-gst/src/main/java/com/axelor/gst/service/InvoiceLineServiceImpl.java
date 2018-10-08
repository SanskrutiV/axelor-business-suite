package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Product;
import com.google.inject.Inject;
import java.math.BigDecimal;

public class InvoiceLineServiceImpl implements InvoiceLineService {

	@Inject
	TaxLineRepository taxLineRepo;

	@Override
	public void calculateInvoiceItem(InvoiceLine invoiceLine, Invoice invoice) {
		Product product = invoiceLine.getProduct();
		BigDecimal netAmount = BigDecimal.ZERO;
		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal grossAmount = BigDecimal.ZERO;
		BigDecimal gstRate = product.getGstRate();
		String hsbn = product.getHsbn();
		netAmount = invoiceLine.getPrice().multiply(invoiceLine.getQty());
		invoiceLine.setExTaxTotal(netAmount.setScale(2));
		Company company = invoice.getCompany();
		if (company == null) {
			return;
		}
		Address companyAddress = company.getAddress();
		Address invoiceAddress = invoice.getAddress();
		if (invoiceAddress != null && companyAddress != null) {
			if (gstRate != BigDecimal.ZERO && companyAddress.getState() != null) {

				if (companyAddress.getState().equals(invoiceAddress.getState())) {
					sgst = netAmount.multiply(gstRate).divide(new BigDecimal(2));
					cgst = netAmount.multiply(gstRate).divide(new BigDecimal(2));
					grossAmount = sgst.add(cgst).add(netAmount);
					igst = new BigDecimal(0);
				} else {

					igst = netAmount.multiply(gstRate);
					sgst = new BigDecimal(0);
					cgst = new BigDecimal(0);
					grossAmount = igst.add(netAmount);
				}
			}

			invoiceLine.setHsbn(hsbn);
			invoiceLine.setCgst(cgst);
			invoiceLine.setIgst(igst);
			invoiceLine.setSgst(sgst);
			invoiceLine.setInTaxTotal(grossAmount);
		}
	}

	@Override
	public void updateInvoiceItem(InvoiceLine invoiceLine) {
		Product product = invoiceLine.getProduct();
		// BigDecimal netAmount = invoiceLine.getPrice().multiply(invoiceLine.getQty());
		// invoiceLine.setExTaxTotal(netAmount);
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
			String query = "self.tax.code='GST"
					+ invoiceLine.getProduct().getGstRate().multiply(new java.math.BigDecimal(100)).intValue() + "'";
			TaxLine taxLine = taxLineRepo.all().filter(query).fetchOne();
			return taxLine;
		}
		return null;
	}
}
