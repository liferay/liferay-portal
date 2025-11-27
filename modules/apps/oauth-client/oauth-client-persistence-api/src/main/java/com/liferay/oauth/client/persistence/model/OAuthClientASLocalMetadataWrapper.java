/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link OAuthClientASLocalMetadata}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientASLocalMetadata
 * @generated
 */
public class OAuthClientASLocalMetadataWrapper
	extends BaseModelWrapper<OAuthClientASLocalMetadata>
	implements ModelWrapper<OAuthClientASLocalMetadata>,
			   OAuthClientASLocalMetadata {

	public OAuthClientASLocalMetadataWrapper(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		super(oAuthClientASLocalMetadata);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put(
			"oAuthClientASLocalMetadataId", getOAuthClientASLocalMetadataId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("allowedGrantTypes", getAllowedGrantTypes());
		attributes.put("allowedScopes", getAllowedScopes());
		attributes.put("issuer", getIssuer());
		attributes.put("localWellKnownEnabled", isLocalWellKnownEnabled());
		attributes.put("localWellKnownURIOAS", getLocalWellKnownURIOAS());
		attributes.put("localWellKnownURIOIC", getLocalWellKnownURIOIC());
		attributes.put("metadataJSONAS", getMetadataJSONAS());
		attributes.put("metadataJSONOIC", getMetadataJSONOIC());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long oAuthClientASLocalMetadataId = (Long)attributes.get(
			"oAuthClientASLocalMetadataId");

		if (oAuthClientASLocalMetadataId != null) {
			setOAuthClientASLocalMetadataId(oAuthClientASLocalMetadataId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String allowedGrantTypes = (String)attributes.get("allowedGrantTypes");

		if (allowedGrantTypes != null) {
			setAllowedGrantTypes(allowedGrantTypes);
		}

		String allowedScopes = (String)attributes.get("allowedScopes");

		if (allowedScopes != null) {
			setAllowedScopes(allowedScopes);
		}

		String issuer = (String)attributes.get("issuer");

		if (issuer != null) {
			setIssuer(issuer);
		}

		Boolean localWellKnownEnabled = (Boolean)attributes.get(
			"localWellKnownEnabled");

		if (localWellKnownEnabled != null) {
			setLocalWellKnownEnabled(localWellKnownEnabled);
		}

		String localWellKnownURIOAS = (String)attributes.get(
			"localWellKnownURIOAS");

		if (localWellKnownURIOAS != null) {
			setLocalWellKnownURIOAS(localWellKnownURIOAS);
		}

		String localWellKnownURIOIC = (String)attributes.get(
			"localWellKnownURIOIC");

		if (localWellKnownURIOIC != null) {
			setLocalWellKnownURIOIC(localWellKnownURIOIC);
		}

		String metadataJSONAS = (String)attributes.get("metadataJSONAS");

		if (metadataJSONAS != null) {
			setMetadataJSONAS(metadataJSONAS);
		}

		String metadataJSONOIC = (String)attributes.get("metadataJSONOIC");

		if (metadataJSONOIC != null) {
			setMetadataJSONOIC(metadataJSONOIC);
		}
	}

	@Override
	public OAuthClientASLocalMetadata cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the allowed grant types of this o auth client as local metadata.
	 *
	 * @return the allowed grant types of this o auth client as local metadata
	 */
	@Override
	public String getAllowedGrantTypes() {
		return model.getAllowedGrantTypes();
	}

	/**
	 * Returns the allowed scopes of this o auth client as local metadata.
	 *
	 * @return the allowed scopes of this o auth client as local metadata
	 */
	@Override
	public String getAllowedScopes() {
		return model.getAllowedScopes();
	}

	/**
	 * Returns the company ID of this o auth client as local metadata.
	 *
	 * @return the company ID of this o auth client as local metadata
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this o auth client as local metadata.
	 *
	 * @return the create date of this o auth client as local metadata
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the issuer of this o auth client as local metadata.
	 *
	 * @return the issuer of this o auth client as local metadata
	 */
	@Override
	public String getIssuer() {
		return model.getIssuer();
	}

	/**
	 * Returns the local well known enabled of this o auth client as local metadata.
	 *
	 * @return the local well known enabled of this o auth client as local metadata
	 */
	@Override
	public boolean getLocalWellKnownEnabled() {
		return model.getLocalWellKnownEnabled();
	}

	/**
	 * Returns the local well known urioas of this o auth client as local metadata.
	 *
	 * @return the local well known urioas of this o auth client as local metadata
	 */
	@Override
	public String getLocalWellKnownURIOAS() {
		return model.getLocalWellKnownURIOAS();
	}

	/**
	 * Returns the local well known urioic of this o auth client as local metadata.
	 *
	 * @return the local well known urioic of this o auth client as local metadata
	 */
	@Override
	public String getLocalWellKnownURIOIC() {
		return model.getLocalWellKnownURIOIC();
	}

	/**
	 * Returns the metadata jsonas of this o auth client as local metadata.
	 *
	 * @return the metadata jsonas of this o auth client as local metadata
	 */
	@Override
	public String getMetadataJSONAS() {
		return model.getMetadataJSONAS();
	}

	/**
	 * Returns the metadata jsonoic of this o auth client as local metadata.
	 *
	 * @return the metadata jsonoic of this o auth client as local metadata
	 */
	@Override
	public String getMetadataJSONOIC() {
		return model.getMetadataJSONOIC();
	}

	/**
	 * Returns the modified date of this o auth client as local metadata.
	 *
	 * @return the modified date of this o auth client as local metadata
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this o auth client as local metadata.
	 *
	 * @return the mvcc version of this o auth client as local metadata
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the o auth client as local metadata ID of this o auth client as local metadata.
	 *
	 * @return the o auth client as local metadata ID of this o auth client as local metadata
	 */
	@Override
	public long getOAuthClientASLocalMetadataId() {
		return model.getOAuthClientASLocalMetadataId();
	}

	/**
	 * Returns the primary key of this o auth client as local metadata.
	 *
	 * @return the primary key of this o auth client as local metadata
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this o auth client as local metadata.
	 *
	 * @return the user ID of this o auth client as local metadata
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this o auth client as local metadata.
	 *
	 * @return the user name of this o auth client as local metadata
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this o auth client as local metadata.
	 *
	 * @return the user uuid of this o auth client as local metadata
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this o auth client as local metadata is local well known enabled.
	 *
	 * @return <code>true</code> if this o auth client as local metadata is local well known enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isLocalWellKnownEnabled() {
		return model.isLocalWellKnownEnabled();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the allowed grant types of this o auth client as local metadata.
	 *
	 * @param allowedGrantTypes the allowed grant types of this o auth client as local metadata
	 */
	@Override
	public void setAllowedGrantTypes(String allowedGrantTypes) {
		model.setAllowedGrantTypes(allowedGrantTypes);
	}

	/**
	 * Sets the allowed scopes of this o auth client as local metadata.
	 *
	 * @param allowedScopes the allowed scopes of this o auth client as local metadata
	 */
	@Override
	public void setAllowedScopes(String allowedScopes) {
		model.setAllowedScopes(allowedScopes);
	}

	/**
	 * Sets the company ID of this o auth client as local metadata.
	 *
	 * @param companyId the company ID of this o auth client as local metadata
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this o auth client as local metadata.
	 *
	 * @param createDate the create date of this o auth client as local metadata
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the issuer of this o auth client as local metadata.
	 *
	 * @param issuer the issuer of this o auth client as local metadata
	 */
	@Override
	public void setIssuer(String issuer) {
		model.setIssuer(issuer);
	}

	/**
	 * Sets whether this o auth client as local metadata is local well known enabled.
	 *
	 * @param localWellKnownEnabled the local well known enabled of this o auth client as local metadata
	 */
	@Override
	public void setLocalWellKnownEnabled(boolean localWellKnownEnabled) {
		model.setLocalWellKnownEnabled(localWellKnownEnabled);
	}

	/**
	 * Sets the local well known urioas of this o auth client as local metadata.
	 *
	 * @param localWellKnownURIOAS the local well known urioas of this o auth client as local metadata
	 */
	@Override
	public void setLocalWellKnownURIOAS(String localWellKnownURIOAS) {
		model.setLocalWellKnownURIOAS(localWellKnownURIOAS);
	}

	/**
	 * Sets the local well known urioic of this o auth client as local metadata.
	 *
	 * @param localWellKnownURIOIC the local well known urioic of this o auth client as local metadata
	 */
	@Override
	public void setLocalWellKnownURIOIC(String localWellKnownURIOIC) {
		model.setLocalWellKnownURIOIC(localWellKnownURIOIC);
	}

	/**
	 * Sets the metadata jsonas of this o auth client as local metadata.
	 *
	 * @param metadataJSONAS the metadata jsonas of this o auth client as local metadata
	 */
	@Override
	public void setMetadataJSONAS(String metadataJSONAS) {
		model.setMetadataJSONAS(metadataJSONAS);
	}

	/**
	 * Sets the metadata jsonoic of this o auth client as local metadata.
	 *
	 * @param metadataJSONOIC the metadata jsonoic of this o auth client as local metadata
	 */
	@Override
	public void setMetadataJSONOIC(String metadataJSONOIC) {
		model.setMetadataJSONOIC(metadataJSONOIC);
	}

	/**
	 * Sets the modified date of this o auth client as local metadata.
	 *
	 * @param modifiedDate the modified date of this o auth client as local metadata
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this o auth client as local metadata.
	 *
	 * @param mvccVersion the mvcc version of this o auth client as local metadata
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the o auth client as local metadata ID of this o auth client as local metadata.
	 *
	 * @param oAuthClientASLocalMetadataId the o auth client as local metadata ID of this o auth client as local metadata
	 */
	@Override
	public void setOAuthClientASLocalMetadataId(
		long oAuthClientASLocalMetadataId) {

		model.setOAuthClientASLocalMetadataId(oAuthClientASLocalMetadataId);
	}

	/**
	 * Sets the primary key of this o auth client as local metadata.
	 *
	 * @param primaryKey the primary key of this o auth client as local metadata
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this o auth client as local metadata.
	 *
	 * @param userId the user ID of this o auth client as local metadata
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this o auth client as local metadata.
	 *
	 * @param userName the user name of this o auth client as local metadata
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this o auth client as local metadata.
	 *
	 * @param userUuid the user uuid of this o auth client as local metadata
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected OAuthClientASLocalMetadataWrapper wrap(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		return new OAuthClientASLocalMetadataWrapper(
			oAuthClientASLocalMetadata);
	}

}