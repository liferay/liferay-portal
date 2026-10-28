/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.model.impl;

import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
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
 * The cache model class for representing AssetListEntryAudienceEntryRel in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetListEntryAudienceEntryRelCacheModel
	implements CacheModel<AssetListEntryAudienceEntryRel>, Externalizable,
			   MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetListEntryAudienceEntryRelCacheModel)) {
			return false;
		}

		AssetListEntryAudienceEntryRelCacheModel
			assetListEntryAudienceEntryRelCacheModel =
				(AssetListEntryAudienceEntryRelCacheModel)object;

		if ((assetListEntryAudienceEntryRelId ==
				assetListEntryAudienceEntryRelCacheModel.
					assetListEntryAudienceEntryRelId) &&
			(mvccVersion ==
				assetListEntryAudienceEntryRelCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, assetListEntryAudienceEntryRelId);

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
		StringBundler sb = new StringBundler(31);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", assetListEntryAudienceEntryRelId=");
		sb.append(assetListEntryAudienceEntryRelId);
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
		sb.append(", assetListEntryId=");
		sb.append(assetListEntryId);
		sb.append(", audienceEntryERC=");
		sb.append(audienceEntryERC);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", typeSettings=");
		sb.append(typeSettings);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public AssetListEntryAudienceEntryRel toEntityModel() {
		AssetListEntryAudienceEntryRelImpl assetListEntryAudienceEntryRelImpl =
			new AssetListEntryAudienceEntryRelImpl();

		assetListEntryAudienceEntryRelImpl.setMvccVersion(mvccVersion);
		assetListEntryAudienceEntryRelImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			assetListEntryAudienceEntryRelImpl.setUuid("");
		}
		else {
			assetListEntryAudienceEntryRelImpl.setUuid(uuid);
		}

		assetListEntryAudienceEntryRelImpl.setAssetListEntryAudienceEntryRelId(
			assetListEntryAudienceEntryRelId);
		assetListEntryAudienceEntryRelImpl.setGroupId(groupId);
		assetListEntryAudienceEntryRelImpl.setCompanyId(companyId);
		assetListEntryAudienceEntryRelImpl.setUserId(userId);

		if (userName == null) {
			assetListEntryAudienceEntryRelImpl.setUserName("");
		}
		else {
			assetListEntryAudienceEntryRelImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			assetListEntryAudienceEntryRelImpl.setCreateDate(null);
		}
		else {
			assetListEntryAudienceEntryRelImpl.setCreateDate(
				new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			assetListEntryAudienceEntryRelImpl.setModifiedDate(null);
		}
		else {
			assetListEntryAudienceEntryRelImpl.setModifiedDate(
				new Date(modifiedDate));
		}

		assetListEntryAudienceEntryRelImpl.setAssetListEntryId(
			assetListEntryId);

		if (audienceEntryERC == null) {
			assetListEntryAudienceEntryRelImpl.setAudienceEntryERC("");
		}
		else {
			assetListEntryAudienceEntryRelImpl.setAudienceEntryERC(
				audienceEntryERC);
		}

		assetListEntryAudienceEntryRelImpl.setPriority(priority);

		if (typeSettings == null) {
			assetListEntryAudienceEntryRelImpl.setTypeSettings("");
		}
		else {
			assetListEntryAudienceEntryRelImpl.setTypeSettings(typeSettings);
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			assetListEntryAudienceEntryRelImpl.setLastPublishDate(null);
		}
		else {
			assetListEntryAudienceEntryRelImpl.setLastPublishDate(
				new Date(lastPublishDate));
		}

		assetListEntryAudienceEntryRelImpl.resetOriginalValues();

		return assetListEntryAudienceEntryRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();

		assetListEntryAudienceEntryRelId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		assetListEntryId = objectInput.readLong();
		audienceEntryERC = objectInput.readUTF();

		priority = objectInput.readInt();
		typeSettings = (String)objectInput.readObject();
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

		objectOutput.writeLong(assetListEntryAudienceEntryRelId);

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

		objectOutput.writeLong(assetListEntryId);

		if (audienceEntryERC == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(audienceEntryERC);
		}

		objectOutput.writeInt(priority);

		if (typeSettings == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(typeSettings);
		}

		objectOutput.writeLong(lastPublishDate);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public long assetListEntryAudienceEntryRelId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long assetListEntryId;
	public String audienceEntryERC;
	public int priority;
	public String typeSettings;
	public long lastPublishDate;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1382772633