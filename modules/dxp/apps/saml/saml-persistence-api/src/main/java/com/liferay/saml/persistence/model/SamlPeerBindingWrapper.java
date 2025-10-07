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
 * This class is a wrapper for {@link SamlPeerBinding}.
 * </p>
 *
 * @author Mika Koivisto
 * @see SamlPeerBinding
 * @generated
 */
public class SamlPeerBindingWrapper
	extends BaseModelWrapper<SamlPeerBinding>
	implements ModelWrapper<SamlPeerBinding>, SamlPeerBinding {

	public SamlPeerBindingWrapper(SamlPeerBinding samlPeerBinding) {
		super(samlPeerBinding);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("samlPeerBindingId", getSamlPeerBindingId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("samlPeerEntityId", getSamlPeerEntityId());
		attributes.put("deleted", isDeleted());
		attributes.put("samlNameIdFormat", getSamlNameIdFormat());
		attributes.put("samlNameIdNameQualifier", getSamlNameIdNameQualifier());
		attributes.put(
			"samlNameIdSpNameQualifier", getSamlNameIdSpNameQualifier());
		attributes.put("samlNameIdSpProvidedId", getSamlNameIdSpProvidedId());
		attributes.put("samlNameIdValue", getSamlNameIdValue());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long samlPeerBindingId = (Long)attributes.get("samlPeerBindingId");

		if (samlPeerBindingId != null) {
			setSamlPeerBindingId(samlPeerBindingId);
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

		String samlPeerEntityId = (String)attributes.get("samlPeerEntityId");

		if (samlPeerEntityId != null) {
			setSamlPeerEntityId(samlPeerEntityId);
		}

		Boolean deleted = (Boolean)attributes.get("deleted");

		if (deleted != null) {
			setDeleted(deleted);
		}

		String samlNameIdFormat = (String)attributes.get("samlNameIdFormat");

		if (samlNameIdFormat != null) {
			setSamlNameIdFormat(samlNameIdFormat);
		}

		String samlNameIdNameQualifier = (String)attributes.get(
			"samlNameIdNameQualifier");

		if (samlNameIdNameQualifier != null) {
			setSamlNameIdNameQualifier(samlNameIdNameQualifier);
		}

		String samlNameIdSpNameQualifier = (String)attributes.get(
			"samlNameIdSpNameQualifier");

		if (samlNameIdSpNameQualifier != null) {
			setSamlNameIdSpNameQualifier(samlNameIdSpNameQualifier);
		}

		String samlNameIdSpProvidedId = (String)attributes.get(
			"samlNameIdSpProvidedId");

		if (samlNameIdSpProvidedId != null) {
			setSamlNameIdSpProvidedId(samlNameIdSpProvidedId);
		}

		String samlNameIdValue = (String)attributes.get("samlNameIdValue");

		if (samlNameIdValue != null) {
			setSamlNameIdValue(samlNameIdValue);
		}
	}

	@Override
	public SamlPeerBinding cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this saml peer binding.
	 *
	 * @return the company ID of this saml peer binding
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this saml peer binding.
	 *
	 * @return the create date of this saml peer binding
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the deleted of this saml peer binding.
	 *
	 * @return the deleted of this saml peer binding
	 */
	@Override
	public boolean getDeleted() {
		return model.getDeleted();
	}

	/**
	 * Returns the primary key of this saml peer binding.
	 *
	 * @return the primary key of this saml peer binding
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the saml name ID format of this saml peer binding.
	 *
	 * @return the saml name ID format of this saml peer binding
	 */
	@Override
	public String getSamlNameIdFormat() {
		return model.getSamlNameIdFormat();
	}

	/**
	 * Returns the saml name ID name qualifier of this saml peer binding.
	 *
	 * @return the saml name ID name qualifier of this saml peer binding
	 */
	@Override
	public String getSamlNameIdNameQualifier() {
		return model.getSamlNameIdNameQualifier();
	}

	/**
	 * Returns the saml name ID sp name qualifier of this saml peer binding.
	 *
	 * @return the saml name ID sp name qualifier of this saml peer binding
	 */
	@Override
	public String getSamlNameIdSpNameQualifier() {
		return model.getSamlNameIdSpNameQualifier();
	}

	/**
	 * Returns the saml name ID sp provided ID of this saml peer binding.
	 *
	 * @return the saml name ID sp provided ID of this saml peer binding
	 */
	@Override
	public String getSamlNameIdSpProvidedId() {
		return model.getSamlNameIdSpProvidedId();
	}

	/**
	 * Returns the saml name ID value of this saml peer binding.
	 *
	 * @return the saml name ID value of this saml peer binding
	 */
	@Override
	public String getSamlNameIdValue() {
		return model.getSamlNameIdValue();
	}

	/**
	 * Returns the saml peer binding ID of this saml peer binding.
	 *
	 * @return the saml peer binding ID of this saml peer binding
	 */
	@Override
	public long getSamlPeerBindingId() {
		return model.getSamlPeerBindingId();
	}

	/**
	 * Returns the saml peer entity ID of this saml peer binding.
	 *
	 * @return the saml peer entity ID of this saml peer binding
	 */
	@Override
	public String getSamlPeerEntityId() {
		return model.getSamlPeerEntityId();
	}

	/**
	 * Returns the user ID of this saml peer binding.
	 *
	 * @return the user ID of this saml peer binding
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this saml peer binding.
	 *
	 * @return the user name of this saml peer binding
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this saml peer binding.
	 *
	 * @return the user uuid of this saml peer binding
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this saml peer binding is deleted.
	 *
	 * @return <code>true</code> if this saml peer binding is deleted; <code>false</code> otherwise
	 */
	@Override
	public boolean isDeleted() {
		return model.isDeleted();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this saml peer binding.
	 *
	 * @param companyId the company ID of this saml peer binding
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this saml peer binding.
	 *
	 * @param createDate the create date of this saml peer binding
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets whether this saml peer binding is deleted.
	 *
	 * @param deleted the deleted of this saml peer binding
	 */
	@Override
	public void setDeleted(boolean deleted) {
		model.setDeleted(deleted);
	}

	/**
	 * Sets the primary key of this saml peer binding.
	 *
	 * @param primaryKey the primary key of this saml peer binding
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the saml name ID format of this saml peer binding.
	 *
	 * @param samlNameIdFormat the saml name ID format of this saml peer binding
	 */
	@Override
	public void setSamlNameIdFormat(String samlNameIdFormat) {
		model.setSamlNameIdFormat(samlNameIdFormat);
	}

	/**
	 * Sets the saml name ID name qualifier of this saml peer binding.
	 *
	 * @param samlNameIdNameQualifier the saml name ID name qualifier of this saml peer binding
	 */
	@Override
	public void setSamlNameIdNameQualifier(String samlNameIdNameQualifier) {
		model.setSamlNameIdNameQualifier(samlNameIdNameQualifier);
	}

	/**
	 * Sets the saml name ID sp name qualifier of this saml peer binding.
	 *
	 * @param samlNameIdSpNameQualifier the saml name ID sp name qualifier of this saml peer binding
	 */
	@Override
	public void setSamlNameIdSpNameQualifier(String samlNameIdSpNameQualifier) {
		model.setSamlNameIdSpNameQualifier(samlNameIdSpNameQualifier);
	}

	/**
	 * Sets the saml name ID sp provided ID of this saml peer binding.
	 *
	 * @param samlNameIdSpProvidedId the saml name ID sp provided ID of this saml peer binding
	 */
	@Override
	public void setSamlNameIdSpProvidedId(String samlNameIdSpProvidedId) {
		model.setSamlNameIdSpProvidedId(samlNameIdSpProvidedId);
	}

	/**
	 * Sets the saml name ID value of this saml peer binding.
	 *
	 * @param samlNameIdValue the saml name ID value of this saml peer binding
	 */
	@Override
	public void setSamlNameIdValue(String samlNameIdValue) {
		model.setSamlNameIdValue(samlNameIdValue);
	}

	/**
	 * Sets the saml peer binding ID of this saml peer binding.
	 *
	 * @param samlPeerBindingId the saml peer binding ID of this saml peer binding
	 */
	@Override
	public void setSamlPeerBindingId(long samlPeerBindingId) {
		model.setSamlPeerBindingId(samlPeerBindingId);
	}

	/**
	 * Sets the saml peer entity ID of this saml peer binding.
	 *
	 * @param samlPeerEntityId the saml peer entity ID of this saml peer binding
	 */
	@Override
	public void setSamlPeerEntityId(String samlPeerEntityId) {
		model.setSamlPeerEntityId(samlPeerEntityId);
	}

	/**
	 * Sets the user ID of this saml peer binding.
	 *
	 * @param userId the user ID of this saml peer binding
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this saml peer binding.
	 *
	 * @param userName the user name of this saml peer binding
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this saml peer binding.
	 *
	 * @param userUuid the user uuid of this saml peer binding
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
	protected SamlPeerBindingWrapper wrap(SamlPeerBinding samlPeerBinding) {
		return new SamlPeerBindingWrapper(samlPeerBinding);
	}

}