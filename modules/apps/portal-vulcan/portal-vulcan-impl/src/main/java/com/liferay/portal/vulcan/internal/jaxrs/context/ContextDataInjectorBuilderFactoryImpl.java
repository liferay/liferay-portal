/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.jaxrs.context.ContextDataInjector;
import com.liferay.portal.vulcan.jaxrs.context.ContextDataInjectorBuilder;
import com.liferay.portal.vulcan.jaxrs.context.ContextDataInjectorBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Correa
 */
@Component(service = ContextDataInjectorBuilderFactory.class)
public class ContextDataInjectorBuilderFactoryImpl
	implements ContextDataInjectorBuilderFactory {

	@Override
	public ContextDataInjectorBuilder builder() {
		return new ContextDataInjectorBuilder() {

			@Override
			public ContextDataInjectorBuilder acceptLanguage(
				AcceptLanguage acceptLanguage) {

				_acceptLanguage = acceptLanguage;

				return this;
			}

			@Override
			public ContextDataInjector build() {
				return instance -> _setInstanceFields(
					instance.getClass(), instance);
			}

			@Override
			public ContextDataInjectorBuilder company(Company company) {
				_company = company;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder expressionConvert(
				ExpressionConvert<?> expressionConvert) {

				_expressionConvert = expressionConvert;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder filterParserProvider(
				FilterParserProvider filterParserProvider) {

				_filterParserProvider = filterParserProvider;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder groupLocalService(
				GroupLocalService groupLocalService) {

				_groupLocalService = groupLocalService;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder httpServletRequest(
				HttpServletRequest httpServletRequest) {

				_httpServletRequest = httpServletRequest;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder httpServletResponse(
				HttpServletResponse httpServletResponse) {

				_httpServletResponse = httpServletResponse;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder resourceActionLocalService(
				ResourceActionLocalService resourceActionLocalService) {

				_resourceActionLocalService = resourceActionLocalService;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder resourcePermissionLocalService(
				ResourcePermissionLocalService resourcePermissionLocalService) {

				_resourcePermissionLocalService =
					resourcePermissionLocalService;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder roleLocalService(
				RoleLocalService roleLocalService) {

				_roleLocalService = roleLocalService;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder scopeChecker(
				Object scopeChecker) {

				_scopeChecker = scopeChecker;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder sortParserProvider(
				SortParserProvider sortParserProvider) {

				_sortParserProvider = sortParserProvider;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder uriInfo(UriInfo uriInfo) {
				_uriInfo = uriInfo;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder user(User user) {
				_user = user;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder
				vulcanBatchEngineExportTaskResource(
					VulcanBatchEngineExportTaskResource
						vulcanBatchEngineExportTaskResource) {

				_vulcanBatchEngineExportTaskResource =
					vulcanBatchEngineExportTaskResource;

				return this;
			}

			@Override
			public ContextDataInjectorBuilder
				vulcanBatchEngineImportTaskResource(
					VulcanBatchEngineImportTaskResource
						vulcanBatchEngineImportTaskResource) {

				_vulcanBatchEngineImportTaskResource =
					vulcanBatchEngineImportTaskResource;

				return this;
			}

			private Object _setInstanceFields(Class<?> clazz, Object instance)
				throws Exception {

				if (clazz == Object.class) {
					return instance;
				}

				Method[] methods = clazz.getDeclaredMethods();

				for (Field field : clazz.getDeclaredFields()) {
					if (Modifier.isFinal(field.getModifiers()) ||
						Modifier.isStatic(field.getModifiers())) {

						continue;
					}

					Class<?> fieldClass = field.getType();

					if (fieldClass.equals(Object.class) &&
						Objects.equals(
							field.getName(), "contextScopeChecker")) {

						_setValue(instance, field, methods, _scopeChecker);
					}
					else if (fieldClass.isAssignableFrom(
								AcceptLanguage.class)) {

						_setValue(instance, field, methods, _acceptLanguage);
					}
					else if (fieldClass.isAssignableFrom(Company.class)) {
						_setValue(instance, field, methods, _company);
					}
					else if (fieldClass.isAssignableFrom(
								ExpressionConvert.class)) {

						_setValue(instance, field, methods, _expressionConvert);
					}
					else if (fieldClass.isAssignableFrom(
								FilterParserProvider.class)) {

						_setValue(
							instance, field, methods, _filterParserProvider);
					}
					else if (fieldClass.isAssignableFrom(
								GroupLocalService.class)) {

						_setValue(instance, field, methods, _groupLocalService);
					}
					else if (fieldClass.isAssignableFrom(
								HttpServletRequest.class)) {

						_setValue(
							instance, field, methods, _httpServletRequest);
					}
					else if (fieldClass.isAssignableFrom(
								HttpServletResponse.class)) {

						_setValue(
							instance, field, methods, _httpServletResponse);
					}
					else if (fieldClass.isAssignableFrom(
								ResourceActionLocalService.class)) {

						_setValue(
							instance, field, methods,
							_resourceActionLocalService);
					}
					else if (fieldClass.isAssignableFrom(
								ResourcePermissionLocalService.class)) {

						_setValue(
							instance, field, methods,
							_resourcePermissionLocalService);
					}
					else if (fieldClass.isAssignableFrom(
								RoleLocalService.class)) {

						_setValue(instance, field, methods, _roleLocalService);
					}
					else if (fieldClass.isAssignableFrom(
								SortParserProvider.class)) {

						_setValue(
							instance, field, methods, _sortParserProvider);
					}
					else if (fieldClass.isAssignableFrom(UriInfo.class)) {
						_setValue(instance, field, methods, _uriInfo);
					}
					else if (fieldClass.isAssignableFrom(User.class)) {
						_setValue(instance, field, methods, _user);
					}
					else if (fieldClass.isAssignableFrom(
								VulcanBatchEngineExportTaskResource.class)) {

						_setValue(
							instance, field, methods,
							_vulcanBatchEngineExportTaskResource);
					}
					else if (fieldClass.isAssignableFrom(
								VulcanBatchEngineImportTaskResource.class)) {

						_setValue(
							instance, field, methods,
							_vulcanBatchEngineImportTaskResource);
					}
				}

				return _setInstanceFields(clazz.getSuperclass(), instance);
			}

			private void _setValue(
					Object instance, Field field, Method[] methods,
					Object value)
				throws Exception {

				if (value == null) {
					return;
				}

				for (Method method : methods) {
					String setterMethodName =
						"set" +
							StringUtil.upperCaseFirstLetter(field.getName());

					if (StringUtil.equals(method.getName(), setterMethodName) &&
						(method.getParameterCount() == 1) &&
						method.getParameterTypes()[0].isInstance(value) &&
						(method.getReturnType() == void.class)) {

						method.invoke(instance, value);

						return;
					}
				}

				field.setAccessible(true);

				field.set(instance, value);
			}

			private AcceptLanguage _acceptLanguage;
			private Company _company;
			private ExpressionConvert<?> _expressionConvert;
			private FilterParserProvider _filterParserProvider;
			private GroupLocalService _groupLocalService;
			private HttpServletRequest _httpServletRequest;
			private HttpServletResponse _httpServletResponse;
			private ResourceActionLocalService _resourceActionLocalService;
			private ResourcePermissionLocalService
				_resourcePermissionLocalService;
			private RoleLocalService _roleLocalService;
			private Object _scopeChecker;
			private SortParserProvider _sortParserProvider;
			private UriInfo _uriInfo;
			private User _user;
			private VulcanBatchEngineExportTaskResource
				_vulcanBatchEngineExportTaskResource;
			private VulcanBatchEngineImportTaskResource
				_vulcanBatchEngineImportTaskResource;

		};
	}

}