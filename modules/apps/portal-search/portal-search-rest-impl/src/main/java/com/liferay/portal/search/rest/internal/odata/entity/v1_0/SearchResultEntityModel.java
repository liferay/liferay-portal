/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.odata.entity.BooleanEntityField;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;

import java.util.Map;

/**
 * @author Petteri Karttunen
 */
public class SearchResultEntityModel implements EntityModel {

	public SearchResultEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new BooleanEntityField("cmsRoot", locale -> "cms_root"),
			new BooleanEntityField("completed", locale -> "completed"),
			new BooleanEntityField(
				"rootDescendantNode", locale -> "rootDescendantNode"),
			new CollectionEntityField(
				new IntegerEntityField(
					"cmpFunnelStageCategoryIds",
					locale -> Field.ASSET_INTERNAL_CATEGORY_IDS)),
			new CollectionEntityField(
				new IntegerEntityField(
					"cmpPersonaCategoryIds",
					locale -> Field.ASSET_INTERNAL_CATEGORY_IDS)),
			new CollectionEntityField(
				new IntegerEntityField(
					"cmpProjectObjectEntryIds",
					locale -> "cmpProjectObjectEntryIds")),
			new CollectionEntityField(
				new IntegerEntityField(
					"cmpTaskObjectEntryIds",
					locale -> "cmpTaskObjectEntryIds")),
			new CollectionEntityField(
				new IntegerEntityField("groupIds", locale -> Field.GROUP_ID)),
			new CollectionEntityField(
				new IntegerEntityField(
					"internalTaxonomyCategoryIds",
					locale -> Field.ASSET_INTERNAL_CATEGORY_IDS)),
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
			new CollectionEntityField(
				new StringEntityField("treePath", locale -> Field.TREE_PATH)),
			new DateTimeEntityField(
				"cmpDueDate", locale -> "cmpDueDate", locale -> "cmpDueDate"),
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
			new DateTimeEntityField(
				"dueDate", locale -> Field.getSortableFieldName("dueDate"),
				locale -> "dueDate"),
			new EntityField(
				"classNameId", EntityField.Type.INTEGER,
				locale -> Field.ENTRY_CLASS_NAME,
				locale -> Field.ENTRY_CLASS_NAME,
				value -> PortalUtil.getClassName(GetterUtil.getLong(value))),
			new IntegerEntityField(
				"cmpProjectManagerUserId", locale -> "cmpProjectManagerUserId"),
			new IntegerEntityField(
				"cmpProjectSponsorUserId", locale -> "cmpProjectSponsorUserId"),
			new IntegerEntityField(
				"cmpTaskCMPProjectId", locale -> "cmpTaskCMPProjectId"),
			new IntegerEntityField("creatorId", locale -> Field.USER_ID),
			new IntegerEntityField(
				"entryClassPK", locale -> Field.ENTRY_CLASS_PK),
			new IntegerEntityField("folderId", locale -> Field.FOLDER_ID),
			new IntegerEntityField(
				"objectDefinitionId", locale -> "objectDefinitionId"),
			new IntegerEntityField("scopeGroupId", locale -> "scopeGroupId"),
			new IntegerEntityField("status", locale -> Field.STATUS),
			new StringEntityField("cmpAssignTo", locale -> "cmpAssignTo"),
			new StringEntityField("cmpState", locale -> "cmpState"),
			new StringEntityField("cmsSection", locale -> "cms_section"),
			new StringEntityField("extension", locale -> "extension"),
			new StringEntityField(
				"objectDefinitionExternalReferenceCode",
				locale -> "objectDefinitionExternalReferenceCode"),
			new StringEntityField(
				"title",
				locale -> Field.getSortableFieldName(
					"localized_title_".concat(LocaleUtil.toLanguageId(locale))),
				locale -> {
					String sortableFieldName = Field.getSortableFieldName(
						"localized_title_".concat(
							LocaleUtil.toLanguageId(locale)));

					return sortableFieldName.concat(".keyword_lowercase");
				}));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}