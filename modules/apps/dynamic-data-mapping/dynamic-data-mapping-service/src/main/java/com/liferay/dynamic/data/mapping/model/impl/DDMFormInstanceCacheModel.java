/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model.impl;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
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
 * The cache model class for representing DDMFormInstance in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DDMFormInstanceCacheModel
	implements CacheModel<DDMFormInstance>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DDMFormInstanceCacheModel)) {
			return false;
		}

		DDMFormInstanceCacheModel ddmFormInstanceCacheModel =
			(DDMFormInstanceCacheModel)object;

		if ((formInstanceId == ddmFormInstanceCacheModel.formInstanceId) &&
			(mvccVersion == ddmFormInstanceCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, formInstanceId);

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
		StringBundler sb = new StringBundler(37);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", formInstanceId=");
		sb.append(formInstanceId);
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
		sb.append(", structureId=");
		sb.append(structureId);
		sb.append(", version=");
		sb.append(version);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", settings=");
		sb.append(settings);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DDMFormInstance toEntityModel() {
		DDMFormInstanceImpl ddmFormInstanceImpl = new DDMFormInstanceImpl();

		ddmFormInstanceImpl.setMvccVersion(mvccVersion);
		ddmFormInstanceImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			ddmFormInstanceImpl.setUuid("");
		}
		else {
			ddmFormInstanceImpl.setUuid(uuid);
		}

		ddmFormInstanceImpl.setFormInstanceId(formInstanceId);
		ddmFormInstanceImpl.setGroupId(groupId);
		ddmFormInstanceImpl.setCompanyId(companyId);
		ddmFormInstanceImpl.setUserId(userId);

		if (userName == null) {
			ddmFormInstanceImpl.setUserName("");
		}
		else {
			ddmFormInstanceImpl.setUserName(userName);
		}

		ddmFormInstanceImpl.setVersionUserId(versionUserId);

		if (versionUserName == null) {
			ddmFormInstanceImpl.setVersionUserName("");
		}
		else {
			ddmFormInstanceImpl.setVersionUserName(versionUserName);
		}

		if (createDate == Long.MIN_VALUE) {
			ddmFormInstanceImpl.setCreateDate(null);
		}
		else {
			ddmFormInstanceImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			ddmFormInstanceImpl.setModifiedDate(null);
		}
		else {
			ddmFormInstanceImpl.setModifiedDate(new Date(modifiedDate));
		}

		ddmFormInstanceImpl.setStructureId(structureId);

		if (version == null) {
			ddmFormInstanceImpl.setVersion("");
		}
		else {
			ddmFormInstanceImpl.setVersion(version);
		}

		if (name == null) {
			ddmFormInstanceImpl.setName("");
		}
		else {
			ddmFormInstanceImpl.setName(name);
		}

		if (description == null) {
			ddmFormInstanceImpl.setDescription("");
		}
		else {
			ddmFormInstanceImpl.setDescription(description);
		}

		if (settings == null) {
			ddmFormInstanceImpl.setSettings("");
		}
		else {
			ddmFormInstanceImpl.setSettings(settings);
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			ddmFormInstanceImpl.setLastPublishDate(null);
		}
		else {
			ddmFormInstanceImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		ddmFormInstanceImpl.resetOriginalValues();

		try {
			_ddmFormValuesMethodHandle.invokeExact(
				ddmFormInstanceImpl, ddmFormValues);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return ddmFormInstanceImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();

		formInstanceId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();

		versionUserId = objectInput.readLong();
		versionUserName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		structureId = objectInput.readLong();
		version = objectInput.readUTF();
		name = objectInput.readUTF();
		description = (String)objectInput.readObject();
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

		objectOutput.writeLong(formInstanceId);

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

		objectOutput.writeLong(structureId);

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
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(description);
		}

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
	public long formInstanceId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long versionUserId;
	public String versionUserName;
	public long createDate;
	public long modifiedDate;
	public long structureId;
	public String version;
	public String name;
	public String description;
	public String settings;
	public long lastPublishDate;
	public volatile com.liferay.dynamic.data.mapping.storage.DDMFormValues
		ddmFormValues;

	private static final MethodHandle _ddmFormValuesMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_ddmFormValuesMethodHandle = lookup.findSetter(
				DDMFormInstanceImpl.class, "_ddmFormValues",
				com.liferay.dynamic.data.mapping.storage.DDMFormValues.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}