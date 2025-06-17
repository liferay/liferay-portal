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

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherFixRelId", getPatcherFixRelId());
		attributes.put("companyId", getCompanyId());
		attributes.put("childPatcherFixId", getChildPatcherFixId());
		attributes.put("parentPatcherFixId", getParentPatcherFixId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long patcherFixRelId = (Long)attributes.get("patcherFixRelId");

		if (patcherFixRelId != null) {
			setPatcherFixRelId(patcherFixRelId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
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
	 * Returns the company ID of this patcher fix rel.
	 *
	 * @return the company ID of this patcher fix rel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the mvcc version of this patcher fix rel.
	 *
	 * @return the mvcc version of this patcher fix rel
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
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
	 * Sets the company ID of this patcher fix rel.
	 *
	 * @param companyId the company ID of this patcher fix rel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the mvcc version of this patcher fix rel.
	 *
	 * @param mvccVersion the mvcc version of this patcher fix rel
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
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