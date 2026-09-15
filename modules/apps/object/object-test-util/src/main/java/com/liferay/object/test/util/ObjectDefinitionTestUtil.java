/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.test.util;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Guilherme Camacho
 */
public class ObjectDefinitionTestUtil {

	public static ObjectDefinition addCustomObjectDefinition()
		throws Exception {

		return addCustomObjectDefinition(Collections.emptyList());
	}

	public static ObjectDefinition addCustomObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

		return addCustomObjectDefinition(0, getRandomName(), objectFields);
	}

	public static ObjectDefinition addCustomObjectDefinition(
			long objectFolderId)
		throws Exception {

		return addCustomObjectDefinition(
			objectFolderId, getRandomName(), Collections.emptyList());
	}

	public static ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, String name, List<ObjectField> objectFields)
		throws Exception {

		return addCustomObjectDefinition(
			objectFolderId, name, objectFields, TestPropsValues.getUserId());
	}

	public static ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, String name, List<ObjectField> objectFields,
			long userId)
		throws Exception {

		return ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
			null, userId, objectFolderId, null, null, true, false, true, false,
			true, false, false, false, false,
			FriendlyURLResolverConstants.URL_SEPARATOR_Y_OBJECT_ENTRY,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			name, null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(), objectFields, Collections.emptyList(),
			new ServiceContext());
	}

	public static ObjectDefinition addCustomObjectDefinition(String name)
		throws Exception {

		return addCustomObjectDefinition(name, TestPropsValues.getUserId());
	}

	public static ObjectDefinition addCustomObjectDefinition(
			String name, List<WorkflowDefinitionLink> workflowDefinitionLinks)
		throws Exception {

		return ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
			null, TestPropsValues.getUserId(), 0, null, null, true, false,
			false, true, false, false, false, false, false, null,
			LocalizedMapUtil.getLocalizedMap(name), name, null, null,
			LocalizedMapUtil.getLocalizedMap(name), true,
			ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())),
			workflowDefinitionLinks, new ServiceContext());
	}

	public static ObjectDefinition addCustomObjectDefinition(
			String name, long userId)
		throws Exception {

		return addCustomObjectDefinition(
			0, name,
			Arrays.asList(
				new TextObjectFieldBuilder(
				).userId(
					userId
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(StringUtil.randomId())
				).name(
					"able"
				).build()),
			userId);
	}

	public static ObjectDefinition addModifiableSystemObjectDefinition(
			long userId, String dbTableName, Map<Locale, String> labelMap,
			String name, String pkObjectFieldDBColumnName,
			String pkObjectFieldName, Map<Locale, String> pluralLabelMap,
			String scope, String titleObjectFieldName, int version,
			List<ObjectField> objectFields)
		throws Exception {

		return ObjectDefinitionLocalServiceUtil.addSystemObjectDefinition(
			null, userId, 0, getUniqueRandomClassName(), dbTableName, null,
			true, false, true, false, true, false, false, false, false, false,
			null, labelMap, true, name, null, null, pkObjectFieldDBColumnName,
			pkObjectFieldName, pluralLabelMap, false, scope,
			titleObjectFieldName, version, WorkflowConstants.STATUS_DRAFT,
			Collections.emptyList(), objectFields, Collections.emptyList());
	}

	public static ObjectDefinition addUnmodifiableSystemObjectDefinition(
			String externalReferenceCode, long userId, String className,
			String dbTableName, Map<Locale, String> labelMap, String name,
			String pkObjectFieldDBColumnName, String pkObjectFieldName,
			Map<Locale, String> pluralLabelMap, String scope,
			String titleObjectFieldName, int version,
			List<ObjectField> objectFields)
		throws Exception {

		return ObjectDefinitionLocalServiceUtil.addSystemObjectDefinition(
			externalReferenceCode, userId, 0, className, dbTableName, null,
			false, false, false, false, true, false, false, false, false, false,
			null, labelMap, false, name, null, null, pkObjectFieldDBColumnName,
			pkObjectFieldName, pluralLabelMap, false, scope,
			titleObjectFieldName, version, WorkflowConstants.STATUS_APPROVED,
			Collections.emptyList(), objectFields, Collections.emptyList());
	}

	public static String getRandomModifiableSystemObjectDefinitionName() {
		return "Test" + RandomTestUtil.randomString();
	}

	public static String getRandomName() {
		return "A" + RandomTestUtil.randomString();
	}

	public static String getUniqueRandomClassName() throws Exception {
		while (true) {
			String className = StringBundler.concat(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION,
				StringUtil.toUpperCase(StringUtil.randomId(1)),
				RandomUtil.nextInt(10),
				StringUtil.toUpperCase(StringUtil.randomId(1)),
				RandomUtil.nextInt(10));

			ObjectDefinition objectDefinition =
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByClassName(
						TestPropsValues.getCompanyId(), className);

			if (objectDefinition == null) {
				return className;
			}
		}
	}

	public static ObjectDefinition publishObjectDefinition() throws Exception {
		return publishObjectDefinition(Collections.emptyList());
	}

	public static ObjectDefinition publishObjectDefinition(
			boolean enableObjectEntryDraft,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, List<ObjectField> objectFields,
			String scope)
		throws Exception {

		return publishObjectDefinition(
			enableObjectEntryDraft, enableObjectEntrySubscription,
			enableObjectEntryVersioning, getRandomName(), objectFields, 0,
			scope, TestPropsValues.getUserId());
	}

	public static ObjectDefinition publishObjectDefinition(
			boolean enableObjectEntryDraft,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, String name,
			List<ObjectField> objectFields, long objectFolderId, String scope,
			long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
				null, userId, objectFolderId, null, null, true, false, true,
				false, true, enableObjectEntryDraft, false,
				enableObjectEntrySubscription, enableObjectEntryVersioning,
				null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				name, null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields, Collections.emptyList(),
				new ServiceContext());

		return ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
			userId, objectDefinition.getObjectDefinitionId());
	}

	public static ObjectDefinition publishObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

		ObjectDefinition objectDefinition = addCustomObjectDefinition(
			objectFields);

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Able")
			).name(
				"able"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				false
			).build());

		ObjectDefinitionLocalServiceUtil.updateTitleObjectFieldId(
			objectDefinition.getObjectDefinitionId(),
			objectField.getObjectFieldId());

		return ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	public static ObjectDefinition publishObjectDefinition(
			List<ObjectField> objectFields, boolean updateTitleObjectFieldId)
		throws Exception {

		if (updateTitleObjectFieldId) {
			return publishObjectDefinition(objectFields);
		}

		return publishObjectDefinition(
			getRandomName(), objectFields,
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	public static ObjectDefinition publishObjectDefinition(
			List<ObjectField> objectFields, String scope)
		throws Exception {

		return publishObjectDefinition(
			getRandomName(), objectFields, scope, TestPropsValues.getUserId());
	}

	public static ObjectDefinition publishObjectDefinition(
			List<ObjectField> objectFields, String scope, long userId)
		throws Exception {

		return publishObjectDefinition(
			getRandomName(), objectFields, scope, userId);
	}

	public static ObjectDefinition publishObjectDefinition(
			String name, List<ObjectField> objectFields, long objectFolderId,
			String scope, long userId)
		throws Exception {

		return publishObjectDefinition(
			false, false, false, name, objectFields, objectFolderId, scope,
			userId);
	}

	public static ObjectDefinition publishObjectDefinition(
			String name, List<ObjectField> objectFields, String scope)
		throws Exception {

		return publishObjectDefinition(
			name, objectFields, scope, TestPropsValues.getUserId());
	}

	public static ObjectDefinition publishObjectDefinition(
			String name, List<ObjectField> objectFields, String scope,
			long userId)
		throws Exception {

		return publishObjectDefinition(name, objectFields, 0, scope, userId);
	}

	public static ObjectDefinition publishSystemObjectDefinition()
		throws Exception {

		return publishSystemObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())));
	}

	public static ObjectDefinition publishSystemObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

		ObjectDefinition objectDefinition = addModifiableSystemObjectDefinition(
			TestPropsValues.getUserId(), null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			getRandomModifiableSystemObjectDefinitionName(), null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionConstants.SCOPE_COMPANY, null, 1, objectFields);

		return ObjectDefinitionLocalServiceUtil.publishSystemObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

}