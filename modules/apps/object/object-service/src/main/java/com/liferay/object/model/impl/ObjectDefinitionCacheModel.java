/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.model.ObjectDefinition;
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
 * The cache model class for representing ObjectDefinition in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class ObjectDefinitionCacheModel
	implements CacheModel<ObjectDefinition>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectDefinitionCacheModel)) {
			return false;
		}

		ObjectDefinitionCacheModel objectDefinitionCacheModel =
			(ObjectDefinitionCacheModel)object;

		if ((objectDefinitionId ==
				objectDefinitionCacheModel.objectDefinitionId) &&
			(mvccVersion == objectDefinitionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, objectDefinitionId);

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
		StringBundler sb = new StringBundler(87);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);
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
		sb.append(", accountEntryRestrictedObjectFieldId=");
		sb.append(accountEntryRestrictedObjectFieldId);
		sb.append(", descriptionObjectFieldId=");
		sb.append(descriptionObjectFieldId);
		sb.append(", objectFolderId=");
		sb.append(objectFolderId);
		sb.append(", titleObjectFieldId=");
		sb.append(titleObjectFieldId);
		sb.append(", accountEntryRestricted=");
		sb.append(accountEntryRestricted);
		sb.append(", active=");
		sb.append(active);
		sb.append(", className=");
		sb.append(className);
		sb.append(", dbTableName=");
		sb.append(dbTableName);
		sb.append(", enableCategorization=");
		sb.append(enableCategorization);
		sb.append(", enableComments=");
		sb.append(enableComments);
		sb.append(", enableFormContainer=");
		sb.append(enableFormContainer);
		sb.append(", enableFriendlyURLCustomization=");
		sb.append(enableFriendlyURLCustomization);
		sb.append(", enableIndexSearch=");
		sb.append(enableIndexSearch);
		sb.append(", enableLocalization=");
		sb.append(enableLocalization);
		sb.append(", enableObjectEntryDraft=");
		sb.append(enableObjectEntryDraft);
		sb.append(", enableObjectEntryHistory=");
		sb.append(enableObjectEntryHistory);
		sb.append(", enableObjectEntrySchedule=");
		sb.append(enableObjectEntrySchedule);
		sb.append(", enableObjectEntrySubscription=");
		sb.append(enableObjectEntrySubscription);
		sb.append(", enableObjectEntryVersioning=");
		sb.append(enableObjectEntryVersioning);
		sb.append(", friendlyURLSeparator=");
		sb.append(friendlyURLSeparator);
		sb.append(", label=");
		sb.append(label);
		sb.append(", modifiable=");
		sb.append(modifiable);
		sb.append(", name=");
		sb.append(name);
		sb.append(", panelAppOrder=");
		sb.append(panelAppOrder);
		sb.append(", panelCategoryKey=");
		sb.append(panelCategoryKey);
		sb.append(", pkObjectFieldDBColumnName=");
		sb.append(pkObjectFieldDBColumnName);
		sb.append(", pkObjectFieldName=");
		sb.append(pkObjectFieldName);
		sb.append(", pluralLabel=");
		sb.append(pluralLabel);
		sb.append(", portlet=");
		sb.append(portlet);
		sb.append(", scope=");
		sb.append(scope);
		sb.append(", storageType=");
		sb.append(storageType);
		sb.append(", system=");
		sb.append(system);
		sb.append(", version=");
		sb.append(version);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ObjectDefinition toEntityModel() {
		ObjectDefinitionImpl objectDefinitionImpl = new ObjectDefinitionImpl();

		objectDefinitionImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			objectDefinitionImpl.setUuid("");
		}
		else {
			objectDefinitionImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			objectDefinitionImpl.setExternalReferenceCode("");
		}
		else {
			objectDefinitionImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		objectDefinitionImpl.setObjectDefinitionId(objectDefinitionId);
		objectDefinitionImpl.setCompanyId(companyId);
		objectDefinitionImpl.setUserId(userId);

		if (userName == null) {
			objectDefinitionImpl.setUserName("");
		}
		else {
			objectDefinitionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			objectDefinitionImpl.setCreateDate(null);
		}
		else {
			objectDefinitionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			objectDefinitionImpl.setModifiedDate(null);
		}
		else {
			objectDefinitionImpl.setModifiedDate(new Date(modifiedDate));
		}

		objectDefinitionImpl.setAccountEntryRestrictedObjectFieldId(
			accountEntryRestrictedObjectFieldId);
		objectDefinitionImpl.setDescriptionObjectFieldId(
			descriptionObjectFieldId);
		objectDefinitionImpl.setObjectFolderId(objectFolderId);
		objectDefinitionImpl.setTitleObjectFieldId(titleObjectFieldId);
		objectDefinitionImpl.setAccountEntryRestricted(accountEntryRestricted);
		objectDefinitionImpl.setActive(active);

		if (className == null) {
			objectDefinitionImpl.setClassName("");
		}
		else {
			objectDefinitionImpl.setClassName(className);
		}

		if (dbTableName == null) {
			objectDefinitionImpl.setDBTableName("");
		}
		else {
			objectDefinitionImpl.setDBTableName(dbTableName);
		}

		objectDefinitionImpl.setEnableCategorization(enableCategorization);
		objectDefinitionImpl.setEnableComments(enableComments);
		objectDefinitionImpl.setEnableFormContainer(enableFormContainer);
		objectDefinitionImpl.setEnableFriendlyURLCustomization(
			enableFriendlyURLCustomization);
		objectDefinitionImpl.setEnableIndexSearch(enableIndexSearch);
		objectDefinitionImpl.setEnableLocalization(enableLocalization);
		objectDefinitionImpl.setEnableObjectEntryDraft(enableObjectEntryDraft);
		objectDefinitionImpl.setEnableObjectEntryHistory(
			enableObjectEntryHistory);
		objectDefinitionImpl.setEnableObjectEntrySchedule(
			enableObjectEntrySchedule);
		objectDefinitionImpl.setEnableObjectEntrySubscription(
			enableObjectEntrySubscription);
		objectDefinitionImpl.setEnableObjectEntryVersioning(
			enableObjectEntryVersioning);

		if (friendlyURLSeparator == null) {
			objectDefinitionImpl.setFriendlyURLSeparator("");
		}
		else {
			objectDefinitionImpl.setFriendlyURLSeparator(friendlyURLSeparator);
		}

		if (label == null) {
			objectDefinitionImpl.setLabel("");
		}
		else {
			objectDefinitionImpl.setLabel(label);
		}

		objectDefinitionImpl.setModifiable(modifiable);

		if (name == null) {
			objectDefinitionImpl.setName("");
		}
		else {
			objectDefinitionImpl.setName(name);
		}

		if (panelAppOrder == null) {
			objectDefinitionImpl.setPanelAppOrder("");
		}
		else {
			objectDefinitionImpl.setPanelAppOrder(panelAppOrder);
		}

		if (panelCategoryKey == null) {
			objectDefinitionImpl.setPanelCategoryKey("");
		}
		else {
			objectDefinitionImpl.setPanelCategoryKey(panelCategoryKey);
		}

		if (pkObjectFieldDBColumnName == null) {
			objectDefinitionImpl.setPKObjectFieldDBColumnName("");
		}
		else {
			objectDefinitionImpl.setPKObjectFieldDBColumnName(
				pkObjectFieldDBColumnName);
		}

		if (pkObjectFieldName == null) {
			objectDefinitionImpl.setPKObjectFieldName("");
		}
		else {
			objectDefinitionImpl.setPKObjectFieldName(pkObjectFieldName);
		}

		if (pluralLabel == null) {
			objectDefinitionImpl.setPluralLabel("");
		}
		else {
			objectDefinitionImpl.setPluralLabel(pluralLabel);
		}

		objectDefinitionImpl.setPortlet(portlet);

		if (scope == null) {
			objectDefinitionImpl.setScope("");
		}
		else {
			objectDefinitionImpl.setScope(scope);
		}

		if (storageType == null) {
			objectDefinitionImpl.setStorageType("");
		}
		else {
			objectDefinitionImpl.setStorageType(storageType);
		}

		objectDefinitionImpl.setSystem(system);
		objectDefinitionImpl.setVersion(version);
		objectDefinitionImpl.setStatus(status);

		objectDefinitionImpl.resetOriginalValues();

		return objectDefinitionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		objectDefinitionId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		accountEntryRestrictedObjectFieldId = objectInput.readLong();

		descriptionObjectFieldId = objectInput.readLong();

		objectFolderId = objectInput.readLong();

		titleObjectFieldId = objectInput.readLong();

		accountEntryRestricted = objectInput.readBoolean();

		active = objectInput.readBoolean();
		className = objectInput.readUTF();
		dbTableName = objectInput.readUTF();

		enableCategorization = objectInput.readBoolean();

		enableComments = objectInput.readBoolean();

		enableFormContainer = objectInput.readBoolean();

		enableFriendlyURLCustomization = objectInput.readBoolean();

		enableIndexSearch = objectInput.readBoolean();

		enableLocalization = objectInput.readBoolean();

		enableObjectEntryDraft = objectInput.readBoolean();

		enableObjectEntryHistory = objectInput.readBoolean();

		enableObjectEntrySchedule = objectInput.readBoolean();

		enableObjectEntrySubscription = objectInput.readBoolean();

		enableObjectEntryVersioning = objectInput.readBoolean();
		friendlyURLSeparator = objectInput.readUTF();
		label = objectInput.readUTF();

		modifiable = objectInput.readBoolean();
		name = objectInput.readUTF();
		panelAppOrder = objectInput.readUTF();
		panelCategoryKey = objectInput.readUTF();
		pkObjectFieldDBColumnName = objectInput.readUTF();
		pkObjectFieldName = objectInput.readUTF();
		pluralLabel = objectInput.readUTF();

		portlet = objectInput.readBoolean();
		scope = objectInput.readUTF();
		storageType = objectInput.readUTF();

		system = objectInput.readBoolean();

		version = objectInput.readInt();

		status = objectInput.readInt();
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

		objectOutput.writeLong(objectDefinitionId);

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

		objectOutput.writeLong(accountEntryRestrictedObjectFieldId);

		objectOutput.writeLong(descriptionObjectFieldId);

		objectOutput.writeLong(objectFolderId);

		objectOutput.writeLong(titleObjectFieldId);

		objectOutput.writeBoolean(accountEntryRestricted);

		objectOutput.writeBoolean(active);

		if (className == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(className);
		}

		if (dbTableName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(dbTableName);
		}

		objectOutput.writeBoolean(enableCategorization);

		objectOutput.writeBoolean(enableComments);

		objectOutput.writeBoolean(enableFormContainer);

		objectOutput.writeBoolean(enableFriendlyURLCustomization);

		objectOutput.writeBoolean(enableIndexSearch);

		objectOutput.writeBoolean(enableLocalization);

		objectOutput.writeBoolean(enableObjectEntryDraft);

		objectOutput.writeBoolean(enableObjectEntryHistory);

		objectOutput.writeBoolean(enableObjectEntrySchedule);

		objectOutput.writeBoolean(enableObjectEntrySubscription);

		objectOutput.writeBoolean(enableObjectEntryVersioning);

		if (friendlyURLSeparator == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(friendlyURLSeparator);
		}

		if (label == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(label);
		}

		objectOutput.writeBoolean(modifiable);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (panelAppOrder == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(panelAppOrder);
		}

		if (panelCategoryKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(panelCategoryKey);
		}

		if (pkObjectFieldDBColumnName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(pkObjectFieldDBColumnName);
		}

		if (pkObjectFieldName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(pkObjectFieldName);
		}

		if (pluralLabel == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(pluralLabel);
		}

		objectOutput.writeBoolean(portlet);

		if (scope == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(scope);
		}

		if (storageType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(storageType);
		}

		objectOutput.writeBoolean(system);

		objectOutput.writeInt(version);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long objectDefinitionId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long accountEntryRestrictedObjectFieldId;
	public long descriptionObjectFieldId;
	public long objectFolderId;
	public long titleObjectFieldId;
	public boolean accountEntryRestricted;
	public boolean active;
	public String className;
	public String dbTableName;
	public boolean enableCategorization;
	public boolean enableComments;
	public boolean enableFormContainer;
	public boolean enableFriendlyURLCustomization;
	public boolean enableIndexSearch;
	public boolean enableLocalization;
	public boolean enableObjectEntryDraft;
	public boolean enableObjectEntryHistory;
	public boolean enableObjectEntrySchedule;
	public boolean enableObjectEntrySubscription;
	public boolean enableObjectEntryVersioning;
	public String friendlyURLSeparator;
	public String label;
	public boolean modifiable;
	public String name;
	public String panelAppOrder;
	public String panelCategoryKey;
	public String pkObjectFieldDBColumnName;
	public String pkObjectFieldName;
	public String pluralLabel;
	public boolean portlet;
	public String scope;
	public String storageType;
	public boolean system;
	public int version;
	public int status;

}