/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaConfigurationCategoryCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		int x = fileName.lastIndexOf(StringPool.SLASH);

		if ((x == -1) ||
			!StringUtil.endsWith(fileName.substring(0, x), "/configuration")) {

			return content;
		}

		String shortFileName = fileName.substring(x + 1);

		if (!shortFileName.endsWith("Configuration.java")) {
			return content;
		}

		Matcher matcher = _categoryNamePattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String categoryName = matcher.group(1);

		List<String> categoryKeys = _getCategoryKeys();

		if (!categoryKeys.isEmpty() && !categoryKeys.contains(categoryName)) {
			addMessage(
				fileName, "Invalid category name \"" + categoryName + "\"",
				getLineNumber(content, matcher.start(1)));
		}

		return content;
	}

	private synchronized List<String> _getCategoryKeys() throws IOException {
		if (_categoryKeys != null) {
			return _categoryKeys;
		}

		_categoryKeys = new ArrayList<>();

		File portalDir = getPortalDir();

		if (portalDir == null) {
			return Collections.emptyList();
		}

		List<String> fileNames =
			SourceFormatterUtil.matchFileContentsForFileNames(
				Arrays.asList("-E", "implements ConfigurationCategory"),
				portalDir.getCanonicalPath(),
				new String[] {"modules/**/*.java"});

		if (fileNames.isEmpty()) {
			return Collections.emptyList();
		}

		for (String fileName : fileNames) {
			String content = FileUtil.read(new File(fileName));

			Matcher matcher = _categoryKeyPattern1.matcher(content);

			if (matcher.find()) {
				_categoryKeys.add(matcher.group(1));

				continue;
			}

			matcher = _categoryKeyPattern2.matcher(content);

			if (matcher.find()) {
				_categoryKeys.add(matcher.group(1));
			}
		}

		return _categoryKeys;
	}

	private static final Pattern _categoryKeyPattern1 = Pattern.compile(
		"String\\s+_CATEGORY_KEY\\s+=\\s+\"(\\w+)\"");
	private static final Pattern _categoryKeyPattern2 = Pattern.compile(
		"String\\s+getCategoryKey\\(\\)\\s+\\{\\s+return\\s+\"(\\w+)\"");
	private static final Pattern _categoryNamePattern = Pattern.compile(
		"\n@ExtendedObjectClassDefinition\\(\\s*category\\s+=\\s+\"(\\w+)\"");

	private List<String> _categoryKeys;

}