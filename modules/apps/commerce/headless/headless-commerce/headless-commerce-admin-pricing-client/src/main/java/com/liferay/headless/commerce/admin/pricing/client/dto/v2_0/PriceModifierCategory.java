/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.dto.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.PriceModifierCategorySerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class PriceModifierCategory implements Cloneable, Serializable {

	public static PriceModifierCategory toDTO(String json) {
		return PriceModifierCategorySerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setCategory(
		UnsafeSupplier<Category, Exception> categoryUnsafeSupplier) {

		try {
			category = categoryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Category category;

	public String getCategoryExternalReferenceCode() {
		return categoryExternalReferenceCode;
	}

	public void setCategoryExternalReferenceCode(
		String categoryExternalReferenceCode) {

		this.categoryExternalReferenceCode = categoryExternalReferenceCode;
	}

	public void setCategoryExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			categoryExternalReferenceCodeUnsafeSupplier) {

		try {
			categoryExternalReferenceCode =
				categoryExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String categoryExternalReferenceCode;

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setCategoryId(
		UnsafeSupplier<Long, Exception> categoryIdUnsafeSupplier) {

		try {
			categoryId = categoryIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long categoryId;

	public Long getPriceModifierCategoryId() {
		return priceModifierCategoryId;
	}

	public void setPriceModifierCategoryId(Long priceModifierCategoryId) {
		this.priceModifierCategoryId = priceModifierCategoryId;
	}

	public void setPriceModifierCategoryId(
		UnsafeSupplier<Long, Exception> priceModifierCategoryIdUnsafeSupplier) {

		try {
			priceModifierCategoryId =
				priceModifierCategoryIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long priceModifierCategoryId;

	public String getPriceModifierExternalReferenceCode() {
		return priceModifierExternalReferenceCode;
	}

	public void setPriceModifierExternalReferenceCode(
		String priceModifierExternalReferenceCode) {

		this.priceModifierExternalReferenceCode =
			priceModifierExternalReferenceCode;
	}

	public void setPriceModifierExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			priceModifierExternalReferenceCodeUnsafeSupplier) {

		try {
			priceModifierExternalReferenceCode =
				priceModifierExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String priceModifierExternalReferenceCode;

	public Long getPriceModifierId() {
		return priceModifierId;
	}

	public void setPriceModifierId(Long priceModifierId) {
		this.priceModifierId = priceModifierId;
	}

	public void setPriceModifierId(
		UnsafeSupplier<Long, Exception> priceModifierIdUnsafeSupplier) {

		try {
			priceModifierId = priceModifierIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long priceModifierId;

	@Override
	public PriceModifierCategory clone() throws CloneNotSupportedException {
		return (PriceModifierCategory)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PriceModifierCategory)) {
			return false;
		}

		PriceModifierCategory priceModifierCategory =
			(PriceModifierCategory)object;

		return Objects.equals(toString(), priceModifierCategory.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PriceModifierCategorySerDes.toJSON(this);
	}

}