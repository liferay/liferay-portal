/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.internal.configuration.persistence.listener;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 * @author Rafael Praxedes
 */
@Component(
	property = "model.class.name=com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration",
	service = ConfigurationModelListener.class
)
public class AIHubCellConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties) {
		long companyId = GetterUtil.getLong(properties.get("companyId"));

		Company company = _companyLocalService.fetchCompany(companyId);

		if ((company == null) ||
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-62272")) {

			return;
		}

		_addOAuth2Application(company);
		_addSAPEntry(company);
	}

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		String serviceURL = GetterUtil.getString(properties.get("serviceURL"));

		if (serviceURL.endsWith(StringPool.SLASH)) {
			throw new ConfigurationModelListenerException(
				_getMessage(
					"please-enter-a-service-url-that-does-not-end-with-a-" +
						"slash"),
				AIHubCellConfiguration.class, getClass(), properties);
		}
	}

	private void _addOAuth2Application(Company company) {
		try {
			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.
					fetchOAuth2ApplicationByExternalReferenceCode(
						"AI-HUB-CELL", company.getCompanyId());

			if (oAuth2Application != null) {
				return;
			}

			User user = _userLocalService.fetchUserByScreenName(
				company.getCompanyId(), "default-service-account");

			if (user == null) {
				return;
			}

			_oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
				"AI-HUB-CELL", user.getUserId(), user.getScreenName(),
				Arrays.asList(GrantType.CLIENT_CREDENTIALS),
				"client_secret_post", user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.HEADLESS_SERVER.id(),
				OAuth2SecureRandomGenerator.generateClientSecret(), null, null,
				company.getPortalURL(0), 0, null, "AI Hub Cell", null,
				Arrays.asList(), false,
				Arrays.asList(
					"Liferay.Headless.Batch.Engine.everything",
					"Liferay.Headless.CMP.everything.read",
					"Liferay.Object.Admin.REST.everything.read",
					"Liferay.Portal.Search.REST.everything.read",
					"cmpprojectlink.everything"),
				false, new ServiceContext());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to add OAuth2 application for company " +
					company.getCompanyId(),
				portalException);
		}
	}

	private void _addSAPEntry(Company company) {
		try {
			SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
				company.getCompanyId(), _SAP_ENTRY_NAME);

			if (sapEntry != null) {
				return;
			}

			_sapEntryLocalService.addSAPEntry(
				_userLocalService.getGuestUserId(company.getCompanyId()),
				StringBundler.concat(
					"com.liferay.ai.hub.cell.rest.internal.resource.v1_0.",
					"AuthorizationTokenResourceImpl#postAuthorizationToken\n",
					"com.liferay.portal.search.rest.internal.resource.v1_0.",
					"SearchResultResourceImpl#getSearchPage"),
				true, true, _SAP_ENTRY_NAME,
				Collections.singletonMap(
					LocaleUtil.getDefault(), _SAP_ENTRY_NAME),
				new ServiceContext());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to add service access policy entry for company " +
					company.getCompanyId(),
				portalException);
		}
	}

	private String _getMessage(String key) {
		try {
			return ResourceBundleUtil.getString(_getResourceBundle(), key);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private ResourceBundle _getResourceBundle() {
		return ResourceBundleUtil.getBundle(
			"content.Language", LocaleThreadLocal.getThemeDisplayLocale(),
			getClass());
	}

	private static final String _SAP_ENTRY_NAME = "AI_HUB_CELL_TOKEN";

	private static final Log _log = LogFactoryUtil.getLog(
		AIHubCellConfigurationModelListener.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}