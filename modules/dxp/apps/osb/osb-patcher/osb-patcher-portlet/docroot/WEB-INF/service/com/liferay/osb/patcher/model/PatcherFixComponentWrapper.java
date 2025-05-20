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
 * This class is a wrapper for {@link PatcherFixComponent}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixComponent
 * @generated
 */
public class PatcherFixComponentWrapper implements PatcherFixComponent,
	ModelWrapper<PatcherFixComponent> {
	public PatcherFixComponentWrapper(PatcherFixComponent patcherFixComponent) {
		_patcherFixComponent = patcherFixComponent;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherFixComponent.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherFixComponent.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

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

	/**
	* Returns the primary key of this patcher fix component.
	*
	* @return the primary key of this patcher fix component
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherFixComponent.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher fix component.
	*
	* @param primaryKey the primary key of this patcher fix component
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherFixComponent.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher fix component ID of this patcher fix component.
	*
	* @return the patcher fix component ID of this patcher fix component
	*/
	@Override
	public long getPatcherFixComponentId() {
		return _patcherFixComponent.getPatcherFixComponentId();
	}

	/**
	* Sets the patcher fix component ID of this patcher fix component.
	*
	* @param patcherFixComponentId the patcher fix component ID of this patcher fix component
	*/
	@Override
	public void setPatcherFixComponentId(long patcherFixComponentId) {
		_patcherFixComponent.setPatcherFixComponentId(patcherFixComponentId);
	}

	/**
	* Returns the company ID of this patcher fix component.
	*
	* @return the company ID of this patcher fix component
	*/
	@Override
	public long getCompanyId() {
		return _patcherFixComponent.getCompanyId();
	}

	/**
	* Sets the company ID of this patcher fix component.
	*
	* @param companyId the company ID of this patcher fix component
	*/
	@Override
	public void setCompanyId(long companyId) {
		_patcherFixComponent.setCompanyId(companyId);
	}

	/**
	* Returns the user ID of this patcher fix component.
	*
	* @return the user ID of this patcher fix component
	*/
	@Override
	public long getUserId() {
		return _patcherFixComponent.getUserId();
	}

	/**
	* Sets the user ID of this patcher fix component.
	*
	* @param userId the user ID of this patcher fix component
	*/
	@Override
	public void setUserId(long userId) {
		_patcherFixComponent.setUserId(userId);
	}

	/**
	* Returns the user uuid of this patcher fix component.
	*
	* @return the user uuid of this patcher fix component
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixComponent.getUserUuid();
	}

	/**
	* Sets the user uuid of this patcher fix component.
	*
	* @param userUuid the user uuid of this patcher fix component
	*/
	@Override
	public void setUserUuid(java.lang.String userUuid) {
		_patcherFixComponent.setUserUuid(userUuid);
	}

	/**
	* Returns the user name of this patcher fix component.
	*
	* @return the user name of this patcher fix component
	*/
	@Override
	public java.lang.String getUserName() {
		return _patcherFixComponent.getUserName();
	}

	/**
	* Sets the user name of this patcher fix component.
	*
	* @param userName the user name of this patcher fix component
	*/
	@Override
	public void setUserName(java.lang.String userName) {
		_patcherFixComponent.setUserName(userName);
	}

	/**
	* Returns the create date of this patcher fix component.
	*
	* @return the create date of this patcher fix component
	*/
	@Override
	public java.util.Date getCreateDate() {
		return _patcherFixComponent.getCreateDate();
	}

	/**
	* Sets the create date of this patcher fix component.
	*
	* @param createDate the create date of this patcher fix component
	*/
	@Override
	public void setCreateDate(java.util.Date createDate) {
		_patcherFixComponent.setCreateDate(createDate);
	}

	/**
	* Returns the modified date of this patcher fix component.
	*
	* @return the modified date of this patcher fix component
	*/
	@Override
	public java.util.Date getModifiedDate() {
		return _patcherFixComponent.getModifiedDate();
	}

	/**
	* Sets the modified date of this patcher fix component.
	*
	* @param modifiedDate the modified date of this patcher fix component
	*/
	@Override
	public void setModifiedDate(java.util.Date modifiedDate) {
		_patcherFixComponent.setModifiedDate(modifiedDate);
	}

	/**
	* Returns the name of this patcher fix component.
	*
	* @return the name of this patcher fix component
	*/
	@Override
	public java.lang.String getName() {
		return _patcherFixComponent.getName();
	}

	/**
	* Sets the name of this patcher fix component.
	*
	* @param name the name of this patcher fix component
	*/
	@Override
	public void setName(java.lang.String name) {
		_patcherFixComponent.setName(name);
	}

	@Override
	public boolean isNew() {
		return _patcherFixComponent.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherFixComponent.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherFixComponent.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherFixComponent.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherFixComponent.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherFixComponent.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherFixComponent.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherFixComponent.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherFixComponent.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherFixComponent.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherFixComponent.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherFixComponentWrapper((PatcherFixComponent)_patcherFixComponent.clone());
	}

	@Override
	public int compareTo(
		com.liferay.osb.patcher.model.PatcherFixComponent patcherFixComponent) {
		return _patcherFixComponent.compareTo(patcherFixComponent);
	}

	@Override
	public int hashCode() {
		return _patcherFixComponent.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherFixComponent> toCacheModel() {
		return _patcherFixComponent.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixComponent toEscapedModel() {
		return new PatcherFixComponentWrapper(_patcherFixComponent.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixComponent toUnescapedModel() {
		return new PatcherFixComponentWrapper(_patcherFixComponent.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherFixComponent.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherFixComponent.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixComponent.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherFixComponentWrapper)) {
			return false;
		}

		PatcherFixComponentWrapper patcherFixComponentWrapper = (PatcherFixComponentWrapper)obj;

		if (Validator.equals(_patcherFixComponent,
					patcherFixComponentWrapper._patcherFixComponent)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherFixComponent getWrappedPatcherFixComponent() {
		return _patcherFixComponent;
	}

	@Override
	public PatcherFixComponent getWrappedModel() {
		return _patcherFixComponent;
	}

	@Override
	public void resetOriginalValues() {
		_patcherFixComponent.resetOriginalValues();
	}

	private PatcherFixComponent _patcherFixComponent;
}