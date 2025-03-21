/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductShippingConfigurationSerDes;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductShippingConfiguration implements Cloneable, Serializable {

	public static ProductShippingConfiguration toDTO(String json) {
		return ProductShippingConfigurationSerDes.toDTO(json);
	}

	public BigDecimal getDepth() {
		return depth;
	}

	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

	public void setDepth(
		UnsafeSupplier<BigDecimal, Exception> depthUnsafeSupplier) {

		try {
			depth = depthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal depth;

	public Boolean getFreeShipping() {
		return freeShipping;
	}

	public void setFreeShipping(Boolean freeShipping) {
		this.freeShipping = freeShipping;
	}

	public void setFreeShipping(
		UnsafeSupplier<Boolean, Exception> freeShippingUnsafeSupplier) {

		try {
			freeShipping = freeShippingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean freeShipping;

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public void setHeight(
		UnsafeSupplier<BigDecimal, Exception> heightUnsafeSupplier) {

		try {
			height = heightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal height;

	public Boolean getShippable() {
		return shippable;
	}

	public void setShippable(Boolean shippable) {
		this.shippable = shippable;
	}

	public void setShippable(
		UnsafeSupplier<Boolean, Exception> shippableUnsafeSupplier) {

		try {
			shippable = shippableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean shippable;

	public BigDecimal getShippingExtraPrice() {
		return shippingExtraPrice;
	}

	public void setShippingExtraPrice(BigDecimal shippingExtraPrice) {
		this.shippingExtraPrice = shippingExtraPrice;
	}

	public void setShippingExtraPrice(
		UnsafeSupplier<BigDecimal, Exception>
			shippingExtraPriceUnsafeSupplier) {

		try {
			shippingExtraPrice = shippingExtraPriceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal shippingExtraPrice;

	public Boolean getShippingSeparately() {
		return shippingSeparately;
	}

	public void setShippingSeparately(Boolean shippingSeparately) {
		this.shippingSeparately = shippingSeparately;
	}

	public void setShippingSeparately(
		UnsafeSupplier<Boolean, Exception> shippingSeparatelyUnsafeSupplier) {

		try {
			shippingSeparately = shippingSeparatelyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean shippingSeparately;

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public void setWeight(
		UnsafeSupplier<BigDecimal, Exception> weightUnsafeSupplier) {

		try {
			weight = weightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal weight;

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public void setWidth(
		UnsafeSupplier<BigDecimal, Exception> widthUnsafeSupplier) {

		try {
			width = widthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal width;

	@Override
	public ProductShippingConfiguration clone()
		throws CloneNotSupportedException {

		return (ProductShippingConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductShippingConfiguration)) {
			return false;
		}

		ProductShippingConfiguration productShippingConfiguration =
			(ProductShippingConfiguration)object;

		return Objects.equals(
			toString(), productShippingConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProductShippingConfigurationSerDes.toJSON(this);
	}

}