/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.dto.v1_0;

import com.liferay.analytics.settings.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.settings.rest.client.serdes.v1_0.FieldSummarySerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class FieldSummary implements Cloneable, Serializable {

	public static FieldSummary toDTO(String json) {
		return FieldSummarySerDes.toDTO(json);
	}

	public Integer getAccount() {
		return account;
	}

	public void setAccount(Integer account) {
		this.account = account;
	}

	public void setAccount(
		UnsafeSupplier<Integer, Exception> accountUnsafeSupplier) {

		try {
			account = accountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer account;

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public void setOrder(
		UnsafeSupplier<Integer, Exception> orderUnsafeSupplier) {

		try {
			order = orderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer order;

	public Integer getPeople() {
		return people;
	}

	public void setPeople(Integer people) {
		this.people = people;
	}

	public void setPeople(
		UnsafeSupplier<Integer, Exception> peopleUnsafeSupplier) {

		try {
			people = peopleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer people;

	public Integer getProduct() {
		return product;
	}

	public void setProduct(Integer product) {
		this.product = product;
	}

	public void setProduct(
		UnsafeSupplier<Integer, Exception> productUnsafeSupplier) {

		try {
			product = productUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer product;

	@Override
	public FieldSummary clone() throws CloneNotSupportedException {
		return (FieldSummary)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FieldSummary)) {
			return false;
		}

		FieldSummary fieldSummary = (FieldSummary)object;

		return Objects.equals(toString(), fieldSummary.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FieldSummarySerDes.toJSON(this);
	}

}