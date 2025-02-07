/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.model.ObjectDefinitionSetting;
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
 * The cache model class for representing ObjectDefinitionSetting in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class ObjectDefinitionSettingCacheModel
	implements CacheModel<ObjectDefinitionSetting>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectDefinitionSettingCacheModel)) {
			return false;
		}

		ObjectDefinitionSettingCacheModel objectDefinitionSettingCacheModel =
			(ObjectDefinitionSettingCacheModel)object;

		if ((objectDefinitionSettingId ==
				objectDefinitionSettingCacheModel.objectDefinitionSettingId) &&
			(mvccVersion == objectDefinitionSettingCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, objectDefinitionSettingId);

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
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", objectDefinitionSettingId=");
		sb.append(objectDefinitionSettingId);
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
		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", value=");
		sb.append(value);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ObjectDefinitionSetting toEntityModel() {
		ObjectDefinitionSettingImpl objectDefinitionSettingImpl =
			new ObjectDefinitionSettingImpl();

		objectDefinitionSettingImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			objectDefinitionSettingImpl.setUuid("");
		}
		else {
			objectDefinitionSettingImpl.setUuid(uuid);
		}

		objectDefinitionSettingImpl.setObjectDefinitionSettingId(
			objectDefinitionSettingId);
		objectDefinitionSettingImpl.setCompanyId(companyId);
		objectDefinitionSettingImpl.setUserId(userId);

		if (userName == null) {
			objectDefinitionSettingImpl.setUserName("");
		}
		else {
			objectDefinitionSettingImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			objectDefinitionSettingImpl.setCreateDate(null);
		}
		else {
			objectDefinitionSettingImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			objectDefinitionSettingImpl.setModifiedDate(null);
		}
		else {
			objectDefinitionSettingImpl.setModifiedDate(new Date(modifiedDate));
		}

		objectDefinitionSettingImpl.setObjectDefinitionId(objectDefinitionId);

		if (name == null) {
			objectDefinitionSettingImpl.setName("");
		}
		else {
			objectDefinitionSettingImpl.setName(name);
		}

		if (value == null) {
			objectDefinitionSettingImpl.setValue("");
		}
		else {
			objectDefinitionSettingImpl.setValue(value);
		}

		objectDefinitionSettingImpl.resetOriginalValues();

		return objectDefinitionSettingImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();

		objectDefinitionSettingId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		objectDefinitionId = objectInput.readLong();
		name = objectInput.readUTF();
		value = objectInput.readUTF();
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

		objectOutput.writeLong(objectDefinitionSettingId);

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

		objectOutput.writeLong(objectDefinitionId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (value == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(value);
		}
	}

	public long mvccVersion;
	public String uuid;
	public long objectDefinitionSettingId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long objectDefinitionId;
	public String name;
	public String value;

}