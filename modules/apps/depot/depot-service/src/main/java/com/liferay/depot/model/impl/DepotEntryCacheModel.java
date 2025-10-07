/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.model.impl;

import com.liferay.depot.model.DepotEntry;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing DepotEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DepotEntryCacheModel
	implements CacheModel<DepotEntry>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DepotEntryCacheModel)) {
			return false;
		}

		DepotEntryCacheModel depotEntryCacheModel =
			(DepotEntryCacheModel)object;

		if ((depotEntryId == depotEntryCacheModel.depotEntryId) &&
			(mvccVersion == depotEntryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, depotEntryId);

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
		StringBundler sb = new StringBundler(23);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", depotEntryId=");
		sb.append(depotEntryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", type=");
		sb.append(type);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DepotEntry toEntityModel() {
		DepotEntryImpl depotEntryImpl = new DepotEntryImpl();

		depotEntryImpl.setMvccVersion(mvccVersion);
		depotEntryImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			depotEntryImpl.setUuid("");
		}
		else {
			depotEntryImpl.setUuid(uuid);
		}

		depotEntryImpl.setDepotEntryId(depotEntryId);
		depotEntryImpl.setGroupId(groupId);
		depotEntryImpl.setCompanyId(companyId);
		depotEntryImpl.setUserId(userId);

		if (userName == null) {
			depotEntryImpl.setUserName("");
		}
		else {
			depotEntryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			depotEntryImpl.setCreateDate(null);
		}
		else {
			depotEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			depotEntryImpl.setModifiedDate(null);
		}
		else {
			depotEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		depotEntryImpl.setType(type);

		depotEntryImpl.resetOriginalValues();

		return depotEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();

		depotEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		type = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		objectOutput.writeLong(depotEntryId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeInt(type);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public long depotEntryId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public int type;

}