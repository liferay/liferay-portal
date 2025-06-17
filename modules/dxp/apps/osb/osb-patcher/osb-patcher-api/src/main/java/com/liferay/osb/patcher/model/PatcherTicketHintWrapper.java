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
 * This class is a wrapper for {@link PatcherTicketHint}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherTicketHint
 * @generated
 */
public class PatcherTicketHintWrapper
	extends BaseModelWrapper<PatcherTicketHint>
	implements ModelWrapper<PatcherTicketHint>, PatcherTicketHint {

	public PatcherTicketHintWrapper(PatcherTicketHint patcherTicketHint) {
		super(patcherTicketHint);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherTicketHintId", getPatcherTicketHintId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("script", getScript());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long patcherTicketHintId = (Long)attributes.get("patcherTicketHintId");

		if (patcherTicketHintId != null) {
			setPatcherTicketHintId(patcherTicketHintId);
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

		String script = (String)attributes.get("script");

		if (script != null) {
			setScript(script);
		}
	}

	@Override
	public PatcherTicketHint cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this patcher ticket hint.
	 *
	 * @return the company ID of this patcher ticket hint
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this patcher ticket hint.
	 *
	 * @return the create date of this patcher ticket hint
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the modified date of this patcher ticket hint.
	 *
	 * @return the modified date of this patcher ticket hint
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this patcher ticket hint.
	 *
	 * @return the mvcc version of this patcher ticket hint
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the patcher product version ID of this patcher ticket hint.
	 *
	 * @return the patcher product version ID of this patcher ticket hint
	 */
	@Override
	public long getPatcherProductVersionId() {
		return model.getPatcherProductVersionId();
	}

	/**
	 * Returns the patcher ticket hint ID of this patcher ticket hint.
	 *
	 * @return the patcher ticket hint ID of this patcher ticket hint
	 */
	@Override
	public long getPatcherTicketHintId() {
		return model.getPatcherTicketHintId();
	}

	/**
	 * Returns the primary key of this patcher ticket hint.
	 *
	 * @return the primary key of this patcher ticket hint
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the script of this patcher ticket hint.
	 *
	 * @return the script of this patcher ticket hint
	 */
	@Override
	public String getScript() {
		return model.getScript();
	}

	/**
	 * Returns the user ID of this patcher ticket hint.
	 *
	 * @return the user ID of this patcher ticket hint
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this patcher ticket hint.
	 *
	 * @return the user name of this patcher ticket hint
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this patcher ticket hint.
	 *
	 * @return the user uuid of this patcher ticket hint
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
	 * Sets the company ID of this patcher ticket hint.
	 *
	 * @param companyId the company ID of this patcher ticket hint
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this patcher ticket hint.
	 *
	 * @param createDate the create date of this patcher ticket hint
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the modified date of this patcher ticket hint.
	 *
	 * @param modifiedDate the modified date of this patcher ticket hint
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this patcher ticket hint.
	 *
	 * @param mvccVersion the mvcc version of this patcher ticket hint
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the patcher product version ID of this patcher ticket hint.
	 *
	 * @param patcherProductVersionId the patcher product version ID of this patcher ticket hint
	 */
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		model.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	 * Sets the patcher ticket hint ID of this patcher ticket hint.
	 *
	 * @param patcherTicketHintId the patcher ticket hint ID of this patcher ticket hint
	 */
	@Override
	public void setPatcherTicketHintId(long patcherTicketHintId) {
		model.setPatcherTicketHintId(patcherTicketHintId);
	}

	/**
	 * Sets the primary key of this patcher ticket hint.
	 *
	 * @param primaryKey the primary key of this patcher ticket hint
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the script of this patcher ticket hint.
	 *
	 * @param script the script of this patcher ticket hint
	 */
	@Override
	public void setScript(String script) {
		model.setScript(script);
	}

	/**
	 * Sets the user ID of this patcher ticket hint.
	 *
	 * @param userId the user ID of this patcher ticket hint
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this patcher ticket hint.
	 *
	 * @param userName the user name of this patcher ticket hint
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this patcher ticket hint.
	 *
	 * @param userUuid the user uuid of this patcher ticket hint
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
	protected PatcherTicketHintWrapper wrap(
		PatcherTicketHint patcherTicketHint) {

		return new PatcherTicketHintWrapper(patcherTicketHint);
	}

}