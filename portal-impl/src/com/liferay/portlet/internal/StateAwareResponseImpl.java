/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayStateAwareResponse;
import com.liferay.portal.kernel.portlet.PortletQNameUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portlet.PublicRenderParametersPool;

import jakarta.portlet.Event;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class StateAwareResponseImpl
	extends PortletResponseImpl implements LiferayStateAwareResponse {

	public String getDefaultNamespace() {
		Portlet portlet = getPortlet();

		if (portlet != null) {
			PortletApp portletApp = portlet.getPortletApp();

			return portletApp.getDefaultNamespace();
		}

		return XMLConstants.NULL_NS_URI;
	}

	@Override
	public List<Event> getEvents() {
		return _events;
	}

	public Layout getLayout() {
		return _layout;
	}

	@Override
	public PortletMode getPortletMode() {
		return _portletMode;
	}

	@Override
	public String getRedirectLocation() {
		return _redirectLocation;
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public Map<String, String[]> getRenderParameterMap() {
		Map<String, String[]> renderParameterMap = new LinkedHashMap<>();

		Map<String, String[]> mutableRenderParameterMap =
			_mutableRenderParametersImpl.getParameterMap();

		for (Map.Entry<String, String[]> entry :
				mutableRenderParameterMap.entrySet()) {

			String parameterName = entry.getKey();

			if (!_mutableRenderParametersImpl.isPublic(parameterName) ||
				_mutableRenderParametersImpl.isMutated(parameterName)) {

				renderParameterMap.put(parameterName, entry.getValue());
			}
		}

		return renderParameterMap;
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		return _mutableRenderParametersImpl;
	}

	public User getUser() {
		return _user;
	}

	@Override
	public WindowState getWindowState() {
		return _windowState;
	}

	public void init(
			PortletRequestImpl portletRequestImpl,
			HttpServletResponse httpServletResponse, User user, Layout layout,
			boolean setWindowStateAndPortletMode)
		throws PortletModeException, WindowStateException {

		super.init(portletRequestImpl, httpServletResponse);

		_user = user;
		_layout = layout;

		_publicRenderParameters = PublicRenderParametersPool.get(
			getHttpServletRequest(), layout.getPlid());

		if (setWindowStateAndPortletMode) {
			setWindowState(portletRequestImpl.getWindowState());
			setPortletMode(portletRequestImpl.getPortletMode());
		}

		// Set _calledSetRenderParameter to false because setWindowState and
		// setPortletMode sets it to true

		_calledSetRenderParameter = false;

		// Since Portlet 3.0 action URLs can contain private render parameters,
		// it is necessary to populate the render parameter map with the render
		// parameters found in the request

		Portlet portlet = portletRequestImpl.getPortlet();

		PortletApp portletApp = portlet.getPortletApp();

		if (portletApp.getSpecMajorVersion() < 3) {
			_mutableRenderParametersImpl = new MutableRenderParametersImpl(
				_params, Collections.emptySet());
		}
		else {
			Set<String> publicRenderParameterNames = new LinkedHashSet<>();

			RenderParametersImpl renderParametersImpl =
				(RenderParametersImpl)portletRequestImpl.getRenderParameters();

			Map<String, String[]> liferayRenderParameterMap =
				renderParametersImpl.getParameterMap();

			for (Map.Entry<String, String[]> entry :
					liferayRenderParameterMap.entrySet()) {

				String renderParameterName = entry.getKey();

				if (renderParametersImpl.isPublic(renderParameterName)) {
					publicRenderParameterNames.add(renderParameterName);
				}

				_params.put(renderParameterName, entry.getValue());
			}

			_mutableRenderParametersImpl = new MutableRenderParametersImpl(
				_params, publicRenderParameterNames);
		}
	}

	@Override
	public boolean isCalledSetRenderParameter() {
		if (_calledSetRenderParameter ||
			_mutableRenderParametersImpl.isMutated()) {

			return true;
		}

		return false;
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

		PublicRenderParameter publicRenderParameter =
			getPortlet().getPublicRenderParameter(name);

		if (publicRenderParameter == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Public parameter " + name + "does not exist");
			}

			return;
		}

		String key = PortletQNameUtil.getPublicRenderParameterName(
			publicRenderParameter.getQName());

		_publicRenderParameters.remove(key);
	}

	@Override
	public void setEvent(QName name, Serializable value) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		_events.add(new EventImpl(name.getLocalPart(), name, value));
	}

	@Override
	public void setEvent(String name, Serializable value) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		setEvent(new QName(getDefaultNamespace(), name), value);
	}

	@Override
	public void setPortletMode(PortletMode portletMode)
		throws PortletModeException {

		if (_redirectLocation != null) {
			throw new IllegalStateException();
		}

		if (portletMode == null) {
			throw new IllegalArgumentException();
		}

		if (!portletRequestImpl.isPortletModeAllowed(portletMode)) {
			throw new PortletModeException(portletMode.toString(), portletMode);
		}

		try {
			_portletMode = PortalUtil.updatePortletMode(
				portletName, _user, _layout, portletMode,
				portletRequestImpl.getHttpServletRequest());

			portletRequestImpl.setPortletMode(_portletMode);
		}
		catch (Exception exception) {
			throw new PortletModeException(exception, portletMode);
		}

		_calledSetRenderParameter = true;
	}

	public void setRedirectLocation(String redirectLocation) {
		_redirectLocation = redirectLocation;
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public void setRenderParameter(String name, String value) {
		if (_redirectLocation != null) {
			throw new IllegalStateException();
		}

		if ((name == null) || (value == null)) {
			throw new IllegalArgumentException();
		}

		setRenderParameter(name, new String[] {value});
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public void setRenderParameter(String name, String[] values) {
		if (_redirectLocation != null) {
			throw new IllegalStateException();
		}

		if ((name == null) || (values == null)) {
			throw new IllegalArgumentException();
		}

		for (String value : values) {
			if (value == null) {
				throw new IllegalArgumentException();
			}
		}

		if (!setPublicRenderParameter(name, values)) {
			_mutableRenderParametersImpl.setValues(name, values);
		}

		_calledSetRenderParameter = true;
	}

	/**
	 * @deprecated As of Judson (7.1.x)
	 */
	@Deprecated
	@Override
	public void setRenderParameters(Map<String, String[]> params) {
		if (_redirectLocation != null) {
			throw new IllegalStateException();
		}

		if (params == null) {
			throw new IllegalArgumentException();
		}

		_mutableRenderParametersImpl.clear();

		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			String key = entry.getKey();

			if (key == null) {
				throw new IllegalArgumentException();
			}

			String[] value = entry.getValue();

			if (value == null) {
				throw new IllegalArgumentException();
			}

			if (setPublicRenderParameter(key, value)) {
				continue;
			}

			_mutableRenderParametersImpl.setValues(key, value);
		}

		_calledSetRenderParameter = true;
	}

	@Override
	public void setWindowState(WindowState windowState)
		throws WindowStateException {

		if (_redirectLocation != null) {
			throw new IllegalStateException();
		}

		if (windowState == null) {
			throw new IllegalArgumentException();
		}

		if (!portletRequestImpl.isWindowStateAllowed(windowState)) {
			throw new WindowStateException(windowState.toString(), windowState);
		}

		try {
			_windowState = PortalUtil.updateWindowState(
				portletName, _user, _layout, windowState,
				portletRequestImpl.getHttpServletRequest());

			portletRequestImpl.setWindowState(_windowState);
		}
		catch (Exception exception) {
			throw new WindowStateException(exception, windowState);
		}

		_calledSetRenderParameter = true;
	}

	protected void reset() {
		_events.clear();
		_mutableRenderParametersImpl.clear();

		try {
			setPortletMode(PortletMode.VIEW);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to reset portlet mode to VIEW", exception);
			}
		}

		_portletMode = PortletMode.UNDEFINED;

		_redirectLocation = null;

		try {
			setWindowState(WindowState.NORMAL);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to reset window state to NORMAL", exception);
			}
		}

		_windowState = WindowState.UNDEFINED;

		_calledSetRenderParameter = false;
	}

	protected boolean setPublicRenderParameter(String name, String[] values) {
		Portlet portlet = getPortlet();

		PublicRenderParameter publicRenderParameter =
			portlet.getPublicRenderParameter(name);

		if (publicRenderParameter == null) {
			return false;
		}

		String[] oldValues = _publicRenderParameters.get(name);

		if (oldValues != null) {
			values = ArrayUtil.append(oldValues, values);
		}

		_publicRenderParameters.put(
			PortletQNameUtil.getPublicRenderParameterName(
				publicRenderParameter.getQName()),
			values);

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StateAwareResponseImpl.class);

	private boolean _calledSetRenderParameter;
	private final List<Event> _events = new ArrayList<>();
	private Layout _layout;
	private MutableRenderParametersImpl _mutableRenderParametersImpl;
	private final Map<String, String[]> _params = new LinkedHashMap<>();
	private PortletMode _portletMode = PortletMode.UNDEFINED;
	private Map<String, String[]> _publicRenderParameters;
	private String _redirectLocation;
	private User _user;
	private WindowState _windowState = WindowState.UNDEFINED;

}