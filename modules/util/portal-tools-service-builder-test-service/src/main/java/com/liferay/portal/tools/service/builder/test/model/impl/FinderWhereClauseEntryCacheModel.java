/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.FinderWhereClauseEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing FinderWhereClauseEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class FinderWhereClauseEntryCacheModel
	implements CacheModel<FinderWhereClauseEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FinderWhereClauseEntryCacheModel)) {
			return false;
		}

		FinderWhereClauseEntryCacheModel finderWhereClauseEntryCacheModel =
			(FinderWhereClauseEntryCacheModel)object;

		if (finderWhereClauseEntryId ==
				finderWhereClauseEntryCacheModel.finderWhereClauseEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, finderWhereClauseEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(11);

		sb.append("{finderWhereClauseEntryId=");
		sb.append(finderWhereClauseEntryId);
		sb.append(", headId=");
		sb.append(headId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", nickname=");
		sb.append(nickname);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public FinderWhereClauseEntry toEntityModel() {
		FinderWhereClauseEntryImpl finderWhereClauseEntryImpl =
			new FinderWhereClauseEntryImpl();

		finderWhereClauseEntryImpl.setFinderWhereClauseEntryId(
			finderWhereClauseEntryId);
		finderWhereClauseEntryImpl.setHeadId(headId);

		if (name == null) {
			finderWhereClauseEntryImpl.setName("");
		}
		else {
			finderWhereClauseEntryImpl.setName(name);
		}

		if (nickname == null) {
			finderWhereClauseEntryImpl.setNickname("");
		}
		else {
			finderWhereClauseEntryImpl.setNickname(nickname);
		}

		finderWhereClauseEntryImpl.setStatus(status);

		finderWhereClauseEntryImpl.resetOriginalValues();

		return finderWhereClauseEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		finderWhereClauseEntryId = objectInput.readLong();

		headId = objectInput.readLong();
		name = objectInput.readUTF();
		nickname = objectInput.readUTF();

		status = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(finderWhereClauseEntryId);

		objectOutput.writeLong(headId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (nickname == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(nickname);
		}

		objectOutput.writeInt(status);
	}

	public long finderWhereClauseEntryId;
	public long headId;
	public String name;
	public String nickname;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:-565013894