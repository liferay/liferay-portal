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
		attributes.put("companyId", getCompanyId());
		attributes.put("snapshotDate", getSnapshotDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long dateEntryId = (Long)attributes.get("dateEntryId");

		if (dateEntryId != null) {
			setDateEntryId(dateEntryId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Date snapshotDate = (Date)attributes.get("snapshotDate");

		if (snapshotDate != null) {
			setSnapshotDate(snapshotDate);
		}
	}

	@Override
	public DateEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this date entry.
	 *
	 * @return the company ID of this date entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
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
	 * Returns the snapshot date of this date entry.
	 *
	 * @return the snapshot date of this date entry
	 */
	@Override
	public Date getSnapshotDate() {
		return model.getSnapshotDate();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this date entry.
	 *
	 * @param companyId the company ID of this date entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
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
	 * Sets the snapshot date of this date entry.
	 *
	 * @param snapshotDate the snapshot date of this date entry
	 */
	@Override
	public void setSnapshotDate(Date snapshotDate) {
		model.setSnapshotDate(snapshotDate);
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
// LIFERAY-SERVICE-BUILDER-HASH:642209834