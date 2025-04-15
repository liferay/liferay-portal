/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.dto.v1_0;

import com.liferay.bulk.rest.client.function.UnsafeSupplier;
import com.liferay.bulk.rest.client.serdes.v1_0.TaxonomyCategoryBulkSelectionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class TaxonomyCategoryBulkSelection implements Cloneable, Serializable {

	public static TaxonomyCategoryBulkSelection toDTO(String json) {
		return TaxonomyCategoryBulkSelectionSerDes.toDTO(json);
	}

	public DocumentBulkSelection getDocumentBulkSelection() {
		return documentBulkSelection;
	}

	public void setDocumentBulkSelection(
		DocumentBulkSelection documentBulkSelection) {

		this.documentBulkSelection = documentBulkSelection;
	}

	public void setDocumentBulkSelection(
		UnsafeSupplier<DocumentBulkSelection, Exception>
			documentBulkSelectionUnsafeSupplier) {

		try {
			documentBulkSelection = documentBulkSelectionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DocumentBulkSelection documentBulkSelection;

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
	public TaxonomyCategoryBulkSelection clone()
		throws CloneNotSupportedException {

		return (TaxonomyCategoryBulkSelection)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TaxonomyCategoryBulkSelection)) {
			return false;
		}

		TaxonomyCategoryBulkSelection taxonomyCategoryBulkSelection =
			(TaxonomyCategoryBulkSelection)object;

		return Objects.equals(
			toString(), taxonomyCategoryBulkSelection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TaxonomyCategoryBulkSelectionSerDes.toJSON(this);
	}

}