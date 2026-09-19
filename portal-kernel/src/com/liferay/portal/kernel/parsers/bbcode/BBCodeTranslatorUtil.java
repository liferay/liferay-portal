/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.parsers.bbcode;

import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Iliyan Peychev
 * @author Miguel Pastor
 */
public class BBCodeTranslatorUtil {

	public static BBCodeTranslator getBBCodeTranslator() {
		return _bbCodeTranslatorSnapshot.get();
	}

	public static String[] getEmoticonDescriptions() {
		BBCodeTranslator bbCodeTranslator = _bbCodeTranslatorSnapshot.get();

		return bbCodeTranslator.getEmoticonDescriptions();
	}

	public static String[] getEmoticonFiles() {
		BBCodeTranslator bbCodeTranslator = _bbCodeTranslatorSnapshot.get();

		return bbCodeTranslator.getEmoticonFiles();
	}

	public static String[] getEmoticonSymbols() {
		BBCodeTranslator bbCodeTranslator = _bbCodeTranslatorSnapshot.get();

		return bbCodeTranslator.getEmoticonSymbols();
	}

	public static String[][] getEmoticons() {
		BBCodeTranslator bbCodeTranslator = _bbCodeTranslatorSnapshot.get();

		return bbCodeTranslator.getEmoticons();
	}

	public static String getHTML(String bbcode) {
		BBCodeTranslator bbCodeTranslator = _bbCodeTranslatorSnapshot.get();

		return bbCodeTranslator.getHTML(bbcode);
	}

	public static String parse(String message) {
		BBCodeTranslator bbCodeTranslator = _bbCodeTranslatorSnapshot.get();

		return bbCodeTranslator.parse(message);
	}

	private BBCodeTranslatorUtil() {
	}

	private static final Snapshot<BBCodeTranslator> _bbCodeTranslatorSnapshot =
		new Snapshot<>(BBCodeTranslatorUtil.class, BBCodeTranslator.class);

}