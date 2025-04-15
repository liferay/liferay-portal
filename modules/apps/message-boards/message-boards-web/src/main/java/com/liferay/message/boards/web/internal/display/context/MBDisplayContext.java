/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * @author Jürgen KAppler
 */
public class MBDisplayContext {

	public MBDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public String getModifiedLabel(MBMessage message) {
		String messageUserName = "anonymous";

		if (!message.isAnonymous()) {
			messageUserName = message.getStatusByUserName();
		}

		Date modifiedDate = message.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - modifiedDate.getTime(), true);

		return LanguageUtil.format(
			_httpServletRequest, "x-modified-x-ago",
			new String[] {messageUserName, modifiedDateDescription});
	}

	private final HttpServletRequest _httpServletRequest;

}