/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model.impl;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
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
 * The cache model class for representing LayoutPageTemplateEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutPageTemplateEntryCacheModel
	implements CacheModel<LayoutPageTemplateEntry>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LayoutPageTemplateEntryCacheModel)) {
			return false;
		}

		LayoutPageTemplateEntryCacheModel layoutPageTemplateEntryCacheModel =
			(LayoutPageTemplateEntryCacheModel)object;

		if ((layoutPageTemplateEntryId ==
				layoutPageTemplateEntryCacheModel.layoutPageTemplateEntryId) &&
			(mvccVersion == layoutPageTemplateEntryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, layoutPageTemplateEntryId);

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
		StringBundler sb = new StringBundler(55);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", layoutPageTemplateEntryId=");
		sb.append(layoutPageTemplateEntryId);
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
		sb.append(", layoutPageTemplateCollectionId=");
		sb.append(layoutPageTemplateCollectionId);
		sb.append(", layoutPageTemplateEntryKey=");
		sb.append(layoutPageTemplateEntryKey);
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", classTypeId=");
		sb.append(classTypeId);
		sb.append(", classTypeKey=");
		sb.append(classTypeKey);
		sb.append(", name=");
		sb.append(name);
		sb.append(", type=");
		sb.append(type);
		sb.append(", previewFileEntryId=");
		sb.append(previewFileEntryId);
		sb.append(", defaultTemplate=");
		sb.append(defaultTemplate);
		sb.append(", layoutPrototypeId=");
		sb.append(layoutPrototypeId);
		sb.append(", plid=");
		sb.append(plid);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append(", status=");
		sb.append(status);
		sb.append(", statusByUserId=");
		sb.append(statusByUserId);
		sb.append(", statusByUserName=");
		sb.append(statusByUserName);
		sb.append(", statusDate=");
		sb.append(statusDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public LayoutPageTemplateEntry toEntityModel() {
		LayoutPageTemplateEntryImpl layoutPageTemplateEntryImpl =
			new LayoutPageTemplateEntryImpl();

		layoutPageTemplateEntryImpl.setMvccVersion(mvccVersion);
		layoutPageTemplateEntryImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			layoutPageTemplateEntryImpl.setUuid("");
		}
		else {
			layoutPageTemplateEntryImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			layoutPageTemplateEntryImpl.setExternalReferenceCode("");
		}
		else {
			layoutPageTemplateEntryImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		layoutPageTemplateEntryImpl.setLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId);
		layoutPageTemplateEntryImpl.setGroupId(groupId);
		layoutPageTemplateEntryImpl.setCompanyId(companyId);
		layoutPageTemplateEntryImpl.setUserId(userId);

		if (userName == null) {
			layoutPageTemplateEntryImpl.setUserName("");
		}
		else {
			layoutPageTemplateEntryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			layoutPageTemplateEntryImpl.setCreateDate(null);
		}
		else {
			layoutPageTemplateEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			layoutPageTemplateEntryImpl.setModifiedDate(null);
		}
		else {
			layoutPageTemplateEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		layoutPageTemplateEntryImpl.setLayoutPageTemplateCollectionId(
			layoutPageTemplateCollectionId);

		if (layoutPageTemplateEntryKey == null) {
			layoutPageTemplateEntryImpl.setLayoutPageTemplateEntryKey("");
		}
		else {
			layoutPageTemplateEntryImpl.setLayoutPageTemplateEntryKey(
				layoutPageTemplateEntryKey);
		}

		layoutPageTemplateEntryImpl.setClassNameId(classNameId);
		layoutPageTemplateEntryImpl.setClassTypeId(classTypeId);

		if (classTypeKey == null) {
			layoutPageTemplateEntryImpl.setClassTypeKey("");
		}
		else {
			layoutPageTemplateEntryImpl.setClassTypeKey(classTypeKey);
		}

		if (name == null) {
			layoutPageTemplateEntryImpl.setName("");
		}
		else {
			layoutPageTemplateEntryImpl.setName(name);
		}

		layoutPageTemplateEntryImpl.setType(type);
		layoutPageTemplateEntryImpl.setPreviewFileEntryId(previewFileEntryId);
		layoutPageTemplateEntryImpl.setDefaultTemplate(defaultTemplate);
		layoutPageTemplateEntryImpl.setLayoutPrototypeId(layoutPrototypeId);
		layoutPageTemplateEntryImpl.setPlid(plid);

		if (lastPublishDate == Long.MIN_VALUE) {
			layoutPageTemplateEntryImpl.setLastPublishDate(null);
		}
		else {
			layoutPageTemplateEntryImpl.setLastPublishDate(
				new Date(lastPublishDate));
		}

		layoutPageTemplateEntryImpl.setStatus(status);
		layoutPageTemplateEntryImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			layoutPageTemplateEntryImpl.setStatusByUserName("");
		}
		else {
			layoutPageTemplateEntryImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			layoutPageTemplateEntryImpl.setStatusDate(null);
		}
		else {
			layoutPageTemplateEntryImpl.setStatusDate(new Date(statusDate));
		}

		layoutPageTemplateEntryImpl.resetOriginalValues();

		return layoutPageTemplateEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		layoutPageTemplateEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		layoutPageTemplateCollectionId = objectInput.readLong();
		layoutPageTemplateEntryKey = objectInput.readUTF();

		classNameId = objectInput.readLong();

		classTypeId = objectInput.readLong();
		classTypeKey = objectInput.readUTF();
		name = objectInput.readUTF();

		type = objectInput.readInt();

		previewFileEntryId = objectInput.readLong();

		defaultTemplate = objectInput.readBoolean();

		layoutPrototypeId = objectInput.readLong();

		plid = objectInput.readLong();
		lastPublishDate = objectInput.readLong();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
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

		objectOutput.writeLong(layoutPageTemplateEntryId);

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

		objectOutput.writeLong(layoutPageTemplateCollectionId);

		if (layoutPageTemplateEntryKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(layoutPageTemplateEntryKey);
		}

		objectOutput.writeLong(classNameId);

		objectOutput.writeLong(classTypeId);

		if (classTypeKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(classTypeKey);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeInt(type);

		objectOutput.writeLong(previewFileEntryId);

		objectOutput.writeBoolean(defaultTemplate);

		objectOutput.writeLong(layoutPrototypeId);

		objectOutput.writeLong(plid);
		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeInt(status);

		objectOutput.writeLong(statusByUserId);

		if (statusByUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(statusByUserName);
		}

		objectOutput.writeLong(statusDate);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long layoutPageTemplateEntryId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long layoutPageTemplateCollectionId;
	public String layoutPageTemplateEntryKey;
	public long classNameId;
	public long classTypeId;
	public String classTypeKey;
	public String name;
	public int type;
	public long previewFileEntryId;
	public boolean defaultTemplate;
	public long layoutPrototypeId;
	public long plid;
	public long lastPublishDate;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}