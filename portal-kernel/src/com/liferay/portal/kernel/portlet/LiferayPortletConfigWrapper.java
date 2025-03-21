/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.filter.PortletConfigWrapper;

/**
 * @author Neil Griffin
 */
public class LiferayPortletConfigWrapper
	extends PortletConfigWrapper implements LiferayPortletConfig {

	public LiferayPortletConfigWrapper(
		LiferayPortletConfig liferayPortletConfig) {

		super(liferayPortletConfig);

		_liferayPortletConfig = liferayPortletConfig;
	}

	@Override
	public Portlet getPortlet() {
		return _liferayPortletConfig.getPortlet();
	}

	@Override
	public String getPortletId() {
		return _liferayPortletConfig.getPortletId();
	}

	@Override
	public boolean isCopyRequestParameters() {
		return _liferayPortletConfig.isCopyRequestParameters();
	}

	@Override
	public boolean isWARFile() {
		return _liferayPortletConfig.isWARFile();
	}

	private final LiferayPortletConfig _liferayPortletConfig;

}