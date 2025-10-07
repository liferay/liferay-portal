/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link SamlIdpSpConnection}.
 * </p>
 *
 * @author Mika Koivisto
 * @see SamlIdpSpConnection
 * @generated
 */
public class SamlIdpSpConnectionWrapper
	extends BaseModelWrapper<SamlIdpSpConnection>
	implements ModelWrapper<SamlIdpSpConnection>, SamlIdpSpConnection {

	public SamlIdpSpConnectionWrapper(SamlIdpSpConnection samlIdpSpConnection) {
		super(samlIdpSpConnection);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("samlIdpSpConnectionId", getSamlIdpSpConnectionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("samlSpEntityId", getSamlSpEntityId());
		attributes.put("assertionLifetime", getAssertionLifetime());
		attributes.put("attributeNames", getAttributeNames());
		attributes.put("attributesEnabled", isAttributesEnabled());
		attributes.put(
			"attributesNamespaceEnabled", isAttributesNamespaceEnabled());
		attributes.put("enabled", isEnabled());
		attributes.put("encryptionForced", isEncryptionForced());
		attributes.put("metadataUpdatedDate", getMetadataUpdatedDate());
		attributes.put("metadataUrl", getMetadataUrl());
		attributes.put("metadataXml", getMetadataXml());
		attributes.put("name", getName());
		attributes.put("nameIdAttribute", getNameIdAttribute());
		attributes.put("nameIdFormat", getNameIdFormat());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long samlIdpSpConnectionId = (Long)attributes.get(
			"samlIdpSpConnectionId");

		if (samlIdpSpConnectionId != null) {
			setSamlIdpSpConnectionId(samlIdpSpConnectionId);
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

		String samlSpEntityId = (String)attributes.get("samlSpEntityId");

		if (samlSpEntityId != null) {
			setSamlSpEntityId(samlSpEntityId);
		}

		Integer assertionLifetime = (Integer)attributes.get(
			"assertionLifetime");

		if (assertionLifetime != null) {
			setAssertionLifetime(assertionLifetime);
		}

		String attributeNames = (String)attributes.get("attributeNames");

		if (attributeNames != null) {
			setAttributeNames(attributeNames);
		}

		Boolean attributesEnabled = (Boolean)attributes.get(
			"attributesEnabled");

		if (attributesEnabled != null) {
			setAttributesEnabled(attributesEnabled);
		}

		Boolean attributesNamespaceEnabled = (Boolean)attributes.get(
			"attributesNamespaceEnabled");

		if (attributesNamespaceEnabled != null) {
			setAttributesNamespaceEnabled(attributesNamespaceEnabled);
		}

		Boolean enabled = (Boolean)attributes.get("enabled");

		if (enabled != null) {
			setEnabled(enabled);
		}

		Boolean encryptionForced = (Boolean)attributes.get("encryptionForced");

		if (encryptionForced != null) {
			setEncryptionForced(encryptionForced);
		}

		Date metadataUpdatedDate = (Date)attributes.get("metadataUpdatedDate");

		if (metadataUpdatedDate != null) {
			setMetadataUpdatedDate(metadataUpdatedDate);
		}

		String metadataUrl = (String)attributes.get("metadataUrl");

		if (metadataUrl != null) {
			setMetadataUrl(metadataUrl);
		}

		String metadataXml = (String)attributes.get("metadataXml");

		if (metadataXml != null) {
			setMetadataXml(metadataXml);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String nameIdAttribute = (String)attributes.get("nameIdAttribute");

		if (nameIdAttribute != null) {
			setNameIdAttribute(nameIdAttribute);
		}

		String nameIdFormat = (String)attributes.get("nameIdFormat");

		if (nameIdFormat != null) {
			setNameIdFormat(nameIdFormat);
		}
	}

	@Override
	public SamlIdpSpConnection cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the assertion lifetime of this saml idp sp connection.
	 *
	 * @return the assertion lifetime of this saml idp sp connection
	 */
	@Override
	public int getAssertionLifetime() {
		return model.getAssertionLifetime();
	}

	/**
	 * Returns the attribute names of this saml idp sp connection.
	 *
	 * @return the attribute names of this saml idp sp connection
	 */
	@Override
	public String getAttributeNames() {
		return model.getAttributeNames();
	}

	/**
	 * Returns the attributes enabled of this saml idp sp connection.
	 *
	 * @return the attributes enabled of this saml idp sp connection
	 */
	@Override
	public boolean getAttributesEnabled() {
		return model.getAttributesEnabled();
	}

	/**
	 * Returns the attributes namespace enabled of this saml idp sp connection.
	 *
	 * @return the attributes namespace enabled of this saml idp sp connection
	 */
	@Override
	public boolean getAttributesNamespaceEnabled() {
		return model.getAttributesNamespaceEnabled();
	}

	/**
	 * Returns the company ID of this saml idp sp connection.
	 *
	 * @return the company ID of this saml idp sp connection
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this saml idp sp connection.
	 *
	 * @return the create date of this saml idp sp connection
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the enabled of this saml idp sp connection.
	 *
	 * @return the enabled of this saml idp sp connection
	 */
	@Override
	public boolean getEnabled() {
		return model.getEnabled();
	}

	/**
	 * Returns the encryption forced of this saml idp sp connection.
	 *
	 * @return the encryption forced of this saml idp sp connection
	 */
	@Override
	public boolean getEncryptionForced() {
		return model.getEncryptionForced();
	}

	/**
	 * Returns the metadata updated date of this saml idp sp connection.
	 *
	 * @return the metadata updated date of this saml idp sp connection
	 */
	@Override
	public Date getMetadataUpdatedDate() {
		return model.getMetadataUpdatedDate();
	}

	/**
	 * Returns the metadata url of this saml idp sp connection.
	 *
	 * @return the metadata url of this saml idp sp connection
	 */
	@Override
	public String getMetadataUrl() {
		return model.getMetadataUrl();
	}

	/**
	 * Returns the metadata xml of this saml idp sp connection.
	 *
	 * @return the metadata xml of this saml idp sp connection
	 */
	@Override
	public String getMetadataXml() {
		return model.getMetadataXml();
	}

	/**
	 * Returns the modified date of this saml idp sp connection.
	 *
	 * @return the modified date of this saml idp sp connection
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the name of this saml idp sp connection.
	 *
	 * @return the name of this saml idp sp connection
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the name ID attribute of this saml idp sp connection.
	 *
	 * @return the name ID attribute of this saml idp sp connection
	 */
	@Override
	public String getNameIdAttribute() {
		return model.getNameIdAttribute();
	}

	/**
	 * Returns the name ID format of this saml idp sp connection.
	 *
	 * @return the name ID format of this saml idp sp connection
	 */
	@Override
	public String getNameIdFormat() {
		return model.getNameIdFormat();
	}

	/**
	 * Returns the primary key of this saml idp sp connection.
	 *
	 * @return the primary key of this saml idp sp connection
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the saml idp sp connection ID of this saml idp sp connection.
	 *
	 * @return the saml idp sp connection ID of this saml idp sp connection
	 */
	@Override
	public long getSamlIdpSpConnectionId() {
		return model.getSamlIdpSpConnectionId();
	}

	/**
	 * Returns the saml sp entity ID of this saml idp sp connection.
	 *
	 * @return the saml sp entity ID of this saml idp sp connection
	 */
	@Override
	public String getSamlSpEntityId() {
		return model.getSamlSpEntityId();
	}

	/**
	 * Returns the user ID of this saml idp sp connection.
	 *
	 * @return the user ID of this saml idp sp connection
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this saml idp sp connection.
	 *
	 * @return the user name of this saml idp sp connection
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this saml idp sp connection.
	 *
	 * @return the user uuid of this saml idp sp connection
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this saml idp sp connection is attributes enabled.
	 *
	 * @return <code>true</code> if this saml idp sp connection is attributes enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isAttributesEnabled() {
		return model.isAttributesEnabled();
	}

	/**
	 * Returns <code>true</code> if this saml idp sp connection is attributes namespace enabled.
	 *
	 * @return <code>true</code> if this saml idp sp connection is attributes namespace enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isAttributesNamespaceEnabled() {
		return model.isAttributesNamespaceEnabled();
	}

	/**
	 * Returns <code>true</code> if this saml idp sp connection is enabled.
	 *
	 * @return <code>true</code> if this saml idp sp connection is enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnabled() {
		return model.isEnabled();
	}

	/**
	 * Returns <code>true</code> if this saml idp sp connection is encryption forced.
	 *
	 * @return <code>true</code> if this saml idp sp connection is encryption forced; <code>false</code> otherwise
	 */
	@Override
	public boolean isEncryptionForced() {
		return model.isEncryptionForced();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the assertion lifetime of this saml idp sp connection.
	 *
	 * @param assertionLifetime the assertion lifetime of this saml idp sp connection
	 */
	@Override
	public void setAssertionLifetime(int assertionLifetime) {
		model.setAssertionLifetime(assertionLifetime);
	}

	/**
	 * Sets the attribute names of this saml idp sp connection.
	 *
	 * @param attributeNames the attribute names of this saml idp sp connection
	 */
	@Override
	public void setAttributeNames(String attributeNames) {
		model.setAttributeNames(attributeNames);
	}

	/**
	 * Sets whether this saml idp sp connection is attributes enabled.
	 *
	 * @param attributesEnabled the attributes enabled of this saml idp sp connection
	 */
	@Override
	public void setAttributesEnabled(boolean attributesEnabled) {
		model.setAttributesEnabled(attributesEnabled);
	}

	/**
	 * Sets whether this saml idp sp connection is attributes namespace enabled.
	 *
	 * @param attributesNamespaceEnabled the attributes namespace enabled of this saml idp sp connection
	 */
	@Override
	public void setAttributesNamespaceEnabled(
		boolean attributesNamespaceEnabled) {

		model.setAttributesNamespaceEnabled(attributesNamespaceEnabled);
	}

	/**
	 * Sets the company ID of this saml idp sp connection.
	 *
	 * @param companyId the company ID of this saml idp sp connection
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this saml idp sp connection.
	 *
	 * @param createDate the create date of this saml idp sp connection
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets whether this saml idp sp connection is enabled.
	 *
	 * @param enabled the enabled of this saml idp sp connection
	 */
	@Override
	public void setEnabled(boolean enabled) {
		model.setEnabled(enabled);
	}

	/**
	 * Sets whether this saml idp sp connection is encryption forced.
	 *
	 * @param encryptionForced the encryption forced of this saml idp sp connection
	 */
	@Override
	public void setEncryptionForced(boolean encryptionForced) {
		model.setEncryptionForced(encryptionForced);
	}

	/**
	 * Sets the metadata updated date of this saml idp sp connection.
	 *
	 * @param metadataUpdatedDate the metadata updated date of this saml idp sp connection
	 */
	@Override
	public void setMetadataUpdatedDate(Date metadataUpdatedDate) {
		model.setMetadataUpdatedDate(metadataUpdatedDate);
	}

	/**
	 * Sets the metadata url of this saml idp sp connection.
	 *
	 * @param metadataUrl the metadata url of this saml idp sp connection
	 */
	@Override
	public void setMetadataUrl(String metadataUrl) {
		model.setMetadataUrl(metadataUrl);
	}

	/**
	 * Sets the metadata xml of this saml idp sp connection.
	 *
	 * @param metadataXml the metadata xml of this saml idp sp connection
	 */
	@Override
	public void setMetadataXml(String metadataXml) {
		model.setMetadataXml(metadataXml);
	}

	/**
	 * Sets the modified date of this saml idp sp connection.
	 *
	 * @param modifiedDate the modified date of this saml idp sp connection
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the name of this saml idp sp connection.
	 *
	 * @param name the name of this saml idp sp connection
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the name ID attribute of this saml idp sp connection.
	 *
	 * @param nameIdAttribute the name ID attribute of this saml idp sp connection
	 */
	@Override
	public void setNameIdAttribute(String nameIdAttribute) {
		model.setNameIdAttribute(nameIdAttribute);
	}

	/**
	 * Sets the name ID format of this saml idp sp connection.
	 *
	 * @param nameIdFormat the name ID format of this saml idp sp connection
	 */
	@Override
	public void setNameIdFormat(String nameIdFormat) {
		model.setNameIdFormat(nameIdFormat);
	}

	/**
	 * Sets the primary key of this saml idp sp connection.
	 *
	 * @param primaryKey the primary key of this saml idp sp connection
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the saml idp sp connection ID of this saml idp sp connection.
	 *
	 * @param samlIdpSpConnectionId the saml idp sp connection ID of this saml idp sp connection
	 */
	@Override
	public void setSamlIdpSpConnectionId(long samlIdpSpConnectionId) {
		model.setSamlIdpSpConnectionId(samlIdpSpConnectionId);
	}

	/**
	 * Sets the saml sp entity ID of this saml idp sp connection.
	 *
	 * @param samlSpEntityId the saml sp entity ID of this saml idp sp connection
	 */
	@Override
	public void setSamlSpEntityId(String samlSpEntityId) {
		model.setSamlSpEntityId(samlSpEntityId);
	}

	/**
	 * Sets the user ID of this saml idp sp connection.
	 *
	 * @param userId the user ID of this saml idp sp connection
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this saml idp sp connection.
	 *
	 * @param userName the user name of this saml idp sp connection
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this saml idp sp connection.
	 *
	 * @param userUuid the user uuid of this saml idp sp connection
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
	protected SamlIdpSpConnectionWrapper wrap(
		SamlIdpSpConnection samlIdpSpConnection) {

		return new SamlIdpSpConnectionWrapper(samlIdpSpConnection);
	}

}