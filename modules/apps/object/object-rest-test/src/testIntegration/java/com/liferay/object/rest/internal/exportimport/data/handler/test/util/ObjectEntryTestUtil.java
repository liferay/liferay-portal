/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.exportimport.data.handler.test.util;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Alberto Javier Moreno Lage
 */
public class ObjectEntryTestUtil {

	public static ObjectEntry addEmptyObjectEntry(
			ObjectDefinition objectDefinition, String externalReferenceCode,
			long groupId, long userId)
		throws Exception {

		objectDefinition = _getObjectDefinition(objectDefinition, groupId);

		return ObjectEntryLocalServiceUtil.getOrAddEmptyObjectEntry(
			externalReferenceCode, _getGroupId(objectDefinition, groupId),
			userId, objectDefinition.getObjectDefinitionId());
	}

	public static ObjectEntry addObjectEntry(
			ObjectDefinition objectDefinition, String externalReferenceCode,
			long groupId, long userId)
		throws Exception {

		objectDefinition = _getObjectDefinition(objectDefinition, groupId);

		groupId = _getGroupId(objectDefinition, groupId);

		return ObjectEntryLocalServiceUtil.addOrUpdateObjectEntry(
			externalReferenceCode, groupId, userId,
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			Collections.singletonMap(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(
				objectDefinition.getCompanyId(), groupId, userId));
	}

	public static ObjectEntry fetchObjectEntry(
			ObjectDefinition objectDefinition, String externalReferenceCode,
			long groupId)
		throws Exception {

		objectDefinition = _getObjectDefinition(objectDefinition, groupId);

		return ObjectEntryLocalServiceUtil.fetchObjectEntry(
			externalReferenceCode, _getGroupId(objectDefinition, groupId),
			objectDefinition.getObjectDefinitionId());
	}

	public static String getEntityClassName(ObjectDefinition objectDefinition) {
		return StringBundler.concat(
			com.liferay.object.rest.dto.v1_0.ObjectEntry.class.getName(), "#",
			objectDefinition.getName());
	}

	public static ObjectDefinition publishObjectDefinition(
			String externalReferenceCode, String name,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			String scope, long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
				externalReferenceCode, userId, 0, null, null, true, true, false,
				false, true, false, false, false, false,
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				name, null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				objectDefinitionSettings, Collections.emptyList(),
				Collections.emptyList(), new ServiceContext());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				userId
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(_OBJECT_FIELD_NAME)
			).name(
				_OBJECT_FIELD_NAME
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				false
			).build());

		ObjectDefinitionLocalServiceUtil.updateTitleObjectFieldId(
			objectDefinition.getObjectDefinitionId(),
			objectField.getObjectFieldId());

		return ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
			userId, objectDefinition.getObjectDefinitionId());
	}

	public static ObjectEntry updateModifiedDate(
			ObjectEntry objectEntry, Date modifiedDate)
		throws Exception {

		objectEntry.setModifiedDate(modifiedDate);

		return ObjectEntryLocalServiceUtil.updateObjectEntry(objectEntry);
	}

	public static ObjectEntry updateObjectEntry(ObjectEntry objectEntry)
		throws Exception {

		return ObjectEntryLocalServiceUtil.updateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			Collections.singletonMap(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(
				objectEntry.getCompanyId(), objectEntry.getGroupId(),
				objectEntry.getUserId()));
	}

	public static ObjectEntry updateUserId(ObjectEntry objectEntry, long userId)
		throws Exception {

		objectEntry.setUserId(userId);

		return ObjectEntryLocalServiceUtil.updateObjectEntry(objectEntry);
	}

	private static long _getGroupId(
		ObjectDefinition objectDefinition, long groupId) {

		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			return 0;
		}

		return groupId;
	}

	private static ObjectDefinition _getObjectDefinition(
			ObjectDefinition objectDefinition, long groupId)
		throws Exception {

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		return ObjectDefinitionLocalServiceUtil.
			getObjectDefinitionByExternalReferenceCode(
				objectDefinition.getExternalReferenceCode(),
				group.getCompanyId());
	}

	private static final String _OBJECT_FIELD_NAME =
		"x" + RandomTestUtil.randomString();

}