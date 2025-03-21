/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.dto.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.SkuSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class Sku implements Cloneable, Serializable {

	public static Sku toDTO(String json) {
		return SkuSerDes.toDTO(json);
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public void setBasePrice(
		UnsafeSupplier<Double, Exception> basePriceUnsafeSupplier) {

		try {
			basePrice = basePriceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double basePrice;

	public String getBasePriceFormatted() {
		return basePriceFormatted;
	}

	public void setBasePriceFormatted(String basePriceFormatted) {
		this.basePriceFormatted = basePriceFormatted;
	}

	public void setBasePriceFormatted(
		UnsafeSupplier<String, Exception> basePriceFormattedUnsafeSupplier) {

		try {
			basePriceFormatted = basePriceFormattedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String basePriceFormatted;

	public Double getBasePromoPrice() {
		return basePromoPrice;
	}

	public void setBasePromoPrice(Double basePromoPrice) {
		this.basePromoPrice = basePromoPrice;
	}

	public void setBasePromoPrice(
		UnsafeSupplier<Double, Exception> basePromoPriceUnsafeSupplier) {

		try {
			basePromoPrice = basePromoPriceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double basePromoPrice;

	public String getBasePromoPriceFormatted() {
		return basePromoPriceFormatted;
	}

	public void setBasePromoPriceFormatted(String basePromoPriceFormatted) {
		this.basePromoPriceFormatted = basePromoPriceFormatted;
	}

	public void setBasePromoPriceFormatted(
		UnsafeSupplier<String, Exception>
			basePromoPriceFormattedUnsafeSupplier) {

		try {
			basePromoPriceFormatted =
				basePromoPriceFormattedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String basePromoPriceFormatted;

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

	@Override
	public Sku clone() throws CloneNotSupportedException {
		return (Sku)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Sku)) {
			return false;
		}

		Sku sku = (Sku)object;

		return Objects.equals(toString(), sku.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SkuSerDes.toJSON(this);
	}

}