/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.jaxrs.exception.mapper;

import com.liferay.exportimport.kernel.exception.RemoteExportException;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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
		"osgi.jaxrs.name=Export.Import.REST.RemoteConnectionExportExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class RemoteConnectionExportExceptionMapper
	extends BaseExceptionMapper<RemoteExportException> {

	@Override
	protected Problem getProblem(RemoteExportException remoteExportException) {
		return new Problem(
			Response.Status.BAD_REQUEST, _getMessage(remoteExportException));
	}

	private String _getMessage(RemoteExportException remoteExportException) {
		Locale locale = _acceptLanguage.getPreferredLocale();
		int type = remoteExportException.getType();

		if (type == RemoteExportException.BAD_CONNECTION) {
			String message = _language.format(
				locale,
				"could-not-connect-to-address-x.-please-verify-that-the-" +
					"specified-port-is-correct-and-that-the-remote-server-is-" +
						"configured-to-accept-requests-from-this-server",
				HtmlUtil.escape(remoteExportException.getURL()), false);

			return StringBundler.concat(
				message, StringPool.SPACE,
				_language.get(
					locale,
					"if-everything-is-configured-correctly,-but-you-still-" +
						"encounter-this-error,-the-administrator-has-the-" +
							"option-to-forcibly-disable-remote-staging"));
		}

		String groupId = String.valueOf(remoteExportException.getGroupId());

		if (type == RemoteExportException.INVALID_GROUP) {
			return _language.format(
				locale,
				"unable-to-activate-remote-staging-on-site-with-id-x.-global-" +
					"site-can-only-be-staged-to-another-global-site",
				groupId, false);
		}

		if (type == RemoteExportException.INVALID_STATE) {
			return _language.format(
				locale,
				"unable-to-activate-remote-staging-on-site-with-id-x.-the-" +
					"remote-live-site-cannot-be-locally-staged",
				groupId, false);
		}

		if (type == RemoteExportException.NO_GROUP) {
			return _language.format(
				locale, "no-site-exists-on-the-remote-server-with-site-id-x",
				groupId, false);
		}

		if (type == RemoteExportException.NO_PERMISSIONS) {
			return _language.format(
				locale,
				StringBundler.concat(
					"you-do-not-have-permissions-to-edit-the-site-with-id-x-",
					"on-the-remote-server.-please-verify-that-you-have-the-",
					"proper-permissions-in-both-the-live-environment-and-the-",
					"staging-environment"),
				groupId, false);
		}

		return _language.format(
			locale,
			"unable-to-activate-remote-staging-on-site-with-id-x.-the-remote-" +
				"live-site-must-be-different-from-the-stage-site",
			groupId, false);
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}