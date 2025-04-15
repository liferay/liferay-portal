/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.dto.v1_0;

import com.liferay.analytics.settings.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.settings.rest.client.serdes.v1_0.DataSourceSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class DataSource implements Cloneable, Serializable {

	public static DataSource toDTO(String json) {
		return DataSourceSerDes.toDTO(json);
	}

	public Long[] getCommerceChannelIds() {
		return commerceChannelIds;
	}

	public void setCommerceChannelIds(Long[] commerceChannelIds) {
		this.commerceChannelIds = commerceChannelIds;
	}

	public void setCommerceChannelIds(
		UnsafeSupplier<Long[], Exception> commerceChannelIdsUnsafeSupplier) {

		try {
			commerceChannelIds = commerceChannelIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] commerceChannelIds;

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public void setDataSourceId(
		UnsafeSupplier<String, Exception> dataSourceIdUnsafeSupplier) {

		try {
			dataSourceId = dataSourceIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String dataSourceId;

	public Long[] getSiteIds() {
		return siteIds;
	}

	public void setSiteIds(Long[] siteIds) {
		this.siteIds = siteIds;
	}

	public void setSiteIds(
		UnsafeSupplier<Long[], Exception> siteIdsUnsafeSupplier) {

		try {
			siteIds = siteIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] siteIds;

	@Override
	public DataSource clone() throws CloneNotSupportedException {
		return (DataSource)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataSource)) {
			return false;
		}

		DataSource dataSource = (DataSource)object;

		return Objects.equals(toString(), dataSource.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DataSourceSerDes.toJSON(this);
	}

}