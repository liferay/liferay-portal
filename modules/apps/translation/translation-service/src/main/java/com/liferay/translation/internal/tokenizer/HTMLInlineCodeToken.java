/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.tokenizer;

import com.liferay.petra.string.StringBundler;

/**
 * @author Akhash Ramprakash
 */
public class HTMLInlineCodeToken {

	public HTMLInlineCodeToken(String rawText, String tagName, Type type) {
		_rawText = rawText;
		_tagName = tagName;
		_type = type;
	}

	public String getRawText() {
		return _rawText;
	}

	public String getTagName() {
		return _tagName;
	}

	public Type getType() {
		return _type;
	}

	@Override
	public String toString() {
		return StringBundler.concat(_type, "[", _rawText, "]");
	}

	public enum Type {

		CLOSING, OPENING, PLACEHOLDER, TEXT

	}

	private final String _rawText;
	private final String _tagName;
	private final Type _type;

}