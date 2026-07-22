/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPMeasurementUnit;
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
 * The cache model class for representing CPMeasurementUnit in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPMeasurementUnitCacheModel
	implements CacheModel<CPMeasurementUnit>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPMeasurementUnitCacheModel)) {
			return false;
		}

		CPMeasurementUnitCacheModel cpMeasurementUnitCacheModel =
			(CPMeasurementUnitCacheModel)object;

		if ((CPMeasurementUnitId ==
				cpMeasurementUnitCacheModel.CPMeasurementUnitId) &&
			(mvccVersion == cpMeasurementUnitCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, CPMeasurementUnitId);

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
		StringBundler sb = new StringBundler(39);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", CPMeasurementUnitId=");
		sb.append(CPMeasurementUnitId);
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
		sb.append(", name=");
		sb.append(name);
		sb.append(", key=");
		sb.append(key);
		sb.append(", rate=");
		sb.append(rate);
		sb.append(", primary=");
		sb.append(primary);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", type=");
		sb.append(type);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CPMeasurementUnit toEntityModel() {
		CPMeasurementUnitImpl cpMeasurementUnitImpl =
			new CPMeasurementUnitImpl();

		cpMeasurementUnitImpl.setMvccVersion(mvccVersion);
		cpMeasurementUnitImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			cpMeasurementUnitImpl.setUuid("");
		}
		else {
			cpMeasurementUnitImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			cpMeasurementUnitImpl.setExternalReferenceCode("");
		}
		else {
			cpMeasurementUnitImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		cpMeasurementUnitImpl.setCPMeasurementUnitId(CPMeasurementUnitId);
		cpMeasurementUnitImpl.setGroupId(groupId);
		cpMeasurementUnitImpl.setCompanyId(companyId);
		cpMeasurementUnitImpl.setUserId(userId);

		if (userName == null) {
			cpMeasurementUnitImpl.setUserName("");
		}
		else {
			cpMeasurementUnitImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			cpMeasurementUnitImpl.setCreateDate(null);
		}
		else {
			cpMeasurementUnitImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			cpMeasurementUnitImpl.setModifiedDate(null);
		}
		else {
			cpMeasurementUnitImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (name == null) {
			cpMeasurementUnitImpl.setName("");
		}
		else {
			cpMeasurementUnitImpl.setName(name);
		}

		if (key == null) {
			cpMeasurementUnitImpl.setKey("");
		}
		else {
			cpMeasurementUnitImpl.setKey(key);
		}

		cpMeasurementUnitImpl.setRate(rate);
		cpMeasurementUnitImpl.setPrimary(primary);
		cpMeasurementUnitImpl.setPriority(priority);
		cpMeasurementUnitImpl.setType(type);

		if (lastPublishDate == Long.MIN_VALUE) {
			cpMeasurementUnitImpl.setLastPublishDate(null);
		}
		else {
			cpMeasurementUnitImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		cpMeasurementUnitImpl.setStatus(status);

		cpMeasurementUnitImpl.resetOriginalValues();

		return cpMeasurementUnitImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		CPMeasurementUnitId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		name = objectInput.readUTF();
		key = objectInput.readUTF();

		rate = objectInput.readDouble();

		primary = objectInput.readBoolean();

		priority = objectInput.readDouble();

		type = objectInput.readInt();
		lastPublishDate = objectInput.readLong();

		status = objectInput.readInt();
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

		objectOutput.writeLong(CPMeasurementUnitId);

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

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		objectOutput.writeDouble(rate);

		objectOutput.writeBoolean(primary);

		objectOutput.writeDouble(priority);

		objectOutput.writeInt(type);
		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long CPMeasurementUnitId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String name;
	public String key;
	public double rate;
	public boolean primary;
	public double priority;
	public int type;
	public long lastPublishDate;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1273195270