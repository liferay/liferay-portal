/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Alan Huang
 */
public class JakartaTransformJavaCheck extends BaseJakartaTransformCheck {

	@Override
	protected String format(
		String fileName, String absolutePath, String content) {

		content = replace(content);
		content = replaceTaglibURIs(content);

		return StringUtil.replace(
			content,
			new String[] {
				"freemarker.ext.jsp.TaglibFactory", "freemarker.ext.servlet.",
				"jakarta.portlet.version=3.0"
			},
			new String[] {
				"freemarker.ext.jakarta.jsp.TaglibFactory",
				"freemarker.ext.jakarta.servlet.", "jakarta.portlet.version=4.0"
			});
	}

	@Override
	protected String[] getValidExtensions() {
		return _VALID_EXTENSIONS;
	}

	private static final String[] _VALID_EXTENSIONS = {".java"};

}