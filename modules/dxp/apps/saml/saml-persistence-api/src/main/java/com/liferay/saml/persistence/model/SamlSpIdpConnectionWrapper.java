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
 * This class is a wrapper for {@link SamlSpIdpConnection}.
 * </p>
 *
 * @author Mika Koivisto
 * @see SamlSpIdpConnection
 * @generated
 */
public class SamlSpIdpConnectionWrapper
	extends BaseModelWrapper<SamlSpIdpConnection>
	implements ModelWrapper<SamlSpIdpConnection>, SamlSpIdpConnection {

	public SamlSpIdpConnectionWrapper(SamlSpIdpConnection samlSpIdpConnection) {
		super(samlSpIdpConnection);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("samlSpIdpConnectionId", getSamlSpIdpConnectionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("samlIdpEntityId", getSamlIdpEntityId());
		attributes.put(
			"assertionSignatureRequired", isAssertionSignatureRequired());
		attributes.put("clockSkew", getClockSkew());
		attributes.put("enabled", isEnabled());
		attributes.put("forceAuthn", isForceAuthn());
		attributes.put("ldapImportEnabled", isLdapImportEnabled());
		attributes.put("metadataUpdatedDate", getMetadataUpdatedDate());
		attributes.put("metadataUrl", getMetadataUrl());
		attributes.put("metadataXml", getMetadataXml());
		attributes.put("name", getName());
		attributes.put("nameIdFormat", getNameIdFormat());
		attributes.put("signAuthnRequest", isSignAuthnRequest());
		attributes.put(
			"unknownUsersAreStrangers", isUnknownUsersAreStrangers());
		attributes.put("userAttributeMappings", getUserAttributeMappings());
		attributes.put(
			"userIdentifierExpression", getUserIdentifierExpression());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long samlSpIdpConnectionId = (Long)attributes.get(
			"samlSpIdpConnectionId");

		if (samlSpIdpConnectionId != null) {
			setSamlSpIdpConnectionId(samlSpIdpConnectionId);
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

		String samlIdpEntityId = (String)attributes.get("samlIdpEntityId");

		if (samlIdpEntityId != null) {
			setSamlIdpEntityId(samlIdpEntityId);
		}

		Boolean assertionSignatureRequired = (Boolean)attributes.get(
			"assertionSignatureRequired");

		if (assertionSignatureRequired != null) {
			setAssertionSignatureRequired(assertionSignatureRequired);
		}

		Long clockSkew = (Long)attributes.get("clockSkew");

		if (clockSkew != null) {
			setClockSkew(clockSkew);
		}

		Boolean enabled = (Boolean)attributes.get("enabled");

		if (enabled != null) {
			setEnabled(enabled);
		}

		Boolean forceAuthn = (Boolean)attributes.get("forceAuthn");

		if (forceAuthn != null) {
			setForceAuthn(forceAuthn);
		}

		Boolean ldapImportEnabled = (Boolean)attributes.get(
			"ldapImportEnabled");

		if (ldapImportEnabled != null) {
			setLdapImportEnabled(ldapImportEnabled);
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

		String nameIdFormat = (String)attributes.get("nameIdFormat");

		if (nameIdFormat != null) {
			setNameIdFormat(nameIdFormat);
		}

		Boolean signAuthnRequest = (Boolean)attributes.get("signAuthnRequest");

		if (signAuthnRequest != null) {
			setSignAuthnRequest(signAuthnRequest);
		}

		Boolean unknownUsersAreStrangers = (Boolean)attributes.get(
			"unknownUsersAreStrangers");

		if (unknownUsersAreStrangers != null) {
			setUnknownUsersAreStrangers(unknownUsersAreStrangers);
		}

		String userAttributeMappings = (String)attributes.get(
			"userAttributeMappings");

		if (userAttributeMappings != null) {
			setUserAttributeMappings(userAttributeMappings);
		}

		String userIdentifierExpression = (String)attributes.get(
			"userIdentifierExpression");

		if (userIdentifierExpression != null) {
			setUserIdentifierExpression(userIdentifierExpression);
		}
	}

	@Override
	public SamlSpIdpConnection cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the assertion signature required of this saml sp idp connection.
	 *
	 * @return the assertion signature required of this saml sp idp connection
	 */
	@Override
	public boolean getAssertionSignatureRequired() {
		return model.getAssertionSignatureRequired();
	}

	/**
	 * Returns the clock skew of this saml sp idp connection.
	 *
	 * @return the clock skew of this saml sp idp connection
	 */
	@Override
	public long getClockSkew() {
		return model.getClockSkew();
	}

	/**
	 * Returns the company ID of this saml sp idp connection.
	 *
	 * @return the company ID of this saml sp idp connection
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this saml sp idp connection.
	 *
	 * @return the create date of this saml sp idp connection
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the enabled of this saml sp idp connection.
	 *
	 * @return the enabled of this saml sp idp connection
	 */
	@Override
	public boolean getEnabled() {
		return model.getEnabled();
	}

	/**
	 * Returns the force authn of this saml sp idp connection.
	 *
	 * @return the force authn of this saml sp idp connection
	 */
	@Override
	public boolean getForceAuthn() {
		return model.getForceAuthn();
	}

	/**
	 * Returns the ldap import enabled of this saml sp idp connection.
	 *
	 * @return the ldap import enabled of this saml sp idp connection
	 */
	@Override
	public boolean getLdapImportEnabled() {
		return model.getLdapImportEnabled();
	}

	/**
	 * Returns the metadata updated date of this saml sp idp connection.
	 *
	 * @return the metadata updated date of this saml sp idp connection
	 */
	@Override
	public Date getMetadataUpdatedDate() {
		return model.getMetadataUpdatedDate();
	}

	/**
	 * Returns the metadata url of this saml sp idp connection.
	 *
	 * @return the metadata url of this saml sp idp connection
	 */
	@Override
	public String getMetadataUrl() {
		return model.getMetadataUrl();
	}

	/**
	 * Returns the metadata xml of this saml sp idp connection.
	 *
	 * @return the metadata xml of this saml sp idp connection
	 */
	@Override
	public String getMetadataXml() {
		return model.getMetadataXml();
	}

	/**
	 * Returns the modified date of this saml sp idp connection.
	 *
	 * @return the modified date of this saml sp idp connection
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the name of this saml sp idp connection.
	 *
	 * @return the name of this saml sp idp connection
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the name ID format of this saml sp idp connection.
	 *
	 * @return the name ID format of this saml sp idp connection
	 */
	@Override
	public String getNameIdFormat() {
		return model.getNameIdFormat();
	}

	@Override
	public java.util.Properties getNormalizedUserAttributeMappings()
		throws java.io.IOException {

		return model.getNormalizedUserAttributeMappings();
	}

	/**
	 * Returns the primary key of this saml sp idp connection.
	 *
	 * @return the primary key of this saml sp idp connection
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the saml idp entity ID of this saml sp idp connection.
	 *
	 * @return the saml idp entity ID of this saml sp idp connection
	 */
	@Override
	public String getSamlIdpEntityId() {
		return model.getSamlIdpEntityId();
	}

	/**
	 * Returns the saml sp idp connection ID of this saml sp idp connection.
	 *
	 * @return the saml sp idp connection ID of this saml sp idp connection
	 */
	@Override
	public long getSamlSpIdpConnectionId() {
		return model.getSamlSpIdpConnectionId();
	}

	/**
	 * Returns the sign authn request of this saml sp idp connection.
	 *
	 * @return the sign authn request of this saml sp idp connection
	 */
	@Override
	public boolean getSignAuthnRequest() {
		return model.getSignAuthnRequest();
	}

	/**
	 * Returns the unknown users are strangers of this saml sp idp connection.
	 *
	 * @return the unknown users are strangers of this saml sp idp connection
	 */
	@Override
	public boolean getUnknownUsersAreStrangers() {
		return model.getUnknownUsersAreStrangers();
	}

	/**
	 * Returns the user attribute mappings of this saml sp idp connection.
	 *
	 * @return the user attribute mappings of this saml sp idp connection
	 */
	@Override
	public String getUserAttributeMappings() {
		return model.getUserAttributeMappings();
	}

	/**
	 * Returns the user ID of this saml sp idp connection.
	 *
	 * @return the user ID of this saml sp idp connection
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user identifier expression of this saml sp idp connection.
	 *
	 * @return the user identifier expression of this saml sp idp connection
	 */
	@Override
	public String getUserIdentifierExpression() {
		return model.getUserIdentifierExpression();
	}

	/**
	 * Returns the user name of this saml sp idp connection.
	 *
	 * @return the user name of this saml sp idp connection
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this saml sp idp connection.
	 *
	 * @return the user uuid of this saml sp idp connection
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this saml sp idp connection is assertion signature required.
	 *
	 * @return <code>true</code> if this saml sp idp connection is assertion signature required; <code>false</code> otherwise
	 */
	@Override
	public boolean isAssertionSignatureRequired() {
		return model.isAssertionSignatureRequired();
	}

	/**
	 * Returns <code>true</code> if this saml sp idp connection is enabled.
	 *
	 * @return <code>true</code> if this saml sp idp connection is enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnabled() {
		return model.isEnabled();
	}

	/**
	 * Returns <code>true</code> if this saml sp idp connection is force authn.
	 *
	 * @return <code>true</code> if this saml sp idp connection is force authn; <code>false</code> otherwise
	 */
	@Override
	public boolean isForceAuthn() {
		return model.isForceAuthn();
	}

	/**
	 * Returns <code>true</code> if this saml sp idp connection is ldap import enabled.
	 *
	 * @return <code>true</code> if this saml sp idp connection is ldap import enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isLdapImportEnabled() {
		return model.isLdapImportEnabled();
	}

	/**
	 * Returns <code>true</code> if this saml sp idp connection is sign authn request.
	 *
	 * @return <code>true</code> if this saml sp idp connection is sign authn request; <code>false</code> otherwise
	 */
	@Override
	public boolean isSignAuthnRequest() {
		return model.isSignAuthnRequest();
	}

	/**
	 * Returns <code>true</code> if this saml sp idp connection is unknown users are strangers.
	 *
	 * @return <code>true</code> if this saml sp idp connection is unknown users are strangers; <code>false</code> otherwise
	 */
	@Override
	public boolean isUnknownUsersAreStrangers() {
		return model.isUnknownUsersAreStrangers();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets whether this saml sp idp connection is assertion signature required.
	 *
	 * @param assertionSignatureRequired the assertion signature required of this saml sp idp connection
	 */
	@Override
	public void setAssertionSignatureRequired(
		boolean assertionSignatureRequired) {

		model.setAssertionSignatureRequired(assertionSignatureRequired);
	}

	/**
	 * Sets the clock skew of this saml sp idp connection.
	 *
	 * @param clockSkew the clock skew of this saml sp idp connection
	 */
	@Override
	public void setClockSkew(long clockSkew) {
		model.setClockSkew(clockSkew);
	}

	/**
	 * Sets the company ID of this saml sp idp connection.
	 *
	 * @param companyId the company ID of this saml sp idp connection
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this saml sp idp connection.
	 *
	 * @param createDate the create date of this saml sp idp connection
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets whether this saml sp idp connection is enabled.
	 *
	 * @param enabled the enabled of this saml sp idp connection
	 */
	@Override
	public void setEnabled(boolean enabled) {
		model.setEnabled(enabled);
	}

	/**
	 * Sets whether this saml sp idp connection is force authn.
	 *
	 * @param forceAuthn the force authn of this saml sp idp connection
	 */
	@Override
	public void setForceAuthn(boolean forceAuthn) {
		model.setForceAuthn(forceAuthn);
	}

	/**
	 * Sets whether this saml sp idp connection is ldap import enabled.
	 *
	 * @param ldapImportEnabled the ldap import enabled of this saml sp idp connection
	 */
	@Override
	public void setLdapImportEnabled(boolean ldapImportEnabled) {
		model.setLdapImportEnabled(ldapImportEnabled);
	}

	/**
	 * Sets the metadata updated date of this saml sp idp connection.
	 *
	 * @param metadataUpdatedDate the metadata updated date of this saml sp idp connection
	 */
	@Override
	public void setMetadataUpdatedDate(Date metadataUpdatedDate) {
		model.setMetadataUpdatedDate(metadataUpdatedDate);
	}

	/**
	 * Sets the metadata url of this saml sp idp connection.
	 *
	 * @param metadataUrl the metadata url of this saml sp idp connection
	 */
	@Override
	public void setMetadataUrl(String metadataUrl) {
		model.setMetadataUrl(metadataUrl);
	}

	/**
	 * Sets the metadata xml of this saml sp idp connection.
	 *
	 * @param metadataXml the metadata xml of this saml sp idp connection
	 */
	@Override
	public void setMetadataXml(String metadataXml) {
		model.setMetadataXml(metadataXml);
	}

	/**
	 * Sets the modified date of this saml sp idp connection.
	 *
	 * @param modifiedDate the modified date of this saml sp idp connection
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the name of this saml sp idp connection.
	 *
	 * @param name the name of this saml sp idp connection
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the name ID format of this saml sp idp connection.
	 *
	 * @param nameIdFormat the name ID format of this saml sp idp connection
	 */
	@Override
	public void setNameIdFormat(String nameIdFormat) {
		model.setNameIdFormat(nameIdFormat);
	}

	/**
	 * Sets the primary key of this saml sp idp connection.
	 *
	 * @param primaryKey the primary key of this saml sp idp connection
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the saml idp entity ID of this saml sp idp connection.
	 *
	 * @param samlIdpEntityId the saml idp entity ID of this saml sp idp connection
	 */
	@Override
	public void setSamlIdpEntityId(String samlIdpEntityId) {
		model.setSamlIdpEntityId(samlIdpEntityId);
	}

	/**
	 * Sets the saml sp idp connection ID of this saml sp idp connection.
	 *
	 * @param samlSpIdpConnectionId the saml sp idp connection ID of this saml sp idp connection
	 */
	@Override
	public void setSamlSpIdpConnectionId(long samlSpIdpConnectionId) {
		model.setSamlSpIdpConnectionId(samlSpIdpConnectionId);
	}

	/**
	 * Sets whether this saml sp idp connection is sign authn request.
	 *
	 * @param signAuthnRequest the sign authn request of this saml sp idp connection
	 */
	@Override
	public void setSignAuthnRequest(boolean signAuthnRequest) {
		model.setSignAuthnRequest(signAuthnRequest);
	}

	/**
	 * Sets whether this saml sp idp connection is unknown users are strangers.
	 *
	 * @param unknownUsersAreStrangers the unknown users are strangers of this saml sp idp connection
	 */
	@Override
	public void setUnknownUsersAreStrangers(boolean unknownUsersAreStrangers) {
		model.setUnknownUsersAreStrangers(unknownUsersAreStrangers);
	}

	/**
	 * Sets the user attribute mappings of this saml sp idp connection.
	 *
	 * @param userAttributeMappings the user attribute mappings of this saml sp idp connection
	 */
	@Override
	public void setUserAttributeMappings(String userAttributeMappings) {
		model.setUserAttributeMappings(userAttributeMappings);
	}

	/**
	 * Sets the user ID of this saml sp idp connection.
	 *
	 * @param userId the user ID of this saml sp idp connection
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user identifier expression of this saml sp idp connection.
	 *
	 * @param userIdentifierExpression the user identifier expression of this saml sp idp connection
	 */
	@Override
	public void setUserIdentifierExpression(String userIdentifierExpression) {
		model.setUserIdentifierExpression(userIdentifierExpression);
	}

	/**
	 * Sets the user name of this saml sp idp connection.
	 *
	 * @param userName the user name of this saml sp idp connection
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this saml sp idp connection.
	 *
	 * @param userUuid the user uuid of this saml sp idp connection
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
	protected SamlSpIdpConnectionWrapper wrap(
		SamlSpIdpConnection samlSpIdpConnection) {

		return new SamlSpIdpConnectionWrapper(samlSpIdpConnection);
	}

}