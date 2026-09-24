/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.SkuOptionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class SkuOption implements Cloneable, Serializable {

	public static SkuOption toDTO(String json) {
		return SkuOptionSerDes.toDTO(json);
	}

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

	public String getOptionFieldType() {
		return optionFieldType;
	}

	public void setOptionFieldType(String optionFieldType) {
		this.optionFieldType = optionFieldType;
	}

	public void setOptionFieldType(
		UnsafeSupplier<String, Exception> optionFieldTypeUnsafeSupplier) {

		try {
			optionFieldType = optionFieldTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String optionFieldType;

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

	public Boolean getOptionSkuContributor() {
		return optionSkuContributor;
	}

	public void setOptionSkuContributor(Boolean optionSkuContributor) {
		this.optionSkuContributor = optionSkuContributor;
	}

	public void setOptionSkuContributor(
		UnsafeSupplier<Boolean, Exception> optionSkuContributorUnsafeSupplier) {

		try {
			optionSkuContributor = optionSkuContributorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean optionSkuContributor;

	public String getOptionValueExternalReferenceCode() {
		return optionValueExternalReferenceCode;
	}

	public void setOptionValueExternalReferenceCode(
		String optionValueExternalReferenceCode) {

		this.optionValueExternalReferenceCode =
			optionValueExternalReferenceCode;
	}

	public void setOptionValueExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			optionValueExternalReferenceCodeUnsafeSupplier) {

		try {
			optionValueExternalReferenceCode =
				optionValueExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String optionValueExternalReferenceCode;

	public Long getOptionValueId() {
		return optionValueId;
	}

	public void setOptionValueId(Long optionValueId) {
		this.optionValueId = optionValueId;
	}

	public void setOptionValueId(
		UnsafeSupplier<Long, Exception> optionValueIdUnsafeSupplier) {

		try {
			optionValueId = optionValueIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long optionValueId;

	public String getParentOptionExternalReferenceCode() {
		return parentOptionExternalReferenceCode;
	}

	public void setParentOptionExternalReferenceCode(
		String parentOptionExternalReferenceCode) {

		this.parentOptionExternalReferenceCode =
			parentOptionExternalReferenceCode;
	}

	public void setParentOptionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentOptionExternalReferenceCodeUnsafeSupplier) {

		try {
			parentOptionExternalReferenceCode =
				parentOptionExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentOptionExternalReferenceCode;

	public String getParentOptionFieldType() {
		return parentOptionFieldType;
	}

	public void setParentOptionFieldType(String parentOptionFieldType) {
		this.parentOptionFieldType = parentOptionFieldType;
	}

	public void setParentOptionFieldType(
		UnsafeSupplier<String, Exception> parentOptionFieldTypeUnsafeSupplier) {

		try {
			parentOptionFieldType = parentOptionFieldTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentOptionFieldType;

	public Boolean getParentOptionSkuContributor() {
		return parentOptionSkuContributor;
	}

	public void setParentOptionSkuContributor(
		Boolean parentOptionSkuContributor) {

		this.parentOptionSkuContributor = parentOptionSkuContributor;
	}

	public void setParentOptionSkuContributor(
		UnsafeSupplier<Boolean, Exception>
			parentOptionSkuContributorUnsafeSupplier) {

		try {
			parentOptionSkuContributor =
				parentOptionSkuContributorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean parentOptionSkuContributor;

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
// LIFERAY-REST-BUILDER-HASH:5553177