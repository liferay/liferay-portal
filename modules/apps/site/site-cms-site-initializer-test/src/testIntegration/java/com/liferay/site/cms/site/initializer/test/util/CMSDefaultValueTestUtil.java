/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.test.util;

import com.liferay.batch.engine.test.util.BatchEngineTestUtil;

/**
 * @author Stefano Motta
 */
public class CMSDefaultValueTestUtil {

	public static void processBatchEngineUnits(Class<?> clazz) {
		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.site.initializer.cms", clazz,
			new String[] {
				".com.liferay.site.initializer.cms.internal.batch.03.object." +
					"definition"
			});
	}

}