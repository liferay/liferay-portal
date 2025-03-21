/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.servlet.taglib;

import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(service = DynamicInclude.class)
public class CTTransactionExceptionDynamicInclude
	extends BaseCTExceptionDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (!SessionErrors.contains(
				httpServletRequest, CTTransactionException.class)) {

			return;
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	protected String getMessage(Locale locale) {
		return _language.get(
			locale, "this-action-can-only-be-performed-in-production-mode");
	}

	@Override
	protected String getTitle(Locale locale) {
		return _language.get(locale, "error");
	}

	@Reference
	private Language _language;

}