/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPOptionCategory;
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
 * The cache model class for representing CPOptionCategory in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPOptionCategoryCacheModel
	implements CacheModel<CPOptionCategory>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPOptionCategoryCacheModel)) {
			return false;
		}

		CPOptionCategoryCacheModel cpOptionCategoryCacheModel =
			(CPOptionCategoryCacheModel)object;

		if ((CPOptionCategoryId ==
				cpOptionCategoryCacheModel.CPOptionCategoryId) &&
			(mvccVersion == cpOptionCategoryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, CPOptionCategoryId);

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
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", CPOptionCategoryId=");
		sb.append(CPOptionCategoryId);
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
		sb.append(", title=");
		sb.append(title);
		sb.append(", description=");
		sb.append(description);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", key=");
		sb.append(key);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CPOptionCategory toEntityModel() {
		CPOptionCategoryImpl cpOptionCategoryImpl = new CPOptionCategoryImpl();

		cpOptionCategoryImpl.setMvccVersion(mvccVersion);
		cpOptionCategoryImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			cpOptionCategoryImpl.setUuid("");
		}
		else {
			cpOptionCategoryImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			cpOptionCategoryImpl.setExternalReferenceCode("");
		}
		else {
			cpOptionCategoryImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		cpOptionCategoryImpl.setCPOptionCategoryId(CPOptionCategoryId);
		cpOptionCategoryImpl.setCompanyId(companyId);
		cpOptionCategoryImpl.setUserId(userId);

		if (userName == null) {
			cpOptionCategoryImpl.setUserName("");
		}
		else {
			cpOptionCategoryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			cpOptionCategoryImpl.setCreateDate(null);
		}
		else {
			cpOptionCategoryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			cpOptionCategoryImpl.setModifiedDate(null);
		}
		else {
			cpOptionCategoryImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (title == null) {
			cpOptionCategoryImpl.setTitle("");
		}
		else {
			cpOptionCategoryImpl.setTitle(title);
		}

		if (description == null) {
			cpOptionCategoryImpl.setDescription("");
		}
		else {
			cpOptionCategoryImpl.setDescription(description);
		}

		cpOptionCategoryImpl.setPriority(priority);

		if (key == null) {
			cpOptionCategoryImpl.setKey("");
		}
		else {
			cpOptionCategoryImpl.setKey(key);
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			cpOptionCategoryImpl.setLastPublishDate(null);
		}
		else {
			cpOptionCategoryImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		cpOptionCategoryImpl.setStatus(status);

		cpOptionCategoryImpl.resetOriginalValues();

		return cpOptionCategoryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		CPOptionCategoryId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		title = objectInput.readUTF();
		description = objectInput.readUTF();

		priority = objectInput.readDouble();
		key = objectInput.readUTF();
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

		objectOutput.writeLong(CPOptionCategoryId);

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

		if (title == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(title);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		objectOutput.writeDouble(priority);

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long CPOptionCategoryId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String title;
	public String description;
	public double priority;
	public String key;
	public long lastPublishDate;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:-759711145