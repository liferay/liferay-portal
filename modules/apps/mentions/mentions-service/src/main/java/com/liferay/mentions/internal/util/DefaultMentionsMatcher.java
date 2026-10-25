/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.internal.util;

import com.liferay.mentions.matcher.BaseRegularExpressionMentionsMatcher;
import com.liferay.mentions.matcher.MentionsMatcherUtil;

/**
 * @author Adolfo Pérez
 */
public class DefaultMentionsMatcher
	extends BaseRegularExpressionMentionsMatcher {

	@Override
	protected String getRegularExpression() {
		return _REGULAR_EXPRESSION;
	}

	private static final String _MENTIONS_REGULAR_EXPRESSION_TEMPLATE =
		"(?:\\s|^|\\]|>)(?:@|&#64;)(%s)(?=[<\\[\\s]|$)";

	private static final String _REGULAR_EXPRESSION;

	static {
		_REGULAR_EXPRESSION = String.format(
			_MENTIONS_REGULAR_EXPRESSION_TEMPLATE,
			MentionsMatcherUtil.getScreenNameRegularExpression());
	}

}