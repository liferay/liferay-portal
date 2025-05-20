/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.ModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PatcherFixRel}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixRel
 * @generated
 */
public class PatcherFixRelWrapper implements PatcherFixRel,
	ModelWrapper<PatcherFixRel> {
	public PatcherFixRelWrapper(PatcherFixRel patcherFixRel) {
		_patcherFixRel = patcherFixRel;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherFixRel.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherFixRel.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherFixRelId", getPatcherFixRelId());
		attributes.put("childPatcherFixId", getChildPatcherFixId());
		attributes.put("parentPatcherFixId", getParentPatcherFixId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherFixRelId = (Long)attributes.get("patcherFixRelId");

		if (patcherFixRelId != null) {
			setPatcherFixRelId(patcherFixRelId);
		}

		Long childPatcherFixId = (Long)attributes.get("childPatcherFixId");

		if (childPatcherFixId != null) {
			setChildPatcherFixId(childPatcherFixId);
		}

		Long parentPatcherFixId = (Long)attributes.get("parentPatcherFixId");

		if (parentPatcherFixId != null) {
			setParentPatcherFixId(parentPatcherFixId);
		}
	}

	/**
	* Returns the primary key of this patcher fix rel.
	*
	* @return the primary key of this patcher fix rel
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherFixRel.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher fix rel.
	*
	* @param primaryKey the primary key of this patcher fix rel
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherFixRel.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher fix rel ID of this patcher fix rel.
	*
	* @return the patcher fix rel ID of this patcher fix rel
	*/
	@Override
	public long getPatcherFixRelId() {
		return _patcherFixRel.getPatcherFixRelId();
	}

	/**
	* Sets the patcher fix rel ID of this patcher fix rel.
	*
	* @param patcherFixRelId the patcher fix rel ID of this patcher fix rel
	*/
	@Override
	public void setPatcherFixRelId(long patcherFixRelId) {
		_patcherFixRel.setPatcherFixRelId(patcherFixRelId);
	}

	/**
	* Returns the child patcher fix ID of this patcher fix rel.
	*
	* @return the child patcher fix ID of this patcher fix rel
	*/
	@Override
	public long getChildPatcherFixId() {
		return _patcherFixRel.getChildPatcherFixId();
	}

	/**
	* Sets the child patcher fix ID of this patcher fix rel.
	*
	* @param childPatcherFixId the child patcher fix ID of this patcher fix rel
	*/
	@Override
	public void setChildPatcherFixId(long childPatcherFixId) {
		_patcherFixRel.setChildPatcherFixId(childPatcherFixId);
	}

	/**
	* Returns the parent patcher fix ID of this patcher fix rel.
	*
	* @return the parent patcher fix ID of this patcher fix rel
	*/
	@Override
	public long getParentPatcherFixId() {
		return _patcherFixRel.getParentPatcherFixId();
	}

	/**
	* Sets the parent patcher fix ID of this patcher fix rel.
	*
	* @param parentPatcherFixId the parent patcher fix ID of this patcher fix rel
	*/
	@Override
	public void setParentPatcherFixId(long parentPatcherFixId) {
		_patcherFixRel.setParentPatcherFixId(parentPatcherFixId);
	}

	@Override
	public boolean isNew() {
		return _patcherFixRel.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherFixRel.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherFixRel.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherFixRel.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherFixRel.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherFixRel.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherFixRel.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherFixRel.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherFixRel.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherFixRel.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherFixRel.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherFixRelWrapper((PatcherFixRel)_patcherFixRel.clone());
	}

	@Override
	public int compareTo(
		com.liferay.osb.patcher.model.PatcherFixRel patcherFixRel) {
		return _patcherFixRel.compareTo(patcherFixRel);
	}

	@Override
	public int hashCode() {
		return _patcherFixRel.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherFixRel> toCacheModel() {
		return _patcherFixRel.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixRel toEscapedModel() {
		return new PatcherFixRelWrapper(_patcherFixRel.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixRel toUnescapedModel() {
		return new PatcherFixRelWrapper(_patcherFixRel.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherFixRel.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherFixRel.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixRel.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherFixRelWrapper)) {
			return false;
		}

		PatcherFixRelWrapper patcherFixRelWrapper = (PatcherFixRelWrapper)obj;

		if (Validator.equals(_patcherFixRel, patcherFixRelWrapper._patcherFixRel)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherFixRel getWrappedPatcherFixRel() {
		return _patcherFixRel;
	}

	@Override
	public PatcherFixRel getWrappedModel() {
		return _patcherFixRel;
	}

	@Override
	public void resetOriginalValues() {
		_patcherFixRel.resetOriginalValues();
	}

	private PatcherFixRel _patcherFixRel;
}