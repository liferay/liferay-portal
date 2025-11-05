/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.dto.v1_0;

import com.liferay.bulk.rest.client.function.UnsafeSupplier;
import com.liferay.bulk.rest.client.serdes.v1_0.TaxonomyCategoryBulkActionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class TaxonomyCategoryBulkAction
	extends BulkAction implements Cloneable, Serializable {

	public static TaxonomyCategoryBulkAction toDTO(String json) {
		return TaxonomyCategoryBulkActionSerDes.toDTO(json);
	}

	public Long[] getTaxonomyCategoryIdsToAdd() {
		return taxonomyCategoryIdsToAdd;
	}

	public void setTaxonomyCategoryIdsToAdd(Long[] taxonomyCategoryIdsToAdd) {
		this.taxonomyCategoryIdsToAdd = taxonomyCategoryIdsToAdd;
	}

	public void setTaxonomyCategoryIdsToAdd(
		UnsafeSupplier<Long[], Exception>
			taxonomyCategoryIdsToAddUnsafeSupplier) {

		try {
			taxonomyCategoryIdsToAdd =
				taxonomyCategoryIdsToAddUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] taxonomyCategoryIdsToAdd;

	public Long[] getTaxonomyCategoryIdsToRemove() {
		return taxonomyCategoryIdsToRemove;
	}

	public void setTaxonomyCategoryIdsToRemove(
		Long[] taxonomyCategoryIdsToRemove) {

		this.taxonomyCategoryIdsToRemove = taxonomyCategoryIdsToRemove;
	}

	public void setTaxonomyCategoryIdsToRemove(
		UnsafeSupplier<Long[], Exception>
			taxonomyCategoryIdsToRemoveUnsafeSupplier) {

		try {
			taxonomyCategoryIdsToRemove =
				taxonomyCategoryIdsToRemoveUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] taxonomyCategoryIdsToRemove;

	@Override
	public TaxonomyCategoryBulkAction clone()
		throws CloneNotSupportedException {

		return (TaxonomyCategoryBulkAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TaxonomyCategoryBulkAction)) {
			return false;
		}

		TaxonomyCategoryBulkAction taxonomyCategoryBulkAction =
			(TaxonomyCategoryBulkAction)object;

		return Objects.equals(
			toString(), taxonomyCategoryBulkAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TaxonomyCategoryBulkActionSerDes.toJSON(this);
	}

}