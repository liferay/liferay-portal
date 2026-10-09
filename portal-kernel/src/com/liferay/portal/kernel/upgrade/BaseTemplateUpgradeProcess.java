/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Albert Gomes Cabral
 * @author Renato Rego
 */
public abstract class BaseTemplateUpgradeProcess extends UpgradeProcess {

	protected abstract String getContextVariable();

	protected abstract String getDeprecatedClass();

	protected Pattern getDeprecatedClassPattern() {
		String deprecatedClass = getDeprecatedClass();

		if (deprecatedClass == null) {
			return null;
		}

		StringBundler sb = new StringBundler(3);

		sb.append("\\w+\\s*\\=\\s*.+");
		sb.append(StringUtil.replace(deprecatedClass, '.', "\\."));
		sb.append("\\\"\\)");

		return Pattern.compile(sb.toString());
	}

	protected abstract String getDeprecatedClassReplacement();

	protected String getVariableName(Matcher matcher) {
		String matcherGroup = matcher.group();

		String variableName = matcherGroup.substring(
			0, matcherGroup.indexOf(StringPool.EQUAL));

		return variableName.trim();
	}

	protected String replaceDeprecatedClass(
		Pattern emptyAssignPattern, String template) {

		if (template == null) {
			return StringPool.BLANK;
		}

		Pattern deprecatedClassPattern = getDeprecatedClassPattern();

		if (deprecatedClassPattern != null) {
			Matcher deprecatedClassMatcher = deprecatedClassPattern.matcher(
				template);

			while (deprecatedClassMatcher.find()) {
				template = StringUtil.replace(
					template, deprecatedClassMatcher.group(),
					getDeprecatedClassReplacement());

				if (Validator.isNotNull(getContextVariable())) {
					template = StringUtil.replace(
						template, getVariableName(deprecatedClassMatcher),
						getContextVariable());
				}

				Matcher emptyAssignMatcher = emptyAssignPattern.matcher(
					template);

				if (!emptyAssignMatcher.find()) {
					continue;
				}

				template = emptyAssignMatcher.replaceAll(StringPool.BLANK);
			}
		}

		return template;
	}

}