/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pedro Victor Silvestre
 */
public class CredentialBufferCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (absolutePath.contains("/modules/third-party/") ||
			absolutePath.contains("/src/test/") ||
			absolutePath.contains("/src/testIntegration/") ||
			absolutePath.contains("/test/unit/")) {

			return content;
		}

		_check(fileName, content, _constructorPattern);
		_check(fileName, content, _methodPattern);

		return content;
	}

	private void _check(String fileName, String content, Pattern pattern) {
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			addMessage(
				fileName,
				"Assign the buffer to a local variable so that it can be " +
					"cleared after use, see LPD-93280",
				getLineNumber(content, matcher.start()));
		}
	}

	private static final String _DERIVED_BUFFER =
		"[\\w.]+(?:\\([^()]*\\))?\\.(?:getBytes|toCharArray)\\([^()]*\\)";

	private static final String _METHOD_PREFIX =
		"\\.(?:getKey|init|load|loadKeyMaterial|loadTrustMaterial|" +
			"setKeyEntry|setPassword|store)\\(" +
				"(?:(?:[^();]|\\([^()]*\\))*?,\\s*)?";

	private static final Pattern _constructorPattern = Pattern.compile(
		"new (?:KeyStore\\.PasswordProtection|PBEKeySpec|SecretKeySpec)" +
			"\\(\\s*" + _DERIVED_BUFFER);
	private static final Pattern _methodPattern = Pattern.compile(
		_METHOD_PREFIX + _DERIVED_BUFFER);

}