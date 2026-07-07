/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.client.dto.v1_0;

import com.liferay.headless.data.mask.client.function.UnsafeSupplier;
import com.liferay.headless.data.mask.client.serdes.v1_0.RedactionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Jose Luis Navarro
 * @generated
 */
@Generated("")
public class Redaction implements Cloneable, Serializable {

	public static Redaction toDTO(String json) {
		return RedactionSerDes.toDTO(json);
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
	public Redaction clone() throws CloneNotSupportedException {
		return (Redaction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Redaction)) {
			return false;
		}

		Redaction redaction = (Redaction)object;

		return Objects.equals(toString(), redaction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return RedactionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1003522558