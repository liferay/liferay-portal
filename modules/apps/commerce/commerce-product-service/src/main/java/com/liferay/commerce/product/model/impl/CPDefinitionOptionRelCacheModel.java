/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
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
 * The cache model class for representing CPDefinitionOptionRel in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPDefinitionOptionRelCacheModel
	implements CacheModel<CPDefinitionOptionRel>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPDefinitionOptionRelCacheModel)) {
			return false;
		}

		CPDefinitionOptionRelCacheModel cpDefinitionOptionRelCacheModel =
			(CPDefinitionOptionRelCacheModel)object;

		if ((CPDefinitionOptionRelId ==
				cpDefinitionOptionRelCacheModel.CPDefinitionOptionRelId) &&
			(mvccVersion == cpDefinitionOptionRelCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, CPDefinitionOptionRelId);

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
		StringBundler sb = new StringBundler(51);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", CPDefinitionOptionRelId=");
		sb.append(CPDefinitionOptionRelId);
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
		sb.append(", CPDefinitionId=");
		sb.append(CPDefinitionId);
		sb.append(", CPOptionId=");
		sb.append(CPOptionId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", commerceOptionTypeKey=");
		sb.append(commerceOptionTypeKey);
		sb.append(", infoItemServiceKey=");
		sb.append(infoItemServiceKey);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", definedExternally=");
		sb.append(definedExternally);
		sb.append(", facetable=");
		sb.append(facetable);
		sb.append(", required=");
		sb.append(required);
		sb.append(", skuContributor=");
		sb.append(skuContributor);
		sb.append(", key=");
		sb.append(key);
		sb.append(", priceType=");
		sb.append(priceType);
		sb.append(", typeSettings=");
		sb.append(typeSettings);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CPDefinitionOptionRel toEntityModel() {
		CPDefinitionOptionRelImpl cpDefinitionOptionRelImpl =
			new CPDefinitionOptionRelImpl();

		cpDefinitionOptionRelImpl.setMvccVersion(mvccVersion);
		cpDefinitionOptionRelImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			cpDefinitionOptionRelImpl.setUuid("");
		}
		else {
			cpDefinitionOptionRelImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			cpDefinitionOptionRelImpl.setExternalReferenceCode("");
		}
		else {
			cpDefinitionOptionRelImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		cpDefinitionOptionRelImpl.setCPDefinitionOptionRelId(
			CPDefinitionOptionRelId);
		cpDefinitionOptionRelImpl.setGroupId(groupId);
		cpDefinitionOptionRelImpl.setCompanyId(companyId);
		cpDefinitionOptionRelImpl.setUserId(userId);

		if (userName == null) {
			cpDefinitionOptionRelImpl.setUserName("");
		}
		else {
			cpDefinitionOptionRelImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			cpDefinitionOptionRelImpl.setCreateDate(null);
		}
		else {
			cpDefinitionOptionRelImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			cpDefinitionOptionRelImpl.setModifiedDate(null);
		}
		else {
			cpDefinitionOptionRelImpl.setModifiedDate(new Date(modifiedDate));
		}

		cpDefinitionOptionRelImpl.setCPDefinitionId(CPDefinitionId);
		cpDefinitionOptionRelImpl.setCPOptionId(CPOptionId);

		if (name == null) {
			cpDefinitionOptionRelImpl.setName("");
		}
		else {
			cpDefinitionOptionRelImpl.setName(name);
		}

		if (description == null) {
			cpDefinitionOptionRelImpl.setDescription("");
		}
		else {
			cpDefinitionOptionRelImpl.setDescription(description);
		}

		if (commerceOptionTypeKey == null) {
			cpDefinitionOptionRelImpl.setCommerceOptionTypeKey("");
		}
		else {
			cpDefinitionOptionRelImpl.setCommerceOptionTypeKey(
				commerceOptionTypeKey);
		}

		if (infoItemServiceKey == null) {
			cpDefinitionOptionRelImpl.setInfoItemServiceKey("");
		}
		else {
			cpDefinitionOptionRelImpl.setInfoItemServiceKey(infoItemServiceKey);
		}

		cpDefinitionOptionRelImpl.setPriority(priority);
		cpDefinitionOptionRelImpl.setDefinedExternally(definedExternally);
		cpDefinitionOptionRelImpl.setFacetable(facetable);
		cpDefinitionOptionRelImpl.setRequired(required);
		cpDefinitionOptionRelImpl.setSkuContributor(skuContributor);

		if (key == null) {
			cpDefinitionOptionRelImpl.setKey("");
		}
		else {
			cpDefinitionOptionRelImpl.setKey(key);
		}

		if (priceType == null) {
			cpDefinitionOptionRelImpl.setPriceType("");
		}
		else {
			cpDefinitionOptionRelImpl.setPriceType(priceType);
		}

		if (typeSettings == null) {
			cpDefinitionOptionRelImpl.setTypeSettings("");
		}
		else {
			cpDefinitionOptionRelImpl.setTypeSettings(typeSettings);
		}

		cpDefinitionOptionRelImpl.resetOriginalValues();

		return cpDefinitionOptionRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		CPDefinitionOptionRelId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		CPDefinitionId = objectInput.readLong();

		CPOptionId = objectInput.readLong();
		name = objectInput.readUTF();
		description = objectInput.readUTF();
		commerceOptionTypeKey = objectInput.readUTF();
		infoItemServiceKey = objectInput.readUTF();

		priority = objectInput.readDouble();

		definedExternally = objectInput.readBoolean();

		facetable = objectInput.readBoolean();

		required = objectInput.readBoolean();

		skuContributor = objectInput.readBoolean();
		key = objectInput.readUTF();
		priceType = objectInput.readUTF();
		typeSettings = (String)objectInput.readObject();
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

		objectOutput.writeLong(CPDefinitionOptionRelId);

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

		objectOutput.writeLong(CPDefinitionId);

		objectOutput.writeLong(CPOptionId);

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

		if (commerceOptionTypeKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(commerceOptionTypeKey);
		}

		if (infoItemServiceKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(infoItemServiceKey);
		}

		objectOutput.writeDouble(priority);

		objectOutput.writeBoolean(definedExternally);

		objectOutput.writeBoolean(facetable);

		objectOutput.writeBoolean(required);

		objectOutput.writeBoolean(skuContributor);

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		if (priceType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(priceType);
		}

		if (typeSettings == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(typeSettings);
		}
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long CPDefinitionOptionRelId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long CPDefinitionId;
	public long CPOptionId;
	public String name;
	public String description;
	public String commerceOptionTypeKey;
	public String infoItemServiceKey;
	public double priority;
	public boolean definedExternally;
	public boolean facetable;
	public boolean required;
	public boolean skuContributor;
	public String key;
	public String priceType;
	public String typeSettings;

}
// LIFERAY-SERVICE-BUILDER-HASH:-681827724