/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.icon.locator.internal;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.configuration.icon.locator.PortletConfigurationIconLocator;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = PortletConfigurationIconLocator.class)
public class MVCPortletConfigurationIconLocator
	implements PortletConfigurationIconLocator {

	@Override
	public List<String> getDefaultViews(String portletId) {
		Portlet portlet = _portletLocalService.getPortletById(portletId);

		if (portlet == null) {
			return Collections.emptyList();
		}

		Map<String, String> initParams = portlet.getInitParams();

		String viewTemplate = initParams.get("view-template");

		if (Validator.isNotNull(viewTemplate)) {
			return Collections.singletonList(viewTemplate);
		}

		return Collections.emptyList();
	}

	@Override
	public String getPath(PortletRequest portletRequest) {
		return ParamUtil.getString(portletRequest, "mvcPath");
	}

	@Reference
	private PortletLocalService _portletLocalService;

}