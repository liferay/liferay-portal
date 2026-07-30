/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PLOEntry}.
 * </p>
 *
 * @author Drew Brokke
 * @see PLOEntry
 * @generated
 */
public class PLOEntryWrapper
	extends BaseModelWrapper<PLOEntry>
	implements ModelWrapper<PLOEntry>, PLOEntry {

	public PLOEntryWrapper(PLOEntry ploEntry) {
		super(ploEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("ploEntryId", getPloEntryId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("key", getKey());
		attributes.put("languageId", getLanguageId());
		attributes.put("value", getValue());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long ploEntryId = (Long)attributes.get("ploEntryId");

		if (ploEntryId != null) {
			setPloEntryId(ploEntryId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		String languageId = (String)attributes.get("languageId");

		if (languageId != null) {
			setLanguageId(languageId);
		}

		String value = (String)attributes.get("value");

		if (value != null) {
			setValue(value);
		}
	}

	@Override
	public PLOEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this plo entry.
	 *
	 * @return the company ID of this plo entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this plo entry.
	 *
	 * @return the create date of this plo entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the external reference code of this plo entry.
	 *
	 * @return the external reference code of this plo entry
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the key of this plo entry.
	 *
	 * @return the key of this plo entry
	 */
	@Override
	public String getKey() {
		return model.getKey();
	}

	/**
	 * Returns the language ID of this plo entry.
	 *
	 * @return the language ID of this plo entry
	 */
	@Override
	public String getLanguageId() {
		return model.getLanguageId();
	}

	/**
	 * Returns the modified date of this plo entry.
	 *
	 * @return the modified date of this plo entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this plo entry.
	 *
	 * @return the mvcc version of this plo entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the plo entry ID of this plo entry.
	 *
	 * @return the plo entry ID of this plo entry
	 */
	@Override
	public long getPloEntryId() {
		return model.getPloEntryId();
	}

	/**
	 * Returns the primary key of this plo entry.
	 *
	 * @return the primary key of this plo entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this plo entry.
	 *
	 * @return the user ID of this plo entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user uuid of this plo entry.
	 *
	 * @return the user uuid of this plo entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the value of this plo entry.
	 *
	 * @return the value of this plo entry
	 */
	@Override
	public String getValue() {
		return model.getValue();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this plo entry.
	 *
	 * @param companyId the company ID of this plo entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this plo entry.
	 *
	 * @param createDate the create date of this plo entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the external reference code of this plo entry.
	 *
	 * @param externalReferenceCode the external reference code of this plo entry
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the key of this plo entry.
	 *
	 * @param key the key of this plo entry
	 */
	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	/**
	 * Sets the language ID of this plo entry.
	 *
	 * @param languageId the language ID of this plo entry
	 */
	@Override
	public void setLanguageId(String languageId) {
		model.setLanguageId(languageId);
	}

	/**
	 * Sets the modified date of this plo entry.
	 *
	 * @param modifiedDate the modified date of this plo entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this plo entry.
	 *
	 * @param mvccVersion the mvcc version of this plo entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the plo entry ID of this plo entry.
	 *
	 * @param ploEntryId the plo entry ID of this plo entry
	 */
	@Override
	public void setPloEntryId(long ploEntryId) {
		model.setPloEntryId(ploEntryId);
	}

	/**
	 * Sets the primary key of this plo entry.
	 *
	 * @param primaryKey the primary key of this plo entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this plo entry.
	 *
	 * @param userId the user ID of this plo entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user uuid of this plo entry.
	 *
	 * @param userUuid the user uuid of this plo entry
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the value of this plo entry.
	 *
	 * @param value the value of this plo entry
	 */
	@Override
	public void setValue(String value) {
		model.setValue(value);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected PLOEntryWrapper wrap(PLOEntry ploEntry) {
		return new PLOEntryWrapper(ploEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:376864435