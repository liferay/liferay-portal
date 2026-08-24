/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;
import com.liferay.trash.exception.RestoreEntryException;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

/**
 * @author Balazs Breier
 */
public class RestoreEntryExceptionMapper
	extends BaseExceptionMapper<RestoreEntryException> {

	public RestoreEntryExceptionMapper(Language language) {
		_language = language;
	}

	@Override
	protected Problem getProblem(RestoreEntryException restoreEntryException) {
		if (restoreEntryException.getType() ==
				RestoreEntryException.INVALID_STATUS) {

			return new Problem(
				Response.Status.BAD_REQUEST,
				_language.get(
					_acceptLanguage.getPreferredLocale(),
					"unable-to-restore-this-item-because-it-is-not-in-the-" +
						"recycle-bin"));
		}

		return new Problem(
			Response.Status.BAD_REQUEST,
			_language.get(
				_acceptLanguage.getPreferredLocale(),
				"unable-to-restore-this-item"));
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	private final Language _language;

}