/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.internal.servlet;

import com.liferay.friendly.url.configuration.FriendlyURLRedirectionConfiguration;
import com.liferay.friendly.url.configuration.FriendlyURLRedirectionConfigurationProvider;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.exception.LayoutPermissionException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.login.AuthLoginGroupSettingsUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LayoutFriendlyURLSeparatorComposite;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.InactiveRequestHandler;
import com.liferay.portal.kernel.servlet.PortalMessages;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.AsyncPortletServletRequest;
import com.liferay.portlet.documentlibrary.constants.DLFriendlyURLConstants;
import com.liferay.redirect.provider.RedirectProvider;
import com.liferay.redirect.tracker.RedirectNotFoundTracker;
import com.liferay.site.model.SiteFriendlyURL;
import com.liferay.site.service.SiteFriendlyURLLocalService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Shuyang Zhou
 * @author Marco Leo
 */
public class FriendlyURLServlet extends HttpServlet {

	public Redirect getRedirect(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path)
		throws PortalException {

		if (path.length() <= 1) {
			return new Redirect();
		}

		String groupFriendlyURL = path;

		int pos = path.indexOf(CharPool.SLASH, 1);

		if (pos != -1) {
			String friendlyURL = path.substring(pos);

			if (friendlyURL.startsWith(
					DLFriendlyURLConstants.PATH_PREFIX_DOCUMENT)) {

				String fileEntryFriendlyURL = friendlyURL.substring(
					DLFriendlyURLConstants.PATH_PREFIX_DOCUMENT.length() - 1);

				groupFriendlyURL = fileEntryFriendlyURL.substring(
					0, fileEntryFriendlyURL.indexOf(CharPool.SLASH, 1));
			}
			else {
				groupFriendlyURL = path.substring(0, pos);
			}
		}

		long companyId = PortalInstances.getCompanyId(httpServletRequest);

		Group group = _getGroup(path, groupFriendlyURL, companyId);

		Locale locale = portal.getLocale(httpServletRequest, null, false);

		SiteFriendlyURL alternativeSiteFriendlyURL =
			_getAlternativeSiteFriendlyURL(
				groupFriendlyURL, companyId, group, locale);

		String layoutFriendlyURL = null;
		Redirect redirectProviderRedirect = null;

		if ((pos != -1) && ((pos + 1) != path.length())) {
			layoutFriendlyURL = path.substring(pos);

			if (StringUtil.endsWith(layoutFriendlyURL, CharPool.SLASH)) {
				layoutFriendlyURL = layoutFriendlyURL.substring(
					0, layoutFriendlyURL.length() - 1);
			}

			redirectProviderRedirect = _getRedirectProviderRedirect(
				group.getGroupId(), httpServletRequest, layoutFriendlyURL);

			if ((redirectProviderRedirect != null) &&
				!_isSkipRedirect(httpServletRequest)) {

				return redirectProviderRedirect;
			}
		}
		else {
			httpServletRequest.setAttribute(
				WebKeys.REDIRECT_TO_DEFAULT_LAYOUT, Boolean.TRUE);
		}

		Map<String, Object> requestContext = HashMapBuilder.<String, Object>put(
			"request", httpServletRequest
		).build();

		ServiceContextThreadLocal.pushServiceContext(
			_getServiceContext(group, httpServletRequest));

		Layout defaultLayout = null;

		Map<String, String[]> params = httpServletRequest.getParameterMap();

		String actualURL = null;

		try {
			try {
				LayoutFriendlyURLSeparatorComposite
					layoutFriendlyURLSeparatorComposite =
						portal.getLayoutFriendlyURLSeparatorComposite(
							group.getGroupId(), _private, layoutFriendlyURL,
							params, requestContext);

				if (layoutFriendlyURLSeparatorComposite.isRedirect()) {
					pos = path.indexOf(
						layoutFriendlyURLSeparatorComposite.getURLSeparator());

					if (pos != 1) {
						String requestURL = portal.getCurrentCompleteURL(
							httpServletRequest);

						int friendlyURLPos = requestURL.indexOf(
							layoutFriendlyURL);

						String friendlyURL =
							layoutFriendlyURLSeparatorComposite.
								getFriendlyURL();

						String redirectURL = null;

						if (friendlyURLPos > 0) {
							redirectURL =
								requestURL.substring(0, friendlyURLPos) +
									friendlyURL;
						}
						else {
							redirectURL = StringBundler.concat(
								portal.getPathContext(),
								PropsValues.
									LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
								path.substring(0, pos), friendlyURL);
						}

						String queryString = HttpComponentsUtil.getQueryString(
							portal.getOriginalServletRequest(
								httpServletRequest));

						if (Validator.isNotNull(queryString)) {
							redirectURL += StringPool.QUESTION + queryString;
						}

						return new Redirect(
							redirectURL, true,
							_isPermanentRedirect(group.getCompanyId()));
					}
				}

				Layout layout = layoutFriendlyURLSeparatorComposite.getLayout();

				if (layout != null) {
					User user = _getUser(httpServletRequest);

					PermissionChecker permissionChecker =
						PermissionThreadLocal.getPermissionChecker(
							user, !user.isGuestUser());

					if (!LayoutPermissionUtil.contains(
							permissionChecker, layout, ActionKeys.VIEW)) {

						if (AuthLoginGroupSettingsUtil.isPromptEnabled(
								group.getGroupId())) {

							String redirect = portal.getLayoutActualURL(
								layout, Portal.PATH_MAIN);

							return new Redirect(redirect);
						}

						throw new LayoutPermissionException();
					}

					if (user.isGuestUser() && layout.isSystem() &&
						Objects.equals(
							layout.getFriendlyURL(),
							PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL)) {

						throw new NoSuchLayoutException();
					}

					if ((redirectProviderRedirect != null) &&
						!LayoutPermissionUtil.containsLayoutUpdatePermission(
							permissionChecker, layout)) {

						return redirectProviderRedirect;
					}
				}

				defaultLayout = layout;

				httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

				if (Objects.equals(
						httpServletRequest.getRequestURI(),
						PropsValues.LAYOUT_FRIENDLY_URL_PAGE_NOT_FOUND)) {

					httpServletRequest.removeAttribute(
						NoSuchLayoutException.class.getName());
				}

				String layoutFriendlyURLSeparatorCompositeFriendlyURL =
					layoutFriendlyURLSeparatorComposite.getFriendlyURL();

				if (Validator.isNull(
						layoutFriendlyURLSeparatorCompositeFriendlyURL)) {

					layoutFriendlyURLSeparatorCompositeFriendlyURL =
						layout.getFriendlyURL(locale);
				}

				pos = layoutFriendlyURLSeparatorCompositeFriendlyURL.indexOf(
					layoutFriendlyURLSeparatorComposite.getURLSeparator());

				if (pos != 0) {
					if (pos != -1) {
						layoutFriendlyURLSeparatorCompositeFriendlyURL =
							layoutFriendlyURLSeparatorCompositeFriendlyURL.
								substring(0, pos);
					}

					String i18nLanguageId =
						(String)httpServletRequest.getAttribute(
							WebKeys.I18N_LANGUAGE_ID);

					boolean localeUnavailable = false;

					if (Validator.isNotNull(i18nLanguageId) &&
						!LanguageUtil.isAvailableLocale(
							group.getGroupId(), i18nLanguageId) &&
						(!portal.isGroupControlPanelPath(path) ||
						 !LanguageUtil.isAvailableLocale(i18nLanguageId))) {

						localeUnavailable = true;
					}

					if (localeUnavailable ||
						(alternativeSiteFriendlyURL != null) ||
						!_equalsLayoutFriendlyURL(
							layoutFriendlyURLSeparatorCompositeFriendlyURL,
							layout, locale)) {

						Locale originalLocale =
							_setAlternativeLayoutFriendlyURL(
								companyId, httpServletRequest, layout,
								layoutFriendlyURLSeparatorCompositeFriendlyURL,
								alternativeSiteFriendlyURL);

						if (localeUnavailable &&
							PropsValues.LOCALE_USE_DEFAULT_IF_NOT_AVAILABLE) {

							locale = LocaleUtil.fromLanguageId(
								group.getDefaultLanguageId());
						}

						String redirect = _getLocalizedFriendlyURL(
							httpServletRequest, layout, locale, originalLocale);

						HttpServletRequest originalHttpServletRequest =
							portal.getOriginalServletRequest(
								httpServletRequest);

						if (redirect.equals(
								originalHttpServletRequest.getRequestURI())) {

							throw new NoSuchLayoutException();
						}

						boolean forcePermanentRedirect = true;

						if (Validator.isNull(i18nLanguageId)) {
							forcePermanentRedirect = _isPermanentRedirect(
								group.getCompanyId());
						}

						return new Redirect(
							redirect, true, forcePermanentRedirect);
					}
				}
			}
			catch (LayoutPermissionException | NoSuchLayoutException
						exception) {

				Layout redirectLayout = null;

				if (!(exception instanceof LayoutPermissionException)) {
					if (layoutFriendlyURL == null) {
						redirectLayout = defaultLayout;
					}
					else {
						redirectLayout = _getLayoutFriendlyURLLayout(
							group, layoutFriendlyURL, httpServletRequest);
					}
				}

				if (redirectLayout != null) {
					String redirect = portal.getLayoutActualURL(
						redirectLayout, Portal.PATH_MAIN);

					return new Redirect(redirect);
				}

				RedirectNotFoundTracker currentRedirectNotFoundTracker =
					_redirectNotFoundTrackerSnapshot.get();

				if (currentRedirectNotFoundTracker != null) {
					currentRedirectNotFoundTracker.trackURL(
						group, _normalizeFriendlyURL(layoutFriendlyURL));
				}

				if (Validator.isNotNull(
						PropsValues.LAYOUT_FRIENDLY_URL_PAGE_NOT_FOUND)) {

					if (exception instanceof NoSuchLayoutException) {
						throw exception;
					}

					throw new NoSuchLayoutException(exception);
				}

				httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

				httpServletRequest.setAttribute(
					NoSuchLayoutException.class.getName(), Boolean.TRUE);

				layoutFriendlyURL = null;
			}

			actualURL = portal.getActualURL(
				group.getGroupId(), _private, Portal.PATH_MAIN,
				layoutFriendlyURL, params, requestContext);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		String portalURL = portal.getPortalURL(httpServletRequest);

		if (actualURL.startsWith(portalURL)) {
			actualURL = StringUtil.removeSubstring(actualURL, portalURL);
		}

		long userId = portal.getUserId(httpServletRequest);

		boolean impersonated = _isImpersonated(httpServletRequest, userId);

		if ((userId > 0) && impersonated) {
			try {
				Company company = portal.getCompany(httpServletRequest);

				String encDoAsUserId = encryptor.encrypt(
					company.getKeyObj(), String.valueOf(userId));

				actualURL = HttpComponentsUtil.setParameter(
					actualURL, "doAsUserId", encDoAsUserId);

				params = new HashMap<>(params);

				params.remove("doAsUserId");
			}
			catch (EncryptorException encryptorException) {
				if (_log.isDebugEnabled()) {
					_log.debug(encryptorException);
				}

				return new Redirect(actualURL, false, false);
			}
		}

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		if ((layout != null) &&
			Objects.equals(layout.getType(), LayoutConstants.TYPE_URL)) {

			actualURL = actualURL.concat(
				HttpComponentsUtil.parameterMapToString(
					params, !actualURL.contains(StringPool.QUESTION)));
		}

		return new Redirect(
			actualURL, false,
			!impersonated && _isPermanentRedirect(group.getCompanyId()));
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		ServletContext servletContext = servletConfig.getServletContext();

		if (servletContext != ServletContextPool.get(
				portal.getServletContextName())) {

			return;
		}

		super.init(servletConfig);

		_private = GetterUtil.getBoolean(
			servletConfig.getInitParameter("servlet.init.private"));

		String proxyPath = portal.getPathProxy();

		_user = GetterUtil.getBoolean(
			servletConfig.getInitParameter("servlet.init.user"));

		if (_private) {
			if (_user) {
				_friendlyURLPathPrefix = portal.getPathFriendlyURLPrivateUser();
			}
			else {
				_friendlyURLPathPrefix =
					portal.getPathFriendlyURLPrivateGroup();
			}
		}
		else {
			_friendlyURLPathPrefix = portal.getPathFriendlyURLPublic();
		}

		_pathInfoOffset = _friendlyURLPathPrefix.length() - proxyPath.length();
	}

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		// Do not set the entire full main path. See LEP-456.

		String pathInfo = _getPathInfo(httpServletRequest);

		Redirect redirect = null;

		try {
			redirect = getRedirect(
				httpServletRequest, httpServletResponse, pathInfo);

			if (httpServletRequest.getAttribute(WebKeys.LAST_PATH) == null) {
				httpServletRequest.setAttribute(
					WebKeys.LAST_PATH,
					_getLastPath(httpServletRequest, pathInfo));
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			if (portalException instanceof NoSuchGroupException ||
				portalException instanceof NoSuchLayoutException) {

				portal.sendError(
					HttpServletResponse.SC_NOT_FOUND, portalException,
					httpServletRequest, httpServletResponse);

				return;
			}
		}

		if (redirect == null) {
			redirect = new Redirect();
		}

		if (redirect.isValidForward()) {
			ServletContext servletContext = getServletContext();

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(redirect.getPath());

			if (httpServletRequest.isAsyncSupported()) {
				AsyncPortletServletRequest asyncPortletServletRequest =
					AsyncPortletServletRequest.getAsyncPortletServletRequest(
						httpServletRequest);

				if (asyncPortletServletRequest != null) {
					asyncPortletServletRequest.update(
						servletContext.getContextPath(), redirect.getPath());
				}
			}

			if (requestDispatcher != null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Forward from ", httpServletRequest.getRequestURI(),
							" to ", redirect.getPath()));
				}

				requestDispatcher.forward(
					httpServletRequest, httpServletResponse);
			}
		}
		else {
			if (redirect.isPermanent()) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Location moved permanently from ",
							httpServletRequest.getRequestURI(), " to ",
							redirect.getPath()));
				}

				httpServletResponse.setHeader("Location", redirect.getPath());
				httpServletResponse.setStatus(
					HttpServletResponse.SC_MOVED_PERMANENTLY);
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Redirect from ",
							httpServletRequest.getRequestURI(), " to ",
							redirect.getPath()));
				}

				httpServletResponse.sendRedirect(redirect.getPath());
			}
		}
	}

	public static class Redirect {

		public Redirect() {
			this(Portal.PATH_MAIN);
		}

		public Redirect(String path) {
			this(path, false, false);
		}

		public Redirect(String path, boolean force, boolean permanent) {
			_path = path;
			_force = force;
			_permanent = permanent;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof Redirect)) {
				return false;
			}

			Redirect redirect = (Redirect)object;

			if (Objects.equals(getPath(), redirect.getPath()) &&
				(isForce() == redirect.isForce()) &&
				(isPermanent() == redirect.isPermanent())) {

				return true;
			}

			return false;
		}

		public String getPath() {
			if (Validator.isNull(_path)) {
				return Portal.PATH_MAIN;
			}

			return _path;
		}

		@Override
		public int hashCode() {
			int hash = HashUtil.hash(0, _path);

			hash = HashUtil.hash(hash, _force);
			hash = HashUtil.hash(hash, _permanent);

			return hash;
		}

		public boolean isForce() {
			return _force;
		}

		public boolean isPermanent() {
			return _permanent;
		}

		public boolean isValidForward() {
			if (isForce()) {
				return false;
			}

			String path = getPath();

			if (path.equals(Portal.PATH_MAIN) || path.startsWith("/c/")) {
				return true;
			}

			return false;
		}

		private final boolean _force;
		private final String _path;
		private final boolean _permanent;

	}

	@Reference
	protected Encryptor encryptor;

	@Reference
	protected FriendlyURLNormalizer friendlyURLNormalizer;

	@Reference
	protected FriendlyURLRedirectionConfigurationProvider
		friendlyURLRedirectionConfigurationProvider;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected InactiveRequestHandler inactiveRequestHandler;

	@Reference
	protected LayoutFriendlyURLLocalService layoutFriendlyURLLocalService;

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected LayoutService layoutService;

	@Reference
	protected Portal portal;

	@Reference
	protected SiteFriendlyURLLocalService siteFriendlyURLLocalService;

	@Reference
	protected UserLocalService userLocalService;

	private boolean _equalsLayoutFriendlyURL(
		String layoutFriendlyURLSeparatorCompositeFriendlyURL, Layout layout,
		Locale locale) {

		String layoutFriendlyURL = layout.getFriendlyURL(locale);

		if (StringUtil.equalsIgnoreCase(
				layoutFriendlyURLSeparatorCompositeFriendlyURL,
				layoutFriendlyURL) ||
			StringUtil.equalsIgnoreCase(
				friendlyURLNormalizer.normalizeWithEncoding(
					layoutFriendlyURLSeparatorCompositeFriendlyURL),
				layoutFriendlyURL)) {

			return true;
		}

		return false;
	}

	private SiteFriendlyURL _getAlternativeSiteFriendlyURL(
		String friendlyURL, long companyId, Group group, Locale locale) {

		SiteFriendlyURL alternativeSiteFriendlyURL = null;

		SiteFriendlyURL siteFriendlyURL =
			siteFriendlyURLLocalService.fetchSiteFriendlyURL(
				companyId, group.getGroupId(), LocaleUtil.toLanguageId(locale));

		if (siteFriendlyURL == null) {
			siteFriendlyURL =
				siteFriendlyURLLocalService.fetchSiteFriendlyURLByFriendlyURL(
					companyId, friendlyURL);
		}

		if ((siteFriendlyURL != null) &&
			!StringUtil.equalsIgnoreCase(
				siteFriendlyURL.getFriendlyURL(), friendlyURL)) {

			alternativeSiteFriendlyURL =
				siteFriendlyURLLocalService.fetchSiteFriendlyURLByFriendlyURL(
					siteFriendlyURL.getCompanyId(), friendlyURL);
		}

		return alternativeSiteFriendlyURL;
	}

	private String _getFriendlyURLRedirectionType(long companyId) {
		FriendlyURLRedirectionConfiguration
			friendlyURLRedirectionConfiguration =
				friendlyURLRedirectionConfigurationProvider.
					getCompanyFriendlyURLRedirectionConfiguration(companyId);

		return friendlyURLRedirectionConfiguration.redirectionType();
	}

	private Group _getGroup(String path, String friendlyURL, long companyId)
		throws NoSuchGroupException {

		Group group = groupLocalService.fetchFriendlyURLGroup(
			companyId, friendlyURL);

		if (group == null) {
			String screenName = friendlyURL.substring(1);

			User user = userLocalService.fetchUserByScreenName(
				companyId, screenName);

			if (user != null) {
				group = user.getGroup();
			}
			else if (_log.isWarnEnabled()) {
				_log.warn("No user exists with friendly URL " + screenName);
			}
		}

		if ((group == null) ||
			(!group.isActive() &&
			 !inactiveRequestHandler.isShowInactiveRequestMessage() &&
			 !path.startsWith(GroupConstants.CONTROL_PANEL_FRIENDLY_URL) &&
			 !path.startsWith(
				 friendlyURL +
					 VirtualLayoutConstants.CANONICAL_URL_SEPARATOR))) {

			throw new NoSuchGroupException(
				StringBundler.concat(
					"{companyId=", companyId, ", friendlyURL=", friendlyURL,
					"}"));
		}

		return group;
	}

	private LastPath _getLastPath(
		HttpServletRequest httpServletRequest, String pathInfo) {

		String lifecycle = ParamUtil.getString(
			httpServletRequest, "p_p_lifecycle");

		if (lifecycle.equals("1")) {
			return new LastPath(_friendlyURLPathPrefix, pathInfo);
		}

		return new LastPath(
			_friendlyURLPathPrefix, pathInfo,
			HttpComponentsUtil.parameterMapToString(
				httpServletRequest.getParameterMap()));
	}

	private Layout _getLayoutFriendlyURLLayout(
		Group group, String friendlyURL,
		HttpServletRequest httpServletRequest) {

		LayoutFriendlyURL layoutFriendlyURL =
			layoutFriendlyURLLocalService.fetchFirstLayoutFriendlyURL(
				group.getGroupId(), _private, friendlyURL);

		if (layoutFriendlyURL == null) {
			if (!group.isUser()) {
				return null;
			}

			List<Layout> layouts = layoutLocalService.getLayouts(
				group.getGroupId(), _private,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

			for (Layout layout : layouts) {
				if (layout.matches(httpServletRequest, friendlyURL)) {
					return layout;
				}
			}

			return null;
		}

		Layout layout = layoutLocalService.fetchLayout(
			layoutFriendlyURL.getPlid());

		if ((layout != null) && !layout.isSystem()) {
			return layout;
		}

		return null;
	}

	private String _getLocalizedFriendlyURL(
			HttpServletRequest httpServletRequest, Layout layout, Locale locale,
			Locale originalLocale)
		throws PortalException {

		String requestURI = _getRequestURI(httpServletRequest);

		int[] groupFriendlyURLIndex = portal.getGroupFriendlyURLIndex(
			requestURI);

		if (groupFriendlyURLIndex != null) {
			String originalRequestURI = null;

			if (HttpComponentsUtil.isForwarded(httpServletRequest)) {
				originalRequestURI = (String)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_SERVLET_FORWARD_REQUEST_URI);
			}
			else {
				originalRequestURI = _getRequestURI(
					portal.getOriginalServletRequest(httpServletRequest));
			}

			if (httpServletRequest.getAttribute(WebKeys.I18N_PATH) != null) {
				int pos = originalRequestURI.indexOf(StringPool.SLASH, 1);

				if (pos != -1) {
					originalRequestURI = originalRequestURI.substring(pos);
				}
			}

			if (portal.getGroupFriendlyURLIndex(originalRequestURI) == null) {
				requestURI = requestURI.substring(groupFriendlyURLIndex[1]);
			}
		}

		String layoutFriendlyURL = null;

		if (originalLocale == null) {
			String path = httpServletRequest.getPathInfo();

			int x = path.indexOf(CharPool.SLASH, 1);

			if ((x != -1) && ((x + 1) != path.length())) {
				layoutFriendlyURL = path.substring(x);
			}

			int y = layoutFriendlyURL.indexOf(
				VirtualLayoutConstants.CANONICAL_URL_SEPARATOR);

			if (y != -1) {
				y = layoutFriendlyURL.indexOf(CharPool.SLASH, 3);

				if ((y != -1) && ((y + 1) != layoutFriendlyURL.length())) {
					layoutFriendlyURL = layoutFriendlyURL.substring(y);
				}
			}

			y = layoutFriendlyURL.indexOf(Portal.FRIENDLY_URL_SEPARATOR);

			if (y != -1) {
				layoutFriendlyURL = layoutFriendlyURL.substring(0, y);
			}
		}
		else {
			layoutFriendlyURL = layout.getFriendlyURL(originalLocale);
		}

		if (requestURI.contains(layoutFriendlyURL)) {
			requestURI = StringUtil.replaceFirst(
				requestURI, layoutFriendlyURL, layout.getFriendlyURL(locale));
		}

		boolean appendI18nPath = true;

		int localePrependFriendlyURLStyle = PrefsPropsUtil.getInteger(
			portal.getCompanyId(httpServletRequest),
			PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);

		User user = _getUser(httpServletRequest);

		Locale userLocale = user.getLocale();

		if (!user.isGuestUser() && (localePrependFriendlyURLStyle == 3) &&
			locale.equals(userLocale)) {

			appendI18nPath = false;
		}
		else if ((localePrependFriendlyURLStyle == 0) ||
				 (((localePrependFriendlyURLStyle == 1) ||
				   (localePrependFriendlyURLStyle == 3)) &&
				  locale.equals(LocaleUtil.getDefault()))) {

			appendI18nPath = false;
		}

		String localizedFriendlyURL = portal.getPathContext();

		if (appendI18nPath) {
			String i18nPathLanguageId = portal.getI18nPathLanguageId(
				locale, LocaleUtil.toLanguageId(locale));

			String i18nPath = StringPool.SLASH + i18nPathLanguageId;

			localizedFriendlyURL += i18nPath;
		}

		localizedFriendlyURL += requestURI;

		String queryString = httpServletRequest.getQueryString();

		if (Validator.isNull(queryString)) {
			queryString = (String)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_SERVLET_FORWARD_QUERY_STRING);
		}

		if (Validator.isNotNull(queryString)) {
			localizedFriendlyURL += StringPool.QUESTION + queryString;
		}

		return localizedFriendlyURL;
	}

	private String _getPathInfo(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		int pos = requestURI.indexOf(Portal.JSESSIONID);

		if (pos == -1) {
			return requestURI.substring(_pathInfoOffset);
		}

		return requestURI.substring(_pathInfoOffset, pos);
	}

	private Redirect _getRedirectProviderRedirect(
		long groupId, HttpServletRequest httpServletRequest,
		String layoutFriendlyURL) {

		RedirectProvider redirectProvider = _redirectProviderSnapshot.get();

		if ((redirectProvider == null) ||
			LiferayWindowState.isExclusive(httpServletRequest) ||
			LiferayWindowState.isPopUp(httpServletRequest)) {

			return null;
		}

		HttpServletRequest originalHttpServletRequest =
			portal.getOriginalServletRequest(httpServletRequest);

		RedirectProvider.Redirect redirect = redirectProvider.getRedirect(
			groupId, _normalizeFriendlyURL(layoutFriendlyURL),
			_normalizeFriendlyURL(originalHttpServletRequest.getRequestURI()),
			httpServletRequest.getHeader(HttpHeaders.USER_AGENT));

		if (redirect == null) {
			return null;
		}

		return new Redirect(
			redirect.getDestinationURL(), true, redirect.isPermanent());
	}

	private String _getRequestURI(HttpServletRequest httpServletRequest) {
		String contextPath = portal.getPathContext();
		String requestURI = httpServletRequest.getRequestURI();

		if (Validator.isNotNull(contextPath) &&
			requestURI.startsWith(contextPath)) {

			requestURI = requestURI.substring(contextPath.length());
		}

		return StringUtil.replace(
			requestURI, StringPool.DOUBLE_SLASH, StringPool.SLASH);
	}

	private ServiceContext _getServiceContext(
			Group group, HttpServletRequest httpServletRequest)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = ServiceContextFactory.getInstance(
				httpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);
		}

		serviceContext = (ServiceContext)serviceContext.clone();

		serviceContext.setCompanyId(group.getCompanyId());
		serviceContext.setScopeGroupId(group.getGroupId());

		return serviceContext;
	}

	private User _getUser(HttpServletRequest httpServletRequest)
		throws PortalException {

		User user = portal.getUser(httpServletRequest);

		if (user == null) {
			user = userLocalService.getGuestUser(
				portal.getCompanyId(httpServletRequest));
		}

		return user;
	}

	private boolean _isImpersonated(
		HttpServletRequest httpServletRequest, long userId) {

		HttpSession httpSession = httpServletRequest.getSession();

		Long realUserId = (Long)httpSession.getAttribute(WebKeys.USER_ID);

		if ((realUserId == null) || (userId == realUserId)) {
			return false;
		}

		return true;
	}

	private boolean _isPermanentRedirect(long companyId) {
		return Objects.equals(
			_getFriendlyURLRedirectionType(companyId), "permanent");
	}

	private boolean _isShowAlternativeLayoutFriendlyURLMessage(long companyId) {
		FriendlyURLRedirectionConfiguration
			friendlyURLRedirectionConfiguration =
				friendlyURLRedirectionConfigurationProvider.
					getCompanyFriendlyURLRedirectionConfiguration(companyId);

		return friendlyURLRedirectionConfiguration.
			showAlternativeLayoutFriendlyURLMessage();
	}

	private boolean _isSkipRedirect(HttpServletRequest httpServletRequest) {
		String refererURL = httpServletRequest.getHeader(HttpHeaders.REFERER);

		if (Validator.isNotNull(refererURL)) {
			int index = refererURL.indexOf(CharPool.QUESTION);

			if (index != -1) {
				refererURL = refererURL.substring(0, index);
			}
		}

		if (Validator.isNotNull(refererURL)) {
			return refererURL.contains(
				VirtualLayoutConstants.CANONICAL_URL_SEPARATOR +
					GroupConstants.CONTROL_PANEL_FRIENDLY_URL);
		}

		return false;
	}

	private String _normalizeFriendlyURL(String friendlyURL) {
		if (Validator.isNull(friendlyURL)) {
			return friendlyURL;
		}

		String normalizedFriendlyURL =
			friendlyURLNormalizer.normalizeWithEncoding(
				HttpComponentsUtil.decodeURL(friendlyURL));

		if (normalizedFriendlyURL.startsWith(StringPool.SLASH)) {
			return normalizedFriendlyURL.substring(1);
		}

		return normalizedFriendlyURL;
	}

	private Locale _setAlternativeLayoutFriendlyURL(
			long companyId, HttpServletRequest httpServletRequest,
			Layout layout, String friendlyURL, SiteFriendlyURL siteFriendlyURL)
		throws PortalException {

		List<LayoutFriendlyURL> layoutFriendlyURLs =
			layoutFriendlyURLLocalService.getLayoutFriendlyURLs(
				layout.getPlid(), friendlyURL, 0, 1);

		if (layoutFriendlyURLs.isEmpty()) {
			return null;
		}

		LayoutFriendlyURL layoutFriendlyURL = layoutFriendlyURLs.get(0);

		Locale locale = LocaleUtil.fromLanguageId(
			layoutFriendlyURL.getLanguageId());

		if (!LanguageUtil.isAvailableLocale(layout.getGroupId(), locale)) {
			return LocaleUtil.fromLanguageId(
				(String)httpServletRequest.getAttribute(
					WebKeys.I18N_LANGUAGE_ID));
		}

		Locale groupLocale = locale;

		if (siteFriendlyURL != null) {
			groupLocale = LocaleUtil.fromLanguageId(
				siteFriendlyURL.getLanguageId());
		}

		String alternativeLayoutFriendlyURL = _getLocalizedFriendlyURL(
			httpServletRequest, layout, groupLocale, locale);

		if (_isShowAlternativeLayoutFriendlyURLMessage(companyId)) {
			SessionMessages.add(
				httpServletRequest, "alternativeLayoutFriendlyURL",
				alternativeLayoutFriendlyURL);

			PortalMessages.add(
				httpServletRequest, PortalMessages.KEY_JSP_PATH,
				"/html/common/themes/layout_friendly_url_redirect.jsp");
		}

		return groupLocale;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FriendlyURLServlet.class);

	private static final Snapshot<RedirectNotFoundTracker>
		_redirectNotFoundTrackerSnapshot = new Snapshot<>(
			FriendlyURLServlet.class, RedirectNotFoundTracker.class, null,
			true);
	private static final Snapshot<RedirectProvider> _redirectProviderSnapshot =
		new Snapshot<>(
			FriendlyURLServlet.class, RedirectProvider.class, null, true);

	private String _friendlyURLPathPrefix;
	private int _pathInfoOffset;
	private boolean _private;
	private boolean _user;

}