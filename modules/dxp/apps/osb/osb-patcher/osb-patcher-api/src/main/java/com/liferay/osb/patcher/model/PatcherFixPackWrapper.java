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
 * This class is a wrapper for {@link PatcherFixPack}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixPack
 * @generated
 */
public class PatcherFixPackWrapper
	extends BaseModelWrapper<PatcherFixPack>
	implements ModelWrapper<PatcherFixPack>, PatcherFixPack {

	public PatcherFixPackWrapper(PatcherFixPack patcherFixPack) {
		super(patcherFixPack);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherFixPackId", getPatcherFixPackId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherBuildId", getPatcherBuildId());
		attributes.put("patcherFixComponentId", getPatcherFixComponentId());
		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("name", getName());
		attributes.put("releasedDate", getReleasedDate());
		attributes.put("requirements", getRequirements());
		attributes.put("version", getVersion());
		attributes.put("status", getStatus());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long patcherFixPackId = (Long)attributes.get("patcherFixPackId");

		if (patcherFixPackId != null) {
			setPatcherFixPackId(patcherFixPackId);
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

		Long patcherBuildId = (Long)attributes.get("patcherBuildId");

		if (patcherBuildId != null) {
			setPatcherBuildId(patcherBuildId);
		}

		Long patcherFixComponentId = (Long)attributes.get(
			"patcherFixComponentId");

		if (patcherFixComponentId != null) {
			setPatcherFixComponentId(patcherFixComponentId);
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

		Date releasedDate = (Date)attributes.get("releasedDate");

		if (releasedDate != null) {
			setReleasedDate(releasedDate);
		}

		String requirements = (String)attributes.get("requirements");

		if (requirements != null) {
			setRequirements(requirements);
		}

		Integer version = (Integer)attributes.get("version");

		if (version != null) {
			setVersion(version);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public PatcherFixPack cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this patcher fix pack.
	 *
	 * @return the company ID of this patcher fix pack
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this patcher fix pack.
	 *
	 * @return the create date of this patcher fix pack
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the modified date of this patcher fix pack.
	 *
	 * @return the modified date of this patcher fix pack
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this patcher fix pack.
	 *
	 * @return the mvcc version of this patcher fix pack
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this patcher fix pack.
	 *
	 * @return the name of this patcher fix pack
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the patcher build ID of this patcher fix pack.
	 *
	 * @return the patcher build ID of this patcher fix pack
	 */
	@Override
	public long getPatcherBuildId() {
		return model.getPatcherBuildId();
	}

	/**
	 * Returns the patcher fix component ID of this patcher fix pack.
	 *
	 * @return the patcher fix component ID of this patcher fix pack
	 */
	@Override
	public long getPatcherFixComponentId() {
		return model.getPatcherFixComponentId();
	}

	/**
	 * Returns the patcher fix pack ID of this patcher fix pack.
	 *
	 * @return the patcher fix pack ID of this patcher fix pack
	 */
	@Override
	public long getPatcherFixPackId() {
		return model.getPatcherFixPackId();
	}

	/**
	 * Returns the patcher project version ID of this patcher fix pack.
	 *
	 * @return the patcher project version ID of this patcher fix pack
	 */
	@Override
	public long getPatcherProjectVersionId() {
		return model.getPatcherProjectVersionId();
	}

	/**
	 * Returns the primary key of this patcher fix pack.
	 *
	 * @return the primary key of this patcher fix pack
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the released date of this patcher fix pack.
	 *
	 * @return the released date of this patcher fix pack
	 */
	@Override
	public Date getReleasedDate() {
		return model.getReleasedDate();
	}

	/**
	 * Returns the requirements of this patcher fix pack.
	 *
	 * @return the requirements of this patcher fix pack
	 */
	@Override
	public String getRequirements() {
		return model.getRequirements();
	}

	/**
	 * Returns the status of this patcher fix pack.
	 *
	 * @return the status of this patcher fix pack
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the user ID of this patcher fix pack.
	 *
	 * @return the user ID of this patcher fix pack
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this patcher fix pack.
	 *
	 * @return the user name of this patcher fix pack
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this patcher fix pack.
	 *
	 * @return the user uuid of this patcher fix pack
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the version of this patcher fix pack.
	 *
	 * @return the version of this patcher fix pack
	 */
	@Override
	public int getVersion() {
		return model.getVersion();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this patcher fix pack.
	 *
	 * @param companyId the company ID of this patcher fix pack
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this patcher fix pack.
	 *
	 * @param createDate the create date of this patcher fix pack
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the modified date of this patcher fix pack.
	 *
	 * @param modifiedDate the modified date of this patcher fix pack
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this patcher fix pack.
	 *
	 * @param mvccVersion the mvcc version of this patcher fix pack
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this patcher fix pack.
	 *
	 * @param name the name of this patcher fix pack
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the patcher build ID of this patcher fix pack.
	 *
	 * @param patcherBuildId the patcher build ID of this patcher fix pack
	 */
	@Override
	public void setPatcherBuildId(long patcherBuildId) {
		model.setPatcherBuildId(patcherBuildId);
	}

	/**
	 * Sets the patcher fix component ID of this patcher fix pack.
	 *
	 * @param patcherFixComponentId the patcher fix component ID of this patcher fix pack
	 */
	@Override
	public void setPatcherFixComponentId(long patcherFixComponentId) {
		model.setPatcherFixComponentId(patcherFixComponentId);
	}

	/**
	 * Sets the patcher fix pack ID of this patcher fix pack.
	 *
	 * @param patcherFixPackId the patcher fix pack ID of this patcher fix pack
	 */
	@Override
	public void setPatcherFixPackId(long patcherFixPackId) {
		model.setPatcherFixPackId(patcherFixPackId);
	}

	/**
	 * Sets the patcher project version ID of this patcher fix pack.
	 *
	 * @param patcherProjectVersionId the patcher project version ID of this patcher fix pack
	 */
	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		model.setPatcherProjectVersionId(patcherProjectVersionId);
	}

	/**
	 * Sets the primary key of this patcher fix pack.
	 *
	 * @param primaryKey the primary key of this patcher fix pack
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the released date of this patcher fix pack.
	 *
	 * @param releasedDate the released date of this patcher fix pack
	 */
	@Override
	public void setReleasedDate(Date releasedDate) {
		model.setReleasedDate(releasedDate);
	}

	/**
	 * Sets the requirements of this patcher fix pack.
	 *
	 * @param requirements the requirements of this patcher fix pack
	 */
	@Override
	public void setRequirements(String requirements) {
		model.setRequirements(requirements);
	}

	/**
	 * Sets the status of this patcher fix pack.
	 *
	 * @param status the status of this patcher fix pack
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the user ID of this patcher fix pack.
	 *
	 * @param userId the user ID of this patcher fix pack
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this patcher fix pack.
	 *
	 * @param userName the user name of this patcher fix pack
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this patcher fix pack.
	 *
	 * @param userUuid the user uuid of this patcher fix pack
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the version of this patcher fix pack.
	 *
	 * @param version the version of this patcher fix pack
	 */
	@Override
	public void setVersion(int version) {
		model.setVersion(version);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected PatcherFixPackWrapper wrap(PatcherFixPack patcherFixPack) {
		return new PatcherFixPackWrapper(patcherFixPack);
	}

}