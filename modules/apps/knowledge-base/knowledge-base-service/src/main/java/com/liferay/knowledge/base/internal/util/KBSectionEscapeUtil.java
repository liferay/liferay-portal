/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Preston Crary
 */
public class KBSectionEscapeUtil {

	public static String[] escapeSections(String[] sections) {
		if (ArrayUtil.isEmpty(sections)) {
			return new String[0];
		}

		sections = ArrayUtil.clone(sections);

		for (int i = 0; i < sections.length; i++) {
			sections[i] = StringBundler.concat(
				StringPool.UNDERLINE, sections[i], StringPool.UNDERLINE);
		}

		return sections;
	}

	public static String[] unescapeSections(String sections) {
		String[] sectionsArray = StringUtil.split(sections);

		for (int i = 0; i < sectionsArray.length; i++) {
			String section = sectionsArray[i];

			if (!StringUtil.startsWith(section, StringPool.UNDERLINE) ||
				!StringUtil.endsWith(section, StringPool.UNDERLINE)) {

				continue;
			}

			sectionsArray[i] = section.substring(1, section.length() - 1);
		}

		return sectionsArray;
	}

	private KBSectionEscapeUtil() {
	}

}