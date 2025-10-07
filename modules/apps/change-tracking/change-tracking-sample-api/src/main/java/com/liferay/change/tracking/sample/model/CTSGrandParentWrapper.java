/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CTSGrandParent}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTSGrandParent
 * @generated
 */
public class CTSGrandParentWrapper
	extends BaseModelWrapper<CTSGrandParent>
	implements CTSGrandParent, ModelWrapper<CTSGrandParent> {

	public CTSGrandParentWrapper(CTSGrandParent ctsGrandParent) {
		super(ctsGrandParent);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctsGrandParentId", getCtsGrandParentId());
		attributes.put("companyId", getCompanyId());
		attributes.put("parentCTSGrandParentId", getParentCTSGrandParentId());
		attributes.put("name", getName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctsGrandParentId = (Long)attributes.get("ctsGrandParentId");

		if (ctsGrandParentId != null) {
			setCtsGrandParentId(ctsGrandParentId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long parentCTSGrandParentId = (Long)attributes.get(
			"parentCTSGrandParentId");

		if (parentCTSGrandParentId != null) {
			setParentCTSGrandParentId(parentCTSGrandParentId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public CTSGrandParent cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this cts grand parent.
	 *
	 * @return the company ID of this cts grand parent
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the cts grand parent ID of this cts grand parent.
	 *
	 * @return the cts grand parent ID of this cts grand parent
	 */
	@Override
	public long getCtsGrandParentId() {
		return model.getCtsGrandParentId();
	}

	/**
	 * Returns the mvcc version of this cts grand parent.
	 *
	 * @return the mvcc version of this cts grand parent
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this cts grand parent.
	 *
	 * @return the name of this cts grand parent
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the parent cts grand parent ID of this cts grand parent.
	 *
	 * @return the parent cts grand parent ID of this cts grand parent
	 */
	@Override
	public long getParentCTSGrandParentId() {
		return model.getParentCTSGrandParentId();
	}

	/**
	 * Returns the primary key of this cts grand parent.
	 *
	 * @return the primary key of this cts grand parent
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
	 * Sets the company ID of this cts grand parent.
	 *
	 * @param companyId the company ID of this cts grand parent
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cts grand parent ID of this cts grand parent.
	 *
	 * @param ctsGrandParentId the cts grand parent ID of this cts grand parent
	 */
	@Override
	public void setCtsGrandParentId(long ctsGrandParentId) {
		model.setCtsGrandParentId(ctsGrandParentId);
	}

	/**
	 * Sets the mvcc version of this cts grand parent.
	 *
	 * @param mvccVersion the mvcc version of this cts grand parent
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this cts grand parent.
	 *
	 * @param name the name of this cts grand parent
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the parent cts grand parent ID of this cts grand parent.
	 *
	 * @param parentCTSGrandParentId the parent cts grand parent ID of this cts grand parent
	 */
	@Override
	public void setParentCTSGrandParentId(long parentCTSGrandParentId) {
		model.setParentCTSGrandParentId(parentCTSGrandParentId);
	}

	/**
	 * Sets the primary key of this cts grand parent.
	 *
	 * @param primaryKey the primary key of this cts grand parent
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
	protected CTSGrandParentWrapper wrap(CTSGrandParent ctsGrandParent) {
		return new CTSGrandParentWrapper(ctsGrandParent);
	}

}