/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

/**
 * @author Seiphon Wang
 */
public class IncorrectFilePathCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		String rootDirName = SourceUtil.getRootDirName(absolutePath);

		if (Validator.isNull(rootDirName)) {
			return content;
		}

		String relativePath = absolutePath.substring(rootDirName.length());

		for (String path : relativePath.split("/")) {
			if (Validator.isNull(path)) {
				continue;
			}

			if (path.endsWith(StringPool.SPACE) ||
				path.startsWith(StringPool.SPACE)) {

				addMessage(
					fileName,
					StringBundler.concat(
						"Do not add leading/trailing spaces in file or folder ",
						"names \"", path, "\""));
			}

			for (String illegalCharacter : _ILLEGAL_CHARACTERS) {
				if (!path.contains(illegalCharacter)) {
					continue;
				}

				addMessage(
					fileName,
					StringBundler.concat(
						"Do not use \"", illegalCharacter, "\" in file or ",
						"folder names \"", path, "\""));
			}
		}

		return content;
	}

	private static final String[] _ILLEGAL_CHARACTERS = {
		StringPool.BACK_SLASH, StringPool.COLON, StringPool.FORWARD_SLASH,
		StringPool.GREATER_THAN, StringPool.LESS_THAN, StringPool.PIPE,
		StringPool.QUESTION, StringPool.QUOTE, StringPool.STAR
	};

}