/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

/**
 * @author Murilo Stodolni
 */
public class DuplicateExternalReferenceCodeExceptionMapper
	extends BaseExceptionMapper<DuplicateExternalReferenceCodeException> {

	public DuplicateExternalReferenceCodeExceptionMapper(Language language) {
		_language = language;
	}

	@Override
	protected Problem getProblem(
		DuplicateExternalReferenceCodeException
			duplicateExternalReferenceCodeException) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			_language.get(
				_acceptLanguage.getPreferredLocale(),
				"this-external-reference-code-is-already-in-use"));
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	private final Language _language;

}