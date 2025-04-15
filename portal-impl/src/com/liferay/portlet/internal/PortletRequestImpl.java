/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.ccpp.PortalProfileFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletSession;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.PortletQNameUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ProtectedPrincipal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.NamespaceServletRequest;
import com.liferay.portal.servlet.SharedSessionServletRequest;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.PortletPreferencesWrapper;
import com.liferay.portlet.PublicRenderParametersPool;
import com.liferay.portlet.RenderParametersPool;
import com.liferay.portlet.UserInfoFactory;
import com.liferay.portlet.portletconfiguration.util.PublicRenderParameterConfiguration;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderParameters;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Sergey Ponomarev
 * @author Raymond Augé
 * @author Neil Griffin
 */
public abstract class PortletRequestImpl implements LiferayPortletRequest {

	@Override
	public void cleanUp() {
		_httpServletRequest.removeAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		_httpServletRequest.removeAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);
		_httpServletRequest.removeAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);
		_httpServletRequest.removeAttribute(PortletRequest.LIFECYCLE_PHASE);
		_httpServletRequest.removeAttribute(WebKeys.PORTLET_ID);
		_httpServletRequest.removeAttribute(WebKeys.PORTLET_CONTENT);
	}

	@Override
	public Map<String, String[]> clearRenderParameters() {
		return RenderParametersPool.clear(
			_httpServletRequest, _plid, _portletName);
	}

	@Override
	public void defineObjects(
		PortletConfig portletConfig, PortletResponse portletResponse) {

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)portletConfig;

		setAttribute(WebKeys.PORTLET_ID, liferayPortletConfig.getPortletId());

		setAttribute(JavaConstants.JAVAX_PORTLET_CONFIG, portletConfig);
		setAttribute(JavaConstants.JAVAX_PORTLET_REQUEST, this);
		setAttribute(JavaConstants.JAVAX_PORTLET_RESPONSE, portletResponse);
		setAttribute(PortletRequest.LIFECYCLE_PHASE, getLifecycle());
	}

	@Override
	public Object getAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (name.equals(PortletRequest.CCPP_PROFILE)) {
			return getCCPPProfile();
		}
		else if (name.equals(PortletRequest.USER_INFO)) {
			Object value = getUserInfo();

			if (value != null) {
				return value;
			}
		}

		return _httpServletRequest.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		Set<String> names = new HashSet<>();

		Enumeration<String> enumeration =
			_httpServletRequest.getAttributeNames();

		_copyAttributeNames(names, enumeration);

		if (_portletRequestDispatcherHttpServletRequest != null) {
			enumeration =
				_portletRequestDispatcherHttpServletRequest.getAttributeNames();

			_copyAttributeNames(names, enumeration);
		}

		return Collections.enumeration(names);
	}

	@Override
	public String getAuthType() {
		return _httpServletRequest.getAuthType();
	}

	public Object getCCPPProfile() {
		if (_profile == null) {
			PortalProfileFactory portalProfileFactory =
				_portalProfileFactorySnapshot.get();

			_profile = portalProfileFactory.getCCPPProfile(_httpServletRequest);
		}

		return _profile;
	}

	@Override
	public String getContextPath() {
		return _portlet.getContextPath();
	}

	@Override
	public Cookie[] getCookies() {
		return _httpServletRequest.getCookies();
	}

	public String getETag() {
		return null;
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	@Override
	public abstract String getLifecycle();

	@Override
	public Locale getLocale() {
		Locale locale = _locale;

		if (locale == null) {
			locale = _httpServletRequest.getLocale();
		}

		if (locale == null) {
			locale = LocaleUtil.getDefault();
		}

		return locale;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return _httpServletRequest.getLocales();
	}

	public String getMethod() {
		return _httpServletRequest.getMethod();
	}

	@Override
	public HttpServletRequest getOriginalHttpServletRequest() {
		return _originalHttpServletRequest;
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public String getParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (_portletRequestDispatcherHttpServletRequest != null) {
			return _portletRequestDispatcherHttpServletRequest.getParameter(
				name);
		}

		return _httpServletRequest.getParameter(name);
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public Map<String, String[]> getParameterMap() {
		if (_portletRequestDispatcherHttpServletRequest != null) {
			return Collections.unmodifiableMap(
				_portletRequestDispatcherHttpServletRequest.getParameterMap());
		}

		return Collections.unmodifiableMap(
			_httpServletRequest.getParameterMap());
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public Enumeration<String> getParameterNames() {
		if (_portletRequestDispatcherHttpServletRequest != null) {
			return _portletRequestDispatcherHttpServletRequest.
				getParameterNames();
		}

		return _httpServletRequest.getParameterNames();
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public String[] getParameterValues(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (_portletRequestDispatcherHttpServletRequest != null) {
			return _portletRequestDispatcherHttpServletRequest.
				getParameterValues(name);
		}

		return _httpServletRequest.getParameterValues(name);
	}

	@Override
	public long getPlid() {
		return _plid;
	}

	@Override
	public PortalContext getPortalContext() {
		return _portalContext;
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public PortletContext getPortletContext() {
		return _portletContext;
	}

	@Override
	public PortletMode getPortletMode() {
		return _portletMode;
	}

	@Override
	public String getPortletName() {
		return _portletName;
	}

	@Override
	public HttpServletRequest getPortletRequestDispatcherRequest() {
		return _portletRequestDispatcherHttpServletRequest;
	}

	@Override
	public PortletSession getPortletSession() {
		return _portletSessionImpl;
	}

	@Override
	public PortletSession getPortletSession(boolean create) {
		if (!create && !isRequestedSessionIdValid()) {
			return null;
		}

		return _portletSessionImpl;
	}

	@Override
	public PortletPreferences getPreferences() {
		String lifecycle = getLifecycle();

		if ((lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			 lifecycle.equals(PortletRequest.RENDER_PHASE)) &&
			PropsValues.PORTLET_PREFERENCES_STRICT_STORE) {

			return new PortletPreferencesWrapper(getPreferencesImpl());
		}

		return getPreferencesImpl();
	}

	public PortletPreferencesImpl getPreferencesImpl() {
		return (PortletPreferencesImpl)_portletPreferences;
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public Map<String, String[]> getPrivateParameterMap() {
		Map<String, String[]> parameterMap = null;

		if (_portletRequestDispatcherHttpServletRequest != null) {
			parameterMap =
				_portletRequestDispatcherHttpServletRequest.getParameterMap();
		}
		else {
			parameterMap = _httpServletRequest.getParameterMap();
		}

		Map<String, String[]> privateParameterMap = null;

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String name = entry.getKey();

			if (_portlet.getPublicRenderParameter(name) != null) {
				continue;
			}

			if (privateParameterMap == null) {
				privateParameterMap = new HashMap<>(parameterMap.size(), 1);
			}

			privateParameterMap.put(name, entry.getValue());
		}

		if (privateParameterMap == null) {
			return Collections.emptyMap();
		}

		return Collections.unmodifiableMap(privateParameterMap);
	}

	@Override
	public Enumeration<String> getProperties(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		List<String> values = new ArrayList<>();

		Enumeration<String> enumeration = _httpServletRequest.getHeaders(name);

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				String header = enumeration.nextElement();

				if (header != null) {
					values.add(header);
				}
			}
		}

		String value = _portalContext.getProperty(name);

		if (value != null) {
			values.add(value);
		}

		return Collections.enumeration(values);
	}

	@Override
	public String getProperty(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		String value = _httpServletRequest.getHeader(name);

		if (value == null) {
			value = _portalContext.getProperty(name);
		}

		return value;
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		List<String> names = new ArrayList<>();

		Enumeration<String> headerNamesEnumeration =
			_httpServletRequest.getHeaderNames();

		if (headerNamesEnumeration != null) {
			while (headerNamesEnumeration.hasMoreElements()) {
				names.add(headerNamesEnumeration.nextElement());
			}
		}

		Enumeration<String> propertyNamesEnumeration =
			_portalContext.getPropertyNames();

		while (propertyNamesEnumeration.hasMoreElements()) {
			names.add(propertyNamesEnumeration.nextElement());
		}

		return Collections.enumeration(names);
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public Map<String, String[]> getPublicParameterMap() {
		Map<String, String[]> parameterMap = null;

		if (_portletRequestDispatcherHttpServletRequest != null) {
			parameterMap =
				_portletRequestDispatcherHttpServletRequest.getParameterMap();
		}
		else {
			parameterMap = _httpServletRequest.getParameterMap();
		}

		Map<String, String[]> publicParameterMap = null;

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String name = entry.getKey();

			if (_portlet.getPublicRenderParameter(name) != null) {
				if (publicParameterMap == null) {
					publicParameterMap = new HashMap<>(parameterMap.size(), 1);
				}

				publicParameterMap.put(name, entry.getValue());
			}
		}

		if (publicParameterMap == null) {
			return Collections.emptyMap();
		}

		return Collections.unmodifiableMap(publicParameterMap);
	}

	@Override
	public String getRemoteUser() {
		return _remoteUser;
	}

	@Override
	public RenderParameters getRenderParameters() {
		if (_portletSpecMajorVersion < 3) {
			throw new UnsupportedOperationException("Requires 3.0 opt-in");
		}

		return _renderParameters;
	}

	@Override
	public String getRequestedSessionId() {
		if (_portletSessionImpl != null) {
			return _portletSessionImpl.getId();
		}

		HttpSession httpSession = _httpServletRequest.getSession(false);

		if (httpSession == null) {
			return StringPool.BLANK;
		}

		return httpSession.getId();
	}

	@Override
	public String getResponseContentType() {
		return ContentTypes.TEXT_HTML;
	}

	@Override
	public Enumeration<String> getResponseContentTypes() {
		return Collections.enumeration(
			ListUtil.fromArray(getResponseContentType()));
	}

	@Override
	public String getScheme() {
		return _httpServletRequest.getScheme();
	}

	@Override
	public String getServerName() {
		return _httpServletRequest.getServerName();
	}

	@Override
	public int getServerPort() {
		return _httpServletRequest.getServerPort();
	}

	@Override
	public String getUserAgent() {
		return _httpServletRequest.getHeader(HttpHeaders.USER_AGENT);
	}

	public LinkedHashMap<String, String> getUserInfo() {
		return UserInfoFactory.getUserInfo(_remoteUserId, _portlet);
	}

	@Override
	public Principal getUserPrincipal() {
		return _userPrincipal;
	}

	@Override
	public String getWindowID() {
		return StringBundler.concat(
			_portletName, LiferayPortletSession.LAYOUT_SEPARATOR, _plid);
	}

	@Override
	public WindowState getWindowState() {
		return _windowState;
	}

	public void init(
		HttpServletRequest httpServletRequest, Portlet portlet,
		InvokerPortlet invokerPortlet, PortletContext portletContext,
		WindowState windowState, PortletMode portletMode,
		PortletPreferences portletPreferences, long plid) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_portlet = portlet;
		_portletName = portlet.getPortletId();

		PortletApp portletApp = portlet.getPortletApp();

		_portletSpecMajorVersion = portletApp.getSpecMajorVersion();

		Map<String, String[]> publicRenderParametersMap =
			PublicRenderParametersPool.get(httpServletRequest, plid);

		String portletNamespace = PortalUtil.getPortletNamespace(_portletName);

		boolean warFile = portletApp.isWARFile();

		if (!warFile) {
			String portletResource = ParamUtil.getString(
				httpServletRequest, portletNamespace.concat("portletResource"));

			if (Validator.isNotNull(portletResource)) {
				Portlet resourcePortlet = null;

				try {
					resourcePortlet = PortletLocalServiceUtil.getPortletById(
						themeDisplay.getCompanyId(), portletResource);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}

				if (resourcePortlet != null) {
					PortletApp resourcePortletApp =
						resourcePortlet.getPortletApp();

					if (resourcePortletApp.isWARFile()) {
						warFile = true;
					}
				}
			}
		}

		if (warFile) {
			httpServletRequest = new SharedSessionServletRequest(
				httpServletRequest, !portlet.isPrivateSessionAttributes());
		}

		String dynamicQueryString = (String)httpServletRequest.getAttribute(
			DynamicServletRequest.DYNAMIC_QUERY_STRING);

		if (dynamicQueryString != null) {
			httpServletRequest.removeAttribute(
				DynamicServletRequest.DYNAMIC_QUERY_STRING);

			httpServletRequest = DynamicServletRequest.addQueryString(
				httpServletRequest, dynamicQueryString, true);
		}

		DynamicServletRequest dynamicServletRequest = null;

		if (portlet.isPrivateRequestAttributes()) {
			dynamicServletRequest = new NamespaceServletRequest(
				httpServletRequest, portletNamespace, portletNamespace, false);
		}
		else {
			dynamicServletRequest = new DynamicServletRequest(
				httpServletRequest, false);
		}

		boolean portletFocus = false;

		String ppid = ParamUtil.getString(httpServletRequest, "p_p_id");

		boolean windowStateRestoreCurrentView = ParamUtil.getBoolean(
			httpServletRequest, "p_p_state_rcv");

		if (_portletName.equals(ppid) &&
			!(windowStateRestoreCurrentView &&
			  portlet.isRestoreCurrentView())) {

			// Request was targeted to this portlet

			if (themeDisplay.isLifecycleRender() ||
				themeDisplay.isLifecycleResource()) {

				// Request was triggered by a render or resource URL

				portletFocus = true;
			}
			else if (themeDisplay.isLifecycleAction()) {
				_triggeredByActionURL = true;

				if (Objects.equals(
						getLifecycle(), PortletRequest.ACTION_PHASE)) {

					// Request was triggered by an action URL and is being
					// processed by
					// com.liferay.portlet.internal.ActionRequestImpl

					portletFocus = true;
				}
			}
		}

		boolean facesPortlet = false;

		if ((invokerPortlet != null) && invokerPortlet.isFacesPortlet()) {
			facesPortlet = true;
		}

		Set<String> privateRenderParameterNames = new LinkedHashSet<>();

		if (portletFocus) {
			Map<String, String[]> privateRenderParameters = null;

			Map<String, String[]> parameters =
				httpServletRequest.getParameterMap();

			for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
				RequestParameter requestParameter = new RequestParameter(
					entry.getKey(), entry.getValue(), portletNamespace,
					_portletSpecMajorVersion);

				if (requestParameter.isNameInvalid()) {
					continue;
				}

				if (Objects.equals(
						getLifecycle(), PortletRequest.HEADER_PHASE) ||
					Objects.equals(
						getLifecycle(), PortletRequest.RENDER_PHASE)) {

					if (privateRenderParameters == null) {
						privateRenderParameters = new HashMap<>();
					}

					privateRenderParameters.put(
						requestParameter.getName(facesPortlet),
						requestParameter.getValues());

					privateRenderParameterNames.add(requestParameter.getName());
				}
				else if (requestParameter.isPrivateRenderNamespaced()) {
					privateRenderParameterNames.add(requestParameter.getName());
				}

				if (requestParameter.getValues() == null) {
					continue;
				}

				if (requestParameter.isPortletNamespaced() ||
					!portlet.isRequiresNamespacedParameters()) {

					dynamicServletRequest.setParameterValues(
						requestParameter.getName(facesPortlet),
						requestParameter.getValues());
				}
			}

			if ((Objects.equals(getLifecycle(), PortletRequest.HEADER_PHASE) ||
				 Objects.equals(getLifecycle(), PortletRequest.RENDER_PHASE)) &&
				!LiferayWindowState.isExclusive(httpServletRequest) &&
				!LiferayWindowState.isPopUp(httpServletRequest)) {

				if ((privateRenderParameters == null) ||
					privateRenderParameters.isEmpty()) {

					RenderParametersPool.clear(
						httpServletRequest, plid, _portletName);
				}
				else {
					RenderParametersPool.put(
						httpServletRequest, plid, _portletName,
						privateRenderParameters);
				}
			}
		}
		else {
			Map<String, String[]> privateRenderParameters =
				RenderParametersPool.get(
					httpServletRequest, plid, _portletName);

			if (privateRenderParameters != null) {
				for (Map.Entry<String, String[]> entry :
						privateRenderParameters.entrySet()) {

					Parameter privateRenderParameter = new Parameter(
						entry.getKey(), entry.getValue(), portletNamespace);

					String publicRenderParameterName =
						PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE.concat(
							privateRenderParameter.getName());

					if (publicRenderParametersMap.containsKey(
							publicRenderParameterName)) {

						if (_portletSpecMajorVersion >= 3) {
							publicRenderParametersMap.put(
								publicRenderParameterName,
								privateRenderParameter.getValues());
						}
					}
					else {
						dynamicServletRequest.setParameterValues(
							privateRenderParameter.getName(facesPortlet),
							privateRenderParameter.getValues());
					}

					privateRenderParameterNames.add(
						privateRenderParameter.getName());
				}
			}
		}

		_mergePublicRenderParameters(
			dynamicServletRequest, publicRenderParametersMap,
			portletPreferences, getLifecycle());

		_processCheckbox(dynamicServletRequest);

		if (!isPortletModeAllowed(portletMode)) {
			portletMode = PortletModeFactory.getPortletMode(null, 3);

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unsupported portlet mode ", portletMode,
						" for portlet ", portlet.getPortletName()));
			}
		}

		_httpServletRequest = dynamicServletRequest;
		_originalHttpServletRequest = httpServletRequest;
		_portlet = portlet;
		_portalContext = new PortalContextImpl();
		_portletContext = portletContext;
		_windowState = windowState;
		_portletMode = portletMode;
		_portletPreferences = portletPreferences;
		_portletSessionImpl = new PortletSessionImpl(
			_httpServletRequest.getSession(), _portletContext, _portletName,
			plid);

		String userPrincipalStrategy = portlet.getUserPrincipalStrategy();

		if (userPrincipalStrategy.equals(
				PortletConstants.USER_PRINCIPAL_STRATEGY_SCREEN_NAME)) {

			try {
				User user = PortalUtil.getUser(httpServletRequest);

				if (user != null) {
					_remoteUser = user.getScreenName();
					_remoteUserId = user.getUserId();
					_userPrincipal = new ProtectedPrincipal(_remoteUser);
				}
			}
			catch (Exception exception) {
				_log.error("Unable to get user", exception);
			}
		}
		else {
			String remoteUser = httpServletRequest.getRemoteUser();
			long userId = PortalUtil.getUserId(httpServletRequest);

			if ((userId > 0) && (remoteUser == null)) {
				_remoteUser = String.valueOf(userId);
				_remoteUserId = userId;
				_userPrincipal = new ProtectedPrincipal(_remoteUser);
			}
			else {
				_remoteUser = remoteUser;
				_remoteUserId = GetterUtil.getLong(remoteUser);
				_userPrincipal = httpServletRequest.getUserPrincipal();
			}
		}

		_locale = themeDisplay.getLocale();
		_plid = plid;

		if (_portletSpecMajorVersion < 3) {
			return;
		}

		Set<String> publicRenderParameterNames = new HashSet<>();

		Set<PublicRenderParameter> publicRenderParameters =
			portlet.getPublicRenderParameters();

		for (PublicRenderParameter publicRenderParameter :
				publicRenderParameters) {

			publicRenderParameterNames.add(
				publicRenderParameter.getIdentifier());
		}

		Map<String, String[]> allRenderParameters = new LinkedHashMap<>();

		if (Objects.equals(getLifecycle(), PortletRequest.RESOURCE_PHASE)) {
			for (PublicRenderParameter publicRenderParameter :
					publicRenderParameters) {

				String[] values = publicRenderParametersMap.get(
					PortletQNameUtil.getPublicRenderParameterName(
						publicRenderParameter.getQName()));

				if (values != null) {
					allRenderParameters.put(
						publicRenderParameter.getIdentifier(), values);
				}
			}

			Map<String, String[]> privateRenderParameters =
				RenderParametersPool.get(
					httpServletRequest, plid, _portletName);

			if (privateRenderParameters != null) {
				for (Map.Entry<String, String[]> entry :
						privateRenderParameters.entrySet()) {

					String privateRenderParameterName = entry.getKey();

					if (allRenderParameters.containsKey(
							privateRenderParameterName)) {

						continue;
					}

					String[] values = entry.getValue();

					if (themeDisplay.isHubAction() ||
						themeDisplay.isHubPartialAction() ||
						themeDisplay.isHubResource()) {

						values = dynamicServletRequest.getParameterValues(
							privateRenderParameterName);
					}
					else {
						String[] requestValues =
							dynamicServletRequest.getParameterValues(
								privateRenderParameterName);

						if ((requestValues != null) &&
							!Arrays.equals(requestValues, values)) {

							dynamicServletRequest.setParameterValues(
								privateRenderParameterName,
								ArrayUtil.append(requestValues, values));
						}
					}

					allRenderParameters.put(privateRenderParameterName, values);
				}
			}

			for (String privateRenderParameterName :
					privateRenderParameterNames) {

				if (!allRenderParameters.containsKey(
						privateRenderParameterName)) {

					allRenderParameters.put(
						privateRenderParameterName,
						dynamicServletRequest.getParameterValues(
							privateRenderParameterName));
				}
			}
		}
		else {
			Map<String, String[]> parameterMap =
				dynamicServletRequest.getParameterMap();

			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				RequestParameter requestParameter = new RequestParameter(
					entry.getKey(), entry.getValue(), portletNamespace,
					_portletSpecMajorVersion);

				if (publicRenderParameterNames.contains(
						requestParameter.getName())) {

					if (_portletSpecMajorVersion >= 3) {
						String publicRenderParameterName =
							PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE;

						publicRenderParameterName =
							publicRenderParameterName.concat(
								requestParameter.getName());

						String[] previousValues = publicRenderParametersMap.get(
							publicRenderParameterName);

						if (previousValues != null) {
							requestParameter.setValues(previousValues);
						}
					}
				}
				else if (!privateRenderParameterNames.contains(
							requestParameter.getName())) {

					requestParameter.setValues(null);
				}

				if (requestParameter.getValues() != null) {
					allRenderParameters.put(
						requestParameter.getName(),
						requestParameter.getValues());
				}
			}
		}

		_renderParameters = new RenderParametersImpl(
			allRenderParameters, publicRenderParameterNames, portletNamespace);
	}

	@Override
	public void invalidateSession() {
		_invalidSession = true;
	}

	@Override
	public boolean isPortletModeAllowed(PortletMode portletMode) {
		if ((portletMode == null) || Validator.isNull(portletMode.toString())) {
			return true;
		}

		return _portlet.hasPortletMode(getResponseContentType(), portletMode);
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		if (_portletSessionImpl.isInvalidated() || _invalidSession) {
			return false;
		}

		return _httpServletRequest.isRequestedSessionIdValid();
	}

	@Override
	public boolean isSecure() {
		return _httpServletRequest.isSecure();
	}

	public boolean isTriggeredByActionURL() {
		return _triggeredByActionURL;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (_remoteUserId <= 0) {
			return false;
		}

		try {
			long companyId = PortalUtil.getCompanyId(_httpServletRequest);

			Map<String, String> roleMappersMap = _portlet.getRoleMappers();

			String roleLink = roleMappersMap.get(role);

			if (Validator.isNotNull(roleLink)) {
				return RoleLocalServiceUtil.hasUserRole(
					_remoteUserId, companyId, roleLink, true);
			}

			return RoleLocalServiceUtil.hasUserRole(
				_remoteUserId, companyId, role, true);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to check if a user is in role " + role, exception);
		}

		return _httpServletRequest.isUserInRole(role);
	}

	@Override
	public boolean isWindowStateAllowed(WindowState windowState) {
		return PortalContextImpl.isSupportedWindowState(windowState);
	}

	@Override
	public void removeAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		_httpServletRequest.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object object) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (object == null) {
			_httpServletRequest.removeAttribute(name);
		}
		else {
			_httpServletRequest.setAttribute(name, object);
		}
	}

	public void setPortletMode(PortletMode portletMode) {
		_portletMode = portletMode;
	}

	@Override
	public void setPortletRequestDispatcherRequest(
		HttpServletRequest httpServletRequest) {

		_portletRequestDispatcherHttpServletRequest = httpServletRequest;
	}

	public void setWindowState(WindowState windowState) {
		_windowState = windowState;
	}

	protected int getPortletSpecMajorVersion() {
		return _portletSpecMajorVersion;
	}

	private void _copyAttributeNames(
		Set<String> names, Enumeration<String> enumeration) {

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (!name.equals(JavaConstants.JAVAX_SERVLET_INCLUDE_PATH_INFO)) {
				names.add(name);
			}
		}
	}

	private void _mergePublicRenderParameters(
		DynamicServletRequest dynamicServletRequest,
		Map<String, String[]> publicRenderParametersMap,
		PortletPreferences portletPreferences, String lifecycle) {

		Set<PublicRenderParameter> publicRenderParameters =
			_portlet.getPublicRenderParameters();

		if (publicRenderParameters.isEmpty()) {
			return;
		}

		Enumeration<String> enumeration = portletPreferences.getNames();

		if (!enumeration.hasMoreElements()) {
			if (publicRenderParametersMap.isEmpty()) {
				return;
			}

			for (PublicRenderParameter publicRenderParameter :
					publicRenderParameters) {

				String[] values = publicRenderParametersMap.get(
					PortletQNameUtil.getPublicRenderParameterName(
						publicRenderParameter.getQName()));

				if (ArrayUtil.isEmpty(values) || Validator.isNull(values[0])) {
					continue;
				}

				String name = publicRenderParameter.getIdentifier();

				String[] requestValues =
					dynamicServletRequest.getParameterValues(name);

				if ((requestValues != null) &&
					(lifecycle.equals(PortletRequest.ACTION_PHASE) ||
					 lifecycle.equals(PortletRequest.RESOURCE_PHASE))) {

					dynamicServletRequest.setParameterValues(
						name, ArrayUtil.append(requestValues, values));
				}
				else {
					dynamicServletRequest.setParameterValues(name, values);
				}
			}

			return;
		}

		for (PublicRenderParameter publicRenderParameter :
				publicRenderParameters) {

			String publicRenderParameterName =
				PortletQNameUtil.getPublicRenderParameterName(
					publicRenderParameter.getQName());

			boolean ignoreValue = GetterUtil.getBoolean(
				portletPreferences.getValue(
					PublicRenderParameterConfiguration.getIgnoreKey(
						publicRenderParameterName),
					null));

			if (ignoreValue) {
				continue;
			}

			String mappingValue = GetterUtil.getString(
				portletPreferences.getValue(
					PublicRenderParameterConfiguration.getMappingKey(
						publicRenderParameterName),
					null));

			HttpServletRequest httpServletRequest =
				(HttpServletRequest)dynamicServletRequest.getRequest();

			String[] newValues = httpServletRequest.getParameterValues(
				mappingValue);

			if ((newValues != null) && (newValues.length != 0)) {
				newValues = ArrayUtil.remove(newValues, StringPool.NULL);
			}

			String name = publicRenderParameter.getIdentifier();

			if (ArrayUtil.isEmpty(newValues)) {
				String[] values = publicRenderParametersMap.get(
					publicRenderParameterName);

				if (ArrayUtil.isEmpty(values) || Validator.isNull(values[0])) {
					continue;
				}

				if (dynamicServletRequest.getParameter(name) == null) {
					dynamicServletRequest.setParameterValues(name, values);
				}
			}
			else {
				dynamicServletRequest.setParameterValues(name, newValues);
			}
		}
	}

	private void _processCheckbox(DynamicServletRequest dynamicServletRequest) {
		String checkboxNames = dynamicServletRequest.getParameter(
			"checkboxNames");

		if (Validator.isNull(checkboxNames)) {
			return;
		}

		for (String checkboxName : StringUtil.split(checkboxNames)) {
			String value = dynamicServletRequest.getParameter(checkboxName);

			if (value == null) {
				dynamicServletRequest.setParameter(
					checkboxName, Boolean.FALSE.toString());
			}
			else if (Objects.equals(value, "on")) {
				dynamicServletRequest.setParameter(
					checkboxName, Boolean.TRUE.toString());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletRequestImpl.class);

	private static final Snapshot<PortalProfileFactory>
		_portalProfileFactorySnapshot = new Snapshot<>(
			PortletRequestImpl.class, PortalProfileFactory.class);

	private HttpServletRequest _httpServletRequest;
	private boolean _invalidSession;
	private Locale _locale;
	private HttpServletRequest _originalHttpServletRequest;
	private long _plid;
	private PortalContext _portalContext;
	private Portlet _portlet;
	private PortletContext _portletContext;
	private PortletMode _portletMode;
	private String _portletName;
	private PortletPreferences _portletPreferences;
	private HttpServletRequest _portletRequestDispatcherHttpServletRequest;
	private PortletSessionImpl _portletSessionImpl;
	private int _portletSpecMajorVersion;
	private Object _profile;
	private String _remoteUser;
	private long _remoteUserId;
	private RenderParameters _renderParameters;
	private boolean _triggeredByActionURL;
	private Principal _userPrincipal;
	private WindowState _windowState;

	private static class Parameter {

		public String getName() {
			return _name;
		}

		public String getName(boolean includePortletNamespace) {
			if (includePortletNamespace && isPortletNamespaced()) {
				return _portletNamespacedName;
			}

			return _name;
		}

		public String[] getValues() {
			return _values;
		}

		public boolean isPortletNamespaced() {
			if (_portletNamespacedName != null) {
				return true;
			}

			return false;
		}

		private Parameter(
			String name, String[] values, String portletNamespace) {

			String portletNamespacedName = null;

			if ((name != null) && name.startsWith(portletNamespace)) {
				portletNamespacedName = name;

				name = name.substring(portletNamespace.length());
			}

			_name = name;

			_portletNamespacedName = portletNamespacedName;

			_values = values;
		}

		private final String _name;
		private final String _portletNamespacedName;
		private final String[] _values;

	}

	private static class RequestParameter extends Parameter {

		@Override
		public String[] getValues() {
			return _values;
		}

		public boolean isNameInvalid() {
			String name = getName();

			if (Validator.isNull(name) ||
				name.startsWith(
					PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE) ||
				name.startsWith(
					PortletQName.REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE) ||
				PortalUtil.isReservedParameter(name)) {

				return true;
			}

			return false;
		}

		public boolean isPrivateRenderNamespaced() {
			return _privateRenderNamespaced;
		}

		public void setValues(String[] values) {
			_values = values;
		}

		private static String _getName(String name) {
			if (name != null) {
				int pos = name.indexOf(
					PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE);

				if (pos >= 0) {
					int privateRenderParameterNamespaceLength =
						PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE.
							length();

					String privateRenderParameterName = name.substring(0, pos);

					privateRenderParameterName =
						privateRenderParameterName.concat(
							name.substring(
								pos + privateRenderParameterNamespaceLength));

					name = privateRenderParameterName;
				}
			}

			return name;
		}

		private RequestParameter(
			String name, String[] values, String portletNamespace,
			int portletSpecMajorVersion) {

			super(_getName(name), values, portletNamespace);

			if ((name != null) &&
				name.contains(
					PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE)) {

				_privateRenderNamespaced = true;
			}
			else {
				_privateRenderNamespaced = false;
			}

			if ((values != null) && (portletSpecMajorVersion >= 3)) {
				for (int i = 0; i < values.length; i++) {
					if (Objects.equals(
							values[i],
							LiferayMutablePortletParameters.NULL_PARAM_VALUE)) {

						values[i] = null;
					}
				}
			}

			_values = values;
		}

		private final boolean _privateRenderNamespaced;
		private String[] _values;

	}

}