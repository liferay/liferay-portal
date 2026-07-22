/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPOptionValue;
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
 * The cache model class for representing CPOptionValue in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPOptionValueCacheModel
	implements CacheModel<CPOptionValue>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPOptionValueCacheModel)) {
			return false;
		}

		CPOptionValueCacheModel cpOptionValueCacheModel =
			(CPOptionValueCacheModel)object;

		if ((CPOptionValueId == cpOptionValueCacheModel.CPOptionValueId) &&
			(mvccVersion == cpOptionValueCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, CPOptionValueId);

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
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", CPOptionValueId=");
		sb.append(CPOptionValueId);
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
		sb.append(", CPOptionId=");
		sb.append(CPOptionId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", key=");
		sb.append(key);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CPOptionValue toEntityModel() {
		CPOptionValueImpl cpOptionValueImpl = new CPOptionValueImpl();

		cpOptionValueImpl.setMvccVersion(mvccVersion);
		cpOptionValueImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			cpOptionValueImpl.setUuid("");
		}
		else {
			cpOptionValueImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			cpOptionValueImpl.setExternalReferenceCode("");
		}
		else {
			cpOptionValueImpl.setExternalReferenceCode(externalReferenceCode);
		}

		cpOptionValueImpl.setCPOptionValueId(CPOptionValueId);
		cpOptionValueImpl.setCompanyId(companyId);
		cpOptionValueImpl.setUserId(userId);

		if (userName == null) {
			cpOptionValueImpl.setUserName("");
		}
		else {
			cpOptionValueImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			cpOptionValueImpl.setCreateDate(null);
		}
		else {
			cpOptionValueImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			cpOptionValueImpl.setModifiedDate(null);
		}
		else {
			cpOptionValueImpl.setModifiedDate(new Date(modifiedDate));
		}

		cpOptionValueImpl.setCPOptionId(CPOptionId);

		if (name == null) {
			cpOptionValueImpl.setName("");
		}
		else {
			cpOptionValueImpl.setName(name);
		}

		cpOptionValueImpl.setPriority(priority);

		if (key == null) {
			cpOptionValueImpl.setKey("");
		}
		else {
			cpOptionValueImpl.setKey(key);
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			cpOptionValueImpl.setLastPublishDate(null);
		}
		else {
			cpOptionValueImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		cpOptionValueImpl.setStatus(status);

		cpOptionValueImpl.resetOriginalValues();

		return cpOptionValueImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		CPOptionValueId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		CPOptionId = objectInput.readLong();
		name = objectInput.readUTF();

		priority = objectInput.readDouble();
		key = objectInput.readUTF();
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

		objectOutput.writeLong(CPOptionValueId);

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

		objectOutput.writeLong(CPOptionId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeDouble(priority);

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long CPOptionValueId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long CPOptionId;
	public String name;
	public double priority;
	public String key;
	public long lastPublishDate;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:950902934