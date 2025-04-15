/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.TierPriceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Objects;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class TierPrice implements Cloneable, Serializable {

	public static TierPrice toDTO(String json) {
		return TierPriceSerDes.toDTO(json);
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setCurrency(
		UnsafeSupplier<String, Exception> currencyUnsafeSupplier) {

		try {
			currency = currencyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String currency;

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setPrice(
		UnsafeSupplier<Double, Exception> priceUnsafeSupplier) {

		try {
			price = priceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double price;

	public String getPriceFormatted() {
		return priceFormatted;
	}

	public void setPriceFormatted(String priceFormatted) {
		this.priceFormatted = priceFormatted;
	}

	public void setPriceFormatted(
		UnsafeSupplier<String, Exception> priceFormattedUnsafeSupplier) {

		try {
			priceFormatted = priceFormattedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String priceFormatted;

	public Double getPricingQuantityPrice() {
		return pricingQuantityPrice;
	}

	public void setPricingQuantityPrice(Double pricingQuantityPrice) {
		this.pricingQuantityPrice = pricingQuantityPrice;
	}

	public void setPricingQuantityPrice(
		UnsafeSupplier<Double, Exception> pricingQuantityPriceUnsafeSupplier) {

		try {
			pricingQuantityPrice = pricingQuantityPriceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double pricingQuantityPrice;

	public String getPricingQuantityPriceFormatted() {
		return pricingQuantityPriceFormatted;
	}

	public void setPricingQuantityPriceFormatted(
		String pricingQuantityPriceFormatted) {

		this.pricingQuantityPriceFormatted = pricingQuantityPriceFormatted;
	}

	public void setPricingQuantityPriceFormatted(
		UnsafeSupplier<String, Exception>
			pricingQuantityPriceFormattedUnsafeSupplier) {

		try {
			pricingQuantityPriceFormatted =
				pricingQuantityPriceFormattedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String pricingQuantityPriceFormatted;

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public void setQuantity(
		UnsafeSupplier<BigDecimal, Exception> quantityUnsafeSupplier) {

		try {
			quantity = quantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal quantity;

	@Override
	public TierPrice clone() throws CloneNotSupportedException {
		return (TierPrice)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TierPrice)) {
			return false;
		}

		TierPrice tierPrice = (TierPrice)object;

		return Objects.equals(toString(), tierPrice.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TierPriceSerDes.toJSON(this);
	}

}