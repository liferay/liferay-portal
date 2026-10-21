/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.tools.ToolsUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Carlos Correa
 */
public class JavaFeatureFlagAnnotationCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/")) {

			return content;
		}

		Matcher matcher = _featureFlagAnnotationPattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start()) ||
				((matcher.group(1) == null) &&
				 !content.contains(
					 "import com.liferay.portal.vulcan.feature.flag." +
						 "FeatureFlag;"))) {

				continue;
			}

			addMessage(
				fileName,
				"Declare the feature flag with \"x-feature-flag\" in " +
					"\"rest-openapi.yaml\" instead of using the annotation " +
						"\"@FeatureFlag\"",
				getLineNumber(content, matcher.start()));
		}

		return content;
	}

	private static final Pattern _featureFlagAnnotationPattern =
		Pattern.compile(
			"@(com\\.liferay\\.portal\\.vulcan\\.feature\\.flag\\.)?" +
				"FeatureFlag\\(");

}