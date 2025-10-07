/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.model.impl;

import com.liferay.depot.model.DepotEntryGroupRel;
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
 * The cache model class for representing DepotEntryGroupRel in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DepotEntryGroupRelCacheModel
	implements CacheModel<DepotEntryGroupRel>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DepotEntryGroupRelCacheModel)) {
			return false;
		}

		DepotEntryGroupRelCacheModel depotEntryGroupRelCacheModel =
			(DepotEntryGroupRelCacheModel)object;

		if ((depotEntryGroupRelId ==
				depotEntryGroupRelCacheModel.depotEntryGroupRelId) &&
			(mvccVersion == depotEntryGroupRelCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, depotEntryGroupRelId);

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
		StringBundler sb = new StringBundler(33);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", depotEntryGroupRelId=");
		sb.append(depotEntryGroupRelId);
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
		sb.append(", ddmStructuresAvailable=");
		sb.append(ddmStructuresAvailable);
		sb.append(", depotEntryId=");
		sb.append(depotEntryId);
		sb.append(", searchable=");
		sb.append(searchable);
		sb.append(", toGroupId=");
		sb.append(toGroupId);
		sb.append(", type=");
		sb.append(type);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DepotEntryGroupRel toEntityModel() {
		DepotEntryGroupRelImpl depotEntryGroupRelImpl =
			new DepotEntryGroupRelImpl();

		depotEntryGroupRelImpl.setMvccVersion(mvccVersion);
		depotEntryGroupRelImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			depotEntryGroupRelImpl.setUuid("");
		}
		else {
			depotEntryGroupRelImpl.setUuid(uuid);
		}

		depotEntryGroupRelImpl.setDepotEntryGroupRelId(depotEntryGroupRelId);
		depotEntryGroupRelImpl.setGroupId(groupId);
		depotEntryGroupRelImpl.setCompanyId(companyId);
		depotEntryGroupRelImpl.setUserId(userId);

		if (userName == null) {
			depotEntryGroupRelImpl.setUserName("");
		}
		else {
			depotEntryGroupRelImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			depotEntryGroupRelImpl.setCreateDate(null);
		}
		else {
			depotEntryGroupRelImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			depotEntryGroupRelImpl.setModifiedDate(null);
		}
		else {
			depotEntryGroupRelImpl.setModifiedDate(new Date(modifiedDate));
		}

		depotEntryGroupRelImpl.setDdmStructuresAvailable(
			ddmStructuresAvailable);
		depotEntryGroupRelImpl.setDepotEntryId(depotEntryId);
		depotEntryGroupRelImpl.setSearchable(searchable);
		depotEntryGroupRelImpl.setToGroupId(toGroupId);
		depotEntryGroupRelImpl.setType(type);

		if (lastPublishDate == Long.MIN_VALUE) {
			depotEntryGroupRelImpl.setLastPublishDate(null);
		}
		else {
			depotEntryGroupRelImpl.setLastPublishDate(
				new Date(lastPublishDate));
		}

		depotEntryGroupRelImpl.resetOriginalValues();

		return depotEntryGroupRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();

		depotEntryGroupRelId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		ddmStructuresAvailable = objectInput.readBoolean();

		depotEntryId = objectInput.readLong();

		searchable = objectInput.readBoolean();

		toGroupId = objectInput.readLong();

		type = objectInput.readInt();
		lastPublishDate = objectInput.readLong();
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

		objectOutput.writeLong(depotEntryGroupRelId);

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

		objectOutput.writeBoolean(ddmStructuresAvailable);

		objectOutput.writeLong(depotEntryId);

		objectOutput.writeBoolean(searchable);

		objectOutput.writeLong(toGroupId);

		objectOutput.writeInt(type);
		objectOutput.writeLong(lastPublishDate);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public long depotEntryGroupRelId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public boolean ddmStructuresAvailable;
	public long depotEntryId;
	public boolean searchable;
	public long toGroupId;
	public int type;
	public long lastPublishDate;

}