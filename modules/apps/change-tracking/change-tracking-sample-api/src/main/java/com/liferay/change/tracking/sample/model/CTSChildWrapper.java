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
 * This class is a wrapper for {@link CTSChild}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTSChild
 * @generated
 */
public class CTSChildWrapper
	extends BaseModelWrapper<CTSChild>
	implements CTSChild, ModelWrapper<CTSChild> {

	public CTSChildWrapper(CTSChild ctsChild) {
		super(ctsChild);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("ctsChildId", getCtsChildId());
		attributes.put("companyId", getCompanyId());
		attributes.put("ctsGrandParentId", getCtsGrandParentId());
		attributes.put("parentCTSChildId", getParentCTSChildId());
		attributes.put("ctsParentName", getCtsParentName());
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

		Long ctsChildId = (Long)attributes.get("ctsChildId");

		if (ctsChildId != null) {
			setCtsChildId(ctsChildId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long ctsGrandParentId = (Long)attributes.get("ctsGrandParentId");

		if (ctsGrandParentId != null) {
			setCtsGrandParentId(ctsGrandParentId);
		}

		Long parentCTSChildId = (Long)attributes.get("parentCTSChildId");

		if (parentCTSChildId != null) {
			setParentCTSChildId(parentCTSChildId);
		}

		String ctsParentName = (String)attributes.get("ctsParentName");

		if (ctsParentName != null) {
			setCtsParentName(ctsParentName);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public CTSChild cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this cts child.
	 *
	 * @return the company ID of this cts child
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the ct collection ID of this cts child.
	 *
	 * @return the ct collection ID of this cts child
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the cts child ID of this cts child.
	 *
	 * @return the cts child ID of this cts child
	 */
	@Override
	public long getCtsChildId() {
		return model.getCtsChildId();
	}

	/**
	 * Returns the cts grand parent ID of this cts child.
	 *
	 * @return the cts grand parent ID of this cts child
	 */
	@Override
	public long getCtsGrandParentId() {
		return model.getCtsGrandParentId();
	}

	/**
	 * Returns the cts parent name of this cts child.
	 *
	 * @return the cts parent name of this cts child
	 */
	@Override
	public String getCtsParentName() {
		return model.getCtsParentName();
	}

	/**
	 * Returns the mvcc version of this cts child.
	 *
	 * @return the mvcc version of this cts child
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this cts child.
	 *
	 * @return the name of this cts child
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the parent cts child ID of this cts child.
	 *
	 * @return the parent cts child ID of this cts child
	 */
	@Override
	public long getParentCTSChildId() {
		return model.getParentCTSChildId();
	}

	/**
	 * Returns the primary key of this cts child.
	 *
	 * @return the primary key of this cts child
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
	 * Sets the company ID of this cts child.
	 *
	 * @param companyId the company ID of this cts child
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the ct collection ID of this cts child.
	 *
	 * @param ctCollectionId the ct collection ID of this cts child
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the cts child ID of this cts child.
	 *
	 * @param ctsChildId the cts child ID of this cts child
	 */
	@Override
	public void setCtsChildId(long ctsChildId) {
		model.setCtsChildId(ctsChildId);
	}

	/**
	 * Sets the cts grand parent ID of this cts child.
	 *
	 * @param ctsGrandParentId the cts grand parent ID of this cts child
	 */
	@Override
	public void setCtsGrandParentId(long ctsGrandParentId) {
		model.setCtsGrandParentId(ctsGrandParentId);
	}

	/**
	 * Sets the cts parent name of this cts child.
	 *
	 * @param ctsParentName the cts parent name of this cts child
	 */
	@Override
	public void setCtsParentName(String ctsParentName) {
		model.setCtsParentName(ctsParentName);
	}

	/**
	 * Sets the mvcc version of this cts child.
	 *
	 * @param mvccVersion the mvcc version of this cts child
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this cts child.
	 *
	 * @param name the name of this cts child
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the parent cts child ID of this cts child.
	 *
	 * @param parentCTSChildId the parent cts child ID of this cts child
	 */
	@Override
	public void setParentCTSChildId(long parentCTSChildId) {
		model.setParentCTSChildId(parentCTSChildId);
	}

	/**
	 * Sets the primary key of this cts child.
	 *
	 * @param primaryKey the primary key of this cts child
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
	public Map<String, Function<CTSChild, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CTSChild, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected CTSChildWrapper wrap(CTSChild ctsChild) {
		return new CTSChildWrapper(ctsChild);
	}

}