/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link DateEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DateEntry
 * @generated
 */
public class DateEntryWrapper
	extends BaseModelWrapper<DateEntry>
	implements DateEntry, ModelWrapper<DateEntry> {

	public DateEntryWrapper(DateEntry dateEntry) {
		super(dateEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("dateEntryId", getDateEntryId());
		attributes.put("value", getValue());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long dateEntryId = (Long)attributes.get("dateEntryId");

		if (dateEntryId != null) {
			setDateEntryId(dateEntryId);
		}

		Date value = (Date)attributes.get("value");

		if (value != null) {
			setValue(value);
		}
	}

	@Override
	public DateEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the date entry ID of this date entry.
	 *
	 * @return the date entry ID of this date entry
	 */
	@Override
	public long getDateEntryId() {
		return model.getDateEntryId();
	}

	/**
	 * Returns the primary key of this date entry.
	 *
	 * @return the primary key of this date entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the value of this date entry.
	 *
	 * @return the value of this date entry
	 */
	@Override
	public Date getValue() {
		return model.getValue();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the date entry ID of this date entry.
	 *
	 * @param dateEntryId the date entry ID of this date entry
	 */
	@Override
	public void setDateEntryId(long dateEntryId) {
		model.setDateEntryId(dateEntryId);
	}

	/**
	 * Sets the primary key of this date entry.
	 *
	 * @param primaryKey the primary key of this date entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the value of this date entry.
	 *
	 * @param value the value of this date entry
	 */
	@Override
	public void setValue(Date value) {
		model.setValue(value);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected DateEntryWrapper wrap(DateEntry dateEntry) {
		return new DateEntryWrapper(dateEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1311406074