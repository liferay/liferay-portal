/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Larissa Ribeiro
 */
public class CMPRichTextUtil {

	public static String toHTML(String text) {
		if (Validator.isBlank(text)) {
			return text;
		}

		StringBundler sb = new StringBundler();

		String[] paragraphs = StringUtil.split(
			StringUtil.replace(
				text,
				new String[] {StringPool.RETURN_NEW_LINE, StringPool.RETURN},
				new String[] {StringPool.NEW_LINE, StringPool.NEW_LINE}),
			StringPool.NEW_LINE + StringPool.NEW_LINE);

		for (String paragraph : paragraphs) {
			if (Validator.isBlank(paragraph)) {
				continue;
			}

			sb.append("<p>");

			String[] lines = StringUtil.split(paragraph, StringPool.NEW_LINE);

			for (int i = 0; i < lines.length; i++) {
				if (i > 0) {
					sb.append("<br>");
				}

				sb.append(HtmlUtil.escape(lines[i]));
			}

			sb.append("</p>");
		}

		return sb.toString();
	}

}