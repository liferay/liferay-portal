/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.related.models.test.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Pedro Leite
 */
public class ObjectEntryTestUtil {

	public static ObjectEntry addObjectEntry(
			long groupId, long objectDefinitionId, long objectEntryFolderId,
			Map<String, Serializable> values)
		throws Exception {

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			groupId, TestPropsValues.getUserId(), objectDefinitionId,
			objectEntryFolderId, null, values,
			ServiceContextTestUtil.getServiceContext());
	}

	public static ObjectEntry addObjectEntry(
			long groupId, long objectDefinitionId,
			Map<String, Serializable> values)
		throws Exception {

		return addObjectEntry(
			groupId, objectDefinitionId,
			ServiceContextTestUtil.getServiceContext(), values);
	}

	public static ObjectEntry addObjectEntry(
			long groupId, long objectDefinitionId,
			ServiceContext serviceContext, Map<String, Serializable> values)
		throws Exception {

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			groupId, TestPropsValues.getUserId(), objectDefinitionId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, serviceContext);
	}

}