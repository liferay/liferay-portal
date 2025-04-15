/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductOptionSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductOption implements Cloneable, Serializable {

	public static ProductOption toDTO(String json) {
		return ProductOptionSerDes.toDTO(json);
	}

	public Long getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
	}

	public void setCatalogId(
		UnsafeSupplier<Long, Exception> catalogIdUnsafeSupplier) {

		try {
			catalogId = catalogIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long catalogId;

	public com.liferay.headless.commerce.admin.catalog.client.custom.field.
		CustomField[] getCustomFields() {

		return customFields;
	}

	public void setCustomFields(
		com.liferay.headless.commerce.admin.catalog.client.custom.field.
			CustomField[] customFields) {

		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.headless.commerce.admin.catalog.client.custom.field.
				CustomField[],
			 Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.commerce.admin.catalog.client.custom.field.
		CustomField[] customFields;

	public Boolean getDefinedExternally() {
		return definedExternally;
	}

	public void setDefinedExternally(Boolean definedExternally) {
		this.definedExternally = definedExternally;
	}

	public void setDefinedExternally(
		UnsafeSupplier<Boolean, Exception> definedExternallyUnsafeSupplier) {

		try {
			definedExternally = definedExternallyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean definedExternally;

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> description;

	public Boolean getFacetable() {
		return facetable;
	}

	public void setFacetable(Boolean facetable) {
		this.facetable = facetable;
	}

	public void setFacetable(
		UnsafeSupplier<Boolean, Exception> facetableUnsafeSupplier) {

		try {
			facetable = facetableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean facetable;

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public void setFieldType(
		UnsafeSupplier<String, Exception> fieldTypeUnsafeSupplier) {

		try {
			fieldType = fieldTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fieldType;

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

	public String getInfoItemServiceKey() {
		return infoItemServiceKey;
	}

	public void setInfoItemServiceKey(String infoItemServiceKey) {
		this.infoItemServiceKey = infoItemServiceKey;
	}

	public void setInfoItemServiceKey(
		UnsafeSupplier<String, Exception> infoItemServiceKeyUnsafeSupplier) {

		try {
			infoItemServiceKey = infoItemServiceKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String infoItemServiceKey;

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

	public Map<String, String> getName() {
		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;
	}

	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> name;

	public String getOptionExternalReferenceCode() {
		return optionExternalReferenceCode;
	}

	public void setOptionExternalReferenceCode(
		String optionExternalReferenceCode) {

		this.optionExternalReferenceCode = optionExternalReferenceCode;
	}

	public void setOptionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			optionExternalReferenceCodeUnsafeSupplier) {

		try {
			optionExternalReferenceCode =
				optionExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String optionExternalReferenceCode;

	public Long getOptionId() {
		return optionId;
	}

	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}

	public void setOptionId(
		UnsafeSupplier<Long, Exception> optionIdUnsafeSupplier) {

		try {
			optionId = optionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long optionId;

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

	public ProductOptionValue[] getProductOptionValues() {
		return productOptionValues;
	}

	public void setProductOptionValues(
		ProductOptionValue[] productOptionValues) {

		this.productOptionValues = productOptionValues;
	}

	public void setProductOptionValues(
		UnsafeSupplier<ProductOptionValue[], Exception>
			productOptionValuesUnsafeSupplier) {

		try {
			productOptionValues = productOptionValuesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductOptionValue[] productOptionValues;

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		try {
			required = requiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean required;

	public Boolean getSkuContributor() {
		return skuContributor;
	}

	public void setSkuContributor(Boolean skuContributor) {
		this.skuContributor = skuContributor;
	}

	public void setSkuContributor(
		UnsafeSupplier<Boolean, Exception> skuContributorUnsafeSupplier) {

		try {
			skuContributor = skuContributorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean skuContributor;

	public String getTypeSettings() {
		return typeSettings;
	}

	public void setTypeSettings(String typeSettings) {
		this.typeSettings = typeSettings;
	}

	public void setTypeSettings(
		UnsafeSupplier<String, Exception> typeSettingsUnsafeSupplier) {

		try {
			typeSettings = typeSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String typeSettings;

	@Override
	public ProductOption clone() throws CloneNotSupportedException {
		return (ProductOption)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductOption)) {
			return false;
		}

		ProductOption productOption = (ProductOption)object;

		return Objects.equals(toString(), productOption.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProductOptionSerDes.toJSON(this);
	}

}