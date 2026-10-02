/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.processor.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Alberto Chaparro
 */
public class FragmentEntryHtmlParserUtil {

	public static Document parse(String html) {
		Document document = Jsoup.parse(html);

		_disablePrettyPrint(document);

		return document;
	}

	public static Document parseBodyFragment(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		_disablePrettyPrint(document);

		return document;
	}

	private static void _disablePrettyPrint(Document document) {
		Document.OutputSettings outputSettings = document.outputSettings();

		outputSettings.prettyPrint(false);
	}

}