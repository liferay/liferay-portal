/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.client.dto.v1_0;

import com.liferay.headless.portal.instances.client.function.UnsafeSupplier;
import com.liferay.headless.portal.instances.client.serdes.v1_0.PortalInstanceCopySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
public class PortalInstanceCopy implements Cloneable, Serializable {

	public static PortalInstanceCopy toDTO(String json) {
		return PortalInstanceCopySerDes.toDTO(json);
	}

	public Long getDestinationCompanyId() {
		return destinationCompanyId;
	}

	public void setDestinationCompanyId(Long destinationCompanyId) {
		this.destinationCompanyId = destinationCompanyId;
	}

	public void setDestinationCompanyId(
		UnsafeSupplier<Long, Exception> destinationCompanyIdUnsafeSupplier) {

		try {
			destinationCompanyId = destinationCompanyIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long destinationCompanyId;

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

	public String getVirtualHost() {
		return virtualHost;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

	public void setVirtualHost(
		UnsafeSupplier<String, Exception> virtualHostUnsafeSupplier) {

		try {
			virtualHost = virtualHostUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String virtualHost;

	public String getWebId() {
		return webId;
	}

	public void setWebId(String webId) {
		this.webId = webId;
	}

	public void setWebId(
		UnsafeSupplier<String, Exception> webIdUnsafeSupplier) {

		try {
			webId = webIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String webId;

	@Override
	public PortalInstanceCopy clone() throws CloneNotSupportedException {
		return (PortalInstanceCopy)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortalInstanceCopy)) {
			return false;
		}

		PortalInstanceCopy portalInstanceCopy = (PortalInstanceCopy)object;

		return Objects.equals(toString(), portalInstanceCopy.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PortalInstanceCopySerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1292188389