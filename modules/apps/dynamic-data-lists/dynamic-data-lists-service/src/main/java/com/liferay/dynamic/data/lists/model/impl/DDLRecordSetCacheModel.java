/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.model.impl;

import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import java.util.Date;

/**
 * The cache model class for representing DDLRecordSet in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DDLRecordSetCacheModel
	implements CacheModel<DDLRecordSet>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DDLRecordSetCacheModel)) {
			return false;
		}

		DDLRecordSetCacheModel ddlRecordSetCacheModel =
			(DDLRecordSetCacheModel)object;

		if ((recordSetId == ddlRecordSetCacheModel.recordSetId) &&
			(mvccVersion == ddlRecordSetCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, recordSetId);

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
		StringBundler sb = new StringBundler(43);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", recordSetId=");
		sb.append(recordSetId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", versionUserId=");
		sb.append(versionUserId);
		sb.append(", versionUserName=");
		sb.append(versionUserName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", DDMStructureId=");
		sb.append(DDMStructureId);
		sb.append(", recordSetKey=");
		sb.append(recordSetKey);
		sb.append(", version=");
		sb.append(version);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", minDisplayRows=");
		sb.append(minDisplayRows);
		sb.append(", scope=");
		sb.append(scope);
		sb.append(", settings=");
		sb.append(settings);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DDLRecordSet toEntityModel() {
		DDLRecordSetImpl ddlRecordSetImpl = new DDLRecordSetImpl();

		ddlRecordSetImpl.setMvccVersion(mvccVersion);
		ddlRecordSetImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			ddlRecordSetImpl.setUuid("");
		}
		else {
			ddlRecordSetImpl.setUuid(uuid);
		}

		ddlRecordSetImpl.setRecordSetId(recordSetId);
		ddlRecordSetImpl.setGroupId(groupId);
		ddlRecordSetImpl.setCompanyId(companyId);
		ddlRecordSetImpl.setUserId(userId);

		if (userName == null) {
			ddlRecordSetImpl.setUserName("");
		}
		else {
			ddlRecordSetImpl.setUserName(userName);
		}

		ddlRecordSetImpl.setVersionUserId(versionUserId);

		if (versionUserName == null) {
			ddlRecordSetImpl.setVersionUserName("");
		}
		else {
			ddlRecordSetImpl.setVersionUserName(versionUserName);
		}

		if (createDate == Long.MIN_VALUE) {
			ddlRecordSetImpl.setCreateDate(null);
		}
		else {
			ddlRecordSetImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			ddlRecordSetImpl.setModifiedDate(null);
		}
		else {
			ddlRecordSetImpl.setModifiedDate(new Date(modifiedDate));
		}

		ddlRecordSetImpl.setDDMStructureId(DDMStructureId);

		if (recordSetKey == null) {
			ddlRecordSetImpl.setRecordSetKey("");
		}
		else {
			ddlRecordSetImpl.setRecordSetKey(recordSetKey);
		}

		if (version == null) {
			ddlRecordSetImpl.setVersion("");
		}
		else {
			ddlRecordSetImpl.setVersion(version);
		}

		if (name == null) {
			ddlRecordSetImpl.setName("");
		}
		else {
			ddlRecordSetImpl.setName(name);
		}

		if (description == null) {
			ddlRecordSetImpl.setDescription("");
		}
		else {
			ddlRecordSetImpl.setDescription(description);
		}

		ddlRecordSetImpl.setMinDisplayRows(minDisplayRows);
		ddlRecordSetImpl.setScope(scope);

		if (settings == null) {
			ddlRecordSetImpl.setSettings("");
		}
		else {
			ddlRecordSetImpl.setSettings(settings);
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			ddlRecordSetImpl.setLastPublishDate(null);
		}
		else {
			ddlRecordSetImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		ddlRecordSetImpl.resetOriginalValues();

		try {
			_ddmFormValuesMethodHandle.invokeExact(
				ddlRecordSetImpl, ddmFormValues);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return ddlRecordSetImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();

		recordSetId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();

		versionUserId = objectInput.readLong();
		versionUserName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		DDMStructureId = objectInput.readLong();
		recordSetKey = objectInput.readUTF();
		version = objectInput.readUTF();
		name = objectInput.readUTF();
		description = objectInput.readUTF();

		minDisplayRows = objectInput.readInt();

		scope = objectInput.readInt();
		settings = (String)objectInput.readObject();
		lastPublishDate = objectInput.readLong();

		ddmFormValues =
			(com.liferay.dynamic.data.mapping.storage.DDMFormValues)
				objectInput.readObject();
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

		objectOutput.writeLong(recordSetId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(versionUserId);

		if (versionUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(versionUserName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(DDMStructureId);

		if (recordSetKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(recordSetKey);
		}

		if (version == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(version);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		objectOutput.writeInt(minDisplayRows);

		objectOutput.writeInt(scope);

		if (settings == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(settings);
		}

		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeObject(ddmFormValues);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public long recordSetId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long versionUserId;
	public String versionUserName;
	public long createDate;
	public long modifiedDate;
	public long DDMStructureId;
	public String recordSetKey;
	public String version;
	public String name;
	public String description;
	public int minDisplayRows;
	public int scope;
	public String settings;
	public long lastPublishDate;
	public volatile com.liferay.dynamic.data.mapping.storage.DDMFormValues
		ddmFormValues;

	private static final MethodHandle _ddmFormValuesMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_ddmFormValuesMethodHandle = lookup.findSetter(
				DDLRecordSetImpl.class, "_ddmFormValues",
				com.liferay.dynamic.data.mapping.storage.DDMFormValues.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}