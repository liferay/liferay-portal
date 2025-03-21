/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.tracker.internal;

import com.liferay.portal.kernel.model.EventDefinition;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletURLListener;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Raymond Augé
 */
public class BundlePortletAppDelegate {

	public BundlePortletAppDelegate(
		Portlet portalPortletModel, ServletContext servletContext) {

		_servletContext = servletContext;

		_portletApp = portalPortletModel.getPortletApp();
	}

	public void addEventDefinition(EventDefinition eventDefinition) {
		_eventDefinitions.add(eventDefinition);
	}

	public void addPortletURLListener(PortletURLListener portletURLListener) {
		_portletURLListeners.add(portletURLListener);
		_portletURLListenersMap.put(
			portletURLListener.getListenerClass(), portletURLListener);
	}

	public String getContextPath() {
		ServletContext servletContext = getServletContext();

		return servletContext.getContextPath();
	}

	public String getDefaultNamespace() {
		if (_defaultNamespace == null) {
			return _portletApp.getDefaultNamespace();
		}

		return _defaultNamespace;
	}

	public Set<EventDefinition> getEventDefinitions() {
		return _eventDefinitions;
	}

	public PortletURLListener getPortletURLListener(String listenerClass) {
		return _portletURLListenersMap.get(listenerClass);
	}

	public Set<PortletURLListener> getPortletURLListeners() {
		return _portletURLListeners;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	public String getServletContextName() {
		ServletContext servletContext = getServletContext();

		return servletContext.getServletContextName();
	}

	public int getSpecMajorVersion() {
		return _specMajorVersion;
	}

	public int getSpecMinorVersion() {
		return _specMinorVersion;
	}

	public boolean isWARFile() {
		return _warFile;
	}

	public void setDefaultNamespace(String defaultNamespace) {
		if (Validator.isNull(defaultNamespace)) {
			_defaultNamespace = null;
		}
		else {
			_defaultNamespace = defaultNamespace;
		}
	}

	public void setServletContext(ServletContext servletContext) {
		throw new UnsupportedOperationException();
	}

	public void setSpecMajorVersion(int specMajorVersion) {
		_specMajorVersion = specMajorVersion;
	}

	public void setSpecMinorVersion(int specMinorVersion) {
		_specMinorVersion = specMinorVersion;
	}

	public void setWARFile(boolean warFile) {
		_warFile = warFile;
	}

	private String _defaultNamespace;
	private final Set<EventDefinition> _eventDefinitions = new HashSet<>();
	private final PortletApp _portletApp;
	private final Set<PortletURLListener> _portletURLListeners =
		new LinkedHashSet<>();
	private final Map<String, PortletURLListener> _portletURLListenersMap =
		new HashMap<>();
	private final ServletContext _servletContext;
	private int _specMajorVersion = 2;
	private int _specMinorVersion;
	private boolean _warFile = true;

}