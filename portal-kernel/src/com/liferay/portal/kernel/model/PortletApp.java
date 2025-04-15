/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.xml.QName;

import jakarta.servlet.ServletContext;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface PortletApp extends Serializable {

	public void addEventDefinition(EventDefinition eventDefinition);

	public void addPortlet(Portlet portlet);

	public void addPortletFilter(PortletFilter portletFilter);

	public void addPortletURLListener(PortletURLListener portletURLListener);

	public void addPublicRenderParameter(
		PublicRenderParameter publicRenderParameter);

	public void addPublicRenderParameter(String identifier, QName qName);

	public void addServletURLPatterns(Set<String> servletURLPatterns);

	public Map<String, String[]> getContainerRuntimeOptions();

	public String getContextPath();

	public Map<String, String> getCustomUserAttributes();

	public String getDefaultNamespace();

	public Set<EventDefinition> getEventDefinitions();

	public PortletFilter getPortletFilter(String filterName);

	public Set<PortletFilter> getPortletFilters();

	public List<Portlet> getPortlets();

	public PortletURLListener getPortletURLListener(String listenerClass);

	public Set<PortletURLListener> getPortletURLListeners();

	public PublicRenderParameter getPublicRenderParameter(String identifier);

	public ServletContext getServletContext();

	public String getServletContextName();

	public Set<String> getServletURLPatterns();

	public int getSpecMajorVersion();

	public int getSpecMinorVersion();

	public Set<String> getUserAttributes();

	public boolean isWARFile();

	public void removePortlet(Portlet portletModel);

	public void setDefaultNamespace(String defaultNamespace);

	public void setServletContext(ServletContext servletContext);

	public void setSpecMajorVersion(int specMajorVersion);

	public void setSpecMinorVersion(int specMinorVersion);

	public void setWARFile(boolean warFile);

}