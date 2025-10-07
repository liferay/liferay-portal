/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.math.BigDecimal;

import java.util.Date;

/**
 * The cache model class for representing CPConfigurationEntry in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPConfigurationEntryCacheModel
	implements CacheModel<CPConfigurationEntry>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPConfigurationEntryCacheModel)) {
			return false;
		}

		CPConfigurationEntryCacheModel cpConfigurationEntryCacheModel =
			(CPConfigurationEntryCacheModel)object;

		if ((CPConfigurationEntryId ==
				cpConfigurationEntryCacheModel.CPConfigurationEntryId) &&
			(mvccVersion == cpConfigurationEntryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, CPConfigurationEntryId);

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
		StringBundler sb = new StringBundler(73);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", CPConfigurationEntryId=");
		sb.append(CPConfigurationEntryId);
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
		sb.append(", CPConfigurationListId=");
		sb.append(CPConfigurationListId);
		sb.append(", CPTaxCategoryId=");
		sb.append(CPTaxCategoryId);
		sb.append(", allowedOrderQuantities=");
		sb.append(allowedOrderQuantities);
		sb.append(", backOrders=");
		sb.append(backOrders);
		sb.append(", commerceAvailabilityEstimateId=");
		sb.append(commerceAvailabilityEstimateId);
		sb.append(", CPDefinitionInventoryEngine=");
		sb.append(CPDefinitionInventoryEngine);
		sb.append(", depth=");
		sb.append(depth);
		sb.append(", displayAvailability=");
		sb.append(displayAvailability);
		sb.append(", displayStockQuantity=");
		sb.append(displayStockQuantity);
		sb.append(", freeShipping=");
		sb.append(freeShipping);
		sb.append(", height=");
		sb.append(height);
		sb.append(", lowStockActivity=");
		sb.append(lowStockActivity);
		sb.append(", maxOrderQuantity=");
		sb.append(maxOrderQuantity);
		sb.append(", minOrderQuantity=");
		sb.append(minOrderQuantity);
		sb.append(", minStockQuantity=");
		sb.append(minStockQuantity);
		sb.append(", multipleOrderQuantity=");
		sb.append(multipleOrderQuantity);
		sb.append(", purchasable=");
		sb.append(purchasable);
		sb.append(", shippable=");
		sb.append(shippable);
		sb.append(", shippingExtraPrice=");
		sb.append(shippingExtraPrice);
		sb.append(", shipSeparately=");
		sb.append(shipSeparately);
		sb.append(", taxExempt=");
		sb.append(taxExempt);
		sb.append(", weight=");
		sb.append(weight);
		sb.append(", width=");
		sb.append(width);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CPConfigurationEntry toEntityModel() {
		CPConfigurationEntryImpl cpConfigurationEntryImpl =
			new CPConfigurationEntryImpl();

		cpConfigurationEntryImpl.setMvccVersion(mvccVersion);
		cpConfigurationEntryImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			cpConfigurationEntryImpl.setUuid("");
		}
		else {
			cpConfigurationEntryImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			cpConfigurationEntryImpl.setExternalReferenceCode("");
		}
		else {
			cpConfigurationEntryImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		cpConfigurationEntryImpl.setCPConfigurationEntryId(
			CPConfigurationEntryId);
		cpConfigurationEntryImpl.setGroupId(groupId);
		cpConfigurationEntryImpl.setCompanyId(companyId);
		cpConfigurationEntryImpl.setUserId(userId);

		if (userName == null) {
			cpConfigurationEntryImpl.setUserName("");
		}
		else {
			cpConfigurationEntryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			cpConfigurationEntryImpl.setCreateDate(null);
		}
		else {
			cpConfigurationEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			cpConfigurationEntryImpl.setModifiedDate(null);
		}
		else {
			cpConfigurationEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		cpConfigurationEntryImpl.setClassNameId(classNameId);
		cpConfigurationEntryImpl.setClassPK(classPK);
		cpConfigurationEntryImpl.setCPConfigurationListId(
			CPConfigurationListId);
		cpConfigurationEntryImpl.setCPTaxCategoryId(CPTaxCategoryId);

		if (allowedOrderQuantities == null) {
			cpConfigurationEntryImpl.setAllowedOrderQuantities("");
		}
		else {
			cpConfigurationEntryImpl.setAllowedOrderQuantities(
				allowedOrderQuantities);
		}

		cpConfigurationEntryImpl.setBackOrders(backOrders);
		cpConfigurationEntryImpl.setCommerceAvailabilityEstimateId(
			commerceAvailabilityEstimateId);

		if (CPDefinitionInventoryEngine == null) {
			cpConfigurationEntryImpl.setCPDefinitionInventoryEngine("");
		}
		else {
			cpConfigurationEntryImpl.setCPDefinitionInventoryEngine(
				CPDefinitionInventoryEngine);
		}

		cpConfigurationEntryImpl.setDepth(depth);
		cpConfigurationEntryImpl.setDisplayAvailability(displayAvailability);
		cpConfigurationEntryImpl.setDisplayStockQuantity(displayStockQuantity);
		cpConfigurationEntryImpl.setFreeShipping(freeShipping);
		cpConfigurationEntryImpl.setHeight(height);

		if (lowStockActivity == null) {
			cpConfigurationEntryImpl.setLowStockActivity("");
		}
		else {
			cpConfigurationEntryImpl.setLowStockActivity(lowStockActivity);
		}

		cpConfigurationEntryImpl.setMaxOrderQuantity(maxOrderQuantity);
		cpConfigurationEntryImpl.setMinOrderQuantity(minOrderQuantity);
		cpConfigurationEntryImpl.setMinStockQuantity(minStockQuantity);
		cpConfigurationEntryImpl.setMultipleOrderQuantity(
			multipleOrderQuantity);
		cpConfigurationEntryImpl.setPurchasable(purchasable);
		cpConfigurationEntryImpl.setShippable(shippable);
		cpConfigurationEntryImpl.setShippingExtraPrice(shippingExtraPrice);
		cpConfigurationEntryImpl.setShipSeparately(shipSeparately);
		cpConfigurationEntryImpl.setTaxExempt(taxExempt);
		cpConfigurationEntryImpl.setWeight(weight);
		cpConfigurationEntryImpl.setWidth(width);

		cpConfigurationEntryImpl.resetOriginalValues();

		return cpConfigurationEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		CPConfigurationEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();

		CPConfigurationListId = objectInput.readLong();

		CPTaxCategoryId = objectInput.readLong();
		allowedOrderQuantities = objectInput.readUTF();

		backOrders = objectInput.readBoolean();

		commerceAvailabilityEstimateId = objectInput.readLong();
		CPDefinitionInventoryEngine = objectInput.readUTF();

		depth = objectInput.readDouble();

		displayAvailability = objectInput.readBoolean();

		displayStockQuantity = objectInput.readBoolean();

		freeShipping = objectInput.readBoolean();

		height = objectInput.readDouble();
		lowStockActivity = objectInput.readUTF();
		maxOrderQuantity = (BigDecimal)objectInput.readObject();
		minOrderQuantity = (BigDecimal)objectInput.readObject();
		minStockQuantity = (BigDecimal)objectInput.readObject();
		multipleOrderQuantity = (BigDecimal)objectInput.readObject();

		purchasable = objectInput.readBoolean();

		shippable = objectInput.readBoolean();

		shippingExtraPrice = objectInput.readDouble();

		shipSeparately = objectInput.readBoolean();

		taxExempt = objectInput.readBoolean();

		weight = objectInput.readDouble();

		width = objectInput.readDouble();
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

		objectOutput.writeLong(CPConfigurationEntryId);

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

		objectOutput.writeLong(CPConfigurationListId);

		objectOutput.writeLong(CPTaxCategoryId);

		if (allowedOrderQuantities == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(allowedOrderQuantities);
		}

		objectOutput.writeBoolean(backOrders);

		objectOutput.writeLong(commerceAvailabilityEstimateId);

		if (CPDefinitionInventoryEngine == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(CPDefinitionInventoryEngine);
		}

		objectOutput.writeDouble(depth);

		objectOutput.writeBoolean(displayAvailability);

		objectOutput.writeBoolean(displayStockQuantity);

		objectOutput.writeBoolean(freeShipping);

		objectOutput.writeDouble(height);

		if (lowStockActivity == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lowStockActivity);
		}

		objectOutput.writeObject(maxOrderQuantity);
		objectOutput.writeObject(minOrderQuantity);
		objectOutput.writeObject(minStockQuantity);
		objectOutput.writeObject(multipleOrderQuantity);

		objectOutput.writeBoolean(purchasable);

		objectOutput.writeBoolean(shippable);

		objectOutput.writeDouble(shippingExtraPrice);

		objectOutput.writeBoolean(shipSeparately);

		objectOutput.writeBoolean(taxExempt);

		objectOutput.writeDouble(weight);

		objectOutput.writeDouble(width);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long CPConfigurationEntryId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long classNameId;
	public long classPK;
	public long CPConfigurationListId;
	public long CPTaxCategoryId;
	public String allowedOrderQuantities;
	public boolean backOrders;
	public long commerceAvailabilityEstimateId;
	public String CPDefinitionInventoryEngine;
	public double depth;
	public boolean displayAvailability;
	public boolean displayStockQuantity;
	public boolean freeShipping;
	public double height;
	public String lowStockActivity;
	public BigDecimal maxOrderQuantity;
	public BigDecimal minOrderQuantity;
	public BigDecimal minStockQuantity;
	public BigDecimal multipleOrderQuantity;
	public boolean purchasable;
	public boolean shippable;
	public double shippingExtraPrice;
	public boolean shipSeparately;
	public boolean taxExempt;
	public double weight;
	public double width;

}