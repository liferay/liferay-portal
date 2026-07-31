/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaMetaOCDSecretFieldCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		if (absolutePath.contains("/archived/") ||
			absolutePath.contains("/gradleTest/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			!fileContent.contains("@Meta.OCD")) {

			return javaTerm.getContent();
		}

		JavaClass javaClass = (JavaClass)javaTerm;

		if ((javaClass.getParentJavaClass() != null) ||
			!javaClass.hasAnnotation("Meta.OCD") || !javaClass.isInterface()) {

			return javaTerm.getContent();
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			String content = childJavaTerm.getContent();

			Matcher matcher = _stringAccessorPattern.matcher(content);

			while (matcher.find()) {
				String name = matcher.group(2);

				if (!_isSecretName(name)) {
					continue;
				}

				int previousSemicolon = content.lastIndexOf(
					CharPool.SEMICOLON, matcher.start());

				String annotation = content.substring(
					previousSemicolon + 1, matcher.start());

				if (annotation.contains("Meta.Type.Password")) {
					continue;
				}

				addMessage(
					fileName,
					StringBundler.concat(
						"Use \"type = Meta.Type.Password\" in \"@Meta.AD\" ",
						"for \"", name, "\", which appears to hold a secret"),
					childJavaTerm.getLineNumber());
			}
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private boolean _isSecretName(String name) {
		String lowerCaseName = StringUtil.toLowerCase(name);

		for (String excludeKeyword : _EXCLUDED_SECRET_KEYWORDS) {
			if (lowerCaseName.contains(excludeKeyword)) {
				return false;
			}
		}

		for (String secretKeyword : _SECRET_KEYWORDS) {
			if (lowerCaseName.contains(secretKeyword)) {
				return true;
			}
		}

		return false;
	}

	private static final String[] _EXCLUDED_SECRET_KEYWORDS = {
		"algorithm", "field", "keyword", "providerid", "sapentry", "type",
		"uri", "url"
	};

	private static final String[] _SECRET_KEYWORDS = {
		"accesstoken", "apikey", "authkey", "authtoken", "credential",
		"jsonwebkey", "passphrase", "passwd", "password", "privatekey",
		"refreshtoken", "secret", "serviceaccountkey", "signaturekey",
		"subscriptionkey"
	};

	private static final Pattern _stringAccessorPattern = Pattern.compile(
		"public\\s+String(\\[\\])?\\s+(\\w+)\\s*\\(\\s*\\)\\s*;");

}