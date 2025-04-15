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
 * @author Sergio González
 */
@Component(service = PortletConfigurationIconLocator.class)
public class StrutsPortletConfigurationIconLocator
	implements PortletConfigurationIconLocator {

	@Override
	public List<String> getDefaultViews(String portletId) {
		Portlet portlet = _portletLocalService.getPortletById(portletId);

		if (portlet == null) {
			return Collections.emptyList();
		}

		Map<String, String> initParams = portlet.getInitParams();

		String viewAction = initParams.get("view-action");

		if (Validator.isNotNull(viewAction)) {
			return Collections.singletonList(viewAction);
		}

		return Collections.emptyList();
	}

	@Override
	public String getPath(PortletRequest portletRequest) {
		return ParamUtil.getString(portletRequest, "struts_action");
	}

	@Reference
	private PortletLocalService _portletLocalService;

}