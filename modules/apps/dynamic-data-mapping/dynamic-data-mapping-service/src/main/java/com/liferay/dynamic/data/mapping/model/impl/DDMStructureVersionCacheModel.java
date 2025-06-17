/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model.impl;

import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
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
 * The cache model class for representing DDMStructureVersion in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DDMStructureVersionCacheModel
	implements CacheModel<DDMStructureVersion>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DDMStructureVersionCacheModel)) {
			return false;
		}

		DDMStructureVersionCacheModel ddmStructureVersionCacheModel =
			(DDMStructureVersionCacheModel)object;

		if ((structureVersionId ==
				ddmStructureVersionCacheModel.structureVersionId) &&
			(mvccVersion == ddmStructureVersionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, structureVersionId);

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
		sb.append(", structureVersionId=");
		sb.append(structureVersionId);
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
		sb.append(", structureId=");
		sb.append(structureId);
		sb.append(", version=");
		sb.append(version);
		sb.append(", parentStructureId=");
		sb.append(parentStructureId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", definition=");
		sb.append(definition);
		sb.append(", storageType=");
		sb.append(storageType);
		sb.append(", type=");
		sb.append(type);
		sb.append(", status=");
		sb.append(status);
		sb.append(", statusByUserId=");
		sb.append(statusByUserId);
		sb.append(", statusByUserName=");
		sb.append(statusByUserName);
		sb.append(", statusDate=");
		sb.append(statusDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DDMStructureVersion toEntityModel() {
		DDMStructureVersionImpl ddmStructureVersionImpl =
			new DDMStructureVersionImpl();

		ddmStructureVersionImpl.setMvccVersion(mvccVersion);
		ddmStructureVersionImpl.setCtCollectionId(ctCollectionId);
		ddmStructureVersionImpl.setStructureVersionId(structureVersionId);
		ddmStructureVersionImpl.setGroupId(groupId);
		ddmStructureVersionImpl.setCompanyId(companyId);
		ddmStructureVersionImpl.setUserId(userId);

		if (userName == null) {
			ddmStructureVersionImpl.setUserName("");
		}
		else {
			ddmStructureVersionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			ddmStructureVersionImpl.setCreateDate(null);
		}
		else {
			ddmStructureVersionImpl.setCreateDate(new Date(createDate));
		}

		ddmStructureVersionImpl.setStructureId(structureId);

		if (version == null) {
			ddmStructureVersionImpl.setVersion("");
		}
		else {
			ddmStructureVersionImpl.setVersion(version);
		}

		ddmStructureVersionImpl.setParentStructureId(parentStructureId);

		if (name == null) {
			ddmStructureVersionImpl.setName("");
		}
		else {
			ddmStructureVersionImpl.setName(name);
		}

		if (description == null) {
			ddmStructureVersionImpl.setDescription("");
		}
		else {
			ddmStructureVersionImpl.setDescription(description);
		}

		if (definition == null) {
			ddmStructureVersionImpl.setDefinition("");
		}
		else {
			ddmStructureVersionImpl.setDefinition(definition);
		}

		if (storageType == null) {
			ddmStructureVersionImpl.setStorageType("");
		}
		else {
			ddmStructureVersionImpl.setStorageType(storageType);
		}

		ddmStructureVersionImpl.setType(type);
		ddmStructureVersionImpl.setStatus(status);
		ddmStructureVersionImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			ddmStructureVersionImpl.setStatusByUserName("");
		}
		else {
			ddmStructureVersionImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			ddmStructureVersionImpl.setStatusDate(null);
		}
		else {
			ddmStructureVersionImpl.setStatusDate(new Date(statusDate));
		}

		ddmStructureVersionImpl.resetOriginalValues();

		try {
			_ddmFormMethodHandle.invokeExact(ddmStructureVersionImpl, ddmForm);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return ddmStructureVersionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();

		structureVersionId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();

		structureId = objectInput.readLong();
		version = objectInput.readUTF();

		parentStructureId = objectInput.readLong();
		name = objectInput.readUTF();
		description = (String)objectInput.readObject();
		definition = (String)objectInput.readObject();
		storageType = objectInput.readUTF();

		type = objectInput.readInt();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();

		ddmForm =
			(com.liferay.dynamic.data.mapping.model.DDMForm)
				objectInput.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		objectOutput.writeLong(structureVersionId);

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

		objectOutput.writeLong(structureId);

		if (version == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(version);
		}

		objectOutput.writeLong(parentStructureId);

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

		if (definition == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(definition);
		}

		if (storageType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(storageType);
		}

		objectOutput.writeInt(type);

		objectOutput.writeInt(status);

		objectOutput.writeLong(statusByUserId);

		if (statusByUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(statusByUserName);
		}

		objectOutput.writeLong(statusDate);

		objectOutput.writeObject(ddmForm);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public long structureVersionId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long structureId;
	public String version;
	public long parentStructureId;
	public String name;
	public String description;
	public String definition;
	public String storageType;
	public int type;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;
	public volatile com.liferay.dynamic.data.mapping.model.DDMForm ddmForm;

	private static final MethodHandle _ddmFormMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_ddmFormMethodHandle = lookup.findSetter(
				DDMStructureVersionImpl.class, "_ddmForm",
				com.liferay.dynamic.data.mapping.model.DDMForm.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}