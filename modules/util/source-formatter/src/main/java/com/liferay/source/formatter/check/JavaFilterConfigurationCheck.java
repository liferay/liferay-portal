/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.io.IOException;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaFilterConfigurationCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("Filter.java")) {
			return content;
		}

		Pattern pattern = Pattern.compile(
			" class " + JavaSourceUtil.getClassName(absolutePath) +
				"\\s+extends\\s+([\\w.]+)\\b");

		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String extendedClassName = matcher.group(1);

		if (!extendedClassName.contains(StringPool.PERIOD)) {
			pattern = Pattern.compile(
				"\nimport (.*\\." + extendedClassName + ");");

			matcher = pattern.matcher(content);

			if (matcher.find()) {
				extendedClassName = matcher.group(1);
			}
			else {
				extendedClassName =
					JavaSourceUtil.getPackageName(content) + StringPool.PERIOD +
						extendedClassName;
			}
		}

		if (!extendedClassName.startsWith("com.liferay.")) {
			return content;
		}

		String fullyQualifiedClassName =
			JavaSourceUtil.getPackageName(content) + "." +
				JavaSourceUtil.getClassName(fileName);

		Properties properties = new Properties();

		PropertiesUtil.load(
			properties,
			getPortalContent(
				"portal-impl/src/portal.properties", absolutePath));

		if (extendedClassName.equals(
				"com.liferay.portal.kernel.servlet.BaseFilter") &&
			(properties.getProperty(fullyQualifiedClassName) != null)) {

			addMessage(
				fileName,
				"Do not add property \"" + fullyQualifiedClassName +
					"\" in portal.properties, see LPD-69645");

			return content;
		}

		if (extendedClassName.equals(
				"com.liferay.portal.servlet.filters.BasePortalFilter") &&
			(properties.getProperty(fullyQualifiedClassName) == null)) {

			addMessage(
				fileName,
				"Missing property \"" + fullyQualifiedClassName +
					"\" in portal.properties, see LPD-69645");

			return content;
		}

		return content;
	}

}