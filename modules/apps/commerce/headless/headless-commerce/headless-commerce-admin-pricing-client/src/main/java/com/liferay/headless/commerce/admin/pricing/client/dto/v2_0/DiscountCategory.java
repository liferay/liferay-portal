/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.dto.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.DiscountCategorySerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class DiscountCategory implements Cloneable, Serializable {

	public static DiscountCategory toDTO(String json) {
		return DiscountCategorySerDes.toDTO(json);
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

	public Long getDiscountCategoryId() {
		return discountCategoryId;
	}

	public void setDiscountCategoryId(Long discountCategoryId) {
		this.discountCategoryId = discountCategoryId;
	}

	public void setDiscountCategoryId(
		UnsafeSupplier<Long, Exception> discountCategoryIdUnsafeSupplier) {

		try {
			discountCategoryId = discountCategoryIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long discountCategoryId;

	public String getDiscountExternalReferenceCode() {
		return discountExternalReferenceCode;
	}

	public void setDiscountExternalReferenceCode(
		String discountExternalReferenceCode) {

		this.discountExternalReferenceCode = discountExternalReferenceCode;
	}

	public void setDiscountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			discountExternalReferenceCodeUnsafeSupplier) {

		try {
			discountExternalReferenceCode =
				discountExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String discountExternalReferenceCode;

	public Long getDiscountId() {
		return discountId;
	}

	public void setDiscountId(Long discountId) {
		this.discountId = discountId;
	}

	public void setDiscountId(
		UnsafeSupplier<Long, Exception> discountIdUnsafeSupplier) {

		try {
			discountId = discountIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long discountId;

	@Override
	public DiscountCategory clone() throws CloneNotSupportedException {
		return (DiscountCategory)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DiscountCategory)) {
			return false;
		}

		DiscountCategory discountCategory = (DiscountCategory)object;

		return Objects.equals(toString(), discountCategory.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DiscountCategorySerDes.toJSON(this);
	}

}