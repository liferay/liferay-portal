/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.internal.feature.flag;

import com.liferay.ai.hub.cell.internal.provisioning.AIHubCellProvisioner;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joao Victor
 */
@Component(
	property = "feature.flag.key=LPD-62272", service = FeatureFlagListener.class
)
public class AIHubCellFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if ((companyId == CompanyConstants.SYSTEM) || !enabled) {
			return;
		}

		_aiHubCellProvisioner.provision(companyId);
	}

	@Activate
	protected void activate() {
		_aiHubCellProvisioner = new AIHubCellProvisioner(
			_companyLocalService, _configurationProvider,
			_oAuth2ApplicationLocalService,
			_oAuth2ApplicationScopeAliasesLocalService, _sapEntryLocalService,
			_scopeLocator, _userLocalService);
	}

	@Deactivate
	protected void deactivate() {
		_aiHubCellProvisioner.stopWaitingForScopeAliases();
	}

	private AIHubCellProvisioner _aiHubCellProvisioner;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	@Reference
	private ScopeLocator _scopeLocator;

	@Reference
	private UserLocalService _userLocalService;

}