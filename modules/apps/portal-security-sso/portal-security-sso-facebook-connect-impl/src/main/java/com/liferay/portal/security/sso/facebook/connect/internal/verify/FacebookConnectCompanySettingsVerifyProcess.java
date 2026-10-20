/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.facebook.connect.internal.verify;

import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.facebook.connect.constants.FacebookConnectConfigurationKeys;
import com.liferay.portal.security.sso.facebook.connect.constants.FacebookConnectConstants;
import com.liferay.portal.security.sso.facebook.connect.constants.LegacyFacebookConnectPropsKeys;
import com.liferay.portal.verify.BaseCompanySettingsVerifyProcess;
import com.liferay.portal.verify.VerifyProcess;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(service = VerifyProcess.class)
public class FacebookConnectCompanySettingsVerifyProcess
	extends BaseCompanySettingsVerifyProcess {

	@Override
	protected CompanyLocalService getCompanyLocalService() {
		return _companyLocalService;
	}

	@Override
	protected Set<String> getLegacyPropertyKeys() {
		return SetUtil.fromArray(
			LegacyFacebookConnectPropsKeys.FACEBOOK_CONNECT_KEYS);
	}

	@Override
	protected Map<String, String> getPropertyValues(long companyId) {
		Map<String, String> map = super.getPropertyValues(companyId);

		String oauthRedirectURL = _prefsProps.getString(
			companyId, LegacyFacebookConnectPropsKeys.OAUTH_REDIRECT_URL);

		if (oauthRedirectURL != null) {
			map.put(
				FacebookConnectConfigurationKeys.OAUTH_REDIRECT_URL,
				_upgradeLegacyRedirectURI(oauthRedirectURL));
		}

		return map;
	}

	@Override
	protected String[][] getRenamePropertyKeysArray() {
		return new String[][] {
			{
				LegacyFacebookConnectPropsKeys.AUTH_ENABLED,
				FacebookConnectConfigurationKeys.AUTH_ENABLED
			},
			{
				LegacyFacebookConnectPropsKeys.APP_ID,
				FacebookConnectConfigurationKeys.APP_ID
			},
			{
				LegacyFacebookConnectPropsKeys.APP_SECRET,
				FacebookConnectConfigurationKeys.APP_SECRET
			},
			{
				LegacyFacebookConnectPropsKeys.GRAPH_URL,
				FacebookConnectConfigurationKeys.GRAPH_URL
			},
			{
				LegacyFacebookConnectPropsKeys.OAUTH_AUTH_URL,
				FacebookConnectConfigurationKeys.OAUTH_AUTH_URL
			},
			{
				LegacyFacebookConnectPropsKeys.OAUTH_TOKEN_URL,
				FacebookConnectConfigurationKeys.OAUTH_TOKEN_URL
			},
			{
				LegacyFacebookConnectPropsKeys.VERIFIED_ACCOUNT_REQUIRED,
				FacebookConnectConfigurationKeys.VERIFIED_ACCOUNT_REQUIRED
			}
		};
	}

	@Override
	protected String getSettingsId() {
		return FacebookConnectConstants.SERVICE_NAME;
	}

	private String _upgradeLegacyRedirectURI(String legacyRedirectURI) {
		if (Validator.isNull(legacyRedirectURI)) {
			return legacyRedirectURI;
		}

		return legacyRedirectURI.replaceFirst(
			"/c/login/facebook_connect_oauth",
			"/c/portal/facebook_connect_oauth");
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private PrefsProps _prefsProps;

}