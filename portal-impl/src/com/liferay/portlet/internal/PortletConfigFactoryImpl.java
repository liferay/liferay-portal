/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletConfigFactory;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portlet.PortletContextFactoryUtil;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;

import jakarta.servlet.ServletContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletConfigFactoryImpl implements PortletConfigFactory {

	@Override
	public PortletConfig create(
		Portlet portlet, ServletContext servletContext) {

		Map<String, PortletConfig> portletConfigs = _pool.get(
			portlet.getRootPortletId());

		if (portletConfigs == null) {
			portletConfigs = new ConcurrentHashMap<>();

			_pool.put(portlet.getRootPortletId(), portletConfigs);
		}

		PortletConfig portletConfig = portletConfigs.get(
			portlet.getPortletId());

		if ((portletConfig == null) ||
			!_isSamePortletDeployedStatus(portlet, portletConfig)) {

			PortletContext portletContext = PortletContextFactoryUtil.create(
				portlet, servletContext);

			portletConfig = new PortletConfigImpl(portlet, portletContext);

			portletConfigs.put(portlet.getPortletId(), portletConfig);
		}

		return portletConfig;
	}

	@Override
	public void destroy(Portlet portlet) {
		_pool.remove(portlet.getRootPortletId());
	}

	@Override
	public PortletConfig get(Portlet portlet) {
		return get(portlet.getPortletId());
	}

	@Override
	public PortletConfig get(String portletId) {
		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		Map<String, PortletConfig> portletConfigs = _pool.get(rootPortletId);

		if (portletConfigs == null) {
			return null;
		}

		return portletConfigs.get(portletId);
	}

	@Override
	public PortletConfig update(Portlet portlet) {
		Map<String, PortletConfig> portletConfigs = _pool.get(
			portlet.getRootPortletId());

		if (portletConfigs == null) {
			return null;
		}

		PortletConfig portletConfig = portletConfigs.get(
			portlet.getPortletId());

		portletConfig = new PortletConfigImpl(
			portlet, portletConfig.getPortletContext());

		portletConfigs.put(portlet.getPortletId(), portletConfig);

		return portletConfig;
	}

	private boolean _isSamePortletDeployedStatus(
		Portlet portlet, PortletConfig portletConfig) {

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)portletConfig;

		Portlet existingPortlet = liferayPortletConfig.getPortlet();

		if ((existingPortlet != null) &&
			(portlet.isUndeployedPortlet() ==
				existingPortlet.isUndeployedPortlet())) {

			return true;
		}

		return false;
	}

	private final Map<String, Map<String, PortletConfig>> _pool =
		new ConcurrentHashMap<>();

}