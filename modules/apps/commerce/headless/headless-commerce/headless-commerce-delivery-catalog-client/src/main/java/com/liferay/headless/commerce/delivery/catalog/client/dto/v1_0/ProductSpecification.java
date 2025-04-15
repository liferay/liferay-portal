/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.ProductSpecificationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Andrea Sbarra
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

	public Double getPriority() {
		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		try {
			priority = priorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double priority;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		try {
			productId = productIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productId;

	public String getSpecificationGroupKey() {
		return specificationGroupKey;
	}

	public void setSpecificationGroupKey(String specificationGroupKey) {
		this.specificationGroupKey = specificationGroupKey;
	}

	public void setSpecificationGroupKey(
		UnsafeSupplier<String, Exception> specificationGroupKeyUnsafeSupplier) {

		try {
			specificationGroupKey = specificationGroupKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String specificationGroupKey;

	public String getSpecificationGroupTitle() {
		return specificationGroupTitle;
	}

	public void setSpecificationGroupTitle(String specificationGroupTitle) {
		this.specificationGroupTitle = specificationGroupTitle;
	}

	public void setSpecificationGroupTitle(
		UnsafeSupplier<String, Exception>
			specificationGroupTitleUnsafeSupplier) {

		try {
			specificationGroupTitle =
				specificationGroupTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String specificationGroupTitle;

	public Long getSpecificationId() {
		return specificationId;
	}

	public void setSpecificationId(Long specificationId) {
		this.specificationId = specificationId;
	}

	public void setSpecificationId(
		UnsafeSupplier<Long, Exception> specificationIdUnsafeSupplier) {

		try {
			specificationId = specificationIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long specificationId;

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

	public Double getSpecificationPriority() {
		return specificationPriority;
	}

	public void setSpecificationPriority(Double specificationPriority) {
		this.specificationPriority = specificationPriority;
	}

	public void setSpecificationPriority(
		UnsafeSupplier<Double, Exception> specificationPriorityUnsafeSupplier) {

		try {
			specificationPriority = specificationPriorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double specificationPriority;

	public String getSpecificationTitle() {
		return specificationTitle;
	}

	public void setSpecificationTitle(String specificationTitle) {
		this.specificationTitle = specificationTitle;
	}

	public void setSpecificationTitle(
		UnsafeSupplier<String, Exception> specificationTitleUnsafeSupplier) {

		try {
			specificationTitle = specificationTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String specificationTitle;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String value;

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