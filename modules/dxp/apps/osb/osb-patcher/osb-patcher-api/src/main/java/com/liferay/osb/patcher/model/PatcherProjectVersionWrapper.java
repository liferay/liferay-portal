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
 * This class is a wrapper for {@link PatcherProjectVersion}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProjectVersion
 * @generated
 */
public class PatcherProjectVersionWrapper
	extends BaseModelWrapper<PatcherProjectVersion>
	implements ModelWrapper<PatcherProjectVersion>, PatcherProjectVersion {

	public PatcherProjectVersionWrapper(
		PatcherProjectVersion patcherProjectVersion) {

		super(patcherProjectVersion);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put(
			"rootPatcherProjectVersionId", getRootPatcherProjectVersionId());
		attributes.put("combinedBranch", isCombinedBranch());
		attributes.put("committish", getCommittish());
		attributes.put("fixedIssues", getFixedIssues());
		attributes.put("hide", isHide());
		attributes.put("name", getName());
		attributes.put("productVersion", getProductVersion());
		attributes.put("repositoryName", getRepositoryName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

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

		Boolean combinedBranch = (Boolean)attributes.get("combinedBranch");

		if (combinedBranch != null) {
			setCombinedBranch(combinedBranch);
		}

		String committish = (String)attributes.get("committish");

		if (committish != null) {
			setCommittish(committish);
		}

		String fixedIssues = (String)attributes.get("fixedIssues");

		if (fixedIssues != null) {
			setFixedIssues(fixedIssues);
		}

		Boolean hide = (Boolean)attributes.get("hide");

		if (hide != null) {
			setHide(hide);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Integer productVersion = (Integer)attributes.get("productVersion");

		if (productVersion != null) {
			setProductVersion(productVersion);
		}

		String repositoryName = (String)attributes.get("repositoryName");

		if (repositoryName != null) {
			setRepositoryName(repositoryName);
		}
	}

	@Override
	public PatcherProjectVersion cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the combined branch of this patcher project version.
	 *
	 * @return the combined branch of this patcher project version
	 */
	@Override
	public boolean getCombinedBranch() {
		return model.getCombinedBranch();
	}

	/**
	 * Returns the committish of this patcher project version.
	 *
	 * @return the committish of this patcher project version
	 */
	@Override
	public String getCommittish() {
		return model.getCommittish();
	}

	/**
	 * Returns the company ID of this patcher project version.
	 *
	 * @return the company ID of this patcher project version
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this patcher project version.
	 *
	 * @return the create date of this patcher project version
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the fixed issues of this patcher project version.
	 *
	 * @return the fixed issues of this patcher project version
	 */
	@Override
	public String getFixedIssues() {
		return model.getFixedIssues();
	}

	/**
	 * Returns the hide of this patcher project version.
	 *
	 * @return the hide of this patcher project version
	 */
	@Override
	public boolean getHide() {
		return model.getHide();
	}

	/**
	 * Returns the modified date of this patcher project version.
	 *
	 * @return the modified date of this patcher project version
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this patcher project version.
	 *
	 * @return the mvcc version of this patcher project version
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this patcher project version.
	 *
	 * @return the name of this patcher project version
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the patcher product version ID of this patcher project version.
	 *
	 * @return the patcher product version ID of this patcher project version
	 */
	@Override
	public long getPatcherProductVersionId() {
		return model.getPatcherProductVersionId();
	}

	/**
	 * Returns the patcher project version ID of this patcher project version.
	 *
	 * @return the patcher project version ID of this patcher project version
	 */
	@Override
	public long getPatcherProjectVersionId() {
		return model.getPatcherProjectVersionId();
	}

	/**
	 * Returns the primary key of this patcher project version.
	 *
	 * @return the primary key of this patcher project version
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the product version of this patcher project version.
	 *
	 * @return the product version of this patcher project version
	 */
	@Override
	public int getProductVersion() {
		return model.getProductVersion();
	}

	/**
	 * Returns the repository name of this patcher project version.
	 *
	 * @return the repository name of this patcher project version
	 */
	@Override
	public String getRepositoryName() {
		return model.getRepositoryName();
	}

	/**
	 * Returns the root patcher project version ID of this patcher project version.
	 *
	 * @return the root patcher project version ID of this patcher project version
	 */
	@Override
	public long getRootPatcherProjectVersionId() {
		return model.getRootPatcherProjectVersionId();
	}

	/**
	 * Returns the user ID of this patcher project version.
	 *
	 * @return the user ID of this patcher project version
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this patcher project version.
	 *
	 * @return the user name of this patcher project version
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this patcher project version.
	 *
	 * @return the user uuid of this patcher project version
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this patcher project version is combined branch.
	 *
	 * @return <code>true</code> if this patcher project version is combined branch; <code>false</code> otherwise
	 */
	@Override
	public boolean isCombinedBranch() {
		return model.isCombinedBranch();
	}

	/**
	 * Returns <code>true</code> if this patcher project version is hide.
	 *
	 * @return <code>true</code> if this patcher project version is hide; <code>false</code> otherwise
	 */
	@Override
	public boolean isHide() {
		return model.isHide();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets whether this patcher project version is combined branch.
	 *
	 * @param combinedBranch the combined branch of this patcher project version
	 */
	@Override
	public void setCombinedBranch(boolean combinedBranch) {
		model.setCombinedBranch(combinedBranch);
	}

	/**
	 * Sets the committish of this patcher project version.
	 *
	 * @param committish the committish of this patcher project version
	 */
	@Override
	public void setCommittish(String committish) {
		model.setCommittish(committish);
	}

	/**
	 * Sets the company ID of this patcher project version.
	 *
	 * @param companyId the company ID of this patcher project version
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this patcher project version.
	 *
	 * @param createDate the create date of this patcher project version
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the fixed issues of this patcher project version.
	 *
	 * @param fixedIssues the fixed issues of this patcher project version
	 */
	@Override
	public void setFixedIssues(String fixedIssues) {
		model.setFixedIssues(fixedIssues);
	}

	/**
	 * Sets whether this patcher project version is hide.
	 *
	 * @param hide the hide of this patcher project version
	 */
	@Override
	public void setHide(boolean hide) {
		model.setHide(hide);
	}

	/**
	 * Sets the modified date of this patcher project version.
	 *
	 * @param modifiedDate the modified date of this patcher project version
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this patcher project version.
	 *
	 * @param mvccVersion the mvcc version of this patcher project version
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this patcher project version.
	 *
	 * @param name the name of this patcher project version
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the patcher product version ID of this patcher project version.
	 *
	 * @param patcherProductVersionId the patcher product version ID of this patcher project version
	 */
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		model.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	 * Sets the patcher project version ID of this patcher project version.
	 *
	 * @param patcherProjectVersionId the patcher project version ID of this patcher project version
	 */
	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		model.setPatcherProjectVersionId(patcherProjectVersionId);
	}

	/**
	 * Sets the primary key of this patcher project version.
	 *
	 * @param primaryKey the primary key of this patcher project version
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the product version of this patcher project version.
	 *
	 * @param productVersion the product version of this patcher project version
	 */
	@Override
	public void setProductVersion(int productVersion) {
		model.setProductVersion(productVersion);
	}

	/**
	 * Sets the repository name of this patcher project version.
	 *
	 * @param repositoryName the repository name of this patcher project version
	 */
	@Override
	public void setRepositoryName(String repositoryName) {
		model.setRepositoryName(repositoryName);
	}

	/**
	 * Sets the root patcher project version ID of this patcher project version.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID of this patcher project version
	 */
	@Override
	public void setRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		model.setRootPatcherProjectVersionId(rootPatcherProjectVersionId);
	}

	/**
	 * Sets the user ID of this patcher project version.
	 *
	 * @param userId the user ID of this patcher project version
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this patcher project version.
	 *
	 * @param userName the user name of this patcher project version
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this patcher project version.
	 *
	 * @param userUuid the user uuid of this patcher project version
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
	protected PatcherProjectVersionWrapper wrap(
		PatcherProjectVersion patcherProjectVersion) {

		return new PatcherProjectVersionWrapper(patcherProjectVersion);
	}

}