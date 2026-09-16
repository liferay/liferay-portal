/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductGroupProductSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductGroupProduct implements Cloneable, Serializable {

	public static ProductGroupProduct toDTO(String json) {
		return ProductGroupProductSerDes.toDTO(json);
	}

	public String getCatalogCurrencyCode() {
		return catalogCurrencyCode;
	}

	public void setCatalogCurrencyCode(String catalogCurrencyCode) {
		this.catalogCurrencyCode = catalogCurrencyCode;
	}

	public void setCatalogCurrencyCode(
		UnsafeSupplier<String, Exception> catalogCurrencyCodeUnsafeSupplier) {

		try {
			catalogCurrencyCode = catalogCurrencyCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String catalogCurrencyCode;

	public String getCatalogCurrencyExternalReferenceCode() {
		return catalogCurrencyExternalReferenceCode;
	}

	public void setCatalogCurrencyExternalReferenceCode(
		String catalogCurrencyExternalReferenceCode) {

		this.catalogCurrencyExternalReferenceCode =
			catalogCurrencyExternalReferenceCode;
	}

	public void setCatalogCurrencyExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			catalogCurrencyExternalReferenceCodeUnsafeSupplier) {

		try {
			catalogCurrencyExternalReferenceCode =
				catalogCurrencyExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String catalogCurrencyExternalReferenceCode;

	public String getCatalogExternalReferenceCode() {
		return catalogExternalReferenceCode;
	}

	public void setCatalogExternalReferenceCode(
		String catalogExternalReferenceCode) {

		this.catalogExternalReferenceCode = catalogExternalReferenceCode;
	}

	public void setCatalogExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			catalogExternalReferenceCodeUnsafeSupplier) {

		try {
			catalogExternalReferenceCode =
				catalogExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String catalogExternalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getProductExternalReferenceCode() {
		return productExternalReferenceCode;
	}

	public void setProductExternalReferenceCode(
		String productExternalReferenceCode) {

		this.productExternalReferenceCode = productExternalReferenceCode;
	}

	public void setProductExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productExternalReferenceCodeUnsafeSupplier) {

		try {
			productExternalReferenceCode =
				productExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String productExternalReferenceCode;

	public String getProductGroupExternalReferenceCode() {
		return productGroupExternalReferenceCode;
	}

	public void setProductGroupExternalReferenceCode(
		String productGroupExternalReferenceCode) {

		this.productGroupExternalReferenceCode =
			productGroupExternalReferenceCode;
	}

	public void setProductGroupExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productGroupExternalReferenceCodeUnsafeSupplier) {

		try {
			productGroupExternalReferenceCode =
				productGroupExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String productGroupExternalReferenceCode;

	public Long getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(Long productGroupId) {
		this.productGroupId = productGroupId;
	}

	public void setProductGroupId(
		UnsafeSupplier<Long, Exception> productGroupIdUnsafeSupplier) {

		try {
			productGroupId = productGroupIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productGroupId;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		try {
			productId = productIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productId;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setProductName(
		UnsafeSupplier<String, Exception> productNameUnsafeSupplier) {

		try {
			productName = productNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String productName;

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public void setProductType(
		UnsafeSupplier<String, Exception> productTypeUnsafeSupplier) {

		try {
			productType = productTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String productType;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setSku(UnsafeSupplier<String, Exception> skuUnsafeSupplier) {
		try {
			sku = skuUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sku;

	@Override
	public ProductGroupProduct clone() throws CloneNotSupportedException {
		return (ProductGroupProduct)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductGroupProduct)) {
			return false;
		}

		ProductGroupProduct productGroupProduct = (ProductGroupProduct)object;

		return Objects.equals(toString(), productGroupProduct.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProductGroupProductSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-846487689