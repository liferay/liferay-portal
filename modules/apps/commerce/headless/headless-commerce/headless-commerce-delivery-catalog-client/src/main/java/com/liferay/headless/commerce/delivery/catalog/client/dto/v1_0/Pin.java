/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.PinSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Pin implements Cloneable, Serializable {

	public static Pin toDTO(String json) {
		return PinSerDes.toDTO(json);
	}

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

	public MappedProduct getMappedProduct() {
		return mappedProduct;
	}

	public void setMappedProduct(MappedProduct mappedProduct) {
		this.mappedProduct = mappedProduct;
	}

	public void setMappedProduct(
		UnsafeSupplier<MappedProduct, Exception> mappedProductUnsafeSupplier) {

		try {
			mappedProduct = mappedProductUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MappedProduct mappedProduct;

	public Double getPositionX() {
		return positionX;
	}

	public void setPositionX(Double positionX) {
		this.positionX = positionX;
	}

	public void setPositionX(
		UnsafeSupplier<Double, Exception> positionXUnsafeSupplier) {

		try {
			positionX = positionXUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double positionX;

	public Double getPositionY() {
		return positionY;
	}

	public void setPositionY(Double positionY) {
		this.positionY = positionY;
	}

	public void setPositionY(
		UnsafeSupplier<Double, Exception> positionYUnsafeSupplier) {

		try {
			positionY = positionYUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double positionY;

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public void setSequence(
		UnsafeSupplier<String, Exception> sequenceUnsafeSupplier) {

		try {
			sequence = sequenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sequence;

	@Override
	public Pin clone() throws CloneNotSupportedException {
		return (Pin)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Pin)) {
			return false;
		}

		Pin pin = (Pin)object;

		return Objects.equals(toString(), pin.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PinSerDes.toJSON(this);
	}

}