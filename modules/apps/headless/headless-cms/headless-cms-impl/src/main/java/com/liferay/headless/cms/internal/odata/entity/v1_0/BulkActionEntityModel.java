/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.BooleanEntityField;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;

import java.util.Map;

/**
 * @author Luca Pellizzon
 */
public class BulkActionEntityModel implements EntityModel {

	public BulkActionEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new BooleanEntityField("cmsRoot", locale -> "cms_root"),
			new CollectionEntityField(
				new IntegerEntityField("groupIds", locale -> Field.GROUP_ID)),
			new CollectionEntityField(
				new IntegerEntityField(
					"taxonomyCategoryIds", locale -> "assetCategoryIds")),
			new CollectionEntityField(
				new StringEntityField(
					"keywords", locale -> "assetTagNames.lowercase")),
			new CollectionEntityField(
				new StringEntityField(
					"objectFolderExternalReferenceCode",
					locale -> "objectFolderExternalReferenceCode")),
			new DateTimeEntityField(
				"dateCreated",
				locale -> Field.getSortableFieldName(Field.CREATE_DATE),
				locale -> Field.CREATE_DATE),
			new DateTimeEntityField(
				"dateDisplay",
				locale -> Field.getSortableFieldName(Field.DISPLAY_DATE),
				locale -> Field.DISPLAY_DATE),
			new DateTimeEntityField(
				"dateExpiration",
				locale -> Field.getSortableFieldName(Field.EXPIRATION_DATE),
				locale -> Field.EXPIRATION_DATE),
			new DateTimeEntityField(
				"dateModified",
				locale -> Field.getSortableFieldName(Field.MODIFIED_DATE),
				locale -> Field.MODIFIED_DATE),
			new DateTimeEntityField(
				"datePublish",
				locale -> Field.getSortableFieldName(Field.PUBLISH_DATE),
				locale -> Field.PUBLISH_DATE),
			new DateTimeEntityField(
				"dateReview",
				locale -> Field.getSortableFieldName("reviewDate"),
				locale -> "reviewDate"),
			new IntegerEntityField("creatorId", locale -> Field.USER_ID),
			new IntegerEntityField("folderId", locale -> Field.FOLDER_ID),
			new IntegerEntityField(
				"objectDefinitionId", locale -> "objectDefinitionId"),
			new IntegerEntityField("scopeGroupId", locale -> "scopeGroupId"),
			new IntegerEntityField("status", locale -> Field.STATUS),
			new StringEntityField("cmsKind", locale -> "cms_kind"),
			new StringEntityField("cmsSection", locale -> "cms_section"),
			new StringEntityField("name", locale -> "name"),
			new StringEntityField(
				"title",
				locale -> Field.getSortableFieldName(
					"localized_title_".concat(LocaleUtil.toLanguageId(locale))),
				locale -> {
					String sortableFieldName = Field.getSortableFieldName(
						"localized_title_".concat(
							LocaleUtil.toLanguageId(locale)));

					return sortableFieldName.concat(".keyword_lowercase");
				}),
			new StringEntityField("type", locale -> "type"),
			new StringEntityField("usages", locale -> "usages"));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}