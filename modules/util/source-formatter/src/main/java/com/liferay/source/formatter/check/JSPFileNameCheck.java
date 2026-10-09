/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;

import java.util.Arrays;
import java.util.List;

/**
 * @author Hugo Huijser
 */
public class JSPFileNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith(".jsp") && !fileName.endsWith(".jspf")) {
			return content;
		}

		int x = absolutePath.lastIndexOf(CharPool.SLASH);
		int y = absolutePath.lastIndexOf(CharPool.PERIOD);

		String shortFileName = absolutePath.substring(x + 1, y);

		for (String allowedSuffix : _allowedSuffixes) {
			if (shortFileName.endsWith(allowedSuffix)) {
				return content;
			}
		}

		for (char c : shortFileName.toCharArray()) {
			if (Character.isLetterOrDigit(c) || (c == CharPool.UNDERLINE)) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat("Do not use \"", c, "\" in file name"));
		}

		return content;
	}

	private static final List<String> _allowedSuffixes = Arrays.asList(
		"-compat", "-ext", "-ext-post", "-ext-pre");

}