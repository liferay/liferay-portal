/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.dto.v1_0;

import com.liferay.analytics.settings.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.settings.rest.client.serdes.v1_0.DataSourceLiferayAnalyticsURLSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class DataSourceLiferayAnalyticsURL implements Cloneable, Serializable {

	public static DataSourceLiferayAnalyticsURL toDTO(String json) {
		return DataSourceLiferayAnalyticsURLSerDes.toDTO(json);
	}

	public String getLiferayAnalyticsURL() {
		return liferayAnalyticsURL;
	}

	public void setLiferayAnalyticsURL(String liferayAnalyticsURL) {
		this.liferayAnalyticsURL = liferayAnalyticsURL;
	}

	public void setLiferayAnalyticsURL(
		UnsafeSupplier<String, Exception> liferayAnalyticsURLUnsafeSupplier) {

		try {
			liferayAnalyticsURL = liferayAnalyticsURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String liferayAnalyticsURL;

	@Override
	public DataSourceLiferayAnalyticsURL clone()
		throws CloneNotSupportedException {

		return (DataSourceLiferayAnalyticsURL)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataSourceLiferayAnalyticsURL)) {
			return false;
		}

		DataSourceLiferayAnalyticsURL dataSourceLiferayAnalyticsURL =
			(DataSourceLiferayAnalyticsURL)object;

		return Objects.equals(
			toString(), dataSourceLiferayAnalyticsURL.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DataSourceLiferayAnalyticsURLSerDes.toJSON(this);
	}

}