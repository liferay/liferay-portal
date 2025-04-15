/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectRelationshipDeletionTypeException;
import com.liferay.object.jaxrs.exception.mapper.util.ObjectExceptionMapperUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * @author Murilo Stodolni
 */
@Provider
public class ObjectRelationshipDeletionTypeExceptionMapper
	extends BaseExceptionMapper<ObjectRelationshipDeletionTypeException> {

	public ObjectRelationshipDeletionTypeExceptionMapper(Language language) {
		_language = language;
	}

	@Override
	protected Problem getProblem(
		ObjectRelationshipDeletionTypeException
			objectRelationshipDeletionTypeException) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			ObjectExceptionMapperUtil.getTitle(
				_acceptLanguage, null, _language,
				objectRelationshipDeletionTypeException.getMessage(),
				objectRelationshipDeletionTypeException.getMessageKey()));
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	private final Language _language;

}