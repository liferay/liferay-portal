/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.exception;

import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.Locale;

/**
 * @author Shuyang Zhou
 */
public class InfoFormOptimisticLockException extends InfoFormException {

	@Override
	public String getLocalizedMessage(Locale locale) {
		return LanguageUtil.get(
			locale,
			"another-user-has-made-changes-since-you-started-editing-please-" +
				"copy-your-changes-and-try-again");
	}

}