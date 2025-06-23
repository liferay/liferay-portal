/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing LayoutSetPrototype in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutSetPrototypeCacheModel
	implements CacheModel<LayoutSetPrototype>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LayoutSetPrototypeCacheModel)) {
			return false;
		}

		LayoutSetPrototypeCacheModel layoutSetPrototypeCacheModel =
			(LayoutSetPrototypeCacheModel)object;

		if ((layoutSetPrototypeId ==
				layoutSetPrototypeCacheModel.layoutSetPrototypeId) &&
			(mvccVersion == layoutSetPrototypeCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, layoutSetPrototypeId);

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
		StringBundler sb = new StringBundler(27);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", layoutSetPrototypeId=");
		sb.append(layoutSetPrototypeId);
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
		sb.append(", description=");
		sb.append(description);
		sb.append(", settings=");
		sb.append(settings);
		sb.append(", active=");
		sb.append(active);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public LayoutSetPrototype toEntityModel() {
		LayoutSetPrototypeImpl layoutSetPrototypeImpl =
			new LayoutSetPrototypeImpl();

		layoutSetPrototypeImpl.setMvccVersion(mvccVersion);
		layoutSetPrototypeImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			layoutSetPrototypeImpl.setUuid("");
		}
		else {
			layoutSetPrototypeImpl.setUuid(uuid);
		}

		layoutSetPrototypeImpl.setLayoutSetPrototypeId(layoutSetPrototypeId);
		layoutSetPrototypeImpl.setCompanyId(companyId);
		layoutSetPrototypeImpl.setUserId(userId);

		if (userName == null) {
			layoutSetPrototypeImpl.setUserName("");
		}
		else {
			layoutSetPrototypeImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			layoutSetPrototypeImpl.setCreateDate(null);
		}
		else {
			layoutSetPrototypeImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			layoutSetPrototypeImpl.setModifiedDate(null);
		}
		else {
			layoutSetPrototypeImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (name == null) {
			layoutSetPrototypeImpl.setName("");
		}
		else {
			layoutSetPrototypeImpl.setName(name);
		}

		if (description == null) {
			layoutSetPrototypeImpl.setDescription("");
		}
		else {
			layoutSetPrototypeImpl.setDescription(description);
		}

		if (settings == null) {
			layoutSetPrototypeImpl.setSettings("");
		}
		else {
			layoutSetPrototypeImpl.setSettings(settings);
		}

		layoutSetPrototypeImpl.setActive(active);

		layoutSetPrototypeImpl.resetOriginalValues();

		return layoutSetPrototypeImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();

		layoutSetPrototypeId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		name = (String)objectInput.readObject();
		description = (String)objectInput.readObject();
		settings = objectInput.readUTF();

		active = objectInput.readBoolean();
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

		objectOutput.writeLong(layoutSetPrototypeId);

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

		if (settings == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(settings);
		}

		objectOutput.writeBoolean(active);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public long layoutSetPrototypeId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String name;
	public String description;
	public String settings;
	public boolean active;

}