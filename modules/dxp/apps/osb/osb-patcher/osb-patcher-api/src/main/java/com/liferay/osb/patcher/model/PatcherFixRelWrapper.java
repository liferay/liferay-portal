/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PatcherFixRel}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixRel
 * @generated
 */
public class PatcherFixRelWrapper
	extends BaseModelWrapper<PatcherFixRel>
	implements ModelWrapper<PatcherFixRel>, PatcherFixRel {

	public PatcherFixRelWrapper(PatcherFixRel patcherFixRel) {
		super(patcherFixRel);
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

	@Override
	public PatcherFixRel cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the child patcher fix ID of this patcher fix rel.
	 *
	 * @return the child patcher fix ID of this patcher fix rel
	 */
	@Override
	public long getChildPatcherFixId() {
		return model.getChildPatcherFixId();
	}

	/**
	 * Returns the parent patcher fix ID of this patcher fix rel.
	 *
	 * @return the parent patcher fix ID of this patcher fix rel
	 */
	@Override
	public long getParentPatcherFixId() {
		return model.getParentPatcherFixId();
	}

	/**
	 * Returns the patcher fix rel ID of this patcher fix rel.
	 *
	 * @return the patcher fix rel ID of this patcher fix rel
	 */
	@Override
	public long getPatcherFixRelId() {
		return model.getPatcherFixRelId();
	}

	/**
	 * Returns the primary key of this patcher fix rel.
	 *
	 * @return the primary key of this patcher fix rel
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the child patcher fix ID of this patcher fix rel.
	 *
	 * @param childPatcherFixId the child patcher fix ID of this patcher fix rel
	 */
	@Override
	public void setChildPatcherFixId(long childPatcherFixId) {
		model.setChildPatcherFixId(childPatcherFixId);
	}

	/**
	 * Sets the parent patcher fix ID of this patcher fix rel.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID of this patcher fix rel
	 */
	@Override
	public void setParentPatcherFixId(long parentPatcherFixId) {
		model.setParentPatcherFixId(parentPatcherFixId);
	}

	/**
	 * Sets the patcher fix rel ID of this patcher fix rel.
	 *
	 * @param patcherFixRelId the patcher fix rel ID of this patcher fix rel
	 */
	@Override
	public void setPatcherFixRelId(long patcherFixRelId) {
		model.setPatcherFixRelId(patcherFixRelId);
	}

	/**
	 * Sets the primary key of this patcher fix rel.
	 *
	 * @param primaryKey the primary key of this patcher fix rel
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected PatcherFixRelWrapper wrap(PatcherFixRel patcherFixRel) {
		return new PatcherFixRelWrapper(patcherFixRel);
	}

}