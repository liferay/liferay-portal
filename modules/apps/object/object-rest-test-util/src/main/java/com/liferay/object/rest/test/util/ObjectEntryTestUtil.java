/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.test.util;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Luis Miguel Barcos
 */
public class ObjectEntryTestUtil {

	public static ObjectEntry addObjectEntry(
			long groupId, ObjectDefinition objectDefinition,
			Map<String, Serializable> values, String... keywords)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		if (keywords.length > 0) {
			serviceContext.setAssetTagNames(keywords);
		}

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, serviceContext);
	}

	public static ObjectEntry addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values,
			String... keywords)
		throws Exception {

		long groupId = 0;

		if (StringUtil.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_SITE)) {

			groupId = TestPropsValues.getGroupId();
		}

		return addObjectEntry(groupId, objectDefinition, values, keywords);
	}

	public static ObjectEntry addObjectEntry(
			ObjectDefinition objectDefinition, String objectFieldName,
			Serializable objectFieldValue)
		throws Exception {

		return addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				objectFieldName, objectFieldValue
			).build());
	}

}