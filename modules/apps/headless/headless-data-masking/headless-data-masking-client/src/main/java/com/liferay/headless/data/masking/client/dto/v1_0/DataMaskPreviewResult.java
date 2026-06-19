/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.client.dto.v1_0;

import com.liferay.headless.data.masking.client.function.UnsafeSupplier;
import com.liferay.headless.data.masking.client.serdes.v1_0.DataMaskPreviewResultSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Jose Luis Navarro
 * @generated
 */
@Generated("")
public class DataMaskPreviewResult implements Cloneable, Serializable {

	public static DataMaskPreviewResult toDTO(String json) {
		return DataMaskPreviewResultSerDes.toDTO(json);
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setError(
		UnsafeSupplier<String, Exception> errorUnsafeSupplier) {

		try {
			error = errorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String error;

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setOutput(
		UnsafeSupplier<String, Exception> outputUnsafeSupplier) {

		try {
			output = outputUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String output;

	@Override
	public DataMaskPreviewResult clone() throws CloneNotSupportedException {
		return (DataMaskPreviewResult)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataMaskPreviewResult)) {
			return false;
		}

		DataMaskPreviewResult dataMaskPreviewResult =
			(DataMaskPreviewResult)object;

		return Objects.equals(toString(), dataMaskPreviewResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DataMaskPreviewResultSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:640748606