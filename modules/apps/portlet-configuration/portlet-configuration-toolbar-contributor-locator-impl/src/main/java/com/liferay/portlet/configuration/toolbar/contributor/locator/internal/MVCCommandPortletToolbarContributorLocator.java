/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.toolbar.contributor.locator.internal;

import com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Sergio González
 */
@Component(service = PortletToolbarContributorLocator.class)
public class MVCCommandPortletToolbarContributorLocator
	extends BasePortletToolbarContributorLocator {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	protected String getParameterName() {
		return "mvcRenderCommandName";
	}

	@Override
	protected String getPropertyName() {
		return "mvc.render.command.name";
	}

}