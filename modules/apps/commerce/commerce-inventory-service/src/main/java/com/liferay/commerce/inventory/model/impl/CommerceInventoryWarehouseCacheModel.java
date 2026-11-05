/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.model.impl;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
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
 * The cache model class for representing CommerceInventoryWarehouse in entity cache.
 *
 * @author Luca Pellizzon
 * @generated
 */
public class CommerceInventoryWarehouseCacheModel
	implements CacheModel<CommerceInventoryWarehouse>, Externalizable,
			   MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CommerceInventoryWarehouseCacheModel)) {
			return false;
		}

		CommerceInventoryWarehouseCacheModel
			commerceInventoryWarehouseCacheModel =
				(CommerceInventoryWarehouseCacheModel)object;

		if ((commerceInventoryWarehouseId ==
				commerceInventoryWarehouseCacheModel.
					commerceInventoryWarehouseId) &&
			(mvccVersion == commerceInventoryWarehouseCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, commerceInventoryWarehouseId);

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
		StringBundler sb = new StringBundler(47);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", commerceInventoryWarehouseId=");
		sb.append(commerceInventoryWarehouseId);
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
		sb.append(", active=");
		sb.append(active);
		sb.append(", street1=");
		sb.append(street1);
		sb.append(", street2=");
		sb.append(street2);
		sb.append(", street3=");
		sb.append(street3);
		sb.append(", city=");
		sb.append(city);
		sb.append(", zip=");
		sb.append(zip);
		sb.append(", commerceRegionCode=");
		sb.append(commerceRegionCode);
		sb.append(", countryTwoLettersISOCode=");
		sb.append(countryTwoLettersISOCode);
		sb.append(", latitude=");
		sb.append(latitude);
		sb.append(", longitude=");
		sb.append(longitude);
		sb.append(", type=");
		sb.append(type);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CommerceInventoryWarehouse toEntityModel() {
		CommerceInventoryWarehouseImpl commerceInventoryWarehouseImpl =
			new CommerceInventoryWarehouseImpl();

		commerceInventoryWarehouseImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			commerceInventoryWarehouseImpl.setUuid("");
		}
		else {
			commerceInventoryWarehouseImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			commerceInventoryWarehouseImpl.setExternalReferenceCode("");
		}
		else {
			commerceInventoryWarehouseImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		commerceInventoryWarehouseImpl.setCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId);
		commerceInventoryWarehouseImpl.setCompanyId(companyId);
		commerceInventoryWarehouseImpl.setUserId(userId);

		if (userName == null) {
			commerceInventoryWarehouseImpl.setUserName("");
		}
		else {
			commerceInventoryWarehouseImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			commerceInventoryWarehouseImpl.setCreateDate(null);
		}
		else {
			commerceInventoryWarehouseImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			commerceInventoryWarehouseImpl.setModifiedDate(null);
		}
		else {
			commerceInventoryWarehouseImpl.setModifiedDate(
				new Date(modifiedDate));
		}

		if (name == null) {
			commerceInventoryWarehouseImpl.setName("");
		}
		else {
			commerceInventoryWarehouseImpl.setName(name);
		}

		if (description == null) {
			commerceInventoryWarehouseImpl.setDescription("");
		}
		else {
			commerceInventoryWarehouseImpl.setDescription(description);
		}

		commerceInventoryWarehouseImpl.setActive(active);

		if (street1 == null) {
			commerceInventoryWarehouseImpl.setStreet1("");
		}
		else {
			commerceInventoryWarehouseImpl.setStreet1(street1);
		}

		if (street2 == null) {
			commerceInventoryWarehouseImpl.setStreet2("");
		}
		else {
			commerceInventoryWarehouseImpl.setStreet2(street2);
		}

		if (street3 == null) {
			commerceInventoryWarehouseImpl.setStreet3("");
		}
		else {
			commerceInventoryWarehouseImpl.setStreet3(street3);
		}

		if (city == null) {
			commerceInventoryWarehouseImpl.setCity("");
		}
		else {
			commerceInventoryWarehouseImpl.setCity(city);
		}

		if (zip == null) {
			commerceInventoryWarehouseImpl.setZip("");
		}
		else {
			commerceInventoryWarehouseImpl.setZip(zip);
		}

		if (commerceRegionCode == null) {
			commerceInventoryWarehouseImpl.setCommerceRegionCode("");
		}
		else {
			commerceInventoryWarehouseImpl.setCommerceRegionCode(
				commerceRegionCode);
		}

		if (countryTwoLettersISOCode == null) {
			commerceInventoryWarehouseImpl.setCountryTwoLettersISOCode("");
		}
		else {
			commerceInventoryWarehouseImpl.setCountryTwoLettersISOCode(
				countryTwoLettersISOCode);
		}

		commerceInventoryWarehouseImpl.setLatitude(latitude);
		commerceInventoryWarehouseImpl.setLongitude(longitude);

		if (type == null) {
			commerceInventoryWarehouseImpl.setType("");
		}
		else {
			commerceInventoryWarehouseImpl.setType(type);
		}

		commerceInventoryWarehouseImpl.setStatus(status);

		commerceInventoryWarehouseImpl.resetOriginalValues();

		return commerceInventoryWarehouseImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		commerceInventoryWarehouseId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		name = objectInput.readUTF();
		description = objectInput.readUTF();

		active = objectInput.readBoolean();
		street1 = objectInput.readUTF();
		street2 = objectInput.readUTF();
		street3 = objectInput.readUTF();
		city = objectInput.readUTF();
		zip = objectInput.readUTF();
		commerceRegionCode = objectInput.readUTF();
		countryTwoLettersISOCode = objectInput.readUTF();

		latitude = objectInput.readDouble();

		longitude = objectInput.readDouble();
		type = objectInput.readUTF();

		status = objectInput.readInt();
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

		objectOutput.writeLong(commerceInventoryWarehouseId);

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

		objectOutput.writeBoolean(active);

		if (street1 == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(street1);
		}

		if (street2 == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(street2);
		}

		if (street3 == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(street3);
		}

		if (city == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(city);
		}

		if (zip == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(zip);
		}

		if (commerceRegionCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(commerceRegionCode);
		}

		if (countryTwoLettersISOCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(countryTwoLettersISOCode);
		}

		objectOutput.writeDouble(latitude);

		objectOutput.writeDouble(longitude);

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long commerceInventoryWarehouseId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String name;
	public String description;
	public boolean active;
	public String street1;
	public String street2;
	public String street3;
	public String city;
	public String zip;
	public String commerceRegionCode;
	public String countryTwoLettersISOCode;
	public double latitude;
	public double longitude;
	public String type;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:-978556128