/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.client.dto.v1_0;

import com.liferay.headless.data.masking.client.function.UnsafeSupplier;
import com.liferay.headless.data.masking.client.serdes.v1_0.DataMaskPreviewRequestSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Jose Luis Navarro
 * @generated
 */
@Generated("")
public class DataMaskPreviewRequest implements Cloneable, Serializable {

	public static DataMaskPreviewRequest toDTO(String json) {
		return DataMaskPreviewRequestSerDes.toDTO(json);
	}

	public String getDetectionRegex() {
		return detectionRegex;
	}

	public void setDetectionRegex(String detectionRegex) {
		this.detectionRegex = detectionRegex;
	}

	public void setDetectionRegex(
		UnsafeSupplier<String, Exception> detectionRegexUnsafeSupplier) {

		try {
			detectionRegex = detectionRegexUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String detectionRegex;

	public String getReplacementRegex() {
		return replacementRegex;
	}

	public void setReplacementRegex(String replacementRegex) {
		this.replacementRegex = replacementRegex;
	}

	public void setReplacementRegex(
		UnsafeSupplier<String, Exception> replacementRegexUnsafeSupplier) {

		try {
			replacementRegex = replacementRegexUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String replacementRegex;

	public String getReplacementValue() {
		return replacementValue;
	}

	public void setReplacementValue(String replacementValue) {
		this.replacementValue = replacementValue;
	}

	public void setReplacementValue(
		UnsafeSupplier<String, Exception> replacementValueUnsafeSupplier) {

		try {
			replacementValue = replacementValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String replacementValue;

	public String getSampleText() {
		return sampleText;
	}

	public void setSampleText(String sampleText) {
		this.sampleText = sampleText;
	}

	public void setSampleText(
		UnsafeSupplier<String, Exception> sampleTextUnsafeSupplier) {

		try {
			sampleText = sampleTextUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sampleText;

	@Override
	public DataMaskPreviewRequest clone() throws CloneNotSupportedException {
		return (DataMaskPreviewRequest)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataMaskPreviewRequest)) {
			return false;
		}

		DataMaskPreviewRequest dataMaskPreviewRequest =
			(DataMaskPreviewRequest)object;

		return Objects.equals(toString(), dataMaskPreviewRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DataMaskPreviewRequestSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1565288128