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
 * This class is a wrapper for {@link PatcherAccount}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherAccount
 * @generated
 */
public class PatcherAccountWrapper
	extends BaseModelWrapper<PatcherAccount>
	implements ModelWrapper<PatcherAccount>, PatcherAccount {

	public PatcherAccountWrapper(PatcherAccount patcherAccount) {
		super(patcherAccount);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherAccountId", getPatcherAccountId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("accountEntryId", getAccountEntryId());
		attributes.put("accountEntryCode", getAccountEntryCode());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long patcherAccountId = (Long)attributes.get("patcherAccountId");

		if (patcherAccountId != null) {
			setPatcherAccountId(patcherAccountId);
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

		Long accountEntryId = (Long)attributes.get("accountEntryId");

		if (accountEntryId != null) {
			setAccountEntryId(accountEntryId);
		}

		String accountEntryCode = (String)attributes.get("accountEntryCode");

		if (accountEntryCode != null) {
			setAccountEntryCode(accountEntryCode);
		}
	}

	@Override
	public PatcherAccount cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the account entry code of this patcher account.
	 *
	 * @return the account entry code of this patcher account
	 */
	@Override
	public String getAccountEntryCode() {
		return model.getAccountEntryCode();
	}

	/**
	 * Returns the account entry ID of this patcher account.
	 *
	 * @return the account entry ID of this patcher account
	 */
	@Override
	public long getAccountEntryId() {
		return model.getAccountEntryId();
	}

	/**
	 * Returns the company ID of this patcher account.
	 *
	 * @return the company ID of this patcher account
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this patcher account.
	 *
	 * @return the create date of this patcher account
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the modified date of this patcher account.
	 *
	 * @return the modified date of this patcher account
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this patcher account.
	 *
	 * @return the mvcc version of this patcher account
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the patcher account ID of this patcher account.
	 *
	 * @return the patcher account ID of this patcher account
	 */
	@Override
	public long getPatcherAccountId() {
		return model.getPatcherAccountId();
	}

	/**
	 * Returns the primary key of this patcher account.
	 *
	 * @return the primary key of this patcher account
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this patcher account.
	 *
	 * @return the user ID of this patcher account
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this patcher account.
	 *
	 * @return the user name of this patcher account
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this patcher account.
	 *
	 * @return the user uuid of this patcher account
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
	 * Sets the account entry code of this patcher account.
	 *
	 * @param accountEntryCode the account entry code of this patcher account
	 */
	@Override
	public void setAccountEntryCode(String accountEntryCode) {
		model.setAccountEntryCode(accountEntryCode);
	}

	/**
	 * Sets the account entry ID of this patcher account.
	 *
	 * @param accountEntryId the account entry ID of this patcher account
	 */
	@Override
	public void setAccountEntryId(long accountEntryId) {
		model.setAccountEntryId(accountEntryId);
	}

	/**
	 * Sets the company ID of this patcher account.
	 *
	 * @param companyId the company ID of this patcher account
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this patcher account.
	 *
	 * @param createDate the create date of this patcher account
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the modified date of this patcher account.
	 *
	 * @param modifiedDate the modified date of this patcher account
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this patcher account.
	 *
	 * @param mvccVersion the mvcc version of this patcher account
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the patcher account ID of this patcher account.
	 *
	 * @param patcherAccountId the patcher account ID of this patcher account
	 */
	@Override
	public void setPatcherAccountId(long patcherAccountId) {
		model.setPatcherAccountId(patcherAccountId);
	}

	/**
	 * Sets the primary key of this patcher account.
	 *
	 * @param primaryKey the primary key of this patcher account
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this patcher account.
	 *
	 * @param userId the user ID of this patcher account
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this patcher account.
	 *
	 * @param userName the user name of this patcher account
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this patcher account.
	 *
	 * @param userUuid the user uuid of this patcher account
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
	protected PatcherAccountWrapper wrap(PatcherAccount patcherAccount) {
		return new PatcherAccountWrapper(patcherAccount);
	}

}