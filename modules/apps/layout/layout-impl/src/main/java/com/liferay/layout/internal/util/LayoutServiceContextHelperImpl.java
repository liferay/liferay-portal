/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.util;

import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ConcurrentHashMapBuilder;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = LayoutServiceContextHelper.class)
public class LayoutServiceContextHelperImpl
	implements LayoutServiceContextHelper {

	@Override
	public AutoCloseable getServiceContextAutoCloseable(Company company)
		throws PortalException {

		return new ServiceContextTemporarySwapper(company);
	}

	@Override
	public AutoCloseable getServiceContextAutoCloseable(
			Company company, User user)
		throws PortalException {

		return new ServiceContextTemporarySwapper(company, user);
	}

	@Override
	public AutoCloseable getServiceContextAutoCloseable(Layout layout)
		throws PortalException {

		return new ServiceContextTemporarySwapper(
			_companyLocalService.getCompany(layout.getCompanyId()), layout);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutServiceContextHelperImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ThemeLocalService _themeLocalService;

	@Reference
	private UserLocalService _userLocalService;

	private class ServiceContextTemporarySwapper implements AutoCloseable {

		public ServiceContextTemporarySwapper(Company company)
			throws PortalException {

			this(company, null, null);
		}

		public ServiceContextTemporarySwapper(Company company, Layout layout)
			throws PortalException {

			this(company, layout, null);
		}

		public ServiceContextTemporarySwapper(
				Company company, Layout layout, User user)
			throws PortalException {

			_company = company;

			_attributes = ConcurrentHashMapBuilder.<String, Object>put(
				WebKeys.CTX,
				ServletContextPool.get(_portal.getServletContextName())
			).build();

			_originalCompanyId = CompanyThreadLocal.getCompanyId();
			_originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();
			_originalName = PrincipalThreadLocal.getName();

			_originalServiceContext =
				ServiceContextThreadLocal.getServiceContext();

			if (_originalServiceContext == null) {
				_httpServletRequest = _createMockHttpServletRequest();
				_httpServletResponse = new DummyHttpServletResponse();
				_originalHttpServletRequest = null;
			}
			else {
				ThemeDisplay themeDisplay =
					_originalServiceContext.getThemeDisplay();

				if (_originalServiceContext.getRequest() != null) {
					_httpServletRequest = _originalServiceContext.getRequest();
					_originalHttpServletRequest =
						_originalServiceContext.getRequest();
				}
				else if ((themeDisplay != null) &&
						 (themeDisplay.getRequest() != null)) {

					_httpServletRequest = themeDisplay.getRequest();
					_originalHttpServletRequest = themeDisplay.getRequest();
				}
				else {
					_httpServletRequest = _createMockHttpServletRequest();
					_originalHttpServletRequest = null;
				}

				if (_originalServiceContext.getResponse() != null) {
					_httpServletResponse =
						_originalServiceContext.getResponse();
				}
				else if ((themeDisplay != null) &&
						 (themeDisplay.getResponse() != null)) {

					_httpServletResponse = themeDisplay.getResponse();
				}
				else {
					_httpServletResponse = new DummyHttpServletResponse();
				}
			}

			if (layout == null) {
				_group = _groupLocalService.getGroup(
					company.getCompanyId(), GroupConstants.GUEST);

				String friendlyURL =
					_friendlyURLNormalizer.normalizeWithEncoding(
						PropsValues.DEFAULT_GUEST_PUBLIC_LAYOUT_FRIENDLY_URL);

				layout = _layoutLocalService.fetchLayoutByFriendlyURL(
					_group.getGroupId(), false, friendlyURL);
			}
			else {
				_group = _groupLocalService.getGroup(layout.getGroupId());
			}

			if (layout == null) {
				layout = _layoutLocalService.fetchFirstLayout(
					_group.getGroupId(), false,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, false);
			}

			if (layout == null) {
				layout = _layoutLocalService.fetchFirstLayout(
					_group.getGroupId(), false,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);
			}

			_layout = layout;

			if (user == null) {
				_user = _userLocalService.fetchGuestUser(
					company.getCompanyId());
			}
			else {
				_user = user;
			}

			_permissionChecker = PermissionCheckerFactoryUtil.create(_user);

			_originalHttpServletRequestAttributesMap =
				_setHttpServletRequestAttributes(_permissionChecker, _user);

			_setCompanyServiceContext();
		}

		public ServiceContextTemporarySwapper(Company company, User user)
			throws PortalException {

			this(company, null, user);
		}

		@Override
		public void close() {
			CompanyThreadLocal.setCompanyId(_originalCompanyId);
			PermissionThreadLocal.setPermissionChecker(
				_originalPermissionChecker);
			PrincipalThreadLocal.setName(_originalName, false);
			ServiceContextThreadLocal.pushServiceContext(
				_originalServiceContext);

			if (_originalHttpServletRequest == null) {
				return;
			}

			for (Map.Entry<String, Object> entry :
					_originalHttpServletRequestAttributesMap.entrySet()) {

				_originalHttpServletRequest.setAttribute(
					entry.getKey(), entry.getValue());
			}
		}

		private HttpServletRequest _createMockHttpServletRequest() {
			return ProxyUtil.newDelegateProxyInstance(
				HttpServletRequest.class.getClassLoader(),
				HttpServletRequest.class,
				new Object() {

					public Object getAttribute(String name) {
						return _attributes.get(name);
					}

					public Enumeration<String> getAttributeNames() {
						return Collections.enumeration(_attributes.keySet());
					}

					public String getContextPath() {
						return _portal.getPathContext();
					}

					public Cookie[] getCookies() {
						return new Cookie[0];
					}

					public Enumeration<String> getHeaderNames() {
						return Collections.emptyEnumeration();
					}

					public String getMethod() {
						return HttpMethods.GET;
					}

					public Map<String, String[]> getParameterMap() {
						return Collections.emptyMap();
					}

					public String[] getParameterValues(String name) {
						return new String[0];
					}

					public RequestDispatcher getRequestDispatcher(String path) {
						return DirectRequestDispatcherFactoryUtil.
							getRequestDispatcher(
								ServletContextPool.get(
									_portal.getServletContextName()),
								path);
					}

					public String getRequestURI() {
						return StringPool.BLANK;
					}

					public ServletContext getServletContext() {
						return ServletContextPool.get(
							_portal.getServletContextName());
					}

					public HttpSession getSession() {
						return _httpSession;
					}

					public HttpSession getSession(boolean create) {
						return _httpSession;
					}

					public void removeAttribute(String name) {
						_attributes.remove(name);
					}

					public void setAttribute(String name, Object value) {
						_setAttribute(name, value);
					}

				},
				ProxyFactory.newDummyInstance(HttpServletRequest.class));
		}

		private ThemeDisplay _getThemeDisplay(
				Company company, PermissionChecker permissionChecker, User user)
			throws PortalException {

			ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

			themeDisplay.setCompany(company);

			if (_layout != null) {
				themeDisplay.setLanguageId(_layout.getDefaultLanguageId());
				themeDisplay.setLayout(_layout);

				LayoutSet layoutSet = _layout.getLayoutSet();

				themeDisplay.setLayoutSet(layoutSet);

				themeDisplay.setLayoutTypePortlet(
					(LayoutTypePortlet)_layout.getLayoutType());
				themeDisplay.setLocale(
					LocaleUtil.fromLanguageId(_layout.getDefaultLanguageId()));

				ColorScheme colorScheme = _layout.getColorScheme();

				Theme theme = _layout.getTheme();

				if (theme == null) {
					if (_log.isDebugEnabled()) {
						_log.debug(_layout.getThemeId() + " is not registered");
					}

					colorScheme = _themeLocalService.getColorScheme(
						company.getCompanyId(), layoutSet.getThemeId(),
						layoutSet.getColorSchemeId());
					theme = _themeLocalService.getTheme(
						company.getCompanyId(), layoutSet.getThemeId());
				}

				if (theme != null) {
					themeDisplay.setLookAndFeel(theme, colorScheme);
				}
				else if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to get theme for layout PLID " +
							_layout.getPlid());
				}

				themeDisplay.setPlid(_layout.getPlid());
			}
			else {
				Locale locale = _portal.getSiteDefaultLocale(
					_group.getGroupId());

				themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
				themeDisplay.setLocale(locale);
			}

			themeDisplay.setPermissionChecker(permissionChecker);
			themeDisplay.setPortalDomain(company.getVirtualHostname());

			boolean secure = _isHttpsEnabled();

			int portalServerPort = _portal.getPortalServerPort(secure);

			themeDisplay.setPortalURL(
				_portal.getPortalURL(
					company.getVirtualHostname(), portalServerPort, secure));

			themeDisplay.setRealUser(user);
			themeDisplay.setScopeGroupId(_group.getGroupId());
			themeDisplay.setServerName(company.getVirtualHostname());
			themeDisplay.setServerPort(portalServerPort);
			themeDisplay.setSiteGroupId(_group.getGroupId());
			themeDisplay.setTimeZone(user.getTimeZone());
			themeDisplay.setUser(user);

			return themeDisplay;
		}

		private boolean _isHttpsEnabled() {
			if (Objects.equals(
					Http.HTTPS,
					PropsUtil.get(PropsKeys.PORTAL_INSTANCE_PROTOCOL)) ||
				Objects.equals(
					Http.HTTPS, PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL))) {

				return true;
			}

			return false;
		}

		private void _setAttribute(String name, Object value) {
			if ((name != null) && (value != null)) {
				_attributes.put(name, value);
			}
			else if (name != null) {
				_attributes.remove(name);
			}
		}

		private void _setCompanyServiceContext() throws PortalException {
			CompanyThreadLocal.setCompanyId(_company.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(_permissionChecker);

			PrincipalThreadLocal.setName(_user.getUserId(), false);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(_company.getCompanyId());

			serviceContext.setRequest(_httpServletRequest);
			serviceContext.setScopeGroupId(_group.getGroupId());
			serviceContext.setUserId(_user.getUserId());

			ServiceContextThreadLocal.pushServiceContext(serviceContext);
		}

		private Map<String, Object> _setHttpServletRequestAttributes(
				PermissionChecker permissionChecker, User user)
			throws PortalException {

			Map<String, Object> attributes = HashMapBuilder.<String, Object>put(
				WebKeys.COMPANY_ID,
				_httpServletRequest.getAttribute(WebKeys.COMPANY_ID)
			).put(
				WebKeys.CTX, _httpServletRequest.getAttribute(WebKeys.CTX)
			).put(
				WebKeys.LAYOUT, _httpServletRequest.getAttribute(WebKeys.LAYOUT)
			).put(
				WebKeys.THEME_DISPLAY,
				_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
			).put(
				WebKeys.USER, _httpServletRequest.getAttribute(WebKeys.USER)
			).put(
				WebKeys.USER_ID,
				_httpServletRequest.getAttribute(WebKeys.USER_ID)
			).build();

			_httpServletRequest.setAttribute(
				WebKeys.COMPANY_ID, _company.getCompanyId());
			_httpServletRequest.setAttribute(
				WebKeys.CTX,
				ServletContextPool.get(_portal.getServletContextName()));

			ThemeDisplay themeDisplay = _getThemeDisplay(
				_company, permissionChecker, user);

			_httpServletRequest.setAttribute(
				WebKeys.LAYOUT, themeDisplay.getLayout());
			_httpServletRequest.setAttribute(
				WebKeys.LOCALE, themeDisplay.getLocale());
			_httpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			_httpServletRequest.setAttribute(WebKeys.USER, user);
			_httpServletRequest.setAttribute(
				WebKeys.USER_ID, _user.getUserId());

			themeDisplay.setRequest(_httpServletRequest);

			themeDisplay.setResponse(_httpServletResponse);

			return attributes;
		}

		private final Map<String, Object> _attributes;
		private final Company _company;
		private final Group _group;
		private final HttpServletRequest _httpServletRequest;
		private final HttpServletResponse _httpServletResponse;

		private final HttpSession _httpSession =
			ProxyUtil.newDelegateProxyInstance(
				HttpSession.class.getClassLoader(), HttpSession.class,
				new Object() {

					public Object getAttribute(String name) {
						return _attributes.get(name);
					}

					public Enumeration<String> getAttributeNames() {
						return Collections.enumeration(_attributes.keySet());
					}

					public String getId() {
						return StringPool.BLANK;
					}

					public String[] getValueNames() {
						return new String[0];
					}

					public boolean isNew() {
						return true;
					}

					public void setAttribute(String name, Object value) {
						_setAttribute(name, value);
					}

				},
				ProxyFactory.newDummyInstance(HttpSession.class));

		private final Layout _layout;
		private final long _originalCompanyId;
		private final HttpServletRequest _originalHttpServletRequest;
		private final Map<String, Object>
			_originalHttpServletRequestAttributesMap;
		private final String _originalName;
		private final PermissionChecker _originalPermissionChecker;
		private final ServiceContext _originalServiceContext;
		private final PermissionChecker _permissionChecker;
		private final User _user;

	}

}