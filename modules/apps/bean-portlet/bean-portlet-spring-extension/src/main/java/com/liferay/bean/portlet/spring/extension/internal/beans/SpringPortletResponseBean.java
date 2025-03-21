/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.beans;

import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManager;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManagerThreadLocal;

import jakarta.annotation.ManagedBean;
import jakarta.annotation.Priority;

import jakarta.portlet.PortletResponse;
import jakarta.portlet.filter.PortletResponseWrapper;

import jakarta.servlet.http.Cookie;

import java.util.Collection;

import org.w3c.dom.Element;

/**
 * @author Neil Griffin
 */
@ManagedBean("portletResponse")

// When the developer uses "@Inject PortletResponse", Spring must be able to
// disambiguate between PortletResponse and all its extending interfaces. This
// is accomplished with @Priority. However, Spring only knows how to apply the
// @Priority annotation at the class-level for a class that represents a single
// bean. In other words, Spring does not know how to apply the @Priority
// annotation for a class like JSR362SpringBeanProducer that produces multiple
// types of beans via producer methods annotated with @Bean.

@Priority(1)

// In order to support unwrapping, it is necessary for this bean to extend
// PortletResponseWrapper. However, PortletResponseWrapper is designed in such a
// way that it requires the wrapped instance to be specified via the
// constructor. Since the instance is obtained from a request-based ThreadLocal,
// it is not possible to pass the instance via the constructor. Therefore each
// of the methods of PortletResponseWrapper are overridden in this class.
public class SpringPortletResponseBean extends PortletResponseWrapper {

	public SpringPortletResponseBean() {

		// The superclass constructor requires a non-null instance or else
		// it will throw IllegalArgumentException.

		super(DummyPortletResponse.INSTANCE);
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
	public PortletResponse getResponse() {
		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		return springScopedBeanManager.getPortletResponse();
	}

	@Override
	public void setProperty(String key, String value) {
		PortletResponse portletResponse = getResponse();

		portletResponse.setProperty(key, value);
	}

	@Override
	public void setResponse(PortletResponse portletResponse) {
		throw new UnsupportedOperationException();
	}

}