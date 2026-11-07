/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.upgrade.registry;

import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.security.service.access.policy.service.SAPEntryService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = UpgradeStepRegistrator.class)
public class QuestionsWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new com.liferay.questions.web.internal.upgrade.v1_0_0.
				UpdateAllowedServicesSignaturesInSAPEntryUpgradeProcess(
					_sapEntryService));

		registry.register(
			"1.0.0", "1.0.1",
			new com.liferay.questions.web.internal.upgrade.v1_0_0.
				UpdateAllowedServicesSignaturesInSAPEntryUpgradeProcess(
					_sapEntryService));

		registry.register(
			"1.0.1", "1.0.2",
			new com.liferay.questions.web.internal.upgrade.v1_1_0.
				QuestionsConfigurationUpgradeProcess(
					_companyLocalService, _configurationAdmin,
					_configurationProvider, _mbCategoryLocalService));

		registry.register(
			"1.0.2", "1.1.0",
			new com.liferay.questions.web.internal.upgrade.v1_1_0.
				UpgradePortletPreferences(_mbCategoryLocalService));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference
	private SAPEntryService _sapEntryService;

}