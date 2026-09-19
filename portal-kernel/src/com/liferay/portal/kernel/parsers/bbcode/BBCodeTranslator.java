/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.parsers.bbcode;

/**
 * @author Iliyan Peychev
 * @author Miguel Pastor
 */
public interface BBCodeTranslator {

	public String[] getEmoticonDescriptions();

	public String[] getEmoticonFiles();

	public String[] getEmoticonSymbols();

	public String[][] getEmoticons();

	public String getHTML(String bbcode);

	public String parse(String message);

}