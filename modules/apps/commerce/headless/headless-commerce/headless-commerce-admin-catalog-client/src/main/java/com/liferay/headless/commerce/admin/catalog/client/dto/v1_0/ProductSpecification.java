/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductSpecificationSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductSpecification implements Cloneable, Serializable {

	public static ProductSpecification toDTO(String json) {
		return ProductSpecificationSerDes.toDTO(json);
	}

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String key;

	public Map<String, String> getLabel() {
		return label;
	}

	public void setLabel(Map<String, String> label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<Map<String, String>, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> label;

	public String getOptionCategoryExternalReferenceCode() {
		return optionCategoryExternalReferenceCode;
	}

	public void setOptionCategoryExternalReferenceCode(
		String optionCategoryExternalReferenceCode) {

		this.optionCategoryExternalReferenceCode =
			optionCategoryExternalReferenceCode;
	}

	public void setOptionCategoryExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			optionCategoryExternalReferenceCodeUnsafeSupplier) {

		try {
			optionCategoryExternalReferenceCode =
				optionCategoryExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String optionCategoryExternalReferenceCode;

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

	public String getSpecificationExternalReferenceCode() {
		return specificationExternalReferenceCode;
	}

	public void setSpecificationExternalReferenceCode(
		String specificationExternalReferenceCode) {

		this.specificationExternalReferenceCode =
			specificationExternalReferenceCode;
	}

	public void setSpecificationExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			specificationExternalReferenceCodeUnsafeSupplier) {

		try {
			specificationExternalReferenceCode =
				specificationExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String specificationExternalReferenceCode;

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

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public void setVisible(
		UnsafeSupplier<Boolean, Exception> visibleUnsafeSupplier) {

		try {
			visible = visibleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean visible;

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