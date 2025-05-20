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
 * This class is a wrapper for {@link PatcherProjectVersion}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherProjectVersion
 * @generated
 */
public class PatcherProjectVersionWrapper implements PatcherProjectVersion,
	ModelWrapper<PatcherProjectVersion> {
	public PatcherProjectVersionWrapper(
		PatcherProjectVersion patcherProjectVersion) {
		_patcherProjectVersion = patcherProjectVersion;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherProjectVersion.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherProjectVersion.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("rootPatcherProjectVersionId",
			getRootPatcherProjectVersionId());
		attributes.put("name", getName());
		attributes.put("combinedBranch", getCombinedBranch());
		attributes.put("hide", getHide());
		attributes.put("committish", getCommittish());
		attributes.put("repositoryName", getRepositoryName());
		attributes.put("fixedIssues", getFixedIssues());
		attributes.put("productVersion", getProductVersion());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherProjectVersionId = (Long)attributes.get(
				"patcherProjectVersionId");

		if (patcherProjectVersionId != null) {
			setPatcherProjectVersionId(patcherProjectVersionId);
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

		Long patcherProductVersionId = (Long)attributes.get(
				"patcherProductVersionId");

		if (patcherProductVersionId != null) {
			setPatcherProductVersionId(patcherProductVersionId);
		}

		Long rootPatcherProjectVersionId = (Long)attributes.get(
				"rootPatcherProjectVersionId");

		if (rootPatcherProjectVersionId != null) {
			setRootPatcherProjectVersionId(rootPatcherProjectVersionId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Boolean combinedBranch = (Boolean)attributes.get("combinedBranch");

		if (combinedBranch != null) {
			setCombinedBranch(combinedBranch);
		}

		Boolean hide = (Boolean)attributes.get("hide");

		if (hide != null) {
			setHide(hide);
		}

		String committish = (String)attributes.get("committish");

		if (committish != null) {
			setCommittish(committish);
		}

		String repositoryName = (String)attributes.get("repositoryName");

		if (repositoryName != null) {
			setRepositoryName(repositoryName);
		}

		String fixedIssues = (String)attributes.get("fixedIssues");

		if (fixedIssues != null) {
			setFixedIssues(fixedIssues);
		}

		Integer productVersion = (Integer)attributes.get("productVersion");

		if (productVersion != null) {
			setProductVersion(productVersion);
		}
	}

	/**
	* Returns the primary key of this patcher project version.
	*
	* @return the primary key of this patcher project version
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherProjectVersion.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher project version.
	*
	* @param primaryKey the primary key of this patcher project version
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherProjectVersion.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher project version ID of this patcher project version.
	*
	* @return the patcher project version ID of this patcher project version
	*/
	@Override
	public long getPatcherProjectVersionId() {
		return _patcherProjectVersion.getPatcherProjectVersionId();
	}

	/**
	* Sets the patcher project version ID of this patcher project version.
	*
	* @param patcherProjectVersionId the patcher project version ID of this patcher project version
	*/
	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherProjectVersion.setPatcherProjectVersionId(patcherProjectVersionId);
	}

	/**
	* Returns the company ID of this patcher project version.
	*
	* @return the company ID of this patcher project version
	*/
	@Override
	public long getCompanyId() {
		return _patcherProjectVersion.getCompanyId();
	}

	/**
	* Sets the company ID of this patcher project version.
	*
	* @param companyId the company ID of this patcher project version
	*/
	@Override
	public void setCompanyId(long companyId) {
		_patcherProjectVersion.setCompanyId(companyId);
	}

	/**
	* Returns the user ID of this patcher project version.
	*
	* @return the user ID of this patcher project version
	*/
	@Override
	public long getUserId() {
		return _patcherProjectVersion.getUserId();
	}

	/**
	* Sets the user ID of this patcher project version.
	*
	* @param userId the user ID of this patcher project version
	*/
	@Override
	public void setUserId(long userId) {
		_patcherProjectVersion.setUserId(userId);
	}

	/**
	* Returns the user uuid of this patcher project version.
	*
	* @return the user uuid of this patcher project version
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersion.getUserUuid();
	}

	/**
	* Sets the user uuid of this patcher project version.
	*
	* @param userUuid the user uuid of this patcher project version
	*/
	@Override
	public void setUserUuid(java.lang.String userUuid) {
		_patcherProjectVersion.setUserUuid(userUuid);
	}

	/**
	* Returns the user name of this patcher project version.
	*
	* @return the user name of this patcher project version
	*/
	@Override
	public java.lang.String getUserName() {
		return _patcherProjectVersion.getUserName();
	}

	/**
	* Sets the user name of this patcher project version.
	*
	* @param userName the user name of this patcher project version
	*/
	@Override
	public void setUserName(java.lang.String userName) {
		_patcherProjectVersion.setUserName(userName);
	}

	/**
	* Returns the create date of this patcher project version.
	*
	* @return the create date of this patcher project version
	*/
	@Override
	public java.util.Date getCreateDate() {
		return _patcherProjectVersion.getCreateDate();
	}

	/**
	* Sets the create date of this patcher project version.
	*
	* @param createDate the create date of this patcher project version
	*/
	@Override
	public void setCreateDate(java.util.Date createDate) {
		_patcherProjectVersion.setCreateDate(createDate);
	}

	/**
	* Returns the modified date of this patcher project version.
	*
	* @return the modified date of this patcher project version
	*/
	@Override
	public java.util.Date getModifiedDate() {
		return _patcherProjectVersion.getModifiedDate();
	}

	/**
	* Sets the modified date of this patcher project version.
	*
	* @param modifiedDate the modified date of this patcher project version
	*/
	@Override
	public void setModifiedDate(java.util.Date modifiedDate) {
		_patcherProjectVersion.setModifiedDate(modifiedDate);
	}

	/**
	* Returns the patcher product version ID of this patcher project version.
	*
	* @return the patcher product version ID of this patcher project version
	*/
	@Override
	public long getPatcherProductVersionId() {
		return _patcherProjectVersion.getPatcherProductVersionId();
	}

	/**
	* Sets the patcher product version ID of this patcher project version.
	*
	* @param patcherProductVersionId the patcher product version ID of this patcher project version
	*/
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherProjectVersion.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	* Returns the root patcher project version ID of this patcher project version.
	*
	* @return the root patcher project version ID of this patcher project version
	*/
	@Override
	public long getRootPatcherProjectVersionId() {
		return _patcherProjectVersion.getRootPatcherProjectVersionId();
	}

	/**
	* Sets the root patcher project version ID of this patcher project version.
	*
	* @param rootPatcherProjectVersionId the root patcher project version ID of this patcher project version
	*/
	@Override
	public void setRootPatcherProjectVersionId(long rootPatcherProjectVersionId) {
		_patcherProjectVersion.setRootPatcherProjectVersionId(rootPatcherProjectVersionId);
	}

	/**
	* Returns the name of this patcher project version.
	*
	* @return the name of this patcher project version
	*/
	@Override
	public java.lang.String getName() {
		return _patcherProjectVersion.getName();
	}

	/**
	* Sets the name of this patcher project version.
	*
	* @param name the name of this patcher project version
	*/
	@Override
	public void setName(java.lang.String name) {
		_patcherProjectVersion.setName(name);
	}

	/**
	* Returns the combined branch of this patcher project version.
	*
	* @return the combined branch of this patcher project version
	*/
	@Override
	public boolean getCombinedBranch() {
		return _patcherProjectVersion.getCombinedBranch();
	}

	/**
	* Returns <code>true</code> if this patcher project version is combined branch.
	*
	* @return <code>true</code> if this patcher project version is combined branch; <code>false</code> otherwise
	*/
	@Override
	public boolean isCombinedBranch() {
		return _patcherProjectVersion.isCombinedBranch();
	}

	/**
	* Sets whether this patcher project version is combined branch.
	*
	* @param combinedBranch the combined branch of this patcher project version
	*/
	@Override
	public void setCombinedBranch(boolean combinedBranch) {
		_patcherProjectVersion.setCombinedBranch(combinedBranch);
	}

	/**
	* Returns the hide of this patcher project version.
	*
	* @return the hide of this patcher project version
	*/
	@Override
	public boolean getHide() {
		return _patcherProjectVersion.getHide();
	}

	/**
	* Returns <code>true</code> if this patcher project version is hide.
	*
	* @return <code>true</code> if this patcher project version is hide; <code>false</code> otherwise
	*/
	@Override
	public boolean isHide() {
		return _patcherProjectVersion.isHide();
	}

	/**
	* Sets whether this patcher project version is hide.
	*
	* @param hide the hide of this patcher project version
	*/
	@Override
	public void setHide(boolean hide) {
		_patcherProjectVersion.setHide(hide);
	}

	/**
	* Returns the committish of this patcher project version.
	*
	* @return the committish of this patcher project version
	*/
	@Override
	public java.lang.String getCommittish() {
		return _patcherProjectVersion.getCommittish();
	}

	/**
	* Sets the committish of this patcher project version.
	*
	* @param committish the committish of this patcher project version
	*/
	@Override
	public void setCommittish(java.lang.String committish) {
		_patcherProjectVersion.setCommittish(committish);
	}

	/**
	* Returns the repository name of this patcher project version.
	*
	* @return the repository name of this patcher project version
	*/
	@Override
	public java.lang.String getRepositoryName() {
		return _patcherProjectVersion.getRepositoryName();
	}

	/**
	* Sets the repository name of this patcher project version.
	*
	* @param repositoryName the repository name of this patcher project version
	*/
	@Override
	public void setRepositoryName(java.lang.String repositoryName) {
		_patcherProjectVersion.setRepositoryName(repositoryName);
	}

	/**
	* Returns the fixed issues of this patcher project version.
	*
	* @return the fixed issues of this patcher project version
	*/
	@Override
	public java.lang.String getFixedIssues() {
		return _patcherProjectVersion.getFixedIssues();
	}

	/**
	* Sets the fixed issues of this patcher project version.
	*
	* @param fixedIssues the fixed issues of this patcher project version
	*/
	@Override
	public void setFixedIssues(java.lang.String fixedIssues) {
		_patcherProjectVersion.setFixedIssues(fixedIssues);
	}

	/**
	* Returns the product version of this patcher project version.
	*
	* @return the product version of this patcher project version
	*/
	@Override
	public int getProductVersion() {
		return _patcherProjectVersion.getProductVersion();
	}

	/**
	* Sets the product version of this patcher project version.
	*
	* @param productVersion the product version of this patcher project version
	*/
	@Override
	public void setProductVersion(int productVersion) {
		_patcherProjectVersion.setProductVersion(productVersion);
	}

	@Override
	public boolean isNew() {
		return _patcherProjectVersion.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherProjectVersion.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherProjectVersion.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherProjectVersion.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherProjectVersion.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherProjectVersion.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherProjectVersion.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherProjectVersion.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherProjectVersion.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherProjectVersion.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherProjectVersion.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherProjectVersionWrapper((PatcherProjectVersion)_patcherProjectVersion.clone());
	}

	@Override
	public int compareTo(
		com.liferay.osb.patcher.model.PatcherProjectVersion patcherProjectVersion) {
		return _patcherProjectVersion.compareTo(patcherProjectVersion);
	}

	@Override
	public int hashCode() {
		return _patcherProjectVersion.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherProjectVersion> toCacheModel() {
		return _patcherProjectVersion.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion toEscapedModel() {
		return new PatcherProjectVersionWrapper(_patcherProjectVersion.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion toUnescapedModel() {
		return new PatcherProjectVersionWrapper(_patcherProjectVersion.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherProjectVersion.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherProjectVersion.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherProjectVersion.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherProjectVersionWrapper)) {
			return false;
		}

		PatcherProjectVersionWrapper patcherProjectVersionWrapper = (PatcherProjectVersionWrapper)obj;

		if (Validator.equals(_patcherProjectVersion,
					patcherProjectVersionWrapper._patcherProjectVersion)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherProjectVersion getWrappedPatcherProjectVersion() {
		return _patcherProjectVersion;
	}

	@Override
	public PatcherProjectVersion getWrappedModel() {
		return _patcherProjectVersion;
	}

	@Override
	public void resetOriginalValues() {
		_patcherProjectVersion.resetOriginalValues();
	}

	private PatcherProjectVersion _patcherProjectVersion;
}