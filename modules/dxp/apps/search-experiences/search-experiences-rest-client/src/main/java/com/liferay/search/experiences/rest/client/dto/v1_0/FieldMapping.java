/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.FieldMappingSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class FieldMapping implements Cloneable, Serializable {

	public static FieldMapping toDTO(String json) {
		return FieldMappingSerDes.toDTO(json);
	}

	public Float getBoost() {
		return boost;
	}

	public void setBoost(Float boost) {
		this.boost = boost;
	}

	public void setBoost(UnsafeSupplier<Float, Exception> boostUnsafeSupplier) {
		try {
			boost = boostUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Float boost;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setField(
		UnsafeSupplier<String, Exception> fieldUnsafeSupplier) {

		try {
			field = fieldUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String field;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setLocale(
		UnsafeSupplier<String, Exception> localeUnsafeSupplier) {

		try {
			locale = localeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String locale;

	@Override
	public FieldMapping clone() throws CloneNotSupportedException {
		return (FieldMapping)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FieldMapping)) {
			return false;
		}

		FieldMapping fieldMapping = (FieldMapping)object;

		return Objects.equals(toString(), fieldMapping.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FieldMappingSerDes.toJSON(this);
	}

}