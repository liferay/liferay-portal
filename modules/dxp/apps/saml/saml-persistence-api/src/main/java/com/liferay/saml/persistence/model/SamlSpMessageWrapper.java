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
 * This class is a wrapper for {@link SamlSpMessage}.
 * </p>
 *
 * @author Mika Koivisto
 * @see SamlSpMessage
 * @generated
 */
public class SamlSpMessageWrapper
	extends BaseModelWrapper<SamlSpMessage>
	implements ModelWrapper<SamlSpMessage>, SamlSpMessage {

	public SamlSpMessageWrapper(SamlSpMessage samlSpMessage) {
		super(samlSpMessage);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("samlSpMessageId", getSamlSpMessageId());
		attributes.put("companyId", getCompanyId());
		attributes.put("createDate", getCreateDate());
		attributes.put("samlIdpEntityId", getSamlIdpEntityId());
		attributes.put("expirationDate", getExpirationDate());
		attributes.put("samlIdpResponseKey", getSamlIdpResponseKey());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long samlSpMessageId = (Long)attributes.get("samlSpMessageId");

		if (samlSpMessageId != null) {
			setSamlSpMessageId(samlSpMessageId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		String samlIdpEntityId = (String)attributes.get("samlIdpEntityId");

		if (samlIdpEntityId != null) {
			setSamlIdpEntityId(samlIdpEntityId);
		}

		Date expirationDate = (Date)attributes.get("expirationDate");

		if (expirationDate != null) {
			setExpirationDate(expirationDate);
		}

		String samlIdpResponseKey = (String)attributes.get(
			"samlIdpResponseKey");

		if (samlIdpResponseKey != null) {
			setSamlIdpResponseKey(samlIdpResponseKey);
		}
	}

	@Override
	public SamlSpMessage cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this saml sp message.
	 *
	 * @return the company ID of this saml sp message
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this saml sp message.
	 *
	 * @return the create date of this saml sp message
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the expiration date of this saml sp message.
	 *
	 * @return the expiration date of this saml sp message
	 */
	@Override
	public Date getExpirationDate() {
		return model.getExpirationDate();
	}

	/**
	 * Returns the primary key of this saml sp message.
	 *
	 * @return the primary key of this saml sp message
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the saml idp entity ID of this saml sp message.
	 *
	 * @return the saml idp entity ID of this saml sp message
	 */
	@Override
	public String getSamlIdpEntityId() {
		return model.getSamlIdpEntityId();
	}

	/**
	 * Returns the saml idp response key of this saml sp message.
	 *
	 * @return the saml idp response key of this saml sp message
	 */
	@Override
	public String getSamlIdpResponseKey() {
		return model.getSamlIdpResponseKey();
	}

	/**
	 * Returns the saml sp message ID of this saml sp message.
	 *
	 * @return the saml sp message ID of this saml sp message
	 */
	@Override
	public long getSamlSpMessageId() {
		return model.getSamlSpMessageId();
	}

	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this saml sp message.
	 *
	 * @param companyId the company ID of this saml sp message
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this saml sp message.
	 *
	 * @param createDate the create date of this saml sp message
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the expiration date of this saml sp message.
	 *
	 * @param expirationDate the expiration date of this saml sp message
	 */
	@Override
	public void setExpirationDate(Date expirationDate) {
		model.setExpirationDate(expirationDate);
	}

	/**
	 * Sets the primary key of this saml sp message.
	 *
	 * @param primaryKey the primary key of this saml sp message
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the saml idp entity ID of this saml sp message.
	 *
	 * @param samlIdpEntityId the saml idp entity ID of this saml sp message
	 */
	@Override
	public void setSamlIdpEntityId(String samlIdpEntityId) {
		model.setSamlIdpEntityId(samlIdpEntityId);
	}

	/**
	 * Sets the saml idp response key of this saml sp message.
	 *
	 * @param samlIdpResponseKey the saml idp response key of this saml sp message
	 */
	@Override
	public void setSamlIdpResponseKey(String samlIdpResponseKey) {
		model.setSamlIdpResponseKey(samlIdpResponseKey);
	}

	/**
	 * Sets the saml sp message ID of this saml sp message.
	 *
	 * @param samlSpMessageId the saml sp message ID of this saml sp message
	 */
	@Override
	public void setSamlSpMessageId(long samlSpMessageId) {
		model.setSamlSpMessageId(samlSpMessageId);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected SamlSpMessageWrapper wrap(SamlSpMessage samlSpMessage) {
		return new SamlSpMessageWrapper(samlSpMessage);
	}

}