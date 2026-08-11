/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.upgrade.registry;

import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.shortcut.internal.upgrade.v1_0_0.OAuth2ApplicationAnalyticsCloudUpgradeProcess;
import com.liferay.oauth2.provider.shortcut.internal.upgrade.v2_0_0.OAuth2ApplicationLDPDomainUpgradeProcess;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Nilton Vieira
 */
@Component(service = UpgradeStepRegistrator.class)
public class OAuth2ProviderShortcutUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new OAuth2ApplicationAnalyticsCloudUpgradeProcess(
				_companyLocalService, _oAuth2ApplicationLocalService,
				_resourcePermissionLocalService, _roleLocalService,
				_userLocalService));

		registry.register(
			"1.0.0", "2.0.0", new OAuth2ApplicationLDPDomainUpgradeProcess());
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.oauth2.provider.service)(release.schema.version>=4.2.8))"
	)
	private Release _release;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}