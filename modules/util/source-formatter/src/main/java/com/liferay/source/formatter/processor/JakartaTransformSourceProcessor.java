/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import java.io.IOException;

import java.util.List;

/**
 * @author Alan Huang
 */
public class JakartaTransformSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws IOException {
		return getFileNames(new String[0], getIncludes());
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	private static final String[] _INCLUDES = {
		"**/*.bnd", "**/*.ftl", "**/*.gradle", "**/*.html", "**/*.java",
		"**/*.js", "**/*.jsp", "**/*.jspf", "**/*.jspx", "**/*.jsx",
		"**/*.properties", "**/*.tld", "**/*.ts", "**/*.tsx", "**/*.xml"
	};

}