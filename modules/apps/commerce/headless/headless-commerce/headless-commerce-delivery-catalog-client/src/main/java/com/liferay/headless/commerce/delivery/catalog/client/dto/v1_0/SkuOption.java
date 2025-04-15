/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.SkuOptionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class SkuOption implements Cloneable, Serializable {

	public static SkuOption toDTO(String json) {
		return SkuOptionSerDes.toDTO(json);
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<Long, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long key;

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setPrice(
		UnsafeSupplier<String, Exception> priceUnsafeSupplier) {

		try {
			price = priceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String price;

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public void setPriceType(
		UnsafeSupplier<String, Exception> priceTypeUnsafeSupplier) {

		try {
			priceType = priceTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String priceType;

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public void setQuantity(
		UnsafeSupplier<String, Exception> quantityUnsafeSupplier) {

		try {
			quantity = quantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String quantity;

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public void setSkuId(UnsafeSupplier<Long, Exception> skuIdUnsafeSupplier) {
		try {
			skuId = skuIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long skuId;

	public Long getSkuOptionId() {
		return skuOptionId;
	}

	public void setSkuOptionId(Long skuOptionId) {
		this.skuOptionId = skuOptionId;
	}

	public void setSkuOptionId(
		UnsafeSupplier<Long, Exception> skuOptionIdUnsafeSupplier) {

		try {
			skuOptionId = skuOptionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long skuOptionId;

	public String getSkuOptionKey() {
		return skuOptionKey;
	}

	public void setSkuOptionKey(String skuOptionKey) {
		this.skuOptionKey = skuOptionKey;
	}

	public void setSkuOptionKey(
		UnsafeSupplier<String, Exception> skuOptionKeyUnsafeSupplier) {

		try {
			skuOptionKey = skuOptionKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String skuOptionKey;

	public String getSkuOptionName() {
		return skuOptionName;
	}

	public void setSkuOptionName(String skuOptionName) {
		this.skuOptionName = skuOptionName;
	}

	public void setSkuOptionName(
		UnsafeSupplier<String, Exception> skuOptionNameUnsafeSupplier) {

		try {
			skuOptionName = skuOptionNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String skuOptionName;

	public Long getSkuOptionValueId() {
		return skuOptionValueId;
	}

	public void setSkuOptionValueId(Long skuOptionValueId) {
		this.skuOptionValueId = skuOptionValueId;
	}

	public void setSkuOptionValueId(
		UnsafeSupplier<Long, Exception> skuOptionValueIdUnsafeSupplier) {

		try {
			skuOptionValueId = skuOptionValueIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long skuOptionValueId;

	public String getSkuOptionValueKey() {
		return skuOptionValueKey;
	}

	public void setSkuOptionValueKey(String skuOptionValueKey) {
		this.skuOptionValueKey = skuOptionValueKey;
	}

	public void setSkuOptionValueKey(
		UnsafeSupplier<String, Exception> skuOptionValueKeyUnsafeSupplier) {

		try {
			skuOptionValueKey = skuOptionValueKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String skuOptionValueKey;

	public String[] getSkuOptionValueNames() {
		return skuOptionValueNames;
	}

	public void setSkuOptionValueNames(String[] skuOptionValueNames) {
		this.skuOptionValueNames = skuOptionValueNames;
	}

	public void setSkuOptionValueNames(
		UnsafeSupplier<String[], Exception> skuOptionValueNamesUnsafeSupplier) {

		try {
			skuOptionValueNames = skuOptionValueNamesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] skuOptionValueNames;

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public void setValue(UnsafeSupplier<Long, Exception> valueUnsafeSupplier) {
		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long value;

	@Override
	public SkuOption clone() throws CloneNotSupportedException {
		return (SkuOption)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SkuOption)) {
			return false;
		}

		SkuOption skuOption = (SkuOption)object;

		return Objects.equals(toString(), skuOption.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SkuOptionSerDes.toJSON(this);
	}

}