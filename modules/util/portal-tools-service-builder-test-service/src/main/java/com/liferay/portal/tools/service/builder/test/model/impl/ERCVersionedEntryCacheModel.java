/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing ERCVersionedEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ERCVersionedEntryCacheModel
	implements CacheModel<ERCVersionedEntry>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ERCVersionedEntryCacheModel)) {
			return false;
		}

		ERCVersionedEntryCacheModel ercVersionedEntryCacheModel =
			(ERCVersionedEntryCacheModel)object;

		if ((ercVersionedEntryId ==
				ercVersionedEntryCacheModel.ercVersionedEntryId) &&
			(mvccVersion == ercVersionedEntryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, ercVersionedEntryId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(15);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", headId=");
		sb.append(headId);
		sb.append(", ercVersionedEntryId=");
		sb.append(ercVersionedEntryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);

		return sb.toString();
	}

	@Override
	public ERCVersionedEntry toEntityModel() {
		ERCVersionedEntryImpl ercVersionedEntryImpl =
			new ERCVersionedEntryImpl();

		ercVersionedEntryImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			ercVersionedEntryImpl.setUuid("");
		}
		else {
			ercVersionedEntryImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			ercVersionedEntryImpl.setExternalReferenceCode("");
		}
		else {
			ercVersionedEntryImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		ercVersionedEntryImpl.setHeadId(headId);
		ercVersionedEntryImpl.setHead(head);
		ercVersionedEntryImpl.setErcVersionedEntryId(ercVersionedEntryId);
		ercVersionedEntryImpl.setGroupId(groupId);
		ercVersionedEntryImpl.setCompanyId(companyId);

		ercVersionedEntryImpl.resetOriginalValues();

		return ercVersionedEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		headId = objectInput.readLong();

		head = objectInput.readBoolean();

		ercVersionedEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		if (externalReferenceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(externalReferenceCode);
		}

		objectOutput.writeLong(headId);

		objectOutput.writeBoolean(head);

		objectOutput.writeLong(ercVersionedEntryId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long headId;
	public boolean head;
	public long ercVersionedEntryId;
	public long groupId;
	public long companyId;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1407182578