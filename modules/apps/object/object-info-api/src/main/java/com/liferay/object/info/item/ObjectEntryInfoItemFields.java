/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.info.item;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.DateTimeInfoFieldType;
import com.liferay.info.field.type.FriendlyURLInfoFieldType;
import com.liferay.info.field.type.ImageInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;

/**
 * @author Jorge Ferrer
 */
public class ObjectEntryInfoItemFields {

	public static final InfoField<TextInfoFieldType> authorInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"author"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "author")
		).build();
	public static final InfoField<DateInfoFieldType> createDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"createDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "create-date")
		).build();
	public static final InfoField<DateTimeInfoFieldType>
		expirationDateInfoField = BuilderHolder._builder.infoFieldType(
			DateTimeInfoFieldType.INSTANCE
		).name(
			"expirationDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "expiration-date")
		).build();
	public static final InfoField<TextInfoFieldType>
		externalReferenceCodeInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"externalReferenceCode"
		).editable(
			true
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "external-reference-code")
		).build();
	public static final InfoField<DateInfoFieldType> modifiedDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"modifiedDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "modified-date")
		).build();
	public static final InfoField<TextInfoFieldType> objectEntryIdInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"objectEntryId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(ObjectEntryInfoItemFields.class, "id")
		).build();
	public static final InfoField<DateInfoFieldType> publishDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"publishDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "publish-date")
		).build();
	public static final InfoField<DateTimeInfoFieldType> reviewDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateTimeInfoFieldType.INSTANCE
		).name(
			"reviewDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "review-date")
		).build();
	public static final InfoField<TextInfoFieldType> statusInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"status"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "status")
		).build();
	public static final InfoField<ImageInfoFieldType>
		userProfileImageInfoField = BuilderHolder._builder.infoFieldType(
			ImageInfoFieldType.INSTANCE
		).name(
			"userProfileImage"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "user-profile-image")
		).build();

	public static InfoField<DateTimeInfoFieldType> getDisplayDateInfoField(
		ObjectDefinition objectDefinition) {

		return BuilderHolder._builder.infoFieldType(
			DateTimeInfoFieldType.INSTANCE
		).name(
			"displayDate"
		).editable(
			objectDefinition.isEnableObjectEntrySchedule()
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "display-date")
		).build();
	}

	public static InfoField<DateTimeInfoFieldType> getExpirationDateInfoField(
		ObjectDefinition objectDefinition) {

		return BuilderHolder._builder.infoFieldType(
			DateTimeInfoFieldType.INSTANCE
		).name(
			"expirationDate"
		).editable(
			objectDefinition.isEnableObjectEntrySchedule()
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "expiration-date")
		).build();
	}

	public static InfoField getFriendlyURLInfoField(
		boolean editable, String name, String namespace) {

		return InfoField.builder(
			namespace
		).infoFieldType(
			FriendlyURLInfoFieldType.INSTANCE
		).name(
			name + "_objectEntryFriendlyURL"
		).editable(
			editable
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "friendly-url")
		).localizable(
			true
		).build();
	}

	public static InfoField getFriendlyURLInfoField(
		ObjectDefinition objectDefinition) {

		return BuilderHolder._builder.infoFieldType(
			FriendlyURLInfoFieldType.INSTANCE
		).name(
			"objectEntryFriendlyURL"
		).editable(
			objectDefinition.isEnableFriendlyURLCustomization()
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "friendly-url")
		).localizable(
			true
		).build();
	}

	public static InfoField<DateTimeInfoFieldType> getReviewDateInfoField(
		ObjectDefinition objectDefinition) {

		return BuilderHolder._builder.infoFieldType(
			DateTimeInfoFieldType.INSTANCE
		).name(
			"reviewDate"
		).editable(
			objectDefinition.isEnableObjectEntrySchedule()
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				ObjectEntryInfoItemFields.class, "review-date")
		).build();
	}

	private static class BuilderHolder {

		private static final InfoField.NamespacedBuilder _builder =
			InfoField.builder(ObjectEntry.class.getSimpleName());

	}

}