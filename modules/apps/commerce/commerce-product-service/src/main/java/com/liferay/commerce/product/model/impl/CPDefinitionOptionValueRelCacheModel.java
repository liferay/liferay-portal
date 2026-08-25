/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.math.BigDecimal;

import java.util.Date;

/**
 * The cache model class for representing CPDefinitionOptionValueRel in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPDefinitionOptionValueRelCacheModel
	implements CacheModel<CPDefinitionOptionValueRel>, Externalizable,
			   MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPDefinitionOptionValueRelCacheModel)) {
			return false;
		}

		CPDefinitionOptionValueRelCacheModel
			cpDefinitionOptionValueRelCacheModel =
				(CPDefinitionOptionValueRelCacheModel)object;

		if ((CPDefinitionOptionValueRelId ==
				cpDefinitionOptionValueRelCacheModel.
					CPDefinitionOptionValueRelId) &&
			(mvccVersion == cpDefinitionOptionValueRelCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, CPDefinitionOptionValueRelId);

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
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", CPDefinitionOptionValueRelId=");
		sb.append(CPDefinitionOptionValueRelId);
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
		sb.append(", CPDefinitionOptionRelId=");
		sb.append(CPDefinitionOptionRelId);
		sb.append(", CPInstanceUuid=");
		sb.append(CPInstanceUuid);
		sb.append(", CProductId=");
		sb.append(CProductId);
		sb.append(", key=");
		sb.append(key);
		sb.append(", name=");
		sb.append(name);
		sb.append(", preselected=");
		sb.append(preselected);
		sb.append(", price=");
		sb.append(price);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", quantity=");
		sb.append(quantity);
		sb.append(", unitOfMeasureKey=");
		sb.append(unitOfMeasureKey);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CPDefinitionOptionValueRel toEntityModel() {
		CPDefinitionOptionValueRelImpl cpDefinitionOptionValueRelImpl =
			new CPDefinitionOptionValueRelImpl();

		cpDefinitionOptionValueRelImpl.setMvccVersion(mvccVersion);
		cpDefinitionOptionValueRelImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			cpDefinitionOptionValueRelImpl.setUuid("");
		}
		else {
			cpDefinitionOptionValueRelImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			cpDefinitionOptionValueRelImpl.setExternalReferenceCode("");
		}
		else {
			cpDefinitionOptionValueRelImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		cpDefinitionOptionValueRelImpl.setCPDefinitionOptionValueRelId(
			CPDefinitionOptionValueRelId);
		cpDefinitionOptionValueRelImpl.setGroupId(groupId);
		cpDefinitionOptionValueRelImpl.setCompanyId(companyId);
		cpDefinitionOptionValueRelImpl.setUserId(userId);

		if (userName == null) {
			cpDefinitionOptionValueRelImpl.setUserName("");
		}
		else {
			cpDefinitionOptionValueRelImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			cpDefinitionOptionValueRelImpl.setCreateDate(null);
		}
		else {
			cpDefinitionOptionValueRelImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			cpDefinitionOptionValueRelImpl.setModifiedDate(null);
		}
		else {
			cpDefinitionOptionValueRelImpl.setModifiedDate(
				new Date(modifiedDate));
		}

		cpDefinitionOptionValueRelImpl.setCPDefinitionOptionRelId(
			CPDefinitionOptionRelId);

		if (CPInstanceUuid == null) {
			cpDefinitionOptionValueRelImpl.setCPInstanceUuid("");
		}
		else {
			cpDefinitionOptionValueRelImpl.setCPInstanceUuid(CPInstanceUuid);
		}

		cpDefinitionOptionValueRelImpl.setCProductId(CProductId);

		if (key == null) {
			cpDefinitionOptionValueRelImpl.setKey("");
		}
		else {
			cpDefinitionOptionValueRelImpl.setKey(key);
		}

		if (name == null) {
			cpDefinitionOptionValueRelImpl.setName("");
		}
		else {
			cpDefinitionOptionValueRelImpl.setName(name);
		}

		cpDefinitionOptionValueRelImpl.setPreselected(preselected);
		cpDefinitionOptionValueRelImpl.setPrice(price);
		cpDefinitionOptionValueRelImpl.setPriority(priority);
		cpDefinitionOptionValueRelImpl.setQuantity(quantity);

		if (unitOfMeasureKey == null) {
			cpDefinitionOptionValueRelImpl.setUnitOfMeasureKey("");
		}
		else {
			cpDefinitionOptionValueRelImpl.setUnitOfMeasureKey(
				unitOfMeasureKey);
		}

		cpDefinitionOptionValueRelImpl.resetOriginalValues();

		return cpDefinitionOptionValueRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		CPDefinitionOptionValueRelId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		CPDefinitionOptionRelId = objectInput.readLong();
		CPInstanceUuid = objectInput.readUTF();

		CProductId = objectInput.readLong();
		key = objectInput.readUTF();
		name = objectInput.readUTF();

		preselected = objectInput.readBoolean();
		price = (BigDecimal)objectInput.readObject();

		priority = objectInput.readDouble();
		quantity = (BigDecimal)objectInput.readObject();
		unitOfMeasureKey = objectInput.readUTF();
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

		objectOutput.writeLong(CPDefinitionOptionValueRelId);

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

		objectOutput.writeLong(CPDefinitionOptionRelId);

		if (CPInstanceUuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(CPInstanceUuid);
		}

		objectOutput.writeLong(CProductId);

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeBoolean(preselected);
		objectOutput.writeObject(price);

		objectOutput.writeDouble(priority);
		objectOutput.writeObject(quantity);

		if (unitOfMeasureKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(unitOfMeasureKey);
		}
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long CPDefinitionOptionValueRelId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long CPDefinitionOptionRelId;
	public String CPInstanceUuid;
	public long CProductId;
	public String key;
	public String name;
	public boolean preselected;
	public BigDecimal price;
	public double priority;
	public BigDecimal quantity;
	public String unitOfMeasureKey;

}
// LIFERAY-SERVICE-BUILDER-HASH:-466523228