/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model.impl;

import com.liferay.commerce.model.CommerceAddress;
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
 * The cache model class for representing CommerceAddress in entity cache.
 *
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class CommerceAddressCacheModel
	implements CacheModel<CommerceAddress>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CommerceAddressCacheModel)) {
			return false;
		}

		CommerceAddressCacheModel commerceAddressCacheModel =
			(CommerceAddressCacheModel)object;

		if ((commerceAddressId ==
				commerceAddressCacheModel.commerceAddressId) &&
			(mvccVersion == commerceAddressCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, commerceAddressId);

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
		StringBundler sb = new StringBundler(57);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", commerceAddressId=");
		sb.append(commerceAddressId);
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
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", classPK=");
		sb.append(classPK);
		sb.append(", countryId=");
		sb.append(countryId);
		sb.append(", regionId=");
		sb.append(regionId);
		sb.append(", city=");
		sb.append(city);
		sb.append(", defaultBilling=");
		sb.append(defaultBilling);
		sb.append(", defaultShipping=");
		sb.append(defaultShipping);
		sb.append(", description=");
		sb.append(description);
		sb.append(", latitude=");
		sb.append(latitude);
		sb.append(", longitude=");
		sb.append(longitude);
		sb.append(", name=");
		sb.append(name);
		sb.append(", phoneNumber=");
		sb.append(phoneNumber);
		sb.append(", street1=");
		sb.append(street1);
		sb.append(", street2=");
		sb.append(street2);
		sb.append(", street3=");
		sb.append(street3);
		sb.append(", subtype=");
		sb.append(subtype);
		sb.append(", type=");
		sb.append(type);
		sb.append(", zip=");
		sb.append(zip);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CommerceAddress toEntityModel() {
		CommerceAddressImpl commerceAddressImpl = new CommerceAddressImpl();

		commerceAddressImpl.setMvccVersion(mvccVersion);

		if (externalReferenceCode == null) {
			commerceAddressImpl.setExternalReferenceCode("");
		}
		else {
			commerceAddressImpl.setExternalReferenceCode(externalReferenceCode);
		}

		commerceAddressImpl.setCommerceAddressId(commerceAddressId);
		commerceAddressImpl.setGroupId(groupId);
		commerceAddressImpl.setCompanyId(companyId);
		commerceAddressImpl.setUserId(userId);

		if (userName == null) {
			commerceAddressImpl.setUserName("");
		}
		else {
			commerceAddressImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			commerceAddressImpl.setCreateDate(null);
		}
		else {
			commerceAddressImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			commerceAddressImpl.setModifiedDate(null);
		}
		else {
			commerceAddressImpl.setModifiedDate(new Date(modifiedDate));
		}

		commerceAddressImpl.setClassNameId(classNameId);
		commerceAddressImpl.setClassPK(classPK);
		commerceAddressImpl.setCountryId(countryId);
		commerceAddressImpl.setRegionId(regionId);

		if (city == null) {
			commerceAddressImpl.setCity("");
		}
		else {
			commerceAddressImpl.setCity(city);
		}

		commerceAddressImpl.setDefaultBilling(defaultBilling);
		commerceAddressImpl.setDefaultShipping(defaultShipping);

		if (description == null) {
			commerceAddressImpl.setDescription("");
		}
		else {
			commerceAddressImpl.setDescription(description);
		}

		commerceAddressImpl.setLatitude(latitude);
		commerceAddressImpl.setLongitude(longitude);

		if (name == null) {
			commerceAddressImpl.setName("");
		}
		else {
			commerceAddressImpl.setName(name);
		}

		if (phoneNumber == null) {
			commerceAddressImpl.setPhoneNumber("");
		}
		else {
			commerceAddressImpl.setPhoneNumber(phoneNumber);
		}

		if (street1 == null) {
			commerceAddressImpl.setStreet1("");
		}
		else {
			commerceAddressImpl.setStreet1(street1);
		}

		if (street2 == null) {
			commerceAddressImpl.setStreet2("");
		}
		else {
			commerceAddressImpl.setStreet2(street2);
		}

		if (street3 == null) {
			commerceAddressImpl.setStreet3("");
		}
		else {
			commerceAddressImpl.setStreet3(street3);
		}

		if (subtype == null) {
			commerceAddressImpl.setSubtype("");
		}
		else {
			commerceAddressImpl.setSubtype(subtype);
		}

		commerceAddressImpl.setType(type);

		if (zip == null) {
			commerceAddressImpl.setZip("");
		}
		else {
			commerceAddressImpl.setZip(zip);
		}

		commerceAddressImpl.setStatus(status);

		commerceAddressImpl.resetOriginalValues();

		return commerceAddressImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();
		externalReferenceCode = objectInput.readUTF();

		commerceAddressId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();

		countryId = objectInput.readLong();

		regionId = objectInput.readLong();
		city = objectInput.readUTF();

		defaultBilling = objectInput.readBoolean();

		defaultShipping = objectInput.readBoolean();
		description = objectInput.readUTF();

		latitude = objectInput.readDouble();

		longitude = objectInput.readDouble();
		name = objectInput.readUTF();
		phoneNumber = objectInput.readUTF();
		street1 = objectInput.readUTF();
		street2 = objectInput.readUTF();
		street3 = objectInput.readUTF();
		subtype = objectInput.readUTF();

		type = objectInput.readInt();
		zip = objectInput.readUTF();

		status = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		if (externalReferenceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(externalReferenceCode);
		}

		objectOutput.writeLong(commerceAddressId);

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

		objectOutput.writeLong(classNameId);

		objectOutput.writeLong(classPK);

		objectOutput.writeLong(countryId);

		objectOutput.writeLong(regionId);

		if (city == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(city);
		}

		objectOutput.writeBoolean(defaultBilling);

		objectOutput.writeBoolean(defaultShipping);

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		objectOutput.writeDouble(latitude);

		objectOutput.writeDouble(longitude);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (phoneNumber == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(phoneNumber);
		}

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

		if (subtype == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(subtype);
		}

		objectOutput.writeInt(type);

		if (zip == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(zip);
		}

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public String externalReferenceCode;
	public long commerceAddressId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long classNameId;
	public long classPK;
	public long countryId;
	public long regionId;
	public String city;
	public boolean defaultBilling;
	public boolean defaultShipping;
	public String description;
	public double latitude;
	public double longitude;
	public String name;
	public String phoneNumber;
	public String street1;
	public String street2;
	public String street3;
	public String subtype;
	public int type;
	public String zip;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:203040492