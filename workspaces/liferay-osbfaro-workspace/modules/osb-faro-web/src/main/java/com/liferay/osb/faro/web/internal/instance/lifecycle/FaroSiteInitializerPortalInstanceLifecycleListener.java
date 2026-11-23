/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.instance.lifecycle;

import com.liferay.osb.faro.web.internal.util.SiteInitializerUtil;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.site.initializer.SiteInitializer;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class FaroSiteInitializerPortalInstanceLifecycleListener
	extends InitialRequestPortalInstanceLifecycleListener {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) throws Exception {
		Group group = _groupLocalService.getGroup(
			companyId, GroupConstants.GUEST);

		if (SiteInitializerUtil.isInitialized(
				group, _siteInitializer, _SITE_INITIALIZER_VERSION)) {

			return;
		}

		SiteInitializerUtil.initialize(
			group, _siteInitializer, _SITE_INITIALIZER_VERSION);
	}

	private static final String _SITE_INITIALIZER_VERSION = "0.1";

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(site.initializer.key=com.liferay.osb.faro.site.initializer)"
	)
	private SiteInitializer _siteInitializer;

}