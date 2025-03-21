/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.ServiceProviderConfigSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class ServiceProviderConfig implements Cloneable, Serializable {

	public static ServiceProviderConfig toDTO(String json) {
		return ServiceProviderConfigSerDes.toDTO(json);
	}

	public AuthenticationScheme[] getAuthenticationSchemes() {
		return authenticationSchemes;
	}

	public void setAuthenticationSchemes(
		AuthenticationScheme[] authenticationSchemes) {

		this.authenticationSchemes = authenticationSchemes;
	}

	public void setAuthenticationSchemes(
		UnsafeSupplier<AuthenticationScheme[], Exception>
			authenticationSchemesUnsafeSupplier) {

		try {
			authenticationSchemes = authenticationSchemesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AuthenticationScheme[] authenticationSchemes;

	public Bulk getBulk() {
		return bulk;
	}

	public void setBulk(Bulk bulk) {
		this.bulk = bulk;
	}

	public void setBulk(UnsafeSupplier<Bulk, Exception> bulkUnsafeSupplier) {
		try {
			bulk = bulkUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Bulk bulk;

	public ChangePassword getChangePassword() {
		return changePassword;
	}

	public void setChangePassword(ChangePassword changePassword) {
		this.changePassword = changePassword;
	}

	public void setChangePassword(
		UnsafeSupplier<ChangePassword, Exception>
			changePasswordUnsafeSupplier) {

		try {
			changePassword = changePasswordUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ChangePassword changePassword;

	public String getDocumentationUri() {
		return documentationUri;
	}

	public void setDocumentationUri(String documentationUri) {
		this.documentationUri = documentationUri;
	}

	public void setDocumentationUri(
		UnsafeSupplier<String, Exception> documentationUriUnsafeSupplier) {

		try {
			documentationUri = documentationUriUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String documentationUri;

	public Etag getEtag() {
		return etag;
	}

	public void setEtag(Etag etag) {
		this.etag = etag;
	}

	public void setEtag(UnsafeSupplier<Etag, Exception> etagUnsafeSupplier) {
		try {
			etag = etagUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Etag etag;

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public void setFilter(
		UnsafeSupplier<Filter, Exception> filterUnsafeSupplier) {

		try {
			filter = filterUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Filter filter;

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public void setMeta(UnsafeSupplier<Meta, Exception> metaUnsafeSupplier) {
		try {
			meta = metaUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Meta meta;

	public Patch getPatch() {
		return patch;
	}

	public void setPatch(Patch patch) {
		this.patch = patch;
	}

	public void setPatch(UnsafeSupplier<Patch, Exception> patchUnsafeSupplier) {
		try {
			patch = patchUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Patch patch;

	public String[] getSchemas() {
		return schemas;
	}

	public void setSchemas(String[] schemas) {
		this.schemas = schemas;
	}

	public void setSchemas(
		UnsafeSupplier<String[], Exception> schemasUnsafeSupplier) {

		try {
			schemas = schemasUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] schemas;

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public void setSort(UnsafeSupplier<Sort, Exception> sortUnsafeSupplier) {
		try {
			sort = sortUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Sort sort;

	@Override
	public ServiceProviderConfig clone() throws CloneNotSupportedException {
		return (ServiceProviderConfig)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ServiceProviderConfig)) {
			return false;
		}

		ServiceProviderConfig serviceProviderConfig =
			(ServiceProviderConfig)object;

		return Objects.equals(toString(), serviceProviderConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ServiceProviderConfigSerDes.toJSON(this);
	}

}