/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper;

import com.liferay.portal.osgi.web.servlet.context.helper.definition.WebXMLDefinition;

import jakarta.servlet.ServletContext;

import java.util.Map;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Raymond Augé
 */
@ProviderType
public interface ServletContextHelperRegistration {

	public void close();

	public Set<Class<?>> getAnnotatedClasses();

	public Set<Class<?>> getClasses();

	public ServletContext getServletContext();

	public WebXMLDefinition getWebXMLDefinition();

	public boolean isWabShapedBundle();

	public void setProperties(Map<String, String> contextParameters);

}