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
 * This class is a wrapper for {@link PatcherTicketHint}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherTicketHint
 * @generated
 */
public class PatcherTicketHintWrapper implements PatcherTicketHint,
	ModelWrapper<PatcherTicketHint> {
	public PatcherTicketHintWrapper(PatcherTicketHint patcherTicketHint) {
		_patcherTicketHint = patcherTicketHint;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherTicketHint.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherTicketHint.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

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

	/**
	* Returns the primary key of this patcher ticket hint.
	*
	* @return the primary key of this patcher ticket hint
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherTicketHint.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher ticket hint.
	*
	* @param primaryKey the primary key of this patcher ticket hint
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherTicketHint.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher ticket hint ID of this patcher ticket hint.
	*
	* @return the patcher ticket hint ID of this patcher ticket hint
	*/
	@Override
	public long getPatcherTicketHintId() {
		return _patcherTicketHint.getPatcherTicketHintId();
	}

	/**
	* Sets the patcher ticket hint ID of this patcher ticket hint.
	*
	* @param patcherTicketHintId the patcher ticket hint ID of this patcher ticket hint
	*/
	@Override
	public void setPatcherTicketHintId(long patcherTicketHintId) {
		_patcherTicketHint.setPatcherTicketHintId(patcherTicketHintId);
	}

	/**
	* Returns the company ID of this patcher ticket hint.
	*
	* @return the company ID of this patcher ticket hint
	*/
	@Override
	public long getCompanyId() {
		return _patcherTicketHint.getCompanyId();
	}

	/**
	* Sets the company ID of this patcher ticket hint.
	*
	* @param companyId the company ID of this patcher ticket hint
	*/
	@Override
	public void setCompanyId(long companyId) {
		_patcherTicketHint.setCompanyId(companyId);
	}

	/**
	* Returns the user ID of this patcher ticket hint.
	*
	* @return the user ID of this patcher ticket hint
	*/
	@Override
	public long getUserId() {
		return _patcherTicketHint.getUserId();
	}

	/**
	* Sets the user ID of this patcher ticket hint.
	*
	* @param userId the user ID of this patcher ticket hint
	*/
	@Override
	public void setUserId(long userId) {
		_patcherTicketHint.setUserId(userId);
	}

	/**
	* Returns the user uuid of this patcher ticket hint.
	*
	* @return the user uuid of this patcher ticket hint
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHint.getUserUuid();
	}

	/**
	* Sets the user uuid of this patcher ticket hint.
	*
	* @param userUuid the user uuid of this patcher ticket hint
	*/
	@Override
	public void setUserUuid(java.lang.String userUuid) {
		_patcherTicketHint.setUserUuid(userUuid);
	}

	/**
	* Returns the user name of this patcher ticket hint.
	*
	* @return the user name of this patcher ticket hint
	*/
	@Override
	public java.lang.String getUserName() {
		return _patcherTicketHint.getUserName();
	}

	/**
	* Sets the user name of this patcher ticket hint.
	*
	* @param userName the user name of this patcher ticket hint
	*/
	@Override
	public void setUserName(java.lang.String userName) {
		_patcherTicketHint.setUserName(userName);
	}

	/**
	* Returns the create date of this patcher ticket hint.
	*
	* @return the create date of this patcher ticket hint
	*/
	@Override
	public java.util.Date getCreateDate() {
		return _patcherTicketHint.getCreateDate();
	}

	/**
	* Sets the create date of this patcher ticket hint.
	*
	* @param createDate the create date of this patcher ticket hint
	*/
	@Override
	public void setCreateDate(java.util.Date createDate) {
		_patcherTicketHint.setCreateDate(createDate);
	}

	/**
	* Returns the modified date of this patcher ticket hint.
	*
	* @return the modified date of this patcher ticket hint
	*/
	@Override
	public java.util.Date getModifiedDate() {
		return _patcherTicketHint.getModifiedDate();
	}

	/**
	* Sets the modified date of this patcher ticket hint.
	*
	* @param modifiedDate the modified date of this patcher ticket hint
	*/
	@Override
	public void setModifiedDate(java.util.Date modifiedDate) {
		_patcherTicketHint.setModifiedDate(modifiedDate);
	}

	/**
	* Returns the patcher product version ID of this patcher ticket hint.
	*
	* @return the patcher product version ID of this patcher ticket hint
	*/
	@Override
	public long getPatcherProductVersionId() {
		return _patcherTicketHint.getPatcherProductVersionId();
	}

	/**
	* Sets the patcher product version ID of this patcher ticket hint.
	*
	* @param patcherProductVersionId the patcher product version ID of this patcher ticket hint
	*/
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherTicketHint.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	* Returns the script of this patcher ticket hint.
	*
	* @return the script of this patcher ticket hint
	*/
	@Override
	public java.lang.String getScript() {
		return _patcherTicketHint.getScript();
	}

	/**
	* Sets the script of this patcher ticket hint.
	*
	* @param script the script of this patcher ticket hint
	*/
	@Override
	public void setScript(java.lang.String script) {
		_patcherTicketHint.setScript(script);
	}

	@Override
	public boolean isNew() {
		return _patcherTicketHint.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherTicketHint.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherTicketHint.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherTicketHint.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherTicketHint.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherTicketHint.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherTicketHint.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherTicketHint.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherTicketHint.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherTicketHint.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherTicketHint.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherTicketHintWrapper((PatcherTicketHint)_patcherTicketHint.clone());
	}

	@Override
	public int compareTo(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint) {
		return _patcherTicketHint.compareTo(patcherTicketHint);
	}

	@Override
	public int hashCode() {
		return _patcherTicketHint.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherTicketHint> toCacheModel() {
		return _patcherTicketHint.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint toEscapedModel() {
		return new PatcherTicketHintWrapper(_patcherTicketHint.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint toUnescapedModel() {
		return new PatcherTicketHintWrapper(_patcherTicketHint.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherTicketHint.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherTicketHint.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherTicketHint.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherTicketHintWrapper)) {
			return false;
		}

		PatcherTicketHintWrapper patcherTicketHintWrapper = (PatcherTicketHintWrapper)obj;

		if (Validator.equals(_patcherTicketHint,
					patcherTicketHintWrapper._patcherTicketHint)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherTicketHint getWrappedPatcherTicketHint() {
		return _patcherTicketHint;
	}

	@Override
	public PatcherTicketHint getWrappedModel() {
		return _patcherTicketHint;
	}

	@Override
	public void resetOriginalValues() {
		_patcherTicketHint.resetOriginalValues();
	}

	private PatcherTicketHint _patcherTicketHint;
}