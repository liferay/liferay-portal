/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaServiceTrackerFactoryCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		Matcher matcher = _serviceTrackerFactoryOpenPattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.end())) {
				continue;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				content.substring(matcher.start()));

			if (parameterList.size() != 1) {
				continue;
			}

			addMessage(
				fileName,
				"ServiceTrackerFactory.open(java.lang.Class) is deprecated, " +
					"because it leaks ServiceTrackers",
				getLineNumber(content, matcher.start()));
		}

		return content;
	}

	private static final Pattern _serviceTrackerFactoryOpenPattern =
		Pattern.compile("\\WServiceTrackerFactory\\.\\s*open\\(");

}