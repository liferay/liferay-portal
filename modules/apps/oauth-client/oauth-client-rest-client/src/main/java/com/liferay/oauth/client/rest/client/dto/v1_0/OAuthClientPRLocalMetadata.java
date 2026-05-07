/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.client.dto.v1_0;

import com.liferay.oauth.client.rest.client.function.UnsafeSupplier;
import com.liferay.oauth.client.rest.client.serdes.v1_0.OAuthClientPRLocalMetadataSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Manuele Castro
 * @generated
 */
@Generated("")
public class OAuthClientPRLocalMetadata implements Cloneable, Serializable {

	public static OAuthClientPRLocalMetadata toDTO(String json) {
		return OAuthClientPRLocalMetadataSerDes.toDTO(json);
	}

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Boolean getLocalWellKnownEnabled() {
		return localWellKnownEnabled;
	}

	public void setLocalWellKnownEnabled(Boolean localWellKnownEnabled) {
		this.localWellKnownEnabled = localWellKnownEnabled;
	}

	public void setLocalWellKnownEnabled(
		UnsafeSupplier<Boolean, Exception>
			localWellKnownEnabledUnsafeSupplier) {

		try {
			localWellKnownEnabled = localWellKnownEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean localWellKnownEnabled;

	public String getLocalWellKnownURI() {
		return localWellKnownURI;
	}

	public void setLocalWellKnownURI(String localWellKnownURI) {
		this.localWellKnownURI = localWellKnownURI;
	}

	public void setLocalWellKnownURI(
		UnsafeSupplier<String, Exception> localWellKnownURIUnsafeSupplier) {

		try {
			localWellKnownURI = localWellKnownURIUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String localWellKnownURI;

	public String getMetadataJSON() {
		return metadataJSON;
	}

	public void setMetadataJSON(String metadataJSON) {
		this.metadataJSON = metadataJSON;
	}

	public void setMetadataJSON(
		UnsafeSupplier<String, Exception> metadataJSONUnsafeSupplier) {

		try {
			metadataJSON = metadataJSONUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String metadataJSON;

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setResource(
		UnsafeSupplier<String, Exception> resourceUnsafeSupplier) {

		try {
			resource = resourceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String resource;

	@Override
	public OAuthClientPRLocalMetadata clone()
		throws CloneNotSupportedException {

		return (OAuthClientPRLocalMetadata)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OAuthClientPRLocalMetadata)) {
			return false;
		}

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			(OAuthClientPRLocalMetadata)object;

		return Objects.equals(
			toString(), oAuthClientPRLocalMetadata.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return OAuthClientPRLocalMetadataSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:541866906