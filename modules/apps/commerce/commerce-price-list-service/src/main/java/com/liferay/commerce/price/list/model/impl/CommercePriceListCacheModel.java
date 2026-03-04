/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.model.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
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
 * The cache model class for representing CommercePriceList in entity cache.
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
public class CommercePriceListCacheModel
	implements CacheModel<CommercePriceList>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CommercePriceListCacheModel)) {
			return false;
		}

		CommercePriceListCacheModel commercePriceListCacheModel =
			(CommercePriceListCacheModel)object;

		if ((commercePriceListId ==
				commercePriceListCacheModel.commercePriceListId) &&
			(mvccVersion == commercePriceListCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, commercePriceListId);

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
		sb.append(", commercePriceListId=");
		sb.append(commercePriceListId);
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
		sb.append(", catalogBasePriceList=");
		sb.append(catalogBasePriceList);
		sb.append(", commerceCurrencyCode=");
		sb.append(commerceCurrencyCode);
		sb.append(", displayDate=");
		sb.append(displayDate);
		sb.append(", expirationDate=");
		sb.append(expirationDate);
		sb.append(", name=");
		sb.append(name);
		sb.append(", netPrice=");
		sb.append(netPrice);
		sb.append(", parentCommercePriceListId=");
		sb.append(parentCommercePriceListId);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", type=");
		sb.append(type);
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
	public CommercePriceList toEntityModel() {
		CommercePriceListImpl commercePriceListImpl =
			new CommercePriceListImpl();

		commercePriceListImpl.setMvccVersion(mvccVersion);
		commercePriceListImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			commercePriceListImpl.setUuid("");
		}
		else {
			commercePriceListImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			commercePriceListImpl.setExternalReferenceCode("");
		}
		else {
			commercePriceListImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		commercePriceListImpl.setCommercePriceListId(commercePriceListId);
		commercePriceListImpl.setGroupId(groupId);
		commercePriceListImpl.setCompanyId(companyId);
		commercePriceListImpl.setUserId(userId);

		if (userName == null) {
			commercePriceListImpl.setUserName("");
		}
		else {
			commercePriceListImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			commercePriceListImpl.setCreateDate(null);
		}
		else {
			commercePriceListImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			commercePriceListImpl.setModifiedDate(null);
		}
		else {
			commercePriceListImpl.setModifiedDate(new Date(modifiedDate));
		}

		commercePriceListImpl.setCatalogBasePriceList(catalogBasePriceList);

		if (commerceCurrencyCode == null) {
			commercePriceListImpl.setCommerceCurrencyCode("");
		}
		else {
			commercePriceListImpl.setCommerceCurrencyCode(commerceCurrencyCode);
		}

		if (displayDate == Long.MIN_VALUE) {
			commercePriceListImpl.setDisplayDate(null);
		}
		else {
			commercePriceListImpl.setDisplayDate(new Date(displayDate));
		}

		if (expirationDate == Long.MIN_VALUE) {
			commercePriceListImpl.setExpirationDate(null);
		}
		else {
			commercePriceListImpl.setExpirationDate(new Date(expirationDate));
		}

		if (name == null) {
			commercePriceListImpl.setName("");
		}
		else {
			commercePriceListImpl.setName(name);
		}

		commercePriceListImpl.setNetPrice(netPrice);
		commercePriceListImpl.setParentCommercePriceListId(
			parentCommercePriceListId);
		commercePriceListImpl.setPriority(priority);

		if (type == null) {
			commercePriceListImpl.setType("");
		}
		else {
			commercePriceListImpl.setType(type);
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			commercePriceListImpl.setLastPublishDate(null);
		}
		else {
			commercePriceListImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		commercePriceListImpl.setStatus(status);
		commercePriceListImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			commercePriceListImpl.setStatusByUserName("");
		}
		else {
			commercePriceListImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			commercePriceListImpl.setStatusDate(null);
		}
		else {
			commercePriceListImpl.setStatusDate(new Date(statusDate));
		}

		commercePriceListImpl.resetOriginalValues();

		return commercePriceListImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		commercePriceListId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		catalogBasePriceList = objectInput.readBoolean();
		commerceCurrencyCode = objectInput.readUTF();
		displayDate = objectInput.readLong();
		expirationDate = objectInput.readLong();
		name = objectInput.readUTF();

		netPrice = objectInput.readBoolean();

		parentCommercePriceListId = objectInput.readLong();

		priority = objectInput.readDouble();
		type = objectInput.readUTF();
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

		objectOutput.writeLong(commercePriceListId);

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

		objectOutput.writeBoolean(catalogBasePriceList);

		if (commerceCurrencyCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(commerceCurrencyCode);
		}

		objectOutput.writeLong(displayDate);
		objectOutput.writeLong(expirationDate);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeBoolean(netPrice);

		objectOutput.writeLong(parentCommercePriceListId);

		objectOutput.writeDouble(priority);

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}

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
	public long commercePriceListId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public boolean catalogBasePriceList;
	public String commerceCurrencyCode;
	public long displayDate;
	public long expirationDate;
	public String name;
	public boolean netPrice;
	public long parentCommercePriceListId;
	public double priority;
	public String type;
	public long lastPublishDate;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}