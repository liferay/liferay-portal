/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.PriceSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Price implements Cloneable, Serializable {

	public static Price toDTO(String json) {
		return PriceSerDes.toDTO(json);
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

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public void setDiscount(
		UnsafeSupplier<String, Exception> discountUnsafeSupplier) {

		try {
			discount = discountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String discount;

	public String getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(String discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public void setDiscountPercentage(
		UnsafeSupplier<String, Exception> discountPercentageUnsafeSupplier) {

		try {
			discountPercentage = discountPercentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String discountPercentage;

	public String[] getDiscountPercentages() {
		return discountPercentages;
	}

	public void setDiscountPercentages(String[] discountPercentages) {
		this.discountPercentages = discountPercentages;
	}

	public void setDiscountPercentages(
		UnsafeSupplier<String[], Exception> discountPercentagesUnsafeSupplier) {

		try {
			discountPercentages = discountPercentagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] discountPercentages;

	public String getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;
	}

	public void setFinalPrice(
		UnsafeSupplier<String, Exception> finalPriceUnsafeSupplier) {

		try {
			finalPrice = finalPriceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String finalPrice;

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

	public Boolean getPriceOnApplication() {
		return priceOnApplication;
	}

	public void setPriceOnApplication(Boolean priceOnApplication) {
		this.priceOnApplication = priceOnApplication;
	}

	public void setPriceOnApplication(
		UnsafeSupplier<Boolean, Exception> priceOnApplicationUnsafeSupplier) {

		try {
			priceOnApplication = priceOnApplicationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean priceOnApplication;

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

	public Double getPromoPrice() {
		return promoPrice;
	}

	public void setPromoPrice(Double promoPrice) {
		this.promoPrice = promoPrice;
	}

	public void setPromoPrice(
		UnsafeSupplier<Double, Exception> promoPriceUnsafeSupplier) {

		try {
			promoPrice = promoPriceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double promoPrice;

	public String getPromoPriceFormatted() {
		return promoPriceFormatted;
	}

	public void setPromoPriceFormatted(String promoPriceFormatted) {
		this.promoPriceFormatted = promoPriceFormatted;
	}

	public void setPromoPriceFormatted(
		UnsafeSupplier<String, Exception> promoPriceFormattedUnsafeSupplier) {

		try {
			promoPriceFormatted = promoPriceFormattedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String promoPriceFormatted;

	public Double getTierPrice() {
		return tierPrice;
	}

	public void setTierPrice(Double tierPrice) {
		this.tierPrice = tierPrice;
	}

	public void setTierPrice(
		UnsafeSupplier<Double, Exception> tierPriceUnsafeSupplier) {

		try {
			tierPrice = tierPriceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double tierPrice;

	public String getTierPriceFormatted() {
		return tierPriceFormatted;
	}

	public void setTierPriceFormatted(String tierPriceFormatted) {
		this.tierPriceFormatted = tierPriceFormatted;
	}

	public void setTierPriceFormatted(
		UnsafeSupplier<String, Exception> tierPriceFormattedUnsafeSupplier) {

		try {
			tierPriceFormatted = tierPriceFormattedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String tierPriceFormatted;

	@Override
	public Price clone() throws CloneNotSupportedException {
		return (Price)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Price)) {
			return false;
		}

		Price price = (Price)object;

		return Objects.equals(toString(), price.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PriceSerDes.toJSON(this);
	}

}