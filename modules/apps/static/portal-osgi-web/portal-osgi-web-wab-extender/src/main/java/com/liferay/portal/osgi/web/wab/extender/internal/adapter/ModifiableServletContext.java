/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal.adapter;

import com.liferay.portal.osgi.web.servlet.context.helper.definition.ListenerDefinition;
import com.liferay.portal.osgi.web.wab.extender.internal.registration.FilterRegistrationImpl;
import com.liferay.portal.osgi.web.wab.extender.internal.registration.ServletRegistrationImpl;

import jakarta.servlet.ServletContext;

import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;

/**
 * @author Raymond Augé
 */
public interface ModifiableServletContext {

	public Bundle getBundle();

	public Map<String, FilterRegistrationImpl> getFilterRegistrationImpls();

	public List<ListenerDefinition> getListenerDefinitions();

	public Map<String, ServletRegistrationImpl> getServletRegistrationImpls();

	public Map<String, String> getUnregisteredInitParameters();

	public ServletContext getWrappedServletContext();

	public void registerFilters();

	public void registerServlets();

}