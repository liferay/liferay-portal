/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntryVersion;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing ERCVersionedEntryVersion in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ERCVersionedEntryVersionCacheModel
	implements CacheModel<ERCVersionedEntryVersion>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ERCVersionedEntryVersionCacheModel)) {
			return false;
		}

		ERCVersionedEntryVersionCacheModel ercVersionedEntryVersionCacheModel =
			(ERCVersionedEntryVersionCacheModel)object;

		if (ercVersionedEntryVersionId ==
				ercVersionedEntryVersionCacheModel.ercVersionedEntryVersionId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, ercVersionedEntryVersionId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(15);

		sb.append("{ercVersionedEntryVersionId=");
		sb.append(ercVersionedEntryVersionId);
		sb.append(", version=");
		sb.append(version);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", ercVersionedEntryId=");
		sb.append(ercVersionedEntryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);

		return sb.toString();
	}

	@Override
	public ERCVersionedEntryVersion toEntityModel() {
		ERCVersionedEntryVersionImpl ercVersionedEntryVersionImpl =
			new ERCVersionedEntryVersionImpl();

		ercVersionedEntryVersionImpl.setErcVersionedEntryVersionId(
			ercVersionedEntryVersionId);
		ercVersionedEntryVersionImpl.setVersion(version);

		if (uuid == null) {
			ercVersionedEntryVersionImpl.setUuid("");
		}
		else {
			ercVersionedEntryVersionImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			ercVersionedEntryVersionImpl.setExternalReferenceCode("");
		}
		else {
			ercVersionedEntryVersionImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		ercVersionedEntryVersionImpl.setErcVersionedEntryId(
			ercVersionedEntryId);
		ercVersionedEntryVersionImpl.setGroupId(groupId);
		ercVersionedEntryVersionImpl.setCompanyId(companyId);

		ercVersionedEntryVersionImpl.resetOriginalValues();

		return ercVersionedEntryVersionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		ercVersionedEntryVersionId = objectInput.readLong();

		version = objectInput.readInt();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		ercVersionedEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(ercVersionedEntryVersionId);

		objectOutput.writeInt(version);

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

		objectOutput.writeLong(ercVersionedEntryId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);
	}

	public long ercVersionedEntryVersionId;
	public int version;
	public String uuid;
	public String externalReferenceCode;
	public long ercVersionedEntryId;
	public long groupId;
	public long companyId;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1729621124