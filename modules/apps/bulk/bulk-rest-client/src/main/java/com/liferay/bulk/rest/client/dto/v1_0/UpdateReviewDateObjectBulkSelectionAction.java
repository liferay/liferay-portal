/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.dto.v1_0;

import com.liferay.bulk.rest.client.function.UnsafeSupplier;
import com.liferay.bulk.rest.client.serdes.v1_0.UpdateReviewDateObjectBulkSelectionActionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class UpdateReviewDateObjectBulkSelectionAction
	extends BulkAction implements Cloneable, Serializable {

	public static UpdateReviewDateObjectBulkSelectionAction toDTO(String json) {
		return UpdateReviewDateObjectBulkSelectionActionSerDes.toDTO(json);
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public void setReviewDate(
		UnsafeSupplier<Date, Exception> reviewDateUnsafeSupplier) {

		try {
			reviewDate = reviewDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date reviewDate;

	@Override
	public UpdateReviewDateObjectBulkSelectionAction clone()
		throws CloneNotSupportedException {

		return (UpdateReviewDateObjectBulkSelectionAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UpdateReviewDateObjectBulkSelectionAction)) {
			return false;
		}

		UpdateReviewDateObjectBulkSelectionAction
			updateReviewDateObjectBulkSelectionAction =
				(UpdateReviewDateObjectBulkSelectionAction)object;

		return Objects.equals(
			toString(), updateReviewDateObjectBulkSelectionAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UpdateReviewDateObjectBulkSelectionActionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:124173355