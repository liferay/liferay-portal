/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.model.impl;

import com.liferay.layout.model.LayoutClassedModelUsage;
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
 * The cache model class for representing LayoutClassedModelUsage in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutClassedModelUsageCacheModel
	implements CacheModel<LayoutClassedModelUsage>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LayoutClassedModelUsageCacheModel)) {
			return false;
		}

		LayoutClassedModelUsageCacheModel layoutClassedModelUsageCacheModel =
			(LayoutClassedModelUsageCacheModel)object;

		if ((layoutClassedModelUsageId ==
				layoutClassedModelUsageCacheModel.layoutClassedModelUsageId) &&
			(mvccVersion == layoutClassedModelUsageCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, layoutClassedModelUsageId);

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
		sb.append(", layoutClassedModelUsageId=");
		sb.append(layoutClassedModelUsageId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", classExternalReferenceCode=");
		sb.append(classExternalReferenceCode);
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", classPK=");
		sb.append(classPK);
		sb.append(", containerKey=");
		sb.append(containerKey);
		sb.append(", containerType=");
		sb.append(containerType);
		sb.append(", plid=");
		sb.append(plid);
		sb.append(", type=");
		sb.append(type);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public LayoutClassedModelUsage toEntityModel() {
		LayoutClassedModelUsageImpl layoutClassedModelUsageImpl =
			new LayoutClassedModelUsageImpl();

		layoutClassedModelUsageImpl.setMvccVersion(mvccVersion);
		layoutClassedModelUsageImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			layoutClassedModelUsageImpl.setUuid("");
		}
		else {
			layoutClassedModelUsageImpl.setUuid(uuid);
		}

		layoutClassedModelUsageImpl.setLayoutClassedModelUsageId(
			layoutClassedModelUsageId);
		layoutClassedModelUsageImpl.setGroupId(groupId);
		layoutClassedModelUsageImpl.setCompanyId(companyId);

		if (createDate == Long.MIN_VALUE) {
			layoutClassedModelUsageImpl.setCreateDate(null);
		}
		else {
			layoutClassedModelUsageImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			layoutClassedModelUsageImpl.setModifiedDate(null);
		}
		else {
			layoutClassedModelUsageImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (classExternalReferenceCode == null) {
			layoutClassedModelUsageImpl.setClassExternalReferenceCode("");
		}
		else {
			layoutClassedModelUsageImpl.setClassExternalReferenceCode(
				classExternalReferenceCode);
		}

		layoutClassedModelUsageImpl.setClassNameId(classNameId);
		layoutClassedModelUsageImpl.setClassPK(classPK);

		if (containerKey == null) {
			layoutClassedModelUsageImpl.setContainerKey("");
		}
		else {
			layoutClassedModelUsageImpl.setContainerKey(containerKey);
		}

		layoutClassedModelUsageImpl.setContainerType(containerType);
		layoutClassedModelUsageImpl.setPlid(plid);
		layoutClassedModelUsageImpl.setType(type);

		if (lastPublishDate == Long.MIN_VALUE) {
			layoutClassedModelUsageImpl.setLastPublishDate(null);
		}
		else {
			layoutClassedModelUsageImpl.setLastPublishDate(
				new Date(lastPublishDate));
		}

		layoutClassedModelUsageImpl.resetOriginalValues();

		return layoutClassedModelUsageImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();

		layoutClassedModelUsageId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		classExternalReferenceCode = objectInput.readUTF();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();
		containerKey = objectInput.readUTF();

		containerType = objectInput.readLong();

		plid = objectInput.readLong();

		type = objectInput.readInt();
		lastPublishDate = objectInput.readLong();
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

		objectOutput.writeLong(layoutClassedModelUsageId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);
		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (classExternalReferenceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(classExternalReferenceCode);
		}

		objectOutput.writeLong(classNameId);

		objectOutput.writeLong(classPK);

		if (containerKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(containerKey);
		}

		objectOutput.writeLong(containerType);

		objectOutput.writeLong(plid);

		objectOutput.writeInt(type);
		objectOutput.writeLong(lastPublishDate);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public long layoutClassedModelUsageId;
	public long groupId;
	public long companyId;
	public long createDate;
	public long modifiedDate;
	public String classExternalReferenceCode;
	public long classNameId;
	public long classPK;
	public String containerKey;
	public long containerType;
	public long plid;
	public int type;
	public long lastPublishDate;

}