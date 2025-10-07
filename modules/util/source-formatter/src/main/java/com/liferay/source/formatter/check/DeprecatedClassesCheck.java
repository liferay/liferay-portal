/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

/**
 * @author Alan Huang
 */
public class DeprecatedClassesCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		List<String> deprecatedClassNames = getAttributeValues(
			_DEPRECATED_CLASS_NAMES_KEY, absolutePath);

		for (String deprecatedClassName : deprecatedClassNames) {
			String[] parts = StringUtil.split(deprecatedClassName, "->");

			if (parts.length != 2) {
				continue;
			}

			content = StringUtil.replace(content, parts[0], parts[1]);
		}

		return content;
	}

	private static final String _DEPRECATED_CLASS_NAMES_KEY =
		"deprecatedClassNames";

}