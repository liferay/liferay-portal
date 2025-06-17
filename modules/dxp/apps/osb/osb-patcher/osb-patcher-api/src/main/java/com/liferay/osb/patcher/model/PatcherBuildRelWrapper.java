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
 * This class is a wrapper for {@link PatcherBuildRel}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildRel
 * @generated
 */
public class PatcherBuildRelWrapper
	extends BaseModelWrapper<PatcherBuildRel>
	implements ModelWrapper<PatcherBuildRel>, PatcherBuildRel {

	public PatcherBuildRelWrapper(PatcherBuildRel patcherBuildRel) {
		super(patcherBuildRel);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherBuildRelId", getPatcherBuildRelId());
		attributes.put("companyId", getCompanyId());
		attributes.put("childPatcherBuildId", getChildPatcherBuildId());
		attributes.put("parentPatcherBuildId", getParentPatcherBuildId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long patcherBuildRelId = (Long)attributes.get("patcherBuildRelId");

		if (patcherBuildRelId != null) {
			setPatcherBuildRelId(patcherBuildRelId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long childPatcherBuildId = (Long)attributes.get("childPatcherBuildId");

		if (childPatcherBuildId != null) {
			setChildPatcherBuildId(childPatcherBuildId);
		}

		Long parentPatcherBuildId = (Long)attributes.get(
			"parentPatcherBuildId");

		if (parentPatcherBuildId != null) {
			setParentPatcherBuildId(parentPatcherBuildId);
		}
	}

	@Override
	public PatcherBuildRel cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the child patcher build ID of this patcher build rel.
	 *
	 * @return the child patcher build ID of this patcher build rel
	 */
	@Override
	public long getChildPatcherBuildId() {
		return model.getChildPatcherBuildId();
	}

	/**
	 * Returns the company ID of this patcher build rel.
	 *
	 * @return the company ID of this patcher build rel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the mvcc version of this patcher build rel.
	 *
	 * @return the mvcc version of this patcher build rel
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the parent patcher build ID of this patcher build rel.
	 *
	 * @return the parent patcher build ID of this patcher build rel
	 */
	@Override
	public long getParentPatcherBuildId() {
		return model.getParentPatcherBuildId();
	}

	/**
	 * Returns the patcher build rel ID of this patcher build rel.
	 *
	 * @return the patcher build rel ID of this patcher build rel
	 */
	@Override
	public long getPatcherBuildRelId() {
		return model.getPatcherBuildRelId();
	}

	/**
	 * Returns the primary key of this patcher build rel.
	 *
	 * @return the primary key of this patcher build rel
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
	 * Sets the child patcher build ID of this patcher build rel.
	 *
	 * @param childPatcherBuildId the child patcher build ID of this patcher build rel
	 */
	@Override
	public void setChildPatcherBuildId(long childPatcherBuildId) {
		model.setChildPatcherBuildId(childPatcherBuildId);
	}

	/**
	 * Sets the company ID of this patcher build rel.
	 *
	 * @param companyId the company ID of this patcher build rel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the mvcc version of this patcher build rel.
	 *
	 * @param mvccVersion the mvcc version of this patcher build rel
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the parent patcher build ID of this patcher build rel.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID of this patcher build rel
	 */
	@Override
	public void setParentPatcherBuildId(long parentPatcherBuildId) {
		model.setParentPatcherBuildId(parentPatcherBuildId);
	}

	/**
	 * Sets the patcher build rel ID of this patcher build rel.
	 *
	 * @param patcherBuildRelId the patcher build rel ID of this patcher build rel
	 */
	@Override
	public void setPatcherBuildRelId(long patcherBuildRelId) {
		model.setPatcherBuildRelId(patcherBuildRelId);
	}

	/**
	 * Sets the primary key of this patcher build rel.
	 *
	 * @param primaryKey the primary key of this patcher build rel
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
	protected PatcherBuildRelWrapper wrap(PatcherBuildRel patcherBuildRel) {
		return new PatcherBuildRelWrapper(patcherBuildRel);
	}

}