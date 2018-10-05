package com.axelor.gst.service;

import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.ProductCategory;
import com.axelor.apps.base.db.repo.ProductCategoryRepository;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ProductServiceImpl implements ProductService {

  @Inject ProductCategoryRepository productCategoryRepo;

  @Inject ProductRepository productRepo;

  @Override
  public BigDecimal getGstRate(Product product) {
    ProductCategory productCategory = product.getProductCategory();
    BigDecimal gstRate = productCategoryRepo.findByName(productCategory.getName()).getGstRate();
    return gstRate;
  }

  @Override
  public List<Integer> getAllProductIds() {
    List<Integer> productIds = new ArrayList<Integer>();
    for (Product product : productRepo.all().fetch()) {
      productIds.add(Integer.parseInt(product.getId().toString()));
    }
    return productIds;
  }

  @Transactional
  @Override
  public List<InvoiceLine> getInvoiceItems(List<Integer> list) {
    InvoiceLine invoiceLine;
    List<InvoiceLine> invoiceItems = new ArrayList<InvoiceLine>();
    Product product;
    for (Integer id : list) {
      invoiceLine = new InvoiceLine();
      product = productRepo.find(Long.parseLong(id.toString()));
      invoiceLine.setProduct(product);
      invoiceLine.setHsbn(product.getHsbn());
      invoiceLine.setPrice(product.getSalePrice());
      invoiceLine.setExTaxTotal(invoiceLine.getPrice().multiply(invoiceLine.getQty()));
      invoiceLine.setInTaxTotal(invoiceLine.getExTaxTotal());
      String string = product.getName() + "[" + product.getCode() + "]";
      invoiceItems.add(invoiceLine);
    }
    return invoiceItems;
  }

  @Override
  public String getProductIdsString(List<Integer> selectedProductsIds) {
    List<String> selectedProductsString = new ArrayList<String>();
    String selectedProducts = null;
    for (Integer i : selectedProductsIds) {
      selectedProductsString.add(Integer.toString(i));
    }
    selectedProducts = String.join(",", selectedProductsString);
    return selectedProducts;
  }
}
