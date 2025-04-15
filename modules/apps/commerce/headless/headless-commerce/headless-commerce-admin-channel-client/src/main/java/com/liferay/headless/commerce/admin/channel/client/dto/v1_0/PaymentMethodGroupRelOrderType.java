/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.dto.v1_0;

import com.liferay.headless.commerce.admin.channel.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.PaymentMethodGroupRelOrderTypeSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class PaymentMethodGroupRelOrderType implements Cloneable, Serializable {

	public static PaymentMethodGroupRelOrderType toDTO(String json) {
		return PaymentMethodGroupRelOrderTypeSerDes.toDTO(json);
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

	public Long getPaymentMethodGroupRelId() {
		return paymentMethodGroupRelId;
	}

	public void setPaymentMethodGroupRelId(Long paymentMethodGroupRelId) {
		this.paymentMethodGroupRelId = paymentMethodGroupRelId;
	}

	public void setPaymentMethodGroupRelId(
		UnsafeSupplier<Long, Exception> paymentMethodGroupRelIdUnsafeSupplier) {

		try {
			paymentMethodGroupRelId =
				paymentMethodGroupRelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long paymentMethodGroupRelId;

	public Long getPaymentMethodGroupRelOrderTypeId() {
		return paymentMethodGroupRelOrderTypeId;
	}

	public void setPaymentMethodGroupRelOrderTypeId(
		Long paymentMethodGroupRelOrderTypeId) {

		this.paymentMethodGroupRelOrderTypeId =
			paymentMethodGroupRelOrderTypeId;
	}

	public void setPaymentMethodGroupRelOrderTypeId(
		UnsafeSupplier<Long, Exception>
			paymentMethodGroupRelOrderTypeIdUnsafeSupplier) {

		try {
			paymentMethodGroupRelOrderTypeId =
				paymentMethodGroupRelOrderTypeIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long paymentMethodGroupRelOrderTypeId;

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

	@Override
	public PaymentMethodGroupRelOrderType clone()
		throws CloneNotSupportedException {

		return (PaymentMethodGroupRelOrderType)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PaymentMethodGroupRelOrderType)) {
			return false;
		}

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType =
			(PaymentMethodGroupRelOrderType)object;

		return Objects.equals(
			toString(), paymentMethodGroupRelOrderType.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PaymentMethodGroupRelOrderTypeSerDes.toJSON(this);
	}

}