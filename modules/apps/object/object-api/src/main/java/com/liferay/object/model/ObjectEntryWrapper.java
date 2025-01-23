/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link ObjectEntry}.
 * </p>
 *
 * @author Marco Leo
 * @see ObjectEntry
 * @generated
 */
public class ObjectEntryWrapper
	extends BaseModelWrapper<ObjectEntry>
	implements ModelWrapper<ObjectEntry>, ObjectEntry {

	public ObjectEntryWrapper(ObjectEntry objectEntry) {
		super(objectEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("objectEntryId", getObjectEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("objectDefinitionId", getObjectDefinitionId());
		attributes.put("rootObjectEntryId", getRootObjectEntryId());
		attributes.put("lastPublishDate", getLastPublishDate());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

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

		Long objectEntryId = (Long)attributes.get("objectEntryId");

		if (objectEntryId != null) {
			setObjectEntryId(objectEntryId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
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

		Long objectDefinitionId = (Long)attributes.get("objectDefinitionId");

		if (objectDefinitionId != null) {
			setObjectDefinitionId(objectDefinitionId);
		}

		Long rootObjectEntryId = (Long)attributes.get("rootObjectEntryId");

		if (rootObjectEntryId != null) {
			setRootObjectEntryId(rootObjectEntryId);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		String statusByUserName = (String)attributes.get("statusByUserName");

		if (statusByUserName != null) {
			setStatusByUserName(statusByUserName);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	@Override
	public ObjectEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this object entry.
	 *
	 * @return the company ID of this object entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this object entry.
	 *
	 * @return the create date of this object entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the external reference code of this object entry.
	 *
	 * @return the external reference code of this object entry
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this object entry.
	 *
	 * @return the group ID of this object entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the last publish date of this object entry.
	 *
	 * @return the last publish date of this object entry
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	@Override
	public Map<String, String> getLocalizedTitleValue()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getLocalizedTitleValue();
	}

	@Override
	public String getModelClassName() {
		return model.getModelClassName();
	}

	/**
	 * Returns the modified date of this object entry.
	 *
	 * @return the modified date of this object entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this object entry.
	 *
	 * @return the mvcc version of this object entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	@Override
	public long getNonzeroGroupId()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getNonzeroGroupId();
	}

	/**
	 * Returns the object definition ID of this object entry.
	 *
	 * @return the object definition ID of this object entry
	 */
	@Override
	public long getObjectDefinitionId() {
		return model.getObjectDefinitionId();
	}

	/**
	 * Returns the object entry ID of this object entry.
	 *
	 * @return the object entry ID of this object entry
	 */
	@Override
	public long getObjectEntryId() {
		return model.getObjectEntryId();
	}

	/**
	 * Returns the primary key of this object entry.
	 *
	 * @return the primary key of this object entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the root object entry ID of this object entry.
	 *
	 * @return the root object entry ID of this object entry
	 */
	@Override
	public long getRootObjectEntryId() {
		return model.getRootObjectEntryId();
	}

	/**
	 * Returns the status of this object entry.
	 *
	 * @return the status of this object entry
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this object entry.
	 *
	 * @return the status by user ID of this object entry
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this object entry.
	 *
	 * @return the status by user name of this object entry
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this object entry.
	 *
	 * @return the status by user uuid of this object entry
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this object entry.
	 *
	 * @return the status date of this object entry
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	@Override
	public String getTitleValue()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getTitleValue();
	}

	/**
	 * Returns the user ID of this object entry.
	 *
	 * @return the user ID of this object entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this object entry.
	 *
	 * @return the user name of this object entry
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this object entry.
	 *
	 * @return the user uuid of this object entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this object entry.
	 *
	 * @return the uuid of this object entry
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public Map<String, Serializable> getValues() {
		return model.getValues();
	}

	/**
	 * Returns <code>true</code> if this object entry is approved.
	 *
	 * @return <code>true</code> if this object entry is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this object entry is denied.
	 *
	 * @return <code>true</code> if this object entry is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this object entry is a draft.
	 *
	 * @return <code>true</code> if this object entry is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this object entry is expired.
	 *
	 * @return <code>true</code> if this object entry is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this object entry is inactive.
	 *
	 * @return <code>true</code> if this object entry is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this object entry is incomplete.
	 *
	 * @return <code>true</code> if this object entry is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this object entry is pending.
	 *
	 * @return <code>true</code> if this object entry is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this object entry is scheduled.
	 *
	 * @return <code>true</code> if this object entry is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this object entry.
	 *
	 * @param companyId the company ID of this object entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this object entry.
	 *
	 * @param createDate the create date of this object entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the external reference code of this object entry.
	 *
	 * @param externalReferenceCode the external reference code of this object entry
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this object entry.
	 *
	 * @param groupId the group ID of this object entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the last publish date of this object entry.
	 *
	 * @param lastPublishDate the last publish date of this object entry
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this object entry.
	 *
	 * @param modifiedDate the modified date of this object entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this object entry.
	 *
	 * @param mvccVersion the mvcc version of this object entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the object definition ID of this object entry.
	 *
	 * @param objectDefinitionId the object definition ID of this object entry
	 */
	@Override
	public void setObjectDefinitionId(long objectDefinitionId) {
		model.setObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Sets the object entry ID of this object entry.
	 *
	 * @param objectEntryId the object entry ID of this object entry
	 */
	@Override
	public void setObjectEntryId(long objectEntryId) {
		model.setObjectEntryId(objectEntryId);
	}

	/**
	 * Sets the primary key of this object entry.
	 *
	 * @param primaryKey the primary key of this object entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the root object entry ID of this object entry.
	 *
	 * @param rootObjectEntryId the root object entry ID of this object entry
	 */
	@Override
	public void setRootObjectEntryId(long rootObjectEntryId) {
		model.setRootObjectEntryId(rootObjectEntryId);
	}

	/**
	 * Sets the status of this object entry.
	 *
	 * @param status the status of this object entry
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this object entry.
	 *
	 * @param statusByUserId the status by user ID of this object entry
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this object entry.
	 *
	 * @param statusByUserName the status by user name of this object entry
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this object entry.
	 *
	 * @param statusByUserUuid the status by user uuid of this object entry
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this object entry.
	 *
	 * @param statusDate the status date of this object entry
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	@Override
	public void setTransientValues(Map<String, Serializable> values) {
		model.setTransientValues(values);
	}

	/**
	 * Sets the user ID of this object entry.
	 *
	 * @param userId the user ID of this object entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this object entry.
	 *
	 * @param userName the user name of this object entry
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this object entry.
	 *
	 * @param userUuid the user uuid of this object entry
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this object entry.
	 *
	 * @param uuid the uuid of this object entry
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	@Override
	public void setValues(Map<String, Serializable> values) {
		model.setValues(values);
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
	protected ObjectEntryWrapper wrap(ObjectEntry objectEntry) {
		return new ObjectEntryWrapper(objectEntry);
	}

}