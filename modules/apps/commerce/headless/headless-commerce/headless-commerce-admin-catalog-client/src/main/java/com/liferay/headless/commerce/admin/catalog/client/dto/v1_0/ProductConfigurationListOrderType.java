/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductConfigurationListOrderTypeSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductConfigurationListOrderType
	implements Cloneable, Serializable {

	public static ProductConfigurationListOrderType toDTO(String json) {
		return ProductConfigurationListOrderTypeSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public void setOrderType(
		UnsafeSupplier<OrderType, Exception> orderTypeUnsafeSupplier) {

		try {
			orderType = orderTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected OrderType orderType;

	public String getOrderTypeExternalReferenceCode() {
		return orderTypeExternalReferenceCode;
	}

	public void setOrderTypeExternalReferenceCode(
		String orderTypeExternalReferenceCode) {

		this.orderTypeExternalReferenceCode = orderTypeExternalReferenceCode;
	}

	public void setOrderTypeExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderTypeExternalReferenceCodeUnsafeSupplier) {

		try {
			orderTypeExternalReferenceCode =
				orderTypeExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String orderTypeExternalReferenceCode;

	public Long getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(Long orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public void setOrderTypeId(
		UnsafeSupplier<Long, Exception> orderTypeIdUnsafeSupplier) {

		try {
			orderTypeId = orderTypeIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long orderTypeId;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setPriority(
		UnsafeSupplier<Integer, Exception> priorityUnsafeSupplier) {

		try {
			priority = priorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer priority;

	public String getProductConfigurationListExternalReferenceCode() {
		return productConfigurationListExternalReferenceCode;
	}

	public void setProductConfigurationListExternalReferenceCode(
		String productConfigurationListExternalReferenceCode) {

		this.productConfigurationListExternalReferenceCode =
			productConfigurationListExternalReferenceCode;
	}

	public void setProductConfigurationListExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productConfigurationListExternalReferenceCodeUnsafeSupplier) {

		try {
			productConfigurationListExternalReferenceCode =
				productConfigurationListExternalReferenceCodeUnsafeSupplier.
					get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String productConfigurationListExternalReferenceCode;

	public Long getProductConfigurationListId() {
		return productConfigurationListId;
	}

	public void setProductConfigurationListId(Long productConfigurationListId) {
		this.productConfigurationListId = productConfigurationListId;
	}

	public void setProductConfigurationListId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListIdUnsafeSupplier) {

		try {
			productConfigurationListId =
				productConfigurationListIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productConfigurationListId;

	public Long getProductConfigurationListOrderTypeId() {
		return productConfigurationListOrderTypeId;
	}

	public void setProductConfigurationListOrderTypeId(
		Long productConfigurationListOrderTypeId) {

		this.productConfigurationListOrderTypeId =
			productConfigurationListOrderTypeId;
	}

	public void setProductConfigurationListOrderTypeId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListOrderTypeIdUnsafeSupplier) {

		try {
			productConfigurationListOrderTypeId =
				productConfigurationListOrderTypeIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productConfigurationListOrderTypeId;

	@Override
	public ProductConfigurationListOrderType clone()
		throws CloneNotSupportedException {

		return (ProductConfigurationListOrderType)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductConfigurationListOrderType)) {
			return false;
		}

		ProductConfigurationListOrderType productConfigurationListOrderType =
			(ProductConfigurationListOrderType)object;

		return Objects.equals(
			toString(), productConfigurationListOrderType.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProductConfigurationListOrderTypeSerDes.toJSON(this);
	}

}