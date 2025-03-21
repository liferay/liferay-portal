/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.GenericError;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
public abstract class BaseSLAExceptionMapper<T extends PortalException>
	implements ExceptionMapper<T> {

	public abstract List<GenericError> toGenericErrors(T portalException);

	@Override
	public Response toResponse(T portalException) {
		return Response.status(
			Response.Status.BAD_REQUEST
		).entity(
			toGenericErrors(portalException)
		).build();
	}

	protected String getMessage(String key) {
		return language.get(_acceptLanguage.getPreferredLocale(), key);
	}

	@Reference
	protected Language language;

	@Context
	private AcceptLanguage _acceptLanguage;

}