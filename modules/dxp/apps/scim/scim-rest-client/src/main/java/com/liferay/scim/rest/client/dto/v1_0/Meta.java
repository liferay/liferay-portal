/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.MetaSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class Meta implements Cloneable, Serializable {

	public static Meta toDTO(String json) {
		return MetaSerDes.toDTO(json);
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setCreated(
		UnsafeSupplier<Date, Exception> createdUnsafeSupplier) {

		try {
			created = createdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date created;

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public void setLastModified(
		UnsafeSupplier<Date, Exception> lastModifiedUnsafeSupplier) {

		try {
			lastModified = lastModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date lastModified;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setLocation(
		UnsafeSupplier<String, Exception> locationUnsafeSupplier) {

		try {
			location = locationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String location;

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setResourceType(
		UnsafeSupplier<String, Exception> resourceTypeUnsafeSupplier) {

		try {
			resourceType = resourceTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String resourceType;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setVersion(
		UnsafeSupplier<String, Exception> versionUnsafeSupplier) {

		try {
			version = versionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String version;

	@Override
	public Meta clone() throws CloneNotSupportedException {
		return (Meta)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Meta)) {
			return false;
		}

		Meta meta = (Meta)object;

		return Objects.equals(toString(), meta.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return MetaSerDes.toJSON(this);
	}

}