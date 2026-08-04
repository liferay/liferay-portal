/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.field.type.URLInfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;

/**
 * @author Alessio Antonio Rendina
 */
public class CPAttachmentFileEntryInfoItemFields {

	public static final InfoField<BooleanInfoFieldType> approvedInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"approved"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "approved")
		).build();
	public static final InfoField<BooleanInfoFieldType> CDNEnabledInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"CDNEnabled"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "cdn-enabled")
		).build();
	public static final InfoField<NumberInfoFieldType> companyIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"companyId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "company-id")
		).build();
	public static final InfoField<NumberInfoFieldType>
		cpAttachmentFileEntryIdInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"cpAttachmentFileEntryId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class,
				"cpAttachmentFileEntryId")
		).build();
	public static final InfoField<NumberInfoFieldType> cpDefinitionIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"cpDefinitionId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "cpDefinitionId")
		).build();
	public static final InfoField<DateInfoFieldType> createDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"createDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "create-date")
		).build();
	public static final InfoField<TextInfoFieldType>
		defaultLanguageIdInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"defaultLanguageId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "default-languageId")
		).build();
	public static final InfoField<DateInfoFieldType> displayDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"displayDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "display-date")
		).build();
	public static final InfoField<URLInfoFieldType> downloadURLInfoField =
		BuilderHolder._builder.infoFieldType(
			URLInfoFieldType.INSTANCE
		).name(
			"downloadURL"
		).attribute(
			URLInfoFieldType.DOWNLOAD, Boolean.TRUE
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "download-url")
		).build();
	public static final InfoField<BooleanInfoFieldType> draftInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"draft"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "draft")
		).build();
	public static final InfoField<DateInfoFieldType> expirationDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"expirationDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "expiration-date")
		).build();
	public static final InfoField<BooleanInfoFieldType> expiredInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"expired"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "expired")
		).build();
	public static final InfoField<NumberInfoFieldType> fileEntryIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"fileEntryId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "fileEntryId")
		).build();
	public static final InfoField<NumberInfoFieldType> groupIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"groupId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "group-id")
		).build();
	public static final InfoField<BooleanInfoFieldType> inactiveInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"inactive"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "inactive")
		).build();
	public static final InfoField<BooleanInfoFieldType> incompleteInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"incomplete"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "incomplete")
		).build();
	public static final InfoField<DateInfoFieldType> lastPublishDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"lastPublishDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "last-publish-date")
		).build();
	public static final InfoField<TextInfoFieldType> mimeTypeInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"mimeType"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "mime-type")
		).build();
	public static final InfoField<DateInfoFieldType> modifiedDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"modifiedDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "modified-date")
		).build();
	public static final InfoField<TextInfoFieldType> optionsInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"options"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "options")
		).build();
	public static final InfoField<NumberInfoFieldType> priorityInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"priority"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "priority")
		).build();
	public static final InfoField<BooleanInfoFieldType> scheduledInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"scheduled"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "scheduled")
		).build();
	public static final InfoField<NumberInfoFieldType> sizeInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"size"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "size")
		).build();
	public static final InfoField<TextInfoFieldType> stagedModelTypeInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"stagedModelType"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "staged-model-type")
		).build();
	public static final InfoField<TextInfoFieldType> statusByUserIdInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"statusByUserId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "status-by-userId")
		).build();
	public static final InfoField<TextInfoFieldType> statusByUserNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"statusByUserName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "status-by-userName")
		).build();
	public static final InfoField<TextInfoFieldType> statusByUserUuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"statusByUserUuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "status-by-userUuid")
		).build();
	public static final InfoField<DateInfoFieldType> statusDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"statusDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "status-date")
		).build();
	public static final InfoField<TextInfoFieldType> statusInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"status"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "status")
		).build();
	public static final InfoField<TextInfoFieldType> thumbnailURLInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"thumbnailURL"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "thumbnail-url")
		).build();
	public static final InfoField<TextInfoFieldType> titleInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"title"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "title")
		).build();
	public static final InfoField<TextInfoFieldType> URLInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"URL"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "url")
		).build();
	public static final InfoField<NumberInfoFieldType> userIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"userId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "userId")
		).build();
	public static final InfoField<TextInfoFieldType> userNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"userName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "author-name")
		).build();
	public static final InfoField<TextInfoFieldType> userUuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"userUuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "userUuid")
		).build();
	public static final InfoField<TextInfoFieldType> uuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"uuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CPAttachmentFileEntryInfoItemFields.class, "uuid")
		).build();

	private static class BuilderHolder {

		private static final InfoField.NamespacedBuilder _builder =
			InfoField.builder(CPAttachmentFileEntry.class.getSimpleName());

	}

}