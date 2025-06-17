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
 * This class is a wrapper for {@link PatcherFix}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFix
 * @generated
 */
public class PatcherFixWrapper
	extends BaseModelWrapper<PatcherFix>
	implements ModelWrapper<PatcherFix>, PatcherFix {

	public PatcherFixWrapper(PatcherFix patcherFix) {
		super(patcherFix);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherFixId", getPatcherFixId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("comments", getComments());
		attributes.put("committish", getCommittish());
		attributes.put("dependencies", getDependencies());
		attributes.put("fixPackStatus", getFixPackStatus());
		attributes.put("gitHash", getGitHash());
		attributes.put("gitRemoteURL", getGitRemoteURL());
		attributes.put("jenkinsResults", getJenkinsResults());
		attributes.put("key", getKey());
		attributes.put("keyVersion", getKeyVersion());
		attributes.put("latestFix", isLatestFix());
		attributes.put("name", getName());
		attributes.put("notified", isNotified());
		attributes.put("obsolete", isObsolete());
		attributes.put("productVersion", getProductVersion());
		attributes.put("requestKey", getRequestKey());
		attributes.put("requirements", getRequirements());
		attributes.put("type", getType());
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

		String comments = (String)attributes.get("comments");

		if (comments != null) {
			setComments(comments);
		}

		String committish = (String)attributes.get("committish");

		if (committish != null) {
			setCommittish(committish);
		}

		String dependencies = (String)attributes.get("dependencies");

		if (dependencies != null) {
			setDependencies(dependencies);
		}

		Integer fixPackStatus = (Integer)attributes.get("fixPackStatus");

		if (fixPackStatus != null) {
			setFixPackStatus(fixPackStatus);
		}

		String gitHash = (String)attributes.get("gitHash");

		if (gitHash != null) {
			setGitHash(gitHash);
		}

		String gitRemoteURL = (String)attributes.get("gitRemoteURL");

		if (gitRemoteURL != null) {
			setGitRemoteURL(gitRemoteURL);
		}

		String jenkinsResults = (String)attributes.get("jenkinsResults");

		if (jenkinsResults != null) {
			setJenkinsResults(jenkinsResults);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		Double keyVersion = (Double)attributes.get("keyVersion");

		if (keyVersion != null) {
			setKeyVersion(keyVersion);
		}

		Boolean latestFix = (Boolean)attributes.get("latestFix");

		if (latestFix != null) {
			setLatestFix(latestFix);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Boolean notified = (Boolean)attributes.get("notified");

		if (notified != null) {
			setNotified(notified);
		}

		Boolean obsolete = (Boolean)attributes.get("obsolete");

		if (obsolete != null) {
			setObsolete(obsolete);
		}

		Integer productVersion = (Integer)attributes.get("productVersion");

		if (productVersion != null) {
			setProductVersion(productVersion);
		}

		String requestKey = (String)attributes.get("requestKey");

		if (requestKey != null) {
			setRequestKey(requestKey);
		}

		String requirements = (String)attributes.get("requirements");

		if (requirements != null) {
			setRequirements(requirements);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
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
	public PatcherFix cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the comments of this patcher fix.
	 *
	 * @return the comments of this patcher fix
	 */
	@Override
	public String getComments() {
		return model.getComments();
	}

	/**
	 * Returns the committish of this patcher fix.
	 *
	 * @return the committish of this patcher fix
	 */
	@Override
	public String getCommittish() {
		return model.getCommittish();
	}

	/**
	 * Returns the company ID of this patcher fix.
	 *
	 * @return the company ID of this patcher fix
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this patcher fix.
	 *
	 * @return the create date of this patcher fix
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the dependencies of this patcher fix.
	 *
	 * @return the dependencies of this patcher fix
	 */
	@Override
	public String getDependencies() {
		return model.getDependencies();
	}

	/**
	 * Returns the fix pack status of this patcher fix.
	 *
	 * @return the fix pack status of this patcher fix
	 */
	@Override
	public int getFixPackStatus() {
		return model.getFixPackStatus();
	}

	/**
	 * Returns the git hash of this patcher fix.
	 *
	 * @return the git hash of this patcher fix
	 */
	@Override
	public String getGitHash() {
		return model.getGitHash();
	}

	/**
	 * Returns the git remote url of this patcher fix.
	 *
	 * @return the git remote url of this patcher fix
	 */
	@Override
	public String getGitRemoteURL() {
		return model.getGitRemoteURL();
	}

	/**
	 * Returns the jenkins results of this patcher fix.
	 *
	 * @return the jenkins results of this patcher fix
	 */
	@Override
	public String getJenkinsResults() {
		return model.getJenkinsResults();
	}

	/**
	 * Returns the key of this patcher fix.
	 *
	 * @return the key of this patcher fix
	 */
	@Override
	public String getKey() {
		return model.getKey();
	}

	/**
	 * Returns the key version of this patcher fix.
	 *
	 * @return the key version of this patcher fix
	 */
	@Override
	public double getKeyVersion() {
		return model.getKeyVersion();
	}

	/**
	 * Returns the latest fix of this patcher fix.
	 *
	 * @return the latest fix of this patcher fix
	 */
	@Override
	public boolean getLatestFix() {
		return model.getLatestFix();
	}

	/**
	 * Returns the modified date of this patcher fix.
	 *
	 * @return the modified date of this patcher fix
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this patcher fix.
	 *
	 * @return the mvcc version of this patcher fix
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this patcher fix.
	 *
	 * @return the name of this patcher fix
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the notified of this patcher fix.
	 *
	 * @return the notified of this patcher fix
	 */
	@Override
	public boolean getNotified() {
		return model.getNotified();
	}

	/**
	 * Returns the obsolete of this patcher fix.
	 *
	 * @return the obsolete of this patcher fix
	 */
	@Override
	public boolean getObsolete() {
		return model.getObsolete();
	}

	/**
	 * Returns the patcher fix ID of this patcher fix.
	 *
	 * @return the patcher fix ID of this patcher fix
	 */
	@Override
	public long getPatcherFixId() {
		return model.getPatcherFixId();
	}

	/**
	 * Returns the patcher product version ID of this patcher fix.
	 *
	 * @return the patcher product version ID of this patcher fix
	 */
	@Override
	public long getPatcherProductVersionId() {
		return model.getPatcherProductVersionId();
	}

	/**
	 * Returns the patcher project version ID of this patcher fix.
	 *
	 * @return the patcher project version ID of this patcher fix
	 */
	@Override
	public long getPatcherProjectVersionId() {
		return model.getPatcherProjectVersionId();
	}

	/**
	 * Returns the primary key of this patcher fix.
	 *
	 * @return the primary key of this patcher fix
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the product version of this patcher fix.
	 *
	 * @return the product version of this patcher fix
	 */
	@Override
	public int getProductVersion() {
		return model.getProductVersion();
	}

	/**
	 * Returns the request key of this patcher fix.
	 *
	 * @return the request key of this patcher fix
	 */
	@Override
	public String getRequestKey() {
		return model.getRequestKey();
	}

	/**
	 * Returns the requirements of this patcher fix.
	 *
	 * @return the requirements of this patcher fix
	 */
	@Override
	public String getRequirements() {
		return model.getRequirements();
	}

	/**
	 * Returns the status of this patcher fix.
	 *
	 * @return the status of this patcher fix
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this patcher fix.
	 *
	 * @return the status by user ID of this patcher fix
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this patcher fix.
	 *
	 * @return the status by user name of this patcher fix
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this patcher fix.
	 *
	 * @return the status by user uuid of this patcher fix
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this patcher fix.
	 *
	 * @return the status date of this patcher fix
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the type of this patcher fix.
	 *
	 * @return the type of this patcher fix
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this patcher fix.
	 *
	 * @return the user ID of this patcher fix
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this patcher fix.
	 *
	 * @return the user name of this patcher fix
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this patcher fix.
	 *
	 * @return the user uuid of this patcher fix
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is approved.
	 *
	 * @return <code>true</code> if this patcher fix is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is denied.
	 *
	 * @return <code>true</code> if this patcher fix is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is a draft.
	 *
	 * @return <code>true</code> if this patcher fix is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is expired.
	 *
	 * @return <code>true</code> if this patcher fix is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is inactive.
	 *
	 * @return <code>true</code> if this patcher fix is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is incomplete.
	 *
	 * @return <code>true</code> if this patcher fix is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is latest fix.
	 *
	 * @return <code>true</code> if this patcher fix is latest fix; <code>false</code> otherwise
	 */
	@Override
	public boolean isLatestFix() {
		return model.isLatestFix();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is notified.
	 *
	 * @return <code>true</code> if this patcher fix is notified; <code>false</code> otherwise
	 */
	@Override
	public boolean isNotified() {
		return model.isNotified();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is obsolete.
	 *
	 * @return <code>true</code> if this patcher fix is obsolete; <code>false</code> otherwise
	 */
	@Override
	public boolean isObsolete() {
		return model.isObsolete();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is pending.
	 *
	 * @return <code>true</code> if this patcher fix is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this patcher fix is scheduled.
	 *
	 * @return <code>true</code> if this patcher fix is scheduled; <code>false</code> otherwise
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
	 * Sets the comments of this patcher fix.
	 *
	 * @param comments the comments of this patcher fix
	 */
	@Override
	public void setComments(String comments) {
		model.setComments(comments);
	}

	/**
	 * Sets the committish of this patcher fix.
	 *
	 * @param committish the committish of this patcher fix
	 */
	@Override
	public void setCommittish(String committish) {
		model.setCommittish(committish);
	}

	/**
	 * Sets the company ID of this patcher fix.
	 *
	 * @param companyId the company ID of this patcher fix
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this patcher fix.
	 *
	 * @param createDate the create date of this patcher fix
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the dependencies of this patcher fix.
	 *
	 * @param dependencies the dependencies of this patcher fix
	 */
	@Override
	public void setDependencies(String dependencies) {
		model.setDependencies(dependencies);
	}

	/**
	 * Sets the fix pack status of this patcher fix.
	 *
	 * @param fixPackStatus the fix pack status of this patcher fix
	 */
	@Override
	public void setFixPackStatus(int fixPackStatus) {
		model.setFixPackStatus(fixPackStatus);
	}

	/**
	 * Sets the git hash of this patcher fix.
	 *
	 * @param gitHash the git hash of this patcher fix
	 */
	@Override
	public void setGitHash(String gitHash) {
		model.setGitHash(gitHash);
	}

	/**
	 * Sets the git remote url of this patcher fix.
	 *
	 * @param gitRemoteURL the git remote url of this patcher fix
	 */
	@Override
	public void setGitRemoteURL(String gitRemoteURL) {
		model.setGitRemoteURL(gitRemoteURL);
	}

	/**
	 * Sets the jenkins results of this patcher fix.
	 *
	 * @param jenkinsResults the jenkins results of this patcher fix
	 */
	@Override
	public void setJenkinsResults(String jenkinsResults) {
		model.setJenkinsResults(jenkinsResults);
	}

	/**
	 * Sets the key of this patcher fix.
	 *
	 * @param key the key of this patcher fix
	 */
	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	/**
	 * Sets the key version of this patcher fix.
	 *
	 * @param keyVersion the key version of this patcher fix
	 */
	@Override
	public void setKeyVersion(double keyVersion) {
		model.setKeyVersion(keyVersion);
	}

	/**
	 * Sets whether this patcher fix is latest fix.
	 *
	 * @param latestFix the latest fix of this patcher fix
	 */
	@Override
	public void setLatestFix(boolean latestFix) {
		model.setLatestFix(latestFix);
	}

	/**
	 * Sets the modified date of this patcher fix.
	 *
	 * @param modifiedDate the modified date of this patcher fix
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this patcher fix.
	 *
	 * @param mvccVersion the mvcc version of this patcher fix
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this patcher fix.
	 *
	 * @param name the name of this patcher fix
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets whether this patcher fix is notified.
	 *
	 * @param notified the notified of this patcher fix
	 */
	@Override
	public void setNotified(boolean notified) {
		model.setNotified(notified);
	}

	/**
	 * Sets whether this patcher fix is obsolete.
	 *
	 * @param obsolete the obsolete of this patcher fix
	 */
	@Override
	public void setObsolete(boolean obsolete) {
		model.setObsolete(obsolete);
	}

	/**
	 * Sets the patcher fix ID of this patcher fix.
	 *
	 * @param patcherFixId the patcher fix ID of this patcher fix
	 */
	@Override
	public void setPatcherFixId(long patcherFixId) {
		model.setPatcherFixId(patcherFixId);
	}

	/**
	 * Sets the patcher product version ID of this patcher fix.
	 *
	 * @param patcherProductVersionId the patcher product version ID of this patcher fix
	 */
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		model.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	 * Sets the patcher project version ID of this patcher fix.
	 *
	 * @param patcherProjectVersionId the patcher project version ID of this patcher fix
	 */
	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		model.setPatcherProjectVersionId(patcherProjectVersionId);
	}

	/**
	 * Sets the primary key of this patcher fix.
	 *
	 * @param primaryKey the primary key of this patcher fix
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the product version of this patcher fix.
	 *
	 * @param productVersion the product version of this patcher fix
	 */
	@Override
	public void setProductVersion(int productVersion) {
		model.setProductVersion(productVersion);
	}

	/**
	 * Sets the request key of this patcher fix.
	 *
	 * @param requestKey the request key of this patcher fix
	 */
	@Override
	public void setRequestKey(String requestKey) {
		model.setRequestKey(requestKey);
	}

	/**
	 * Sets the requirements of this patcher fix.
	 *
	 * @param requirements the requirements of this patcher fix
	 */
	@Override
	public void setRequirements(String requirements) {
		model.setRequirements(requirements);
	}

	/**
	 * Sets the status of this patcher fix.
	 *
	 * @param status the status of this patcher fix
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this patcher fix.
	 *
	 * @param statusByUserId the status by user ID of this patcher fix
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this patcher fix.
	 *
	 * @param statusByUserName the status by user name of this patcher fix
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this patcher fix.
	 *
	 * @param statusByUserUuid the status by user uuid of this patcher fix
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this patcher fix.
	 *
	 * @param statusDate the status date of this patcher fix
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the type of this patcher fix.
	 *
	 * @param type the type of this patcher fix
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this patcher fix.
	 *
	 * @param userId the user ID of this patcher fix
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this patcher fix.
	 *
	 * @param userName the user name of this patcher fix
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this patcher fix.
	 *
	 * @param userUuid the user uuid of this patcher fix
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
	protected PatcherFixWrapper wrap(PatcherFix patcherFix) {
		return new PatcherFixWrapper(patcherFix);
	}

}