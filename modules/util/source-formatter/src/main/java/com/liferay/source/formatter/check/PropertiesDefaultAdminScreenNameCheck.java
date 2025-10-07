/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.io.IOException;
import java.io.StringReader;

import java.util.Properties;

/**
 * @author Alan Huang
 */
public class PropertiesDefaultAdminScreenNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (absolutePath.endsWith("/portal-impl/src/portal.properties")) {
			return content;
		}

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		if (properties.containsKey("default.admin.screen.name")) {
			addMessage(
				fileName,
				"Do not use \"default.admin.screen.name\", see LPD-59150");
		}

		return content;
	}

}