/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.model.ObjectView;
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
 * The cache model class for representing ObjectView in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class ObjectViewCacheModel
	implements CacheModel<ObjectView>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectViewCacheModel)) {
			return false;
		}

		ObjectViewCacheModel objectViewCacheModel =
			(ObjectViewCacheModel)object;

		if ((objectViewId == objectViewCacheModel.objectViewId) &&
			(mvccVersion == objectViewCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, objectViewId);

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
		StringBundler sb = new StringBundler(25);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", objectViewId=");
		sb.append(objectViewId);
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
		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);
		sb.append(", defaultObjectView=");
		sb.append(defaultObjectView);
		sb.append(", name=");
		sb.append(name);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ObjectView toEntityModel() {
		ObjectViewImpl objectViewImpl = new ObjectViewImpl();

		objectViewImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			objectViewImpl.setUuid("");
		}
		else {
			objectViewImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			objectViewImpl.setExternalReferenceCode("");
		}
		else {
			objectViewImpl.setExternalReferenceCode(externalReferenceCode);
		}

		objectViewImpl.setObjectViewId(objectViewId);
		objectViewImpl.setCompanyId(companyId);
		objectViewImpl.setUserId(userId);

		if (userName == null) {
			objectViewImpl.setUserName("");
		}
		else {
			objectViewImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			objectViewImpl.setCreateDate(null);
		}
		else {
			objectViewImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			objectViewImpl.setModifiedDate(null);
		}
		else {
			objectViewImpl.setModifiedDate(new Date(modifiedDate));
		}

		objectViewImpl.setObjectDefinitionId(objectDefinitionId);
		objectViewImpl.setDefaultObjectView(defaultObjectView);

		if (name == null) {
			objectViewImpl.setName("");
		}
		else {
			objectViewImpl.setName(name);
		}

		objectViewImpl.resetOriginalValues();

		return objectViewImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		objectViewId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		objectDefinitionId = objectInput.readLong();

		defaultObjectView = objectInput.readBoolean();
		name = objectInput.readUTF();
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

		objectOutput.writeLong(objectViewId);

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

		objectOutput.writeLong(objectDefinitionId);

		objectOutput.writeBoolean(defaultObjectView);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long objectViewId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long objectDefinitionId;
	public boolean defaultObjectView;
	public String name;

}
// LIFERAY-SERVICE-BUILDER-HASH:1244268228