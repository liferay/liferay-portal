/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.upgrade.registry;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.web.internal.upgrade.v1_0_0.UpgradePortletPreferences;
import com.liferay.blogs.web.internal.upgrade.v1_0_0.UpgradePortletSettings;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.BaseStagingGroupTypeSettingsUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(service = UpgradeStepRegistrator.class)
public class BlogsWebUpgradeStepRegistrator implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register("0.0.1", "0.0.2", new UpgradePortletPreferences());

		registry.register("0.0.2", "1.0.0", new UpgradePortletSettings());

		registry.register(
			"1.0.0", "1.1.0",
			new com.liferay.blogs.web.internal.upgrade.v1_1_0.
				UpgradePortletPreferences());

		registry.register(
			"1.1.0", "1.2.0",
			new com.liferay.blogs.web.internal.upgrade.v1_2_0.
				UpgradePortletPreferences());

		registry.register(
			"1.2.0", "1.2.1",
			new BaseStagingGroupTypeSettingsUpgradeProcess(
				_companyLocalService, _groupLocalService,
				BlogsPortletKeys.BLOGS, BlogsPortletKeys.BLOGS_ADMIN));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}