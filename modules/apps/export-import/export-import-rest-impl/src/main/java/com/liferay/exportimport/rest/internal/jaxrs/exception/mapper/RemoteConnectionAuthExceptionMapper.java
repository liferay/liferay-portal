/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.jaxrs.exception.mapper;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.RemoteAuthException;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Raposo
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Export.Import.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Export.Import.REST.RemoteConnectionAuthExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class RemoteConnectionAuthExceptionMapper
	extends BaseExceptionMapper<AuthException> {

	@Override
	protected Problem getProblem(AuthException authException) {
		Locale locale = _acceptLanguage.getPreferredLocale();

		String message = _language.get(locale, _getKey(authException));

		if (authException instanceof RemoteAuthException remoteAuthException) {
			message = StringBundler.concat(
				_language.format(
					locale,
					"an-unexpected-error-occurred-in-the-remote-server-at-x",
					HtmlUtil.escape(remoteAuthException.getURL()), false),
				StringPool.SPACE, message);
		}

		return new Problem(Response.Status.BAD_REQUEST, message);
	}

	private String _getKey(AuthException authException) {
		int type = authException.getType();

		if (type == AuthException.INVALID_SHARED_SECRET) {
			return "the-tunneling-servlet-shared-secret-must-be-at-least-8-" +
				"bytes-long";
		}

		if (type == AuthException.NO_SHARED_SECRET) {
			return "the-tunneling-servlet-shared-secret-is-not-set";
		}

		if (type == RemoteAuthException.WRONG_SHARED_SECRET) {
			return "the-tunneling-servlet-shared-secrets-do-not-match";
		}

		return "internal-server-error";
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}