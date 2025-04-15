/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.jaxrs.extension.ExtendedEntity;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import java.io.IOException;

/**
 * @author Javier de Arcos
 */
@Provider
public class EntityExtensionWriterInterceptor implements WriterInterceptor {

	@Override
	public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext)
		throws IOException {

		EntityExtensionHandler entityExtensionHandler =
			_getEntityExtensionHandler(
				writerInterceptorContext.getType(),
				writerInterceptorContext.getMediaType());

		if (entityExtensionHandler != null) {
			_extendEntity(entityExtensionHandler, writerInterceptorContext);
		}

		writerInterceptorContext.proceed();
	}

	private void _extendEntity(
			EntityExtensionHandler entityExtensionHandler,
			WriterInterceptorContext writerInterceptorContext)
		throws IOException {

		try {
			writerInterceptorContext.setEntity(
				ExtendedEntity.extend(
					writerInterceptorContext.getEntity(),
					entityExtensionHandler.getExtendedProperties(
						_company.getCompanyId(), _user.getUserId(),
						writerInterceptorContext.getEntity()),
					entityExtensionHandler.getFilteredPropertyNames(
						_company.getCompanyId(),
						writerInterceptorContext.getEntity())));
			writerInterceptorContext.setGenericType(ExtendedEntity.class);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new IOException(exception);
		}
	}

	private EntityExtensionHandler _getEntityExtensionHandler(
		Class<?> clazz, MediaType mediaType) {

		ContextResolver<EntityExtensionHandler> contextResolver =
			_providers.getContextResolver(
				EntityExtensionHandler.class, mediaType);

		if (contextResolver == null) {
			return null;
		}

		return contextResolver.getContext(clazz);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EntityExtensionWriterInterceptor.class);

	@Context
	private Company _company;

	@Context
	private Providers _providers;

	@Context
	private User _user;

}