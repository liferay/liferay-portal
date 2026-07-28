/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing DateEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DateEntryCacheModel
	implements CacheModel<DateEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DateEntryCacheModel)) {
			return false;
		}

		DateEntryCacheModel dateEntryCacheModel = (DateEntryCacheModel)object;

		if (dateEntryId == dateEntryCacheModel.dateEntryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, dateEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{dateEntryId=");
		sb.append(dateEntryId);
		sb.append(", value=");
		sb.append(value);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DateEntry toEntityModel() {
		DateEntryImpl dateEntryImpl = new DateEntryImpl();

		dateEntryImpl.setDateEntryId(dateEntryId);

		if (value == Long.MIN_VALUE) {
			dateEntryImpl.setValue(null);
		}
		else {
			dateEntryImpl.setValue(new Date(value));
		}

		dateEntryImpl.resetOriginalValues();

		return dateEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		dateEntryId = objectInput.readLong();
		value = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(dateEntryId);
		objectOutput.writeLong(value);
	}

	public long dateEntryId;
	public long value;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2050526301