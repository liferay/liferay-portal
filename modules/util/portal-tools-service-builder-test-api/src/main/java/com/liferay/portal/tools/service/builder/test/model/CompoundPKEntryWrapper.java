/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CompoundPKEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CompoundPKEntry
 * @generated
 */
public class CompoundPKEntryWrapper
	extends BaseModelWrapper<CompoundPKEntry>
	implements CompoundPKEntry, ModelWrapper<CompoundPKEntry> {

	public CompoundPKEntryWrapper(CompoundPKEntry compoundPKEntry) {
		super(compoundPKEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("companyId", getCompanyId());
		attributes.put("classNameId", getClassNameId());
		attributes.put("name", getName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public CompoundPKEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the fully qualified class name of this compound pk entry.
	 *
	 * @return the fully qualified class name of this compound pk entry
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this compound pk entry.
	 *
	 * @return the class name ID of this compound pk entry
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the company ID of this compound pk entry.
	 *
	 * @return the company ID of this compound pk entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the name of this compound pk entry.
	 *
	 * @return the name of this compound pk entry
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this compound pk entry.
	 *
	 * @return the primary key of this compound pk entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.service.persistence.
		CompoundPKEntryPK getPrimaryKey() {

		return model.getPrimaryKey();
	}

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this compound pk entry.
	 *
	 * @param classNameId the class name ID of this compound pk entry
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the company ID of this compound pk entry.
	 *
	 * @param companyId the company ID of this compound pk entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the name of this compound pk entry.
	 *
	 * @param name the name of this compound pk entry
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this compound pk entry.
	 *
	 * @param primaryKey the primary key of this compound pk entry
	 */
	@Override
	public void setPrimaryKey(
		com.liferay.portal.tools.service.builder.test.service.persistence.
			CompoundPKEntryPK primaryKey) {

		model.setPrimaryKey(primaryKey);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected CompoundPKEntryWrapper wrap(CompoundPKEntry compoundPKEntry) {
		return new CompoundPKEntryWrapper(compoundPKEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2121672181