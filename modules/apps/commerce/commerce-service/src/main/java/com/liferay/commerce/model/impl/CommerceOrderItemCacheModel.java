/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model.impl;

import com.liferay.commerce.model.CommerceOrderItem;
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
 * The cache model class for representing CommerceOrderItem in entity cache.
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
public class CommerceOrderItemCacheModel
	implements CacheModel<CommerceOrderItem>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CommerceOrderItemCacheModel)) {
			return false;
		}

		CommerceOrderItemCacheModel commerceOrderItemCacheModel =
			(CommerceOrderItemCacheModel)object;

		if ((commerceOrderItemId ==
				commerceOrderItemCacheModel.commerceOrderItemId) &&
			(mvccVersion == commerceOrderItemCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, commerceOrderItemId);

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
		StringBundler sb = new StringBundler(139);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", commerceOrderItemId=");
		sb.append(commerceOrderItemId);
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
		sb.append(", commerceInventoryBookedQuantityId=");
		sb.append(commerceInventoryBookedQuantityId);
		sb.append(", commerceOrderId=");
		sb.append(commerceOrderId);
		sb.append(", commercePriceListId=");
		sb.append(commercePriceListId);
		sb.append(", CPInstanceId=");
		sb.append(CPInstanceId);
		sb.append(", CPMeasurementUnitId=");
		sb.append(CPMeasurementUnitId);
		sb.append(", CProductId=");
		sb.append(CProductId);
		sb.append(", customerCommerceOrderItemId=");
		sb.append(customerCommerceOrderItemId);
		sb.append(", parentCommerceOrderItemId=");
		sb.append(parentCommerceOrderItemId);
		sb.append(", shippingAddressId=");
		sb.append(shippingAddressId);
		sb.append(", deliveryGroupName=");
		sb.append(deliveryGroupName);
		sb.append(", deliveryMaxSubscriptionCycles=");
		sb.append(deliveryMaxSubscriptionCycles);
		sb.append(", deliverySubscriptionLength=");
		sb.append(deliverySubscriptionLength);
		sb.append(", deliverySubscriptionType=");
		sb.append(deliverySubscriptionType);
		sb.append(", deliverySubscriptionTypeSettings=");
		sb.append(deliverySubscriptionTypeSettings);
		sb.append(", depth=");
		sb.append(depth);
		sb.append(", discountAmount=");
		sb.append(discountAmount);
		sb.append(", discountManuallyAdjusted=");
		sb.append(discountManuallyAdjusted);
		sb.append(", discountPercentageLevel1=");
		sb.append(discountPercentageLevel1);
		sb.append(", discountPercentageLevel2=");
		sb.append(discountPercentageLevel2);
		sb.append(", discountPercentageLevel3=");
		sb.append(discountPercentageLevel3);
		sb.append(", discountPercentageLevel4=");
		sb.append(discountPercentageLevel4);
		sb.append(", discountPercentageLevel1WithTaxAmount=");
		sb.append(discountPercentageLevel1WithTaxAmount);
		sb.append(", discountPercentageLevel2WithTaxAmount=");
		sb.append(discountPercentageLevel2WithTaxAmount);
		sb.append(", discountPercentageLevel3WithTaxAmount=");
		sb.append(discountPercentageLevel3WithTaxAmount);
		sb.append(", discountPercentageLevel4WithTaxAmount=");
		sb.append(discountPercentageLevel4WithTaxAmount);
		sb.append(", discountWithTaxAmount=");
		sb.append(discountWithTaxAmount);
		sb.append(", finalPrice=");
		sb.append(finalPrice);
		sb.append(", finalPriceWithTaxAmount=");
		sb.append(finalPriceWithTaxAmount);
		sb.append(", freeShipping=");
		sb.append(freeShipping);
		sb.append(", height=");
		sb.append(height);
		sb.append(", json=");
		sb.append(json);
		sb.append(", manuallyAdjusted=");
		sb.append(manuallyAdjusted);
		sb.append(", maxSubscriptionCycles=");
		sb.append(maxSubscriptionCycles);
		sb.append(", name=");
		sb.append(name);
		sb.append(", priceManuallyAdjusted=");
		sb.append(priceManuallyAdjusted);
		sb.append(", priceOnApplication=");
		sb.append(priceOnApplication);
		sb.append(", printedNote=");
		sb.append(printedNote);
		sb.append(", promoPrice=");
		sb.append(promoPrice);
		sb.append(", promoPriceWithTaxAmount=");
		sb.append(promoPriceWithTaxAmount);
		sb.append(", quantity=");
		sb.append(quantity);
		sb.append(", replacedCPInstanceId=");
		sb.append(replacedCPInstanceId);
		sb.append(", replacedSku=");
		sb.append(replacedSku);
		sb.append(", requestedDeliveryDate=");
		sb.append(requestedDeliveryDate);
		sb.append(", shipSeparately=");
		sb.append(shipSeparately);
		sb.append(", shippable=");
		sb.append(shippable);
		sb.append(", shippedQuantity=");
		sb.append(shippedQuantity);
		sb.append(", shippingExtraPrice=");
		sb.append(shippingExtraPrice);
		sb.append(", sku=");
		sb.append(sku);
		sb.append(", subscription=");
		sb.append(subscription);
		sb.append(", subscriptionLength=");
		sb.append(subscriptionLength);
		sb.append(", subscriptionType=");
		sb.append(subscriptionType);
		sb.append(", subscriptionTypeSettings=");
		sb.append(subscriptionTypeSettings);
		sb.append(", unitOfMeasureIncrementalOrderQuantity=");
		sb.append(unitOfMeasureIncrementalOrderQuantity);
		sb.append(", unitOfMeasureKey=");
		sb.append(unitOfMeasureKey);
		sb.append(", unitPrice=");
		sb.append(unitPrice);
		sb.append(", unitPriceWithTaxAmount=");
		sb.append(unitPriceWithTaxAmount);
		sb.append(", weight=");
		sb.append(weight);
		sb.append(", width=");
		sb.append(width);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CommerceOrderItem toEntityModel() {
		CommerceOrderItemImpl commerceOrderItemImpl =
			new CommerceOrderItemImpl();

		commerceOrderItemImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			commerceOrderItemImpl.setUuid("");
		}
		else {
			commerceOrderItemImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			commerceOrderItemImpl.setExternalReferenceCode("");
		}
		else {
			commerceOrderItemImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		commerceOrderItemImpl.setCommerceOrderItemId(commerceOrderItemId);
		commerceOrderItemImpl.setGroupId(groupId);
		commerceOrderItemImpl.setCompanyId(companyId);
		commerceOrderItemImpl.setUserId(userId);

		if (userName == null) {
			commerceOrderItemImpl.setUserName("");
		}
		else {
			commerceOrderItemImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			commerceOrderItemImpl.setCreateDate(null);
		}
		else {
			commerceOrderItemImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			commerceOrderItemImpl.setModifiedDate(null);
		}
		else {
			commerceOrderItemImpl.setModifiedDate(new Date(modifiedDate));
		}

		commerceOrderItemImpl.setCommerceInventoryBookedQuantityId(
			commerceInventoryBookedQuantityId);
		commerceOrderItemImpl.setCommerceOrderId(commerceOrderId);
		commerceOrderItemImpl.setCommercePriceListId(commercePriceListId);
		commerceOrderItemImpl.setCPInstanceId(CPInstanceId);
		commerceOrderItemImpl.setCPMeasurementUnitId(CPMeasurementUnitId);
		commerceOrderItemImpl.setCProductId(CProductId);
		commerceOrderItemImpl.setCustomerCommerceOrderItemId(
			customerCommerceOrderItemId);
		commerceOrderItemImpl.setParentCommerceOrderItemId(
			parentCommerceOrderItemId);
		commerceOrderItemImpl.setShippingAddressId(shippingAddressId);

		if (deliveryGroupName == null) {
			commerceOrderItemImpl.setDeliveryGroupName("");
		}
		else {
			commerceOrderItemImpl.setDeliveryGroupName(deliveryGroupName);
		}

		commerceOrderItemImpl.setDeliveryMaxSubscriptionCycles(
			deliveryMaxSubscriptionCycles);
		commerceOrderItemImpl.setDeliverySubscriptionLength(
			deliverySubscriptionLength);

		if (deliverySubscriptionType == null) {
			commerceOrderItemImpl.setDeliverySubscriptionType("");
		}
		else {
			commerceOrderItemImpl.setDeliverySubscriptionType(
				deliverySubscriptionType);
		}

		if (deliverySubscriptionTypeSettings == null) {
			commerceOrderItemImpl.setDeliverySubscriptionTypeSettings("");
		}
		else {
			commerceOrderItemImpl.setDeliverySubscriptionTypeSettings(
				deliverySubscriptionTypeSettings);
		}

		commerceOrderItemImpl.setDepth(depth);
		commerceOrderItemImpl.setDiscountAmount(discountAmount);
		commerceOrderItemImpl.setDiscountManuallyAdjusted(
			discountManuallyAdjusted);
		commerceOrderItemImpl.setDiscountPercentageLevel1(
			discountPercentageLevel1);
		commerceOrderItemImpl.setDiscountPercentageLevel2(
			discountPercentageLevel2);
		commerceOrderItemImpl.setDiscountPercentageLevel3(
			discountPercentageLevel3);
		commerceOrderItemImpl.setDiscountPercentageLevel4(
			discountPercentageLevel4);
		commerceOrderItemImpl.setDiscountPercentageLevel1WithTaxAmount(
			discountPercentageLevel1WithTaxAmount);
		commerceOrderItemImpl.setDiscountPercentageLevel2WithTaxAmount(
			discountPercentageLevel2WithTaxAmount);
		commerceOrderItemImpl.setDiscountPercentageLevel3WithTaxAmount(
			discountPercentageLevel3WithTaxAmount);
		commerceOrderItemImpl.setDiscountPercentageLevel4WithTaxAmount(
			discountPercentageLevel4WithTaxAmount);
		commerceOrderItemImpl.setDiscountWithTaxAmount(discountWithTaxAmount);
		commerceOrderItemImpl.setFinalPrice(finalPrice);
		commerceOrderItemImpl.setFinalPriceWithTaxAmount(
			finalPriceWithTaxAmount);
		commerceOrderItemImpl.setFreeShipping(freeShipping);
		commerceOrderItemImpl.setHeight(height);

		if (json == null) {
			commerceOrderItemImpl.setJson("");
		}
		else {
			commerceOrderItemImpl.setJson(json);
		}

		commerceOrderItemImpl.setManuallyAdjusted(manuallyAdjusted);
		commerceOrderItemImpl.setMaxSubscriptionCycles(maxSubscriptionCycles);

		if (name == null) {
			commerceOrderItemImpl.setName("");
		}
		else {
			commerceOrderItemImpl.setName(name);
		}

		commerceOrderItemImpl.setPriceManuallyAdjusted(priceManuallyAdjusted);
		commerceOrderItemImpl.setPriceOnApplication(priceOnApplication);

		if (printedNote == null) {
			commerceOrderItemImpl.setPrintedNote("");
		}
		else {
			commerceOrderItemImpl.setPrintedNote(printedNote);
		}

		commerceOrderItemImpl.setPromoPrice(promoPrice);
		commerceOrderItemImpl.setPromoPriceWithTaxAmount(
			promoPriceWithTaxAmount);
		commerceOrderItemImpl.setQuantity(quantity);
		commerceOrderItemImpl.setReplacedCPInstanceId(replacedCPInstanceId);

		if (replacedSku == null) {
			commerceOrderItemImpl.setReplacedSku("");
		}
		else {
			commerceOrderItemImpl.setReplacedSku(replacedSku);
		}

		if (requestedDeliveryDate == Long.MIN_VALUE) {
			commerceOrderItemImpl.setRequestedDeliveryDate(null);
		}
		else {
			commerceOrderItemImpl.setRequestedDeliveryDate(
				new Date(requestedDeliveryDate));
		}

		commerceOrderItemImpl.setShipSeparately(shipSeparately);
		commerceOrderItemImpl.setShippable(shippable);
		commerceOrderItemImpl.setShippedQuantity(shippedQuantity);
		commerceOrderItemImpl.setShippingExtraPrice(shippingExtraPrice);

		if (sku == null) {
			commerceOrderItemImpl.setSku("");
		}
		else {
			commerceOrderItemImpl.setSku(sku);
		}

		commerceOrderItemImpl.setSubscription(subscription);
		commerceOrderItemImpl.setSubscriptionLength(subscriptionLength);

		if (subscriptionType == null) {
			commerceOrderItemImpl.setSubscriptionType("");
		}
		else {
			commerceOrderItemImpl.setSubscriptionType(subscriptionType);
		}

		if (subscriptionTypeSettings == null) {
			commerceOrderItemImpl.setSubscriptionTypeSettings("");
		}
		else {
			commerceOrderItemImpl.setSubscriptionTypeSettings(
				subscriptionTypeSettings);
		}

		commerceOrderItemImpl.setUnitOfMeasureIncrementalOrderQuantity(
			unitOfMeasureIncrementalOrderQuantity);

		if (unitOfMeasureKey == null) {
			commerceOrderItemImpl.setUnitOfMeasureKey("");
		}
		else {
			commerceOrderItemImpl.setUnitOfMeasureKey(unitOfMeasureKey);
		}

		commerceOrderItemImpl.setUnitPrice(unitPrice);
		commerceOrderItemImpl.setUnitPriceWithTaxAmount(unitPriceWithTaxAmount);
		commerceOrderItemImpl.setWeight(weight);
		commerceOrderItemImpl.setWidth(width);
		commerceOrderItemImpl.setStatus(status);

		commerceOrderItemImpl.resetOriginalValues();

		return commerceOrderItemImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		commerceOrderItemId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		commerceInventoryBookedQuantityId = objectInput.readLong();

		commerceOrderId = objectInput.readLong();

		commercePriceListId = objectInput.readLong();

		CPInstanceId = objectInput.readLong();

		CPMeasurementUnitId = objectInput.readLong();

		CProductId = objectInput.readLong();

		customerCommerceOrderItemId = objectInput.readLong();

		parentCommerceOrderItemId = objectInput.readLong();

		shippingAddressId = objectInput.readLong();
		deliveryGroupName = objectInput.readUTF();

		deliveryMaxSubscriptionCycles = objectInput.readLong();

		deliverySubscriptionLength = objectInput.readInt();
		deliverySubscriptionType = objectInput.readUTF();
		deliverySubscriptionTypeSettings = objectInput.readUTF();

		depth = objectInput.readDouble();
		discountAmount = (BigDecimal)objectInput.readObject();

		discountManuallyAdjusted = objectInput.readBoolean();
		discountPercentageLevel1 = (BigDecimal)objectInput.readObject();
		discountPercentageLevel2 = (BigDecimal)objectInput.readObject();
		discountPercentageLevel3 = (BigDecimal)objectInput.readObject();
		discountPercentageLevel4 = (BigDecimal)objectInput.readObject();
		discountPercentageLevel1WithTaxAmount =
			(BigDecimal)objectInput.readObject();
		discountPercentageLevel2WithTaxAmount =
			(BigDecimal)objectInput.readObject();
		discountPercentageLevel3WithTaxAmount =
			(BigDecimal)objectInput.readObject();
		discountPercentageLevel4WithTaxAmount =
			(BigDecimal)objectInput.readObject();
		discountWithTaxAmount = (BigDecimal)objectInput.readObject();
		finalPrice = (BigDecimal)objectInput.readObject();
		finalPriceWithTaxAmount = (BigDecimal)objectInput.readObject();

		freeShipping = objectInput.readBoolean();

		height = objectInput.readDouble();
		json = (String)objectInput.readObject();

		manuallyAdjusted = objectInput.readBoolean();

		maxSubscriptionCycles = objectInput.readLong();
		name = objectInput.readUTF();

		priceManuallyAdjusted = objectInput.readBoolean();

		priceOnApplication = objectInput.readBoolean();
		printedNote = objectInput.readUTF();
		promoPrice = (BigDecimal)objectInput.readObject();
		promoPriceWithTaxAmount = (BigDecimal)objectInput.readObject();
		quantity = (BigDecimal)objectInput.readObject();

		replacedCPInstanceId = objectInput.readLong();
		replacedSku = objectInput.readUTF();
		requestedDeliveryDate = objectInput.readLong();

		shipSeparately = objectInput.readBoolean();

		shippable = objectInput.readBoolean();
		shippedQuantity = (BigDecimal)objectInput.readObject();

		shippingExtraPrice = objectInput.readDouble();
		sku = objectInput.readUTF();

		subscription = objectInput.readBoolean();

		subscriptionLength = objectInput.readInt();
		subscriptionType = objectInput.readUTF();
		subscriptionTypeSettings = objectInput.readUTF();
		unitOfMeasureIncrementalOrderQuantity =
			(BigDecimal)objectInput.readObject();
		unitOfMeasureKey = objectInput.readUTF();
		unitPrice = (BigDecimal)objectInput.readObject();
		unitPriceWithTaxAmount = (BigDecimal)objectInput.readObject();

		weight = objectInput.readDouble();

		width = objectInput.readDouble();

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

		objectOutput.writeLong(commerceOrderItemId);

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

		objectOutput.writeLong(commerceInventoryBookedQuantityId);

		objectOutput.writeLong(commerceOrderId);

		objectOutput.writeLong(commercePriceListId);

		objectOutput.writeLong(CPInstanceId);

		objectOutput.writeLong(CPMeasurementUnitId);

		objectOutput.writeLong(CProductId);

		objectOutput.writeLong(customerCommerceOrderItemId);

		objectOutput.writeLong(parentCommerceOrderItemId);

		objectOutput.writeLong(shippingAddressId);

		if (deliveryGroupName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(deliveryGroupName);
		}

		objectOutput.writeLong(deliveryMaxSubscriptionCycles);

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
		objectOutput.writeObject(discountAmount);

		objectOutput.writeBoolean(discountManuallyAdjusted);
		objectOutput.writeObject(discountPercentageLevel1);
		objectOutput.writeObject(discountPercentageLevel2);
		objectOutput.writeObject(discountPercentageLevel3);
		objectOutput.writeObject(discountPercentageLevel4);
		objectOutput.writeObject(discountPercentageLevel1WithTaxAmount);
		objectOutput.writeObject(discountPercentageLevel2WithTaxAmount);
		objectOutput.writeObject(discountPercentageLevel3WithTaxAmount);
		objectOutput.writeObject(discountPercentageLevel4WithTaxAmount);
		objectOutput.writeObject(discountWithTaxAmount);
		objectOutput.writeObject(finalPrice);
		objectOutput.writeObject(finalPriceWithTaxAmount);

		objectOutput.writeBoolean(freeShipping);

		objectOutput.writeDouble(height);

		if (json == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(json);
		}

		objectOutput.writeBoolean(manuallyAdjusted);

		objectOutput.writeLong(maxSubscriptionCycles);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeBoolean(priceManuallyAdjusted);

		objectOutput.writeBoolean(priceOnApplication);

		if (printedNote == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(printedNote);
		}

		objectOutput.writeObject(promoPrice);
		objectOutput.writeObject(promoPriceWithTaxAmount);
		objectOutput.writeObject(quantity);

		objectOutput.writeLong(replacedCPInstanceId);

		if (replacedSku == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(replacedSku);
		}

		objectOutput.writeLong(requestedDeliveryDate);

		objectOutput.writeBoolean(shipSeparately);

		objectOutput.writeBoolean(shippable);
		objectOutput.writeObject(shippedQuantity);

		objectOutput.writeDouble(shippingExtraPrice);

		if (sku == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sku);
		}

		objectOutput.writeBoolean(subscription);

		objectOutput.writeInt(subscriptionLength);

		if (subscriptionType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(subscriptionType);
		}

		if (subscriptionTypeSettings == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(subscriptionTypeSettings);
		}

		objectOutput.writeObject(unitOfMeasureIncrementalOrderQuantity);

		if (unitOfMeasureKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(unitOfMeasureKey);
		}

		objectOutput.writeObject(unitPrice);
		objectOutput.writeObject(unitPriceWithTaxAmount);

		objectOutput.writeDouble(weight);

		objectOutput.writeDouble(width);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long commerceOrderItemId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long commerceInventoryBookedQuantityId;
	public long commerceOrderId;
	public long commercePriceListId;
	public long CPInstanceId;
	public long CPMeasurementUnitId;
	public long CProductId;
	public long customerCommerceOrderItemId;
	public long parentCommerceOrderItemId;
	public long shippingAddressId;
	public String deliveryGroupName;
	public long deliveryMaxSubscriptionCycles;
	public int deliverySubscriptionLength;
	public String deliverySubscriptionType;
	public String deliverySubscriptionTypeSettings;
	public double depth;
	public BigDecimal discountAmount;
	public boolean discountManuallyAdjusted;
	public BigDecimal discountPercentageLevel1;
	public BigDecimal discountPercentageLevel2;
	public BigDecimal discountPercentageLevel3;
	public BigDecimal discountPercentageLevel4;
	public BigDecimal discountPercentageLevel1WithTaxAmount;
	public BigDecimal discountPercentageLevel2WithTaxAmount;
	public BigDecimal discountPercentageLevel3WithTaxAmount;
	public BigDecimal discountPercentageLevel4WithTaxAmount;
	public BigDecimal discountWithTaxAmount;
	public BigDecimal finalPrice;
	public BigDecimal finalPriceWithTaxAmount;
	public boolean freeShipping;
	public double height;
	public String json;
	public boolean manuallyAdjusted;
	public long maxSubscriptionCycles;
	public String name;
	public boolean priceManuallyAdjusted;
	public boolean priceOnApplication;
	public String printedNote;
	public BigDecimal promoPrice;
	public BigDecimal promoPriceWithTaxAmount;
	public BigDecimal quantity;
	public long replacedCPInstanceId;
	public String replacedSku;
	public long requestedDeliveryDate;
	public boolean shipSeparately;
	public boolean shippable;
	public BigDecimal shippedQuantity;
	public double shippingExtraPrice;
	public String sku;
	public boolean subscription;
	public int subscriptionLength;
	public String subscriptionType;
	public String subscriptionTypeSettings;
	public BigDecimal unitOfMeasureIncrementalOrderQuantity;
	public String unitOfMeasureKey;
	public BigDecimal unitPrice;
	public BigDecimal unitPriceWithTaxAmount;
	public double weight;
	public double width;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:-326351220