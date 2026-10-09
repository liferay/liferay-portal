/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.xml;

import com.liferay.portal.kernel.util.Validator;

/**
 * @author Leonardo Barros
 * @author Julius Lee
 */
public class XMLUtil {

	public static String stripInvalidChars(String xml) {
		if (Validator.isNull(xml)) {
			return xml;
		}

		// Strip characters that are not valid in the 1.0 XML spec
		// http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < xml.length(); i++) {
			char c1 = xml.charAt(i);

			if ((c1 == 0x9) || (c1 == 0xA) || (c1 == 0xD) ||
				((c1 >= 0x20) && (c1 <= 0xD7FF)) ||
				((c1 >= 0xE000) && (c1 <= 0xFFFD))) {

				sb.append(c1);
			}

			if (!Character.isHighSurrogate(c1) || ((i + 1) >= xml.length())) {
				continue;
			}

			char c2 = xml.charAt(i + 1);

			if (!Character.isLowSurrogate(c2)) {
				continue;
			}

			int codePoint = Character.toCodePoint(c1, c2);

			if ((codePoint < 0x10000) || (codePoint > 0x10FFFF)) {
				continue;
			}

			sb.appendCodePoint(codePoint);
		}

		return sb.toString();
	}

}