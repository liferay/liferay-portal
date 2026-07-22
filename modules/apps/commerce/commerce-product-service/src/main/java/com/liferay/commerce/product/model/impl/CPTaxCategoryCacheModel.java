/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPTaxCategory;
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
 * The cache model class for representing CPTaxCategory in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPTaxCategoryCacheModel
	implements CacheModel<CPTaxCategory>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPTaxCategoryCacheModel)) {
			return false;
		}

		CPTaxCategoryCacheModel cpTaxCategoryCacheModel =
			(CPTaxCategoryCacheModel)object;

		if ((CPTaxCategoryId == cpTaxCategoryCacheModel.CPTaxCategoryId) &&
			(mvccVersion == cpTaxCategoryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, CPTaxCategoryId);

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
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", CPTaxCategoryId=");
		sb.append(CPTaxCategoryId);
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
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CPTaxCategory toEntityModel() {
		CPTaxCategoryImpl cpTaxCategoryImpl = new CPTaxCategoryImpl();

		cpTaxCategoryImpl.setMvccVersion(mvccVersion);
		cpTaxCategoryImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			cpTaxCategoryImpl.setUuid("");
		}
		else {
			cpTaxCategoryImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			cpTaxCategoryImpl.setExternalReferenceCode("");
		}
		else {
			cpTaxCategoryImpl.setExternalReferenceCode(externalReferenceCode);
		}

		cpTaxCategoryImpl.setCPTaxCategoryId(CPTaxCategoryId);
		cpTaxCategoryImpl.setCompanyId(companyId);
		cpTaxCategoryImpl.setUserId(userId);

		if (userName == null) {
			cpTaxCategoryImpl.setUserName("");
		}
		else {
			cpTaxCategoryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			cpTaxCategoryImpl.setCreateDate(null);
		}
		else {
			cpTaxCategoryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			cpTaxCategoryImpl.setModifiedDate(null);
		}
		else {
			cpTaxCategoryImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (name == null) {
			cpTaxCategoryImpl.setName("");
		}
		else {
			cpTaxCategoryImpl.setName(name);
		}

		if (description == null) {
			cpTaxCategoryImpl.setDescription("");
		}
		else {
			cpTaxCategoryImpl.setDescription(description);
		}

		cpTaxCategoryImpl.setStatus(status);

		cpTaxCategoryImpl.resetOriginalValues();

		return cpTaxCategoryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		CPTaxCategoryId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		name = objectInput.readUTF();
		description = objectInput.readUTF();

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

		objectOutput.writeLong(CPTaxCategoryId);

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

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long CPTaxCategoryId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String name;
	public String description;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:-279879815