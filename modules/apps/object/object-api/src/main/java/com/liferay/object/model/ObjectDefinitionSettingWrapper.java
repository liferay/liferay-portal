/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link ObjectDefinitionSetting}.
 * </p>
 *
 * @author Marco Leo
 * @see ObjectDefinitionSetting
 * @generated
 */
public class ObjectDefinitionSettingWrapper
	extends BaseModelWrapper<ObjectDefinitionSetting>
	implements ModelWrapper<ObjectDefinitionSetting>, ObjectDefinitionSetting {

	public ObjectDefinitionSettingWrapper(
		ObjectDefinitionSetting objectDefinitionSetting) {

		super(objectDefinitionSetting);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put(
			"objectDefinitionSettingId", getObjectDefinitionSettingId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("objectDefinitionId", getObjectDefinitionId());
		attributes.put("name", getName());
		attributes.put("value", getValue());

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

		Long objectDefinitionSettingId = (Long)attributes.get(
			"objectDefinitionSettingId");

		if (objectDefinitionSettingId != null) {
			setObjectDefinitionSettingId(objectDefinitionSettingId);
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

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String value = (String)attributes.get("value");

		if (value != null) {
			setValue(value);
		}
	}

	@Override
	public ObjectDefinitionSetting cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this object definition setting.
	 *
	 * @return the company ID of this object definition setting
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this object definition setting.
	 *
	 * @return the create date of this object definition setting
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the modified date of this object definition setting.
	 *
	 * @return the modified date of this object definition setting
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this object definition setting.
	 *
	 * @return the mvcc version of this object definition setting
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this object definition setting.
	 *
	 * @return the name of this object definition setting
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the object definition ID of this object definition setting.
	 *
	 * @return the object definition ID of this object definition setting
	 */
	@Override
	public long getObjectDefinitionId() {
		return model.getObjectDefinitionId();
	}

	/**
	 * Returns the object definition setting ID of this object definition setting.
	 *
	 * @return the object definition setting ID of this object definition setting
	 */
	@Override
	public long getObjectDefinitionSettingId() {
		return model.getObjectDefinitionSettingId();
	}

	/**
	 * Returns the primary key of this object definition setting.
	 *
	 * @return the primary key of this object definition setting
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this object definition setting.
	 *
	 * @return the user ID of this object definition setting
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this object definition setting.
	 *
	 * @return the user name of this object definition setting
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this object definition setting.
	 *
	 * @return the user uuid of this object definition setting
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this object definition setting.
	 *
	 * @return the uuid of this object definition setting
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the value of this object definition setting.
	 *
	 * @return the value of this object definition setting
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
	 * Sets the company ID of this object definition setting.
	 *
	 * @param companyId the company ID of this object definition setting
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this object definition setting.
	 *
	 * @param createDate the create date of this object definition setting
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the modified date of this object definition setting.
	 *
	 * @param modifiedDate the modified date of this object definition setting
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this object definition setting.
	 *
	 * @param mvccVersion the mvcc version of this object definition setting
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this object definition setting.
	 *
	 * @param name the name of this object definition setting
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the object definition ID of this object definition setting.
	 *
	 * @param objectDefinitionId the object definition ID of this object definition setting
	 */
	@Override
	public void setObjectDefinitionId(long objectDefinitionId) {
		model.setObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Sets the object definition setting ID of this object definition setting.
	 *
	 * @param objectDefinitionSettingId the object definition setting ID of this object definition setting
	 */
	@Override
	public void setObjectDefinitionSettingId(long objectDefinitionSettingId) {
		model.setObjectDefinitionSettingId(objectDefinitionSettingId);
	}

	/**
	 * Sets the primary key of this object definition setting.
	 *
	 * @param primaryKey the primary key of this object definition setting
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this object definition setting.
	 *
	 * @param userId the user ID of this object definition setting
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this object definition setting.
	 *
	 * @param userName the user name of this object definition setting
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this object definition setting.
	 *
	 * @param userUuid the user uuid of this object definition setting
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this object definition setting.
	 *
	 * @param uuid the uuid of this object definition setting
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the value of this object definition setting.
	 *
	 * @param value the value of this object definition setting
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
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected ObjectDefinitionSettingWrapper wrap(
		ObjectDefinitionSetting objectDefinitionSetting) {

		return new ObjectDefinitionSettingWrapper(objectDefinitionSetting);
	}

}