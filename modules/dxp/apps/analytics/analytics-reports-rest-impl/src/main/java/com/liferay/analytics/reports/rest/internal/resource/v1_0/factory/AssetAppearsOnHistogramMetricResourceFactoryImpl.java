/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.internal.resource.v1_0.factory;

import com.liferay.analytics.reports.rest.internal.security.permission.LiberalPermissionChecker;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetAppearsOnHistogramMetricResource;
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
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Marcos Martins
 * @generated
 */
@Component(
	property = "resource.locator.key=/analytics-reports-rest/v1.0/AssetAppearsOnHistogramMetric",
	service = AssetAppearsOnHistogramMetricResource.Factory.class
)
@Generated("")
public class AssetAppearsOnHistogramMetricResourceFactoryImpl
	implements AssetAppearsOnHistogramMetricResource.Factory {

	@Override
	public AssetAppearsOnHistogramMetricResource.Builder create() {
		return new AssetAppearsOnHistogramMetricResource.Builder() {

			@Override
			public AssetAppearsOnHistogramMetricResource build() {
				if (_user == null) {
					throw new IllegalArgumentException("User is not set");
				}

				Function
					<InvocationHandler, AssetAppearsOnHistogramMetricResource>
						assetAppearsOnHistogramMetricResourceProxyProviderFunction =
							ResourceProxyProviderFunctionHolder.
								_assetAppearsOnHistogramMetricResourceProxyProviderFunction;

				return assetAppearsOnHistogramMetricResourceProxyProviderFunction.
					apply(
						(proxy, method, arguments) -> _invoke(
							method, arguments, _checkPermissions,
							_httpServletRequest, _httpServletResponse,
							_preferredLocale, _uriInfo, _user));
			}

			@Override
			public AssetAppearsOnHistogramMetricResource.Builder
				checkPermissions(boolean checkPermissions) {

				_checkPermissions = checkPermissions;

				return this;
			}

			@Override
			public AssetAppearsOnHistogramMetricResource.Builder
				httpServletRequest(HttpServletRequest httpServletRequest) {

				_httpServletRequest = httpServletRequest;

				return this;
			}

			@Override
			public AssetAppearsOnHistogramMetricResource.Builder
				httpServletResponse(HttpServletResponse httpServletResponse) {

				_httpServletResponse = httpServletResponse;

				return this;
			}

			@Override
			public AssetAppearsOnHistogramMetricResource.Builder
				preferredLocale(Locale preferredLocale) {

				_preferredLocale = preferredLocale;

				return this;
			}

			@Override
			public AssetAppearsOnHistogramMetricResource.Builder uriInfo(
				UriInfo uriInfo) {

				_uriInfo = uriInfo;

				return this;
			}

			@Override
			public AssetAppearsOnHistogramMetricResource.Builder user(
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
		<InvocationHandler, AssetAppearsOnHistogramMetricResource>
			_getProxyProviderFunction() {

		Class<?> proxyClass = ProxyUtil.getProxyClass(
			AssetAppearsOnHistogramMetricResource.class.getClassLoader(),
			AssetAppearsOnHistogramMetricResource.class);

		try {
			Constructor<AssetAppearsOnHistogramMetricResource> constructor =
				(Constructor<AssetAppearsOnHistogramMetricResource>)
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

		AssetAppearsOnHistogramMetricResource
			assetAppearsOnHistogramMetricResource =
				_componentServiceObjects.getService();

		assetAppearsOnHistogramMetricResource.setContextAcceptLanguage(
			new AcceptLanguageImpl(httpServletRequest, preferredLocale, user));

		Company company = _companyLocalService.getCompany(user.getCompanyId());

		assetAppearsOnHistogramMetricResource.setContextCompany(company);

		assetAppearsOnHistogramMetricResource.setContextHttpServletRequest(
			httpServletRequest);
		assetAppearsOnHistogramMetricResource.setContextHttpServletResponse(
			httpServletResponse);
		assetAppearsOnHistogramMetricResource.setContextUriInfo(uriInfo);
		assetAppearsOnHistogramMetricResource.setContextUser(user);
		assetAppearsOnHistogramMetricResource.setExpressionConvert(
			_expressionConvert);
		assetAppearsOnHistogramMetricResource.setFilterParserProvider(
			_filterParserProvider);
		assetAppearsOnHistogramMetricResource.setGroupLocalService(
			_groupLocalService);
		assetAppearsOnHistogramMetricResource.setResourceActionLocalService(
			_resourceActionLocalService);
		assetAppearsOnHistogramMetricResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		assetAppearsOnHistogramMetricResource.setRoleLocalService(
			_roleLocalService);
		assetAppearsOnHistogramMetricResource.setSortParserProvider(
			_sortParserProvider);

		try {
			return method.invoke(
				assetAppearsOnHistogramMetricResource, arguments);
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getTargetException();
		}
		finally {
			_componentServiceObjects.ungetService(
				assetAppearsOnHistogramMetricResource);

			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssetAppearsOnHistogramMetricResource>
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
			<InvocationHandler, AssetAppearsOnHistogramMetricResource>
				_assetAppearsOnHistogramMetricResourceProxyProviderFunction =
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