/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.dto.v1_0;

import com.liferay.bulk.rest.client.function.UnsafeSupplier;
import com.liferay.bulk.rest.client.serdes.v1_0.KeywordBulkSelectionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class KeywordBulkSelection implements Cloneable, Serializable {

	public static KeywordBulkSelection toDTO(String json) {
		return KeywordBulkSelectionSerDes.toDTO(json);
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

	public String[] getKeywordsToAdd() {
		return keywordsToAdd;
	}

	public void setKeywordsToAdd(String[] keywordsToAdd) {
		this.keywordsToAdd = keywordsToAdd;
	}

	public void setKeywordsToAdd(
		UnsafeSupplier<String[], Exception> keywordsToAddUnsafeSupplier) {

		try {
			keywordsToAdd = keywordsToAddUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] keywordsToAdd;

	public String[] getKeywordsToRemove() {
		return keywordsToRemove;
	}

	public void setKeywordsToRemove(String[] keywordsToRemove) {
		this.keywordsToRemove = keywordsToRemove;
	}

	public void setKeywordsToRemove(
		UnsafeSupplier<String[], Exception> keywordsToRemoveUnsafeSupplier) {

		try {
			keywordsToRemove = keywordsToRemoveUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] keywordsToRemove;

	@Override
	public KeywordBulkSelection clone() throws CloneNotSupportedException {
		return (KeywordBulkSelection)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof KeywordBulkSelection)) {
			return false;
		}

		KeywordBulkSelection keywordBulkSelection =
			(KeywordBulkSelection)object;

		return Objects.equals(toString(), keywordBulkSelection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return KeywordBulkSelectionSerDes.toJSON(this);
	}

}