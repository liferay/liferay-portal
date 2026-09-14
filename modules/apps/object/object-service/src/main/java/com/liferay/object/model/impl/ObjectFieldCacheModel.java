/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.model.ObjectField;
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
 * The cache model class for representing ObjectField in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class ObjectFieldCacheModel
	implements CacheModel<ObjectField>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectFieldCacheModel)) {
			return false;
		}

		ObjectFieldCacheModel objectFieldCacheModel =
			(ObjectFieldCacheModel)object;

		if ((objectFieldId == objectFieldCacheModel.objectFieldId) &&
			(mvccVersion == objectFieldCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, objectFieldId);

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
		StringBundler sb = new StringBundler(57);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", objectFieldId=");
		sb.append(objectFieldId);
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
		sb.append(", listTypeDefinitionId=");
		sb.append(listTypeDefinitionId);
		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);
		sb.append(", businessType=");
		sb.append(businessType);
		sb.append(", dbColumnName=");
		sb.append(dbColumnName);
		sb.append(", dbTableName=");
		sb.append(dbTableName);
		sb.append(", dbType=");
		sb.append(dbType);
		sb.append(", description=");
		sb.append(description);
		sb.append(", indexed=");
		sb.append(indexed);
		sb.append(", indexedAsKeyword=");
		sb.append(indexedAsKeyword);
		sb.append(", indexedLanguageId=");
		sb.append(indexedLanguageId);
		sb.append(", label=");
		sb.append(label);
		sb.append(", localized=");
		sb.append(localized);
		sb.append(", name=");
		sb.append(name);
		sb.append(", readOnly=");
		sb.append(readOnly);
		sb.append(", readOnlyConditionExpression=");
		sb.append(readOnlyConditionExpression);
		sb.append(", relationshipType=");
		sb.append(relationshipType);
		sb.append(", required=");
		sb.append(required);
		sb.append(", state=");
		sb.append(state);
		sb.append(", system=");
		sb.append(system);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ObjectField toEntityModel() {
		ObjectFieldImpl objectFieldImpl = new ObjectFieldImpl();

		objectFieldImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			objectFieldImpl.setUuid("");
		}
		else {
			objectFieldImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			objectFieldImpl.setExternalReferenceCode("");
		}
		else {
			objectFieldImpl.setExternalReferenceCode(externalReferenceCode);
		}

		objectFieldImpl.setObjectFieldId(objectFieldId);
		objectFieldImpl.setCompanyId(companyId);
		objectFieldImpl.setUserId(userId);

		if (userName == null) {
			objectFieldImpl.setUserName("");
		}
		else {
			objectFieldImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			objectFieldImpl.setCreateDate(null);
		}
		else {
			objectFieldImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			objectFieldImpl.setModifiedDate(null);
		}
		else {
			objectFieldImpl.setModifiedDate(new Date(modifiedDate));
		}

		objectFieldImpl.setListTypeDefinitionId(listTypeDefinitionId);
		objectFieldImpl.setObjectDefinitionId(objectDefinitionId);

		if (businessType == null) {
			objectFieldImpl.setBusinessType("");
		}
		else {
			objectFieldImpl.setBusinessType(businessType);
		}

		if (dbColumnName == null) {
			objectFieldImpl.setDBColumnName("");
		}
		else {
			objectFieldImpl.setDBColumnName(dbColumnName);
		}

		if (dbTableName == null) {
			objectFieldImpl.setDBTableName("");
		}
		else {
			objectFieldImpl.setDBTableName(dbTableName);
		}

		if (dbType == null) {
			objectFieldImpl.setDBType("");
		}
		else {
			objectFieldImpl.setDBType(dbType);
		}

		if (description == null) {
			objectFieldImpl.setDescription("");
		}
		else {
			objectFieldImpl.setDescription(description);
		}

		objectFieldImpl.setIndexed(indexed);
		objectFieldImpl.setIndexedAsKeyword(indexedAsKeyword);

		if (indexedLanguageId == null) {
			objectFieldImpl.setIndexedLanguageId("");
		}
		else {
			objectFieldImpl.setIndexedLanguageId(indexedLanguageId);
		}

		if (label == null) {
			objectFieldImpl.setLabel("");
		}
		else {
			objectFieldImpl.setLabel(label);
		}

		objectFieldImpl.setLocalized(localized);

		if (name == null) {
			objectFieldImpl.setName("");
		}
		else {
			objectFieldImpl.setName(name);
		}

		if (readOnly == null) {
			objectFieldImpl.setReadOnly("");
		}
		else {
			objectFieldImpl.setReadOnly(readOnly);
		}

		if (readOnlyConditionExpression == null) {
			objectFieldImpl.setReadOnlyConditionExpression("");
		}
		else {
			objectFieldImpl.setReadOnlyConditionExpression(
				readOnlyConditionExpression);
		}

		if (relationshipType == null) {
			objectFieldImpl.setRelationshipType("");
		}
		else {
			objectFieldImpl.setRelationshipType(relationshipType);
		}

		objectFieldImpl.setRequired(required);
		objectFieldImpl.setState(state);
		objectFieldImpl.setSystem(system);

		objectFieldImpl.resetOriginalValues();

		return objectFieldImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		objectFieldId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		listTypeDefinitionId = objectInput.readLong();

		objectDefinitionId = objectInput.readLong();
		businessType = objectInput.readUTF();
		dbColumnName = objectInput.readUTF();
		dbTableName = objectInput.readUTF();
		dbType = objectInput.readUTF();
		description = objectInput.readUTF();

		indexed = objectInput.readBoolean();

		indexedAsKeyword = objectInput.readBoolean();
		indexedLanguageId = objectInput.readUTF();
		label = objectInput.readUTF();

		localized = objectInput.readBoolean();
		name = objectInput.readUTF();
		readOnly = objectInput.readUTF();
		readOnlyConditionExpression = (String)objectInput.readObject();
		relationshipType = objectInput.readUTF();

		required = objectInput.readBoolean();

		state = objectInput.readBoolean();

		system = objectInput.readBoolean();
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

		objectOutput.writeLong(objectFieldId);

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

		objectOutput.writeLong(listTypeDefinitionId);

		objectOutput.writeLong(objectDefinitionId);

		if (businessType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(businessType);
		}

		if (dbColumnName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(dbColumnName);
		}

		if (dbTableName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(dbTableName);
		}

		if (dbType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(dbType);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		objectOutput.writeBoolean(indexed);

		objectOutput.writeBoolean(indexedAsKeyword);

		if (indexedLanguageId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(indexedLanguageId);
		}

		if (label == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(label);
		}

		objectOutput.writeBoolean(localized);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (readOnly == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(readOnly);
		}

		if (readOnlyConditionExpression == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(readOnlyConditionExpression);
		}

		if (relationshipType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(relationshipType);
		}

		objectOutput.writeBoolean(required);

		objectOutput.writeBoolean(state);

		objectOutput.writeBoolean(system);
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long objectFieldId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long listTypeDefinitionId;
	public long objectDefinitionId;
	public String businessType;
	public String dbColumnName;
	public String dbTableName;
	public String dbType;
	public String description;
	public boolean indexed;
	public boolean indexedAsKeyword;
	public String indexedLanguageId;
	public String label;
	public boolean localized;
	public String name;
	public String readOnly;
	public String readOnlyConditionExpression;
	public String relationshipType;
	public boolean required;
	public boolean state;
	public boolean system;

}
// LIFERAY-SERVICE-BUILDER-HASH:1003040820