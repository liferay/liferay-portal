/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.test.util;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.service.ObjectFieldSettingLocalServiceUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Carlos Correa
 */
public class ObjectFieldTestUtil {

	public static ObjectField addCustomObjectField(
			long userId, ObjectField objectField)
		throws Exception {

		return ObjectFieldLocalServiceUtil.addCustomObjectField(
			objectField.getExternalReferenceCode(), TestPropsValues.getUserId(),
			objectField.getListTypeDefinitionId(),
			objectField.getObjectDefinitionId(), objectField.getBusinessType(),
			objectField.getDBType(), objectField.getDescriptionMap(),
			objectField.isIndexed(), objectField.isIndexedAsKeyword(),
			objectField.getIndexedLanguageId(), objectField.getLabelMap(),
			objectField.isLocalized(), objectField.getName(),
			objectField.getReadOnly(),
			objectField.getReadOnlyConditionExpression(),
			objectField.isRequired(), objectField.isState(),
			objectField.getObjectFieldSettings());
	}

	public static ObjectField addCustomObjectField(
			long userId, String businessType, String dbType,
			ObjectDefinition objectDefinition, String objectFieldName)
		throws Exception {

		List<ObjectFieldSetting> objectFieldSettings = null;

		if (Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT) ||
			Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_TEXT)) {

			ObjectFieldSetting objectFieldSetting =
				ObjectFieldSettingLocalServiceUtil.createObjectFieldSetting(0);

			objectFieldSetting.setName("showCounter");
			objectFieldSetting.setValue("false");

			objectFieldSettings = Collections.singletonList(objectFieldSetting);
		}

		return ObjectFieldLocalServiceUtil.addCustomObjectField(
			null, userId, 0, objectDefinition.getObjectDefinitionId(),
			businessType, dbType, null, false, true, "",
			LocalizedMapUtil.getLocalizedMap(objectFieldName), false,
			objectFieldName, ObjectFieldConstants.READ_ONLY_FALSE, null, false,
			false, objectFieldSettings);
	}

}