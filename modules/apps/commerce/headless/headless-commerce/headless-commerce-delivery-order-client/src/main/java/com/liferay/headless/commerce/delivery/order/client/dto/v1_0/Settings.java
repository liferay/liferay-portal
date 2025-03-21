/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.order.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.order.client.serdes.v1_0.SettingsSerDes;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Settings implements Cloneable, Serializable {

	public static Settings toDTO(String json) {
		return SettingsSerDes.toDTO(json);
	}

	public BigDecimal[] getAllowedQuantities() {
		return allowedQuantities;
	}

	public void setAllowedQuantities(BigDecimal[] allowedQuantities) {
		this.allowedQuantities = allowedQuantities;
	}

	public void setAllowedQuantities(
		UnsafeSupplier<BigDecimal[], Exception>
			allowedQuantitiesUnsafeSupplier) {

		try {
			allowedQuantities = allowedQuantitiesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal[] allowedQuantities;

	public BigDecimal getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(BigDecimal maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public void setMaxQuantity(
		UnsafeSupplier<BigDecimal, Exception> maxQuantityUnsafeSupplier) {

		try {
			maxQuantity = maxQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal maxQuantity;

	public BigDecimal getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(BigDecimal minQuantity) {
		this.minQuantity = minQuantity;
	}

	public void setMinQuantity(
		UnsafeSupplier<BigDecimal, Exception> minQuantityUnsafeSupplier) {

		try {
			minQuantity = minQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal minQuantity;

	public BigDecimal getMultipleQuantity() {
		return multipleQuantity;
	}

	public void setMultipleQuantity(BigDecimal multipleQuantity) {
		this.multipleQuantity = multipleQuantity;
	}

	public void setMultipleQuantity(
		UnsafeSupplier<BigDecimal, Exception> multipleQuantityUnsafeSupplier) {

		try {
			multipleQuantity = multipleQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal multipleQuantity;

	@Override
	public Settings clone() throws CloneNotSupportedException {
		return (Settings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Settings)) {
			return false;
		}

		Settings settings = (Settings)object;

		return Objects.equals(toString(), settings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SettingsSerDes.toJSON(this);
	}

}