/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.dto.v1_0;

import com.liferay.headless.commerce.machine.learning.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.machine.learning.client.serdes.v1_0.ProductSpecificationSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class ProductSpecification implements Cloneable, Serializable {

	public static ProductSpecification toDTO(String json) {
		return ProductSpecificationSerDes.toDTO(json);
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

	public Long getOptionCategoryId() {
		return optionCategoryId;
	}

	public void setOptionCategoryId(Long optionCategoryId) {
		this.optionCategoryId = optionCategoryId;
	}

	public void setOptionCategoryId(
		UnsafeSupplier<Long, Exception> optionCategoryIdUnsafeSupplier) {

		try {
			optionCategoryId = optionCategoryIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long optionCategoryId;

	public String getSpecificationKey() {
		return specificationKey;
	}

	public void setSpecificationKey(String specificationKey) {
		this.specificationKey = specificationKey;
	}

	public void setSpecificationKey(
		UnsafeSupplier<String, Exception> specificationKeyUnsafeSupplier) {

		try {
			specificationKey = specificationKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String specificationKey;

	public Map<String, String> getValue() {
		return value;
	}

	public void setValue(Map<String, String> value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<Map<String, String>, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> value;

	@Override
	public ProductSpecification clone() throws CloneNotSupportedException {
		return (ProductSpecification)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductSpecification)) {
			return false;
		}

		ProductSpecification productSpecification =
			(ProductSpecification)object;

		return Objects.equals(toString(), productSpecification.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProductSpecificationSerDes.toJSON(this);
	}

}