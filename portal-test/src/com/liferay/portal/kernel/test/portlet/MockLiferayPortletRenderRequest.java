/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ProxyUtil;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderParameters;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class MockLiferayPortletRenderRequest
	extends MockRenderRequest implements LiferayPortletRequest {

	public MockLiferayPortletRenderRequest() {
		this(new MockHttpServletRequest());
	}

	public MockLiferayPortletRenderRequest(
		MockHttpServletRequest mockHttpServletRequest) {

		_mockHttpServletRequest = mockHttpServletRequest;

		_mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			ProxyUtil.newProxyInstance(
				LiferayPortletConfig.class.getClassLoader(),
				new Class<?>[] {LiferayPortletConfig.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getPortletId")) {
						return "testPortlet";
					}

					return null;
				}));
	}

	@Override
	public void addParameter(String name, String value) {
		_mockHttpServletRequest.addParameter(name, value);
	}

	@Override
	public void addParameter(String name, String[] values) {
		_mockHttpServletRequest.addParameter(name, values);
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public Map<String, String[]> clearRenderParameters() {
		return null;
	}

	@Override
	public void defineObjects(
		PortletConfig portletConfig, PortletResponse portletResponse) {
	}

	@Override
	public Object getAttribute(String name) {
		return _mockHttpServletRequest.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return _mockHttpServletRequest.getAttributeNames();
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return _mockHttpServletRequest;
	}

	@Override
	public String getLifecycle() {
		return null;
	}

	@Override
	public HttpServletRequest getOriginalHttpServletRequest() {
		return null;
	}

	@Override
	public String getParameter(String name) {
		return _mockHttpServletRequest.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _mockHttpServletRequest.getParameterMap();
	}

	@Override
	public long getPlid() {
		return 0;
	}

	@Override
	public Portlet getPortlet() {
		return null;
	}

	@Override
	public PortletContext getPortletContext() {
		return null;
	}

	@Override
	public String getPortletName() {
		return null;
	}

	@Override
	public HttpServletRequest getPortletRequestDispatcherRequest() {
		return null;
	}

	@Override
	public RenderParameters getRenderParameters() {
		return null;
	}

	@Override
	public String getUserAgent() {
		return null;
	}

	@Override
	public void invalidateSession() {
	}

	@Override
	public void setAttribute(String name, Object value) {
		_mockHttpServletRequest.setAttribute(name, value);
	}

	@Override
	public void setParameter(String key, String value) {
		_mockHttpServletRequest.setParameter(key, value);
	}

	@Override
	public void setParameter(String key, String[] values) {
		_mockHttpServletRequest.setParameter(key, values);
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		_mockHttpServletRequest.setParameters(parameters);
	}

	@Override
	public void setPortletRequestDispatcherRequest(
		HttpServletRequest httpServletRequest) {
	}

	private final MockHttpServletRequest _mockHttpServletRequest;

}