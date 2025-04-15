/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.ObjectReviewedSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectReviewed implements Cloneable, Serializable {

	public static ObjectReviewed toDTO(String json) {
		return ObjectReviewedSerDes.toDTO(json);
	}

	public String getAssetTitle() {
		return assetTitle;
	}

	public void setAssetTitle(String assetTitle) {
		this.assetTitle = assetTitle;
	}

	public void setAssetTitle(
		UnsafeSupplier<String, Exception> assetTitleUnsafeSupplier) {

		try {
			assetTitle = assetTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String assetTitle;

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public void setAssetType(
		UnsafeSupplier<String, Exception> assetTypeUnsafeSupplier) {

		try {
			assetType = assetTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String assetType;

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

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setResourceType(
		UnsafeSupplier<String, Exception> resourceTypeUnsafeSupplier) {

		try {
			resourceType = resourceTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String resourceType;

	@Override
	public ObjectReviewed clone() throws CloneNotSupportedException {
		return (ObjectReviewed)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectReviewed)) {
			return false;
		}

		ObjectReviewed objectReviewed = (ObjectReviewed)object;

		return Objects.equals(toString(), objectReviewed.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectReviewedSerDes.toJSON(this);
	}

}