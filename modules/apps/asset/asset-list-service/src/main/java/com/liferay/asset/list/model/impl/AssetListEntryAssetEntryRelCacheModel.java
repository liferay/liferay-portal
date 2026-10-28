/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.model.impl;

import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
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
 * The cache model class for representing AssetListEntryAssetEntryRel in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetListEntryAssetEntryRelCacheModel
	implements CacheModel<AssetListEntryAssetEntryRel>, Externalizable,
			   MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetListEntryAssetEntryRelCacheModel)) {
			return false;
		}

		AssetListEntryAssetEntryRelCacheModel
			assetListEntryAssetEntryRelCacheModel =
				(AssetListEntryAssetEntryRelCacheModel)object;

		if ((assetListEntryAssetEntryRelId ==
				assetListEntryAssetEntryRelCacheModel.
					assetListEntryAssetEntryRelId) &&
			(mvccVersion ==
				assetListEntryAssetEntryRelCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, assetListEntryAssetEntryRelId);

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
		sb.append(", assetListEntryAssetEntryRelId=");
		sb.append(assetListEntryAssetEntryRelId);
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
		sb.append(", assetEntryId=");
		sb.append(assetEntryId);
		sb.append(", assetListEntryId=");
		sb.append(assetListEntryId);
		sb.append(", audienceEntryERC=");
		sb.append(audienceEntryERC);
		sb.append(", segmentsEntryId=");
		sb.append(segmentsEntryId);
		sb.append(", position=");
		sb.append(position);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public AssetListEntryAssetEntryRel toEntityModel() {
		AssetListEntryAssetEntryRelImpl assetListEntryAssetEntryRelImpl =
			new AssetListEntryAssetEntryRelImpl();

		assetListEntryAssetEntryRelImpl.setMvccVersion(mvccVersion);
		assetListEntryAssetEntryRelImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			assetListEntryAssetEntryRelImpl.setUuid("");
		}
		else {
			assetListEntryAssetEntryRelImpl.setUuid(uuid);
		}

		assetListEntryAssetEntryRelImpl.setAssetListEntryAssetEntryRelId(
			assetListEntryAssetEntryRelId);
		assetListEntryAssetEntryRelImpl.setGroupId(groupId);
		assetListEntryAssetEntryRelImpl.setCompanyId(companyId);
		assetListEntryAssetEntryRelImpl.setUserId(userId);

		if (userName == null) {
			assetListEntryAssetEntryRelImpl.setUserName("");
		}
		else {
			assetListEntryAssetEntryRelImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			assetListEntryAssetEntryRelImpl.setCreateDate(null);
		}
		else {
			assetListEntryAssetEntryRelImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			assetListEntryAssetEntryRelImpl.setModifiedDate(null);
		}
		else {
			assetListEntryAssetEntryRelImpl.setModifiedDate(
				new Date(modifiedDate));
		}

		assetListEntryAssetEntryRelImpl.setAssetEntryId(assetEntryId);
		assetListEntryAssetEntryRelImpl.setAssetListEntryId(assetListEntryId);

		if (audienceEntryERC == null) {
			assetListEntryAssetEntryRelImpl.setAudienceEntryERC("");
		}
		else {
			assetListEntryAssetEntryRelImpl.setAudienceEntryERC(
				audienceEntryERC);
		}

		assetListEntryAssetEntryRelImpl.setSegmentsEntryId(segmentsEntryId);
		assetListEntryAssetEntryRelImpl.setPosition(position);

		if (lastPublishDate == Long.MIN_VALUE) {
			assetListEntryAssetEntryRelImpl.setLastPublishDate(null);
		}
		else {
			assetListEntryAssetEntryRelImpl.setLastPublishDate(
				new Date(lastPublishDate));
		}

		assetListEntryAssetEntryRelImpl.resetOriginalValues();

		return assetListEntryAssetEntryRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();

		assetListEntryAssetEntryRelId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		assetEntryId = objectInput.readLong();

		assetListEntryId = objectInput.readLong();
		audienceEntryERC = objectInput.readUTF();

		segmentsEntryId = objectInput.readLong();

		position = objectInput.readInt();
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

		objectOutput.writeLong(assetListEntryAssetEntryRelId);

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

		objectOutput.writeLong(assetEntryId);

		objectOutput.writeLong(assetListEntryId);

		if (audienceEntryERC == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(audienceEntryERC);
		}

		objectOutput.writeLong(segmentsEntryId);

		objectOutput.writeInt(position);
		objectOutput.writeLong(lastPublishDate);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public long assetListEntryAssetEntryRelId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long assetEntryId;
	public long assetListEntryId;
	public String audienceEntryERC;
	public long segmentsEntryId;
	public int position;
	public long lastPublishDate;

}
// LIFERAY-SERVICE-BUILDER-HASH:-878485345