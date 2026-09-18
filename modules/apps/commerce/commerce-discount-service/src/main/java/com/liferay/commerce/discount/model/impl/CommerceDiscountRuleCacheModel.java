/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.model.impl;

import com.liferay.commerce.discount.model.CommerceDiscountRule;
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
 * The cache model class for representing CommerceDiscountRule in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CommerceDiscountRuleCacheModel
	implements CacheModel<CommerceDiscountRule>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CommerceDiscountRuleCacheModel)) {
			return false;
		}

		CommerceDiscountRuleCacheModel commerceDiscountRuleCacheModel =
			(CommerceDiscountRuleCacheModel)object;

		if ((commerceDiscountRuleId ==
				commerceDiscountRuleCacheModel.commerceDiscountRuleId) &&
			(mvccVersion == commerceDiscountRuleCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, commerceDiscountRuleId);

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
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", commerceDiscountRuleId=");
		sb.append(commerceDiscountRuleId);
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
		sb.append(", commerceDiscountId=");
		sb.append(commerceDiscountId);
		sb.append(", type=");
		sb.append(type);
		sb.append(", typeSettings=");
		sb.append(typeSettings);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CommerceDiscountRule toEntityModel() {
		CommerceDiscountRuleImpl commerceDiscountRuleImpl =
			new CommerceDiscountRuleImpl();

		commerceDiscountRuleImpl.setMvccVersion(mvccVersion);

		if (externalReferenceCode == null) {
			commerceDiscountRuleImpl.setExternalReferenceCode("");
		}
		else {
			commerceDiscountRuleImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		commerceDiscountRuleImpl.setCommerceDiscountRuleId(
			commerceDiscountRuleId);
		commerceDiscountRuleImpl.setCompanyId(companyId);
		commerceDiscountRuleImpl.setUserId(userId);

		if (userName == null) {
			commerceDiscountRuleImpl.setUserName("");
		}
		else {
			commerceDiscountRuleImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			commerceDiscountRuleImpl.setCreateDate(null);
		}
		else {
			commerceDiscountRuleImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			commerceDiscountRuleImpl.setModifiedDate(null);
		}
		else {
			commerceDiscountRuleImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (name == null) {
			commerceDiscountRuleImpl.setName("");
		}
		else {
			commerceDiscountRuleImpl.setName(name);
		}

		commerceDiscountRuleImpl.setCommerceDiscountId(commerceDiscountId);

		if (type == null) {
			commerceDiscountRuleImpl.setType("");
		}
		else {
			commerceDiscountRuleImpl.setType(type);
		}

		if (typeSettings == null) {
			commerceDiscountRuleImpl.setTypeSettings("");
		}
		else {
			commerceDiscountRuleImpl.setTypeSettings(typeSettings);
		}

		commerceDiscountRuleImpl.resetOriginalValues();

		return commerceDiscountRuleImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();
		externalReferenceCode = objectInput.readUTF();

		commerceDiscountRuleId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		name = objectInput.readUTF();

		commerceDiscountId = objectInput.readLong();
		type = objectInput.readUTF();
		typeSettings = (String)objectInput.readObject();
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

		objectOutput.writeLong(commerceDiscountRuleId);

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

		objectOutput.writeLong(commerceDiscountId);

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}

		if (typeSettings == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(typeSettings);
		}
	}

	public long mvccVersion;
	public String externalReferenceCode;
	public long commerceDiscountRuleId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String name;
	public long commerceDiscountId;
	public String type;
	public String typeSettings;

}
// LIFERAY-SERVICE-BUILDER-HASH:1360032994