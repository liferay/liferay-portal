/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPSpecificationOption;
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
 * The cache model class for representing CPSpecificationOption in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPSpecificationOptionCacheModel
	implements CacheModel<CPSpecificationOption>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPSpecificationOptionCacheModel)) {
			return false;
		}

		CPSpecificationOptionCacheModel cpSpecificationOptionCacheModel =
			(CPSpecificationOptionCacheModel)object;

		if ((CPSpecificationOptionId ==
				cpSpecificationOptionCacheModel.CPSpecificationOptionId) &&
			(mvccVersion == cpSpecificationOptionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, CPSpecificationOptionId);

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
		sb.append(", CPSpecificationOptionId=");
		sb.append(CPSpecificationOptionId);
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
		sb.append(", CPOptionCategoryId=");
		sb.append(CPOptionCategoryId);
		sb.append(", title=");
		sb.append(title);
		sb.append(", description=");
		sb.append(description);
		sb.append(", facetable=");
		sb.append(facetable);
		sb.append(", key=");
		sb.append(key);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", visible=");
		sb.append(visible);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CPSpecificationOption toEntityModel() {
		CPSpecificationOptionImpl cpSpecificationOptionImpl =
			new CPSpecificationOptionImpl();

		cpSpecificationOptionImpl.setMvccVersion(mvccVersion);
		cpSpecificationOptionImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			cpSpecificationOptionImpl.setUuid("");
		}
		else {
			cpSpecificationOptionImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			cpSpecificationOptionImpl.setExternalReferenceCode("");
		}
		else {
			cpSpecificationOptionImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		cpSpecificationOptionImpl.setCPSpecificationOptionId(
			CPSpecificationOptionId);
		cpSpecificationOptionImpl.setCompanyId(companyId);
		cpSpecificationOptionImpl.setUserId(userId);

		if (userName == null) {
			cpSpecificationOptionImpl.setUserName("");
		}
		else {
			cpSpecificationOptionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			cpSpecificationOptionImpl.setCreateDate(null);
		}
		else {
			cpSpecificationOptionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			cpSpecificationOptionImpl.setModifiedDate(null);
		}
		else {
			cpSpecificationOptionImpl.setModifiedDate(new Date(modifiedDate));
		}

		cpSpecificationOptionImpl.setCPOptionCategoryId(CPOptionCategoryId);

		if (title == null) {
			cpSpecificationOptionImpl.setTitle("");
		}
		else {
			cpSpecificationOptionImpl.setTitle(title);
		}

		if (description == null) {
			cpSpecificationOptionImpl.setDescription("");
		}
		else {
			cpSpecificationOptionImpl.setDescription(description);
		}

		cpSpecificationOptionImpl.setFacetable(facetable);

		if (key == null) {
			cpSpecificationOptionImpl.setKey("");
		}
		else {
			cpSpecificationOptionImpl.setKey(key);
		}

		cpSpecificationOptionImpl.setPriority(priority);
		cpSpecificationOptionImpl.setVisible(visible);

		if (lastPublishDate == Long.MIN_VALUE) {
			cpSpecificationOptionImpl.setLastPublishDate(null);
		}
		else {
			cpSpecificationOptionImpl.setLastPublishDate(
				new Date(lastPublishDate));
		}

		cpSpecificationOptionImpl.setStatus(status);

		cpSpecificationOptionImpl.resetOriginalValues();

		return cpSpecificationOptionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		CPSpecificationOptionId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		CPOptionCategoryId = objectInput.readLong();
		title = objectInput.readUTF();
		description = objectInput.readUTF();

		facetable = objectInput.readBoolean();
		key = objectInput.readUTF();

		priority = objectInput.readDouble();

		visible = objectInput.readBoolean();
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

		objectOutput.writeLong(CPSpecificationOptionId);

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

		objectOutput.writeLong(CPOptionCategoryId);

		if (title == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(title);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		objectOutput.writeBoolean(facetable);

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		objectOutput.writeDouble(priority);

		objectOutput.writeBoolean(visible);
		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long CPSpecificationOptionId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long CPOptionCategoryId;
	public String title;
	public String description;
	public boolean facetable;
	public String key;
	public double priority;
	public boolean visible;
	public long lastPublishDate;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:1693618283