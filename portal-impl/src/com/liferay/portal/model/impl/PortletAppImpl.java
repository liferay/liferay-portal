/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.EventDefinition;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletFilter;
import com.liferay.portal.kernel.model.PortletURLListener;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.QName;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletAppImpl implements PortletApp {

	public PortletAppImpl(String servletContextName) {
		_servletContextName = servletContextName;

		if (Validator.isNotNull(servletContextName)) {
			_contextPath = StringPool.SLASH.concat(_servletContextName);
			_warFile = true;
		}
		else {
			_warFile = false;
		}
	}

	@Override
	public void addEventDefinition(EventDefinition eventDefinition) {
		_eventDefinitions.add(eventDefinition);
	}

	@Override
	public void addPortlet(Portlet portlet) {
		_portlets.add(portlet);
	}

	@Override
	public void addPortletFilter(PortletFilter portletFilter) {
		_portletFilters.add(portletFilter);
		_portletFiltersMap.put(portletFilter.getFilterName(), portletFilter);
	}

	@Override
	public void addPortletURLListener(PortletURLListener portletURLListener) {
		_portletURLListeners.add(portletURLListener);
		_portletURLListenersMap.put(
			portletURLListener.getListenerClass(), portletURLListener);
	}

	@Override
	public void addPublicRenderParameter(
		PublicRenderParameter publicRenderParameter) {

		_publicRenderParametersMap.put(
			publicRenderParameter.getIdentifier(), publicRenderParameter);
	}

	@Override
	public void addPublicRenderParameter(String identifier, QName qName) {
		PublicRenderParameter publicRenderParameter =
			new PublicRenderParameterImpl(identifier, qName, this);

		addPublicRenderParameter(publicRenderParameter);
	}

	@Override
	public void addServletURLPatterns(Set<String> servletURLPatterns) {
		_servletURLPatterns.addAll(servletURLPatterns);
	}

	@Override
	public Map<String, String[]> getContainerRuntimeOptions() {
		return _containerRuntimeOptions;
	}

	@Override
	public String getContextPath() {
		return _contextPath;
	}

	@Override
	public Map<String, String> getCustomUserAttributes() {
		return _customUserAttributes;
	}

	@Override
	public String getDefaultNamespace() {
		return _defaultNamespace;
	}

	@Override
	public Set<EventDefinition> getEventDefinitions() {
		return _eventDefinitions;
	}

	@Override
	public PortletFilter getPortletFilter(String filterName) {
		return _portletFiltersMap.get(filterName);
	}

	@Override
	public Set<PortletFilter> getPortletFilters() {
		return _portletFilters;
	}

	@Override
	public List<Portlet> getPortlets() {
		return new ArrayList<>(_portlets);
	}

	@Override
	public PortletURLListener getPortletURLListener(String listenerClass) {
		return _portletURLListenersMap.get(listenerClass);
	}

	@Override
	public Set<PortletURLListener> getPortletURLListeners() {
		return _portletURLListeners;
	}

	@Override
	public PublicRenderParameter getPublicRenderParameter(String identifier) {
		return _publicRenderParametersMap.get(identifier);
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public String getServletContextName() {
		return _servletContextName;
	}

	@Override
	public Set<String> getServletURLPatterns() {
		return _servletURLPatterns;
	}

	@Override
	public int getSpecMajorVersion() {
		return _specMajorVersion;
	}

	@Override
	public int getSpecMinorVersion() {
		return _specMinorVersion;
	}

	@Override
	public Set<String> getUserAttributes() {
		return _userAttributes;
	}

	@Override
	public boolean isWARFile() {
		return _warFile;
	}

	@Override
	public void removePortlet(Portlet portletModel) {
		_portlets.remove(portletModel);
	}

	@Override
	public void setDefaultNamespace(String defaultNamespace) {
		_defaultNamespace = defaultNamespace;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;

		_contextPath = _servletContext.getContextPath();
	}

	@Override
	public void setSpecMajorVersion(int specMajorVersion) {
		_specMajorVersion = specMajorVersion;
	}

	@Override
	public void setSpecMinorVersion(int specMinorVersion) {
		_specMinorVersion = specMinorVersion;
	}

	@Override
	public void setWARFile(boolean warFile) {
		_warFile = warFile;
	}

	private final Map<String, String[]> _containerRuntimeOptions =
		new HashMap<>();
	private String _contextPath = StringPool.BLANK;
	private final Map<String, String> _customUserAttributes =
		new LinkedHashMap<>();
	private String _defaultNamespace = XMLConstants.NULL_NS_URI;
	private final Set<EventDefinition> _eventDefinitions =
		new LinkedHashSet<>();
	private final Set<PortletFilter> _portletFilters = new LinkedHashSet<>();
	private final Map<String, PortletFilter> _portletFiltersMap =
		new HashMap<>();
	private final Set<Portlet> _portlets = new LinkedHashSet<>();
	private final Set<PortletURLListener> _portletURLListeners =
		new LinkedHashSet<>();
	private final Map<String, PortletURLListener> _portletURLListenersMap =
		new HashMap<>();
	private final Map<String, PublicRenderParameter>
		_publicRenderParametersMap = new HashMap<>();
	private ServletContext _servletContext;
	private final String _servletContextName;
	private final Set<String> _servletURLPatterns = new LinkedHashSet<>();
	private int _specMajorVersion = 2;
	private int _specMinorVersion;
	private final Set<String> _userAttributes = new LinkedHashSet<>();
	private boolean _warFile;

}