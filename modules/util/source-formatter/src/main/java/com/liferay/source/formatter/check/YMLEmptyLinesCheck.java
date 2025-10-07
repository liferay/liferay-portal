/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.check.util.YMLSourceUtil;

import java.io.IOException;

/**
 * @author Alan Huang
 */
public class YMLEmptyLinesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String blockStyleLeadingSpaces = null;
			boolean insideBlockStyle = false;
			String leadingSpaces = null;
			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.startsWith("{{- define ") && (sb.index() > 0)) {
					sb.append("\n");
				}

				if (insideBlockStyle) {
					sb.append(line);
					sb.append("\n");

					leadingSpaces = SourceUtil.getLeadingSpaces(line);

					if (!Validator.isBlank(line) &&
						(leadingSpaces.length() <=
							blockStyleLeadingSpaces.length()) &&
						!YMLSourceUtil.isBlockStyle(line)) {

						insideBlockStyle = false;
					}

					continue;
				}

				if (Validator.isBlank(line)) {
					continue;
				}

				sb.append(line);
				sb.append("\n");

				if (!YMLSourceUtil.isBlockStyle(line)) {
					continue;
				}

				blockStyleLeadingSpaces = SourceUtil.getLeadingSpaces(line);
				insideBlockStyle = true;
			}
		}

		if (sb.length() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

}