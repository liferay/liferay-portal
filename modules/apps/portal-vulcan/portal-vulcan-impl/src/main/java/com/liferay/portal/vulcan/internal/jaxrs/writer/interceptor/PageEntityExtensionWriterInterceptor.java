/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.jaxrs.extension.ExtendedEntity;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import java.io.IOException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Javier de Arcos
 */
@Provider
public class PageEntityExtensionWriterInterceptor implements WriterInterceptor {

	@Override
	public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext)
		throws IOException {

		if (Page.class.isAssignableFrom(writerInterceptorContext.getType()) &&
			(writerInterceptorContext.getGenericType() instanceof
				ParameterizedType)) {

			ParameterizedType parameterizedType =
				(ParameterizedType)writerInterceptorContext.getGenericType();

			Type entityType = parameterizedType.getActualTypeArguments()[0];

			EntityExtensionHandler entityExtensionHandler =
				_getEntityExtensionHandler(
					(Class)entityType, writerInterceptorContext.getMediaType());

			if (entityExtensionHandler != null) {
				_extendPageEntities(
					entityExtensionHandler, writerInterceptorContext);
			}
		}

		writerInterceptorContext.proceed();
	}

	private void _extendPageEntities(
			EntityExtensionHandler entityExtensionHandler,
			WriterInterceptorContext writerInterceptorContext)
		throws IOException {

		Page<?> page = (Page<?>)writerInterceptorContext.getEntity();

		List<ExtendedEntity> extendedEntities = new ArrayList<>();

		try {
			for (Object item : page.getItems()) {
				extendedEntities.add(
					ExtendedEntity.extend(
						item,
						entityExtensionHandler.getExtendedProperties(
							_company.getCompanyId(), _user.getUserId(), item),
						entityExtensionHandler.getFilteredPropertyNames(
							_company.getCompanyId(), item)));
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new IOException(exception);
		}

		Pagination pagination = Pagination.of(
			GetterUtil.getInteger(page.getPage()),
			GetterUtil.getInteger(page.getPageSize()));

		writerInterceptorContext.setEntity(
			Page.of(
				page.getActions(), extendedEntities, pagination,
				page.getTotalCount()));

		writerInterceptorContext.setGenericType(
			new GenericType<Page<ExtendedEntity>>() {
			}.getType());
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
		PageEntityExtensionWriterInterceptor.class);

	@Context
	private Company _company;

	@Context
	private Providers _providers;

	@Context
	private User _user;

}