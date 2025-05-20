/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.ModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PatcherAccount}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherAccount
 * @generated
 */
public class PatcherAccountWrapper implements PatcherAccount,
	ModelWrapper<PatcherAccount> {
	public PatcherAccountWrapper(PatcherAccount patcherAccount) {
		_patcherAccount = patcherAccount;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherAccount.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherAccount.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

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

	/**
	* Returns the primary key of this patcher account.
	*
	* @return the primary key of this patcher account
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherAccount.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher account.
	*
	* @param primaryKey the primary key of this patcher account
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherAccount.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher account ID of this patcher account.
	*
	* @return the patcher account ID of this patcher account
	*/
	@Override
	public long getPatcherAccountId() {
		return _patcherAccount.getPatcherAccountId();
	}

	/**
	* Sets the patcher account ID of this patcher account.
	*
	* @param patcherAccountId the patcher account ID of this patcher account
	*/
	@Override
	public void setPatcherAccountId(long patcherAccountId) {
		_patcherAccount.setPatcherAccountId(patcherAccountId);
	}

	/**
	* Returns the company ID of this patcher account.
	*
	* @return the company ID of this patcher account
	*/
	@Override
	public long getCompanyId() {
		return _patcherAccount.getCompanyId();
	}

	/**
	* Sets the company ID of this patcher account.
	*
	* @param companyId the company ID of this patcher account
	*/
	@Override
	public void setCompanyId(long companyId) {
		_patcherAccount.setCompanyId(companyId);
	}

	/**
	* Returns the user ID of this patcher account.
	*
	* @return the user ID of this patcher account
	*/
	@Override
	public long getUserId() {
		return _patcherAccount.getUserId();
	}

	/**
	* Sets the user ID of this patcher account.
	*
	* @param userId the user ID of this patcher account
	*/
	@Override
	public void setUserId(long userId) {
		_patcherAccount.setUserId(userId);
	}

	/**
	* Returns the user uuid of this patcher account.
	*
	* @return the user uuid of this patcher account
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccount.getUserUuid();
	}

	/**
	* Sets the user uuid of this patcher account.
	*
	* @param userUuid the user uuid of this patcher account
	*/
	@Override
	public void setUserUuid(java.lang.String userUuid) {
		_patcherAccount.setUserUuid(userUuid);
	}

	/**
	* Returns the user name of this patcher account.
	*
	* @return the user name of this patcher account
	*/
	@Override
	public java.lang.String getUserName() {
		return _patcherAccount.getUserName();
	}

	/**
	* Sets the user name of this patcher account.
	*
	* @param userName the user name of this patcher account
	*/
	@Override
	public void setUserName(java.lang.String userName) {
		_patcherAccount.setUserName(userName);
	}

	/**
	* Returns the create date of this patcher account.
	*
	* @return the create date of this patcher account
	*/
	@Override
	public java.util.Date getCreateDate() {
		return _patcherAccount.getCreateDate();
	}

	/**
	* Sets the create date of this patcher account.
	*
	* @param createDate the create date of this patcher account
	*/
	@Override
	public void setCreateDate(java.util.Date createDate) {
		_patcherAccount.setCreateDate(createDate);
	}

	/**
	* Returns the modified date of this patcher account.
	*
	* @return the modified date of this patcher account
	*/
	@Override
	public java.util.Date getModifiedDate() {
		return _patcherAccount.getModifiedDate();
	}

	/**
	* Sets the modified date of this patcher account.
	*
	* @param modifiedDate the modified date of this patcher account
	*/
	@Override
	public void setModifiedDate(java.util.Date modifiedDate) {
		_patcherAccount.setModifiedDate(modifiedDate);
	}

	/**
	* Returns the account entry ID of this patcher account.
	*
	* @return the account entry ID of this patcher account
	*/
	@Override
	public long getAccountEntryId() {
		return _patcherAccount.getAccountEntryId();
	}

	/**
	* Sets the account entry ID of this patcher account.
	*
	* @param accountEntryId the account entry ID of this patcher account
	*/
	@Override
	public void setAccountEntryId(long accountEntryId) {
		_patcherAccount.setAccountEntryId(accountEntryId);
	}

	/**
	* Returns the account entry code of this patcher account.
	*
	* @return the account entry code of this patcher account
	*/
	@Override
	public java.lang.String getAccountEntryCode() {
		return _patcherAccount.getAccountEntryCode();
	}

	/**
	* Sets the account entry code of this patcher account.
	*
	* @param accountEntryCode the account entry code of this patcher account
	*/
	@Override
	public void setAccountEntryCode(java.lang.String accountEntryCode) {
		_patcherAccount.setAccountEntryCode(accountEntryCode);
	}

	@Override
	public boolean isNew() {
		return _patcherAccount.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherAccount.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherAccount.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherAccount.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherAccount.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherAccount.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherAccount.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherAccount.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherAccount.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherAccount.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherAccount.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherAccountWrapper((PatcherAccount)_patcherAccount.clone());
	}

	@Override
	public int compareTo(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount) {
		return _patcherAccount.compareTo(patcherAccount);
	}

	@Override
	public int hashCode() {
		return _patcherAccount.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherAccount> toCacheModel() {
		return _patcherAccount.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherAccount toEscapedModel() {
		return new PatcherAccountWrapper(_patcherAccount.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherAccount toUnescapedModel() {
		return new PatcherAccountWrapper(_patcherAccount.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherAccount.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherAccount.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccount.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherAccountWrapper)) {
			return false;
		}

		PatcherAccountWrapper patcherAccountWrapper = (PatcherAccountWrapper)obj;

		if (Validator.equals(_patcherAccount,
					patcherAccountWrapper._patcherAccount)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherAccount getWrappedPatcherAccount() {
		return _patcherAccount;
	}

	@Override
	public PatcherAccount getWrappedModel() {
		return _patcherAccount;
	}

	@Override
	public void resetOriginalValues() {
		_patcherAccount.resetOriginalValues();
	}

	private PatcherAccount _patcherAccount;
}