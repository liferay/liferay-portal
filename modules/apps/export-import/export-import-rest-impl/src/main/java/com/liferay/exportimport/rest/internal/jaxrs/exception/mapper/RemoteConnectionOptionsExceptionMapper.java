/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.RemoteOptionsException;
import com.liferay.portal.kernel.language.Language;
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
		"osgi.jaxrs.name=Export.Import.REST.RemoteConnectionOptionsExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class RemoteConnectionOptionsExceptionMapper
	extends BaseExceptionMapper<RemoteOptionsException> {

	@Override
	protected Problem getProblem(
		RemoteOptionsException remoteOptionsException) {

		return new Problem(
			Response.Status.BAD_REQUEST, _getMessage(remoteOptionsException));
	}

	private String _getMessage(RemoteOptionsException remoteOptionsException) {
		Locale locale = _acceptLanguage.getPreferredLocale();
		int type = remoteOptionsException.getType();

		if (type == RemoteOptionsException.REMOTE_ADDRESS) {
			return _language.format(
				locale, "the-remote-address-x-is-not-valid",
				HtmlUtil.escape(remoteOptionsException.getRemoteAddress()),
				false);
		}

		if (type == RemoteOptionsException.REMOTE_GROUP_ID) {
			return _language.format(
				locale, "the-remote-site-id-x-is-not-valid",
				String.valueOf(remoteOptionsException.getRemoteGroupId()),
				false);
		}

		if (type == RemoteOptionsException.REMOTE_PATH_CONTEXT) {
			return _language.format(
				locale, "the-remote-path-context-x-is-not-valid",
				HtmlUtil.escape(remoteOptionsException.getRemotePathContext()),
				false);
		}

		return _language.format(
			locale, "the-remote-port-x-is-not-valid",
			String.valueOf(remoteOptionsException.getRemotePort()), false);
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}