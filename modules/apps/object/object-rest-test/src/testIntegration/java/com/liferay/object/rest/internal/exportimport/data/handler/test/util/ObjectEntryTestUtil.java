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

	public static ObjectEntry addObjectEntry(
			String externalReferenceCode, long groupId,
			ObjectDefinition objectDefinition, long userId)
		throws Exception {

		objectDefinition = _getObjectDefinition(groupId, objectDefinition);

		groupId = _getGroupId(groupId, objectDefinition);

		return ObjectEntryLocalServiceUtil.addOrUpdateObjectEntry(
			externalReferenceCode, groupId, userId,
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			Collections.singletonMap(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(
				objectDefinition.getCompanyId(), groupId, userId));
	}

	public static void deleteObjectEntry(
			String externalReferenceCode, long groupId,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntryLocalServiceUtil.deleteObjectEntry(
			_getObjectEntry(externalReferenceCode, groupId, objectDefinition));
	}

	public static ObjectEntry fetchObjectEntry(
		String externalReferenceCode, long groupId,
		ObjectDefinition objectDefinition) {

		objectDefinition = _fetchObjectDefinition(groupId, objectDefinition);

		if (objectDefinition == null) {
			return null;
		}

		return ObjectEntryLocalServiceUtil.fetchObjectEntry(
			externalReferenceCode, _getGroupId(groupId, objectDefinition),
			objectDefinition.getObjectDefinitionId());
	}

	public static String getEntityClassName(ObjectDefinition objectDefinition) {
		return StringBundler.concat(
			com.liferay.object.rest.dto.v1_0.ObjectEntry.class.getName(), "#",
			objectDefinition.getName());
	}

	public static long getObjectEntryCreatorUserId(
			String externalReferenceCode, long groupId,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, groupId, objectDefinition);

		return objectEntry.getUserId();
	}

	public static long getObjectEntryId(
			String externalReferenceCode, long groupId,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, groupId, objectDefinition);

		return objectEntry.getObjectEntryId();
	}

	public static int getObjectEntryStatus(
			String externalReferenceCode, long groupId,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, groupId, objectDefinition);

		return objectEntry.getStatus();
	}

	public static ObjectEntry getOrAddEmptyObjectEntry(
			String externalReferenceCode, long groupId,
			ObjectDefinition objectDefinition, long userId)
		throws Exception {

		objectDefinition = _getObjectDefinition(groupId, objectDefinition);

		return ObjectEntryLocalServiceUtil.getOrAddEmptyObjectEntry(
			externalReferenceCode, _getGroupId(groupId, objectDefinition),
			userId, objectDefinition.getObjectDefinitionId());
	}

	public static boolean hasObjectEntry(
		String externalReferenceCode, long groupId,
		ObjectDefinition objectDefinition) {

		ObjectEntry objectEntry = fetchObjectEntry(
			externalReferenceCode, groupId, objectDefinition);

		if ((objectEntry == null) || objectEntry.isInTrash()) {
			return false;
		}

		return true;
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

	public static ObjectEntry updateObjectEntry(
			String externalReferenceCode, long groupId,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, groupId, objectDefinition);

		return ObjectEntryLocalServiceUtil.updateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			Collections.singletonMap(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(
				objectEntry.getCompanyId(), objectEntry.getGroupId(),
				objectEntry.getUserId()));
	}

	public static ObjectEntry updateObjectEntryCreatorUserId(
			String externalReferenceCode, long groupId,
			ObjectDefinition objectDefinition, long userId)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, groupId, objectDefinition);

		objectEntry.setUserId(userId);

		return ObjectEntryLocalServiceUtil.updateObjectEntry(objectEntry);
	}

	public static ObjectEntry updateObjectEntryModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry objectEntry = _getObjectEntry(
			externalReferenceCode, groupId, objectDefinition);

		objectEntry.setModifiedDate(modifiedDate);

		return ObjectEntryLocalServiceUtil.updateObjectEntry(objectEntry);
	}

	private static ObjectDefinition _fetchObjectDefinition(
		long groupId, ObjectDefinition objectDefinition) {

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return ObjectDefinitionLocalServiceUtil.
			fetchObjectDefinitionByExternalReferenceCode(
				objectDefinition.getExternalReferenceCode(),
				group.getCompanyId());
	}

	private static long _getGroupId(
		long groupId, ObjectDefinition objectDefinition) {

		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			return 0;
		}

		return groupId;
	}

	private static ObjectDefinition _getObjectDefinition(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		return ObjectDefinitionLocalServiceUtil.
			getObjectDefinitionByExternalReferenceCode(
				objectDefinition.getExternalReferenceCode(),
				group.getCompanyId());
	}

	private static ObjectEntry _getObjectEntry(
			String externalReferenceCode, long groupId,
			ObjectDefinition objectDefinition)
		throws Exception {

		objectDefinition = _getObjectDefinition(groupId, objectDefinition);

		return ObjectEntryLocalServiceUtil.getObjectEntry(
			externalReferenceCode, _getGroupId(groupId, objectDefinition),
			objectDefinition.getObjectDefinitionId());
	}

	private static final String _OBJECT_FIELD_NAME =
		"x" + RandomTestUtil.randomString();

}