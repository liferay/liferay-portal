/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.dto.v1_0;

import com.liferay.headless.commerce.admin.channel.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.CategoryDisplayPageSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class CategoryDisplayPage implements Cloneable, Serializable {

	public static CategoryDisplayPage toDTO(String json) {
		return CategoryDisplayPageSerDes.toDTO(json);
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

	public String getGroupExternalReferenceCode() {
		return groupExternalReferenceCode;
	}

	public void setGroupExternalReferenceCode(
		String groupExternalReferenceCode) {

		this.groupExternalReferenceCode = groupExternalReferenceCode;
	}

	public void setGroupExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			groupExternalReferenceCodeUnsafeSupplier) {

		try {
			groupExternalReferenceCode =
				groupExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String groupExternalReferenceCode;

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

	public String getPageUuid() {
		return pageUuid;
	}

	public void setPageUuid(String pageUuid) {
		this.pageUuid = pageUuid;
	}

	public void setPageUuid(
		UnsafeSupplier<String, Exception> pageUuidUnsafeSupplier) {

		try {
			pageUuid = pageUuidUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String pageUuid;

	@Override
	public CategoryDisplayPage clone() throws CloneNotSupportedException {
		return (CategoryDisplayPage)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CategoryDisplayPage)) {
			return false;
		}

		CategoryDisplayPage categoryDisplayPage = (CategoryDisplayPage)object;

		return Objects.equals(toString(), categoryDisplayPage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CategoryDisplayPageSerDes.toJSON(this);
	}

}