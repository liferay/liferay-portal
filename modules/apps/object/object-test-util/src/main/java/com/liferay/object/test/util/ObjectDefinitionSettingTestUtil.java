/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.test.util;

import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.persistence.ObjectDefinitionSettingUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;

/**
 * @author Shuyang Zhou
 */
public class ObjectDefinitionSettingTestUtil {

	public static ObjectDefinitionSetting getObjectDefinitionSetting(
			long objectDefinitionId, String name)
		throws Exception {

		return TransactionTestUtil.invoke(
			ObjectDefinitionSettingUtil.getPersistence(),
			objectDefinitionSettingPersistence ->
				objectDefinitionSettingPersistence.findByODI_N(
					objectDefinitionId, name));
	}

}