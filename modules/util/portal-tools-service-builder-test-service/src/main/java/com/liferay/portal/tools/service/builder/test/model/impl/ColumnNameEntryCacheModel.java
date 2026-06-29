/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.ColumnNameEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing ColumnNameEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ColumnNameEntryCacheModel
	implements CacheModel<ColumnNameEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ColumnNameEntryCacheModel)) {
			return false;
		}

		ColumnNameEntryCacheModel columnNameEntryCacheModel =
			(ColumnNameEntryCacheModel)object;

		if (columnNameEntryId == columnNameEntryCacheModel.columnNameEntryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, columnNameEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{columnNameEntryId=");
		sb.append(columnNameEntryId);
		sb.append(", name=");
		sb.append(name);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ColumnNameEntry toEntityModel() {
		ColumnNameEntryImpl columnNameEntryImpl = new ColumnNameEntryImpl();

		columnNameEntryImpl.setColumnNameEntryId(columnNameEntryId);

		if (name == null) {
			columnNameEntryImpl.setName("");
		}
		else {
			columnNameEntryImpl.setName(name);
		}

		columnNameEntryImpl.resetOriginalValues();

		return columnNameEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		columnNameEntryId = objectInput.readLong();
		name = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(columnNameEntryId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}
	}

	public long columnNameEntryId;
	public String name;

}
// LIFERAY-SERVICE-BUILDER-HASH:-636210305