/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.test.util;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryTestUtil {

	public static ObjectEntry partialUpdateObjectEntry(
			ObjectEntry objectEntry, Map<String, Serializable> values)
		throws Exception {

		return ObjectEntryLocalServiceUtil.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), values,
			ServiceContextTestUtil.getServiceContext());
	}

}