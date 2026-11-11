/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.RemoteConnectionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class RemoteConnection implements Cloneable, Serializable {

	public static RemoteConnection toDTO(String json) {
		return RemoteConnectionSerDes.toDTO(json);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setAddress(
		UnsafeSupplier<String, Exception> addressUnsafeSupplier) {

		try {
			address = addressUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String address;

	public String getPathContext() {
		return pathContext;
	}

	public void setPathContext(String pathContext) {
		this.pathContext = pathContext;
	}

	public void setPathContext(
		UnsafeSupplier<String, Exception> pathContextUnsafeSupplier) {

		try {
			pathContext = pathContextUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String pathContext;

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setPort(UnsafeSupplier<Integer, Exception> portUnsafeSupplier) {
		try {
			port = portUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer port;

	public Boolean getSecureConnection() {
		return secureConnection;
	}

	public void setSecureConnection(Boolean secureConnection) {
		this.secureConnection = secureConnection;
	}

	public void setSecureConnection(
		UnsafeSupplier<Boolean, Exception> secureConnectionUnsafeSupplier) {

		try {
			secureConnection = secureConnectionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean secureConnection;

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		try {
			siteId = siteIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long siteId;

	@Override
	public RemoteConnection clone() throws CloneNotSupportedException {
		return (RemoteConnection)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RemoteConnection)) {
			return false;
		}

		RemoteConnection remoteConnection = (RemoteConnection)object;

		return Objects.equals(toString(), remoteConnection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return RemoteConnectionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1786261345