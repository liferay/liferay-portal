/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.dto.v1_0;

import com.liferay.portal.search.rest.client.function.UnsafeSupplier;
import com.liferay.portal.search.rest.client.serdes.v1_0.EmbeddingProviderValidationResultSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class EmbeddingProviderValidationResult
	implements Cloneable, Serializable {

	public static EmbeddingProviderValidationResult toDTO(String json) {
		return EmbeddingProviderValidationResultSerDes.toDTO(json);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setErrorMessage(
		UnsafeSupplier<String, Exception> errorMessageUnsafeSupplier) {

		try {
			errorMessage = errorMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String errorMessage;

	public Integer getExpectedDimensions() {
		return expectedDimensions;
	}

	public void setExpectedDimensions(Integer expectedDimensions) {
		this.expectedDimensions = expectedDimensions;
	}

	public void setExpectedDimensions(
		UnsafeSupplier<Integer, Exception> expectedDimensionsUnsafeSupplier) {

		try {
			expectedDimensions = expectedDimensionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer expectedDimensions;

	@Override
	public EmbeddingProviderValidationResult clone()
		throws CloneNotSupportedException {

		return (EmbeddingProviderValidationResult)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EmbeddingProviderValidationResult)) {
			return false;
		}

		EmbeddingProviderValidationResult embeddingProviderValidationResult =
			(EmbeddingProviderValidationResult)object;

		return Objects.equals(
			toString(), embeddingProviderValidationResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return EmbeddingProviderValidationResultSerDes.toJSON(this);
	}

}