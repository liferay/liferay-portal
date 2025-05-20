/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.model;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.ModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PatcherProductVersion}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherProductVersion
 * @generated
 */
public class PatcherProductVersionWrapper implements PatcherProductVersion,
	ModelWrapper<PatcherProductVersion> {
	public PatcherProductVersionWrapper(
		PatcherProductVersion patcherProductVersion) {
		_patcherProductVersion = patcherProductVersion;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherProductVersion.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherProductVersion.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());
		attributes.put("fixDeliveryMethod", getFixDeliveryMethod());
		attributes.put("moduleFolderName", getModuleFolderName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
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

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Integer fixDeliveryMethod = (Integer)attributes.get("fixDeliveryMethod");

		if (fixDeliveryMethod != null) {
			setFixDeliveryMethod(fixDeliveryMethod);
		}

		String moduleFolderName = (String)attributes.get("moduleFolderName");

		if (moduleFolderName != null) {
			setModuleFolderName(moduleFolderName);
		}
	}

	/**
	* Returns the primary key of this patcher product version.
	*
	* @return the primary key of this patcher product version
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherProductVersion.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher product version.
	*
	* @param primaryKey the primary key of this patcher product version
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherProductVersion.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher product version ID of this patcher product version.
	*
	* @return the patcher product version ID of this patcher product version
	*/
	@Override
	public long getPatcherProductVersionId() {
		return _patcherProductVersion.getPatcherProductVersionId();
	}

	/**
	* Sets the patcher product version ID of this patcher product version.
	*
	* @param patcherProductVersionId the patcher product version ID of this patcher product version
	*/
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherProductVersion.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	* Returns the company ID of this patcher product version.
	*
	* @return the company ID of this patcher product version
	*/
	@Override
	public long getCompanyId() {
		return _patcherProductVersion.getCompanyId();
	}

	/**
	* Sets the company ID of this patcher product version.
	*
	* @param companyId the company ID of this patcher product version
	*/
	@Override
	public void setCompanyId(long companyId) {
		_patcherProductVersion.setCompanyId(companyId);
	}

	/**
	* Returns the user ID of this patcher product version.
	*
	* @return the user ID of this patcher product version
	*/
	@Override
	public long getUserId() {
		return _patcherProductVersion.getUserId();
	}

	/**
	* Sets the user ID of this patcher product version.
	*
	* @param userId the user ID of this patcher product version
	*/
	@Override
	public void setUserId(long userId) {
		_patcherProductVersion.setUserId(userId);
	}

	/**
	* Returns the user uuid of this patcher product version.
	*
	* @return the user uuid of this patcher product version
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersion.getUserUuid();
	}

	/**
	* Sets the user uuid of this patcher product version.
	*
	* @param userUuid the user uuid of this patcher product version
	*/
	@Override
	public void setUserUuid(java.lang.String userUuid) {
		_patcherProductVersion.setUserUuid(userUuid);
	}

	/**
	* Returns the user name of this patcher product version.
	*
	* @return the user name of this patcher product version
	*/
	@Override
	public java.lang.String getUserName() {
		return _patcherProductVersion.getUserName();
	}

	/**
	* Sets the user name of this patcher product version.
	*
	* @param userName the user name of this patcher product version
	*/
	@Override
	public void setUserName(java.lang.String userName) {
		_patcherProductVersion.setUserName(userName);
	}

	/**
	* Returns the create date of this patcher product version.
	*
	* @return the create date of this patcher product version
	*/
	@Override
	public java.util.Date getCreateDate() {
		return _patcherProductVersion.getCreateDate();
	}

	/**
	* Sets the create date of this patcher product version.
	*
	* @param createDate the create date of this patcher product version
	*/
	@Override
	public void setCreateDate(java.util.Date createDate) {
		_patcherProductVersion.setCreateDate(createDate);
	}

	/**
	* Returns the modified date of this patcher product version.
	*
	* @return the modified date of this patcher product version
	*/
	@Override
	public java.util.Date getModifiedDate() {
		return _patcherProductVersion.getModifiedDate();
	}

	/**
	* Sets the modified date of this patcher product version.
	*
	* @param modifiedDate the modified date of this patcher product version
	*/
	@Override
	public void setModifiedDate(java.util.Date modifiedDate) {
		_patcherProductVersion.setModifiedDate(modifiedDate);
	}

	/**
	* Returns the name of this patcher product version.
	*
	* @return the name of this patcher product version
	*/
	@Override
	public java.lang.String getName() {
		return _patcherProductVersion.getName();
	}

	/**
	* Sets the name of this patcher product version.
	*
	* @param name the name of this patcher product version
	*/
	@Override
	public void setName(java.lang.String name) {
		_patcherProductVersion.setName(name);
	}

	/**
	* Returns the fix delivery method of this patcher product version.
	*
	* @return the fix delivery method of this patcher product version
	*/
	@Override
	public int getFixDeliveryMethod() {
		return _patcherProductVersion.getFixDeliveryMethod();
	}

	/**
	* Sets the fix delivery method of this patcher product version.
	*
	* @param fixDeliveryMethod the fix delivery method of this patcher product version
	*/
	@Override
	public void setFixDeliveryMethod(int fixDeliveryMethod) {
		_patcherProductVersion.setFixDeliveryMethod(fixDeliveryMethod);
	}

	/**
	* Returns the module folder name of this patcher product version.
	*
	* @return the module folder name of this patcher product version
	*/
	@Override
	public java.lang.String getModuleFolderName() {
		return _patcherProductVersion.getModuleFolderName();
	}

	/**
	* Sets the module folder name of this patcher product version.
	*
	* @param moduleFolderName the module folder name of this patcher product version
	*/
	@Override
	public void setModuleFolderName(java.lang.String moduleFolderName) {
		_patcherProductVersion.setModuleFolderName(moduleFolderName);
	}

	@Override
	public boolean isNew() {
		return _patcherProductVersion.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherProductVersion.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherProductVersion.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherProductVersion.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherProductVersion.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherProductVersion.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherProductVersion.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherProductVersion.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherProductVersion.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherProductVersion.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherProductVersion.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherProductVersionWrapper((PatcherProductVersion)_patcherProductVersion.clone());
	}

	@Override
	public int compareTo(
		com.liferay.osb.patcher.model.PatcherProductVersion patcherProductVersion) {
		return _patcherProductVersion.compareTo(patcherProductVersion);
	}

	@Override
	public int hashCode() {
		return _patcherProductVersion.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherProductVersion> toCacheModel() {
		return _patcherProductVersion.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion toEscapedModel() {
		return new PatcherProductVersionWrapper(_patcherProductVersion.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion toUnescapedModel() {
		return new PatcherProductVersionWrapper(_patcherProductVersion.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherProductVersion.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherProductVersion.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherProductVersion.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherProductVersionWrapper)) {
			return false;
		}

		PatcherProductVersionWrapper patcherProductVersionWrapper = (PatcherProductVersionWrapper)obj;

		if (Validator.equals(_patcherProductVersion,
					patcherProductVersionWrapper._patcherProductVersion)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherProductVersion getWrappedPatcherProductVersion() {
		return _patcherProductVersion;
	}

	@Override
	public PatcherProductVersion getWrappedModel() {
		return _patcherProductVersion;
	}

	@Override
	public void resetOriginalValues() {
		_patcherProductVersion.resetOriginalValues();
	}

	private PatcherProductVersion _patcherProductVersion;
}