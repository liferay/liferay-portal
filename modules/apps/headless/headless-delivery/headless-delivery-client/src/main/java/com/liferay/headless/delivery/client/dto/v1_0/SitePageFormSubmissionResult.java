/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.SitePageFormSubmissionResultSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class SitePageFormSubmissionResult implements Cloneable, Serializable {

	public static SitePageFormSubmissionResult toDTO(String json) {
		return SitePageFormSubmissionResultSerDes.toDTO(json);
	}

	public ClassFieldsReference getItemReference() {
		return itemReference;
	}

	public void setItemReference(ClassFieldsReference itemReference) {
		this.itemReference = itemReference;
	}

	public void setItemReference(
		UnsafeSupplier<ClassFieldsReference, Exception>
			itemReferenceUnsafeSupplier) {

		try {
			itemReference = itemReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ClassFieldsReference itemReference;

	@Override
	public SitePageFormSubmissionResult clone()
		throws CloneNotSupportedException {

		return (SitePageFormSubmissionResult)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SitePageFormSubmissionResult)) {
			return false;
		}

		SitePageFormSubmissionResult sitePageFormSubmissionResult =
			(SitePageFormSubmissionResult)object;

		return Objects.equals(
			toString(), sitePageFormSubmissionResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SitePageFormSubmissionResultSerDes.toJSON(this);
	}

}