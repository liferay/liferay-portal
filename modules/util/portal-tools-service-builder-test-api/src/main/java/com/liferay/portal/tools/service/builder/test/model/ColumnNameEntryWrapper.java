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
 * This class is a wrapper for {@link ColumnNameEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ColumnNameEntry
 * @generated
 */
public class ColumnNameEntryWrapper
	extends BaseModelWrapper<ColumnNameEntry>
	implements ColumnNameEntry, ModelWrapper<ColumnNameEntry> {

	public ColumnNameEntryWrapper(ColumnNameEntry columnNameEntry) {
		super(columnNameEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("columnNameEntryId", getColumnNameEntryId());
		attributes.put("name", getName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long columnNameEntryId = (Long)attributes.get("columnNameEntryId");

		if (columnNameEntryId != null) {
			setColumnNameEntryId(columnNameEntryId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public ColumnNameEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the column name entry ID of this column name entry.
	 *
	 * @return the column name entry ID of this column name entry
	 */
	@Override
	public long getColumnNameEntryId() {
		return model.getColumnNameEntryId();
	}

	/**
	 * Returns the name of this column name entry.
	 *
	 * @return the name of this column name entry
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this column name entry.
	 *
	 * @return the primary key of this column name entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Sets the column name entry ID of this column name entry.
	 *
	 * @param columnNameEntryId the column name entry ID of this column name entry
	 */
	@Override
	public void setColumnNameEntryId(long columnNameEntryId) {
		model.setColumnNameEntryId(columnNameEntryId);
	}

	/**
	 * Sets the name of this column name entry.
	 *
	 * @param name the name of this column name entry
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this column name entry.
	 *
	 * @param primaryKey the primary key of this column name entry
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
	protected ColumnNameEntryWrapper wrap(ColumnNameEntry columnNameEntry) {
		return new ColumnNameEntryWrapper(columnNameEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-151264039