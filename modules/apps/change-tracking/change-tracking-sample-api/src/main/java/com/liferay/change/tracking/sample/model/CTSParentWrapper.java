/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link CTSParent}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTSParent
 * @generated
 */
public class CTSParentWrapper
	extends BaseModelWrapper<CTSParent>
	implements CTSParent, ModelWrapper<CTSParent> {

	public CTSParentWrapper(CTSParent ctsParent) {
		super(ctsParent);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("ctsParentId", getCtsParentId());
		attributes.put("companyId", getCompanyId());
		attributes.put("ctsGrandParentId", getCtsGrandParentId());
		attributes.put("name", getName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		Long ctsParentId = (Long)attributes.get("ctsParentId");

		if (ctsParentId != null) {
			setCtsParentId(ctsParentId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long ctsGrandParentId = (Long)attributes.get("ctsGrandParentId");

		if (ctsGrandParentId != null) {
			setCtsGrandParentId(ctsGrandParentId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public CTSParent cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this cts parent.
	 *
	 * @return the company ID of this cts parent
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the ct collection ID of this cts parent.
	 *
	 * @return the ct collection ID of this cts parent
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the cts grand parent ID of this cts parent.
	 *
	 * @return the cts grand parent ID of this cts parent
	 */
	@Override
	public long getCtsGrandParentId() {
		return model.getCtsGrandParentId();
	}

	/**
	 * Returns the cts parent ID of this cts parent.
	 *
	 * @return the cts parent ID of this cts parent
	 */
	@Override
	public long getCtsParentId() {
		return model.getCtsParentId();
	}

	/**
	 * Returns the mvcc version of this cts parent.
	 *
	 * @return the mvcc version of this cts parent
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this cts parent.
	 *
	 * @return the name of this cts parent
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this cts parent.
	 *
	 * @return the primary key of this cts parent
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
	 * Sets the company ID of this cts parent.
	 *
	 * @param companyId the company ID of this cts parent
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the ct collection ID of this cts parent.
	 *
	 * @param ctCollectionId the ct collection ID of this cts parent
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the cts grand parent ID of this cts parent.
	 *
	 * @param ctsGrandParentId the cts grand parent ID of this cts parent
	 */
	@Override
	public void setCtsGrandParentId(long ctsGrandParentId) {
		model.setCtsGrandParentId(ctsGrandParentId);
	}

	/**
	 * Sets the cts parent ID of this cts parent.
	 *
	 * @param ctsParentId the cts parent ID of this cts parent
	 */
	@Override
	public void setCtsParentId(long ctsParentId) {
		model.setCtsParentId(ctsParentId);
	}

	/**
	 * Sets the mvcc version of this cts parent.
	 *
	 * @param mvccVersion the mvcc version of this cts parent
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this cts parent.
	 *
	 * @param name the name of this cts parent
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this cts parent.
	 *
	 * @param primaryKey the primary key of this cts parent
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
	public Map<String, Function<CTSParent, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CTSParent, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected CTSParentWrapper wrap(CTSParent ctsParent) {
		return new CTSParentWrapper(ctsParent);
	}

}