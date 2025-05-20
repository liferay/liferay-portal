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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PatcherBuildRel}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherBuildRel
 * @generated
 */
public class PatcherBuildRelWrapper implements PatcherBuildRel,
	ModelWrapper<PatcherBuildRel> {
	public PatcherBuildRelWrapper(PatcherBuildRel patcherBuildRel) {
		_patcherBuildRel = patcherBuildRel;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherBuildRel.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherBuildRel.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherBuildRelId", getPatcherBuildRelId());
		attributes.put("childPatcherBuildId", getChildPatcherBuildId());
		attributes.put("parentPatcherBuildId", getParentPatcherBuildId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherBuildRelId = (Long)attributes.get("patcherBuildRelId");

		if (patcherBuildRelId != null) {
			setPatcherBuildRelId(patcherBuildRelId);
		}

		Long childPatcherBuildId = (Long)attributes.get("childPatcherBuildId");

		if (childPatcherBuildId != null) {
			setChildPatcherBuildId(childPatcherBuildId);
		}

		Long parentPatcherBuildId = (Long)attributes.get("parentPatcherBuildId");

		if (parentPatcherBuildId != null) {
			setParentPatcherBuildId(parentPatcherBuildId);
		}
	}

	/**
	* Returns the primary key of this patcher build rel.
	*
	* @return the primary key of this patcher build rel
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherBuildRel.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher build rel.
	*
	* @param primaryKey the primary key of this patcher build rel
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherBuildRel.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher build rel ID of this patcher build rel.
	*
	* @return the patcher build rel ID of this patcher build rel
	*/
	@Override
	public long getPatcherBuildRelId() {
		return _patcherBuildRel.getPatcherBuildRelId();
	}

	/**
	* Sets the patcher build rel ID of this patcher build rel.
	*
	* @param patcherBuildRelId the patcher build rel ID of this patcher build rel
	*/
	@Override
	public void setPatcherBuildRelId(long patcherBuildRelId) {
		_patcherBuildRel.setPatcherBuildRelId(patcherBuildRelId);
	}

	/**
	* Returns the child patcher build ID of this patcher build rel.
	*
	* @return the child patcher build ID of this patcher build rel
	*/
	@Override
	public long getChildPatcherBuildId() {
		return _patcherBuildRel.getChildPatcherBuildId();
	}

	/**
	* Sets the child patcher build ID of this patcher build rel.
	*
	* @param childPatcherBuildId the child patcher build ID of this patcher build rel
	*/
	@Override
	public void setChildPatcherBuildId(long childPatcherBuildId) {
		_patcherBuildRel.setChildPatcherBuildId(childPatcherBuildId);
	}

	/**
	* Returns the parent patcher build ID of this patcher build rel.
	*
	* @return the parent patcher build ID of this patcher build rel
	*/
	@Override
	public long getParentPatcherBuildId() {
		return _patcherBuildRel.getParentPatcherBuildId();
	}

	/**
	* Sets the parent patcher build ID of this patcher build rel.
	*
	* @param parentPatcherBuildId the parent patcher build ID of this patcher build rel
	*/
	@Override
	public void setParentPatcherBuildId(long parentPatcherBuildId) {
		_patcherBuildRel.setParentPatcherBuildId(parentPatcherBuildId);
	}

	@Override
	public boolean isNew() {
		return _patcherBuildRel.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherBuildRel.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherBuildRel.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherBuildRel.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherBuildRel.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherBuildRel.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherBuildRel.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherBuildRel.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherBuildRel.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherBuildRel.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherBuildRel.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherBuildRelWrapper((PatcherBuildRel)_patcherBuildRel.clone());
	}

	@Override
	public int compareTo(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel) {
		return _patcherBuildRel.compareTo(patcherBuildRel);
	}

	@Override
	public int hashCode() {
		return _patcherBuildRel.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherBuildRel> toCacheModel() {
		return _patcherBuildRel.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel toEscapedModel() {
		return new PatcherBuildRelWrapper(_patcherBuildRel.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel toUnescapedModel() {
		return new PatcherBuildRelWrapper(_patcherBuildRel.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherBuildRel.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherBuildRel.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildRel.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherBuildRelWrapper)) {
			return false;
		}

		PatcherBuildRelWrapper patcherBuildRelWrapper = (PatcherBuildRelWrapper)obj;

		if (Validator.equals(_patcherBuildRel,
					patcherBuildRelWrapper._patcherBuildRel)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherBuildRel getWrappedPatcherBuildRel() {
		return _patcherBuildRel;
	}

	@Override
	public PatcherBuildRel getWrappedModel() {
		return _patcherBuildRel;
	}

	@Override
	public void resetOriginalValues() {
		_patcherBuildRel.resetOriginalValues();
	}

	private PatcherBuildRel _patcherBuildRel;
}