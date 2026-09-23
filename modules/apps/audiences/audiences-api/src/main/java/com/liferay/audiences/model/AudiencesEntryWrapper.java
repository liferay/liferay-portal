/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link AudiencesEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntry
 * @generated
 */
public class AudiencesEntryWrapper
	extends BaseModelWrapper<AudiencesEntry>
	implements AudiencesEntry, ModelWrapper<AudiencesEntry> {

	public AudiencesEntryWrapper(AudiencesEntry audiencesEntry) {
		super(audiencesEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("audiencesEntryId", getAudiencesEntryId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("json", getJSON());
		attributes.put("name", getName());

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

		Long audiencesEntryId = (Long)attributes.get("audiencesEntryId");

		if (audiencesEntryId != null) {
			setAudiencesEntryId(audiencesEntryId);
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

		String json = (String)attributes.get("json");

		if (json != null) {
			setJSON(json);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public AudiencesEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the audiences entry ID of this audiences entry.
	 *
	 * @return the audiences entry ID of this audiences entry
	 */
	@Override
	public long getAudiencesEntryId() {
		return model.getAudiencesEntryId();
	}

	/**
	 * Returns the company ID of this audiences entry.
	 *
	 * @return the company ID of this audiences entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this audiences entry.
	 *
	 * @return the create date of this audiences entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the external reference code of this audiences entry.
	 *
	 * @return the external reference code of this audiences entry
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	@Override
	public java.util.List<String> getGroupERCs() {
		return model.getGroupERCs();
	}

	/**
	 * Returns the json of this audiences entry.
	 *
	 * @return the json of this audiences entry
	 */
	@Override
	public String getJSON() {
		return model.getJSON();
	}

	/**
	 * Returns the modified date of this audiences entry.
	 *
	 * @return the modified date of this audiences entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this audiences entry.
	 *
	 * @return the mvcc version of this audiences entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this audiences entry.
	 *
	 * @return the name of this audiences entry
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this audiences entry.
	 *
	 * @return the primary key of this audiences entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this audiences entry.
	 *
	 * @return the user ID of this audiences entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this audiences entry.
	 *
	 * @return the user name of this audiences entry
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this audiences entry.
	 *
	 * @return the user uuid of this audiences entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the audiences entry ID of this audiences entry.
	 *
	 * @param audiencesEntryId the audiences entry ID of this audiences entry
	 */
	@Override
	public void setAudiencesEntryId(long audiencesEntryId) {
		model.setAudiencesEntryId(audiencesEntryId);
	}

	/**
	 * Sets the company ID of this audiences entry.
	 *
	 * @param companyId the company ID of this audiences entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this audiences entry.
	 *
	 * @param createDate the create date of this audiences entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the external reference code of this audiences entry.
	 *
	 * @param externalReferenceCode the external reference code of this audiences entry
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the json of this audiences entry.
	 *
	 * @param json the json of this audiences entry
	 */
	@Override
	public void setJSON(String json) {
		model.setJSON(json);
	}

	/**
	 * Sets the modified date of this audiences entry.
	 *
	 * @param modifiedDate the modified date of this audiences entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this audiences entry.
	 *
	 * @param mvccVersion the mvcc version of this audiences entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this audiences entry.
	 *
	 * @param name the name of this audiences entry
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this audiences entry.
	 *
	 * @param primaryKey the primary key of this audiences entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this audiences entry.
	 *
	 * @param userId the user ID of this audiences entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this audiences entry.
	 *
	 * @param userName the user name of this audiences entry
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this audiences entry.
	 *
	 * @param userUuid the user uuid of this audiences entry
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
	protected AudiencesEntryWrapper wrap(AudiencesEntry audiencesEntry) {
		return new AudiencesEntryWrapper(audiencesEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-708844041