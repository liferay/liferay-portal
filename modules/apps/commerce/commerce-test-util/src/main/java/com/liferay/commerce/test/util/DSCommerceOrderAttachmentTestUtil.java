/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test.util;

import com.liferay.batch.engine.test.util.BatchEngineTestUtil;

/**
 * @author Andrea Sbarra
 */
public class DSCommerceOrderAttachmentTestUtil {

	public static void initialize(Class<?> clazz) throws Exception {
		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.digital.signature.impl", clazz,
			new String[] {
				".com.liferay.digital.signature.internal.batch.01.object." +
					"definition",
				".com.liferay.digital.signature.internal.batch.02.object." +
					"relationship",
				".com.liferay.digital.signature.internal.batch.03." +
					"notification.template"
			});
	}

}