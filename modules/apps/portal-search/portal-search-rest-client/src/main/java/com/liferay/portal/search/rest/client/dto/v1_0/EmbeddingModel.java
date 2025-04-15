/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.dto.v1_0;

import com.liferay.portal.search.rest.client.function.UnsafeSupplier;
import com.liferay.portal.search.rest.client.serdes.v1_0.EmbeddingModelSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class EmbeddingModel implements Cloneable, Serializable {

	public static EmbeddingModel toDTO(String json) {
		return EmbeddingModelSerDes.toDTO(json);
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public void setModelId(
		UnsafeSupplier<String, Exception> modelIdUnsafeSupplier) {

		try {
			modelId = modelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String modelId;

	@Override
	public EmbeddingModel clone() throws CloneNotSupportedException {
		return (EmbeddingModel)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EmbeddingModel)) {
			return false;
		}

		EmbeddingModel embeddingModel = (EmbeddingModel)object;

		return Objects.equals(toString(), embeddingModel.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return EmbeddingModelSerDes.toJSON(this);
	}

}