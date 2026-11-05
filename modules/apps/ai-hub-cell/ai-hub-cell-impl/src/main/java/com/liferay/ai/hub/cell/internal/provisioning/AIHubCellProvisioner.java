/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.internal.provisioning;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Joao Victor
 */
public class AIHubCellProvisioner {

	public AIHubCellProvisioner(
		CompanyLocalService companyLocalService,
		ConfigurationProvider configurationProvider,
		OAuth2ApplicationLocalService oAuth2ApplicationLocalService,
		OAuth2ApplicationScopeAliasesLocalService
			oAuth2ApplicationScopeAliasesLocalService,
		SAPEntryLocalService sapEntryLocalService, ScopeLocator scopeLocator,
		UserLocalService userLocalService) {

		_companyLocalService = companyLocalService;
		_configurationProvider = configurationProvider;
		_oAuth2ApplicationLocalService = oAuth2ApplicationLocalService;
		_oAuth2ApplicationScopeAliasesLocalService =
			oAuth2ApplicationScopeAliasesLocalService;
		_sapEntryLocalService = sapEntryLocalService;
		_scopeLocator = scopeLocator;
		_userLocalService = userLocalService;
	}

	public void deprovision(long companyId) {
		Runnable runnable = _runnables.remove(companyId);

		if (runnable != null) {
			_scopeLocator.stopWaitingForScopeAliases(runnable);
		}

		try {
			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.
					fetchOAuth2ApplicationByExternalReferenceCode(
						_OAUTH2_APPLICATION_EXTERNAL_REFERENCE_CODE, companyId);

			if (oAuth2Application != null) {
				_oAuth2ApplicationLocalService.deleteOAuth2Application(
					oAuth2Application.getOAuth2ApplicationId());
			}

			SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
				companyId, _SAP_ENTRY_NAME);

			if (sapEntry != null) {
				_sapEntryLocalService.deleteSAPEntry(sapEntry);
			}
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to delete OAuth2 application and service access " +
					"policy entry for company " + companyId,
				portalException);
		}
	}

	public void provision(long companyId) {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-62272")) {
			return;
		}

		try {
			AIHubCellConfiguration aiHubCellConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AIHubCellConfiguration.class, companyId);

			if (Validator.isNull(aiHubCellConfiguration.serviceURL())) {
				return;
			}

			_provision(companyId, aiHubCellConfiguration.serviceURL());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to provision AI Hub Cell for company " + companyId,
				portalException);
		}
	}

	public void provision(long companyId, String serviceURL) {
		if (Validator.isNull(serviceURL) ||
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-62272")) {

			return;
		}

		_provision(companyId, serviceURL);
	}

	public void stopWaitingForScopeAliases() {
		for (Runnable runnable : _runnables.values()) {
			_scopeLocator.stopWaitingForScopeAliases(runnable);
		}

		_runnables.clear();
	}

	private void _addOAuth2Application(
			Company company, String homePageURL, List<String> scopeAliasesList)
		throws PortalException {

		User user = _userLocalService.fetchUserByScreenName(
			company.getCompanyId(),
			UserConstants.SCREEN_NAME_DEFAULT_SERVICE_ACCOUNT);

		if (user == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to add OAuth2 application for company ",
						company.getCompanyId(),
						" because the default service account does not exist"));
			}

			return;
		}

		_oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
			_OAUTH2_APPLICATION_EXTERNAL_REFERENCE_CODE, user.getUserId(),
			user.getScreenName(), Arrays.asList(GrantType.CLIENT_CREDENTIALS),
			"client_secret_post", user.getUserId(),
			OAuth2SecureRandomGenerator.generateClientId(),
			ClientProfile.HEADLESS_SERVER.id(),
			OAuth2SecureRandomGenerator.generateClientSecret(), null, null,
			homePageURL, 0, null, "AI Hub Cell", null, Arrays.asList(), false,
			scopeAliasesList, false, new ServiceContext());
	}

	private void _addOrUpdateOAuth2Application(
		long companyId, String serviceURL) {

		try {
			Company company = _companyLocalService.getCompany(companyId);

			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.
					fetchOAuth2ApplicationByExternalReferenceCode(
						_OAUTH2_APPLICATION_EXTERNAL_REFERENCE_CODE, companyId);

			boolean local = StringUtil.equalsIgnoreCase(
				company.getVirtualHostname(),
				HttpComponentsUtil.getDomain(serviceURL));

			if (oAuth2Application == null) {
				String homePageURL = company.getPortalURL(0);

				if (local) {
					homePageURL = serviceURL;
				}

				_addOAuth2Application(
					company, homePageURL,
					new ArrayList<>(
						_getScopeAliases(companyId, local, new HashSet<>())));

				return;
			}

			List<String> scopeAliasesList =
				_oAuth2ApplicationScopeAliasesLocalService.getScopeAliasesList(
					oAuth2Application.getOAuth2ApplicationScopeAliasesId());

			Set<String> scopeAliases = _getScopeAliases(
				companyId, local, SetUtil.fromList(scopeAliasesList));

			if (scopeAliases.size() > scopeAliasesList.size()) {
				oAuth2Application =
					_oAuth2ApplicationLocalService.updateScopeAliases(
						oAuth2Application.getUserId(),
						oAuth2Application.getUserName(),
						oAuth2Application.getOAuth2ApplicationId(),
						new ArrayList<>(scopeAliases));
			}

			if (local &&
				!Objects.equals(
					oAuth2Application.getHomePageURL(), serviceURL)) {

				oAuth2Application.setHomePageURL(serviceURL);

				_oAuth2ApplicationLocalService.updateOAuth2Application(
					oAuth2Application);
			}
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to add or update OAuth2 application for company " +
					companyId,
				portalException);
		}
	}

	private void _addOrUpdateSAPEntry(long companyId) {
		try {
			SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
				companyId, _SAP_ENTRY_NAME);

			if (sapEntry == null) {
				_sapEntryLocalService.addSAPEntry(
					_userLocalService.getGuestUserId(companyId),
					_SAP_ENTRY_ALLOWED_SERVICE_SIGNATURES, true, true,
					_SAP_ENTRY_NAME,
					Collections.singletonMap(
						LocaleUtil.getDefault(), _SAP_ENTRY_NAME),
					new ServiceContext());

				return;
			}

			if (Objects.equals(
					SetUtil.fromList(
						sapEntry.getAllowedServiceSignaturesList()),
					SetUtil.fromArray(
						StringUtil.split(
							_SAP_ENTRY_ALLOWED_SERVICE_SIGNATURES,
							StringPool.NEW_LINE)))) {

				return;
			}

			_sapEntryLocalService.updateSAPEntry(
				sapEntry.getSapEntryId(), _SAP_ENTRY_ALLOWED_SERVICE_SIGNATURES,
				sapEntry.isDefaultSAPEntry(), sapEntry.isEnabled(),
				sapEntry.getName(), sapEntry.getTitleMap(),
				new ServiceContext());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to add or update service access policy entry for " +
					"company " + companyId,
				portalException);
		}
	}

	private Set<String> _getScopeAliases(
		long companyId, boolean local, Set<String> scopeAliases) {

		Set<String> missingScopeAliases = SetUtil.fromList(
			Arrays.asList(
				"Liferay.Bulk.REST.everything.write",
				"Liferay.Headless.Admin.Fragment.everything",
				"Liferay.Headless.Admin.Taxonomy.everything.read",
				"Liferay.Headless.Admin.User.everything.read",
				"Liferay.Headless.Batch.Engine.everything",
				"Liferay.Headless.CMP.everything.read",
				"Liferay.Object.Admin.REST.everything.read",
				"Liferay.Portal.Search.REST.everything.read",
				"cmpprojectlink.everything"));

		if (local) {
			Collections.addAll(
				missingScopeAliases, "Liferay.AI.Hub.REST.everything",
				"Liferay.AI.Hub.REST.everything.read",
				"Liferay.AI.Hub.REST.everything.write");
		}

		missingScopeAliases.removeAll(scopeAliases);

		if (missingScopeAliases.isEmpty()) {
			return scopeAliases;
		}

		scopeAliases.addAll(
			_scopeLocator.waitForScopeAliases(
				companyId, missingScopeAliases,
				_runnables.computeIfAbsent(
					companyId, curCompanyId -> () -> provision(curCompanyId))));

		return scopeAliases;
	}

	private void _provision(long companyId, String serviceURL) {
		synchronized (AIHubCellProvisioner.class) {
			_addOrUpdateOAuth2Application(companyId, serviceURL);
			_addOrUpdateSAPEntry(companyId);
		}
	}

	private static final String _OAUTH2_APPLICATION_EXTERNAL_REFERENCE_CODE =
		"AI-HUB-CELL";

	private static final String _SAP_ENTRY_ALLOWED_SERVICE_SIGNATURES =
		"com.liferay.portal.search.rest.internal.resource.v1_0." +
			"SearchResultResourceImpl#getSearchPage";

	private static final String _SAP_ENTRY_NAME = "AI_HUB_CELL_TOKEN";

	private static final Log _log = LogFactoryUtil.getLog(
		AIHubCellProvisioner.class);

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationProvider _configurationProvider;
	private final OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;
	private final OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;
	private final Map<Long, Runnable> _runnables = new ConcurrentHashMap<>();
	private final SAPEntryLocalService _sapEntryLocalService;
	private final ScopeLocator _scopeLocator;
	private final UserLocalService _userLocalService;

}