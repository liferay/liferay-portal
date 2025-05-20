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
 * This class is a wrapper for {@link PatcherFixPack}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixPack
 * @generated
 */
public class PatcherFixPackWrapper implements PatcherFixPack,
	ModelWrapper<PatcherFixPack> {
	public PatcherFixPackWrapper(PatcherFixPack patcherFixPack) {
		_patcherFixPack = patcherFixPack;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherFixPack.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherFixPack.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

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
		attributes.put("version", getVersion());
		attributes.put("releasedDate", getReleasedDate());
		attributes.put("requirements", getRequirements());
		attributes.put("status", getStatus());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
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

		Integer version = (Integer)attributes.get("version");

		if (version != null) {
			setVersion(version);
		}

		Date releasedDate = (Date)attributes.get("releasedDate");

		if (releasedDate != null) {
			setReleasedDate(releasedDate);
		}

		String requirements = (String)attributes.get("requirements");

		if (requirements != null) {
			setRequirements(requirements);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	/**
	* Returns the primary key of this patcher fix pack.
	*
	* @return the primary key of this patcher fix pack
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherFixPack.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher fix pack.
	*
	* @param primaryKey the primary key of this patcher fix pack
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherFixPack.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher fix pack ID of this patcher fix pack.
	*
	* @return the patcher fix pack ID of this patcher fix pack
	*/
	@Override
	public long getPatcherFixPackId() {
		return _patcherFixPack.getPatcherFixPackId();
	}

	/**
	* Sets the patcher fix pack ID of this patcher fix pack.
	*
	* @param patcherFixPackId the patcher fix pack ID of this patcher fix pack
	*/
	@Override
	public void setPatcherFixPackId(long patcherFixPackId) {
		_patcherFixPack.setPatcherFixPackId(patcherFixPackId);
	}

	/**
	* Returns the company ID of this patcher fix pack.
	*
	* @return the company ID of this patcher fix pack
	*/
	@Override
	public long getCompanyId() {
		return _patcherFixPack.getCompanyId();
	}

	/**
	* Sets the company ID of this patcher fix pack.
	*
	* @param companyId the company ID of this patcher fix pack
	*/
	@Override
	public void setCompanyId(long companyId) {
		_patcherFixPack.setCompanyId(companyId);
	}

	/**
	* Returns the user ID of this patcher fix pack.
	*
	* @return the user ID of this patcher fix pack
	*/
	@Override
	public long getUserId() {
		return _patcherFixPack.getUserId();
	}

	/**
	* Sets the user ID of this patcher fix pack.
	*
	* @param userId the user ID of this patcher fix pack
	*/
	@Override
	public void setUserId(long userId) {
		_patcherFixPack.setUserId(userId);
	}

	/**
	* Returns the user uuid of this patcher fix pack.
	*
	* @return the user uuid of this patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPack.getUserUuid();
	}

	/**
	* Sets the user uuid of this patcher fix pack.
	*
	* @param userUuid the user uuid of this patcher fix pack
	*/
	@Override
	public void setUserUuid(java.lang.String userUuid) {
		_patcherFixPack.setUserUuid(userUuid);
	}

	/**
	* Returns the user name of this patcher fix pack.
	*
	* @return the user name of this patcher fix pack
	*/
	@Override
	public java.lang.String getUserName() {
		return _patcherFixPack.getUserName();
	}

	/**
	* Sets the user name of this patcher fix pack.
	*
	* @param userName the user name of this patcher fix pack
	*/
	@Override
	public void setUserName(java.lang.String userName) {
		_patcherFixPack.setUserName(userName);
	}

	/**
	* Returns the create date of this patcher fix pack.
	*
	* @return the create date of this patcher fix pack
	*/
	@Override
	public java.util.Date getCreateDate() {
		return _patcherFixPack.getCreateDate();
	}

	/**
	* Sets the create date of this patcher fix pack.
	*
	* @param createDate the create date of this patcher fix pack
	*/
	@Override
	public void setCreateDate(java.util.Date createDate) {
		_patcherFixPack.setCreateDate(createDate);
	}

	/**
	* Returns the modified date of this patcher fix pack.
	*
	* @return the modified date of this patcher fix pack
	*/
	@Override
	public java.util.Date getModifiedDate() {
		return _patcherFixPack.getModifiedDate();
	}

	/**
	* Sets the modified date of this patcher fix pack.
	*
	* @param modifiedDate the modified date of this patcher fix pack
	*/
	@Override
	public void setModifiedDate(java.util.Date modifiedDate) {
		_patcherFixPack.setModifiedDate(modifiedDate);
	}

	/**
	* Returns the patcher build ID of this patcher fix pack.
	*
	* @return the patcher build ID of this patcher fix pack
	*/
	@Override
	public long getPatcherBuildId() {
		return _patcherFixPack.getPatcherBuildId();
	}

	/**
	* Sets the patcher build ID of this patcher fix pack.
	*
	* @param patcherBuildId the patcher build ID of this patcher fix pack
	*/
	@Override
	public void setPatcherBuildId(long patcherBuildId) {
		_patcherFixPack.setPatcherBuildId(patcherBuildId);
	}

	/**
	* Returns the patcher fix component ID of this patcher fix pack.
	*
	* @return the patcher fix component ID of this patcher fix pack
	*/
	@Override
	public long getPatcherFixComponentId() {
		return _patcherFixPack.getPatcherFixComponentId();
	}

	/**
	* Sets the patcher fix component ID of this patcher fix pack.
	*
	* @param patcherFixComponentId the patcher fix component ID of this patcher fix pack
	*/
	@Override
	public void setPatcherFixComponentId(long patcherFixComponentId) {
		_patcherFixPack.setPatcherFixComponentId(patcherFixComponentId);
	}

	/**
	* Returns the patcher project version ID of this patcher fix pack.
	*
	* @return the patcher project version ID of this patcher fix pack
	*/
	@Override
	public long getPatcherProjectVersionId() {
		return _patcherFixPack.getPatcherProjectVersionId();
	}

	/**
	* Sets the patcher project version ID of this patcher fix pack.
	*
	* @param patcherProjectVersionId the patcher project version ID of this patcher fix pack
	*/
	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherFixPack.setPatcherProjectVersionId(patcherProjectVersionId);
	}

	/**
	* Returns the name of this patcher fix pack.
	*
	* @return the name of this patcher fix pack
	*/
	@Override
	public java.lang.String getName() {
		return _patcherFixPack.getName();
	}

	/**
	* Sets the name of this patcher fix pack.
	*
	* @param name the name of this patcher fix pack
	*/
	@Override
	public void setName(java.lang.String name) {
		_patcherFixPack.setName(name);
	}

	/**
	* Returns the version of this patcher fix pack.
	*
	* @return the version of this patcher fix pack
	*/
	@Override
	public int getVersion() {
		return _patcherFixPack.getVersion();
	}

	/**
	* Sets the version of this patcher fix pack.
	*
	* @param version the version of this patcher fix pack
	*/
	@Override
	public void setVersion(int version) {
		_patcherFixPack.setVersion(version);
	}

	/**
	* Returns the released date of this patcher fix pack.
	*
	* @return the released date of this patcher fix pack
	*/
	@Override
	public java.util.Date getReleasedDate() {
		return _patcherFixPack.getReleasedDate();
	}

	/**
	* Sets the released date of this patcher fix pack.
	*
	* @param releasedDate the released date of this patcher fix pack
	*/
	@Override
	public void setReleasedDate(java.util.Date releasedDate) {
		_patcherFixPack.setReleasedDate(releasedDate);
	}

	/**
	* Returns the requirements of this patcher fix pack.
	*
	* @return the requirements of this patcher fix pack
	*/
	@Override
	public java.lang.String getRequirements() {
		return _patcherFixPack.getRequirements();
	}

	/**
	* Sets the requirements of this patcher fix pack.
	*
	* @param requirements the requirements of this patcher fix pack
	*/
	@Override
	public void setRequirements(java.lang.String requirements) {
		_patcherFixPack.setRequirements(requirements);
	}

	/**
	* Returns the status of this patcher fix pack.
	*
	* @return the status of this patcher fix pack
	*/
	@Override
	public int getStatus() {
		return _patcherFixPack.getStatus();
	}

	/**
	* Sets the status of this patcher fix pack.
	*
	* @param status the status of this patcher fix pack
	*/
	@Override
	public void setStatus(int status) {
		_patcherFixPack.setStatus(status);
	}

	@Override
	public boolean isNew() {
		return _patcherFixPack.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherFixPack.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherFixPack.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherFixPack.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherFixPack.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherFixPack.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherFixPack.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherFixPack.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherFixPack.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherFixPack.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherFixPack.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherFixPackWrapper((PatcherFixPack)_patcherFixPack.clone());
	}

	@Override
	public int compareTo(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack) {
		return _patcherFixPack.compareTo(patcherFixPack);
	}

	@Override
	public int hashCode() {
		return _patcherFixPack.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherFixPack> toCacheModel() {
		return _patcherFixPack.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack toEscapedModel() {
		return new PatcherFixPackWrapper(_patcherFixPack.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack toUnescapedModel() {
		return new PatcherFixPackWrapper(_patcherFixPack.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherFixPack.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherFixPack.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPack.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherFixPackWrapper)) {
			return false;
		}

		PatcherFixPackWrapper patcherFixPackWrapper = (PatcherFixPackWrapper)obj;

		if (Validator.equals(_patcherFixPack,
					patcherFixPackWrapper._patcherFixPack)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherFixPack getWrappedPatcherFixPack() {
		return _patcherFixPack;
	}

	@Override
	public PatcherFixPack getWrappedModel() {
		return _patcherFixPack;
	}

	@Override
	public void resetOriginalValues() {
		_patcherFixPack.resetOriginalValues();
	}

	private PatcherFixPack _patcherFixPack;
}