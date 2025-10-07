/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.check.util.XMLSourceUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class XMLSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws IOException {
		return getFileNames(
			new String[] {
				"**/.bnd/**", "**/.idea/**", "**/.ivy/**", "**/bin/**",
				"**/javadocs-*.xml", "**/logs/**", "**/modules/**/.project",
				"**/portal-impl/**/*.action", "**/portal-impl/**/*.function",
				"**/portal-impl/**/*.macro", "**/portal-impl/**/*.testcase",
				"**/src/test/**", "**/test-classes/unit/**",
				"**/test-results/**", "**/test/unit/**", "**/tools/node**"
			},
			getIncludes());
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	@Override
	protected File format(
			File file, String fileName, String absolutePath, String content)
		throws Exception {

		if ((fileName.endsWith(".function") || fileName.endsWith(".macro") ||
			 fileName.endsWith(".project") ||
			 fileName.endsWith(".properties") ||
			 fileName.endsWith(".testcase")) &&
			!SourceUtil.isXML(content)) {

			return file;
		}

		return super.format(file, fileName, absolutePath, content);
	}

	@Override
	protected boolean hasGeneratedTag(String content) {
		return _hasGeneratedTag(content, "@generated", "<!-- Generated");
	}

	private boolean _hasGeneratedTag(String content, String... tags) {
		for (String tag : tags) {
			if (!content.contains(tag)) {
				continue;
			}

			int pos = -1;

			while (true) {
				pos = content.indexOf(tag, pos + 1);

				if (pos == -1) {
					break;
				}

				if (!XMLSourceUtil.isInsideCDATAMarkup(content, pos)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final String[] _INCLUDES = {
		"**/*.action", "**/*.function", "**/*.jelly", "**/*.jrxml",
		"**/*.macro", "**/*.pom", "**/*.project", "**/*.properties", "**/*.qti",
		"**/*.svg", "**/*.testcase", "**/*.toggle", "**/*.wsdl", "**/*.xml",
		"**/definitions/liferay-*.xsd", "**/*.xml.tpl"
	};

}