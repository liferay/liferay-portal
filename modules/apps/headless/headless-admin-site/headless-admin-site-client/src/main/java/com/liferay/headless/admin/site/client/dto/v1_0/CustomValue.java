/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.CustomValueSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CustomValue implements Cloneable, Serializable {

	public static CustomValue toDTO(String json) {
		return CustomValueSerDes.toDTO(json);
	}

	public Map<String, String> getData_i18n() {
		return data_i18n;
	}

	public void setData_i18n(Map<String, String> data_i18n) {
		this.data_i18n = data_i18n;
	}

	public void setData_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			data_i18nUnsafeSupplier) {

		try {
			data_i18n = data_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> data_i18n;

	public Geo getGeo() {
		return geo;
	}

	public void setGeo(Geo geo) {
		this.geo = geo;
	}

	public void setGeo(UnsafeSupplier<Geo, Exception> geoUnsafeSupplier) {
		try {
			geo = geoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Geo geo;

	@Override
	public CustomValue clone() throws CloneNotSupportedException {
		return (CustomValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CustomValue)) {
			return false;
		}

		CustomValue customValue = (CustomValue)object;

		return Objects.equals(toString(), customValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CustomValueSerDes.toJSON(this);
	}

}