/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Christopher Kian
 */
public class BouncyCastleFIPSCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (absolutePath.contains("/modules/third-party/")) {
			return content;
		}

		if (fileName.endsWith(".gradle")) {
			_check(fileName, content, _artifactPattern);
		}
		else {
			_check(fileName, content, _importPattern);
		}

		return content;
	}

	private void _check(String fileName, String content, Pattern pattern) {
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			addMessage(
				fileName, "Do not use non-FIPS BouncyCastle, see LPD-90318",
				getLineNumber(content, matcher.start(1)));
		}
	}

	private static final Pattern _artifactPattern = Pattern.compile(
		"name: \"(bc(?:mail|pkix|prov|util)-jdk[^\"]*)\"");
	private static final Pattern _importPattern = Pattern.compile(
		"\nimport (?:static )?(org\\.bouncycastle\\.(?:crypto\\.(?:engines|" +
			"generators|macs|modes|paddings|params|prng|signers)|jce" +
				"\\.provider|jcajce\\.provider)\\.[^;]+);");

}