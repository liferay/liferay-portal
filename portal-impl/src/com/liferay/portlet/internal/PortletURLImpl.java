/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletURLListener;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.PortletQNameUtil;
import com.liferay.portal.kernel.portlet.WindowStateFactory;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletURLListenerFactory;
import com.liferay.portlet.PublicRenderParametersPool;
import com.liferay.portlet.RenderParametersPool;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.MutableActionParameters;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.MutableResourceParameters;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSecurityException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.PortletURLGenerationListener;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;
import jakarta.portlet.annotations.PortletSerializable;
import jakarta.portlet.annotations.RenderStateScoped;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import java.security.Key;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Connor McKay
 * @author Neil Griffin
 */
public class PortletURLImpl
	implements LiferayPortletURL, PortletURL, ResourceURL, Serializable {

	public PortletURLImpl(
		HttpServletRequest httpServletRequest, Portlet portlet, Layout layout,
		String lifecycle, MimeResponse.Copy copy) {

		this(httpServletRequest, portlet, null, layout, lifecycle, copy);
	}

	public PortletURLImpl(
		PortletRequest portletRequest, Portlet portlet, Layout layout,
		String lifecycle, MimeResponse.Copy copy) {

		this(
			PortalUtil.getHttpServletRequest(portletRequest), portlet,
			portletRequest, layout, lifecycle, copy);
	}

	@Override
	public void addParameterIncludedInPath(String name) {
		if (_parametersIncludedInPath.isEmpty()) {
			_parametersIncludedInPath = new LinkedHashSet<>();
		}

		_parametersIncludedInPath.add(name);
	}

	@Override
	public void addProperty(String key, String value) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Appendable append(Appendable appendable) throws IOException {
		return append(appendable, true);
	}

	@Override
	public Appendable append(Appendable appendable, boolean escapeXml)
		throws IOException {

		String toString = toString();

		if (escapeXml && !_escapeXml) {
			toString = HtmlUtil.escape(toString);
		}

		return appendable.append(toString);
	}

	public MutableActionParameters getActionParameters() {
		if (_portletSpecMajorVersion < 3) {
			throw new UnsupportedOperationException("Requires 3.0 opt-in");
		}

		return _mutableActionParametersImpl;
	}

	@Override
	public String getCacheability() {
		return _cacheability;
	}

	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	public Layout getLayout() {
		if (_layout == null) {
			try {
				Layout layout = (Layout)_httpServletRequest.getAttribute(
					WebKeys.LAYOUT);

				if ((layout != null) && (layout.getPlid() == _plid)) {
					_layout = layout;
				}
				else if (_plid > 0) {
					_layout = LayoutLocalServiceUtil.getLayout(_plid);
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Layout cannot be found for " + _plid, exception);
				}
			}
		}

		return _layout;
	}

	public String getLayoutFriendlyURL() {
		return _layoutFriendlyURL;
	}

	@Override
	public String getLifecycle() {
		return _lifecycle;
	}

	public String getNamespace() {
		if (_namespace == null) {
			_namespace = PortalUtil.getPortletNamespace(
				_portlet.getPortletId());
		}

		return _namespace;
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public String getParameter(String name) {
		String[] values = _portletURLParameterMap.get(name);

		if (ArrayUtil.isNotEmpty(values)) {
			return values[0];
		}

		return null;
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public Map<String, String[]> getParameterMap() {
		return _portletURLParameterMap;
	}

	@Override
	public Set<String> getParametersIncludedInPath() {
		return _parametersIncludedInPath;
	}

	@Override
	public long getPlid() {
		return _plid;
	}

	public Portlet getPortlet() {
		return _portlet;
	}

	public String getPortletFriendlyURLPath() {
		String portletFriendlyURLPath = null;

		if (_portlet.isUndeployedPortlet()) {
			return portletFriendlyURLPath;
		}

		FriendlyURLMapper friendlyURLMapper =
			_portlet.getFriendlyURLMapperInstance();

		if (friendlyURLMapper != null) {
			portletFriendlyURLPath = friendlyURLMapper.buildPath(this);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Portlet friendly URL path " + portletFriendlyURLPath);
			}
		}

		return portletFriendlyURLPath;
	}

	@Override
	public String getPortletId() {
		return _portlet.getPortletId();
	}

	@Override
	public PortletMode getPortletMode() {
		if (_portletModeString == null) {
			return null;
		}

		return PortletModeFactory.getPortletMode(_portletModeString);
	}

	public PortletRequest getPortletRequest() {
		return _portletRequest;
	}

	@Override
	public Set<String> getRemovedParameterNames() {
		return _removedParameterNames;
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		if (_portletSpecMajorVersion < 3) {
			throw new UnsupportedOperationException("Requires 3.0 opt-in");
		}

		return _mutableRenderParametersImpl;
	}

	@Override
	public String getResourceID() {
		return _resourceID;
	}

	@Override
	public MutableResourceParameters getResourceParameters() {
		if (_portletSpecMajorVersion < 3) {
			throw new UnsupportedOperationException("Requires 3.0 opt-in");
		}

		return _mutableResourceParametersImpl;
	}

	@Override
	public WindowState getWindowState() {
		if (_windowStateString == null) {
			return null;
		}

		return WindowStateFactory.getWindowState(_windowStateString);
	}

	@Override
	public boolean isAnchor() {
		return _anchor;
	}

	@Override
	public boolean isCopyCurrentRenderParameters() {
		return _copyCurrentRenderParameters;
	}

	@Override
	public boolean isEncrypt() {
		return _encrypt;
	}

	@Override
	public boolean isEscapeXml() {
		return _escapeXml;
	}

	@Override
	public boolean isParameterIncludedInPath(String name) {
		if (_parametersIncludedInPath.contains(name)) {
			return true;
		}

		if (name.startsWith(PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE)) {
			name = name.substring(
				PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE.length());

			if (_parametersIncludedInPath.contains(name)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isSecure() {
		return _secure;
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public void removePublicRenderParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (_portlet.isUndeployedPortlet()) {
			return;
		}

		PublicRenderParameter publicRenderParameter =
			_portlet.getPublicRenderParameter(name);

		if (publicRenderParameter == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Public parameter " + name + "does not exist");
			}

			return;
		}

		_removePublicRenderParameters.add(
			PortletQNameUtil.getRemovePublicRenderParameterName(
				publicRenderParameter.getQName()));
	}

	@Override
	public void setAnchor(boolean anchor) {
		_anchor = anchor;

		clearCache();
	}

	@Override
	public void setBeanParameter(PortletSerializable portletSerializable) {
		if (_portletSpecMajorVersion < 3) {
			throw new UnsupportedOperationException("Requires 3.0 opt-in");
		}

		if (portletSerializable == null) {
			throw new IllegalArgumentException();
		}

		Class<? extends PortletSerializable> portletSerializableClass =
			portletSerializable.getClass();

		RenderStateScoped renderStateScoped =
			portletSerializableClass.getAnnotation(RenderStateScoped.class);

		if (renderStateScoped == null) {
			throw new IllegalArgumentException(
				"Class not annotated with @RenderStateScoped");
		}

		String paramName = renderStateScoped.paramName();

		if (Validator.isNull(paramName)) {
			paramName = portletSerializableClass.getSimpleName();

			if (paramName.endsWith("$$EnhancerBySpringCGLIB")) {

				// If RenderURL#setBeanParameter(PortletSerializable) is called
				// from within a bean portlet that uses Spring for IoC, and if
				// Spring dynamically created the bean at runtime using CGLib,
				// then the parameter name will have "$$EnhanderBySpringCGLib"
				// as a suffix.

				paramName = paramName.substring(
					0, paramName.length() - "$$EnhancerBySpringCGLIB".length());
			}
			else if (paramName.endsWith("$Proxy$_$$_WeldClientProxy")) {

				// Otherwise, if using CDI, then the parameter name will have
				// "$Proxy$_$$_WeldClientProxy" as a suffix.

				paramName = paramName.substring(
					0,
					paramName.length() - "$Proxy$_$$_WeldClientProxy".length());
			}
		}

		MutableRenderParameters mutableRenderParameters = getRenderParameters();

		mutableRenderParameters.setValues(
			paramName, portletSerializable.serialize());
	}

	@Override
	public void setCacheability(String cacheability) {
		if (cacheability == null) {
			throw new IllegalArgumentException("Cacheability is null");
		}

		String mappedCacheability = _cacheabilities.getOrDefault(
			cacheability, cacheability);

		if (!mappedCacheability.equals(FULL) &&
			!mappedCacheability.equals(PAGE) &&
			!mappedCacheability.equals(PORTLET)) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"Cacheability ", cacheability, " is not FULL, ", FULL,
					", PAGE, ", PAGE, ", or PORTLET, ", PORTLET));
		}

		if (_portletRequest instanceof ResourceRequest) {
			ResourceRequest resourceRequest = (ResourceRequest)_portletRequest;

			String parentCacheability = resourceRequest.getCacheability();

			if (parentCacheability.equals(FULL)) {
				if (!mappedCacheability.equals(FULL)) {
					throw new IllegalStateException(
						"Unable to set a weaker cacheability " + cacheability);
				}
			}
			else if (parentCacheability.equals(PORTLET)) {
				if (!mappedCacheability.equals(FULL) &&
					!mappedCacheability.equals(PORTLET)) {

					throw new IllegalStateException(
						"Unable to set a weaker cacheability " + cacheability);
				}
			}
		}

		_cacheability = mappedCacheability;

		clearCache();
	}

	@Override
	public void setCopyCurrentRenderParameters(
		boolean copyCurrentRenderParameters) {

		if (_portletSpecMajorVersion < 3) {
			_copyCurrentRenderParameters = copyCurrentRenderParameters;

			return;
		}

		boolean changed = false;

		if (_copyCurrentRenderParameters != copyCurrentRenderParameters) {
			changed = true;
		}

		_copyCurrentRenderParameters = copyCurrentRenderParameters;

		if (changed) {
			_initMutableRenderParameters();
		}
	}

	@Override
	public void setDoAsGroupId(long doAsGroupId) {
		_doAsGroupId = doAsGroupId;

		clearCache();
	}

	@Override
	public void setDoAsUserId(long doAsUserId) {
		_doAsUserId = doAsUserId;

		clearCache();
	}

	@Override
	public void setDoAsUserLanguageId(String doAsUserLanguageId) {
		_doAsUserLanguageId = doAsUserLanguageId;

		clearCache();
	}

	@Override
	public void setEncrypt(boolean encrypt) {
		_encrypt = encrypt;

		clearCache();
	}

	@Override
	public void setEscapeXml(boolean escapeXml) {
		_escapeXml = escapeXml;

		clearCache();
	}

	@Override
	public void setLifecycle(String lifecycle) {
		_lifecycle = lifecycle;

		clearCache();
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public void setParameter(String name, String value) {
		setParameter(name, value, PropsValues.PORTLET_URL_APPEND_PARAMETERS);
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public void setParameter(String name, String... values) {
		setParameter(name, values, PropsValues.PORTLET_URL_APPEND_PARAMETERS);
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public void setParameter(String name, String value, boolean append) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (_portletSpecMajorVersion < 3) {
			if (value == null) {
				if (_portletURLParameterMap.containsKey(name)) {
					_portletURLParameterMap.remove(name);
				}

				return;
			}

			setParameter(name, new String[] {value}, append);

			return;
		}

		LiferayMutablePortletParameters liferayMutablePortletParameters =
			_getMutablePortletParameters(name);

		if (name.startsWith(PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE)) {
			name = name.substring(
				PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE.length());
		}

		if (value == null) {
			liferayMutablePortletParameters.removeParameter(name);
		}

		liferayMutablePortletParameters.setValue(name, value, append);
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public void setParameter(String name, String[] values, boolean append) {
		if ((name == null) ||
			((values == null) && (_portletSpecMajorVersion < 3))) {

			throw new IllegalArgumentException();
		}

		LiferayMutablePortletParameters liferayMutablePortletParameters = null;

		if (_portletSpecMajorVersion >= 3) {
			liferayMutablePortletParameters = _getMutablePortletParameters(
				name);

			if (name.startsWith(
					PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE)) {

				name = name.substring(
					PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE.length());
			}

			if (values == null) {
				liferayMutablePortletParameters.removeParameter(name);

				return;
			}
		}

		for (String value : values) {
			if (value == null) {
				throw new IllegalArgumentException();
			}
		}

		if (_portletSpecMajorVersion >= 3) {
			liferayMutablePortletParameters.setValues(name, values, append);

			return;
		}

		if (!append) {
			_portletURLParameterMap.put(name, values);
		}
		else {
			String[] oldValues = _portletURLParameterMap.get(name);

			if (oldValues == null) {
				_portletURLParameterMap.put(name, values);
			}
			else {
				String[] newValues = ArrayUtil.append(oldValues, values);

				_portletURLParameterMap.put(name, newValues);
			}
		}

		clearCache();
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public void setParameters(Map<String, String[]> params) {
		if (params == null) {
			throw new IllegalArgumentException();
		}

		Map<String, String[]> newParams = new LinkedHashMap<>();

		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			try {
				String key = entry.getKey();

				if (key == null) {
					throw new IllegalArgumentException();
				}

				String[] value = entry.getValue();

				if (value == null) {
					throw new IllegalArgumentException();
				}

				newParams.put(key, value);
			}
			catch (ClassCastException classCastException) {
				throw new IllegalArgumentException(classCastException);
			}
		}

		if (_portletSpecMajorVersion >= 3) {
			_mutableRenderParametersImpl.clear();

			if (_mutableActionParametersImpl != null) {
				_mutableActionParametersImpl.clear();
			}

			if (_mutableResourceParametersImpl != null) {
				_mutableResourceParametersImpl.clear();
			}

			for (Map.Entry<String, String[]> entry : newParams.entrySet()) {
				setParameter(entry.getKey(), entry.getValue());
			}
		}
		else {
			_portletURLParameterMap = newParams;
		}

		clearCache();
	}

	@Override
	public void setPlid(long plid) {
		_plid = plid;

		clearCache();
	}

	@Override
	public void setPortletId(String portletId) {
		_portlet = PortletLocalServiceUtil.getPortletById(
			PortalUtil.getCompanyId(_httpServletRequest), portletId);

		clearCache();
	}

	@Override
	public void setPortletMode(PortletMode portletMode)
		throws PortletModeException {

		if ((_portletRequest != null) && !_portlet.isUndeployedPortlet() &&
			!_portlet.hasPortletMode(
				_portletRequest.getResponseContentType(), portletMode)) {

			throw new PortletModeException(portletMode.toString(), portletMode);
		}

		_portletModeString = portletMode.toString();

		clearCache();
	}

	public void setPortletMode(String portletMode) throws PortletModeException {
		setPortletMode(PortletModeFactory.getPortletMode(portletMode));
	}

	@Override
	public void setProperty(String key, String value) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void setRefererGroupId(long refererGroupId) {
		_refererGroupId = refererGroupId;

		clearCache();
	}

	@Override
	public void setRefererPlid(long refererPlid) {
		_refererPlid = refererPlid;

		clearCache();
	}

	@Override
	public void setRemovedParameterNames(Set<String> removedParameterNames) {
		_removedParameterNames = removedParameterNames;

		clearCache();
	}

	@Override
	public void setResourceID(String resourceID) {
		_resourceID = resourceID;

		clearCache();
	}

	@Override
	public void setSecure(boolean secure) throws PortletSecurityException {
		_secure = secure;

		clearCache();
	}

	public void setWindowState(String windowState) throws WindowStateException {
		setWindowState(WindowStateFactory.getWindowState(windowState));
	}

	@Override
	public void setWindowState(WindowState windowState)
		throws WindowStateException {

		if ((_portletRequest != null) &&
			!_portletRequest.isWindowStateAllowed(windowState)) {

			throw new WindowStateException(windowState.toString(), windowState);
		}

		if (LiferayWindowState.isWindowStatePreserved(
				getWindowState(), windowState)) {

			_windowStateString = windowState.toString();
		}

		clearCache();
	}

	@Override
	public void setWindowStateRestoreCurrentView(
		boolean windowStateRestoreCurrentView) {

		_windowStateRestoreCurrentView = windowStateRestoreCurrentView;
	}

	@Override
	public String toString() {
		if (_portletSpecMajorVersion < 3) {
			if (_toString != null) {
				return _toString;
			}
		}
		else {
			LiferayMutablePortletParameters mutableActionParameters =
				(LiferayMutablePortletParameters)_mutableActionParametersImpl;

			LiferayMutablePortletParameters mutableResourceParameters =
				(LiferayMutablePortletParameters)_mutableResourceParametersImpl;

			if (!_mutableRenderParametersImpl.isMutated() &&
				(mutableActionParameters != null) &&
				!mutableActionParameters.isMutated() &&
				(mutableResourceParameters != null) &&
				!mutableResourceParameters.isMutated() && (_toString != null)) {

				return _toString;
			}
		}

		_callPortletURLGenerationListener();

		_toString = generateToString();

		return _toString;
	}

	@Override
	public void visitReservedParameters(BiConsumer<String, String> biConsumer) {
		biConsumer.accept("p_p_id", _portlet.getPortletId());

		if (_lifecycle.equals(PortletRequest.ACTION_PHASE)) {
			biConsumer.accept("p_p_lifecycle", "1");
		}
		else if (_lifecycle.equals(PortletRequest.RENDER_PHASE)) {
			biConsumer.accept("p_p_lifecycle", "0");
		}
		else if (_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			biConsumer.accept("p_p_lifecycle", "2");
		}

		if ((_windowStateString != null) &&
			!_cacheability.equals(ResourceURL.FULL)) {

			biConsumer.accept("p_p_state", _windowStateString);
		}

		if (_windowStateRestoreCurrentView) {
			biConsumer.accept("p_p_state_rcv", "1");
		}

		if ((_portletModeString != null) &&
			!_cacheability.equals(ResourceURL.FULL)) {

			biConsumer.accept("p_p_mode", _portletModeString);
		}

		if (_resourceID != null) {
			biConsumer.accept("p_p_resource_id", _resourceID);
		}

		if (_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			biConsumer.accept("p_p_cacheability", _cacheability);
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		write(writer, _escapeXml);
	}

	@Override
	public void write(Writer writer, boolean escapeXml) throws IOException {
		String toString = toString();

		if (escapeXml && !_escapeXml) {
			toString = HtmlUtil.escape(toString);
		}

		writer.write(toString);
	}

	protected void clearCache() {
		_toString = null;
	}

	protected String generateToString() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to generate string because theme display is null");
			}

			return StringPool.BLANK;
		}

		try {
			if (_layoutFriendlyURL == null) {
				Layout layout = getLayout();

				if (layout != null) {
					_layoutFriendlyURL = GetterUtil.getString(
						PortalUtil.getLayoutFriendlyURL(layout, themeDisplay));

					if (_secure) {
						_layoutFriendlyURL = HttpComponentsUtil.protocolize(
							_layoutFriendlyURL,
							PropsValues.WEB_SERVER_HTTPS_PORT, true);
					}
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		StringBundler sb = new StringBundler(64);

		Key key = _getKey();

		if (Validator.isNull(_layoutFriendlyURL)) {
			sb.append(PortalUtil.getPortalURL(_httpServletRequest, _secure));
			sb.append(themeDisplay.getPathMain());
			sb.append("/portal/layout?p_l_id=");
			sb.append(processValue(key, _plid));
			sb.append(StringPool.AMPERSAND);
		}
		else {

			// A virtual host URL will contain the complete path. Do not
			// append the portal URL if the virtual host URL starts with
			// "http://" or "https://".

			if (!_layoutFriendlyURL.startsWith(Http.HTTP_WITH_SLASH) &&
				!_layoutFriendlyURL.startsWith(Http.HTTPS_WITH_SLASH)) {

				sb.append(
					PortalUtil.getPortalURL(_httpServletRequest, _secure));
			}

			sb.append(_layoutFriendlyURL);

			String friendlyURLPath = getPortletFriendlyURLPath();

			if (Validator.isNotNull(friendlyURLPath)) {
				sb.append("/-");
				sb.append(friendlyURLPath);
			}

			sb.append(StringPool.QUESTION);
		}

		AuthTokenUtil.addCSRFToken(_httpServletRequest, this);
		AuthTokenUtil.addPortletInvocationToken(_httpServletRequest, this);

		visitReservedParameters(
			(name, value) -> {
				if (!isParameterIncludedInPath(name)) {
					sb.append(name);
					sb.append(StringPool.EQUAL);
					sb.append(processValue(key, value));
					sb.append(StringPool.AMPERSAND);
				}
			});

		if (_doAsUserId > 0) {
			sb.append("doAsUserId=");
			sb.append(processValue(key, _doAsUserId));
			sb.append(StringPool.AMPERSAND);
		}
		else {
			String doAsUserId = themeDisplay.getDoAsUserId();

			if (Validator.isNotNull(doAsUserId)) {
				sb.append("doAsUserId=");
				sb.append(processValue(key, doAsUserId));
				sb.append(StringPool.AMPERSAND);
			}
		}

		String doAsUserLanguageId = _doAsUserLanguageId;

		if (Validator.isNull(doAsUserLanguageId)) {
			doAsUserLanguageId = themeDisplay.getDoAsUserLanguageId();
		}

		if (Validator.isNotNull(doAsUserLanguageId)) {
			sb.append("doAsUserLanguageId=");
			sb.append(processValue(key, doAsUserLanguageId));
			sb.append(StringPool.AMPERSAND);
		}

		long doAsGroupId = _doAsGroupId;

		if (doAsGroupId <= 0) {
			doAsGroupId = themeDisplay.getDoAsGroupId();
		}

		if (doAsGroupId > 0) {
			sb.append("doAsGroupId=");
			sb.append(processValue(key, doAsGroupId));
			sb.append(StringPool.AMPERSAND);
		}

		long refererGroupId = _refererGroupId;

		if (refererGroupId <= 0) {
			refererGroupId = themeDisplay.getRefererGroupId();
		}

		if (refererGroupId > 0) {
			sb.append("refererGroupId=");
			sb.append(processValue(key, refererGroupId));
			sb.append(StringPool.AMPERSAND);
		}

		long refererPlid = _refererPlid;

		if (refererPlid <= 0) {
			refererPlid = themeDisplay.getRefererPlid();
		}

		if (refererPlid > 0) {
			sb.append("refererPlid=");
			sb.append(processValue(key, refererPlid));
			sb.append(StringPool.AMPERSAND);
		}

		if (!_removePublicRenderParameters.isEmpty()) {
			String lastString = sb.stringAt(sb.index() - 1);

			if (lastString.charAt(lastString.length() - 1) !=
					CharPool.AMPERSAND) {

				sb.append(StringPool.AMPERSAND);
			}

			for (String removedPublicParameter :
					_removePublicRenderParameters) {

				sb.append(URLCodec.encodeURL(removedPublicParameter));
				sb.append(StringPool.EQUAL);
				sb.append(StringPool.AMPERSAND);
			}
		}

		Object layoutAssetEntry = _httpServletRequest.getAttribute(
			WebKeys.LAYOUT_ASSET_ENTRY);

		if (layoutAssetEntry instanceof AssetEntry) {
			AssetEntry assetEntry = (AssetEntry)layoutAssetEntry;

			if ((_layout != null) && _layout.isTypeAssetDisplay() &&
				(assetEntry != null)) {

				sb.append("assetEntryId=");
				sb.append(assetEntry.getEntryId());
				sb.append(StringPool.AMPERSAND);
			}
		}

		Map<String, String[]> portletURLParams = _portletURLParameterMap;

		if (_portletSpecMajorVersion < 3) {
			if (_copyCurrentRenderParameters &&
				!(_lifecycle.equals(PortletRequest.RESOURCE_PHASE) &&
				  _cacheability.equals(ResourceURL.FULL))) {

				portletURLParams = _mergeWithRenderParametersV2(
					portletURLParams);
			}
		}
		else {
			portletURLParams = _combineAllParametersV3();
		}

		for (Map.Entry<String, String[]> entry : portletURLParams.entrySet()) {
			String[] values = entry.getValue();

			if (values == null) {
				continue;
			}

			String name = entry.getKey();

			if (!_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
				String publicRenderParameterName = getPublicRenderParameterName(
					name);

				if (Validator.isNotNull(publicRenderParameterName)) {
					name = publicRenderParameterName;
				}
			}

			if (name.startsWith(_ACTION_PARAMETER_NAMESPACE)) {
				name = name.substring(_ACTION_PARAMETER_NAMESPACE.length());
			}
			else if (name.startsWith(_RESOURCE_PARAMETER_NAMESPACE)) {
				name = name.substring(_RESOURCE_PARAMETER_NAMESPACE.length());
			}

			if (isParameterIncludedInPath(name)) {
				continue;
			}

			for (String value : values) {
				_appendNamespaceAndEncode(sb, name);

				sb.append(StringPool.EQUAL);

				if (value == null) {
					sb.append(
						processValue(
							key,
							LiferayMutablePortletParameters.NULL_PARAM_VALUE));
				}
				else {
					sb.append(processValue(key, value));
				}

				sb.append(StringPool.AMPERSAND);
			}
		}

		if (_encrypt) {
			sb.append(WebKeys.ENCRYPT);
			sb.append("=1");
		}
		else {
			sb.setIndex(sb.index() - 1);
		}

		String result = sb.toString();

		if (!CookiesManagerUtil.hasSessionId(_httpServletRequest)) {
			HttpSession httpSession = _httpServletRequest.getSession();

			result = PortalUtil.getURLWithSessionId(
				result, httpSession.getId());
		}

		if (!_escapeXml) {
			result = HttpComponentsUtil.shortenURL(result);
		}

		if (PropsValues.PORTLET_URL_ANCHOR_ENABLE && _anchor &&
			(_windowStateString != null) &&
			!_windowStateString.equals(WindowState.MAXIMIZED.toString()) &&
			!_windowStateString.equals(
				LiferayWindowState.EXCLUSIVE.toString()) &&
			!_windowStateString.equals(LiferayWindowState.POP_UP.toString())) {

			sb.setIndex(0);

			sb.append(result);
			sb.append("#p_");
			sb.append(URLCodec.encodeURL(_portlet.getPortletId()));

			result = sb.toString();
		}

		if (_escapeXml) {
			result = HtmlUtil.escape(result);

			result = HttpComponentsUtil.shortenURL(result);
		}

		return result;
	}

	protected String getPublicRenderParameterName(String name) {
		String publicRenderParameterName = null;

		if (!_portlet.isUndeployedPortlet()) {
			PublicRenderParameter publicRenderParameter =
				_portlet.getPublicRenderParameter(name);

			if (publicRenderParameter != null) {
				publicRenderParameterName =
					PortletQNameUtil.getPublicRenderParameterName(
						publicRenderParameter.getQName());
			}
		}

		return publicRenderParameterName;
	}

	protected String processValue(Key key, long value) {
		return processValue(key, String.valueOf(value));
	}

	protected String processValue(Key key, String value) {
		if (key == null) {
			return URLCodec.encodeURL(value);
		}

		try {
			return URLCodec.encodeURL(EncryptorUtil.encrypt(key, value));
		}
		catch (EncryptorException encryptorException) {
			if (_log.isDebugEnabled()) {
				_log.debug(encryptorException);
			}

			return value;
		}
	}

	private PortletURLImpl(
		HttpServletRequest httpServletRequest, Portlet portlet,
		PortletRequest portletRequest, Layout layout, String lifecycle,
		MimeResponse.Copy copy) {

		if (portlet == null) {
			throw new NullPointerException("Portlet is null");
		}

		_httpServletRequest = httpServletRequest;
		_portlet = portlet;
		_portletRequest = portletRequest;
		_layout = layout;
		_lifecycle = lifecycle;
		_copy = copy;

		_parametersIncludedInPath = Collections.emptySet();

		PortletApp portletApp = portlet.getPortletApp();

		_portletSpecMajorVersion = portletApp.getSpecMajorVersion();

		if (_portletSpecMajorVersion < 3) {
			_portletURLParameterMap = new LinkedHashMap<>();
		}
		else {
			_portletURLParameterMap = new PortletURLParameterMap();
		}

		_removePublicRenderParameters = new LinkedHashSet<>();
		_secure = PortalUtil.isSecure(httpServletRequest);

		if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			_copyCurrentRenderParameters = true;
		}

		if (_portletSpecMajorVersion >= 3) {
			if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
				_mutableActionParametersImpl = new MutableActionParametersImpl(
					new LinkedHashMap<>());
			}
			else if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
				_mutableResourceParametersImpl =
					new MutableResourceParametersImpl(new LinkedHashMap<>());
			}

			_initMutableRenderParameters();
		}

		if (!portlet.isUndeployedPortlet()) {
			Set<String> autopropagatedParameters =
				portlet.getAutopropagatedParameters();

			for (String autopropagatedParameter : autopropagatedParameters) {
				if (PortalUtil.isReservedParameter(autopropagatedParameter)) {
					continue;
				}

				String value = httpServletRequest.getParameter(
					autopropagatedParameter);

				if (value != null) {
					setParameter(autopropagatedParameter, value);
				}
			}

			_escapeXml = MapUtil.getBoolean(
				portletApp.getContainerRuntimeOptions(),
				LiferayPortletConfig.RUNTIME_OPTION_ESCAPE_XML,
				PropsValues.PORTLET_URL_ESCAPE_XML);
		}

		if (layout != null) {
			_plid = layout.getPlid();
		}
	}

	private void _appendNamespaceAndEncode(StringBundler sb, String name) {
		String namespace = getNamespace();

		if (!name.startsWith(PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE) &&
			!name.startsWith(namespace) &&
			!PortalUtil.isReservedParameter(name)) {

			if (_encodedNamespace == null) {
				_encodedNamespace = URLCodec.encodeURL(namespace);
			}

			sb.append(_encodedNamespace);
		}

		sb.append(URLCodec.encodeURL(name));
	}

	private void _callPortletURLGenerationListener() {
		PortletApp portletApp = _portlet.getPortletApp();

		for (PortletURLListener portletURLListener :
				portletApp.getPortletURLListeners()) {

			try {
				PortletURLGenerationListener portletURLGenerationListener =
					PortletURLListenerFactory.create(portletURLListener);

				if (_lifecycle.equals(PortletRequest.ACTION_PHASE)) {
					portletURLGenerationListener.filterActionURL(this);
				}
				else if (_lifecycle.equals(PortletRequest.RENDER_PHASE)) {
					portletURLGenerationListener.filterRenderURL(this);
				}
				else if (_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
					portletURLGenerationListener.filterResourceURL(this);
				}
			}
			catch (PortletException portletException) {
				_log.error(portletException);
			}
		}
	}

	private Map<String, String[]> _combineAllParametersV3() {
		String namespace = getNamespace();

		Map<String, String[]> portletURLParams = new LinkedHashMap<>();

		Set<String> actionParameterNames = Collections.emptySet();

		if (_mutableActionParametersImpl != null) {
			actionParameterNames = new HashSet<>();

			Map<String, String[]> mutableActionParameterMap =
				_mutableActionParametersImpl.getParameterMap();

			for (Map.Entry<String, String[]> entry :
					mutableActionParameterMap.entrySet()) {

				String actionParameterName = entry.getKey();

				if (actionParameterName.startsWith(namespace)) {
					actionParameterName = actionParameterName.substring(
						namespace.length());
				}

				actionParameterNames.add(actionParameterName);

				portletURLParams.put(
					_ACTION_PARAMETER_NAMESPACE.concat(actionParameterName),
					entry.getValue());
			}
		}

		Set<String> resourceParameterNames = Collections.emptySet();

		if (_mutableResourceParametersImpl != null) {
			resourceParameterNames = new HashSet<>();

			Map<String, String[]> mutableResourceParameterMap =
				_mutableResourceParametersImpl.getParameterMap();

			for (Map.Entry<String, String[]> entry :
					mutableResourceParameterMap.entrySet()) {

				String resourceParameterName = entry.getKey();

				if (resourceParameterName.startsWith(namespace)) {
					resourceParameterName = resourceParameterName.substring(
						namespace.length());
				}

				resourceParameterNames.add(resourceParameterName);

				portletURLParams.put(
					_RESOURCE_PARAMETER_NAMESPACE.concat(resourceParameterName),
					entry.getValue());
			}
		}

		if (!_lifecycle.equals(PortletRequest.RESOURCE_PHASE) ||
			(_lifecycle.equals(PortletRequest.RESOURCE_PHASE) &&
			 _copyCurrentRenderParameters &&
			 !_cacheability.equals(ResourceURL.FULL))) {

			Map<String, String[]> mutableRenderParameterMap =
				_mutableRenderParametersImpl.getParameterMap();

			for (Map.Entry<String, String[]> entry :
					mutableRenderParameterMap.entrySet()) {

				String renderParameterName = entry.getKey();

				if (renderParameterName.startsWith(namespace)) {
					renderParameterName = renderParameterName.substring(
						namespace.length());
				}

				if (resourceParameterNames.contains(renderParameterName) ||
					(_lifecycle.equals(PortletRequest.RESOURCE_PHASE) &&
					 _mutableRenderParametersImpl.isPublic(
						 renderParameterName))) {

					continue;
				}

				if (!_lifecycle.equals(PortletRequest.RESOURCE_PHASE) &&
					(_removedParameterNames != null) &&
					_removedParameterNames.contains(renderParameterName)) {

					continue;
				}

				String[] renderParameterValues = entry.getValue();

				if (_mutableRenderParametersImpl.isPublic(
						renderParameterName)) {

					portletURLParams.put(
						renderParameterName, renderParameterValues);

					continue;
				}

				if (_lifecycle.equals(PortletRequest.ACTION_PHASE) &&
					actionParameterNames.contains(renderParameterName)) {

					String[] actionParameterValues =
						_mutableActionParametersImpl.getValues(
							renderParameterName);

					if ((actionParameterValues != null) &&
						_copyCurrentRenderParameters) {

						renderParameterValues = ArrayUtil.append(
							actionParameterValues, renderParameterValues);
					}

					renderParameterName = _ACTION_PARAMETER_NAMESPACE.concat(
						renderParameterName);
				}
				else if (_lifecycle.equals(PortletRequest.RENDER_PHASE)) {
					PortletRequest portletRequest = getPortletRequest();

					if (portletRequest != null) {
						LiferayRenderParameters renderParameters =
							(LiferayRenderParameters)
								portletRequest.getRenderParameters();

						String[] requestRenderParameterValues =
							renderParameters.getValues(renderParameterName);

						if ((requestRenderParameterValues != null) &&
							_copyCurrentRenderParameters &&
							!Arrays.equals(
								requestRenderParameterValues,
								renderParameterValues)) {

							renderParameterValues = ArrayUtil.append(
								renderParameterValues,
								requestRenderParameterValues);
						}
					}
				}
				else {
					renderParameterName =
						PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE.concat(
							renderParameterName);
				}

				portletURLParams.put(
					renderParameterName, renderParameterValues);
			}
		}

		return portletURLParams;
	}

	private Key _getKey() {
		try {
			if (_encrypt) {
				Company company = PortalUtil.getCompany(_httpServletRequest);

				return company.getKeyObj();
			}
		}
		catch (Exception exception) {
			_log.error("Unable to get company key", exception);
		}

		return null;
	}

	private LiferayMutablePortletParameters _getMutablePortletParameters(
		String parameterName) {

		if (_lifecycle.equals(PortletRequest.ACTION_PHASE) &&
			!_mutableRenderParametersImpl.isPublic(parameterName) &&
			!parameterName.startsWith(
				PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE)) {

			return _mutableActionParametersImpl;
		}

		if (_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			return _mutableResourceParametersImpl;
		}

		return _mutableRenderParametersImpl;
	}

	private void _initMutableRenderParameters() {
		Set<String> publicRenderParameterNames;
		Map<String, String[]> mutableRenderParameterMap = null;

		if (_portletRequest == null) {
			long plid = 0;

			if (_layout != null) {
				plid = _layout.getPlid();
			}

			publicRenderParameterNames = new HashSet<>();

			if (MimeResponse.Copy.ALL.equals(_copy) ||
				MimeResponse.Copy.PUBLIC.equals(_copy) ||
				_copyCurrentRenderParameters) {

				Map<String, String[]> privateRenderParameterMap =
					RenderParametersPool.get(
						_httpServletRequest, plid, _portlet.getPortletId());

				if (privateRenderParameterMap == null) {
					mutableRenderParameterMap = new LinkedHashMap<>();
				}
				else {
					mutableRenderParameterMap = new LinkedHashMap<>(
						privateRenderParameterMap);
				}

				Map<String, String[]> publicRenderParametersMap =
					PublicRenderParametersPool.get(_httpServletRequest, plid);

				Set<PublicRenderParameter> publicRenderParameters =
					_portlet.getPublicRenderParameters();

				for (PublicRenderParameter publicRenderParameter :
						publicRenderParameters) {

					String[] values = publicRenderParametersMap.get(
						PortletQNameUtil.getPublicRenderParameterName(
							publicRenderParameter.getQName()));

					if (ArrayUtil.isEmpty(values) ||
						Validator.isNull(values[0])) {

						continue;
					}

					String name = publicRenderParameter.getIdentifier();

					mutableRenderParameterMap.put(name, values);
					publicRenderParameterNames.add(name);
				}
			}

			if (mutableRenderParameterMap == null) {
				mutableRenderParameterMap = new LinkedHashMap<>();
			}
		}
		else {
			mutableRenderParameterMap = new LinkedHashMap<>();

			RenderParametersImpl liferayRenderParametersImpl =
				(RenderParametersImpl)_portletRequest.getRenderParameters();

			publicRenderParameterNames =
				liferayRenderParametersImpl.getPublicRenderParameterNames();

			if (MimeResponse.Copy.ALL.equals(_copy) ||
				MimeResponse.Copy.PUBLIC.equals(_copy)) {

				Map<String, String[]> liferayRenderParameterMap =
					liferayRenderParametersImpl.getParameterMap();

				for (Map.Entry<String, String[]> entry :
						liferayRenderParameterMap.entrySet()) {

					String renderParameterName = entry.getKey();

					if (MimeResponse.Copy.ALL.equals(_copy) ||
						liferayRenderParametersImpl.isPublic(
							renderParameterName)) {

						mutableRenderParameterMap.put(
							renderParameterName, entry.getValue());
					}
				}
			}
		}

		_mutableRenderParametersImpl = new MutableRenderParametersImpl(
			mutableRenderParameterMap, publicRenderParameterNames);
	}

	private boolean _isBlankValue(String[] value) {
		if ((value != null) && (value.length == 1) &&
			value[0].equals(StringPool.BLANK)) {

			return true;
		}

		return false;
	}

	private Map<String, String[]> _mergeWithRenderParametersV2(
		Map<String, String[]> portletURLParams) {

		Map<String, String[]> renderParameters = RenderParametersPool.get(
			_httpServletRequest, _plid, _portlet.getPortletId());

		if (renderParameters == null) {
			return portletURLParams;
		}

		String namespace = getNamespace();

		Map<String, String[]> mergedRenderParams = new LinkedHashMap<>(
			portletURLParams);

		for (Map.Entry<String, String[]> entry : renderParameters.entrySet()) {
			String name = entry.getKey();

			if (name.startsWith(namespace)) {
				name = name.substring(namespace.length());
			}

			if (!_lifecycle.equals(PortletRequest.RESOURCE_PHASE) &&
				(_removedParameterNames != null) &&
				_removedParameterNames.contains(name)) {

				continue;
			}

			String[] oldValues = entry.getValue();
			String[] newValues = _portletURLParameterMap.get(name);

			if (newValues == null) {
				mergedRenderParams.put(name, oldValues);
			}
			else if (_isBlankValue(newValues)) {
				mergedRenderParams.remove(name);
			}
			else {
				newValues = ArrayUtil.append(newValues, oldValues);

				mergedRenderParams.put(name, newValues);
			}
		}

		return mergedRenderParams;
	}

	private static final String _ACTION_PARAMETER_NAMESPACE = "p_action_p_";

	private static final String _RESOURCE_PARAMETER_NAMESPACE = "p_resource_p_";

	private static final Log _log = LogFactoryUtil.getLog(PortletURLImpl.class);

	private static final Map<String, String> _cacheabilities =
		HashMapBuilder.put(
			"FULL", ResourceURL.FULL
		).put(
			"PAGE", ResourceURL.PAGE
		).put(
			"PORTLET", ResourceURL.PORTLET
		).build();

	private boolean _anchor = true;
	private String _cacheability = ResourceURL.PAGE;
	private MimeResponse.Copy _copy;
	private boolean _copyCurrentRenderParameters;
	private long _doAsGroupId;
	private long _doAsUserId;
	private String _doAsUserLanguageId;
	private String _encodedNamespace;
	private boolean _encrypt;
	private boolean _escapeXml = PropsValues.PORTLET_URL_ESCAPE_XML;
	private final HttpServletRequest _httpServletRequest;
	private Layout _layout;
	private String _layoutFriendlyURL;
	private String _lifecycle;
	private MutableActionParametersImpl _mutableActionParametersImpl;
	private MutableRenderParametersImpl _mutableRenderParametersImpl;
	private MutableResourceParametersImpl _mutableResourceParametersImpl;
	private String _namespace;
	private Set<String> _parametersIncludedInPath;
	private long _plid;
	private Portlet _portlet;
	private String _portletModeString;
	private final PortletRequest _portletRequest;
	private final int _portletSpecMajorVersion;
	private Map<String, String[]> _portletURLParameterMap;
	private long _refererGroupId;
	private long _refererPlid;
	private Set<String> _removedParameterNames;
	private final Set<String> _removePublicRenderParameters;
	private String _resourceID;
	private boolean _secure;
	private String _toString;
	private boolean _windowStateRestoreCurrentView;
	private String _windowStateString;

	private class PortletURLParameterMap extends AbstractMap<String, String[]> {

		@Override
		public Set<Entry<String, String[]>> entrySet() {
			if ((_entrySet == null) ||
				((_mutableRenderParametersImpl != null) &&
				 _mutableRenderParametersImpl.isMutated()) ||
				((_mutableActionParametersImpl != null) &&
				 _mutableActionParametersImpl.isMutated()) ||
				((_mutableResourceParametersImpl != null) &&
				 _mutableResourceParametersImpl.isMutated())) {

				_entrySet = new LinkedHashSet<>();

				if (_mutableResourceParametersImpl != null) {
					Map<String, String[]> mutableResourceParameterMap =
						_mutableResourceParametersImpl.getParameterMap();

					for (Map.Entry<String, String[]> entry :
							mutableResourceParameterMap.entrySet()) {

						_entrySet.add(
							new SimpleEntry<>(
								entry.getKey(), entry.getValue()));
					}
				}

				if (_mutableActionParametersImpl != null) {
					Map<String, String[]> mutableActionParameterMap =
						_mutableActionParametersImpl.getParameterMap();

					for (Map.Entry<String, String[]> entry :
							mutableActionParameterMap.entrySet()) {

						_entrySet.add(
							new SimpleEntry<>(
								entry.getKey(), entry.getValue()));
					}
				}

				if ((_mutableRenderParametersImpl != null) &&
					!_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

					Map<String, String[]> mutableRenderParameterMap =
						_mutableRenderParametersImpl.getParameterMap();

					for (Map.Entry<String, String[]> entry :
							mutableRenderParameterMap.entrySet()) {

						_entrySet.add(
							new SimpleEntry<>(
								entry.getKey(), entry.getValue()));
					}
				}
			}

			return _entrySet;
		}

		@Override
		public String[] put(String key, String[] value) {
			Set<Map.Entry<String, String[]>> entrySet = entrySet();

			for (Map.Entry<String, String[]> entry : entrySet) {
				String entryKey = entry.getKey();

				if (entryKey.equals(key)) {
					String[] oldValues = entry.getValue();

					entry.setValue(value);

					return oldValues;
				}
			}

			entrySet.add(new SimpleEntry<>(key, value));

			return null;
		}

		private Set<Map.Entry<String, String[]>> _entrySet;

	}

}