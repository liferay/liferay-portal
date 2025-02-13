/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.test.util;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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
			userId, objectFolderId, null, false, false, true,
			enableLocalization, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			name, null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(), objectFields);
	}

	public static ObjectDefinition addCustomObjectDefinition(String name)
		throws Exception {

		return addCustomObjectDefinition(name, TestPropsValues.getUserId());
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
			null, userId, 0, null, dbTableName, false, false, true,
			enableLocalization, labelMap, true, name, null, null,
			pkObjectFieldDBColumnName, pkObjectFieldName, pluralLabelMap, false,
			scope, titleObjectFieldName, version,
			WorkflowConstants.STATUS_DRAFT, Collections.emptyList(),
			objectFields);
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
			false, true, false, labelMap, false, name, null, null,
			pkObjectFieldDBColumnName, pkObjectFieldName, pluralLabelMap, false,
			scope, titleObjectFieldName, version,
			WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
			objectFields);
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
			String scope, long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
				userId, 0, null, false, false, true, localized, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				name, null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields);

		return ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
			userId, objectDefinition.getObjectDefinitionId());
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

}