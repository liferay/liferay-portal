/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPDefinition;
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
 * The cache model class for representing CPDefinition in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPDefinitionCacheModel
	implements CacheModel<CPDefinition>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPDefinitionCacheModel)) {
			return false;
		}

		CPDefinitionCacheModel cpDefinitionCacheModel =
			(CPDefinitionCacheModel)object;

		if ((CPDefinitionId == cpDefinitionCacheModel.CPDefinitionId) &&
			(mvccVersion == cpDefinitionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, CPDefinitionId);

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
		StringBundler sb = new StringBundler(99);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", defaultLanguageId=");
		sb.append(defaultLanguageId);
		sb.append(", CPDefinitionId=");
		sb.append(CPDefinitionId);
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
		sb.append(", CProductExternalReferenceCode=");
		sb.append(CProductExternalReferenceCode);
		sb.append(", CProductId=");
		sb.append(CProductId);
		sb.append(", CPTaxCategoryId=");
		sb.append(CPTaxCategoryId);
		sb.append(", accountGroupFilterEnabled=");
		sb.append(accountGroupFilterEnabled);
		sb.append(", availableIndividually=");
		sb.append(availableIndividually);
		sb.append(", channelFilterEnabled=");
		sb.append(channelFilterEnabled);
		sb.append(", DDMStructureKey=");
		sb.append(DDMStructureKey);
		sb.append(", deliveryMaxSubscriptionCycles=");
		sb.append(deliveryMaxSubscriptionCycles);
		sb.append(", deliverySubscriptionEnabled=");
		sb.append(deliverySubscriptionEnabled);
		sb.append(", deliverySubscriptionLength=");
		sb.append(deliverySubscriptionLength);
		sb.append(", deliverySubscriptionType=");
		sb.append(deliverySubscriptionType);
		sb.append(", deliverySubscriptionTypeSettings=");
		sb.append(deliverySubscriptionTypeSettings);
		sb.append(", depth=");
		sb.append(depth);
		sb.append(", displayDate=");
		sb.append(displayDate);
		sb.append(", expirationDate=");
		sb.append(expirationDate);
		sb.append(", freeShipping=");
		sb.append(freeShipping);
		sb.append(", height=");
		sb.append(height);
		sb.append(", ignoreSKUCombinations=");
		sb.append(ignoreSKUCombinations);
		sb.append(", maxSubscriptionCycles=");
		sb.append(maxSubscriptionCycles);
		sb.append(", productTypeName=");
		sb.append(productTypeName);
		sb.append(", published=");
		sb.append(published);
		sb.append(", shipSeparately=");
		sb.append(shipSeparately);
		sb.append(", shippable=");
		sb.append(shippable);
		sb.append(", shippingExtraPrice=");
		sb.append(shippingExtraPrice);
		sb.append(", subscriptionEnabled=");
		sb.append(subscriptionEnabled);
		sb.append(", subscriptionLength=");
		sb.append(subscriptionLength);
		sb.append(", subscriptionType=");
		sb.append(subscriptionType);
		sb.append(", subscriptionTypeSettings=");
		sb.append(subscriptionTypeSettings);
		sb.append(", taxExempt=");
		sb.append(taxExempt);
		sb.append(", telcoOrElectronics=");
		sb.append(telcoOrElectronics);
		sb.append(", version=");
		sb.append(version);
		sb.append(", weight=");
		sb.append(weight);
		sb.append(", width=");
		sb.append(width);
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
	public CPDefinition toEntityModel() {
		CPDefinitionImpl cpDefinitionImpl = new CPDefinitionImpl();

		cpDefinitionImpl.setMvccVersion(mvccVersion);
		cpDefinitionImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			cpDefinitionImpl.setUuid("");
		}
		else {
			cpDefinitionImpl.setUuid(uuid);
		}

		if (defaultLanguageId == null) {
			cpDefinitionImpl.setDefaultLanguageId("");
		}
		else {
			cpDefinitionImpl.setDefaultLanguageId(defaultLanguageId);
		}

		cpDefinitionImpl.setCPDefinitionId(CPDefinitionId);
		cpDefinitionImpl.setGroupId(groupId);
		cpDefinitionImpl.setCompanyId(companyId);
		cpDefinitionImpl.setUserId(userId);

		if (userName == null) {
			cpDefinitionImpl.setUserName("");
		}
		else {
			cpDefinitionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			cpDefinitionImpl.setCreateDate(null);
		}
		else {
			cpDefinitionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			cpDefinitionImpl.setModifiedDate(null);
		}
		else {
			cpDefinitionImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (CProductExternalReferenceCode == null) {
			cpDefinitionImpl.setCProductExternalReferenceCode("");
		}
		else {
			cpDefinitionImpl.setCProductExternalReferenceCode(
				CProductExternalReferenceCode);
		}

		cpDefinitionImpl.setCProductId(CProductId);
		cpDefinitionImpl.setCPTaxCategoryId(CPTaxCategoryId);
		cpDefinitionImpl.setAccountGroupFilterEnabled(
			accountGroupFilterEnabled);
		cpDefinitionImpl.setAvailableIndividually(availableIndividually);
		cpDefinitionImpl.setChannelFilterEnabled(channelFilterEnabled);

		if (DDMStructureKey == null) {
			cpDefinitionImpl.setDDMStructureKey("");
		}
		else {
			cpDefinitionImpl.setDDMStructureKey(DDMStructureKey);
		}

		cpDefinitionImpl.setDeliveryMaxSubscriptionCycles(
			deliveryMaxSubscriptionCycles);
		cpDefinitionImpl.setDeliverySubscriptionEnabled(
			deliverySubscriptionEnabled);
		cpDefinitionImpl.setDeliverySubscriptionLength(
			deliverySubscriptionLength);

		if (deliverySubscriptionType == null) {
			cpDefinitionImpl.setDeliverySubscriptionType("");
		}
		else {
			cpDefinitionImpl.setDeliverySubscriptionType(
				deliverySubscriptionType);
		}

		if (deliverySubscriptionTypeSettings == null) {
			cpDefinitionImpl.setDeliverySubscriptionTypeSettings("");
		}
		else {
			cpDefinitionImpl.setDeliverySubscriptionTypeSettings(
				deliverySubscriptionTypeSettings);
		}

		cpDefinitionImpl.setDepth(depth);

		if (displayDate == Long.MIN_VALUE) {
			cpDefinitionImpl.setDisplayDate(null);
		}
		else {
			cpDefinitionImpl.setDisplayDate(new Date(displayDate));
		}

		if (expirationDate == Long.MIN_VALUE) {
			cpDefinitionImpl.setExpirationDate(null);
		}
		else {
			cpDefinitionImpl.setExpirationDate(new Date(expirationDate));
		}

		cpDefinitionImpl.setFreeShipping(freeShipping);
		cpDefinitionImpl.setHeight(height);
		cpDefinitionImpl.setIgnoreSKUCombinations(ignoreSKUCombinations);
		cpDefinitionImpl.setMaxSubscriptionCycles(maxSubscriptionCycles);

		if (productTypeName == null) {
			cpDefinitionImpl.setProductTypeName("");
		}
		else {
			cpDefinitionImpl.setProductTypeName(productTypeName);
		}

		cpDefinitionImpl.setPublished(published);
		cpDefinitionImpl.setShipSeparately(shipSeparately);
		cpDefinitionImpl.setShippable(shippable);
		cpDefinitionImpl.setShippingExtraPrice(shippingExtraPrice);
		cpDefinitionImpl.setSubscriptionEnabled(subscriptionEnabled);
		cpDefinitionImpl.setSubscriptionLength(subscriptionLength);

		if (subscriptionType == null) {
			cpDefinitionImpl.setSubscriptionType("");
		}
		else {
			cpDefinitionImpl.setSubscriptionType(subscriptionType);
		}

		if (subscriptionTypeSettings == null) {
			cpDefinitionImpl.setSubscriptionTypeSettings("");
		}
		else {
			cpDefinitionImpl.setSubscriptionTypeSettings(
				subscriptionTypeSettings);
		}

		cpDefinitionImpl.setTaxExempt(taxExempt);
		cpDefinitionImpl.setTelcoOrElectronics(telcoOrElectronics);
		cpDefinitionImpl.setVersion(version);
		cpDefinitionImpl.setWeight(weight);
		cpDefinitionImpl.setWidth(width);

		if (lastPublishDate == Long.MIN_VALUE) {
			cpDefinitionImpl.setLastPublishDate(null);
		}
		else {
			cpDefinitionImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		cpDefinitionImpl.setStatus(status);
		cpDefinitionImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			cpDefinitionImpl.setStatusByUserName("");
		}
		else {
			cpDefinitionImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			cpDefinitionImpl.setStatusDate(null);
		}
		else {
			cpDefinitionImpl.setStatusDate(new Date(statusDate));
		}

		cpDefinitionImpl.resetOriginalValues();

		return cpDefinitionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		defaultLanguageId = objectInput.readUTF();

		CPDefinitionId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		CProductExternalReferenceCode = objectInput.readUTF();

		CProductId = objectInput.readLong();

		CPTaxCategoryId = objectInput.readLong();

		accountGroupFilterEnabled = objectInput.readBoolean();

		availableIndividually = objectInput.readBoolean();

		channelFilterEnabled = objectInput.readBoolean();
		DDMStructureKey = objectInput.readUTF();

		deliveryMaxSubscriptionCycles = objectInput.readLong();

		deliverySubscriptionEnabled = objectInput.readBoolean();

		deliverySubscriptionLength = objectInput.readInt();
		deliverySubscriptionType = objectInput.readUTF();
		deliverySubscriptionTypeSettings = objectInput.readUTF();

		depth = objectInput.readDouble();
		displayDate = objectInput.readLong();
		expirationDate = objectInput.readLong();

		freeShipping = objectInput.readBoolean();

		height = objectInput.readDouble();

		ignoreSKUCombinations = objectInput.readBoolean();

		maxSubscriptionCycles = objectInput.readLong();
		productTypeName = objectInput.readUTF();

		published = objectInput.readBoolean();

		shipSeparately = objectInput.readBoolean();

		shippable = objectInput.readBoolean();

		shippingExtraPrice = objectInput.readDouble();

		subscriptionEnabled = objectInput.readBoolean();

		subscriptionLength = objectInput.readInt();
		subscriptionType = objectInput.readUTF();
		subscriptionTypeSettings = (String)objectInput.readObject();

		taxExempt = objectInput.readBoolean();

		telcoOrElectronics = objectInput.readBoolean();

		version = objectInput.readInt();

		weight = objectInput.readDouble();

		width = objectInput.readDouble();
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

		if (defaultLanguageId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(defaultLanguageId);
		}

		objectOutput.writeLong(CPDefinitionId);

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

		if (CProductExternalReferenceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(CProductExternalReferenceCode);
		}

		objectOutput.writeLong(CProductId);

		objectOutput.writeLong(CPTaxCategoryId);

		objectOutput.writeBoolean(accountGroupFilterEnabled);

		objectOutput.writeBoolean(availableIndividually);

		objectOutput.writeBoolean(channelFilterEnabled);

		if (DDMStructureKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(DDMStructureKey);
		}

		objectOutput.writeLong(deliveryMaxSubscriptionCycles);

		objectOutput.writeBoolean(deliverySubscriptionEnabled);

		objectOutput.writeInt(deliverySubscriptionLength);

		if (deliverySubscriptionType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(deliverySubscriptionType);
		}

		if (deliverySubscriptionTypeSettings == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(deliverySubscriptionTypeSettings);
		}

		objectOutput.writeDouble(depth);
		objectOutput.writeLong(displayDate);
		objectOutput.writeLong(expirationDate);

		objectOutput.writeBoolean(freeShipping);

		objectOutput.writeDouble(height);

		objectOutput.writeBoolean(ignoreSKUCombinations);

		objectOutput.writeLong(maxSubscriptionCycles);

		if (productTypeName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(productTypeName);
		}

		objectOutput.writeBoolean(published);

		objectOutput.writeBoolean(shipSeparately);

		objectOutput.writeBoolean(shippable);

		objectOutput.writeDouble(shippingExtraPrice);

		objectOutput.writeBoolean(subscriptionEnabled);

		objectOutput.writeInt(subscriptionLength);

		if (subscriptionType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(subscriptionType);
		}

		if (subscriptionTypeSettings == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(subscriptionTypeSettings);
		}

		objectOutput.writeBoolean(taxExempt);

		objectOutput.writeBoolean(telcoOrElectronics);

		objectOutput.writeInt(version);

		objectOutput.writeDouble(weight);

		objectOutput.writeDouble(width);
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
	public String defaultLanguageId;
	public long CPDefinitionId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String CProductExternalReferenceCode;
	public long CProductId;
	public long CPTaxCategoryId;
	public boolean accountGroupFilterEnabled;
	public boolean availableIndividually;
	public boolean channelFilterEnabled;
	public String DDMStructureKey;
	public long deliveryMaxSubscriptionCycles;
	public boolean deliverySubscriptionEnabled;
	public int deliverySubscriptionLength;
	public String deliverySubscriptionType;
	public String deliverySubscriptionTypeSettings;
	public double depth;
	public long displayDate;
	public long expirationDate;
	public boolean freeShipping;
	public double height;
	public boolean ignoreSKUCombinations;
	public long maxSubscriptionCycles;
	public String productTypeName;
	public boolean published;
	public boolean shipSeparately;
	public boolean shippable;
	public double shippingExtraPrice;
	public boolean subscriptionEnabled;
	public int subscriptionLength;
	public String subscriptionType;
	public String subscriptionTypeSettings;
	public boolean taxExempt;
	public boolean telcoOrElectronics;
	public int version;
	public double weight;
	public double width;
	public long lastPublishDate;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1147853357