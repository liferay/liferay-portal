/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.GroupedProductSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class GroupedProduct implements Cloneable, Serializable {

	public static GroupedProduct toDTO(String json) {
		return GroupedProductSerDes.toDTO(json);
	}

	public String getEntryProductExternalReferenceCode() {
		return entryProductExternalReferenceCode;
	}

	public void setEntryProductExternalReferenceCode(
		String entryProductExternalReferenceCode) {

		this.entryProductExternalReferenceCode =
			entryProductExternalReferenceCode;
	}

	public void setEntryProductExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			entryProductExternalReferenceCodeUnsafeSupplier) {

		try {
			entryProductExternalReferenceCode =
				entryProductExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String entryProductExternalReferenceCode;

	public Long getEntryProductId() {
		return entryProductId;
	}

	public void setEntryProductId(Long entryProductId) {
		this.entryProductId = entryProductId;
	}

	public void setEntryProductId(
		UnsafeSupplier<Long, Exception> entryProductIdUnsafeSupplier) {

		try {
			entryProductId = entryProductIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long entryProductId;

	public Map<String, String> getEntryProductName() {
		return entryProductName;
	}

	public void setEntryProductName(Map<String, String> entryProductName) {
		this.entryProductName = entryProductName;
	}

	public void setEntryProductName(
		UnsafeSupplier<Map<String, String>, Exception>
			entryProductNameUnsafeSupplier) {

		try {
			entryProductName = entryProductNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> entryProductName;

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

	public Double getPriority() {
		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		try {
			priority = priorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double priority;

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

	public Map<String, String> getProductName() {
		return productName;
	}

	public void setProductName(Map<String, String> productName) {
		this.productName = productName;
	}

	public void setProductName(
		UnsafeSupplier<Map<String, String>, Exception>
			productNameUnsafeSupplier) {

		try {
			productName = productNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> productName;

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setQuantity(
		UnsafeSupplier<Integer, Exception> quantityUnsafeSupplier) {

		try {
			quantity = quantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer quantity;

	@Override
	public GroupedProduct clone() throws CloneNotSupportedException {
		return (GroupedProduct)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GroupedProduct)) {
			return false;
		}

		GroupedProduct groupedProduct = (GroupedProduct)object;

		return Objects.equals(toString(), groupedProduct.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return GroupedProductSerDes.toJSON(this);
	}

}