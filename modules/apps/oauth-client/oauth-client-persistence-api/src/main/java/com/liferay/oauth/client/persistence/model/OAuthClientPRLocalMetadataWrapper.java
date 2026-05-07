/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link OAuthClientPRLocalMetadata}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadata
 * @generated
 */
public class OAuthClientPRLocalMetadataWrapper
	extends BaseModelWrapper<OAuthClientPRLocalMetadata>
	implements ModelWrapper<OAuthClientPRLocalMetadata>,
			   OAuthClientPRLocalMetadata {

	public OAuthClientPRLocalMetadataWrapper(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		super(oAuthClientPRLocalMetadata);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put(
			"oAuthClientPRLocalMetadataId", getOAuthClientPRLocalMetadataId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("localWellKnownEnabled", isLocalWellKnownEnabled());
		attributes.put("localWellKnownURI", getLocalWellKnownURI());
		attributes.put("metadataJSON", getMetadataJSON());
		attributes.put("resource", getResource());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long oAuthClientPRLocalMetadataId = (Long)attributes.get(
			"oAuthClientPRLocalMetadataId");

		if (oAuthClientPRLocalMetadataId != null) {
			setOAuthClientPRLocalMetadataId(oAuthClientPRLocalMetadataId);
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

		Boolean localWellKnownEnabled = (Boolean)attributes.get(
			"localWellKnownEnabled");

		if (localWellKnownEnabled != null) {
			setLocalWellKnownEnabled(localWellKnownEnabled);
		}

		String localWellKnownURI = (String)attributes.get("localWellKnownURI");

		if (localWellKnownURI != null) {
			setLocalWellKnownURI(localWellKnownURI);
		}

		String metadataJSON = (String)attributes.get("metadataJSON");

		if (metadataJSON != null) {
			setMetadataJSON(metadataJSON);
		}

		String resource = (String)attributes.get("resource");

		if (resource != null) {
			setResource(resource);
		}
	}

	@Override
	public OAuthClientPRLocalMetadata cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this o auth client pr local metadata.
	 *
	 * @return the company ID of this o auth client pr local metadata
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this o auth client pr local metadata.
	 *
	 * @return the create date of this o auth client pr local metadata
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the external reference code of this o auth client pr local metadata.
	 *
	 * @return the external reference code of this o auth client pr local metadata
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the local well known enabled of this o auth client pr local metadata.
	 *
	 * @return the local well known enabled of this o auth client pr local metadata
	 */
	@Override
	public boolean getLocalWellKnownEnabled() {
		return model.getLocalWellKnownEnabled();
	}

	/**
	 * Returns the local well known uri of this o auth client pr local metadata.
	 *
	 * @return the local well known uri of this o auth client pr local metadata
	 */
	@Override
	public String getLocalWellKnownURI() {
		return model.getLocalWellKnownURI();
	}

	/**
	 * Returns the metadata json of this o auth client pr local metadata.
	 *
	 * @return the metadata json of this o auth client pr local metadata
	 */
	@Override
	public String getMetadataJSON() {
		return model.getMetadataJSON();
	}

	/**
	 * Returns the modified date of this o auth client pr local metadata.
	 *
	 * @return the modified date of this o auth client pr local metadata
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this o auth client pr local metadata.
	 *
	 * @return the mvcc version of this o auth client pr local metadata
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the o auth client pr local metadata ID of this o auth client pr local metadata.
	 *
	 * @return the o auth client pr local metadata ID of this o auth client pr local metadata
	 */
	@Override
	public long getOAuthClientPRLocalMetadataId() {
		return model.getOAuthClientPRLocalMetadataId();
	}

	/**
	 * Returns the primary key of this o auth client pr local metadata.
	 *
	 * @return the primary key of this o auth client pr local metadata
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the resource of this o auth client pr local metadata.
	 *
	 * @return the resource of this o auth client pr local metadata
	 */
	@Override
	public String getResource() {
		return model.getResource();
	}

	/**
	 * Returns the user ID of this o auth client pr local metadata.
	 *
	 * @return the user ID of this o auth client pr local metadata
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this o auth client pr local metadata.
	 *
	 * @return the user name of this o auth client pr local metadata
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this o auth client pr local metadata.
	 *
	 * @return the user uuid of this o auth client pr local metadata
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this o auth client pr local metadata.
	 *
	 * @return the uuid of this o auth client pr local metadata
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this o auth client pr local metadata is local well known enabled.
	 *
	 * @return <code>true</code> if this o auth client pr local metadata is local well known enabled; <code>false</code> otherwise
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
	 * Sets the company ID of this o auth client pr local metadata.
	 *
	 * @param companyId the company ID of this o auth client pr local metadata
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this o auth client pr local metadata.
	 *
	 * @param createDate the create date of this o auth client pr local metadata
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the external reference code of this o auth client pr local metadata.
	 *
	 * @param externalReferenceCode the external reference code of this o auth client pr local metadata
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets whether this o auth client pr local metadata is local well known enabled.
	 *
	 * @param localWellKnownEnabled the local well known enabled of this o auth client pr local metadata
	 */
	@Override
	public void setLocalWellKnownEnabled(boolean localWellKnownEnabled) {
		model.setLocalWellKnownEnabled(localWellKnownEnabled);
	}

	/**
	 * Sets the local well known uri of this o auth client pr local metadata.
	 *
	 * @param localWellKnownURI the local well known uri of this o auth client pr local metadata
	 */
	@Override
	public void setLocalWellKnownURI(String localWellKnownURI) {
		model.setLocalWellKnownURI(localWellKnownURI);
	}

	/**
	 * Sets the metadata json of this o auth client pr local metadata.
	 *
	 * @param metadataJSON the metadata json of this o auth client pr local metadata
	 */
	@Override
	public void setMetadataJSON(String metadataJSON) {
		model.setMetadataJSON(metadataJSON);
	}

	/**
	 * Sets the modified date of this o auth client pr local metadata.
	 *
	 * @param modifiedDate the modified date of this o auth client pr local metadata
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this o auth client pr local metadata.
	 *
	 * @param mvccVersion the mvcc version of this o auth client pr local metadata
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the o auth client pr local metadata ID of this o auth client pr local metadata.
	 *
	 * @param oAuthClientPRLocalMetadataId the o auth client pr local metadata ID of this o auth client pr local metadata
	 */
	@Override
	public void setOAuthClientPRLocalMetadataId(
		long oAuthClientPRLocalMetadataId) {

		model.setOAuthClientPRLocalMetadataId(oAuthClientPRLocalMetadataId);
	}

	/**
	 * Sets the primary key of this o auth client pr local metadata.
	 *
	 * @param primaryKey the primary key of this o auth client pr local metadata
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the resource of this o auth client pr local metadata.
	 *
	 * @param resource the resource of this o auth client pr local metadata
	 */
	@Override
	public void setResource(String resource) {
		model.setResource(resource);
	}

	/**
	 * Sets the user ID of this o auth client pr local metadata.
	 *
	 * @param userId the user ID of this o auth client pr local metadata
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this o auth client pr local metadata.
	 *
	 * @param userName the user name of this o auth client pr local metadata
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this o auth client pr local metadata.
	 *
	 * @param userUuid the user uuid of this o auth client pr local metadata
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this o auth client pr local metadata.
	 *
	 * @param uuid the uuid of this o auth client pr local metadata
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected OAuthClientPRLocalMetadataWrapper wrap(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		return new OAuthClientPRLocalMetadataWrapper(
			oAuthClientPRLocalMetadata);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1722097359