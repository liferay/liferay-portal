/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
 * This class is a wrapper for {@link SamlIbSloMessage}.
 * </p>
 *
 * @author Mika Koivisto
 * @see SamlIbSloMessage
 * @generated
 */
public class SamlIbSloMessageWrapper
	extends BaseModelWrapper<SamlIbSloMessage>
	implements ModelWrapper<SamlIbSloMessage>, SamlIbSloMessage {

	public SamlIbSloMessageWrapper(SamlIbSloMessage samlIbSloMessage) {
		super(samlIbSloMessage);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("samlIbSloMessageId", getSamlIbSloMessageId());
		attributes.put("companyId", getCompanyId());
		attributes.put("createDate", getCreateDate());
		attributes.put("samlIdpEntityId", getSamlIdpEntityId());
		attributes.put("logoutRequestXml", getLogoutRequestXml());
		attributes.put("samlIdpSessionIndex", getSamlIdpSessionIndex());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long samlIbSloMessageId = (Long)attributes.get("samlIbSloMessageId");

		if (samlIbSloMessageId != null) {
			setSamlIbSloMessageId(samlIbSloMessageId);
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

		String logoutRequestXml = (String)attributes.get("logoutRequestXml");

		if (logoutRequestXml != null) {
			setLogoutRequestXml(logoutRequestXml);
		}

		String samlIdpSessionIndex = (String)attributes.get(
			"samlIdpSessionIndex");

		if (samlIdpSessionIndex != null) {
			setSamlIdpSessionIndex(samlIdpSessionIndex);
		}
	}

	@Override
	public SamlIbSloMessage cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this saml ib slo message.
	 *
	 * @return the company ID of this saml ib slo message
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this saml ib slo message.
	 *
	 * @return the create date of this saml ib slo message
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the logout request xml of this saml ib slo message.
	 *
	 * @return the logout request xml of this saml ib slo message
	 */
	@Override
	public String getLogoutRequestXml() {
		return model.getLogoutRequestXml();
	}

	/**
	 * Returns the primary key of this saml ib slo message.
	 *
	 * @return the primary key of this saml ib slo message
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the saml ib slo message ID of this saml ib slo message.
	 *
	 * @return the saml ib slo message ID of this saml ib slo message
	 */
	@Override
	public long getSamlIbSloMessageId() {
		return model.getSamlIbSloMessageId();
	}

	/**
	 * Returns the saml idp entity ID of this saml ib slo message.
	 *
	 * @return the saml idp entity ID of this saml ib slo message
	 */
	@Override
	public String getSamlIdpEntityId() {
		return model.getSamlIdpEntityId();
	}

	/**
	 * Returns the saml idp session index of this saml ib slo message.
	 *
	 * @return the saml idp session index of this saml ib slo message
	 */
	@Override
	public String getSamlIdpSessionIndex() {
		return model.getSamlIdpSessionIndex();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this saml ib slo message.
	 *
	 * @param companyId the company ID of this saml ib slo message
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this saml ib slo message.
	 *
	 * @param createDate the create date of this saml ib slo message
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the logout request xml of this saml ib slo message.
	 *
	 * @param logoutRequestXml the logout request xml of this saml ib slo message
	 */
	@Override
	public void setLogoutRequestXml(String logoutRequestXml) {
		model.setLogoutRequestXml(logoutRequestXml);
	}

	/**
	 * Sets the primary key of this saml ib slo message.
	 *
	 * @param primaryKey the primary key of this saml ib slo message
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the saml ib slo message ID of this saml ib slo message.
	 *
	 * @param samlIbSloMessageId the saml ib slo message ID of this saml ib slo message
	 */
	@Override
	public void setSamlIbSloMessageId(long samlIbSloMessageId) {
		model.setSamlIbSloMessageId(samlIbSloMessageId);
	}

	/**
	 * Sets the saml idp entity ID of this saml ib slo message.
	 *
	 * @param samlIdpEntityId the saml idp entity ID of this saml ib slo message
	 */
	@Override
	public void setSamlIdpEntityId(String samlIdpEntityId) {
		model.setSamlIdpEntityId(samlIdpEntityId);
	}

	/**
	 * Sets the saml idp session index of this saml ib slo message.
	 *
	 * @param samlIdpSessionIndex the saml idp session index of this saml ib slo message
	 */
	@Override
	public void setSamlIdpSessionIndex(String samlIdpSessionIndex) {
		model.setSamlIdpSessionIndex(samlIdpSessionIndex);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected SamlIbSloMessageWrapper wrap(SamlIbSloMessage samlIbSloMessage) {
		return new SamlIbSloMessageWrapper(samlIbSloMessage);
	}

}