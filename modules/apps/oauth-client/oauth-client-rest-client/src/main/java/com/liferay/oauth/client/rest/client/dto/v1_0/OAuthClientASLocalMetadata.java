/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.client.dto.v1_0;

import com.liferay.oauth.client.rest.client.function.UnsafeSupplier;
import com.liferay.oauth.client.rest.client.serdes.v1_0.OAuthClientASLocalMetadataSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Manuele Castro
 * @generated
 */
@Generated("")
public class OAuthClientASLocalMetadata implements Cloneable, Serializable {

	public static OAuthClientASLocalMetadata toDTO(String json) {
		return OAuthClientASLocalMetadataSerDes.toDTO(json);
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

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public void setIssuer(
		UnsafeSupplier<String, Exception> issuerUnsafeSupplier) {

		try {
			issuer = issuerUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String issuer;

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

	public String getOAuthASLocalWellKnownURI() {
		return oAuthASLocalWellKnownURI;
	}

	public void setOAuthASLocalWellKnownURI(String oAuthASLocalWellKnownURI) {
		this.oAuthASLocalWellKnownURI = oAuthASLocalWellKnownURI;
	}

	public void setOAuthASLocalWellKnownURI(
		UnsafeSupplier<String, Exception>
			oAuthASLocalWellKnownURIUnsafeSupplier) {

		try {
			oAuthASLocalWellKnownURI =
				oAuthASLocalWellKnownURIUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String oAuthASLocalWellKnownURI;

	public String getOAuthASMetadataJSON() {
		return oAuthASMetadataJSON;
	}

	public void setOAuthASMetadataJSON(String oAuthASMetadataJSON) {
		this.oAuthASMetadataJSON = oAuthASMetadataJSON;
	}

	public void setOAuthASMetadataJSON(
		UnsafeSupplier<String, Exception> oAuthASMetadataJSONUnsafeSupplier) {

		try {
			oAuthASMetadataJSON = oAuthASMetadataJSONUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String oAuthASMetadataJSON;

	public com.liferay.oauth.client.rest.client.permission.Permission[]
		getPermissions() {

		return permissions;
	}

	public void setPermissions(
		com.liferay.oauth.client.rest.client.permission.Permission[]
			permissions) {

		this.permissions = permissions;
	}

	public void setPermissions(
		UnsafeSupplier
			<com.liferay.oauth.client.rest.client.permission.Permission[],
			 Exception> permissionsUnsafeSupplier) {

		try {
			permissions = permissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.oauth.client.rest.client.permission.Permission[]
		permissions;

	@Override
	public OAuthClientASLocalMetadata clone()
		throws CloneNotSupportedException {

		return (OAuthClientASLocalMetadata)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OAuthClientASLocalMetadata)) {
			return false;
		}

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			(OAuthClientASLocalMetadata)object;

		return Objects.equals(
			toString(), oAuthClientASLocalMetadata.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return OAuthClientASLocalMetadataSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1234310487