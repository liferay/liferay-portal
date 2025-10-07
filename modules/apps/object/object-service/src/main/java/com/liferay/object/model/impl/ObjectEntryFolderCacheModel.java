/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.model.ObjectEntryFolder;
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
 * The cache model class for representing ObjectEntryFolder in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class ObjectEntryFolderCacheModel
	implements CacheModel<ObjectEntryFolder>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectEntryFolderCacheModel)) {
			return false;
		}

		ObjectEntryFolderCacheModel objectEntryFolderCacheModel =
			(ObjectEntryFolderCacheModel)object;

		if ((objectEntryFolderId ==
				objectEntryFolderCacheModel.objectEntryFolderId) &&
			(mvccVersion == objectEntryFolderCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, objectEntryFolderId);

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
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", objectEntryFolderId=");
		sb.append(objectEntryFolderId);
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
		sb.append(", parentObjectEntryFolderId=");
		sb.append(parentObjectEntryFolderId);
		sb.append(", description=");
		sb.append(description);
		sb.append(", label=");
		sb.append(label);
		sb.append(", name=");
		sb.append(name);
		sb.append(", treePath=");
		sb.append(treePath);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ObjectEntryFolder toEntityModel() {
		ObjectEntryFolderImpl objectEntryFolderImpl =
			new ObjectEntryFolderImpl();

		objectEntryFolderImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			objectEntryFolderImpl.setUuid("");
		}
		else {
			objectEntryFolderImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			objectEntryFolderImpl.setExternalReferenceCode("");
		}
		else {
			objectEntryFolderImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		objectEntryFolderImpl.setObjectEntryFolderId(objectEntryFolderId);
		objectEntryFolderImpl.setGroupId(groupId);
		objectEntryFolderImpl.setCompanyId(companyId);
		objectEntryFolderImpl.setUserId(userId);

		if (userName == null) {
			objectEntryFolderImpl.setUserName("");
		}
		else {
			objectEntryFolderImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			objectEntryFolderImpl.setCreateDate(null);
		}
		else {
			objectEntryFolderImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			objectEntryFolderImpl.setModifiedDate(null);
		}
		else {
			objectEntryFolderImpl.setModifiedDate(new Date(modifiedDate));
		}

		objectEntryFolderImpl.setParentObjectEntryFolderId(
			parentObjectEntryFolderId);

		if (description == null) {
			objectEntryFolderImpl.setDescription("");
		}
		else {
			objectEntryFolderImpl.setDescription(description);
		}

		if (label == null) {
			objectEntryFolderImpl.setLabel("");
		}
		else {
			objectEntryFolderImpl.setLabel(label);
		}

		if (name == null) {
			objectEntryFolderImpl.setName("");
		}
		else {
			objectEntryFolderImpl.setName(name);
		}

		if (treePath == null) {
			objectEntryFolderImpl.setTreePath("");
		}
		else {
			objectEntryFolderImpl.setTreePath(treePath);
		}

		objectEntryFolderImpl.setStatus(status);

		objectEntryFolderImpl.resetOriginalValues();

		return objectEntryFolderImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		objectEntryFolderId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		parentObjectEntryFolderId = objectInput.readLong();
		description = objectInput.readUTF();
		label = objectInput.readUTF();
		name = objectInput.readUTF();
		treePath = objectInput.readUTF();

		status = objectInput.readInt();
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

		objectOutput.writeLong(objectEntryFolderId);

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

		objectOutput.writeLong(parentObjectEntryFolderId);

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		if (label == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(label);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (treePath == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(treePath);
		}

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long objectEntryFolderId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long parentObjectEntryFolderId;
	public String description;
	public String label;
	public String name;
	public String treePath;
	public int status;

}