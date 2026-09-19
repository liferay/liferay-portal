/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;

/**
 * @author Eudaldo Alonso
 */
public class CPDefinitionInfoItemFields {

	public static final InfoField<BooleanInfoFieldType>
		accountGroupFilterEnabledInfoField =
			BuilderHolder._builder.infoFieldType(
				BooleanInfoFieldType.INSTANCE
			).name(
				"accountGroupFilterEnabled"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CPDefinitionInfoItemFields.class,
					"account-group-filter-enabled")
			).build();
	public static final InfoField<BooleanInfoFieldType> approvedInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"approved"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "approved")
		).build();
	public static final InfoField<TextInfoFieldType>
		availabilityStatusInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"availabilityStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "availability-status")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		availableIndividuallyInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"availableIndividually"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "available-individually")
		).build();
	public static final InfoField<NumberInfoFieldType> cProductIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"cProductId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "cProductId")
		).build();
	public static final InfoField<TextInfoFieldType> categoriesInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"categories"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "all-categories")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		channelFilterEnabledInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"channelFilterEnabled"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "channel-filter-enabled")
		).build();
	public static final InfoField<NumberInfoFieldType> companyIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"companyId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "company-id")
		).build();
	public static final InfoField<NumberInfoFieldType> cpDefinitionIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"cpDefinitionId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "cpDefinitionId")
		).build();
	public static final InfoField<NumberInfoFieldType>
		cpTaxCategoryIdInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"cpTaxCategoryId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "cpTaxCategoryId")
		).build();
	public static final InfoField<DateInfoFieldType> createDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"createDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "create-date")
		).build();
	public static final InfoField<TextInfoFieldType> ddmStructureKeyInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"ddmStructureKey"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "ddm-structure-key")
		).build();
	public static final InfoField<TextInfoFieldType>
		defaultLanguageIdInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"defaultLanguageId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "default-languageId")
		).build();
	public static final InfoField<NumberInfoFieldType>
		deliveryMaxSubscriptionCyclesInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"deliveryMaxSubscriptionCycles"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CPDefinitionInfoItemFields.class,
					"delivery-max-subscription-cycles")
			).build();
	public static final InfoField<BooleanInfoFieldType>
		deliverySubscriptionEnabledInfoField =
			BuilderHolder._builder.infoFieldType(
				BooleanInfoFieldType.INSTANCE
			).name(
				"deliverySubscriptionEnabled"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CPDefinitionInfoItemFields.class,
					"delivery-subscription-enabled")
			).build();
	public static final InfoField<NumberInfoFieldType>
		deliverySubscriptionLengthInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"deliverySubscriptionLength"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CPDefinitionInfoItemFields.class,
					"delivery-subscription-length")
			).build();
	public static final InfoField<TextInfoFieldType>
		deliverySubscriptionTypeInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"deliverySubscriptionType"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CPDefinitionInfoItemFields.class,
					"delivery-subscription-type")
			).build();
	public static final InfoField<TextInfoFieldType>
		deliverySubscriptionTypeSettingsInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"deliverySubscriptionTypeSettings"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CPDefinitionInfoItemFields.class,
					"delivery-subscription-type-settings")
			).build();
	public static final InfoField<BooleanInfoFieldType> deniedInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"denied"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "denied")
		).build();
	public static final InfoField<NumberInfoFieldType> depthInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"depth"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "depth")
		).build();
	public static final InfoField<HTMLInfoFieldType> descriptionInfoField =
		BuilderHolder._builder.infoFieldType(
			HTMLInfoFieldType.INSTANCE
		).name(
			"description"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "description")
		).build();
	public static final InfoField<DateInfoFieldType> displayDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"displayDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "display-date")
		).build();
	public static final InfoField<BooleanInfoFieldType> draftInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"draft"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "draft")
		).build();
	public static final InfoField<DateInfoFieldType> expirationDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"expirationDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "expiration-date")
		).build();
	public static final InfoField<BooleanInfoFieldType> expiredInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"expired"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "expired")
		).build();
	public static final InfoField<TextInfoFieldType> finalPriceInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"finalPrice"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "final-price")
		).build();
	public static final InfoField<BooleanInfoFieldType> freeShippingInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"freeShipping"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "free-shipping")
		).build();
	public static final InfoField<NumberInfoFieldType> groupIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"groupId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "group-id")
		).build();
	public static final InfoField<NumberInfoFieldType> heightInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"height"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "height")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		ignoreSKUCombinationsInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"ignoreSKUCombinations"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "ignore-sku-combinations")
		).build();
	public static final InfoField<BooleanInfoFieldType> inactiveInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"inactive"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "inactive")
		).build();
	public static final InfoField<BooleanInfoFieldType> incompleteInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"incomplete"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "incomplete")
		).build();
	public static final InfoField<NumberInfoFieldType> inventoryInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"inventory"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "inventory")
		).build();
	public static final InfoField<DateInfoFieldType> lastPublishDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"lastPublishDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "last-publish-date")
		).build();
	public static final InfoField<NumberInfoFieldType>
		maxSubscriptionCyclesInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"maxSubscriptionCycles"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "max-subscription-cycles")
		).build();
	public static final InfoField<TextInfoFieldType> metaDescriptionInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"metaDescription"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "meta-description")
		).build();
	public static final InfoField<TextInfoFieldType> metaKeywordsInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"metaKeywords"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "meta-keywords")
		).build();
	public static final InfoField<TextInfoFieldType> metaTitleInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"metaTitle"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "meta-title")
		).build();
	public static final InfoField<DateInfoFieldType> modifiedDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"modifiedDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "modified-date")
		).build();
	public static final InfoField<TextInfoFieldType> nameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"name"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "name")
		).build();
	public static final InfoField<BooleanInfoFieldType> pendingInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"pending"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "pending")
		).build();
	public static final InfoField<TextInfoFieldType> productTypeNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"productTypeName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "product-type")
		).build();
	public static final InfoField<BooleanInfoFieldType> publishedInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"published"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "published")
		).build();
	public static final InfoField<BooleanInfoFieldType> scheduledInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"scheduled"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "scheduled")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		shipSeparatelyPriceInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"shipSeparately"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "ship-separately")
		).build();
	public static final InfoField<BooleanInfoFieldType> shippableInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"shippable"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "shippable")
		).build();
	public static final InfoField<NumberInfoFieldType>
		shippingExtraPriceInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"shippingExtraPrice"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "shipping-extra-price")
		).build();
	public static final InfoField<TextInfoFieldType> shortDescriptionInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"shortDescription"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "short-description")
		).build();
	public static final InfoField<TextInfoFieldType> skuInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"sku"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(CPDefinitionInfoItemFields.class, "sku")
		).build();
	public static final InfoField<TextInfoFieldType> stagedModelTypeInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"stagedModelType"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "staged-model-type")
		).build();
	public static final InfoField<TextInfoFieldType> statusByUserIdInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"statusByUserId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "status-by-userId")
		).build();
	public static final InfoField<TextInfoFieldType> statusByUserNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"statusByUserName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "status-by-userName")
		).build();
	public static final InfoField<TextInfoFieldType> statusByUserUuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"statusByUserUuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "status-by-userUuid")
		).build();
	public static final InfoField<DateInfoFieldType> statusDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"statusDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "status-date")
		).build();
	public static final InfoField<TextInfoFieldType> statusInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"status"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "status")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		subscriptionEnabledInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"subscriptionEnabled"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "subscription-enabled")
		).build();
	public static final InfoField<NumberInfoFieldType>
		subscriptionLengthInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"subscriptionLength"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "subscription-length")
		).build();
	public static final InfoField<TextInfoFieldType> subscriptionTypeInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"subscriptionType"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "subscription-type")
		).build();
	public static final InfoField<TextInfoFieldType>
		subscriptionTypeSettingsInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"subscriptionTypeSettings"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CPDefinitionInfoItemFields.class,
					"subscription-type-settings")
			).build();
	public static final InfoField<BooleanInfoFieldType> taxExemptInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"taxExempt"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "tax-exempt")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		telcoOrElectronicsInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"telcoOrElectronics"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "telco-or-electronics")
		).build();
	public static final InfoField<NumberInfoFieldType> userIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"userId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "userId")
		).build();
	public static final InfoField<TextInfoFieldType> userNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"userName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "author-name")
		).build();
	public static final InfoField<TextInfoFieldType> userUuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"userUuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "userUuid")
		).build();
	public static final InfoField<TextInfoFieldType> uuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"uuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "uuid")
		).build();
	public static final InfoField<NumberInfoFieldType> versionInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"version"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "version")
		).build();
	public static final InfoField<NumberInfoFieldType> weightInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"weight"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "weight")
		).build();
	public static final InfoField<NumberInfoFieldType> widthInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"width"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPDefinitionInfoItemFields.class, "width")
		).build();

	private static class BuilderHolder {

		private static final InfoField.NamespacedBuilder _builder =
			InfoField.builder(CPDefinition.class.getSimpleName());

	}

}