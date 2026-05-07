/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.client.dto.v1_0;

import com.liferay.oauth.client.rest.client.function.UnsafeSupplier;
import com.liferay.oauth.client.rest.client.serdes.v1_0.OAuthClientEntrySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Manuele Castro
 * @generated
 */
@Generated("")
public class OAuthClientEntry implements Cloneable, Serializable {

	public static OAuthClientEntry toDTO(String json) {
		return OAuthClientEntrySerDes.toDTO(json);
	}

	public String getAuthRequestParametersJSON() {
		return authRequestParametersJSON;
	}

	public void setAuthRequestParametersJSON(String authRequestParametersJSON) {
		this.authRequestParametersJSON = authRequestParametersJSON;
	}

	public void setAuthRequestParametersJSON(
		UnsafeSupplier<String, Exception>
			authRequestParametersJSONUnsafeSupplier) {

		try {
			authRequestParametersJSON =
				authRequestParametersJSONUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String authRequestParametersJSON;

	public String getAuthServerWellKnownURI() {
		return authServerWellKnownURI;
	}

	public void setAuthServerWellKnownURI(String authServerWellKnownURI) {
		this.authServerWellKnownURI = authServerWellKnownURI;
	}

	public void setAuthServerWellKnownURI(
		UnsafeSupplier<String, Exception>
			authServerWellKnownURIUnsafeSupplier) {

		try {
			authServerWellKnownURI = authServerWellKnownURIUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String authServerWellKnownURI;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientId(
		UnsafeSupplier<String, Exception> clientIdUnsafeSupplier) {

		try {
			clientId = clientIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String clientId;

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

	public String getCustomClaims() {
		return customClaims;
	}

	public void setCustomClaims(String customClaims) {
		this.customClaims = customClaims;
	}

	public void setCustomClaims(
		UnsafeSupplier<String, Exception> customClaimsUnsafeSupplier) {

		try {
			customClaims = customClaimsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String customClaims;

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

	public String getInfoJSON() {
		return infoJSON;
	}

	public void setInfoJSON(String infoJSON) {
		this.infoJSON = infoJSON;
	}

	public void setInfoJSON(
		UnsafeSupplier<String, Exception> infoJSONUnsafeSupplier) {

		try {
			infoJSON = infoJSONUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String infoJSON;

	public String getMatcherField() {
		return matcherField;
	}

	public void setMatcherField(String matcherField) {
		this.matcherField = matcherField;
	}

	public void setMatcherField(
		UnsafeSupplier<String, Exception> matcherFieldUnsafeSupplier) {

		try {
			matcherField = matcherFieldUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String matcherField;

	public Long getMetadataCacheTime() {
		return metadataCacheTime;
	}

	public void setMetadataCacheTime(Long metadataCacheTime) {
		this.metadataCacheTime = metadataCacheTime;
	}

	public void setMetadataCacheTime(
		UnsafeSupplier<Long, Exception> metadataCacheTimeUnsafeSupplier) {

		try {
			metadataCacheTime = metadataCacheTimeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long metadataCacheTime;

	public OAuthClientASLocalMetadata getOAuthClientASLocalMetadata() {
		return oAuthClientASLocalMetadata;
	}

	public void setOAuthClientASLocalMetadata(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		this.oAuthClientASLocalMetadata = oAuthClientASLocalMetadata;
	}

	public void setOAuthClientASLocalMetadata(
		UnsafeSupplier<OAuthClientASLocalMetadata, Exception>
			oAuthClientASLocalMetadataUnsafeSupplier) {

		try {
			oAuthClientASLocalMetadata =
				oAuthClientASLocalMetadataUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected OAuthClientASLocalMetadata oAuthClientASLocalMetadata;

	public String getOidcUserInfoMapperJSON() {
		return oidcUserInfoMapperJSON;
	}

	public void setOidcUserInfoMapperJSON(String oidcUserInfoMapperJSON) {
		this.oidcUserInfoMapperJSON = oidcUserInfoMapperJSON;
	}

	public void setOidcUserInfoMapperJSON(
		UnsafeSupplier<String, Exception>
			oidcUserInfoMapperJSONUnsafeSupplier) {

		try {
			oidcUserInfoMapperJSON = oidcUserInfoMapperJSONUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String oidcUserInfoMapperJSON;

	public String getTokenRequestParametersJSON() {
		return tokenRequestParametersJSON;
	}

	public void setTokenRequestParametersJSON(
		String tokenRequestParametersJSON) {

		this.tokenRequestParametersJSON = tokenRequestParametersJSON;
	}

	public void setTokenRequestParametersJSON(
		UnsafeSupplier<String, Exception>
			tokenRequestParametersJSONUnsafeSupplier) {

		try {
			tokenRequestParametersJSON =
				tokenRequestParametersJSONUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String tokenRequestParametersJSON;

	@Override
	public OAuthClientEntry clone() throws CloneNotSupportedException {
		return (OAuthClientEntry)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OAuthClientEntry)) {
			return false;
		}

		OAuthClientEntry oAuthClientEntry = (OAuthClientEntry)object;

		return Objects.equals(toString(), oAuthClientEntry.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return OAuthClientEntrySerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1378405626