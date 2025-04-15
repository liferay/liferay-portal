/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.order.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.order.client.serdes.v1_0.OrderTransitionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class OrderTransition implements Cloneable, Serializable {

	public static OrderTransition toDTO(String json) {
		return OrderTransitionSerDes.toDTO(json);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setComment(
		UnsafeSupplier<String, Exception> commentUnsafeSupplier) {

		try {
			comment = commentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String comment;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String label;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setOrderId(
		UnsafeSupplier<Long, Exception> orderIdUnsafeSupplier) {

		try {
			orderId = orderIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long orderId;

	public Long getPlacedOrderId() {
		return placedOrderId;
	}

	public void setPlacedOrderId(Long placedOrderId) {
		this.placedOrderId = placedOrderId;
	}

	public void setPlacedOrderId(
		UnsafeSupplier<Long, Exception> placedOrderIdUnsafeSupplier) {

		try {
			placedOrderId = placedOrderIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long placedOrderId;

	public Long getWorkflowTaskId() {
		return workflowTaskId;
	}

	public void setWorkflowTaskId(Long workflowTaskId) {
		this.workflowTaskId = workflowTaskId;
	}

	public void setWorkflowTaskId(
		UnsafeSupplier<Long, Exception> workflowTaskIdUnsafeSupplier) {

		try {
			workflowTaskId = workflowTaskIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long workflowTaskId;

	@Override
	public OrderTransition clone() throws CloneNotSupportedException {
		return (OrderTransition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OrderTransition)) {
			return false;
		}

		OrderTransition orderTransition = (OrderTransition)object;

		return Objects.equals(toString(), orderTransition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return OrderTransitionSerDes.toJSON(this);
	}

}