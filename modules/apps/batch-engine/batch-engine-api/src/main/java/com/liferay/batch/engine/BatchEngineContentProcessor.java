/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine;

import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public interface BatchEngineContentProcessor {

	public String process(
		String content, Map<String, Object> fieldNameValueMap);

}