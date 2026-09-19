/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.info;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceOrderItemInfoItemFields {

	public static final InfoField<TextInfoFieldType> URLInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"URL"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "url")
		).build();
	public static final InfoField<NumberInfoFieldType> companyIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"companyId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "company-id")
		).build();
	public static final InfoField<DateInfoFieldType> createDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"createDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "create-date")
		).build();
	public static final InfoField<TextInfoFieldType>
		defaultLanguageIdInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"defaultLanguageId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "default-languageId")
		).build();
	public static final InfoField<TextInfoFieldType> discountAmountInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"discountAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "discount")
		).build();
	public static final InfoField<NumberInfoFieldType> groupIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"groupId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "group-id")
		).build();
	public static final InfoField<DateInfoFieldType> modifiedDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"modifiedDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "modified-date")
		).build();
	public static final InfoField<TextInfoFieldType> nameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"name"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "name")
		).build();
	public static final InfoField<TextInfoFieldType> optionsInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"options"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "options")
		).build();
	public static final InfoField<NumberInfoFieldType> orderIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"orderId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "order-id")
		).build();
	public static final InfoField<NumberInfoFieldType> orderItemIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"orderItemId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "order-item-id")
		).build();
	public static final InfoField<TextInfoFieldType> promoPriceInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"promoPrice"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "promo-price")
		).build();
	public static final InfoField<NumberInfoFieldType> quantityInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"quantity"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "quantity")
		).build();
	public static final InfoField<TextInfoFieldType> skuInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"SKU"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "sku")
		).build();
	public static final InfoField<TextInfoFieldType> stagedModelTypeInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"stagedModelType"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "staged-model-type")
		).build();
	public static final InfoField<TextInfoFieldType> thumbnailURLInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"thumbnailURL"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "thumbnail-url")
		).build();
	public static final InfoField<TextInfoFieldType> totalPriceInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"totalPrice"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "total-price")
		).build();
	public static final InfoField<TextInfoFieldType> unitPriceInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"unitPrice"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "unit-price")
		).build();
	public static final InfoField<NumberInfoFieldType> userIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"userId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "userId")
		).build();
	public static final InfoField<TextInfoFieldType> userNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"userName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "author-name")
		).build();
	public static final InfoField<TextInfoFieldType> userUuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"userUuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "userUuid")
		).build();
	public static final InfoField<TextInfoFieldType> uuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"uuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderItemInfoItemFields.class, "uuid")
		).build();

	private static class BuilderHolder {

		private static final InfoField.NamespacedBuilder _builder =
			InfoField.builder(CPAttachmentFileEntry.class.getSimpleName());

	}

}