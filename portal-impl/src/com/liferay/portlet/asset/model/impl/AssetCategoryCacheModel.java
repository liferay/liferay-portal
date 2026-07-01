/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.model.impl;

import com.liferay.asset.kernel.model.AssetCategory;
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
 * The cache model class for representing AssetCategory in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetCategoryCacheModel
	implements CacheModel<AssetCategory>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetCategoryCacheModel)) {
			return false;
		}

		AssetCategoryCacheModel assetCategoryCacheModel =
			(AssetCategoryCacheModel)object;

		if ((categoryId == assetCategoryCacheModel.categoryId) &&
			(mvccVersion == assetCategoryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, categoryId);

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
		StringBundler sb = new StringBundler(41);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", categoryId=");
		sb.append(categoryId);
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
		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);
		sb.append(", treePath=");
		sb.append(treePath);
		sb.append(", name=");
		sb.append(name);
		sb.append(", title=");
		sb.append(title);
		sb.append(", description=");
		sb.append(description);
		sb.append(", vocabularyId=");
		sb.append(vocabularyId);
		sb.append(", system=");
		sb.append(system);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public AssetCategory toEntityModel() {
		AssetCategoryImpl assetCategoryImpl = new AssetCategoryImpl();

		assetCategoryImpl.setMvccVersion(mvccVersion);
		assetCategoryImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			assetCategoryImpl.setUuid("");
		}
		else {
			assetCategoryImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			assetCategoryImpl.setExternalReferenceCode("");
		}
		else {
			assetCategoryImpl.setExternalReferenceCode(externalReferenceCode);
		}

		assetCategoryImpl.setCategoryId(categoryId);
		assetCategoryImpl.setGroupId(groupId);
		assetCategoryImpl.setCompanyId(companyId);
		assetCategoryImpl.setUserId(userId);

		if (userName == null) {
			assetCategoryImpl.setUserName("");
		}
		else {
			assetCategoryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			assetCategoryImpl.setCreateDate(null);
		}
		else {
			assetCategoryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			assetCategoryImpl.setModifiedDate(null);
		}
		else {
			assetCategoryImpl.setModifiedDate(new Date(modifiedDate));
		}

		assetCategoryImpl.setParentCategoryId(parentCategoryId);

		if (treePath == null) {
			assetCategoryImpl.setTreePath("");
		}
		else {
			assetCategoryImpl.setTreePath(treePath);
		}

		if (name == null) {
			assetCategoryImpl.setName("");
		}
		else {
			assetCategoryImpl.setName(name);
		}

		if (title == null) {
			assetCategoryImpl.setTitle("");
		}
		else {
			assetCategoryImpl.setTitle(title);
		}

		if (description == null) {
			assetCategoryImpl.setDescription("");
		}
		else {
			assetCategoryImpl.setDescription(description);
		}

		assetCategoryImpl.setVocabularyId(vocabularyId);
		assetCategoryImpl.setSystem(system);

		if (lastPublishDate == Long.MIN_VALUE) {
			assetCategoryImpl.setLastPublishDate(null);
		}
		else {
			assetCategoryImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		assetCategoryImpl.setStatus(status);

		assetCategoryImpl.resetOriginalValues();

		return assetCategoryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		categoryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		parentCategoryId = objectInput.readLong();
		treePath = objectInput.readUTF();
		name = objectInput.readUTF();
		title = (String)objectInput.readObject();
		description = (String)objectInput.readObject();

		vocabularyId = objectInput.readLong();

		system = objectInput.readBoolean();
		lastPublishDate = objectInput.readLong();

		status = objectInput.readInt();
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

		if (externalReferenceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(externalReferenceCode);
		}

		objectOutput.writeLong(categoryId);

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

		objectOutput.writeLong(parentCategoryId);

		if (treePath == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(treePath);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (title == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(title);
		}

		if (description == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(description);
		}

		objectOutput.writeLong(vocabularyId);

		objectOutput.writeBoolean(system);
		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long categoryId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long parentCategoryId;
	public String treePath;
	public String name;
	public String title;
	public String description;
	public long vocabularyId;
	public boolean system;
	public long lastPublishDate;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:299791781