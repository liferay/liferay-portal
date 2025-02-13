/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.test.util;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Sergio Jiménez del Coso
 */
public class ObjectDefinitionTestUtil {

	public static ObjectDefinition publishObjectDefinition(
			List<ObjectField> objectFields, String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				com.liferay.object.test.util.ObjectDefinitionTestUtil.
					getRandomName(),
				null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields);

		return ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

}