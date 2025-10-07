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
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
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
			boolean enableLocalization, List<ObjectField> objectFields)
		throws Exception {

		return addCustomObjectDefinition(
			0, enableLocalization, getRandomName(), objectFields);
	}

	public static ObjectDefinition addCustomObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

		return addCustomObjectDefinition(
			FeatureFlagManagerUtil.isEnabled("LPD-32050"), objectFields);
	}

	public static ObjectDefinition addCustomObjectDefinition(
			long objectFolderId)
		throws Exception {

		return addCustomObjectDefinition(
			objectFolderId, FeatureFlagManagerUtil.isEnabled("LPD-32050"),
			getRandomName(), Collections.emptyList());
	}

	public static ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, boolean enableLocalization, String name,
			List<ObjectField> objectFields)
		throws Exception {

		return addCustomObjectDefinition(
			objectFolderId, enableLocalization, name, objectFields,
			TestPropsValues.getUserId());
	}

	public static ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, boolean enableLocalization, String name,
			List<ObjectField> objectFields, long userId)
		throws Exception {

		return ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
			userId, objectFolderId, null, false, true, false, true,
			enableLocalization, false, false, false, false,
			FriendlyURLResolverConstants.URL_SEPARATOR_Y_OBJECT_ENTRY,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			name, null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(), objectFields, Collections.emptyList());
	}

	public static ObjectDefinition addCustomObjectDefinition(String name)
		throws Exception {

		return addCustomObjectDefinition(name, TestPropsValues.getUserId());
	}

	public static ObjectDefinition addCustomObjectDefinition(
			String name, List<WorkflowDefinitionLink> workflowDefinitionLinks)
		throws Exception {

		return ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
			TestPropsValues.getUserId(), 0, null, false, false, true, false,
			false, false, false, false, false, null,
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
			workflowDefinitionLinks);
	}

	public static ObjectDefinition addCustomObjectDefinition(
			String name, long userId)
		throws Exception {

		return addCustomObjectDefinition(
			0, FeatureFlagManagerUtil.isEnabled("LPD-32050"), name,
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
			long userId, String dbTableName, boolean enableLocalization,
			Map<Locale, String> labelMap, String name,
			String pkObjectFieldDBColumnName, String pkObjectFieldName,
			Map<Locale, String> pluralLabelMap, String scope,
			String titleObjectFieldName, int version,
			List<ObjectField> objectFields)
		throws Exception {

		return ObjectDefinitionLocalServiceUtil.addSystemObjectDefinition(
			null, userId, 0, null, dbTableName, false, true, false, true,
			enableLocalization, false, false, false, false, null, labelMap,
			true, name, null, null, pkObjectFieldDBColumnName,
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
			externalReferenceCode, userId, 0, className, dbTableName, false,
			false, false, true, false, false, false, false, false, null,
			labelMap, false, name, null, null, pkObjectFieldDBColumnName,
			pkObjectFieldName, pluralLabelMap, false, scope,
			titleObjectFieldName, version, WorkflowConstants.STATUS_APPROVED,
			Collections.emptyList(), objectFields, Collections.emptyList());
	}

	public static String getRandomName() {
		return "A" + RandomTestUtil.randomString();
	}

	public static ObjectDefinition publishObjectDefinition() throws Exception {
		return publishObjectDefinition(
			FeatureFlagManagerUtil.isEnabled("LPD-32050"),
			Collections.emptyList());
	}

	public static ObjectDefinition publishObjectDefinition(
			boolean enableObjectEntryDraft,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, boolean localized, String name,
			List<ObjectField> objectFields, long objectFolderId, String scope,
			long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
				userId, objectFolderId, null, false, true, false, true,
				localized, enableObjectEntryDraft, false,
				enableObjectEntrySubscription, enableObjectEntryVersioning,
				null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				name, null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields, Collections.emptyList());

		return ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
			userId, objectDefinition.getObjectDefinitionId());
	}

	public static ObjectDefinition publishObjectDefinition(
			boolean enableObjectEntryDraft,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, List<ObjectField> objectFields,
			String scope)
		throws Exception {

		return publishObjectDefinition(
			enableObjectEntryDraft, enableObjectEntrySubscription,
			enableObjectEntryVersioning,
			FeatureFlagManagerUtil.isEnabled("LPD-32050"), getRandomName(),
			objectFields, 0, scope, TestPropsValues.getUserId());
	}

	public static ObjectDefinition publishObjectDefinition(
			boolean enableLocalization, List<ObjectField> objectFields)
		throws Exception {

		ObjectDefinition objectDefinition = addCustomObjectDefinition(
			enableLocalization, objectFields);

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
			boolean localized, String name, List<ObjectField> objectFields,
			long objectFolderId, String scope, long userId)
		throws Exception {

		return publishObjectDefinition(
			false, false, false, localized, name, objectFields, objectFolderId,
			scope, userId);
	}

	public static ObjectDefinition publishObjectDefinition(
			boolean localized, String name, List<ObjectField> objectFields,
			String scope, long userId)
		throws Exception {

		return publishObjectDefinition(
			localized, name, objectFields, 0, scope, userId);
	}

	public static ObjectDefinition publishObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

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
			String name, List<ObjectField> objectFields, String scope)
		throws Exception {

		return publishObjectDefinition(
			name, objectFields, scope, TestPropsValues.getUserId());
	}

	public static ObjectDefinition publishObjectDefinition(
			String name, List<ObjectField> objectFields, String scope,
			long userId)
		throws Exception {

		return publishObjectDefinition(
			FeatureFlagManagerUtil.isEnabled("LPD-32050"), name, objectFields,
			scope, userId);
	}

	public static ObjectDefinition publishSystemObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition = addModifiableSystemObjectDefinition(
			TestPropsValues.getUserId(), null, true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"Test", null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())));

		return ObjectDefinitionLocalServiceUtil.publishSystemObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

}