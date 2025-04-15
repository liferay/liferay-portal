/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.factory;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.tools.rest.builder.test.internal.security.permission.LiberalPermissionChecker;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.EntityModelResourceTestEntity1Resource;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Component(
	property = "resource.locator.key=/test/v1.0/EntityModelResourceTestEntity1",
	service = EntityModelResourceTestEntity1Resource.Factory.class
)
@Generated("")
public class EntityModelResourceTestEntity1ResourceFactoryImpl
	implements EntityModelResourceTestEntity1Resource.Factory {

	@Override
	public EntityModelResourceTestEntity1Resource.Builder create() {
		return new EntityModelResourceTestEntity1Resource.Builder() {

			@Override
			public EntityModelResourceTestEntity1Resource build() {
				if (_user == null) {
					throw new IllegalArgumentException("User is not set");
				}

				Function
					<InvocationHandler, EntityModelResourceTestEntity1Resource>
						entityModelResourceTestEntity1ResourceProxyProviderFunction =
							ResourceProxyProviderFunctionHolder.
								_entityModelResourceTestEntity1ResourceProxyProviderFunction;

				return entityModelResourceTestEntity1ResourceProxyProviderFunction.
					apply(
						(proxy, method, arguments) -> _invoke(
							method, arguments, _checkPermissions,
							_httpServletRequest, _httpServletResponse,
							_preferredLocale, _uriInfo, _user));
			}

			@Override
			public EntityModelResourceTestEntity1Resource.Builder
				checkPermissions(boolean checkPermissions) {

				_checkPermissions = checkPermissions;

				return this;
			}

			@Override
			public EntityModelResourceTestEntity1Resource.Builder
				httpServletRequest(HttpServletRequest httpServletRequest) {

				_httpServletRequest = httpServletRequest;

				return this;
			}

			@Override
			public EntityModelResourceTestEntity1Resource.Builder
				httpServletResponse(HttpServletResponse httpServletResponse) {

				_httpServletResponse = httpServletResponse;

				return this;
			}

			@Override
			public EntityModelResourceTestEntity1Resource.Builder
				preferredLocale(Locale preferredLocale) {

				_preferredLocale = preferredLocale;

				return this;
			}

			@Override
			public EntityModelResourceTestEntity1Resource.Builder uriInfo(
				UriInfo uriInfo) {

				_uriInfo = uriInfo;

				return this;
			}

			@Override
			public EntityModelResourceTestEntity1Resource.Builder user(
				User user) {

				_user = user;

				return this;
			}

			private boolean _checkPermissions = true;
			private HttpServletRequest _httpServletRequest;
			private HttpServletResponse _httpServletResponse;
			private Locale _preferredLocale;
			private UriInfo _uriInfo;
			private User _user;

		};
	}

	private static Function
		<InvocationHandler, EntityModelResourceTestEntity1Resource>
			_getProxyProviderFunction() {

		Class<?> proxyClass = ProxyUtil.getProxyClass(
			EntityModelResourceTestEntity1Resource.class.getClassLoader(),
			EntityModelResourceTestEntity1Resource.class);

		try {
			Constructor<EntityModelResourceTestEntity1Resource> constructor =
				(Constructor<EntityModelResourceTestEntity1Resource>)
					proxyClass.getConstructor(InvocationHandler.class);

			return invocationHandler -> {
				try {
					return constructor.newInstance(invocationHandler);
				}
				catch (ReflectiveOperationException
							reflectiveOperationException) {

					throw new InternalError(reflectiveOperationException);
				}
			};
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new InternalError(noSuchMethodException);
		}
	}

	private Object _invoke(
			Method method, Object[] arguments, boolean checkPermissions,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Locale preferredLocale,
			UriInfo uriInfo, User user)
		throws Throwable {

		String name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(user.getUserId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (checkPermissions) {
			PermissionThreadLocal.setPermissionChecker(
				_defaultPermissionCheckerFactory.create(user));
		}
		else {
			PermissionThreadLocal.setPermissionChecker(
				new LiberalPermissionChecker(user));
		}

		EntityModelResourceTestEntity1Resource
			entityModelResourceTestEntity1Resource =
				_componentServiceObjects.getService();

		entityModelResourceTestEntity1Resource.setContextAcceptLanguage(
			new AcceptLanguageImpl(httpServletRequest, preferredLocale, user));

		Company company = _companyLocalService.getCompany(user.getCompanyId());

		entityModelResourceTestEntity1Resource.setContextCompany(company);

		entityModelResourceTestEntity1Resource.setContextHttpServletRequest(
			httpServletRequest);
		entityModelResourceTestEntity1Resource.setContextHttpServletResponse(
			httpServletResponse);
		entityModelResourceTestEntity1Resource.setContextUriInfo(uriInfo);
		entityModelResourceTestEntity1Resource.setContextUser(user);
		entityModelResourceTestEntity1Resource.setExpressionConvert(
			_expressionConvert);
		entityModelResourceTestEntity1Resource.setFilterParserProvider(
			_filterParserProvider);
		entityModelResourceTestEntity1Resource.setGroupLocalService(
			_groupLocalService);
		entityModelResourceTestEntity1Resource.setResourceActionLocalService(
			_resourceActionLocalService);
		entityModelResourceTestEntity1Resource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		entityModelResourceTestEntity1Resource.setRoleLocalService(
			_roleLocalService);
		entityModelResourceTestEntity1Resource.setSortParserProvider(
			_sortParserProvider);

		try {
			return method.invoke(
				entityModelResourceTestEntity1Resource, arguments);
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getTargetException();
		}
		finally {
			_componentServiceObjects.ungetService(
				entityModelResourceTestEntity1Resource);

			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<EntityModelResourceTestEntity1Resource>
		_componentServiceObjects;

	@Reference
	private PermissionCheckerFactory _defaultPermissionCheckerFactory;

	@Reference(
		target = "(result.class.name=com.liferay.portal.kernel.search.filter.Filter)"
	)
	private ExpressionConvert<Filter> _expressionConvert;

	@Reference
	private FilterParserProvider _filterParserProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SortParserProvider _sortParserProvider;

	@Reference
	private UserLocalService _userLocalService;

	private static class ResourceProxyProviderFunctionHolder {

		private static final Function
			<InvocationHandler, EntityModelResourceTestEntity1Resource>
				_entityModelResourceTestEntity1ResourceProxyProviderFunction =
					_getProxyProviderFunction();

	}

	private class AcceptLanguageImpl implements AcceptLanguage {

		public AcceptLanguageImpl(
			HttpServletRequest httpServletRequest, Locale preferredLocale,
			User user) {

			_httpServletRequest = httpServletRequest;
			_preferredLocale = preferredLocale;
			_user = user;
		}

		@Override
		public List<Locale> getLocales() {
			return Arrays.asList(getPreferredLocale());
		}

		@Override
		public String getPreferredLanguageId() {
			return LocaleUtil.toLanguageId(getPreferredLocale());
		}

		@Override
		public Locale getPreferredLocale() {
			if (_preferredLocale != null) {
				return _preferredLocale;
			}

			if (_httpServletRequest != null) {
				Locale locale = (Locale)_httpServletRequest.getAttribute(
					WebKeys.LOCALE);

				if (locale != null) {
					return locale;
				}
			}

			return _user.getLocale();
		}

		@Override
		public boolean isAcceptAllLanguages() {
			return false;
		}

		private final HttpServletRequest _httpServletRequest;
		private final Locale _preferredLocale;
		private final User _user;

	}

}