/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.dto.v1_0;

import com.liferay.bulk.rest.client.function.UnsafeSupplier;
import com.liferay.bulk.rest.client.serdes.v1_0.DocumentBulkSelectionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class DocumentBulkSelection implements Cloneable, Serializable {

	public static DocumentBulkSelection toDTO(String json) {
		return DocumentBulkSelectionSerDes.toDTO(json);
	}

	public String[] getDocumentIds() {
		return documentIds;
	}

	public void setDocumentIds(String[] documentIds) {
		this.documentIds = documentIds;
	}

	public void setDocumentIds(
		UnsafeSupplier<String[], Exception> documentIdsUnsafeSupplier) {

		try {
			documentIds = documentIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] documentIds;

	public SelectionScope getSelectionScope() {
		return selectionScope;
	}

	public void setSelectionScope(SelectionScope selectionScope) {
		this.selectionScope = selectionScope;
	}

	public void setSelectionScope(
		UnsafeSupplier<SelectionScope, Exception>
			selectionScopeUnsafeSupplier) {

		try {
			selectionScope = selectionScopeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SelectionScope selectionScope;

	@Override
	public DocumentBulkSelection clone() throws CloneNotSupportedException {
		return (DocumentBulkSelection)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DocumentBulkSelection)) {
			return false;
		}

		DocumentBulkSelection documentBulkSelection =
			(DocumentBulkSelection)object;

		return Objects.equals(toString(), documentBulkSelection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DocumentBulkSelectionSerDes.toJSON(this);
	}

}