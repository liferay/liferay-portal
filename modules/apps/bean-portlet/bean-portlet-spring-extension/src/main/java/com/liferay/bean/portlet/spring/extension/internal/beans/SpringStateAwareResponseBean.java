/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.beans;

import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManager;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManagerThreadLocal;

import jakarta.annotation.ManagedBean;
import jakarta.annotation.Priority;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.StateAwareResponse;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;
import jakarta.portlet.filter.StateAwareResponseWrapper;

import jakarta.servlet.http.Cookie;

import java.io.Serializable;

import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * @author Neil Griffin
 */
@ManagedBean("stateAwareResponse")

// When the developer uses "@Inject StateAwareResponse", Spring must be able to
// disambiguate between StateAwareResponse, ActionResponse, and EventResponse.
// This is accomplished with @Priority. However, Spring only knows how to apply
// the @Priority annotation at the class-level for a class that represents a
// single bean. In other words, Spring does not know how to apply the @Priority
// annotation for a class like JSR362SpringBeanProducer that produces multiple
// types of beans via producer methods annotated with @Bean.

@Priority(3)

// In order to support unwrapping, it is necessary for this bean to extend
// StateAwareResponseWrapper. However, StateAwareResponseWrapper is designed in
// such a way that it requires the wrapped instance to be specified via the
// constructor. Since the instance is obtained from a request-based ThreadLocal,
// it is not possible to pass the instance via the constructor. Therefore each
// of the methods of PortletResponseWrapper and StateAwareResponseWrapper are
// overridden in this class.
public class SpringStateAwareResponseBean extends StateAwareResponseWrapper {

	public SpringStateAwareResponseBean() {

		// The superclass constructor requires a non-null instance or else
		// it will throw IllegalArgumentException.

		super(DummyStateAwareResponse.INSTANCE);
	}

	@Override
	public void addProperty(Cookie cookie) {
		PortletResponse portletResponse = getResponse();

		portletResponse.addProperty(cookie);
	}

	@Override
	public void addProperty(String key, Element element) {
		PortletResponse portletResponse = getResponse();

		portletResponse.addProperty(key, element);
	}

	@Override
	public void addProperty(String key, String value) {
		PortletResponse portletResponse = getResponse();

		portletResponse.addProperty(key, value);
	}

	@Override
	public Element createElement(String tagName) {
		PortletResponse portletResponse = getResponse();

		return portletResponse.createElement(tagName);
	}

	@Override
	public String encodeURL(String path) {
		PortletResponse portletResponse = getResponse();

		return portletResponse.encodeURL(path);
	}

	@Override
	public String getNamespace() {
		PortletResponse portletResponse = getResponse();

		return portletResponse.getNamespace();
	}

	@Override
	public PortletMode getPortletMode() {
		StateAwareResponse stateAwareResponse = getResponse();

		return stateAwareResponse.getPortletMode();
	}

	@Override
	public String getProperty(String key) {
		PortletResponse portletResponse = getResponse();

		return portletResponse.getProperty(key);
	}

	@Override
	public Collection<String> getPropertyNames() {
		PortletResponse portletResponse = getResponse();

		return portletResponse.getPropertyNames();
	}

	@Override
	public Collection<String> getPropertyValues(String key) {
		PortletResponse portletResponse = getResponse();

		return portletResponse.getPropertyValues(key);
	}

	@Override
	@SuppressWarnings("deprecation")
	public Map<String, String[]> getRenderParameterMap() {
		StateAwareResponse stateAwareResponse = getResponse();

		return stateAwareResponse.getRenderParameterMap();
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		StateAwareResponse stateAwareResponse = getResponse();

		return stateAwareResponse.getRenderParameters();
	}

	@Override
	public StateAwareResponse getResponse() {
		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		return (StateAwareResponse)springScopedBeanManager.getPortletResponse();
	}

	@Override
	public WindowState getWindowState() {
		StateAwareResponse stateAwareResponse = getResponse();

		return stateAwareResponse.getWindowState();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void removePublicRenderParameter(String name) {
		StateAwareResponse stateAwareResponse = getResponse();

		stateAwareResponse.removePublicRenderParameter(name);
	}

	@Override
	public void setEvent(QName name, Serializable value) {
		StateAwareResponse stateAwareResponse = getResponse();

		stateAwareResponse.setEvent(name, value);
	}

	@Override
	public void setEvent(String name, Serializable value) {
		StateAwareResponse stateAwareResponse = getResponse();

		stateAwareResponse.setEvent(name, value);
	}

	@Override
	public void setPortletMode(PortletMode portletMode)
		throws PortletModeException {

		StateAwareResponse stateAwareResponse = getResponse();

		stateAwareResponse.setPortletMode(portletMode);
	}

	@Override
	public void setProperty(String key, String value) {
		PortletResponse portletResponse = getResponse();

		portletResponse.setProperty(key, value);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setRenderParameter(String key, String value) {
		StateAwareResponse stateAwareResponse = getResponse();

		stateAwareResponse.setRenderParameter(key, value);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setRenderParameter(String key, String... values) {
		StateAwareResponse stateAwareResponse = getResponse();

		stateAwareResponse.setRenderParameter(key, values);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setRenderParameters(Map<String, String[]> parameters) {
		StateAwareResponse stateAwareResponse = getResponse();

		stateAwareResponse.setRenderParameters(parameters);
	}

	@Override
	public void setResponse(StateAwareResponse stateAwareResponse) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setWindowState(WindowState windowState)
		throws WindowStateException {

		StateAwareResponse stateAwareResponse = getResponse();

		stateAwareResponse.setWindowState(windowState);
	}

}