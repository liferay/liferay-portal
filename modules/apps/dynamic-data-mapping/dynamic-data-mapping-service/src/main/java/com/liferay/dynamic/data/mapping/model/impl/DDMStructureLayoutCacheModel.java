/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model.impl;

import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
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
 * The cache model class for representing DDMStructureLayout in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DDMStructureLayoutCacheModel
	implements CacheModel<DDMStructureLayout>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DDMStructureLayoutCacheModel)) {
			return false;
		}

		DDMStructureLayoutCacheModel ddmStructureLayoutCacheModel =
			(DDMStructureLayoutCacheModel)object;

		if ((structureLayoutId ==
				ddmStructureLayoutCacheModel.structureLayoutId) &&
			(mvccVersion == ddmStructureLayoutCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, structureLayoutId);

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
		sb.append(", structureLayoutId=");
		sb.append(structureLayoutId);
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
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", structureLayoutKey=");
		sb.append(structureLayoutKey);
		sb.append(", structureVersionId=");
		sb.append(structureVersionId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", definition=");
		sb.append(definition);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DDMStructureLayout toEntityModel() {
		DDMStructureLayoutImpl ddmStructureLayoutImpl =
			new DDMStructureLayoutImpl();

		ddmStructureLayoutImpl.setMvccVersion(mvccVersion);
		ddmStructureLayoutImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			ddmStructureLayoutImpl.setUuid("");
		}
		else {
			ddmStructureLayoutImpl.setUuid(uuid);
		}

		ddmStructureLayoutImpl.setStructureLayoutId(structureLayoutId);
		ddmStructureLayoutImpl.setGroupId(groupId);
		ddmStructureLayoutImpl.setCompanyId(companyId);
		ddmStructureLayoutImpl.setUserId(userId);

		if (userName == null) {
			ddmStructureLayoutImpl.setUserName("");
		}
		else {
			ddmStructureLayoutImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			ddmStructureLayoutImpl.setCreateDate(null);
		}
		else {
			ddmStructureLayoutImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			ddmStructureLayoutImpl.setModifiedDate(null);
		}
		else {
			ddmStructureLayoutImpl.setModifiedDate(new Date(modifiedDate));
		}

		ddmStructureLayoutImpl.setClassNameId(classNameId);

		if (structureLayoutKey == null) {
			ddmStructureLayoutImpl.setStructureLayoutKey("");
		}
		else {
			ddmStructureLayoutImpl.setStructureLayoutKey(structureLayoutKey);
		}

		ddmStructureLayoutImpl.setStructureVersionId(structureVersionId);

		if (name == null) {
			ddmStructureLayoutImpl.setName("");
		}
		else {
			ddmStructureLayoutImpl.setName(name);
		}

		if (description == null) {
			ddmStructureLayoutImpl.setDescription("");
		}
		else {
			ddmStructureLayoutImpl.setDescription(description);
		}

		if (definition == null) {
			ddmStructureLayoutImpl.setDefinition("");
		}
		else {
			ddmStructureLayoutImpl.setDefinition(definition);
		}

		ddmStructureLayoutImpl.resetOriginalValues();

		try {
			_ddmFormLayoutMethodHandle.invokeExact(
				ddmStructureLayoutImpl, ddmFormLayout);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return ddmStructureLayoutImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();

		structureLayoutId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		classNameId = objectInput.readLong();
		structureLayoutKey = objectInput.readUTF();

		structureVersionId = objectInput.readLong();
		name = (String)objectInput.readObject();
		description = (String)objectInput.readObject();
		definition = (String)objectInput.readObject();

		ddmFormLayout =
			(com.liferay.dynamic.data.mapping.model.DDMFormLayout)
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

		objectOutput.writeLong(structureLayoutId);

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

		objectOutput.writeLong(classNameId);

		if (structureLayoutKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(structureLayoutKey);
		}

		objectOutput.writeLong(structureVersionId);

		if (name == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(name);
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

		objectOutput.writeObject(ddmFormLayout);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public long structureLayoutId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long classNameId;
	public String structureLayoutKey;
	public long structureVersionId;
	public String name;
	public String description;
	public String definition;
	public volatile com.liferay.dynamic.data.mapping.model.DDMFormLayout
		ddmFormLayout;

	private static final MethodHandle _ddmFormLayoutMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_ddmFormLayoutMethodHandle = lookup.findSetter(
				DDMStructureLayoutImpl.class, "_ddmFormLayout",
				com.liferay.dynamic.data.mapping.model.DDMFormLayout.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}