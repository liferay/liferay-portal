/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.script;

import java.net.URL;

/**
 * @author Kenji Heigel
 */
public class UnbalancedCodeException extends PoshiScriptParserException {

	public UnbalancedCodeException(
		String msg, int index, String code, URL filePathURL) {

		super(
			msg, _getErrorLineNumber(index, code),
			_getErrorSnippet(index, code), filePathURL);
	}

	private static int _getErrorLineNumber(int index, String code) {
		int lineNumber = 1;

		for (int i = 0; i < index; i++) {
			if (code.charAt(i) != '\n') {
				continue;
			}

			lineNumber++;
		}

		return lineNumber;
	}

	private static String _getErrorSnippet(int index, String code) {
		int lineNumber = 1;

		int newLineIndex = -1;

		for (int i = 0; i < index; i++) {
			if (code.charAt(i) != '\n') {
				continue;
			}

			lineNumber++;

			newLineIndex = i;
		}

		int column = 1;

		for (int i = newLineIndex + 1; i < index; i++) {
			if (code.charAt(i) == '\t') {
				column += 4;

				continue;
			}

			column++;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(_getLine(lineNumber, code));
		sb.append("\n");

		for (int i = 1; i < column; i++) {
			sb.append(" ");
		}

		sb.append("^");

		return sb.toString();
	}

	private static String _getLine(int lineNumber, String code) {
		String[] lines = code.split("\n");

		return lines[lineNumber - 1].replace("\t", "    ");
	}

}