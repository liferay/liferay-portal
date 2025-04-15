/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.client.dto.v1_0;

import com.liferay.headless.portal.instances.client.function.UnsafeSupplier;
import com.liferay.headless.portal.instances.client.serdes.v1_0.PortalInstanceSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
public class PortalInstance implements Cloneable, Serializable {

	public static PortalInstance toDTO(String json) {
		return PortalInstanceSerDes.toDTO(json);
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public void setAdmin(UnsafeSupplier<Admin, Exception> adminUnsafeSupplier) {
		try {
			admin = adminUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Admin admin;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setCompanyId(
		UnsafeSupplier<Long, Exception> companyIdUnsafeSupplier) {

		try {
			companyId = companyIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long companyId;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setDomain(
		UnsafeSupplier<String, Exception> domainUnsafeSupplier) {

		try {
			domain = domainUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String domain;

	public String getPortalInstanceId() {
		return portalInstanceId;
	}

	public void setPortalInstanceId(String portalInstanceId) {
		this.portalInstanceId = portalInstanceId;
	}

	public void setPortalInstanceId(
		UnsafeSupplier<String, Exception> portalInstanceIdUnsafeSupplier) {

		try {
			portalInstanceId = portalInstanceIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String portalInstanceId;

	public String getSiteInitializerKey() {
		return siteInitializerKey;
	}

	public void setSiteInitializerKey(String siteInitializerKey) {
		this.siteInitializerKey = siteInitializerKey;
	}

	public void setSiteInitializerKey(
		UnsafeSupplier<String, Exception> siteInitializerKeyUnsafeSupplier) {

		try {
			siteInitializerKey = siteInitializerKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String siteInitializerKey;

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

	@Override
	public PortalInstance clone() throws CloneNotSupportedException {
		return (PortalInstance)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortalInstance)) {
			return false;
		}

		PortalInstance portalInstance = (PortalInstance)object;

		return Objects.equals(toString(), portalInstance.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PortalInstanceSerDes.toJSON(this);
	}

}