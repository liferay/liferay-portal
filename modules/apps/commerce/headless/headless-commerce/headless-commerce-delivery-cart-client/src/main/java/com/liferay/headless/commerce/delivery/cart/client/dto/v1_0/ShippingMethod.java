/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.cart.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0.ShippingMethodSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class ShippingMethod implements Cloneable, Serializable {

	public static ShippingMethod toDTO(String json) {
		return ShippingMethodSerDes.toDTO(json);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public String getEngineKey() {
		return engineKey;
	}

	public void setEngineKey(String engineKey) {
		this.engineKey = engineKey;
	}

	public void setEngineKey(
		UnsafeSupplier<String, Exception> engineKeyUnsafeSupplier) {

		try {
			engineKey = engineKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String engineKey;

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

	public ShippingOption[] getShippingOptions() {
		return shippingOptions;
	}

	public void setShippingOptions(ShippingOption[] shippingOptions) {
		this.shippingOptions = shippingOptions;
	}

	public void setShippingOptions(
		UnsafeSupplier<ShippingOption[], Exception>
			shippingOptionsUnsafeSupplier) {

		try {
			shippingOptions = shippingOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ShippingOption[] shippingOptions;

	@Override
	public ShippingMethod clone() throws CloneNotSupportedException {
		return (ShippingMethod)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ShippingMethod)) {
			return false;
		}

		ShippingMethod shippingMethod = (ShippingMethod)object;

		return Objects.equals(toString(), shippingMethod.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ShippingMethodSerDes.toJSON(this);
	}

}