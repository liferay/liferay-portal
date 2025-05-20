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
 * This class is a wrapper for {@link PatcherFix}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFix
 * @generated
 */
public class PatcherFixWrapper implements PatcherFix, ModelWrapper<PatcherFix> {
	public PatcherFixWrapper(PatcherFix patcherFix) {
		_patcherFix = patcherFix;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherFix.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherFix.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherFixId", getPatcherFixId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("name", getName());
		attributes.put("key", getKey());
		attributes.put("keyVersion", getKeyVersion());
		attributes.put("type", getType());
		attributes.put("latestFix", getLatestFix());
		attributes.put("obsolete", getObsolete());
		attributes.put("committish", getCommittish());
		attributes.put("gitHash", getGitHash());
		attributes.put("gitRemoteURL", getGitRemoteURL());
		attributes.put("dependencies", getDependencies());
		attributes.put("requirements", getRequirements());
		attributes.put("requestKey", getRequestKey());
		attributes.put("jenkinsResults", getJenkinsResults());
		attributes.put("comments", getComments());
		attributes.put("fixPackStatus", getFixPackStatus());
		attributes.put("notified", getNotified());
		attributes.put("productVersion", getProductVersion());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherFixId = (Long)attributes.get("patcherFixId");

		if (patcherFixId != null) {
			setPatcherFixId(patcherFixId);
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

		Long patcherProjectVersionId = (Long)attributes.get(
				"patcherProjectVersionId");

		if (patcherProjectVersionId != null) {
			setPatcherProjectVersionId(patcherProjectVersionId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		Double keyVersion = (Double)attributes.get("keyVersion");

		if (keyVersion != null) {
			setKeyVersion(keyVersion);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Boolean latestFix = (Boolean)attributes.get("latestFix");

		if (latestFix != null) {
			setLatestFix(latestFix);
		}

		Boolean obsolete = (Boolean)attributes.get("obsolete");

		if (obsolete != null) {
			setObsolete(obsolete);
		}

		String committish = (String)attributes.get("committish");

		if (committish != null) {
			setCommittish(committish);
		}

		String gitHash = (String)attributes.get("gitHash");

		if (gitHash != null) {
			setGitHash(gitHash);
		}

		String gitRemoteURL = (String)attributes.get("gitRemoteURL");

		if (gitRemoteURL != null) {
			setGitRemoteURL(gitRemoteURL);
		}

		String dependencies = (String)attributes.get("dependencies");

		if (dependencies != null) {
			setDependencies(dependencies);
		}

		String requirements = (String)attributes.get("requirements");

		if (requirements != null) {
			setRequirements(requirements);
		}

		String requestKey = (String)attributes.get("requestKey");

		if (requestKey != null) {
			setRequestKey(requestKey);
		}

		String jenkinsResults = (String)attributes.get("jenkinsResults");

		if (jenkinsResults != null) {
			setJenkinsResults(jenkinsResults);
		}

		String comments = (String)attributes.get("comments");

		if (comments != null) {
			setComments(comments);
		}

		Integer fixPackStatus = (Integer)attributes.get("fixPackStatus");

		if (fixPackStatus != null) {
			setFixPackStatus(fixPackStatus);
		}

		Boolean notified = (Boolean)attributes.get("notified");

		if (notified != null) {
			setNotified(notified);
		}

		Integer productVersion = (Integer)attributes.get("productVersion");

		if (productVersion != null) {
			setProductVersion(productVersion);
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

	/**
	* Returns the primary key of this patcher fix.
	*
	* @return the primary key of this patcher fix
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherFix.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher fix.
	*
	* @param primaryKey the primary key of this patcher fix
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherFix.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher fix ID of this patcher fix.
	*
	* @return the patcher fix ID of this patcher fix
	*/
	@Override
	public long getPatcherFixId() {
		return _patcherFix.getPatcherFixId();
	}

	/**
	* Sets the patcher fix ID of this patcher fix.
	*
	* @param patcherFixId the patcher fix ID of this patcher fix
	*/
	@Override
	public void setPatcherFixId(long patcherFixId) {
		_patcherFix.setPatcherFixId(patcherFixId);
	}

	/**
	* Returns the company ID of this patcher fix.
	*
	* @return the company ID of this patcher fix
	*/
	@Override
	public long getCompanyId() {
		return _patcherFix.getCompanyId();
	}

	/**
	* Sets the company ID of this patcher fix.
	*
	* @param companyId the company ID of this patcher fix
	*/
	@Override
	public void setCompanyId(long companyId) {
		_patcherFix.setCompanyId(companyId);
	}

	/**
	* Returns the user ID of this patcher fix.
	*
	* @return the user ID of this patcher fix
	*/
	@Override
	public long getUserId() {
		return _patcherFix.getUserId();
	}

	/**
	* Sets the user ID of this patcher fix.
	*
	* @param userId the user ID of this patcher fix
	*/
	@Override
	public void setUserId(long userId) {
		_patcherFix.setUserId(userId);
	}

	/**
	* Returns the user uuid of this patcher fix.
	*
	* @return the user uuid of this patcher fix
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFix.getUserUuid();
	}

	/**
	* Sets the user uuid of this patcher fix.
	*
	* @param userUuid the user uuid of this patcher fix
	*/
	@Override
	public void setUserUuid(java.lang.String userUuid) {
		_patcherFix.setUserUuid(userUuid);
	}

	/**
	* Returns the user name of this patcher fix.
	*
	* @return the user name of this patcher fix
	*/
	@Override
	public java.lang.String getUserName() {
		return _patcherFix.getUserName();
	}

	/**
	* Sets the user name of this patcher fix.
	*
	* @param userName the user name of this patcher fix
	*/
	@Override
	public void setUserName(java.lang.String userName) {
		_patcherFix.setUserName(userName);
	}

	/**
	* Returns the create date of this patcher fix.
	*
	* @return the create date of this patcher fix
	*/
	@Override
	public java.util.Date getCreateDate() {
		return _patcherFix.getCreateDate();
	}

	/**
	* Sets the create date of this patcher fix.
	*
	* @param createDate the create date of this patcher fix
	*/
	@Override
	public void setCreateDate(java.util.Date createDate) {
		_patcherFix.setCreateDate(createDate);
	}

	/**
	* Returns the modified date of this patcher fix.
	*
	* @return the modified date of this patcher fix
	*/
	@Override
	public java.util.Date getModifiedDate() {
		return _patcherFix.getModifiedDate();
	}

	/**
	* Sets the modified date of this patcher fix.
	*
	* @param modifiedDate the modified date of this patcher fix
	*/
	@Override
	public void setModifiedDate(java.util.Date modifiedDate) {
		_patcherFix.setModifiedDate(modifiedDate);
	}

	/**
	* Returns the patcher product version ID of this patcher fix.
	*
	* @return the patcher product version ID of this patcher fix
	*/
	@Override
	public long getPatcherProductVersionId() {
		return _patcherFix.getPatcherProductVersionId();
	}

	/**
	* Sets the patcher product version ID of this patcher fix.
	*
	* @param patcherProductVersionId the patcher product version ID of this patcher fix
	*/
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherFix.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	* Returns the patcher project version ID of this patcher fix.
	*
	* @return the patcher project version ID of this patcher fix
	*/
	@Override
	public long getPatcherProjectVersionId() {
		return _patcherFix.getPatcherProjectVersionId();
	}

	/**
	* Sets the patcher project version ID of this patcher fix.
	*
	* @param patcherProjectVersionId the patcher project version ID of this patcher fix
	*/
	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherFix.setPatcherProjectVersionId(patcherProjectVersionId);
	}

	/**
	* Returns the name of this patcher fix.
	*
	* @return the name of this patcher fix
	*/
	@Override
	public java.lang.String getName() {
		return _patcherFix.getName();
	}

	/**
	* Sets the name of this patcher fix.
	*
	* @param name the name of this patcher fix
	*/
	@Override
	public void setName(java.lang.String name) {
		_patcherFix.setName(name);
	}

	/**
	* Returns the key of this patcher fix.
	*
	* @return the key of this patcher fix
	*/
	@Override
	public java.lang.String getKey() {
		return _patcherFix.getKey();
	}

	/**
	* Sets the key of this patcher fix.
	*
	* @param key the key of this patcher fix
	*/
	@Override
	public void setKey(java.lang.String key) {
		_patcherFix.setKey(key);
	}

	/**
	* Returns the key version of this patcher fix.
	*
	* @return the key version of this patcher fix
	*/
	@Override
	public double getKeyVersion() {
		return _patcherFix.getKeyVersion();
	}

	/**
	* Sets the key version of this patcher fix.
	*
	* @param keyVersion the key version of this patcher fix
	*/
	@Override
	public void setKeyVersion(double keyVersion) {
		_patcherFix.setKeyVersion(keyVersion);
	}

	/**
	* Returns the type of this patcher fix.
	*
	* @return the type of this patcher fix
	*/
	@Override
	public int getType() {
		return _patcherFix.getType();
	}

	/**
	* Sets the type of this patcher fix.
	*
	* @param type the type of this patcher fix
	*/
	@Override
	public void setType(int type) {
		_patcherFix.setType(type);
	}

	/**
	* Returns the latest fix of this patcher fix.
	*
	* @return the latest fix of this patcher fix
	*/
	@Override
	public boolean getLatestFix() {
		return _patcherFix.getLatestFix();
	}

	/**
	* Returns <code>true</code> if this patcher fix is latest fix.
	*
	* @return <code>true</code> if this patcher fix is latest fix; <code>false</code> otherwise
	*/
	@Override
	public boolean isLatestFix() {
		return _patcherFix.isLatestFix();
	}

	/**
	* Sets whether this patcher fix is latest fix.
	*
	* @param latestFix the latest fix of this patcher fix
	*/
	@Override
	public void setLatestFix(boolean latestFix) {
		_patcherFix.setLatestFix(latestFix);
	}

	/**
	* Returns the obsolete of this patcher fix.
	*
	* @return the obsolete of this patcher fix
	*/
	@Override
	public boolean getObsolete() {
		return _patcherFix.getObsolete();
	}

	/**
	* Returns <code>true</code> if this patcher fix is obsolete.
	*
	* @return <code>true</code> if this patcher fix is obsolete; <code>false</code> otherwise
	*/
	@Override
	public boolean isObsolete() {
		return _patcherFix.isObsolete();
	}

	/**
	* Sets whether this patcher fix is obsolete.
	*
	* @param obsolete the obsolete of this patcher fix
	*/
	@Override
	public void setObsolete(boolean obsolete) {
		_patcherFix.setObsolete(obsolete);
	}

	/**
	* Returns the committish of this patcher fix.
	*
	* @return the committish of this patcher fix
	*/
	@Override
	public java.lang.String getCommittish() {
		return _patcherFix.getCommittish();
	}

	/**
	* Sets the committish of this patcher fix.
	*
	* @param committish the committish of this patcher fix
	*/
	@Override
	public void setCommittish(java.lang.String committish) {
		_patcherFix.setCommittish(committish);
	}

	/**
	* Returns the git hash of this patcher fix.
	*
	* @return the git hash of this patcher fix
	*/
	@Override
	public java.lang.String getGitHash() {
		return _patcherFix.getGitHash();
	}

	/**
	* Sets the git hash of this patcher fix.
	*
	* @param gitHash the git hash of this patcher fix
	*/
	@Override
	public void setGitHash(java.lang.String gitHash) {
		_patcherFix.setGitHash(gitHash);
	}

	/**
	* Returns the git remote u r l of this patcher fix.
	*
	* @return the git remote u r l of this patcher fix
	*/
	@Override
	public java.lang.String getGitRemoteURL() {
		return _patcherFix.getGitRemoteURL();
	}

	/**
	* Sets the git remote u r l of this patcher fix.
	*
	* @param gitRemoteURL the git remote u r l of this patcher fix
	*/
	@Override
	public void setGitRemoteURL(java.lang.String gitRemoteURL) {
		_patcherFix.setGitRemoteURL(gitRemoteURL);
	}

	/**
	* Returns the dependencies of this patcher fix.
	*
	* @return the dependencies of this patcher fix
	*/
	@Override
	public java.lang.String getDependencies() {
		return _patcherFix.getDependencies();
	}

	/**
	* Sets the dependencies of this patcher fix.
	*
	* @param dependencies the dependencies of this patcher fix
	*/
	@Override
	public void setDependencies(java.lang.String dependencies) {
		_patcherFix.setDependencies(dependencies);
	}

	/**
	* Returns the requirements of this patcher fix.
	*
	* @return the requirements of this patcher fix
	*/
	@Override
	public java.lang.String getRequirements() {
		return _patcherFix.getRequirements();
	}

	/**
	* Sets the requirements of this patcher fix.
	*
	* @param requirements the requirements of this patcher fix
	*/
	@Override
	public void setRequirements(java.lang.String requirements) {
		_patcherFix.setRequirements(requirements);
	}

	/**
	* Returns the request key of this patcher fix.
	*
	* @return the request key of this patcher fix
	*/
	@Override
	public java.lang.String getRequestKey() {
		return _patcherFix.getRequestKey();
	}

	/**
	* Sets the request key of this patcher fix.
	*
	* @param requestKey the request key of this patcher fix
	*/
	@Override
	public void setRequestKey(java.lang.String requestKey) {
		_patcherFix.setRequestKey(requestKey);
	}

	/**
	* Returns the jenkins results of this patcher fix.
	*
	* @return the jenkins results of this patcher fix
	*/
	@Override
	public java.lang.String getJenkinsResults() {
		return _patcherFix.getJenkinsResults();
	}

	/**
	* Sets the jenkins results of this patcher fix.
	*
	* @param jenkinsResults the jenkins results of this patcher fix
	*/
	@Override
	public void setJenkinsResults(java.lang.String jenkinsResults) {
		_patcherFix.setJenkinsResults(jenkinsResults);
	}

	/**
	* Returns the comments of this patcher fix.
	*
	* @return the comments of this patcher fix
	*/
	@Override
	public java.lang.String getComments() {
		return _patcherFix.getComments();
	}

	/**
	* Sets the comments of this patcher fix.
	*
	* @param comments the comments of this patcher fix
	*/
	@Override
	public void setComments(java.lang.String comments) {
		_patcherFix.setComments(comments);
	}

	/**
	* Returns the fix pack status of this patcher fix.
	*
	* @return the fix pack status of this patcher fix
	*/
	@Override
	public int getFixPackStatus() {
		return _patcherFix.getFixPackStatus();
	}

	/**
	* Sets the fix pack status of this patcher fix.
	*
	* @param fixPackStatus the fix pack status of this patcher fix
	*/
	@Override
	public void setFixPackStatus(int fixPackStatus) {
		_patcherFix.setFixPackStatus(fixPackStatus);
	}

	/**
	* Returns the notified of this patcher fix.
	*
	* @return the notified of this patcher fix
	*/
	@Override
	public boolean getNotified() {
		return _patcherFix.getNotified();
	}

	/**
	* Returns <code>true</code> if this patcher fix is notified.
	*
	* @return <code>true</code> if this patcher fix is notified; <code>false</code> otherwise
	*/
	@Override
	public boolean isNotified() {
		return _patcherFix.isNotified();
	}

	/**
	* Sets whether this patcher fix is notified.
	*
	* @param notified the notified of this patcher fix
	*/
	@Override
	public void setNotified(boolean notified) {
		_patcherFix.setNotified(notified);
	}

	/**
	* Returns the product version of this patcher fix.
	*
	* @return the product version of this patcher fix
	*/
	@Override
	public int getProductVersion() {
		return _patcherFix.getProductVersion();
	}

	/**
	* Sets the product version of this patcher fix.
	*
	* @param productVersion the product version of this patcher fix
	*/
	@Override
	public void setProductVersion(int productVersion) {
		_patcherFix.setProductVersion(productVersion);
	}

	/**
	* Returns the status of this patcher fix.
	*
	* @return the status of this patcher fix
	*/
	@Override
	public int getStatus() {
		return _patcherFix.getStatus();
	}

	/**
	* Sets the status of this patcher fix.
	*
	* @param status the status of this patcher fix
	*/
	@Override
	public void setStatus(int status) {
		_patcherFix.setStatus(status);
	}

	/**
	* Returns the status by user ID of this patcher fix.
	*
	* @return the status by user ID of this patcher fix
	*/
	@Override
	public long getStatusByUserId() {
		return _patcherFix.getStatusByUserId();
	}

	/**
	* Sets the status by user ID of this patcher fix.
	*
	* @param statusByUserId the status by user ID of this patcher fix
	*/
	@Override
	public void setStatusByUserId(long statusByUserId) {
		_patcherFix.setStatusByUserId(statusByUserId);
	}

	/**
	* Returns the status by user uuid of this patcher fix.
	*
	* @return the status by user uuid of this patcher fix
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getStatusByUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFix.getStatusByUserUuid();
	}

	/**
	* Sets the status by user uuid of this patcher fix.
	*
	* @param statusByUserUuid the status by user uuid of this patcher fix
	*/
	@Override
	public void setStatusByUserUuid(java.lang.String statusByUserUuid) {
		_patcherFix.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	* Returns the status by user name of this patcher fix.
	*
	* @return the status by user name of this patcher fix
	*/
	@Override
	public java.lang.String getStatusByUserName() {
		return _patcherFix.getStatusByUserName();
	}

	/**
	* Sets the status by user name of this patcher fix.
	*
	* @param statusByUserName the status by user name of this patcher fix
	*/
	@Override
	public void setStatusByUserName(java.lang.String statusByUserName) {
		_patcherFix.setStatusByUserName(statusByUserName);
	}

	/**
	* Returns the status date of this patcher fix.
	*
	* @return the status date of this patcher fix
	*/
	@Override
	public java.util.Date getStatusDate() {
		return _patcherFix.getStatusDate();
	}

	/**
	* Sets the status date of this patcher fix.
	*
	* @param statusDate the status date of this patcher fix
	*/
	@Override
	public void setStatusDate(java.util.Date statusDate) {
		_patcherFix.setStatusDate(statusDate);
	}

	/**
	* @deprecated As of 6.1.0, replaced by {@link #isApproved()}
	*/
	@Override
	public boolean getApproved() {
		return _patcherFix.getApproved();
	}

	/**
	* Returns <code>true</code> if this patcher fix is approved.
	*
	* @return <code>true</code> if this patcher fix is approved; <code>false</code> otherwise
	*/
	@Override
	public boolean isApproved() {
		return _patcherFix.isApproved();
	}

	/**
	* Returns <code>true</code> if this patcher fix is denied.
	*
	* @return <code>true</code> if this patcher fix is denied; <code>false</code> otherwise
	*/
	@Override
	public boolean isDenied() {
		return _patcherFix.isDenied();
	}

	/**
	* Returns <code>true</code> if this patcher fix is a draft.
	*
	* @return <code>true</code> if this patcher fix is a draft; <code>false</code> otherwise
	*/
	@Override
	public boolean isDraft() {
		return _patcherFix.isDraft();
	}

	/**
	* Returns <code>true</code> if this patcher fix is expired.
	*
	* @return <code>true</code> if this patcher fix is expired; <code>false</code> otherwise
	*/
	@Override
	public boolean isExpired() {
		return _patcherFix.isExpired();
	}

	/**
	* Returns <code>true</code> if this patcher fix is inactive.
	*
	* @return <code>true</code> if this patcher fix is inactive; <code>false</code> otherwise
	*/
	@Override
	public boolean isInactive() {
		return _patcherFix.isInactive();
	}

	/**
	* Returns <code>true</code> if this patcher fix is incomplete.
	*
	* @return <code>true</code> if this patcher fix is incomplete; <code>false</code> otherwise
	*/
	@Override
	public boolean isIncomplete() {
		return _patcherFix.isIncomplete();
	}

	/**
	* Returns <code>true</code> if this patcher fix is pending.
	*
	* @return <code>true</code> if this patcher fix is pending; <code>false</code> otherwise
	*/
	@Override
	public boolean isPending() {
		return _patcherFix.isPending();
	}

	/**
	* Returns <code>true</code> if this patcher fix is scheduled.
	*
	* @return <code>true</code> if this patcher fix is scheduled; <code>false</code> otherwise
	*/
	@Override
	public boolean isScheduled() {
		return _patcherFix.isScheduled();
	}

	@Override
	public boolean isNew() {
		return _patcherFix.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherFix.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherFix.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherFix.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherFix.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherFix.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherFix.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherFix.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherFix.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherFix.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherFix.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherFixWrapper((PatcherFix)_patcherFix.clone());
	}

	@Override
	public int compareTo(com.liferay.osb.patcher.model.PatcherFix patcherFix) {
		return _patcherFix.compareTo(patcherFix);
	}

	@Override
	public int hashCode() {
		return _patcherFix.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherFix> toCacheModel() {
		return _patcherFix.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFix toEscapedModel() {
		return new PatcherFixWrapper(_patcherFix.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFix toUnescapedModel() {
		return new PatcherFixWrapper(_patcherFix.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherFix.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherFix.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFix.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherFixWrapper)) {
			return false;
		}

		PatcherFixWrapper patcherFixWrapper = (PatcherFixWrapper)obj;

		if (Validator.equals(_patcherFix, patcherFixWrapper._patcherFix)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherFix getWrappedPatcherFix() {
		return _patcherFix;
	}

	@Override
	public PatcherFix getWrappedModel() {
		return _patcherFix;
	}

	@Override
	public void resetOriginalValues() {
		_patcherFix.resetOriginalValues();
	}

	private PatcherFix _patcherFix;
}