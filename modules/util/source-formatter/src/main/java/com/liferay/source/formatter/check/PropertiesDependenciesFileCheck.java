/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class PropertiesDependenciesFileCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (fileName.endsWith("/dependencies.properties")) {
			return _formatDependenciesProperties(content);
		}

		return content;
	}

	private String _formatDependenciesProperties(String content) {
		List<String> lines = ListUtil.fromString(content);

		lines = ListUtil.sort(lines);

		StringBundler sb = new StringBundler(content.length() * 2);

		for (String line : lines) {
			line = StringUtil.removeChar(line, CharPool.SPACE);

			if (Validator.isNull(line) || (line.charAt(0) == CharPool.POUND)) {
				continue;
			}

			sb.append(line);
			sb.append(CharPool.NEW_LINE);
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

}