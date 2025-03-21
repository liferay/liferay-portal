/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.toolbar.contributor.locator.internal;

import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides an implementation of {@link PortletToolbarContributorLocator} for
 * portlets using Struts as MVC pattern, allowing them to have a different
 * {@link
 * com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor}
 * for different struts actions.
 *
 * <p>
 * PortletToolbarContributor implementations must be registered in the OSGI
 * Registry using the following properties:
 * </p>
 *
 * <ul>
 * <li>
 * &quot;jakarta.portlet.name&quot; the ID of the portlet whose portlet toolbar to
 * extend.
 * </li>
 * <li>
 * &quot;struts.action&quot; this property is optional. If this property is not
 * present, the portlet toolbar is always extended. If it contains a value
 * (e.g., <code>/blogs/view_entry</code>) the portlet toolbar is extended only
 * for that specific struts action. If the value is &quot;-&quot; the portlet
 * toolbar is extended when there is no <code>strutsAction</code> specified in
 * the request (typically when rendering the first view of the portlet).
 * </li>
 * </ul>
 *
 * <p>
 * A single PortletToolbarContributor implementation can be used for different
 * portlets and struts actions by including multiple times with the same
 * properties.
 * </p>
 *
 * @author Sergio González
 */
@Component(service = PortletToolbarContributorLocator.class)
public class StrutsPortletToolbarContributorLocator
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
		return "struts_action";
	}

	@Override
	protected String getPropertyName() {
		return "struts.action";
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}