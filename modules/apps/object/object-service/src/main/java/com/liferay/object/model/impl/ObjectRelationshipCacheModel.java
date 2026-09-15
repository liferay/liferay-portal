/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.model.ObjectRelationship;
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
 * The cache model class for representing ObjectRelationship in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class ObjectRelationshipCacheModel
	implements CacheModel<ObjectRelationship>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectRelationshipCacheModel)) {
			return false;
		}

		ObjectRelationshipCacheModel objectRelationshipCacheModel =
			(ObjectRelationshipCacheModel)object;

		if ((objectRelationshipId ==
				objectRelationshipCacheModel.objectRelationshipId) &&
			(mvccVersion == objectRelationshipCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, objectRelationshipId);

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
		StringBundler sb = new StringBundler(45);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", objectRelationshipId=");
		sb.append(objectRelationshipId);
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
		sb.append(", objectDefinitionId1=");
		sb.append(objectDefinitionId1);
		sb.append(", objectDefinitionId2=");
		sb.append(objectDefinitionId2);
		sb.append(", objectFieldId2=");
		sb.append(objectFieldId2);
		sb.append(", parameterObjectFieldId=");
		sb.append(parameterObjectFieldId);
		sb.append(", deletionType=");
		sb.append(deletionType);
		sb.append(", dbTableName=");
		sb.append(dbTableName);
		sb.append(", description=");
		sb.append(description);
		sb.append(", edge=");
		sb.append(edge);
		sb.append(", label=");
		sb.append(label);
		sb.append(", name=");
		sb.append(name);
		sb.append(", reverse=");
		sb.append(reverse);
		sb.append(", system=");
		sb.append(system);
		sb.append(", type=");
		sb.append(type);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ObjectRelationship toEntityModel() {
		ObjectRelationshipImpl objectRelationshipImpl =
			new ObjectRelationshipImpl();

		objectRelationshipImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			objectRelationshipImpl.setUuid("");
		}
		else {
			objectRelationshipImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			objectRelationshipImpl.setExternalReferenceCode("");
		}
		else {
			objectRelationshipImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		objectRelationshipImpl.setObjectRelationshipId(objectRelationshipId);
		objectRelationshipImpl.setCompanyId(companyId);
		objectRelationshipImpl.setUserId(userId);

		if (userName == null) {
			objectRelationshipImpl.setUserName("");
		}
		else {
			objectRelationshipImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			objectRelationshipImpl.setCreateDate(null);
		}
		else {
			objectRelationshipImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			objectRelationshipImpl.setModifiedDate(null);
		}
		else {
			objectRelationshipImpl.setModifiedDate(new Date(modifiedDate));
		}

		objectRelationshipImpl.setObjectDefinitionId1(objectDefinitionId1);
		objectRelationshipImpl.setObjectDefinitionId2(objectDefinitionId2);
		objectRelationshipImpl.setObjectFieldId2(objectFieldId2);
		objectRelationshipImpl.setParameterObjectFieldId(
			parameterObjectFieldId);

		if (deletionType == null) {
			objectRelationshipImpl.setDeletionType("");
		}
		else {
			objectRelationshipImpl.setDeletionType(deletionType);
		}

		if (dbTableName == null) {
			objectRelationshipImpl.setDBTableName("");
		}
		else {
			objectRelationshipImpl.setDBTableName(dbTableName);
		}

		if (description == null) {
			objectRelationshipImpl.setDescription("");
		}
		else {
			objectRelationshipImpl.setDescription(description);
		}

		objectRelationshipImpl.setEdge(edge);

		if (label == null) {
			objectRelationshipImpl.setLabel("");
		}
		else {
			objectRelationshipImpl.setLabel(label);
		}

		if (name == null) {
			objectRelationshipImpl.setName("");
		}
		else {
			objectRelationshipImpl.setName(name);
		}

		objectRelationshipImpl.setReverse(reverse);
		objectRelationshipImpl.setSystem(system);

		if (type == null) {
			objectRelationshipImpl.setType("");
		}
		else {
			objectRelationshipImpl.setType(type);
		}

		objectRelationshipImpl.resetOriginalValues();

		return objectRelationshipImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		objectRelationshipId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		objectDefinitionId1 = objectInput.readLong();

		objectDefinitionId2 = objectInput.readLong();

		objectFieldId2 = objectInput.readLong();

		parameterObjectFieldId = objectInput.readLong();
		deletionType = objectInput.readUTF();
		dbTableName = objectInput.readUTF();
		description = objectInput.readUTF();

		edge = objectInput.readBoolean();
		label = objectInput.readUTF();
		name = objectInput.readUTF();

		reverse = objectInput.readBoolean();

		system = objectInput.readBoolean();
		type = objectInput.readUTF();
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

		objectOutput.writeLong(objectRelationshipId);

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

		objectOutput.writeLong(objectDefinitionId1);

		objectOutput.writeLong(objectDefinitionId2);

		objectOutput.writeLong(objectFieldId2);

		objectOutput.writeLong(parameterObjectFieldId);

		if (deletionType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(deletionType);
		}

		if (dbTableName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(dbTableName);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		objectOutput.writeBoolean(edge);

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

		objectOutput.writeBoolean(reverse);

		objectOutput.writeBoolean(system);

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long objectRelationshipId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long objectDefinitionId1;
	public long objectDefinitionId2;
	public long objectFieldId2;
	public long parameterObjectFieldId;
	public String deletionType;
	public String dbTableName;
	public String description;
	public boolean edge;
	public String label;
	public String name;
	public boolean reverse;
	public boolean system;
	public String type;

}
// LIFERAY-SERVICE-BUILDER-HASH:1461971225