/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PatcherFixComponent}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixComponent
 * @generated
 */
public class PatcherFixComponentWrapper
	extends BaseModelWrapper<PatcherFixComponent>
	implements ModelWrapper<PatcherFixComponent>, PatcherFixComponent {

	public PatcherFixComponentWrapper(PatcherFixComponent patcherFixComponent) {
		super(patcherFixComponent);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherFixComponentId", getPatcherFixComponentId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long patcherFixComponentId = (Long)attributes.get(
			"patcherFixComponentId");

		if (patcherFixComponentId != null) {
			setPatcherFixComponentId(patcherFixComponentId);
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

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public PatcherFixComponent cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this patcher fix component.
	 *
	 * @return the company ID of this patcher fix component
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this patcher fix component.
	 *
	 * @return the create date of this patcher fix component
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the modified date of this patcher fix component.
	 *
	 * @return the modified date of this patcher fix component
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this patcher fix component.
	 *
	 * @return the mvcc version of this patcher fix component
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this patcher fix component.
	 *
	 * @return the name of this patcher fix component
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the patcher fix component ID of this patcher fix component.
	 *
	 * @return the patcher fix component ID of this patcher fix component
	 */
	@Override
	public long getPatcherFixComponentId() {
		return model.getPatcherFixComponentId();
	}

	/**
	 * Returns the primary key of this patcher fix component.
	 *
	 * @return the primary key of this patcher fix component
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this patcher fix component.
	 *
	 * @return the user ID of this patcher fix component
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this patcher fix component.
	 *
	 * @return the user name of this patcher fix component
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this patcher fix component.
	 *
	 * @return the user uuid of this patcher fix component
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
	 * Sets the company ID of this patcher fix component.
	 *
	 * @param companyId the company ID of this patcher fix component
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this patcher fix component.
	 *
	 * @param createDate the create date of this patcher fix component
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the modified date of this patcher fix component.
	 *
	 * @param modifiedDate the modified date of this patcher fix component
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this patcher fix component.
	 *
	 * @param mvccVersion the mvcc version of this patcher fix component
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this patcher fix component.
	 *
	 * @param name the name of this patcher fix component
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the patcher fix component ID of this patcher fix component.
	 *
	 * @param patcherFixComponentId the patcher fix component ID of this patcher fix component
	 */
	@Override
	public void setPatcherFixComponentId(long patcherFixComponentId) {
		model.setPatcherFixComponentId(patcherFixComponentId);
	}

	/**
	 * Sets the primary key of this patcher fix component.
	 *
	 * @param primaryKey the primary key of this patcher fix component
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this patcher fix component.
	 *
	 * @param userId the user ID of this patcher fix component
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this patcher fix component.
	 *
	 * @param userName the user name of this patcher fix component
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this patcher fix component.
	 *
	 * @param userUuid the user uuid of this patcher fix component
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
	protected PatcherFixComponentWrapper wrap(
		PatcherFixComponent patcherFixComponent) {

		return new PatcherFixComponentWrapper(patcherFixComponent);
	}

}