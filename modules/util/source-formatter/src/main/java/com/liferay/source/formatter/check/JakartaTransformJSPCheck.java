/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

/**
 * @author Alan Huang
 */
public class JakartaTransformJSPCheck extends BaseJakartaTransformCheck {

	@Override
	protected String format(
		String fileName, String absolutePath, String content) {

		content = replace(content);

		return replaceTaglibURIs(content);
	}

	@Override
	protected String[] getValidExtensions() {
		return _VALID_EXTENSIONS;
	}

	private static final String[] _VALID_EXTENSIONS = {
		".jsp", ".jspf", ".jspx"
	};

}