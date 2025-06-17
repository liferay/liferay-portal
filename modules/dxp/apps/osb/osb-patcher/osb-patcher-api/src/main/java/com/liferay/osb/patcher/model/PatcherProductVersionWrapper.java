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
 * This class is a wrapper for {@link PatcherProductVersion}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProductVersion
 * @generated
 */
public class PatcherProductVersionWrapper
	extends BaseModelWrapper<PatcherProductVersion>
	implements ModelWrapper<PatcherProductVersion>, PatcherProductVersion {

	public PatcherProductVersionWrapper(
		PatcherProductVersion patcherProductVersion) {

		super(patcherProductVersion);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("fixDeliveryMethod", getFixDeliveryMethod());
		attributes.put("moduleFolderName", getModuleFolderName());
		attributes.put("name", getName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long patcherProductVersionId = (Long)attributes.get(
			"patcherProductVersionId");

		if (patcherProductVersionId != null) {
			setPatcherProductVersionId(patcherProductVersionId);
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

		Integer fixDeliveryMethod = (Integer)attributes.get(
			"fixDeliveryMethod");

		if (fixDeliveryMethod != null) {
			setFixDeliveryMethod(fixDeliveryMethod);
		}

		String moduleFolderName = (String)attributes.get("moduleFolderName");

		if (moduleFolderName != null) {
			setModuleFolderName(moduleFolderName);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public PatcherProductVersion cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this patcher product version.
	 *
	 * @return the company ID of this patcher product version
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this patcher product version.
	 *
	 * @return the create date of this patcher product version
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the fix delivery method of this patcher product version.
	 *
	 * @return the fix delivery method of this patcher product version
	 */
	@Override
	public int getFixDeliveryMethod() {
		return model.getFixDeliveryMethod();
	}

	/**
	 * Returns the modified date of this patcher product version.
	 *
	 * @return the modified date of this patcher product version
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the module folder name of this patcher product version.
	 *
	 * @return the module folder name of this patcher product version
	 */
	@Override
	public String getModuleFolderName() {
		return model.getModuleFolderName();
	}

	/**
	 * Returns the mvcc version of this patcher product version.
	 *
	 * @return the mvcc version of this patcher product version
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this patcher product version.
	 *
	 * @return the name of this patcher product version
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the patcher product version ID of this patcher product version.
	 *
	 * @return the patcher product version ID of this patcher product version
	 */
	@Override
	public long getPatcherProductVersionId() {
		return model.getPatcherProductVersionId();
	}

	/**
	 * Returns the primary key of this patcher product version.
	 *
	 * @return the primary key of this patcher product version
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this patcher product version.
	 *
	 * @return the user ID of this patcher product version
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this patcher product version.
	 *
	 * @return the user name of this patcher product version
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this patcher product version.
	 *
	 * @return the user uuid of this patcher product version
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
	 * Sets the company ID of this patcher product version.
	 *
	 * @param companyId the company ID of this patcher product version
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this patcher product version.
	 *
	 * @param createDate the create date of this patcher product version
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the fix delivery method of this patcher product version.
	 *
	 * @param fixDeliveryMethod the fix delivery method of this patcher product version
	 */
	@Override
	public void setFixDeliveryMethod(int fixDeliveryMethod) {
		model.setFixDeliveryMethod(fixDeliveryMethod);
	}

	/**
	 * Sets the modified date of this patcher product version.
	 *
	 * @param modifiedDate the modified date of this patcher product version
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the module folder name of this patcher product version.
	 *
	 * @param moduleFolderName the module folder name of this patcher product version
	 */
	@Override
	public void setModuleFolderName(String moduleFolderName) {
		model.setModuleFolderName(moduleFolderName);
	}

	/**
	 * Sets the mvcc version of this patcher product version.
	 *
	 * @param mvccVersion the mvcc version of this patcher product version
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this patcher product version.
	 *
	 * @param name the name of this patcher product version
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the patcher product version ID of this patcher product version.
	 *
	 * @param patcherProductVersionId the patcher product version ID of this patcher product version
	 */
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		model.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	 * Sets the primary key of this patcher product version.
	 *
	 * @param primaryKey the primary key of this patcher product version
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this patcher product version.
	 *
	 * @param userId the user ID of this patcher product version
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this patcher product version.
	 *
	 * @param userName the user name of this patcher product version
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this patcher product version.
	 *
	 * @param userUuid the user uuid of this patcher product version
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
	protected PatcherProductVersionWrapper wrap(
		PatcherProductVersion patcherProductVersion) {

		return new PatcherProductVersionWrapper(patcherProductVersion);
	}

}