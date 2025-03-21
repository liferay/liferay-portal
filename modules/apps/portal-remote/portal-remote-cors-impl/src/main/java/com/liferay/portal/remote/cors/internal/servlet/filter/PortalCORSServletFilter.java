/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.internal.servlet.filter;

import com.liferay.oauth2.provider.scope.liferay.OAuth2ProviderScopeLiferayAccessControlContext;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.url.pattern.mapper.URLPatternMapper;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.remote.cors.internal.CORSSupport;
import com.liferay.portal.remote.cors.internal.configuration.persistence.listener.PortalCORSConfigurationModelListener;
import com.liferay.portal.remote.cors.internal.util.PortalCORSRegistryUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"before-filter=Upload Servlet Request Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Portal CORS Servlet Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class PortalCORSServletFilter extends BaseFilter {

	@Override
	public void init(FilterConfig filterConfig) {
		ServletContext servletContext = filterConfig.getServletContext();

		_contextPath = servletContext.getContextPath();
	}

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return CORSSupport.isCORSRequest(httpServletRequest::getHeader);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_serviceRegistration = bundleContext.registerService(
			ConfigurationModelListener.class,
			new PortalCORSConfigurationModelListener(
				PortalCORSRegistryUtil.getConfigurationPidsProperties()),
			new HashMapDictionary<>(
				HashMapBuilder.putAll(
					properties
				).put(
					"model.class.name",
					"com.liferay.portal.remote.cors.configuration." +
						"PortalCORSConfiguration"
				).build()));
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (companyId == CompanyConstants.SYSTEM) {
			return;
		}

		URLPatternMapper<CORSSupport> urlPatternMapper = _getURLPatternMapper(
			companyId);

		if (urlPatternMapper != null) {
			CORSSupport corsSupport = urlPatternMapper.getValue(
				_getURI(httpServletRequest));

			if (corsSupport != null) {
				if (StringUtil.equals(
						HttpMethods.OPTIONS, httpServletRequest.getMethod())) {

					if (corsSupport.isValidCORSPreflightRequest(
							httpServletRequest::getHeader)) {

						corsSupport.writeResponseHeaders(
							httpServletRequest::getHeader,
							httpServletResponse::setHeader);
					}

					return;
				}

				if (corsSupport.isValidCORSRequest(
						httpServletRequest.getMethod(),
						httpServletRequest::getHeader) &&
					(PropsValues.CORS_DISABLE_AUTHORIZATION_CONTEXT_CHECK ||
					 OAuth2ProviderScopeLiferayAccessControlContext.
						 isOAuth2AuthVerified() ||
					 _isGuest())) {

					corsSupport.writeResponseHeaders(
						httpServletRequest::getHeader,
						httpServletResponse::setHeader);
				}
			}
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	private String _getURI(HttpServletRequest httpServletRequest) {
		String uri = httpServletRequest.getRequestURI();

		if (Validator.isNotNull(_contextPath) &&
			!_contextPath.equals(StringPool.SLASH) &&
			uri.startsWith(_contextPath)) {

			uri = uri.substring(_contextPath.length());
		}

		return HttpComponentsUtil.normalizePath(uri);
	}

	private URLPatternMapper<CORSSupport> _getURLPatternMapper(long companyId) {
		URLPatternMapper<CORSSupport> urlPatternMapper =
			PortalCORSRegistryUtil.getUrlPatternMappers(companyId);

		if (urlPatternMapper != null) {
			return urlPatternMapper;
		}

		urlPatternMapper = PortalCORSRegistryUtil.getUrlPatternMappers(
			CompanyConstants.SYSTEM);

		if (urlPatternMapper != null) {
			return urlPatternMapper;
		}

		return null;
	}

	private boolean _isGuest() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			return true;
		}

		User user = permissionChecker.getUser();

		return user.isGuestUser();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalCORSServletFilter.class);

	private String _contextPath;

	@Reference
	private Portal _portal;

	private ServiceRegistration<ConfigurationModelListener>
		_serviceRegistration;

}