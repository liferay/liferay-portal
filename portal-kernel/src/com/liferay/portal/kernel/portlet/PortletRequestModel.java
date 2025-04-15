/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.simple.Element;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletSession;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import java.io.Serializable;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author Shuyang Zhou
 */
public class PortletRequestModel implements Serializable {

	public PortletRequestModel(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		_portletRequest = portletRequest;
		_portletResponse = portletResponse;

		_contentType = portletRequest.getResponseContentType();
		_serverName = portletRequest.getServerName();
		_serverPort = portletRequest.getServerPort();
		_secure = portletRequest.isSecure();
		_authType = portletRequest.getAuthType();
		_remoteUser = portletRequest.getRemoteUser();
		_contextPath = portletRequest.getContextPath();
		_locale = portletRequest.getLocale();
		_portletMode = portletRequest.getPortletMode();
		_portletSessionId = portletRequest.getRequestedSessionId();
		_scheme = portletRequest.getScheme();
		_windowState = portletRequest.getWindowState();

		if (portletRequest instanceof ActionRequest) {
			_lifecycle = RenderRequest.ACTION_PHASE;
		}
		else if (portletRequest instanceof RenderRequest) {
			_lifecycle = RenderRequest.RENDER_PHASE;
		}
		else if (portletRequest instanceof ResourceRequest) {
			_lifecycle = RenderRequest.RESOURCE_PHASE;
		}
		else {
			_lifecycle = null;
		}

		if (_portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse)_portletResponse;

			_portletNamespace = mimeResponse.getNamespace();
		}
		else {
			_portletNamespace = null;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			_themeDisplayModel = new ThemeDisplayModel(themeDisplay);
		}
		else {
			_themeDisplayModel = null;
		}
	}

	public String getActionURL() {
		_initURLs();

		return _actionURL;
	}

	public Map<String, Object> getApplicationScopeSessionAttributes() {
		_initPortletSessionAttributes();

		return _applicationScopeSessionAttributes;
	}

	public Map<String, Object> getAttributes() {
		_initAttributes();

		return _attributes;
	}

	public String getAuthType() {
		return _authType;
	}

	public String getContainerNamespace() {
		return _contextPath;
	}

	public String getContentType() {
		return _contentType;
	}

	public String getContextPath() {
		return _contextPath;
	}

	public String getLifecycle() {
		return _lifecycle;
	}

	public Locale getLocale() {
		return _locale;
	}

	public Map<String, String[]> getParameters() {
		_initParameters();

		return _parameters;
	}

	public PortletMode getPortletMode() {
		return _portletMode;
	}

	public String getPortletNamespace() {
		return _portletNamespace;
	}

	public PortletRequest getPortletRequest() {
		return _portletRequest;
	}

	public PortletResponse getPortletResponse() {
		return _portletResponse;
	}

	public Map<String, Object> getPortletScopeSessioAttributes() {
		_initPortletSessionAttributes();

		return _portletScopeSessioAttributes;
	}

	public String getPortletSessionId() {
		return _portletSessionId;
	}

	public String getRemoteUser() {
		return _remoteUser;
	}

	public String getRenderURL() {
		_initURLs();

		return _renderURL;
	}

	public String getRenderURLExclusive() {
		_initURLs();

		return _renderURLExclusive;
	}

	public String getRenderURLMaximized() {
		_initURLs();

		return _renderURLMaximized;
	}

	public String getRenderURLMinimized() {
		_initURLs();

		return _renderURLMinimized;
	}

	public String getRenderURLNormal() {
		_initURLs();

		return _renderURLNormal;
	}

	public String getRenderURLPopUp() {
		_initURLs();

		return _renderURLPopUp;
	}

	public String getResourceURL() {
		_initURLs();

		return _resourceURL;
	}

	public String getScheme() {
		return _scheme;
	}

	public String getServerName() {
		return _serverName;
	}

	public int getServerPort() {
		return _serverPort;
	}

	public ThemeDisplayModel getThemeDisplayModel() {
		return _themeDisplayModel;
	}

	public WindowState getWindowState() {
		return _windowState;
	}

	public boolean isSecure() {
		return _secure;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> portletRequestModelMap =
			HashMapBuilder.<String, Object>put(
				"auth-type", _authType
			).put(
				"container-namespace", _contextPath
			).put(
				"container-type", "portlet"
			).put(
				"content-type", _contentType
			).put(
				"context-path", _contextPath
			).put(
				"lifecycle", _lifecycle
			).put(
				"locale", _locale
			).put(
				"portlet-mode", _portletMode
			).put(
				"portlet-session-id", _portletSessionId
			).put(
				"remote-user", _remoteUser
			).put(
				"scheme", _scheme
			).put(
				"secure", _secure
			).put(
				"server-name", _serverName
			).put(
				"server-port", _serverPort
			).put(
				"window-state", _windowState
			).build();

		if (_portletNamespace != null) {
			portletRequestModelMap.put("portlet-namespace", _portletNamespace);

			_initURLs();

			if (_actionURL != null) {
				portletRequestModelMap.put("action-url", "_actionURL");
			}

			if (_renderURL != null) {
				portletRequestModelMap.put("render-url", _renderURL);

				if (_renderURLExclusive != null) {
					portletRequestModelMap.put(
						"render-url-exclusive", _renderURLExclusive);
				}

				if (_renderURLMaximized != null) {
					portletRequestModelMap.put(
						"render-url-maximized", _renderURLMaximized);
				}

				if (_renderURLMinimized != null) {
					portletRequestModelMap.put(
						"render-url-minimized", _renderURLMinimized);
				}

				if (_renderURLNormal != null) {
					portletRequestModelMap.put(
						"render-url-normal", _renderURLNormal);
				}

				if (_renderURLPopUp != null) {
					portletRequestModelMap.put(
						"render-url-pop-up", _renderURLPopUp);
				}
			}

			portletRequestModelMap.put("resource-url", _resourceURL);
		}

		if (_themeDisplayModel != null) {
			Map<String, Object> themeDisplayModelMap = new HashMap<>();

			portletRequestModelMap.put("theme-display", themeDisplayModelMap);

			themeDisplayModelMap.put(
				"cdn-host", _themeDisplayModel.getCdnHost());
			themeDisplayModelMap.put(
				"company-id", _themeDisplayModel.getCompanyId());
			themeDisplayModelMap.put(
				"do-as-user-id", _themeDisplayModel.getDoAsUserId());
			themeDisplayModelMap.put(
				"i18n-language-id", _themeDisplayModel.getI18nLanguageId());
			themeDisplayModelMap.put(
				"i18n-path", _themeDisplayModel.getI18nPath());
			themeDisplayModelMap.put(
				"language-id", _themeDisplayModel.getLanguageId());
			themeDisplayModelMap.put("locale", _themeDisplayModel.getLocale());
			themeDisplayModelMap.put(
				"path-context", _themeDisplayModel.getPathContext());
			themeDisplayModelMap.put(
				"path-friendly-url-private-group",
				_themeDisplayModel.getPathFriendlyURLPrivateGroup());
			themeDisplayModelMap.put(
				"path-friendly-url-private-user",
				_themeDisplayModel.getPathFriendlyURLPrivateUser());
			themeDisplayModelMap.put(
				"path-friendly-url-public",
				_themeDisplayModel.getPathFriendlyURLPublic());
			themeDisplayModelMap.put(
				"path-image", _themeDisplayModel.getPathImage());
			themeDisplayModelMap.put(
				"path-main", _themeDisplayModel.getPathMain());
			themeDisplayModelMap.put(
				"path-theme-images", _themeDisplayModel.getPathThemeImages());
			themeDisplayModelMap.put("plid", _themeDisplayModel.getPlid());
			themeDisplayModelMap.put(
				"portal-url", _themeDisplayModel.getPortalURL());
			themeDisplayModelMap.put(
				"real-user-id", _themeDisplayModel.getRealUserId());
			themeDisplayModelMap.put(
				"scope-group-id", _themeDisplayModel.getScopeGroupId());
			themeDisplayModelMap.put("secure", _themeDisplayModel.isSecure());
			themeDisplayModelMap.put(
				"server-name", _themeDisplayModel.getServerName());
			themeDisplayModelMap.put(
				"server-port", _themeDisplayModel.getServerPort());

			TimeZone timeZone = _themeDisplayModel.getTimeZone();

			themeDisplayModelMap.put("time-zone", timeZone.getID());

			themeDisplayModelMap.put(
				"url-portal", _themeDisplayModel.getURLPortal());
			themeDisplayModelMap.put("user-id", _themeDisplayModel.getUserId());

			PortletDisplayModel portletDisplayModel =
				_themeDisplayModel.getPortletDisplayModel();

			if (portletDisplayModel != null) {
				Map<String, Object> portletDisplayModelMap = new HashMap<>();

				themeDisplayModelMap.put(
					"portlet-display", portletDisplayModelMap);

				portletDisplayModelMap.put("id", portletDisplayModel.getId());
				portletDisplayModelMap.put(
					"instance-id", portletDisplayModel.getInstanceId());
				portletDisplayModelMap.put(
					"portlet-name", portletDisplayModel.getPortletName());
				portletDisplayModelMap.put(
					"resource-pk", portletDisplayModel.getResourcePK());
				portletDisplayModelMap.put(
					"root-portlet-id", portletDisplayModel.getRootPortletId());
				portletDisplayModelMap.put(
					"title", portletDisplayModel.getTitle());
			}
		}

		portletRequestModelMap.put("parameters", getParameters());

		_attributes = filterInvalidAttributes(getAttributes());

		portletRequestModelMap.put("attributes", _attributes);

		Map<String, Object> portletSessionMap = new HashMap<>();

		portletRequestModelMap.put("portlet-session", portletSessionMap);

		_portletScopeSessioAttributes = filterInvalidAttributes(
			getPortletScopeSessioAttributes());

		portletSessionMap.put(
			"portlet-attributes", _portletScopeSessioAttributes);

		_applicationScopeSessionAttributes = filterInvalidAttributes(
			getApplicationScopeSessionAttributes());

		portletSessionMap.put(
			"application-attributes", _applicationScopeSessionAttributes);

		return portletRequestModelMap;
	}

	public String toXML() {
		Element requestElement = new Element("request");

		requestElement.addElement("container-type", "portlet");
		requestElement.addElement("container-namespace", _contextPath);
		requestElement.addElement("content-type", _contentType);
		requestElement.addElement("server-name", _serverName);
		requestElement.addElement("server-port", _serverPort);
		requestElement.addElement("secure", _secure);
		requestElement.addElement("auth-type", _authType);
		requestElement.addElement("remote-user", _remoteUser);
		requestElement.addElement("context-path", _contextPath);
		requestElement.addElement("locale", _locale);
		requestElement.addElement("portlet-mode", _portletMode);
		requestElement.addElement("portlet-session-id", _portletSessionId);
		requestElement.addElement("scheme", _scheme);
		requestElement.addElement("window-state", _windowState);
		requestElement.addElement("lifecycle", _lifecycle);

		if (_portletNamespace != null) {
			requestElement.addElement("portlet-namespace", _portletNamespace);

			_initURLs();

			if (_actionURL != null) {
				requestElement.addElement("action-url", _actionURL);
			}

			if (_renderURL != null) {
				requestElement.addElement("render-url", _renderURL);

				if (_renderURLExclusive != null) {
					requestElement.addElement(
						"render-url-exclusive", _renderURLExclusive);
				}

				if (_renderURLMaximized != null) {
					requestElement.addElement(
						"render-url-maximized", _renderURLMaximized);
				}

				if (_renderURLMinimized != null) {
					requestElement.addElement(
						"render-url-minimized", _renderURLMinimized);
				}

				if (_renderURLNormal != null) {
					requestElement.addElement(
						"render-url-normal", _renderURLNormal);
				}

				if (_renderURLPopUp != null) {
					requestElement.addElement(
						"render-url-pop-up", _renderURLPopUp);
				}
			}

			requestElement.addElement("resource-url", _resourceURL);
		}

		if (_themeDisplayModel != null) {
			Element themeDisplayElement = requestElement.addElement(
				"theme-display");

			themeDisplayElement.addElement(
				"cdn-host", _themeDisplayModel.getCdnHost());
			themeDisplayElement.addElement(
				"company-id", _themeDisplayModel.getCompanyId());
			themeDisplayElement.addElement(
				"do-as-user-id", _themeDisplayModel.getDoAsUserId());
			themeDisplayElement.addElement(
				"i18n-language-id", _themeDisplayModel.getI18nLanguageId());
			themeDisplayElement.addElement(
				"i18n-path", _themeDisplayModel.getI18nPath());
			themeDisplayElement.addElement(
				"language-id", _themeDisplayModel.getLanguageId());
			themeDisplayElement.addElement(
				"locale", _themeDisplayModel.getLocale());
			themeDisplayElement.addElement(
				"path-context", _themeDisplayModel.getPathContext());
			themeDisplayElement.addElement(
				"path-friendly-url-private-group",
				_themeDisplayModel.getPathFriendlyURLPrivateGroup());
			themeDisplayElement.addElement(
				"path-friendly-url-private-user",
				_themeDisplayModel.getPathFriendlyURLPrivateUser());
			themeDisplayElement.addElement(
				"path-friendly-url-public",
				_themeDisplayModel.getPathFriendlyURLPublic());
			themeDisplayElement.addElement(
				"path-image", _themeDisplayModel.getPathImage());
			themeDisplayElement.addElement(
				"path-main", _themeDisplayModel.getPathMain());
			themeDisplayElement.addElement(
				"path-theme-images", _themeDisplayModel.getPathThemeImages());
			themeDisplayElement.addElement(
				"plid", _themeDisplayModel.getPlid());
			themeDisplayElement.addElement(
				"portal-url", _themeDisplayModel.getPortalURL());
			themeDisplayElement.addElement(
				"real-user-id", _themeDisplayModel.getRealUserId());
			themeDisplayElement.addElement(
				"scope-group-id", _themeDisplayModel.getScopeGroupId());
			themeDisplayElement.addElement(
				"secure", _themeDisplayModel.isSecure());
			themeDisplayElement.addElement(
				"server-name", _themeDisplayModel.getServerName());
			themeDisplayElement.addElement(
				"server-port", _themeDisplayModel.getServerPort());

			TimeZone timeZone = _themeDisplayModel.getTimeZone();

			themeDisplayElement.addElement("time-zone", timeZone.getID());

			themeDisplayElement.addElement(
				"url-portal", _themeDisplayModel.getURLPortal());
			themeDisplayElement.addElement(
				"user-id", _themeDisplayModel.getUserId());

			PortletDisplayModel portletDisplayModel =
				_themeDisplayModel.getPortletDisplayModel();

			if (portletDisplayModel != null) {
				Element portletDisplayElement = themeDisplayElement.addElement(
					"portlet-display");

				portletDisplayElement.addElement(
					"id", portletDisplayModel.getId());
				portletDisplayElement.addElement(
					"instance-id", portletDisplayModel.getInstanceId());
				portletDisplayElement.addElement(
					"portlet-name", portletDisplayModel.getPortletName());
				portletDisplayElement.addElement(
					"resource-pk", portletDisplayModel.getResourcePK());
				portletDisplayElement.addElement(
					"root-portlet-id", portletDisplayModel.getRootPortletId());
				portletDisplayElement.addElement(
					"title", portletDisplayModel.getTitle());
			}
		}

		Element parametersElement = requestElement.addElement("parameters");

		Map<String, String[]> parameters = getParameters();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			Element parameterElement = parametersElement.addElement(
				"parameter");

			parameterElement.addElement("name", entry.getKey());

			for (String value : entry.getValue()) {
				parameterElement.addElement("value", value);
			}
		}

		Element attributesElement = requestElement.addElement("attributes");

		Map<String, Object> attributes = getAttributes();

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String name = entry.getKey();

			if (!_isValidAttributeName(name)) {
				continue;
			}

			Object value = entry.getValue();

			if (!_isValidAttributeValue(value)) {
				continue;
			}

			Element attributeElement = attributesElement.addElement(
				"attribute");

			attributeElement.addElement("name", name);
			attributeElement.addElement("value", value);
		}

		Element portletSessionElement = requestElement.addElement(
			"portlet-session");

		attributesElement = portletSessionElement.addElement(
			"portlet-attributes");

		Map<String, Object> portletScopeSessioAttributes =
			getPortletScopeSessioAttributes();

		for (Map.Entry<String, Object> entry :
				portletScopeSessioAttributes.entrySet()) {

			String name = entry.getKey();

			if (!_isValidAttributeName(name)) {
				continue;
			}

			Object value = entry.getValue();

			if (!_isValidAttributeValue(value)) {
				continue;
			}

			Element attributeElement = attributesElement.addElement(
				"attribute");

			attributeElement.addElement("name", name);
			attributeElement.addElement("value", value);
		}

		attributesElement = portletSessionElement.addElement(
			"application-attributes");

		Map<String, Object> applicationScopeSessionAttributes =
			getApplicationScopeSessionAttributes();

		for (Map.Entry<String, Object> entry :
				applicationScopeSessionAttributes.entrySet()) {

			String name = entry.getKey();

			if (!_isValidAttributeName(name)) {
				continue;
			}

			Object value = entry.getValue();

			if (!_isValidAttributeValue(value)) {
				continue;
			}

			Element attributeElement = attributesElement.addElement(
				"attribute");

			attributeElement.addElement("name", name);
			attributeElement.addElement("value", value);
		}

		return requestElement.toXMLString();
	}

	protected PortletRequestModel() {
		_authType = null;
		_contentType = null;
		_contextPath = null;
		_lifecycle = null;
		_locale = null;
		_portletMode = null;
		_portletNamespace = null;
		_portletRequest = null;
		_portletResponse = null;
		_portletSessionId = null;
		_remoteUser = null;
		_scheme = null;
		_secure = false;
		_serverName = null;
		_serverPort = 0;
		_themeDisplayModel = null;
		_windowState = null;
	}

	protected Map<String, Object> filterInvalidAttributes(
		Map<String, Object> map) {

		map = new HashMap<>(map);

		Set<Map.Entry<String, Object>> entrySet = map.entrySet();

		entrySet.removeIf(
			entry ->
				!_isValidAttributeName(entry.getKey()) ||
				!_isValidAttributeValue(entry.getValue()));

		return map;
	}

	private void _initAttributes() {
		if (_attributes != null) {
			return;
		}

		_attributes = new HashMap<>();

		Enumeration<String> enumeration = _portletRequest.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			Object value = _portletRequest.getAttribute(name);

			_attributes.put(name, value);
		}
	}

	private void _initParameters() {
		if (_parameters != null) {
			return;
		}

		_parameters = new HashMap<>(_portletRequest.getParameterMap());
	}

	private void _initPortletSessionAttributes() {
		if (_portletScopeSessioAttributes != null) {
			return;
		}

		PortletSession portletSession = _portletRequest.getPortletSession();

		try {
			_portletScopeSessioAttributes = portletSession.getAttributeMap(
				PortletSession.PORTLET_SCOPE);

			_applicationScopeSessionAttributes = portletSession.getAttributeMap(
				PortletSession.APPLICATION_SCOPE);
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isWarnEnabled()) {
				_log.warn(illegalStateException);
			}
		}
	}

	private void _initURLs() {
		if ((_resourceURL == null) &&
			(_portletResponse instanceof MimeResponse)) {

			MimeResponse mimeResponse = (MimeResponse)_portletResponse;

			try {
				PortletURL actionURL = mimeResponse.createActionURL();

				_actionURL = actionURL.toString();
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isWarnEnabled()) {
					_log.warn(illegalStateException);
				}
			}

			try {
				PortletURL renderURL = mimeResponse.createRenderURL();

				_renderURL = renderURL.toString();

				try {
					renderURL.setWindowState(LiferayWindowState.EXCLUSIVE);

					_renderURLExclusive = renderURL.toString();
				}
				catch (WindowStateException windowStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(windowStateException);
					}
				}

				try {
					renderURL.setWindowState(LiferayWindowState.MAXIMIZED);

					_renderURLMaximized = renderURL.toString();
				}
				catch (WindowStateException windowStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(windowStateException);
					}
				}

				try {
					renderURL.setWindowState(LiferayWindowState.MINIMIZED);

					_renderURLMinimized = renderURL.toString();
				}
				catch (WindowStateException windowStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(windowStateException);
					}
				}

				try {
					renderURL.setWindowState(LiferayWindowState.NORMAL);

					_renderURLNormal = renderURL.toString();
				}
				catch (WindowStateException windowStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(windowStateException);
					}
				}

				try {
					renderURL.setWindowState(LiferayWindowState.POP_UP);

					_renderURLPopUp = renderURL.toString();
				}
				catch (WindowStateException windowStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(windowStateException);
					}
				}
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isWarnEnabled()) {
					_log.warn(illegalStateException);
				}
			}

			ResourceURL resourceURL = mimeResponse.createResourceURL();

			String resourceURLString = HttpComponentsUtil.removeParameter(
				resourceURL.toString(), _portletNamespace + "struts_action");

			resourceURLString = HttpComponentsUtil.removeParameter(
				resourceURLString, _portletNamespace + "redirect");

			_resourceURL = resourceURLString;
		}
	}

	private boolean _isValidAttributeName(String name) {
		if (StringUtil.equalsIgnoreCase(
				name, WebKeys.PORTLET_RENDER_PARAMETERS) ||
			StringUtil.equalsIgnoreCase(name, "j_password") ||
			StringUtil.equalsIgnoreCase(name, "LAYOUT_CONTENT") ||
			StringUtil.equalsIgnoreCase(name, "LAYOUTS") ||
			StringUtil.equalsIgnoreCase(name, "USER_PASSWORD") ||
			name.startsWith("javax.") || name.startsWith("liferay-ui:")) {

			return false;
		}

		return true;
	}

	private boolean _isValidAttributeValue(Object object) {
		if (object == null) {
			return false;
		}
		else if (object instanceof Collection<?>) {
			Collection<?> col = (Collection<?>)object;

			return !col.isEmpty();
		}
		else if (object instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>)object;

			return !map.isEmpty();
		}

		String objString = String.valueOf(object);

		if (Validator.isNull(objString)) {
			return false;
		}

		String hashCode = StringPool.AT.concat(
			StringUtil.toHexString(object.hashCode()));

		return !objString.endsWith(hashCode);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletRequestModel.class);

	private String _actionURL;
	private Map<String, Object> _applicationScopeSessionAttributes;
	private Map<String, Object> _attributes;
	private final String _authType;
	private final String _contentType;
	private final String _contextPath;
	private final String _lifecycle;
	private final Locale _locale;
	private Map<String, String[]> _parameters;
	private final PortletMode _portletMode;
	private final String _portletNamespace;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;
	private Map<String, Object> _portletScopeSessioAttributes;
	private final String _portletSessionId;
	private final String _remoteUser;
	private String _renderURL;
	private String _renderURLExclusive;
	private String _renderURLMaximized;
	private String _renderURLMinimized;
	private String _renderURLNormal;
	private String _renderURLPopUp;
	private String _resourceURL;
	private final String _scheme;
	private final boolean _secure;
	private final String _serverName;
	private final int _serverPort;
	private final ThemeDisplayModel _themeDisplayModel;
	private final WindowState _windowState;

}