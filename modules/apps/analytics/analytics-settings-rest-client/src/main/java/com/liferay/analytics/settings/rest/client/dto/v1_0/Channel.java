/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.dto.v1_0;

import com.liferay.analytics.settings.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.settings.rest.client.serdes.v1_0.ChannelSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class Channel implements Cloneable, Serializable {

	public static Channel toDTO(String json) {
		return ChannelSerDes.toDTO(json);
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public void setChannelId(
		UnsafeSupplier<String, Exception> channelIdUnsafeSupplier) {

		try {
			channelId = channelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String channelId;

	public Boolean getCommerceSyncEnabled() {
		return commerceSyncEnabled;
	}

	public void setCommerceSyncEnabled(Boolean commerceSyncEnabled) {
		this.commerceSyncEnabled = commerceSyncEnabled;
	}

	public void setCommerceSyncEnabled(
		UnsafeSupplier<Boolean, Exception> commerceSyncEnabledUnsafeSupplier) {

		try {
			commerceSyncEnabled = commerceSyncEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean commerceSyncEnabled;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		try {
			createDate = createDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date createDate;

	public DataSource[] getDataSources() {
		return dataSources;
	}

	public void setDataSources(DataSource[] dataSources) {
		this.dataSources = dataSources;
	}

	public void setDataSources(
		UnsafeSupplier<DataSource[], Exception> dataSourcesUnsafeSupplier) {

		try {
			dataSources = dataSourcesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DataSource[] dataSources;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	@Override
	public Channel clone() throws CloneNotSupportedException {
		return (Channel)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Channel)) {
			return false;
		}

		Channel channel = (Channel)object;

		return Objects.equals(toString(), channel.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ChannelSerDes.toJSON(this);
	}

}