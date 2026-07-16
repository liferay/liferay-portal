/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryStatusEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing DSLQueryStatusEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DSLQueryStatusEntryCacheModel
	implements CacheModel<DSLQueryStatusEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DSLQueryStatusEntryCacheModel)) {
			return false;
		}

		DSLQueryStatusEntryCacheModel dslQueryStatusEntryCacheModel =
			(DSLQueryStatusEntryCacheModel)object;

		if (dslQueryStatusEntryId ==
				dslQueryStatusEntryCacheModel.dslQueryStatusEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, dslQueryStatusEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(11);

		sb.append("{dslQueryStatusEntryId=");
		sb.append(dslQueryStatusEntryId);
		sb.append(", dslQueryEntryId=");
		sb.append(dslQueryEntryId);
		sb.append(", weight=");
		sb.append(weight);
		sb.append(", status=");
		sb.append(status);
		sb.append(", statusDate=");
		sb.append(statusDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DSLQueryStatusEntry toEntityModel() {
		DSLQueryStatusEntryImpl dslQueryStatusEntryImpl =
			new DSLQueryStatusEntryImpl();

		dslQueryStatusEntryImpl.setDslQueryStatusEntryId(dslQueryStatusEntryId);
		dslQueryStatusEntryImpl.setDslQueryEntryId(dslQueryEntryId);
		dslQueryStatusEntryImpl.setWeight(weight);

		if (status == null) {
			dslQueryStatusEntryImpl.setStatus("");
		}
		else {
			dslQueryStatusEntryImpl.setStatus(status);
		}

		if (statusDate == Long.MIN_VALUE) {
			dslQueryStatusEntryImpl.setStatusDate(null);
		}
		else {
			dslQueryStatusEntryImpl.setStatusDate(new Date(statusDate));
		}

		dslQueryStatusEntryImpl.resetOriginalValues();

		return dslQueryStatusEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		dslQueryStatusEntryId = objectInput.readLong();

		dslQueryEntryId = objectInput.readLong();

		weight = objectInput.readDouble();
		status = objectInput.readUTF();
		statusDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(dslQueryStatusEntryId);

		objectOutput.writeLong(dslQueryEntryId);

		objectOutput.writeDouble(weight);

		if (status == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(status);
		}

		objectOutput.writeLong(statusDate);
	}

	public long dslQueryStatusEntryId;
	public long dslQueryEntryId;
	public double weight;
	public String status;
	public long statusDate;

}
// LIFERAY-SERVICE-BUILDER-HASH:323440120